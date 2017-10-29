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

package com.github.dnvriend.compatibility

import com.github.dnvriend.TestSpec
import com.github.dnvriend.ops.AllOps
import org.apache.avro.Schema
import org.scalatest.Matchers
import org.typelevel.scalatest.DisjunctionMatchers

object CompatibilityTest extends AllOps {
  val primitiveTypes = List("int", "long", "float", "double")

  val stringType = """{"type": "string"}""" // // <length> + UTF-8 encoded char data
  val bytesType = """{"type": "bytes"}""" // <length> + bytes of data
  val doubleType = """{"type": "double"}""" // 8 bytes
  val floatType = """{"type": "float"}""" // 4 bytes
  val longType = """{"type": "long"}""" // 4 bytes zig/zag
  val intType = """{"type": "int"}""" // 4 bytes zig/zag
  val booleanType = """{"type": "boolean"}""" // encoded as 1 byte
  val nullType = """{"type": "null"}""" // encoded as 0x00

  // records are written by taking the fields in order and concatting the encodings
  // thats why you need the writers schema

  val enumType: String = """
                     |{ "type": "enum",
                     |  "name": "Suit",
                     |  "symbols" : ["SPADES", "HEARTS", "DIAMONDS", "CLUBS"]
                     |}
                   """.stripMargin

  val arrayStringType = """{"type": "array", "items": "string"}"""
  val arrayBytesType = """{"type": "array", "items": "bytes"}"""
  val arrayIntType = """{"type": "array", "items": "int"}"""
  val arrayLongType = """{"type": "array", "items": "long"}"""
  val arrayFloatType = """{"type": "array", "items": "float"}"""
  val arrayDoubleType = """{"type": "array", "items": "double"}"""

  val mapStringType = """{"type": "map", "values": "string"}"""
  val mapBytesType = """{"type": "map", "values": "bytes"}"""
  val mapIntType = """{"type": "map", "values": "int"}"""
  val mapLongType = """{"type": "map", "values": "long"}"""

  val fixed15Type = """{"type": "fixed", "size": 15, "name": "md5"}"""
  val fixed16Type = """{"type": "fixed", "size": 16, "name": "md5"}"""
  val fixed17Type = """{"type": "fixed", "size": 17, "name": "md5"}"""

  val stringSchema: Schema = stringType.s
  val bytesSchema: Schema = bytesType.s
  val booleanSchema: Schema = booleanType.s
  val intSchema: Schema = intType.s
  val longSchema: Schema = longType.s
  val floatSchema: Schema = floatType.s
  val doubleSchema: Schema = doubleType.s

  val arrayStringSchema: Schema = arrayStringType.s
  val arrayBytesSchema: Schema = arrayBytesType.s
  val arrayIntSchema: Schema = arrayIntType.s
  val arrayLongSchema: Schema = arrayLongType.s
  val arrayFloatSchema: Schema = arrayFloatType.s
  val arrayDoubleSchema: Schema = arrayDoubleType.s

  val mapStringSchema: Schema = mapStringType.s
  val mapBytesSchema: Schema = mapBytesType.s
  val mapIntSchema: Schema = mapIntType.s
  val mapLongSchema: Schema = mapLongType.s

  val fixed15Schema: Schema = fixed15Type.s
  val fixed16Schema: Schema = fixed16Type.s
  val fixed17Schema: Schema = fixed17Type.s

  implicit class ToIsCompatibleOps(schema: Schema) extends Matchers with DisjunctionMatchers {
    def canBeReadBy(existing: List[Schema]): Unit = {
      existing.validateCanBeReadByExistingSchemas(schema).log shouldBe right[Schema]
    }
  }
  implicit def ToListOfSchemas(schema: Schema): List[Schema] = List(schema)
}

class CompatibilityTest extends TestSpec {

  import CompatibilityTest._
  "type promotions" should "" in {
    bytesSchema canBeReadBy stringSchema
    stringSchema canBeReadBy bytesSchema

    intSchema canBeReadBy longSchema
    longSchema canBeReadBy floatSchema
    floatSchema canBeReadBy doubleSchema

    intSchema canBeReadBy List(longSchema, floatSchema, doubleSchema)
    longSchema canBeReadBy List(floatSchema, doubleSchema)

    arrayStringSchema canBeReadBy arrayBytesSchema
    arrayBytesSchema canBeReadBy arrayStringSchema
    arrayIntSchema canBeReadBy arrayLongSchema
    arrayLongSchema canBeReadBy arrayFloatSchema
    arrayFloatSchema canBeReadBy arrayDoubleSchema

    mapStringSchema canBeReadBy mapBytesSchema
    mapBytesSchema canBeReadBy mapStringSchema
    mapIntSchema canBeReadBy mapLongSchema

    fixed15Schema canBeReadBy fixed15Schema
    fixed16Schema canBeReadBy fixed16Schema
    fixed17Schema canBeReadBy fixed17Schema
  }
}
