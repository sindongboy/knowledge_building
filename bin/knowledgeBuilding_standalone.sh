#!/bin/sh

pid=`ps -ef | grep "com.skplanet.omp.knowledgeBuilding.KnowledgeBuildingTransaction_StandAlone" | grep -v 'grep' | awk '{print $2}'`

case "$1" in
	start)
java -classpath ./knowledgeBuilding-0.1-SNAPSHOT-jar-with-dependencies.jar com.skplanet.omp.knowledgeBuilding.KnowledgeBuildingTransaction_StandAlone "$2" >> ../log/knowledgeBuilding_StandAlone.log 2>&1 &
		echo
		;;
	stop)
		kill -9 $pid
		;;
	*)
		echo "Usage : $0 {start|stop} {properties location}"
		exit 1
		;;
esac
