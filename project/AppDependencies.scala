import sbt.*

object AppDependencies {

  private val bootstrapVersion = "8.5.0"
  

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-frontend-play-30" % bootstrapVersion,
    "uk.gov.hmrc"             %% "play-frontend-hmrc-play-30" % "8.5.0"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"     % bootstrapVersion    % Test,
    "org.jsoup"               %  "jsoup"                      % "1.13.1"            % Test
  )

  val it = Seq.empty
}
