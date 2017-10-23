# apache-avro-test
A small study project on schema evolution with Apache AVRO.

## Schema Evolution
Apache AVRO separates the concept of schema and the concept of 'encoded data'. When you split the two from each other, you can focus
on a concept separately.

## Encoded data
AVRO defines the concept of 'encoded data' as the product of taking some application data structure, taking a schema as definition and
taking an encoder to start encoding the data structure. The result can either be a 'binary' product or a 'JSON' product.

The encoded product can be decoded by giving the decoder the data product, the schema used to encode the data product, and of
course the data product itself to produce the applicatiom data structure.

## Schemas
AVRO defines the concept of a 'schema' as a definition that describes a data structure using a taxonomy of types from the AVRO specification.
The specification abstracts every application data structure into arrays, maps, enums, fixed, records, union and primitive types. Using these
types it is possible to describe any application data structure and thus encode any application data structure.

A schema as a concept has properties like a name. So schemas can be identified. For example, the schema `com.github.dnvriend.Person` is a
schema that defines an application data structure `Person`. The schema uses JSON for the description format like:

```json
{
  "type" : "record",
  "name" : "Person",
  "namespace" : "com.github.dnvriend",
  "fields" : [ {
    "name" : "name",
    "type" : "string",
    "default" : ""
  } ]
}
```

The schema has a `namespace` and a `name`, when combined it resolved to the `fully qualified name` of the schema being `com.github.dnvriend.Person`.

## Schema Evolution
The interesting thing is that a schema can exist multiple times. When we use a versioning scheme. with numbers for example, then we can
create a `vector` to a schema like:

`com.github.dnvriend.Person:1` and `com.github.dnvriend.Person:2`. Lets describe both schemas.

Say that version 1 of `com.github.dnvriend.Person` is the following schema:

```json
{
  "type" : "record",
  "name" : "Person",
  "namespace" : "com.github.dnvriend",
  "fields" : [ {
    "name" : "name",
    "type" : "string",
    "default" : ""
  } ]
}
```

And say that version 2 of `com.github.dnvriend.Person` is the following schema:

```
{
  "type" : "record",
  "name" : "Person",
  "namespace" : "com.github.dnvriend",
  "fields" : [ {
    "name" : "name",
    "type" : "string",
    "default" : ""
  }, {
    "name" : "age",
    "type" : "int",
    "default" : 0
  } ]
}
```

Both schemas are the same because they have the fully qualified name `com.github.dnvriend.Person`. This concept is very important.
Both schemas describe a `Person` data structure but do that differently. The obvious difference is that version 2 has an age attribute
that version 1 doesn't contain. We can do interesting things with the concept that the same schema can exist as multiple shapes and when
versioned can be pointed to by means of a vector.

## Reader and Writer Schema
AVRO has an interesting property in that the `encoded data product` can be decoded using a schema and a decoder. What schema to use
to decode the data product with can be calculated by the decoder. In its most simplest form you use the version of the schema you wrote the
data product with. The decoded data structure will have the shape of the schema we used so the data we used to write the data product with.
This schema is called the 'writer's' schema.

Lets say I receive a version 1 data product of Person that I want to decode with a version 2 schema. Well, AVRO supports this. We call
the version 2 the 'reader's schema'. The readers schema is what we use to decode an encoded data product with. The interesting thing is that
we can choose any version of the schema to read the encoded data product with, the only requirement is that we know the schema we have written
the data product with. AVRO will calculate the differences between the reader's schema and the writer's schema and decode the data product to
the shape of the reader's schema.

## Resources
- http://avro.apache.org/
- https://github.com/sksamuel/avro4s
- https://martin.kleppmann.com/2012/12/05/schema-evolution-in-avro-protocol-buffers-thrift.html



