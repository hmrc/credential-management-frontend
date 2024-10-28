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

package uk.gov.hmrc.credentialmanagementfrontend.helpers

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import play.api.{Application, Environment, Mode}
import uk.gov.hmrc.http.SessionKeys

import scala.concurrent.ExecutionContext

trait BaseISpec extends AnyWordSpec
  with Matchers
  with ScalaFutures
  with IntegrationPatience
  with GuiceOneServerPerSuite
  with BeforeAndAfterAll
  with BeforeAndAfterEach
  with WireMockSetup {

  val baseUrl = s"http://localhost:$port/credential-management"

  implicit val ec: ExecutionContext = ExecutionContext.Implicits.global

  val defaultSessionValues: Map[String, String] = Map(
    SessionKeys.sessionId -> "sessionId-eb3158c2-0aff-4ce8-8d1b-f2208ace52fe"
  )

  protected def playSessionCookie(data: Map[String, String] = defaultSessionValues): String = SessionCookieBaker.bakeSessionCookie(data)

  protected implicit lazy val wsClient: WSClient = app.injector.instanceOf[WSClient]

  override def fakeApplication(): Application =
    GuiceApplicationBuilder()
      .configure(servicesConfig ++ Map("metrics.enabled" -> false))
      .build()

  def servicesConfig: Map[String, String] = Map(
    "microservice.services.identity-provider-account-context.host" -> host,
    "microservice.services.identity-provider-account-context.port" -> s"$wmPort",
    "microservice.services.centralised-authorisation-server.host" -> host,
    "microservice.services.centralised-authorisation-server.port" -> s"$wmPort",
  )

  override implicit lazy val app: Application = new GuiceApplicationBuilder()
    .in(Environment.simple(mode = Mode.Dev))
    .configure(servicesConfig ++ Map(
      "play.http.router" -> "testOnlyDoNotUseInAppConf.Routes")
    )
    .build()


  override def beforeAll(): Unit = {
    super.beforeAll()
    startWireMock()
  }

  override def afterAll(): Unit = {
    stopWireMock()
    super.afterAll()
  }
}
