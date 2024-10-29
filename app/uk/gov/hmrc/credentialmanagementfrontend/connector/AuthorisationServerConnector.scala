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

package uk.gov.hmrc.credentialmanagementfrontend.connector


import play.api.Logging
import play.api.http.Status.{INTERNAL_SERVER_ERROR, NOT_FOUND, OK}
import play.api.libs.json.{JsSuccess, Json}
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class AuthorisationServerConnector @Inject()(servicesConfig: ServicesConfig, httpClient: HttpClientV2)
                                            (implicit ec: ExecutionContext) extends Logging {
  private val baseUrl: String = servicesConfig.baseUrl("centralised-authorisation-server")
  private val getTokenAttributesUrl: String = s"$baseUrl/centralised-authorisation-server/token/search"

  implicit object TokenAttributesHttpReads extends HttpReads[Either[ErrorResponseWithStatus, Option[TokenAttributesResponse]]] {
    override def read(method: String, url: String, response: HttpResponse): Either[ErrorResponseWithStatus, Option[TokenAttributesResponse]] = {
      response.status match {
        case OK => response.json.validate[TokenAttributesResponse] match {
          case JsSuccess(tokenAttributes, _) => Right(Some(tokenAttributes))
          case _ =>
            Left(ErrorResponseWithStatus(INTERNAL_SERVER_ERROR,
              "Failed to validate json success OK response from AuthorisationServerConnector"))
        }
        case NOT_FOUND => Right(None)
        case _ =>
          logger.error(s"Received unexpected response. Status: ${response.status}, Exception: ${response.body}")
          Left(ErrorResponseWithStatus(response.status, "INTERNAL_SERVER_ERROR"))

      }
    }
  }

  def getTokenAttributes(gnapToken: String)
                        (implicit hc: HeaderCarrier): Future[Either[ErrorResponseWithStatus, Option[TokenAttributesResponse]]] = {
    httpClient.post(url"$getTokenAttributesUrl")
      .withBody(Json.toJson(TokenAttributesRequest(gnapToken.replace("GNAP ", "").trim)))
      .execute[Either[ErrorResponseWithStatus, Option[TokenAttributesResponse]]]
  }

}