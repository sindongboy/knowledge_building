#!/bin/sh

pid=`ps -ef | grep "com.skplanet.omp.knowledgeBuilding.EngKnowledgeBuildingTransaction_Admin" | grep -v 'grep' | awk '{print $2}'`

case "$1" in
	start)
nohup java -classpath ./knowledgeBuilding-0.1-SNAPSHOT-jar-with-dependencies.jar com.skplanet.omp.knowledgeBuilding.EngKnowledgeBuildingTransaction_Admin >> ../log/knowledgeBuilding_admin_eng.log 2>&1 &
		echo
		;;
	stop)
		kill -9 $pid
		;;
	*)
		echo "Usage : $0 {start|stop}"
		exit 1
		;;
esac
