ThisBuild / version := "1.0"
ThisBuild / scalaVersion := "2.12.15" //2.12.15

lazy val root = (project in file("."))
  .settings(
    name := "ProgettoScalable",
    mainClass := Some("copurchase"),
    libraryDependencies ++= Seq(
      "org.apache.spark" %% "spark-core" % "3.1.3", //3.1.3
      "org.apache.spark" %% "spark-sql" % "3.1.3"
    )
  )
