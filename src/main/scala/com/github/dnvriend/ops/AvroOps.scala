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

object AvroOps extends AvroOps

trait AvroOps {
  implicit def toAvroSerializeOpsImpl[A <: Product: SchemaFor: ToRecord](a: A) = new AvroSerializeOpsImpl(a)
  implicit def toAvroDeserializeOpsImpl[A <: Product](bytes: Array[Byte]) = new AvroDeSerializeOpsImpl(bytes)

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

class AvroCompatibilityOpsImpl[A <: Product, B <: Product](implicit schemaForA: SchemaFor[A], schemaForB: SchemaFor[B]) {

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

  def to[B <: Product: SchemaFor: FromRecord]: Option[B] = {
    new AvroDeSerializeOpsImpl(toAvroBinary).parseAvroBinary[B, A]
  }
}

class AvroDeSerializeOpsImpl(bytes: Array[Byte]) {
  def parseAvroBinary[R <: Product: SchemaFor: FromRecord, W <: Product](implicit writerSchemaFor: SchemaFor[W]): Option[R] = {
    AvroInputStream.binary[R](bytes, writerSchemaFor()).iterator().toList.headOption
  }

  def parseAvroBinary[R <: Product: FromRecord](writerSchema: Schema)(implicit readerSchema: SchemaFor[R]): Option[R] = {
    new AvroBinaryInputStream[R](new SeekableByteArrayInput(bytes), Option(writerSchema), Option(readerSchema())).iterator().toList.headOption
  }

  def parseAvroJson[R <: Product: FromRecord, W <: Product](implicit readerSchemaFor: SchemaFor[R], writerSchemaFor: SchemaFor[W]): Option[R] = {
    AvroJsonInputStream[R](new SeekableByteArrayInput(bytes), Option(writerSchemaFor())).iterator().toList.headOption
  }

  def parseAvroJson[R <: Product: FromRecord](writerSchema: Schema)(implicit readerSchema: SchemaFor[R]): Option[R] = {
    AvroJsonInputStream[R](new SeekableByteArrayInput(bytes), Option(writerSchema), Option(readerSchema())).iterator().toList.headOption
  }
}
