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

package com.github.dnvriend.matching

import com.github.dnvriend.TestSpec
import com.github.dnvriend.matching.MatchingTest.{ Nel, Person }

import scala.reflect.runtime.universe._
import scalaz._
import scalaz.Scalaz._

object MatchingTest {
  type Nel[A] = NonEmptyList[A]
  case class Person(name: String)
}

class MatchingTest extends TestSpec {

  def determineWithOutTypeTag[A](xs: List[A]): String = xs match {
    case ys: List[String] => "string"
    case ys: List[Int]    => "int"
    case ys: List[Long]   => "long"
    case ys: List[Person] => "person"
  }

  def determineWithTypeTagList[A: TypeTag](xs: List[A]): String = typeOf[A] match {
    case t if t =:= typeOf[String] =>
      val ys: List[String] = xs.asInstanceOf[List[String]]
      "string"
    case t if t =:= typeOf[Int] =>
      val ys: List[Int] = xs.asInstanceOf[List[Int]]
      "int"
    case t if t =:= typeOf[Long] =>
      val ys: List[Long] = xs.asInstanceOf[List[Long]]
      "long"
    case t if t =:= typeOf[Person] =>
      val ys: List[Person] = xs.asInstanceOf[List[Person]]
      "person"
  }

  def determineWithTypeTagNel[A: TypeTag](xs: Nel[A]): String = typeOf[A] match {
    case t if t =:= typeOf[String] =>
      val ys: Nel[String] = xs.asInstanceOf[Nel[String]]
      "string"
    case t if t =:= typeOf[Int] =>
      val ys: Nel[Int] = xs.asInstanceOf[Nel[Int]]
      "int"
    case t if t =:= typeOf[Long] =>
      val ys: Nel[Long] = xs.asInstanceOf[Nel[Long]]
      "long"
    case t if t =:= typeOf[Person] =>
      val ys: Nel[Person] = xs.asInstanceOf[Nel[Person]]
      "person"
  }

  it should "determineWithOutTypeTag" in {
    determineWithOutTypeTag(List("a")) shouldBe "string"
    determineWithOutTypeTag(List(1)) shouldBe "string"
    determineWithOutTypeTag(List(2L)) shouldBe "string"
    determineWithOutTypeTag(List(Person("Dennis"))) shouldBe "string"
  }

  it should "determineWithTypeTagList" in {
    determineWithTypeTagList(List("a")) shouldBe "string"
    determineWithTypeTagList(List(1)) shouldBe "int"
    determineWithTypeTagList(List(2L)) shouldBe "long"
    determineWithTypeTagList(List(Person("Dennis"))) shouldBe "person"
  }

  it should "determineWithTypeTagNel" in {
    determineWithTypeTagNel(NonEmptyList("a")) shouldBe "string"
    determineWithTypeTagNel(NonEmptyList(1)) shouldBe "int"
    determineWithTypeTagNel(NonEmptyList(2L)) shouldBe "long"
    determineWithTypeTagNel(NonEmptyList(Person("Dennis"))) shouldBe "person"
  }
}
