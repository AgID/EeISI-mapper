@echo off

SET APP_NAME=Infocert - Eigor CLI Client
SET APP_PATH=- %~dp0

TITLE %APP_NAME%
COLOR B
cd /d %~dp0
SET APPLICATION_HOME=.
SET MAIN_CLASS=it.infocert.eigor.cli.Eigor

SET JVM_ARGUMENTS= -Xmx512m  -Dlogback.configurationFile=%APPLICATION_HOME%\conf\logback.xml

if not exist %APPLICATION_HOME%\reports mkdir %APPLICATION_HOME%\reports
SET APP_ARGS=%1 %2 %3 %4 %5 %6 %7 %8

setlocal ENABLEDELAYEDEXPANSION

cd %APPLICATION_HOME%

set LIB_CLASSPATH=
for %%i in (%APPLICATION_HOME%\lib\*.jar) do  set LIB_CLASSPATH=%%i;!LIB_CLASSPATH!

SET CLASSPATH=%LIB_CLASSPATH%;%APPLICATION_HOME%\conf;

IF "%JAVA_HOME%"== "" GOTO :noJavaHome ELSE GOTO :runWithJavaHome


:runWithJavaHome
SET JAVA_COMMAND="%JAVA_HOME%\bin\java"
goto :end

:noJavaHome
ECHO WARNING: JAVA_HOME not defined, please define your JAVA_HOME in environment.
ECHO -------------------------------------------------------------
ECHO Looking for a java runtime environment:
SET JAVA_COMMAND=java

goto :end

:end
ECHO --------------------------------------------------------------
%JAVA_COMMAND% -version
ECHO --------------------------------------------------------------
echo LAUNCHING %APP_NAME% %APP_PATH%
ECHO --------------------------------------------------------------
%JAVA_COMMAND% %JVM_ARGUMENTS% %MAIN_CLASS% %APP_ARGS%

pause

