#!/bin/sh

home=/svc/omp/knowledgeBuilding
parse_home=$home/bin

category_id=$1
lang=$2

out_crawl_dir=$home/crawl/$lang/$category_id
out_data_dir=../data/$category_id
out_log_dir=../log/$category_id
category_run_path=$out_crawl_dir/run

logfile=$out_log_dir/crawl_log

#
# define functions
#
write_run()
{
	date_str=`date '+%Y/%m/%d %H:%M:%S'`
	echo "$__JOB_CODE__|$date_str|$1|$2|$3" >> $logfile
}

error_exit()
{
	write_run "error" "$1"
	exit $__JOB_CODE__
}

#
# Transaction Main
#
write_run  "start" "crawl naver shopping"
object_list=`cat $out_crawl_dir/run.list`
for object in $object_list
do
	echo $category_run_path/naver_shopping.run $object
	$category_run_path/naver_shopping.run $object
done
write_run  "end" "crawl naver shopping"
