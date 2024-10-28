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

object IdentityProviderAccountContextStub extends WireMockMethods {

  private val contextStoreUrl: String = s"/identity-provider-account-context"

  def returnContext(expectStatus: Int, expectBody: String = "", eacdGroupId: String): StubMapping =
    returnGetStubFor(expectStatus = expectStatus, expectBody = expectBody, callingUrl = s"$contextStoreUrl/contexts?eacdGroupId=$eacdGroupId")

  def returnIndividualContext(expectStatus: Int, expectBody: String = "", caUserId: String): StubMapping =
    returnGetStubFor(expectStatus = expectStatus, expectBody = expectBody, callingUrl = s"$contextStoreUrl/contexts/individual?caUserId=$caUserId")

  def returnNewVerifiedContextCreated(expectStatus: Int, expectBody: String = ""): StubMapping =
    returnPostStubFor(expectStatus = expectStatus, expectBody = expectBody, callingUrl = s"$contextStoreUrl/contexts/individual")

  def returnError(expectStatus: Int, expectBody: String): StubMapping =
    returnPostStubFor(expectStatus = expectStatus, expectBody = expectBody, callingUrl = s"$contextStoreUrl/contexts/individual")

}
