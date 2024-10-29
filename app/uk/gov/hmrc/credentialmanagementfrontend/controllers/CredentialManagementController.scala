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

package uk.gov.hmrc.credentialmanagementfrontend.controllers

import play.api.Logging
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import uk.gov.hmrc.credentialmanagementfrontend.config.{AppConfig, ErrorHandler}
import uk.gov.hmrc.credentialmanagementfrontend.service.CredentialManagementService
import uk.gov.hmrc.credentialmanagementfrontend.views.html.{GuidanceCreate, GuidanceManage, SignInDetailsPage}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendController

import javax.inject.{Inject, Singleton}
import scala.concurrent.ExecutionContext

@Singleton
class CredentialManagementController @Inject()(mcc: MessagesControllerComponents,
                                               signInDetailsPage: SignInDetailsPage,
                                               guidanceCreatePage: GuidanceCreate,
                                               guidanceManagePage: GuidanceManage,
                                               credentialManagementService: CredentialManagementService,
                                               appConfig:AppConfig)(implicit ec: ExecutionContext,
                                                                    val errorHandler: ErrorHandler,
)
  extends FrontendController(mcc) with Logging {

  def signInDetails(): Action[AnyContent] = Action { implicit request =>
    if (appConfig.signInDetailsEnabled) {
      Ok(signInDetailsPage())
    } else {
      NotImplemented
    }
  }

  def ropcRegister(): Action[AnyContent] = Action(NotImplemented("www.example.com"))

  def legacyLogin(): Action[AnyContent] = Action(NotImplemented("www.example.com"))

  def lostCredsEmail(): Action[AnyContent] = Action(NotImplemented("www.example.com"))

  // If user has gnap token we assumes that this session is a potentially valid ropc User
  // they will be redirected to the Create page
  // to see the Manage page the user needs the createdBy value in Account store to be "credential-management-frontend"
  // TODO change this logic and check the use is allowed to see the created or manage page
  def guidance(): Action[AnyContent] = Action.async { implicit request =>
    credentialManagementService.getGnapTokenFromSessionOrHeader match {
      case Some(gnapToken) =>
        credentialManagementService.getTokenAttributesForGnapToken(gnapToken).flatMap {
          case Right(Some(tokenAttributes)) if tokenAttributes.eacdGroupId.isDefined =>
            credentialManagementService.validRopcEacdUserId(tokenAttributes.eacdGroupId.get).map{ validRopcEacdUserIds =>
              if(validRopcEacdUserIds.nonEmpty)
                Ok(guidanceManagePage())
              else
                Ok(guidanceCreatePage())
            }
          case Right(_) =>
            logger.warn("Failed to get Gnap token attributes or eacdGroupId not defined from Gnap token attributes")
            errorHandler.handleError(FORBIDDEN)
          case Left(errorResponseWithStatus) =>
            logger.error(s"Failed to get token attributes for gnap token: $gnapToken. Error: ${errorResponseWithStatus.errorResponse}")
            errorHandler.handleError(errorResponseWithStatus.status)
        }
      case None =>
        logger.warn("Failed to get gnap token from session or header")
        errorHandler.handleError(FORBIDDEN)
    }
  }
}
