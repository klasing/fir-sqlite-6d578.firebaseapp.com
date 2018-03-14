ECHO OFF

SET JAVA_HOME=C:\Progra~1\Java\jdk1.8.0_131\bin\

CD ..
SET CLASSPATH=
SET CLASSPATH=.\build
SET CLASSPATH=%CLASSPATH%;.\lib\commons-logging-1.1.1.jar
SET CLASSPATH=%CLASSPATH%;.\lib\firebase-admin-5.1.0.jar
REM SET CLASSPATH=%CLASSPATH%;.\lib\firebase-admin-5.9.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\google-api-client-1.23.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\google-api-client-gson-1.23.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\google-http-client-1.23.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\google-http-client-gson-1.23.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\google-http-client-jackson2-1.23.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\google-oauth-client-1.23.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\gson-2.1.jar
SET CLASSPATH=%CLASSPATH%;.\lib\guava-24.0-jre.jar
SET CLASSPATH=%CLASSPATH%;.\lib\httpclient-4.5.5.jar
SET CLASSPATH=%CLASSPATH%;.\lib\httpcore-4.4.9.jar
SET CLASSPATH=%CLASSPATH%;.\lib\jackson-annotations-2.8.0.jar
SET CLASSPATH=%CLASSPATH%;.\lib\jackson-core-2.1.3.jar
SET CLASSPATH=%CLASSPATH%;.\lib\jackson-core-asl-1.9.13.jar
SET CLASSPATH=%CLASSPATH%;.\lib\jackson-mapper-asl-1.9.13.jar
SET CLASSPATH=%CLASSPATH%;.\lib\json-20180130.jar
SET CLASSPATH=%CLASSPATH%;.\lib\jsr305-1.3.9.jar
SET CLASSPATH=%CLASSPATH%;.\lib\protobuf-java-2.6.1.jar
SET CLASSPATH=%CLASSPATH%;.\lib\xpp3-1.1.4c.jar

ECHO [LIBRARY]

ECHO [COMPILE]
%JAVA_HOME%javac -Xlint:deprecation -Xlint:unchecked src\*.java

ECHO [DEPLOY]
MOVE src\*.class build\src

ECHO [JAVADOC]
REM %JAVA_HOME%javadoc -d javadoc src\*.java

ECHO [RUN]
%JAVA_HOME%java src.Main

CD tool
