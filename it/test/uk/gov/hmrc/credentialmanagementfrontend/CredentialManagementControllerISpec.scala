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
import play.api.{Application, Environment, Mode}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.credentialmanagementfrontend.stubs.BaseISpec
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
}

class CredentialManagementControllerISpec2 extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with GuiceOneServerPerSuite
  with LogCapturing {

  val baseUrl  = s"http://localhost:$port/credential-management"

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
