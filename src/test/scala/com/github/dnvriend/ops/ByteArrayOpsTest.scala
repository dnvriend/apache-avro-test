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

class ByteArrayOpsTest extends TestSpec {
  final val HelloWorld = "Hello World!"
  final val HelloWorldBytes = HelloWorld.getBytes("utf-8")
  it should "md5" in {
    HelloWorldBytes.md5 shouldBe "ED076287532E86365E841E92BFC50D8C"
  }

  it should "sha-1" in {
    HelloWorldBytes.sha1 shouldBe "2EF7BDE608CE5404E97D5F042F95F89F1C232871"
  }

  it should "sha-256" in {
    HelloWorldBytes.sha256 shouldBe "7F83B1657FF1FC53B92DC18148A1D65DFC2D4B1FA3D677284ADDD200126D9069"
  }

  it should "base64" in {
    HelloWorldBytes.base64 shouldBe "SGVsbG8gV29ybGQh"
  }

  it should "hex" in {
    HelloWorldBytes.hex shouldBe "48656C6C6F20576F726C6421"
  }

  it should "toUtf8String" in {
    HelloWorldBytes.toUtf8String shouldBe HelloWorld
  }

  it should "convert to inputstream" in {
    HelloWorldBytes.toInputStream.toByteArray.toUtf8String shouldBe HelloWorld
  }

  it should "compress and decompress" in {
    HelloWorldBytes.compress.decompress.toUtf8String shouldBe HelloWorld
  }
}
