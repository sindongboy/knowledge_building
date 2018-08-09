#!/bin/sh

home=/svc/omp/knowledgeBuilding
parse_home=$home/bin

category_id=$1
lang=$2

out_crawl_dir=$home/crawl/$lang/$category_id
category_run_path=$out_crawl_dir/run
logfile=$out_log_dir/log

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

__JOB_CODE__=206;
write_run  "start" "crawl sites"

site_list=`cat $out_crawl_dir/site.list`
for site in $site_list
	do
	echo $category_run_path/$site.run $category_id $site
	$category_run_path/$site.run $category_id $site
done

write_run  "end" "crawl site"

write_run  "close" "crawl run"

