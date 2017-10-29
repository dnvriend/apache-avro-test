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

package com.github.dnvriend.optionalfields

import com.github.dnvriend.TestSpec
import com.github.dnvriend.ops.AvroOps._
import com.sksamuel.avro4s.AvroNamespace
import org.apache.avro.Schema
import scalaz._
import scalaz.Scalaz._

object v1 {
  @AvroNamespace("com.github.dnvriend")
  case class PersonCreated(name: Option[String] = None)
  val avroSchema: String = """{"type":"record","name":"PersonCreated","namespace":"com.github.dnvriend","fields":[{"name":"name","type":["null","string"],"default":null}]}"""
  val schema: Schema = avroSchema.s
}

object v2 {
  @AvroNamespace("com.github.dnvriend")
  case class PersonCreated(name: Option[String] = None, age: Option[Int] = None)
  val avroSchema: String = """{"type":"record","name":"PersonCreated","namespace":"com.github.dnvriend","fields":[{"name":"name","type":["null","string"],"default":null},{"name":"age","type":["null","int"],"default":null}]}"""
  val schema: Schema = avroSchema.s
}

object v3 {
  @AvroNamespace("com.github.dnvriend")
  case class PersonCreated(name: Option[String] = None, age: Option[Int] = None, gender: Gender = Gender.MALE)
  val avroSchema: String = """{"type":"record","name":"PersonCreated","namespace":"com.github.dnvriend","fields":[{"name":"name","type":["null","string"],"default":null},{"name":"age","type":["null","int"],"default":null},{"name":"gender","type":{"type":"enum","name":"Gender","namespace":"com.github.dnvriend.optionalfields","symbols":["MALE","FEMALE"]},"default":"MALE"}]}"""
  val schema: Schema = avroSchema.s
}

object v4 {
  @AvroNamespace("com.github.dnvriend")
  case class PersonCreated(name: Option[String] = None, address: Option[String] = None)
  val avroSchema: String = """{"type":"record","name":"PersonCreated","namespace":"com.github.dnvriend","fields":[{"name":"name","type":["null","string"],"default":null},{"name":"address","type":["null","string"],"default":null}]}"""
  val schema: Schema = avroSchema.s
}

object v5 {
  @AvroNamespace("com.github.dnvriend")
  case class PersonCreated(address: Option[String] = None, zipcode: Option[String] = None)
  val avroSchema: String = """{"type":"record","name":"PersonCreated","namespace":"com.github.dnvriend","fields":[{"name":"address","type":["null","string"],"default":null},{"name":"zipcode","type":["null","string"],"default":null}]}"""
  val schema: Schema = avroSchema.s
}

object v6 {
  @AvroNamespace("com.github.dnvriend")
  case class PersonCreated(name: Option[String] = None)
  val avroSchema: String = """{"type":"record","name":"PersonCreated","namespace":"com.github.dnvriend","fields":[{"name":"name","type":["null","string"],"default":null}]}"""
  val schema: Schema = avroSchema.s
}

class OptionalFieldsTest extends TestSpec {
  val EmptyHex: String = "00"
  val v1Hex: String = "020E76312E6E616D65"
  val v2Hex: String = "020E76322E6E616D650254"
  val v3Hex: String = "020E76332E6E616D65025402"
  val v4Hex: String = "020E76342E6E616D65021476342E61646472657373"
  val v5Hex: String = "021476352E61646472657373021476352E7A6970636F6465"
  val v6Hex: String = "020E76362E6E616D65"

  info(
    """
      |The correct way to work with Apache AVRO is to:
      | 1. always set a default value
      | 2. if you don't know a good default value,
      |   make the field optional and use 'None' as the default value
      | 3. always set a default value
      | 4. see 3.
    """.stripMargin)

  ignore should "generate schemas" in {
    schemaFor[v1.PersonCreated].log
    schemaFor[v2.PersonCreated].log
    schemaFor[v3.PersonCreated].log
    schemaFor[v4.PersonCreated].log
    schemaFor[v5.PersonCreated].log
  }

  ignore should "generate data" in {
    v1.PersonCreated("v1.name".some).toAvroBinary.hex.log
    v2.PersonCreated("v2.name".some, 42.some).toAvroBinary.hex.log
    v3.PersonCreated("v3.name".some, 42.some, Gender.FEMALE).toAvroBinary.hex.log
    v4.PersonCreated("v4.name".some, "v4.address".some).toAvroBinary.hex.log
    v5.PersonCreated("v5.address".some, "v5.zipcode".some).toAvroBinary.hex.log
    v6.PersonCreated("v6.name".some).toAvroBinary.hex.log
  }

  "empty data" should "read to specified versions in [R] type parameter" in {
    EmptyHex.parseAvroBinary[v1.PersonCreated](v1.schema).value shouldBe v1.PersonCreated(None)
    EmptyHex.parseAvroBinary[v2.PersonCreated](v1.schema).value shouldBe v2.PersonCreated(None, None)
    EmptyHex.parseAvroBinary[v3.PersonCreated](v1.schema).value shouldBe v3.PersonCreated(None, None, Gender.MALE)
    EmptyHex.parseAvroBinary[v4.PersonCreated](v1.schema).value shouldBe v4.PersonCreated(None, None)
    EmptyHex.parseAvroBinary[v5.PersonCreated](v1.schema).value shouldBe v5.PersonCreated(None, None)
    EmptyHex.parseAvroBinary[v6.PersonCreated](v1.schema).value shouldBe v6.PersonCreated(None)
  }

  "v1Hex" should "read to specified version" in {
    v1Hex.parseAvroBinary[v1.PersonCreated](v1.schema).value shouldBe v1.PersonCreated("v1.name".some)
    v1Hex.parseAvroBinary[v2.PersonCreated](v1.schema).value shouldBe v2.PersonCreated("v1.name".some, None)
    v1Hex.parseAvroBinary[v3.PersonCreated](v1.schema).value shouldBe v3.PersonCreated("v1.name".some, None, Gender.MALE)
    v1Hex.parseAvroBinary[v4.PersonCreated](v1.schema).value shouldBe v4.PersonCreated("v1.name".some, None)
    v1Hex.parseAvroBinary[v5.PersonCreated](v1.schema).value shouldBe v5.PersonCreated(None, None)
    v1Hex.parseAvroBinary[v6.PersonCreated](v1.schema).value shouldBe v6.PersonCreated("v1.name".some)
  }

  "v2Hex" should "read to specified version" in {
    v2Hex.parseAvroBinary[v1.PersonCreated](v2.schema).value shouldBe v1.PersonCreated("v2.name".some)
    v2Hex.parseAvroBinary[v2.PersonCreated](v2.schema).value shouldBe v2.PersonCreated("v2.name".some, 42.some)
    v2Hex.parseAvroBinary[v3.PersonCreated](v2.schema).value shouldBe v3.PersonCreated("v2.name".some, 42.some, Gender.MALE)
    v2Hex.parseAvroBinary[v4.PersonCreated](v2.schema).value shouldBe v4.PersonCreated("v2.name".some, None)
    v2Hex.parseAvroBinary[v5.PersonCreated](v2.schema).value shouldBe v5.PersonCreated(None, None)
    v2Hex.parseAvroBinary[v6.PersonCreated](v2.schema).value shouldBe v6.PersonCreated("v2.name".some)
  }

  "v3Hex" should "read to specified version" in {
    v3Hex.parseAvroBinary[v1.PersonCreated](v3.schema).value shouldBe v1.PersonCreated("v3.name".some)
    v3Hex.parseAvroBinary[v2.PersonCreated](v3.schema).value shouldBe v2.PersonCreated("v3.name".some, 42.some)
    v3Hex.parseAvroBinary[v3.PersonCreated](v3.schema).value shouldBe v3.PersonCreated("v3.name".some, 42.some, Gender.FEMALE)
    v3Hex.parseAvroBinary[v4.PersonCreated](v3.schema).value shouldBe v4.PersonCreated("v3.name".some, None)
    v3Hex.parseAvroBinary[v5.PersonCreated](v3.schema).value shouldBe v5.PersonCreated(None, None)
    v3Hex.parseAvroBinary[v6.PersonCreated](v3.schema).value shouldBe v6.PersonCreated("v3.name".some)
  }

  "v4Hex" should "read to specified version" in {
    v4Hex.parseAvroBinary[v1.PersonCreated](v4.schema).value shouldBe v1.PersonCreated("v4.name".some)
    v4Hex.parseAvroBinary[v2.PersonCreated](v4.schema).value shouldBe v2.PersonCreated("v4.name".some, None)
    v4Hex.parseAvroBinary[v3.PersonCreated](v4.schema).value shouldBe v3.PersonCreated("v4.name".some, None, Gender.MALE)
    v4Hex.parseAvroBinary[v4.PersonCreated](v4.schema).value shouldBe v4.PersonCreated("v4.name".some, "v4.address".some)
    v4Hex.parseAvroBinary[v5.PersonCreated](v4.schema).value shouldBe v5.PersonCreated("v4.address".some, None)
    v4Hex.parseAvroBinary[v6.PersonCreated](v4.schema).value shouldBe v6.PersonCreated("v4.name".some)
  }

  "v5Hex" should "read to specified version" in {
    v5Hex.parseAvroBinary[v1.PersonCreated](v5.schema).value shouldBe v1.PersonCreated(None)
    v5Hex.parseAvroBinary[v2.PersonCreated](v5.schema).value shouldBe v2.PersonCreated(None, None)
    v5Hex.parseAvroBinary[v3.PersonCreated](v5.schema).value shouldBe v3.PersonCreated(None, None, Gender.MALE)
    v5Hex.parseAvroBinary[v4.PersonCreated](v5.schema).value shouldBe v4.PersonCreated(None, "v5.address".some)
    v5Hex.parseAvroBinary[v5.PersonCreated](v5.schema).value shouldBe v5.PersonCreated("v5.address".some, "v5.zipcode".some)
    v5Hex.parseAvroBinary[v6.PersonCreated](v5.schema).value shouldBe v6.PersonCreated(None)
  }

  "v6Hex" should "read to specified version" in {
    v6Hex.parseAvroBinary[v1.PersonCreated](v6.schema).value shouldBe v1.PersonCreated("v6.name".some)
    v6Hex.parseAvroBinary[v2.PersonCreated](v6.schema).value shouldBe v2.PersonCreated("v6.name".some, None)
    v6Hex.parseAvroBinary[v3.PersonCreated](v6.schema).value shouldBe v3.PersonCreated("v6.name".some, None, Gender.MALE)
    v6Hex.parseAvroBinary[v4.PersonCreated](v6.schema).value shouldBe v4.PersonCreated("v6.name".some, None)
    v6Hex.parseAvroBinary[v5.PersonCreated](v6.schema).value shouldBe v5.PersonCreated(None, None)
    v6Hex.parseAvroBinary[v6.PersonCreated](v6.schema).value shouldBe v6.PersonCreated("v6.name".some)
  }
}
