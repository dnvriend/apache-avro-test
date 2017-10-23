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

import com.github.dnvriend.TestSpec
import com.sksamuel.avro4s.{ AvroAlias, AvroNamespace, SchemaFor }
import org.apache.avro.Schema

object v1 {
  @AvroNamespace("nl.zorgdomein.test")
  case class Person(name: String = "", age: Int = 0)
  val zero = Person("John Doe", 42)
  val schema: Schema = SchemaFor[Person]()
  val hex = "104A6F686E20446F6554"
  val jsonHex = "7B226E616D65223A224A6F686E20446F65222C22616765223A34327D"
}

object v2 {
  @AvroNamespace("nl.zorgdomein.test")
  case class Person(@AvroAlias("name") naam: String = "", @AvroAlias("age") leeftijd: Int = 0)
  val zero = Person("John Doe", 42)
  val hex = "104A6F686E20446F6554"
  val jsonHex = "7B226E61616D223A224A6F686E20446F65222C226C65656674696A64223A34327D"
}

class AvroOpsTest extends TestSpec {
  it should "encode instance to avro binary" in {
    v1.zero.toAvroBinary.hex shouldBe v1.hex
    v2.zero.toAvroBinary.hex shouldBe v2.hex
  }

  it should "encode instance to avro json" in {
    v1.zero.toAvroJson.hex shouldBe v1.jsonHex
    v2.zero.toAvroJson.hex shouldBe v2.jsonHex
  }

  it should "decode an avro binary" in {
    v1.hex.fromHex.parseAvroBinary[v1.Person, v1.Person].value shouldBe v1.zero
    v2.hex.fromHex.parseAvroBinary[v2.Person, v2.Person].value shouldBe v2.zero
  }

  it should "decode an avro binary when you only have the writer's schema" in {
    v1.hex.fromHex.parseAvroBinary[v1.Person](v1.schema).value shouldBe v1.zero
    v1.hex.fromHex.parseAvroBinary[v2.Person](v1.schema).value shouldBe v2.zero
  }

  it should "decode an avro json" in {
    v1.jsonHex.fromHex.parseAvroJson[v1.Person, v1.Person].value shouldBe v1.zero
    v2.jsonHex.fromHex.parseAvroJson[v2.Person, v2.Person].value shouldBe v2.zero
  }

  it should "check for full compatibility" in {
    checkFullCompatibility[v1.Person](schemaFor[v2.Person]) shouldBe right[Schema]
  }

  it should "check if a record can be read with" in {
    // can a v2.Person record be read with a v1.Person schema
    checkCanReadWith[v1.Person, v2.Person] shouldBe right[Schema]
  }

  it should "convert a v1.Person to a v2.Person" in {
    v1.zero.to[v2.Person].value shouldBe v2.zero
  }
}
