#!/bin/sh

home=/svc/omp/knowledgeBuilding
parse_home=$home/bin

category_id=$1
lang=$2

object_path=OBJECT
tmp_dir=../tmp
clue_dir=clue
cate_dir=cate

out_crawl_dir=$home/crawl/$lang/$category_id
out_data_dir=../data/$category_id
out_log_dir=../log/$category_id

category_inf_path=$out_crawl_dir/inf
category_run_path=$out_crawl_dir/run
category_clue_file=$out_crawl_dir/$clue_dir/$category_id.clue
category_cate_file=$out_crawl_dir/$cate_dir/$category_id.cate
category_object_file=$object_path/$category_id.txt


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

copy_run()
{
	run_list=`ls $tmp_dir/run`

	for run_tmp in $run_list
	do
		count=0
		while read line;
	    do
			count=$(($count+1))

			echo $line >> $category_run_path/$run_tmp

			if [ $count -eq 2 ]
			then echo "crawl_home=$out_crawl_dir" >>  $category_run_path/$run_tmp
			fi

		done < $tmp_dir/run/$run_tmp

		chmod 755 $category_run_path/$run_tmp
	done
}


init_system()
{
	if [ -d $out_crawl_dir/run ]
	    then rm -rf $out_crawl_dir/run  ||  error_exit "clear dir $out_crawl_dir/run"
	fi
	
	mkdir $out_crawl_dir/run || error_exit "mkdir fail $out_crawl_dir/run"
	copy_run;
}

make_seed()
{
	cateLs=$out_crawl_dir"/"$cate_dir"/"$category_id".cate"

	echo $cateLs;
	
	cateList=`cat $cateLs`

	seed=`cat $tmp_dir/seed/naver_shopping.seed`

	cat $cateLs | awk '{print $0}' | awk -v arg1=$seed -v arg2=$category_inf_path '{fname=arg2"/"$1"_shop.seed";print arg1$2 >> fname }'

	cat $cateLs | awk '{print $0}' | awk -v arg1=$out_crawl_dir '{fname=arg1"/run.list";print $1 >> fname }'
}


if [ ! -d $out_log_dir ]
then mkdir $out_log_dir  ||  error_exit "mkdir fail $out_log_dir"
fi

if [ -f $logfile ]
then rm -f $logfile
fi


#
# Transaction Main
#
__JOB_CODE__=255;
write_run  "start" "batch script"

__JOB_CODE__=200;
write_run  "start" "init system"
init_system
write_run  "close" "init system"

__JOB_CODE__=202;
write_run  "start" "make seed naver shopping"
make_seed
write_run  "close" "make seed naver shopping"
