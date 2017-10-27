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

import java.io.OutputStream

import play.api.libs.json.{ Json, Writes }

trait OutputStreamOps {
  def toOutputStreamOps[A <: Product: Writes](that: A) = new OutputStreamOpsImpl(that)
}

class OutputStreamOpsImpl[A <: Product: Writes](that: A) extends StringOps {
  def write(os: OutputStream): Unit = {
    os.write(Json.toJson(that).toString().toUtf8Array)
    os.close()
  }
}