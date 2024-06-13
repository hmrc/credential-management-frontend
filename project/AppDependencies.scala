import sbt.*

object AppDependencies {

  private val playVersion      = "play-30"
  private val bootstrapVersion = "9.0.0"
  private val frontendVersion  = "10.0.0"

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-frontend-$playVersion" % bootstrapVersion,
    "uk.gov.hmrc"             %% s"play-frontend-hmrc-$playVersion" % frontendVersion
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"             %% s"bootstrap-test-$playVersion"     % bootstrapVersion   % Test
  )
}
