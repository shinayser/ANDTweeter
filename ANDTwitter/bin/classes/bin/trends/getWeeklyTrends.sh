#/bin/sh
cd ..
. ./setEnv.sh

RUN_CMD="$JAVA_HOME/bin/java $MEM_ARGS -cp $CLASSPATH twitter4j.examples.trends.GetWeeklyTrends"
echo $RUN_CMD ${1+"$@"}
exec $RUN_CMD ${1+"$@"}
