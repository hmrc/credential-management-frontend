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

package uk.gov.hmrc.credentialmanagementfrontend.helpers

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig
import org.scalatestplus.play.guice.GuiceOneServerPerSuite

trait WireMockSetup {

  self: GuiceOneServerPerSuite =>

  val wmPort: Int = 11111
  val host: String = "localhost"

  lazy val wmConfig: WireMockConfiguration = wireMockConfig.port(wmPort)
  lazy val wmServer: WireMockServer = new WireMockServer(wmConfig)

  def startWireMock(): Unit = {
    wmServer.start()
    WireMock.configureFor(host, wmPort)
  }

  def resetStubs(): Unit = {
    wmServer.resetAll()
  }

  def stopWireMock(): Unit = wmServer.stop()

}
