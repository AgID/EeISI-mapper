#!/bin/sh 
#############################################################################
#
# NAME        : Infocert - Eigor CLI Client
# DESCRIPTION : Eigor CLI Client
#
#############################################################################


#############################################################################

APP_HOME=`pwd`
echo "APP_HOME=${APP_HOME}"

OS=`uname -s`
PID=
PROCESS_OWNER=`whoami`

CLASSPATH=".:${APP_HOME}/lib/*:$APP_HOME/conf:$APP_HOME/converterdata"


echo "Using JDK at ${JAVA_HOME:?Please set JAVA_HOME environment variable}"

echo ${CLASSPATH}
cd ${APP_HOME}
mkdir -p reports
#JAVA_OPTS="-server -Xmx512m -Dapp=${APP_NAME} -Dlogback.configurationFile=${APP_HOME}/config/logback.xml -Deigor.configurationFile=%APPLICATION_HOME%/conf/eigor.properties"
JAVA_OPTS="-Xmx512m  -Dlogback.configurationFile=${APP_HOME}/conf/logback.xml"
START_CLASS=it.infocert.eigor.cli.Eigor
JAVA_ARGS="$1 $2 $3 $4 $5 $6 $7 $8 $9"

echo "java ${JAVA_OPTS} -cp ${CLASSPATH} ${START_CLASS} ${JAVA_ARGS}"
java ${JAVA_OPTS} -cp "${CLASSPATH}" ${START_CLASS} ${JAVA_ARGS}


