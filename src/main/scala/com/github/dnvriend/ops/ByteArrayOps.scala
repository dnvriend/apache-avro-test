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

import java.io.{ ByteArrayInputStream, ByteArrayOutputStream, InputStream, OutputStream }
import java.nio.ByteBuffer

import play.api.libs.json.{ JsValue, Json }

import scala.language.implicitConversions

object ByteArrayOps extends ByteArrayOps

trait ByteArrayOps {
  implicit def toByteArrayOps(that: Array[Byte]): ByteArrayOpsImpl = new ByteArrayOpsImpl(that)

  def withOutputStream(f: OutputStream => Unit): Array[Byte] = {
    val baos = new ByteArrayOutputStream()
    f(baos)
    baos.close()
    baos.toByteArray
  }
}

class ByteArrayOpsImpl(that: Array[Byte]) {
  def hex: String = {
    javax.xml.bind.DatatypeConverter.printHexBinary(that)
  }
  def compress: Array[Byte] = {
    val bos = new java.io.ByteArrayOutputStream(that.length)
    val gzip = new java.util.zip.GZIPOutputStream(bos)
    gzip.write(that)
    gzip.close()
    bos.close()
    bos.toByteArray
  }

  def decompress: Array[Byte] = {
    val bis = new ByteArrayInputStream(that)
    val gzip = new java.util.zip.GZIPInputStream(bis, that.length)
    Stream.continually(gzip.read()).takeWhile(_ != -1).map(_.toByte).toArray
  }

  def toInputStream: InputStream = {
    new java.io.ByteArrayInputStream(that)
  }

  def toByteBuffer: ByteBuffer = {
    java.nio.ByteBuffer.wrap(that)
  }

  def toUtf8String: String = {
    new String(that, "UTF-8")
  }

  def log: String = {
    println(toUtf8String)
    toUtf8String
  }

  def base64: String = {
    java.util.Base64.getEncoder.encodeToString(that)
  }

  def md5: String = {
    import ByteArrayOps._
    java.security.MessageDigest.getInstance("MD5").digest(that).hex
  }

  def sha1: String = {
    import ByteArrayOps._
    java.security.MessageDigest.getInstance("SHA-1").digest(that).hex
  }

  def sha256: String = {
    import ByteArrayOps._
    java.security.MessageDigest.getInstance("SHA-256").digest(that).hex
  }

  def json: JsValue = {
    Json.parse(that)
  }
}