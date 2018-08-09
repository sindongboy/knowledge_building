java -Xmx4096m -XX:+UseParallelGC -Xverify:none -classpath .:./lib/mnlp.1.0.3.jar -Djava.library.path=./lib POSTaggerTest_han eng $MNLP_HOME_DIR_PATH/resource/bin_data/ $1 $2 3
