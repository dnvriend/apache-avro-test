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

import java.io.{ ByteArrayInputStream, InputStream }

import scala.language.implicitConversions
import scala.util.matching.Regex

object StringOps extends StringOps

trait StringOps {
  implicit def toStringOps(that: String): StringOpsImpl = new StringOpsImpl(that)
}

class StringOpsImpl(that: String) {
  def fromBase64: Array[Byte] = {
    java.util.Base64.getDecoder.decode(that)
  }

  def parseBase64: Array[Byte] = {
    fromBase64
  }

  def fromHex: Array[Byte] = {
    javax.xml.bind.DatatypeConverter.parseHexBinary(that)
  }

  def parseHex: Array[Byte] = {
    fromHex
  }

  def toInputStream: InputStream = {
    new ByteArrayInputStream(that.getBytes)
  }

  def toUtf8Array: Array[Byte] = {
    that.getBytes("UTF-8")
  }

  def arr: Array[Byte] = {
    toUtf8Array
  }

  def log: String = {
    println(that)
    that
  }

  def find(regex: Regex): Option[String] = {
    regex.findFirstIn(that)
  }

  def findAll(regex: Regex): List[String] = {
    regex.findAllIn(that).toList
  }
}