#!/bin/sh

build_home=..

source $build_home/omp.ini

node_server_home=$OMP_KB

node_all=$KNOWLEDGE_LIST

home=$build_home/knowledgeBuilding

user=omp

bin=knowledgeBuilding-0.1-SNAPSHOT-jar-with-dependencies.jar

target=""
option="";

Usage()
{
	echo "Usage: $0 target-program [option]"
	echo "          *target-program  : 배포할 프로그램 선택"
	echo "              - all        : 모든 프로그램 배포"
	echo "          * option         : 배포할 대상에 대한 옵션"
	echo "              - all        : 실행에 필요한 모든 파일 배포"
	echo "              - jar        : jar 파일만 배포"
	echo "              - env        : 환경설정 및 리소스 파일들 배포"
	echo "              - shell      : 실행용 쉘만 배포"

	exit 1
}

makeKnowledgeDir()
{
    for node_list in $node_all
    do
        ssh $user@$node_list " mkdir $node_server_home "
        ssh $user@$node_list " mkdir $node_server_home/bin "
        ssh $user@$node_list " mkdir $node_server_home/config "
        ssh $user@$node_list " mkdir $node_server_home/resource "
        ssh $user@$node_list " mkdir $node_server_home/resource/dict "
        ssh $user@$node_list " mkdir $node_server_home/resource/attr "
        ssh $user@$node_list " mkdir $node_server_home/resource/clue "
        ssh $user@$node_list " mkdir $node_server_home/resource/expr "
        ssh $user@$node_list " mkdir $node_server_home/log "
        ssh $user@$node_list " mkdir $node_server_home/tmp "
    done
}


knowledgeBuildingJar()
{
	for node_list in $node_all
	do
		echo "$user@$node_list:$node_server_home/bin"
		scp -P22  ./target/$bin $user@$node_list:$node_server_home/bin
	done
		cp ./target/$bin ./bin
}

knowledgeBuildingConfig()
{
	for node_list in $node_all
	do
		echo "$user@$node_list:$node_server_home/config"
		scp -P22  ./config/* $user@$node_list:$node_server_home/config/
	done
}

knowledgeBuildingDict()
{

	for node_list in $node_all
	do
		echo "$user@$node_list:$node_server_home/resource/dict"
		scp -P22  ./resource/dict/* $user@$node_list:$node_server_home/resource/dict/
	done
}

knowledgeBuildingShell()
{
	for node_list in $node_all
	do
		echo "$user@$node_list:$node_server_home/bin"
		scp -P22  ./bin/* $user@$node_list:$node_server_home/bin
	done
}

knowledgeBuildingLib()
{
  	for node_list in $node_all
	do
		echo "$user@$node_list:$node_server_home/lib"
		scp -P22  ./lib/* $user@$node_list:$node_server_home/bin/lib
	done

}

[[ $# -eq 0 ]] && Usage

if [ $# -eq 1 ]
then 
	target=$1
	option="all"
fi

if [ $# -ge 2 ]
then
	target=$1
	option=$2
fi

echo "========================================================================"
echo "== 서버로 배포 시작                                                   =="
echo "========================================================================"
echo "args # = $#"
echo "target = $target"
echo "option = $option"

makeKnowledgeDir

case $target in
	"all" )
		knowledgeBuildingJar
		knowledgeBuildingConfig
		knowledgeBuildingShell
		knowledgeBuildingDict
		knowledgeBuildingLib
	;;
	"jar" )
		knowledgeBuildingJar
	;;	
	"config" )
		knowledgeBuildingConfig
	;;	
	"shell" )
		knowledgeBuildingShell
	;;	
	"dict" )
		knowledgeBuildingDICT
	;;
esac

echo "========================================================================"
echo "== 서버로 배포 끝                                                     =="
echo "========================================================================"

