name := """lily-backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,  
  "mysql" 			% "mysql-connector-java" 	% "5.1.18",
  "org.mindrot" 	% "jbcrypt" 				% "0.3m",
  "com.github.scribejava" % "scribejava-apis" 	% "2.3.0",
  "com.github.scribejava" % "scribejava-core" 	% "2.3.0",
  "com.ning" 			  % "async-http-client" % "1.9.33"
)

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator