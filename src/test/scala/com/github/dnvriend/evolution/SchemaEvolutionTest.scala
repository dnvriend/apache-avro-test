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

package com.github.dnvriend.evolution

import com.github.dnvriend.TestSpec
import com.github.dnvriend.ops.All._
import com.github.dnvriend.ops.AvroOps
import com.sksamuel.avro4s.{ AvroNamespace, SchemaFor }
import org.apache.avro.Schema

import scalaz.Scalaz._

object v1 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: String = "")

  @AvroNamespace("com.github.dnvriend.cars")
  case class Car(brand: String = "", color: String = "", hp: Int = 100)

  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "name",
      |    "type" : "string",
      |    "default" : ""
      |  } ]
      |}
    """.stripMargin
  val schema: Schema = new Schema.Parser().parse(avroSchema)
}

object v2 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: String = "", age: Int = 0)
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "name",
      |    "type" : "string",
      |    "default" : ""
      |  }, {
      |    "name" : "age",
      |    "type" : "int",
      |    "default" : 0
      |  } ]
      |}
    """.stripMargin
  val schema: Schema = new Schema.Parser().parse(avroSchema)
}

object v3 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(age: Int = 0, name: String = "")
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "age",
      |    "type" : "int",
      |    "default" : 0
      |  }, {
      |    "name" : "name",
      |    "type" : "string",
      |    "default": ""
      |  } ]
      |}
    """.stripMargin
  val schema: Schema = new Schema.Parser().parse(avroSchema)
}

object v4 {
  @AvroNamespace("com.github.dnvriend")
  case class LivingAddress(street: Option[String] = None, city: Option[String] = None, zipcode: Option[String] = None)
  @AvroNamespace("com.github.dnvriend")
  case class Person(age: Int = 0, name: String = "", livingAddress: Option[LivingAddress] = None)
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "age",
      |    "type" : "int",
      |    "default" : 0
      |  }, {
      |    "name" : "name",
      |    "type" : "string",
      |    "default" : ""
      |  }, {
      |    "name" : "livingAddress",
      |    "type" : [ "null", {
      |      "type" : "record",
      |      "name" : "LivingAddress",
      |      "fields" : [ {
      |        "name" : "street",
      |        "type" : [ "null", "string" ],
      |        "default" : null
      |      }, {
      |        "name" : "city",
      |        "type" : [ "null", "string" ],
      |        "default" : null
      |      }, {
      |        "name" : "zipcode",
      |        "type" : [ "null", "string" ],
      |        "default" : null
      |      } ]
      |    } ],
      |    "default" : null
      |  } ]
      |}
    """.stripMargin

  // create a schema with Avro4s
  val schema: Schema = schemaFor[Person]
}

object v5 {
  @AvroNamespace("com.github.dnvriend")
  case class DeliveryAddress(city: Option[String] = None, zipcode: Option[String] = None, houseNumber: Option[Int] = None)
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: String = "", age: Int = 0, deliveryAddress: Option[DeliveryAddress] = None)
  val schema: Schema = schemaFor[Person]
}

object v6 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: String = "", age: Int = 0, clientType: ClientType = ClientType.STANDARD)
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "name",
      |    "type" : "string",
      |    "default" : ""
      |  }, {
      |    "name" : "age",
      |    "type" : "int",
      |    "default" : 0
      |  }, {
      |    "name" : "clientType",
      |    "type" : {
      |      "type" : "enum",
      |      "name" : "ClientType",
      |      "namespace" : "com.github.dnvriend.evolution",
      |      "symbols" : [ "STANDARD", "PREMIUM" ]
      |    },
      |    "default" : "STANDARD"
      |  } ]
      |}
    """.stripMargin
  val schema: Schema = schemaFor[Person]
}

object v7 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: String = "", age: Int = 0, deliveryOptions: List[DeliveryOption] = List(DeliveryOption.STANDARD))
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "name",
      |    "type" : "string",
      |    "default" : ""
      |  }, {
      |    "name" : "age",
      |    "type" : "int",
      |    "default" : 0
      |  }, {
      |    "name" : "deliveryOptions",
      |    "type" : {
      |      "type" : "array",
      |      "items" : {
      |        "type" : "enum",
      |        "name" : "DeliveryOption",
      |        "namespace" : "com.github.dnvriend.evolution",
      |        "symbols" : [ "STANDARD", "FAST" ]
      |      }
      |    },
      |    "default" : [ "STANDARD" ]
      |  } ]
      |}
    """.stripMargin
  val schema: Schema = schemaFor[Person]
}

object v8 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(
      name: String = "",
      age: Int = 0,
      customerOptions: Map[String, String] = Map(
        "DELIVERY_OPTION" -> DeliveryOption.STANDARD.toString,
        "DISCOUNT_OPTION" -> DiscountPreference.FIRST_SALE.toString))
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "name",
      |    "type" : "string",
      |    "default" : ""
      |  }, {
      |    "name" : "age",
      |    "type" : "int",
      |    "default" : 0
      |  }, {
      |    "name" : "customerOptions",
      |    "type" : {
      |      "type" : "map",
      |      "values" : "string"
      |    },
      |    "default" : {
      |      "DELIVERY_OPTION" : "STANDARD",
      |      "DISCOUNT_OPTION" : "FIRST_SALE"
      |    }
      |  } ]
      |}
    """.stripMargin
  val schema: Schema = schemaFor[Person]
}

object v9 {
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: Array[Byte], age: Double = 0)
  val avroSchema: String =
    """
      |{
      |  "type" : "record",
      |  "name" : "Person",
      |  "namespace" : "com.github.dnvriend",
      |  "fields" : [ {
      |    "name" : "name",
      |    "type" : "bytes"
      |  }, {
      |    "name" : "age",
      |    "type" : "double",
      |    "default" : 0.0
      |  } ]
      |}
    """.stripMargin

  val schema: Schema = SchemaFor[Person]()
}

object v10 {
  // not compatible
  @AvroNamespace("com.github.dnvriend")
  case class Person(name: String, age: Int)
  val schema = schemaFor[Person]
}

class SchemaEvolutionTest extends TestSpec {
  val v1Person = v1.Person("Dennis")
  val v1Hex: String = v1Person.toAvroBinary.toHex

  val v2Person = v2.Person("Dennis", 42)
  val v2Hex: String = v2Person.toAvroBinary.toHex

  val v3Person = v3.Person(42, "Dennis")
  val v3Hex: String = v3Person.toAvroBinary.toHex

  it should "handle reordering of fields" in {
    v1Hex shouldBe "0C44656E6E6973"
    v1Hex.fromHex.parseAvro[v1.Person, v1.Person].value shouldBe v1Person

    v2Hex shouldBe "0C44656E6E697354"
    v2Hex.fromHex.parseAvro[v2.Person, v2.Person].value shouldBe v2Person

    v3Hex shouldBe "540C44656E6E6973"
    v3Hex.fromHex.parseAvro[v3.Person, v3.Person].value shouldBe v3Person

    // reordering: name and age switched
    v2.Person("dennis", 42).toAvroBinary.parseAvro[v3.Person, v2.Person].value shouldBe v3.Person(42, "dennis")

    // or shorter
    v2.Person("dennis", 42).to[v3.Person].value shouldBe v3.Person(42, "dennis")

    // reading v1 to a v2 with default values
    v1.Person("dennis").to[v2.Person].value shouldBe v2.Person("dennis", 0)

    // reading a v1 to a v3 with default values
    v1.Person("dennis").to[v3.Person].value shouldBe v3.Person(0, "dennis")

    // reading a v1 to a v4 with default values
    v1.Person("dennis").to[v4.Person].value shouldBe v4.Person(0, "dennis", None)
  }

  it should "handle removing fields" in {
    // v5 removed the livingAddressField
    v4.Person(
      age = 42,
      name = "Dennis",
      livingAddress = v4.LivingAddress("Kalmoespad".some, "Almere".some, "1313HX".some).some
    ).to[v5.Person].value shouldBe v5.Person("Dennis", 42, None)

    // a v1 can be read by a v5, in the evolution fields were added and removed
    v1.Person("Dennis").to[v5.Person].value shouldBe v5.Person("Dennis", 0, None)
  }

  it should "handle adding fields with default values" in {
    // age has default value 0
    v1.Person("Dennis").to[v2.Person].value shouldBe v2.Person("Dennis", 0)
  }

  it should "handle enum fields" in {
    v1.Person("Dennis").to[v6.Person].value shouldBe v6.Person("Dennis", 0, ClientType.STANDARD)
  }

  it should "handle list types" in {
    v1.Person("Dennis").to[v7.Person].value shouldBe v7.Person("Dennis", 0, List(DeliveryOption.STANDARD))
  }

  it should "handle map types" in {
    v1.Person("Dennis").to[v8.Person].value shouldBe v8.Person("Dennis", 0, Map(
      "DELIVERY_OPTION" -> DeliveryOption.STANDARD.toString,
      "DISCOUNT_OPTION" -> DiscountPreference.FIRST_SALE.toString))
  }

  it should "handle options" in {
    // v3 defines options
    v4.Person(
      age = 42,
      name = "Dennis",
      livingAddress = v4.LivingAddress("Kalmoespad".some, "Almere".some, "1313HX".some).some
    ).to[v4.Person].value shouldBe v4.Person(
      age = 42,
      name = "Dennis",
      livingAddress = v4.LivingAddress("Kalmoespad".some, "Almere".some, "1313HX".some).some
    )
  }

  it should "promote int to long, float, double" in {
    v2.Person("Dennis", 42).to[v9.Person].value.age shouldBe 42.0
  }

  it should "promote string to byte array" in {
    v2.Person("Dennis", 42).to[v9.Person].value.name.toUtf8String shouldBe "Dennis"
  }

  it should "promote byte array to string" in {
    v2.Person("Dennis", 42).to[v9.Person].value.to[v1.Person].value shouldBe v1.Person("Dennis")
  }

  it should "write a json encoded data product" in {
    v4.Person(
      age = 42,
      name = "Dennis",
      livingAddress = v4.LivingAddress("Kalmoespad".some, "Almere".some, "1313HX".some).some
    ).toAvroJson.toUtf8String shouldBe """{"age":42,"name":"Dennis","livingAddress":{"com.github.dnvriend.LivingAddress":{"street":{"string":"Kalmoespad"},"city":{"string":"Almere"},"zipcode":{"string":"1313HX"}}}}"""

    v6.Person("Dennis", 42, ClientType.STANDARD).toAvroJson.toUtf8String shouldBe """{"name":"Dennis","age":42,"clientType":"STANDARD"}"""
  }

  it should "test compatibility" in {
    AvroOps.canReadWith[v2.Person, v1.Person] shouldBe right[Schema]
    AvroOps.canReadWith[v2.Person, v1.Car] shouldBe left[Throwable]

    // check full compatibility
    AvroOps.checkFullCompatibility[v1.Person](
      schemaFor[v2.Person],
      schemaFor[v3.Person],
      schemaFor[v4.Person],
      schemaFor[v5.Person],
      schemaFor[v6.Person],
      schemaFor[v7.Person],
      schemaFor[v8.Person],
      schemaFor[v9.Person],
    ) shouldBe right[Schema]

    // not compatible
    AvroOps.checkFullCompatibility[v10.Person](
      schemaFor[v2.Person],
      schemaFor[v3.Person],
      schemaFor[v4.Person],
      schemaFor[v5.Person],
      schemaFor[v6.Person],
      schemaFor[v7.Person],
      schemaFor[v8.Person],
      schemaFor[v9.Person],
    ) shouldBe left[Throwable]
  }
}
