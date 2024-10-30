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

package uk.gov.hmrc.credentialmanagementfrontend.service

import play.api.mvc.Request
import uk.gov.hmrc.credentialmanagementfrontend.connector.{AuthorisationServerConnector, ErrorResponseWithStatus, IdentityProviderAccountContextConnector, TokenAttributesResponse}
import uk.gov.hmrc.http.{HeaderCarrier, SessionKeys}

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class CredentialManagementService @Inject()(authorisationServerConnector: AuthorisationServerConnector,
                                            identityProviderAccountContextConnector: IdentityProviderAccountContextConnector) {



  def getTokenAttributesForGnapToken(gnapToken: String)(implicit hc: HeaderCarrier): Future[Either[ErrorResponseWithStatus, Option[TokenAttributesResponse]]] = {
    authorisationServerConnector.getTokenAttributes(gnapToken)
  }

  private def getGnapTokenFromSession(implicit request: Request[_]): Option[String] = {
    request.session.get(SessionKeys.authToken).flatMap(_.split(',')
        .find(t => t.trim.startsWith("GNAP"))
        .map(t => t.trim))
  }

  private def getGnapTokenFromHeader(implicit hc: HeaderCarrier): Option[String] = {
    hc.authorization.flatMap(_.value.split(',')
        .find(t => t.trim.startsWith("GNAP"))
        .map(t => t.trim))
  }

  def getGnapTokenFromSessionOrHeader(implicit request: Request[_], hc: HeaderCarrier): Option[String] = {
    getGnapTokenFromHeader.orElse(getGnapTokenFromSession)
  }

  def validRopcEacdUserId(eacdGroupId: String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Set[String]] = {
    identityProviderAccountContextConnector.getContextViaEacdGroupId(eacdGroupId).map {
      case Some(contextResource) =>
        contextResource.credentials.filter(_.createdBy.contains("credential-management-frontend")).flatMap(_.eacdUserId)
      case None => Set.empty[String]
  }
}


}
