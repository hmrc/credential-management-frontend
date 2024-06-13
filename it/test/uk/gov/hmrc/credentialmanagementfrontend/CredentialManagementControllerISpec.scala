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

import ch.qos.logback.classic.Level
import play.api.Logger
import play.api.libs.ws.DefaultWSCookie
import play.api.test.Helpers.LOCATION
import uk.gov.hmrc.credentialmanagementfrontend.stubs.BaseISpec
import uk.gov.hmrc.play.bootstrap.tools.LogCapturing

import java.net.URLEncoder
import scala.util.Random

class CredentialManagementControllerISpec extends BaseISpec with LogCapturing {

  "sign-in details endpoint" should {
    "respond with 200 status" in {
      val response =
        wsClient
          .url(s"$baseUrl/sign-in-details?olfgJourneyId=${olfgJourneyId.toString}")
          .get()
          .futureValue

      response.status shouldBe 200
      response.body should include("View and manage your sign in details")
    }
  }
}
