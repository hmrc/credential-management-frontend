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
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, StringContextOps}
import uk.gov.hmrc.play.bootstrap.config.ServicesConfig
import uk.gov.hmrc.http.HttpReads.Implicits._

import javax.inject.{Inject, Singleton}
import java.net.URL

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class IdentityProviderAccountContextConnector@Inject()(servicesConfig: ServicesConfig, httpClient: HttpClientV2)
                                                      (implicit ec: ExecutionContext) extends Logging {
  private val baseUrl: String = servicesConfig.baseUrl("identity-provider-account-context")
  private val getContextViaEacdGroupIdUrl: String = s"$baseUrl/identity-provider-account-context/contexts?eacdGroupId="

  def getContextViaEacdGroupId(eacdGroupId: String)(implicit hc: HeaderCarrier): Future[Option[ContextResource]] =
    httpClient.get(new URL(getContextViaEacdGroupIdUrl + eacdGroupId)).execute[Option[ContextResource]]
}