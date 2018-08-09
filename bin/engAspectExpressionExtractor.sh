if [ $# -ne 2 ]
then
    echo "Usage: $0 [input data] [output data]"
else
echo "실행"
java -classpath ./knowledgeBuilding-0.1-SNAPSHOT-jar-with-dependencies.jar com.skplanet.omp.knowledgeBuilding.EngAspectExpressionExtractor $1 $2 >> ../log/engAspectExpressionExtractor.log 2>&1 &
fi
