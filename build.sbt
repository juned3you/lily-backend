name := """lily-backend"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(  
  javaJdbc,
  javaJpa,
  cache,  
  //"org.postgresql" 		% "postgresql" 					% "9.4.1208.jre7",
  "mysql" 					% "mysql-connector-java" 		% "5.1.18",
  "org.mindrot" 			% "jbcrypt" 					% "0.3m",
  "com.github.scribejava" 	% "scribejava-apis" 			% "2.3.0",
  "com.github.scribejava" 	% "scribejava-core" 			% "2.3.0",
  "com.ning" 			  	% "async-http-client" 			% "1.9.33",
  "commons-beanutils" 	  	% "commons-beanutils" 			% "1.9.2",
  "org.hibernate" 		  	% "hibernate-entitymanager" 	% "5.1.0.Final",
  "org.mongodb" 		  	% "mongo-java-driver" 			% "3.2.2",
  "org.mongodb.morphia" 	% "morphia" 					% "1.1.1",
  "org.mongodb.morphia" 	% "morphia-logging-slf4j" 		% "1.1.1",
  "org.mongodb.morphia" 	% "morphia-validation" 			% "1.1.1"  
)



PlayKeys.externalizeResources := false

// Play provides two styles of routers, one expects its actions to be injected, the
// other, legacy style, accesses its actions statically.
routesGenerator := InjectedRoutesGenerator