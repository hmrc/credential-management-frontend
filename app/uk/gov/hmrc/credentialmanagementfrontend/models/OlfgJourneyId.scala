/*
 * Copyright 2024 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.credentialmanagementfrontend.models

import play.api.libs.json.{Format, Json}
import play.api.mvc.QueryStringBindable

import java.util.UUID

case class OlfgJourneyId(value: UUID) {
  override val toString: String = value.toString
}

object OlfgJourneyId {

  def apply(): OlfgJourneyId = OlfgJourneyId(UUID.randomUUID())
  def apply(s: String): OlfgJourneyId = OlfgJourneyId(UUID.fromString(s))

  implicit val format: Format[OlfgJourneyId] = Json.format[OlfgJourneyId]

  implicit def queryStringBindable(implicit stringBinder: QueryStringBindable[String]): QueryStringBindable[OlfgJourneyId] =
    new QueryStringBindable[OlfgJourneyId] {
      override def bind(key: String, params: Map[String, Seq[String]]): Option[Either[String, OlfgJourneyId]] = {
        for {
          olfgJourneyId <- stringBinder.bind("olfgJourneyId", params)
        } yield {
          olfgJourneyId match {
            case Right(id) => Right(OlfgJourneyId(id))
            case _         => Left("Unable to bind OlfgJourneyId")
          }
        }
      }
      override def unbind(key: String, olfgJourneyId: OlfgJourneyId): String = {
        stringBinder.unbind("olfgJourneyId", olfgJourneyId.toString)
      }
  }
}
