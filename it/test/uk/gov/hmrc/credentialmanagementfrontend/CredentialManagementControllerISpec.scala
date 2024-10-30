/*
 * Copyright 2023 HM Revenue & Customs
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

package uk.gov.hmrc.credentialmanagementfrontend

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.http.HeaderNames
import play.api.http.Status.{FORBIDDEN, INTERNAL_SERVER_ERROR}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.libs.ws.WSClient
import play.api.test.Helpers.{AUTHORIZATION, OK}
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.credentialmanagementfrontend.connector.{ContextResource, Credential, TokenAttributesRequest, TokenAttributesResponse}
import uk.gov.hmrc.credentialmanagementfrontend.helpers.{BaseISpec, CentralAuthServerStub, IdentityProviderAccountContextStub}
import uk.gov.hmrc.http.SessionKeys.authToken
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

class CredentialManagementControllerISpec extends BaseISpec with LogCapturing {

  "sign-in details endpoint" should {
    "respond with 200 status" in {
      val response =
        wsClient
          .url(s"$baseUrl/sign-in-details")
          .get()
          .futureValue

      response.status shouldBe 200
      response.body should include("View and manage your sign in details")
    }
  }
  "respond with 501 status for ropcRegister endpoint" in {
    val response = wsClient.url(s"$baseUrl/ropc-register").get().futureValue
    response.status shouldBe 501
  }

  "respond with 501 status for legacyLogin endpoint" in {
    val response = wsClient.url(s"$baseUrl/legacy-login").get().futureValue
    response.status shouldBe 501
  }

  "respond with 501 status for lostCredsEmail endpoint" in {
    val response = wsClient.url(s"$baseUrl/lost-creds-email").get().futureValue
    response.status shouldBe 501
  }

  "guidance endpoint" should {

    val gnapTokenHeader = "Bearer 0987654321, GNAP validGnapToken"

    "redirect to guidanceManagePage if valid ROPC EACD user ID is found" in {
      CentralAuthServerStub.centralAuthServerStub(OK,
        Json.toJson(TokenAttributesRequest("validGnapToken")).toString(),
        Json.toJson(TokenAttributesResponse("credId", Some("eacdGroupId"), Some("caUserId"))).toString()
      )
      IdentityProviderAccountContextStub.returnContext(OK,
        Json.toJson(ContextResource(Some("AA000003D"), "eacdGroupId", Set(Credential("credId", Some("eacdUserId"), Some("credential-management-frontend"))))).toString(),
        "eacdGroupId"
      )

      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> gnapTokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> gnapTokenHeader)))
        .get().futureValue

      response.status shouldBe OK
      response.body should include("Our records show that you already have Government Gateway sign in details. Look up your user ID and reset your password below")
    }

    "redirect to guidanceCreatePage if no valid ROPC EACD user ID is found" in {

      CentralAuthServerStub.centralAuthServerStub(OK,
        Json.toJson(TokenAttributesRequest("validGnapToken")).toString(),
        Json.toJson(TokenAttributesResponse("credId", Some("eacdGroupId"), Some("caUserId"))).toString()
      )
      IdentityProviderAccountContextStub.returnContext(OK,
        Json.toJson(ContextResource(Some("AA000003D"), "eacdGroupId", Set(Credential("credId", Some("eacdUserId"), Some("credential-management"))))).toString(),
        "eacdGroupId"
      )

      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> gnapTokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> gnapTokenHeader)))
        .get().futureValue

      response.status shouldBe OK
      response.body should include("Create Government Gateway sign in details")
    }

    "return FORBIDDEN if eacdGroupId is not defined in the TokenAttributesResponse" in {

      CentralAuthServerStub.centralAuthServerStub(OK,
        Json.toJson(TokenAttributesRequest("validGnapToken")).toString(),
        Json.toJson(TokenAttributesResponse("credId", None, Some("caUserId"))).toString()
      )

      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> gnapTokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> gnapTokenHeader)))
        .get().futureValue

      response.status shouldBe FORBIDDEN
    }

    "return internal server error if the Authorisation server returns an invalid json" in {

      CentralAuthServerStub.centralAuthServerStub(OK,
        Json.toJson(TokenAttributesRequest("validGnapToken")).toString(),
        """{"invalid":"json"}"""
      )

      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> gnapTokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> gnapTokenHeader)))
        .get().futureValue

      response.status shouldBe INTERNAL_SERVER_ERROR
    }

    "return FORBIDDEN if user is not defined via the TokenAttributesResponse" in {

      CentralAuthServerStub.centralAuthServerStub(404, Json.toJson(TokenAttributesRequest("validGnapToken")).toString(), "")

      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> gnapTokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> gnapTokenHeader)))
        .get().futureValue

      response.status shouldBe FORBIDDEN
    }

    "return Internal Server Error if 500 is returned from the Authorisation server" in {

      CentralAuthServerStub.centralAuthServerStub(500, Json.toJson(TokenAttributesRequest("validGnapToken")).toString(), "")

      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> gnapTokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> gnapTokenHeader)))
        .get().futureValue

      response.status shouldBe INTERNAL_SERVER_ERROR
    }

    "return FORBIDDEN if no Gnap token is found" in {

      val tokenHeader = "Bearer 0987654321"
      val response = wsClient.url(s"$baseUrl/guidance")
        .withFollowRedirects(false)
        .withHttpHeaders(AUTHORIZATION -> tokenHeader, HeaderNames.COOKIE -> playSessionCookie(Map(authToken -> tokenHeader)))
        .get().futureValue

      response.status shouldBe FORBIDDEN
    }
  }
}

class CredentialManagementControllerISpec2 extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with GuiceOneServerPerSuite
  with LogCapturing {

  val baseUrl = s"http://localhost:$port/credential-management"

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(
        "metrics.enabled" -> false,
        "features.sign-in-details" -> false)
      .build()

  def servicesConfig: Map[String, String] = Map(
    "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes")

  val app2: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig)
    .build()
  val wsClient: WSClient = app2.injector.instanceOf[WSClient]

  "sign-in details endpoint when sign-in details feature disabled" should {
    "respond 501" in {
      val response =
        wsClient
          .url(s"$baseUrl/sign-in-details")
          .get()
          .futureValue

      response.status shouldBe 501
    }
  }
}
