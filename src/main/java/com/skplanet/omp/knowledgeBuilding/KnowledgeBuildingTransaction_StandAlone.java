package com.skplanet.omp.knowledgeBuilding;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import com.skplanet.nlp.utils.UtilPropertyReader;
import com.skplanet.nlp.utils.UtilTimer;

/**
 * 커맨드로 부터 요청을 받아 속성과 속성-표현후보 추출기를 실행하는 Transaction 클래스<p>
 *
 * @version 0.2
 * @since 2012.09.03
 * @author 한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier 한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file KnowledgeBuildingTransaction_StandAlone.java
 * @history  2012.09.03 * v0.1 클래스 최초 생성. 한영섭.<br>
 * @history  2012.11.06 * v0.2 default encording UTF-8 처리 추가. 한영섭.<br>
 * @history   2012.11.06 * v0.1주석 업데이트. 한영섭.<br>
 * @history   2013.03.18 * v0.1주석 업데이트. 한영섭.<br>
 */
public class KnowledgeBuildingTransaction_StandAlone {

	/**
	 * 지식구축 설정 파일의 위치
	 */
	private String  PROP_FILE_NAME = "config/knowledgeBuilding.properties";

	private final String MAVEN_BASE_DIR = "../";

	private String MAINDIRECTORY  = "/Users/sindongboy/Dropbox/Documents/workspace/knowledgeBuilding";

	private String STANDALONE_MAIN  = "/Users/sindongboy/Dropbox/Documents/workspace/knowledgeBuilding/StandAlone";

	private String DATA_LOCATION  = STANDALONE_MAIN + "/data";

	private String DICT_LOCATION  = STANDALONE_MAIN + "/dict";

	private String CLUE_LOCATION  = STANDALONE_MAIN + "/clue/test.clue";

	private String TOPICNAME  = "test";

	private String NOUNPOS  = "nng,nnp,nnk,eng,xsn,nnb";

	private String CLUEJK  = "에서,은,는,에는,이,에,의,으로";

	private String ATTRJK  = "은,는,이,가,도,까지,면에서,측면에서,에,에도,에는,에서는,에서도";

	private String CHARSET  = "UTF-8";

	/**
	 * 커맨드 형태의 지식구축 main()<br>
	 * - 커맨드에서 속성-표현 후보 추출하기 위한 Main() 함수.<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param args
	 * @exception none
	 */
	public static void main(String[] args) throws Exception {

		KnowledgeBuildingTransaction_StandAlone kmStandAlone = new KnowledgeBuildingTransaction_StandAlone();

		if((args.length>1)){
			System.out.println("Wrong Parameter !");
		}
		else
		{
			if((args.length==1)&&(!args[0].trim().equals(""))){
				System.out.println("Now Custom Properties Loading !");
				kmStandAlone.PROP_FILE_NAME = args[0];
			}

			kmStandAlone.init();
			System.out.println();

			//common
			String encoding  = kmStandAlone.CHARSET;
			String topicName = kmStandAlone.TOPICNAME;

			//input
			String mainDirectory = kmStandAlone.MAINDIRECTORY;
			String standAloneDirectory = kmStandAlone.STANDALONE_MAIN;

			String dictDirectory  = kmStandAlone.DICT_LOCATION;
			String dataLocation = kmStandAlone.DATA_LOCATION;
			String clueLocation = kmStandAlone.CLUE_LOCATION;

			//output
			String topicDirectory  = standAloneDirectory +"/"+topicName;
			String attrDirectory  = standAloneDirectory +"/"+topicName+"/attr";
			String exprDirectory  = standAloneDirectory +"/"+topicName+"/expr";
			String attrFromClue = attrDirectory+"/"+topicName+"_clue.txt";
			String attrFromExpr  = attrDirectory+"/"+topicName+"_expr.txt";
			String exprMatched  = exprDirectory+"/NEW/"+topicName+"_match.txt";
			String exprMissed  = exprDirectory+"/NEW/"+topicName+"_miss.txt";
			String exprMatchedConflict = exprDirectory+"/NEW/"+topicName+"_match_dup.txt";
			String exprMissedConflict  = exprDirectory+"/NEW/"+topicName+"_miss_dup.txt";
			String exprOld = exprDirectory+"/OLD/"+topicName;

			File file1 = new File(standAloneDirectory);
			File file2 = new File(dataLocation);
			File file3 = new File(clueLocation);
			File file4 = new File(dictDirectory+"/stopword.txt");
			File file5 = new File(dictDirectory+"/GENERAL.txt");
			File file6 = new File(dictDirectory+"/synonym.txt");

			if(!file1.exists()||!file2.exists()||!file3.exists()||!file4.exists()||!file5.exists()||!file6.exists())
			{
				System.out.println("[ERROR] Directory and Dictionary File is not exits");
			}
			else
			{
				//encoding 확인
				if(encoding.equals("UTF-8")||encoding.equals("utf-8")||encoding.equals("MS949")||encoding.equals("ms949")){
					encoding = "UTF-8";
				}else{
					System.out.println("Wrong Encording!  Set default Encoding -> UTF-8");
				}

				//output 디렉터리 확인
				file1 = new File(topicDirectory);
				file2 = new File(attrDirectory);
				file3 = new File(exprDirectory);
				file4 = new File(exprDirectory+"/NEW");
				file5 = new File(exprDirectory+"/OLD");

				if(!file1.isDirectory())
				{
					file1.mkdir();
				}
				if(!file2.isDirectory())
				{
					file2.mkdir();
				}
				if(!file3.isDirectory())
				{
					file3.mkdir();
				}
				if(!file4.isDirectory())
				{
					file4.mkdir();
				}
				if(!file5.isDirectory())
				{
					file5.mkdir();
				}

				long start_time = System.currentTimeMillis();

				Date now = new Date();
				System.out.println("===== Knowledge Building : Start ("+now+") =====");
				System.out.println();
				System.out.println("Topic Name : "+topicName);
				System.out.println("Main Directory : "+mainDirectory);
				System.out.println("StandAlone Directory : "+standAloneDirectory);
				System.out.println("Clue Location : "+clueLocation);
				System.out.println("encoding : "+encoding);
				System.out.println();

				now = new Date();
				System.out.println("===== Knowledge Building : Aspect Extracting Start ("+now+") =====");
				System.out.println();

				AspectExtractor AE = new AspectExtractor();

				System.out.println();

				if(!kmStandAlone.CLUEJK.equals(""))
					AE.clueJKtext.addAll(Arrays.asList(kmStandAlone.CLUEJK.split(",")));

				if(!kmStandAlone.ATTRJK.equals(""))
					AE.attrJKtext.addAll(Arrays.asList(kmStandAlone.ATTRJK.split(",")));

				if(!kmStandAlone.NOUNPOS.equals(""))
					AE.clueNOUNpos.addAll(Arrays.asList(kmStandAlone.NOUNPOS.split(",")));

				//clue setting
				AE.clue.addAll(AE.setWordList(clueLocation));

				System.out.println();
				System.out.println("Clue Count : "+AE.clue.size());

				//금칙어 setting
				AE.stopwords.addAll(AE.setWordList(dictDirectory+"/stopword.txt"));
				System.out.println("Stopword Count : "+AE.stopwords.size());

				//감성사전 setting
				AE.sentimentDic.set_dict(dictDirectory+"/GENERAL.txt",  AE.nlpapi);

				File file = new File(dataLocation);
				File[] subFile;
				//입력된 input data 경로가 디렉토리인지 파일인지 체크하여 분석수행

				System.out.println(file.getName());
				AE.startAnalysis(AE, file, encoding, "true", "true");
				AE.docs.clear();
				AE.saveAttrFromClue(attrFromClue);
				AE.saveAttrFromExpr(attrFromExpr);

				now = new Date();
				System.out.println("===== Knowledge Building : Aspect Extracting End ("+now+") =====");
				System.out.println();

				now = new Date();
				System.out.println("===== Knowledge Building : Aspect-Expression Extracting Start ("+now+") =====");
				System.out.println();

				// TODO Auto-generated method stub
				AspectExpressionExtractor AEE = new AspectExpressionExtractor();

				AspectExpressionExtractor.sentimentDic.set_dict(dictDirectory+"/GENERAL.txt",AspectExpressionExtractor.nlpapi);
				AspectExpressionExtractor.sentimentDic.print_dict();

				AEE.do_synonym_mapper(dictDirectory+"/synonym.txt");
				AEE.do_represent_mapper(dictDirectory+"/RepAttr.txt");

				AEE.set_ExpressionOld(exprOld);

				System.out.println("Old Expression Count = "+AspectExpressionExtractor.mapOldExpr.size());

				//clue setting
				AspectExpressionExtractor.clue.addAll(AE.setWordList(clueLocation));

				AspectExpressionExtractor.category = topicName;

				//속성 세팅
				AspectExpressionExtractor.treeAttr.addAll(AEE.setWordList(attrFromClue));
				AspectExpressionExtractor.treeAttr.addAll(AEE.setWordList(attrFromExpr));

				System.out.println("Null Attr Count = "+AspectExpressionExtractor.clue.size());

				//속성수
				System.out.println("Attr Count = "+AspectExpressionExtractor.treeAttr.size());

				file = new File(dataLocation);
				//입력된 input data 경로가 디렉토리인지 파일인지 체크하여 분석수행
				if(file.isDirectory()){
					subFile = file.listFiles();
					for (int i = 0; i < subFile.length; i++)
					{
						System.out.println(subFile[i].toString());
						AEE.startAnalysis(AEE, subFile[i].toString(),"UTF-8");
						AspectExpressionExtractor.docs.clear();
					}
				}
				else
				{
					System.out.println(file.toString());
					AEE.startAnalysis(AEE,file.toString(),"UTF-8");
					AspectExpressionExtractor.docs.clear();
				}



				AEE.saveResultMatched(exprMatched,exprMatchedConflict, exprOld,"StandAlone");
				AEE.saveResultMissed(exprMissed,exprMissedConflict, exprOld,"StandAlone");

				now = new Date();
				System.out.println("===== Knowledge Building : Aspect-Expression Extracting End ("+now+") =====");
				System.out.println();

				System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024+"MB");
				long end_time = System.currentTimeMillis();
				System.out.println("Run time : "+UtilTimer.timeDiff(start_time,end_time));

				System.out.println();
				now = new Date();
				System.out.println("===== Knowledge Building : End ("+now+") =====");
			}
		}
	}
	/**
	 * 외부에서 지식구축 환경설정을 로딩하기 위한 클래스<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param none
	 * @exception none
	 */
	public void init() throws Exception
	{
		// TODO Auto-generated method stub
		try
		{
			UtilPropertyReader.readProperties(this, PROP_FILE_NAME);
		}catch(Exception e){
			System.err.println("Change the path and reload...");
			UtilPropertyReader.readProperties(this, MAVEN_BASE_DIR + PROP_FILE_NAME);
		}
	}
}
