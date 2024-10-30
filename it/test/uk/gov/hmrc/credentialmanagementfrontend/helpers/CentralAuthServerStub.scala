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

import com.github.tomakehurst.wiremock.stubbing.StubMapping


object CentralAuthServerStub extends WireMockMethods {

  private val centralAuthServerBaseUrl: String = s"/centralised-authorisation-server"

  def centralAuthServerStub(expectStatus: Int, expectReqBody: String, expectBody: String): StubMapping =
    returnPostStubForWithReqBody(
      expectStatus = expectStatus,
      expectBody = expectBody,
      expectReqBody = expectReqBody,
      callingUrl = s"$centralAuthServerBaseUrl/token/search")


}