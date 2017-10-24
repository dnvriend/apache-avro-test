// Copyright 2017 Dennis Vriend
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.github.dnvriend.ops

import java.io.{ ByteArrayOutputStream, OutputStream }

import com.sksamuel.avro4s._
import org.apache.avro.SchemaCompatibility.SchemaCompatibilityType
import org.apache.avro.file.SeekableByteArrayInput
import org.apache.avro.{ Schema, SchemaCompatibility, SchemaNormalization, SchemaValidatorBuilder }

import scalaz._
import scalaz.Scalaz._

object AvroOps extends AvroOps

trait AvroOps {
  implicit def toAvroSerializeOpsImpl[A <: Product: SchemaFor: ToRecord](a: A): AvroSerializeOpsImpl[A] = new AvroSerializeOpsImpl(a)
  implicit def toAvroDeserializeOpsImpl[A <: Product](bytes: Array[Byte]): AvroDeSerializeOpsImpl = new AvroDeSerializeOpsImpl(bytes)
  implicit def toAvroSchemaOps(that: Schema): AvroSchemaOpsImpl = new AvroSchemaOpsImpl(that)
  implicit def toAvroStringOps(that: String): AvroStringOpsImpl = new AvroStringOpsImpl(that)

  def fingerPrintFor[A <: Product](implicit schemaFor: SchemaFor[A]): Array[Byte] = {
    SchemaNormalization.parsingFingerprint("SHA-256", schemaFor())
  }

  def schemaFor[A <: Product](implicit schemaFor: SchemaFor[A]): Schema = schemaFor()

  def checkCanReadWith[R <: Product, W <: Product](implicit readerSchema: SchemaFor[R], writerSchema: SchemaFor[W]): Disjunction[Throwable, Schema] = {
    val result: SchemaCompatibility.SchemaPairCompatibility = SchemaCompatibility.checkReaderWriterCompatibility(readerSchema(), writerSchema())
    result.getType match {
      case SchemaCompatibilityType.COMPATIBLE   => DRight(readerSchema())
      case SchemaCompatibilityType.INCOMPATIBLE => DLeft(new Error(result.getDescription))
      case _                                    => DLeft(new Error("Unknown status: " + result.getType))
    }
  }

  def checkFullCompatibility[R <: Product](existingSchemas: Schema*)(implicit readerSchema: SchemaFor[R]): Disjunction[Throwable, Schema] = Disjunction.fromTryCatchNonFatal {
    import scala.collection.JavaConverters._
    new SchemaValidatorBuilder()
      .mutualReadStrategy()
      .validateAll()
      .validate(readerSchema(), List(existingSchemas: _*).asJava)

    readerSchema()
  }
}

class AvroSerializeOpsImpl[A <: Product: SchemaFor: ToRecord](data: A) {
  private def withOutputStream(f: OutputStream => Unit): Array[Byte] = {
    val baos = new ByteArrayOutputStream
    f(baos)
    baos.toByteArray
  }

  def toAvroBinary: Array[Byte] = withOutputStream { os =>
    val output = AvroOutputStream.binary[A](os)
    output.write(data)
    output.close()
  }

  def toAvroJson: Array[Byte] = withOutputStream { os =>
    val output = AvroOutputStream.json[A](os)
    output.write(data)
    output.close()
  }

  def to[B <: Product: SchemaFor: FromRecord]: Disjunction[Throwable, B] = {
    new AvroDeSerializeOpsImpl(toAvroBinary).parseAvroBinary[B, A]
  }
}

class AvroDeSerializeOpsImpl(bytes: Array[Byte]) {
  def parseAvroBinary[R <: Product: SchemaFor: FromRecord, W <: Product](implicit writerSchemaFor: SchemaFor[W]): Disjunction[Throwable, R] = {
    parseAvroBinary[R](writerSchemaFor())
  }

  def parseAvroBinary[R <: Product: FromRecord](writerSchema: Schema)(implicit readerSchemaFor: SchemaFor[R]): Disjunction[Throwable, R] = {
    new AvroBinaryInputStream[R](new SeekableByteArrayInput(bytes), Option(writerSchema), Option(readerSchemaFor())).tryIterator().next().toDisjunction
  }

  def parseAvroJson[R <: Product: SchemaFor: FromRecord, W <: Product](implicit writerSchemaFor: SchemaFor[W]): Disjunction[Throwable, R] = {
    parseAvroJson[R](writerSchemaFor())
  }

  def parseAvroJson[R <: Product: FromRecord](writerSchema: Schema)(implicit readerSchemaFor: SchemaFor[R]): Disjunction[Throwable, R] = {
    AvroJsonInputStream[R](new SeekableByteArrayInput(bytes), Option(writerSchema), Option(readerSchemaFor())).tryIterator().next().toDisjunction
  }
}

class AvroStringOpsImpl(that: String) {
  def avroSchema: Schema = {
    new Schema.Parser().parse(that)
  }
}

class AvroSchemaOpsImpl(that: Schema) {
  def fingerprint: Array[Byte] = {
    SchemaNormalization.parsingFingerprint("SHA-256", that)
  }
  def toUtf8Array: Array[Byte] = {
    that.toString.getBytes("UTF-8")
  }
}