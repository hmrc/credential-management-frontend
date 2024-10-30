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


import com.github.tomakehurst.wiremock.client.WireMock._
import com.github.tomakehurst.wiremock.stubbing.StubMapping


trait WireMockMethods {

  def returnPostStubFor(expectStatus: Int, expectBody: String, callingUrl: String): StubMapping = {
    stubFor(post(urlEqualTo(callingUrl))
      .willReturn(
        aResponse().
          withStatus(expectStatus).
          withBody(expectBody)
      )
    )
  }

  def returnPostStubForWithReqBody(expectStatus: Int, expectReqBody: String, expectBody: String, callingUrl: String): StubMapping = {
    stubFor(post(urlEqualTo(callingUrl))
      .withRequestBody(equalToJson(expectReqBody))
      .willReturn(
        aResponse().
          withStatus(expectStatus).
          withBody(expectBody)
      )
    )
  }

  def returnGetStubFor(expectStatus: Int, expectBody: String, callingUrl: String): StubMapping = {
    stubFor(get(urlEqualTo(callingUrl))
      .willReturn(
        aResponse().
          withStatus(expectStatus).
          withBody(expectBody)
      )
    )
  }
}
