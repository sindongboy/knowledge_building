package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import com.skplanet.omp.common.charset.converter.AutoCharsetConverter;
import com.skplanet.omp.knowledgeBuilding.Util.ProcessOutputThread;
import com.skplanet.omp.knowledgeBuilding.Util.ScdDupChecker;
import com.skplanet.omp.knowledgeBuilding.command.AnalysisStatus;
import com.skplanet.omp.knowledgeBuilding.command.InsertAspectExprData;
import com.skplanet.omp.knowledgeBuilding.dao.KonwledgeBuildingDAO;
import com.skplanet.nlp.utils.UtilPropertyReader;
import com.skplanet.nlp.utils.UtilTimer;

/**
 *  관리도구로 부터 요청을 받아 속성과 속성-표현후보 추출기를 실행하는 Transaction 클래스<p>
 *
 * @version  	0.1
 * @since  	2012.09.03
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	KnowledgeBuildingTransaction_Admin.java
 * @history   2012.09.03	* v0.1	클래스 최초 생성.	한영섭.<br>
 * @history   2012.11.06	* v0.1주석 업데이트.	한영섭.<br>
 * @history   2013.03.03	* v0.1 수집seed생성 모듈추가. 한영섭<br>
 * @history   2013.03.18	* v0.1주석 업데이트.	한영섭.<br>
 */
public class KnowledgeBuildingTransaction_Admin {

	/**
	 * DB 접속을 위한 KonwledgeBuildingDAO 객체
	 * @uml.property  name="dao"
	 * @uml.associationEnd
	 */
	public static KonwledgeBuildingDAO dao;

	/**
	 * 지식구축 설정 파일의 위치 
	 */

	private String 	PROP_FILE_NAME					= "config/knowledgeBuilding.properties";

	private final String	MAVEN_BASE_DIR			= "../";

	private String MAINDIRECTORY 						= "/svc/omp/knowledgeBuilding";

	private String STANDALONE_MAIN 					= "/svc/omp/knowledgeBuilding/StandAlone";

	private String DATA_LOCATION 							= "/svc/omp/knowledgeBuilding/StandAlone/data";

	private String DICT_LOCATION 							= "/svc/omp/knowledgeBuilding/StandAlone/dict";

	private String CLUE_LOCATION 							= "/svc/omp/knowledgeBuilding/StandAlone/clue/test.clue";

	private String TOPICNAME 									= "test";

	private String NOUNPOS 										= "nng,nnp,nnk,eng,xsn,nnb";

	private String CLUEJK 											= "에서,은,는,에는,이,에,의,으로";

	private String ATTRJK 											= "은,는,이,가,도,까지,면에서,측면에서,에,에도,에는,에서는,에서도";

	private String CHARSET 										= "UTF-8";


	public KnowledgeBuildingTransaction_Admin()
	{
		try
		{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 지식구축을 위한 초기 세팅<br>
	 *  - 관리도구에서 설정 내려받기<br>
	 *  - 수집 환경설정<br>
	 *  - 분석 디렉토리 생성<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param category 카테고리번호, categoryDirectory 카테고리 디렉토리, tmpDirectory 임시파일디렉토리, clueDirectory clue디렉토리, status 지식구축설정객체리스트
	 * @exception none
	 */
	public void initSystem(String category, String categoryDirectory, String tmpDirectory, String clueDirectory, AnalysisStatus status, ArrayList<String> objects) throws IOException{

		File fileCategoryDirectory = new File(categoryDirectory);

		if(!fileCategoryDirectory.isDirectory())
		{
			fileCategoryDirectory.mkdir();
		}
		else
		{
			removeDIR(fileCategoryDirectory);
			fileCategoryDirectory.mkdir();
		}

		fileCategoryDirectory = new File(categoryDirectory+"/chk");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/clue");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/inf");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/inf/ptn");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/loc");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/log");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/run");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/scd");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/ecd");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/cate");
		fileCategoryDirectory.mkdir();
		fileCategoryDirectory = new File(categoryDirectory+"/tmp");
		fileCategoryDirectory.mkdir();

		String[] siteIDs = status.getCrawlingSiteSeqs().split(",");
		String[] categoryIDs = status.getCrawlingCategoryIDs().split(",");
		String[] crawlWords = status.getCrawlwords().split(",");
		String[] clueWords = status.getCluewords().split(",");

		copyFileReal(tmpDirectory+"/inf/naver_shopping.inf",categoryDirectory+"/inf/naver_shopping.inf");
		copyFileReal(tmpDirectory+"/ptn/naver_shopping.ptn",categoryDirectory+"/inf/ptn/naver_shopping.ptn");
		copyFileReal(tmpDirectory+"/run/naver_shopping.run",categoryDirectory+"/run/naver_shopping.run");
		copyFileReal(tmpDirectory+"/seed/naver_shopping.seed",categoryDirectory+"/inf/naver_shopping.seed");

		BufferedWriter out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/site.list"),"UTF-8"));

		for(String siteID:siteIDs){
			copyFileReal(tmpDirectory+"/inf/"+siteID+".inf",categoryDirectory+"/inf/"+siteID+".inf");
			copyFileReal(tmpDirectory+"/ptn/"+siteID+".ptn",categoryDirectory+"/inf/ptn/"+siteID+".ptn");
			copyFileReal(tmpDirectory+"/seed/"+siteID+".seed",categoryDirectory+"/inf/"+siteID+".seed");

			File fileSiteRunFile = new File(tmpDirectory+"/run/"+siteID+".run");
			if(fileSiteRunFile.exists()){
				copyFileReal(tmpDirectory+"/run/"+siteID+".run",categoryDirectory+"/run/"+siteID+".run");
			}
			else
			{
				copyFileReal(tmpDirectory+"/run/base.run",categoryDirectory+"/run/"+siteID+".run");
			}

			String str = siteID+"\n";
			out.write(new String(str.getBytes("UTF-8"), "MS949"));
		}
		out.close();

		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/cate/"+category+".cate"),"UTF-8"));

		int i = 1;
		for(String categoryID:categoryIDs)
		{
			if(categoryID.equals(""))
				categoryID = "0";
			String str = category+"_"+i+" "+categoryID+"\n";
			out.write(new String(str.getBytes("UTF-8"), "MS949"));
			i++;
		}
		out.close();

		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/clue/"+category+".clue"),"UTF-8"));

		for(String crawlWord:crawlWords)
		{
			String str = crawlWord+"\n";
			out.write(str);
		}
		out.close();

		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(clueDirectory+"/"+category+".clue"),"UTF-8"));

		for(String clueWord:clueWords)
		{
			String str = clueWord+"\n";
			out.write(str);
		}
		out.close();

		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/clue/"+category+".object"),"UTF-8"));

		String searchwords[] = status.getSearchwords().split(",");

		for(String searchword:searchwords){
			out.write(searchword.trim()+"\n");
		}

		if(status.getSearchObjectDictUse().equals("true")){
			for(String object:objects)
			{
				String str = object+"\n";
				out.write(str);
			}
		}

		out.close();
	}

	/**
	 * 지식구축을 위한 수집 seed 생성<br>
	 *
	 * @since 2012.03.03
	 * @author 한영섭
	 * @param category 카테고리번호, categoryDirectory 카테고리 디렉토리, status 지식구축설정객체리스트
	 * @exception none
	 */
	public void makeSeed(String category, String categoryDirectory, AnalysisStatus status) throws IOException{

		String[] siteIDs = status.getCrawlingSiteSeqs().split(",");

		for(String siteID:siteIDs){

			String line1 = "";
			String line2 = "";
			String line3 = "";


			BufferedWriter out							= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/inf/"+category+"_"+siteID+".seed"),"UTF-8"));
			BufferedReader in 							= new BufferedReader(new FileReader(categoryDirectory+"/inf/"+siteID+".seed"));

			while((line1 = in.readLine()) != null)
			{
				if(line1.trim().equals("")) continue;
				if(status.getSearchCategoryNameUse().equals("true")){
					out.append(line1+URLEncoder.encode(status.getCategoryName())+"\n");
				}

				BufferedReader objectFile 			= new BufferedReader(new FileReader(categoryDirectory+"/clue/"+category+".object"));

				while((line2 = objectFile.readLine()) != null)
				{
					if(line2.trim().equals("")) continue;
					if(status.getSearchCategoryNameUse().equals("true")){
						out.append(line1+URLEncoder.encode(status.getCategoryName()+" "+line2)+"\n");
					}
					out.append(line1+URLEncoder.encode(line2)+"\n");

					BufferedReader categoryClue 	= new BufferedReader(new FileReader(categoryDirectory+"/clue/"+category+".clue"));

					while((line3 = categoryClue.readLine()) != null)
					{
						if(line3.trim().equals("")) continue;
						if(status.getSearchCategoryNameUse().equals("true")){
							out.append(line1+URLEncoder.encode(status.getCategoryName()+" "+line3)+"\n");
							out.append(line1+URLEncoder.encode(status.getCategoryName()+" "+line2 +" "+line3)+"\n");
						}
						out.append(line1+URLEncoder.encode(line2+" "+line3)+"\n");
					}
					categoryClue.close();
				}
				objectFile.close();
				out.flush();
			}
			in.close();
			out.close();
		}

	}

	/**
	 * 지식구축 결과를 저장한다.<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param category 카테고리번호, fileMatched 결과파일, fileMissed 결과파일, fileMatchedConfilct 결과파일, fileMissedConfilct 결과파일
	 * @exception none
	 */
	public static void saveResult(String category, String fileMatched, String fileMissed, String fileMatchedConfilct, String fileMissedConfilct) throws IOException
	{
		BufferedReader out 	= new BufferedReader(new FileReader(fileMatched));

		InsertAspectExprData aspectExprData = new InsertAspectExprData();

		dao.deleteAspectExpr(category);

		String line;
		String[] data;

		while((line = out.readLine()) != null)
		{
			data = line.split("\t");

			aspectExprData.setAttributeName(data[0]);
			aspectExprData.setExpressionName(data[1]);
			aspectExprData.setAttributeSynonym(data[2]);
			aspectExprData.setExpressionValue(data[3]);
			aspectExprData.setRepresentationId(data[4]);
			aspectExprData.setCategoryId(category);
			aspectExprData.setExpressionType("1");
			aspectExprData.setExpressionText(data[6].replaceAll("\\[\\[\\[@\\]" , "<b>").replaceAll("\\[@\\]\\]\\]" , "</b>").replaceAll("\\[\\[@\\]" , "\t:").replaceAll("\\[@\\]\\]" , ":\t"));

			dao.insertAspectExpr(aspectExprData);
		}

		out.close();

		out 									= new BufferedReader(new FileReader(fileMissed));

		while((line = out.readLine()) != null)
		{
			data = line.split("\t");

			aspectExprData.setAttributeName(data[0]);
			aspectExprData.setExpressionName(data[1]);
			aspectExprData.setAttributeSynonym(data[2]);
			aspectExprData.setExpressionValue("0");
			aspectExprData.setRepresentationId(data[3]);
			aspectExprData.setCategoryId(category);
			aspectExprData.setExpressionType("2");

			aspectExprData.setExpressionText(data[5].replaceAll("\\[\\[\\[@\\]", "<b>").replaceAll("\\[@\\]\\]\\]", "</b>").replaceAll("\\[\\[@\\]" , "\t:").replaceAll("\\[@\\]\\]" , ":\t"));

			dao.insertAspectExpr(aspectExprData);
		}

		out.close();

		out 	= new BufferedReader(new FileReader(fileMatchedConfilct));

		aspectExprData = new InsertAspectExprData();

		while((line = out.readLine()) != null)
		{
			data = line.split("\t");

			aspectExprData.setAttributeName(data[0]);
			aspectExprData.setExpressionName(data[1]);
			aspectExprData.setAttributeSynonym(data[2]);
			aspectExprData.setExpressionValue(data[3]);
			aspectExprData.setRepresentationId(data[4]);
			aspectExprData.setCategoryId(category);
			aspectExprData.setExpressionType("3");

			aspectExprData.setExpressionText(data[6].replaceAll("\\[\\[\\[@\\]", "<b>").replaceAll("\\[@\\]\\]\\]", "</b>").replaceAll("\\[\\[@\\]" , "\t:").replaceAll("\\[@\\]\\]" , ":\t"));

			dao.insertAspectExpr(aspectExprData);
		}

		out.close();

		out 									= new BufferedReader(new FileReader(fileMissedConfilct));

		while((line = out.readLine()) != null)
		{
			data = line.split("\t");

			aspectExprData.setAttributeName(data[0].trim().toUpperCase());
			aspectExprData.setExpressionName(data[1].trim().toUpperCase());
			aspectExprData.setAttributeSynonym(data[2]);
			aspectExprData.setExpressionValue("0");
			aspectExprData.setRepresentationId(data[3]);
			aspectExprData.setCategoryId(category);
			aspectExprData.setExpressionType("3");
			aspectExprData.setExpressionText(data[5].replaceAll("\\[\\[\\[@\\]", "<b>").replaceAll("\\[@\\]\\]\\]", "</b>").replaceAll("\\[\\[@\\]" , "\t:").replaceAll("\\[@\\]\\]" , ":\t").replaceAll("\\^\\^\\^\\&\\^\\^\\^", "<br/>"));

			dao.insertAspectExpr(aspectExprData);
		}

		out.close();
	}

	/**
	 * Webot을 실행하기 위해 프로그램 외부의 프로세스를 실행하는 클래스<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param fileName 실행파일명
	 * @exception none
	 */
	public static void execute(String fileName) throws IOException{
		try {
			//String[] command = {"/bin/sh","-c",fileName};

			Process process = Runtime.getRuntime().exec(fileName);

			StringBuffer stdMsg = new StringBuffer();

			// 스레드로 inputStream 버퍼 비우기
			ProcessOutputThread o = new ProcessOutputThread(process.getInputStream(), stdMsg);
			o.start();
			StringBuffer errMsg = new StringBuffer();

			// 스레드로 errorStream 버퍼 비우기
			o = new ProcessOutputThread(process.getErrorStream(), errMsg);
			o.start();

			// 수행종료시까지 대기

			process.waitFor();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 *	환경설정을 위해 과거 디렉토리 정보를 삭제 하는 클래스 <br>
	 *	하위 디렉토리까지 모두 삭제한다.<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param fileCategoryDirectory 삭제할 디렉토리명
	 * @exception none
	 */
	public static void removeDIR(File fileCategoryDirectory){
		File[] listFile 	=	fileCategoryDirectory.listFiles();
		try
		{
			if(listFile.length > 0)
			{
				for(int i = 0 ; i < listFile.length ; i++)
				{
					if(listFile[i].isFile())
					{
						listFile[i].delete();
					}else{
						removeDIR(listFile[i]);
					}
					listFile[i].delete();
				}
			}

		}catch(Exception e){
			//System.err.println(System.err);
		}
	}

	/**
	 *	환경설정을 위해 대상파일을 복사하는 클래스 <br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param fileIn 입력파일, fileOut 출력파일
	 * @exception none
	 */
	public static boolean copyFileReal(String fileIn, String fileOut){
		boolean result = false;
		try{
			BufferedInputStream in = null;
			OutputStream out = null;

			in = new BufferedInputStream(new FileInputStream(new File(fileIn))); //원본파일
			out = new FileOutputStream(fileOut); //수정파일(저장이름) 

			int s1=-1;
			while((s1=in.read())!=-1)
			{
				out.write(s1);
			}

			in.close();
			out.close();

			result = true;
		}catch (Exception  e) {
			System.out.println(" Error occurred: " + e.getMessage());
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 관리도구와 연동하여 속성 후보와 속성-표현 후보를 추출하는 main()<br>
	 * - 1분 간격으로 지식구축 요청을 확인하여 설정정보와 함께 지식구축을 수행한다.<br>
	 *
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param args
	 * @exception none
	 */
	public static void main(String[] args) throws Exception {
		KnowledgeBuildingTransaction_Admin kmStandAdmin = new KnowledgeBuildingTransaction_Admin();

		String baseDirectory					= kmStandAdmin.MAINDIRECTORY;

		while(true){
			ArrayList<AnalysisStatus> analysisStatus 	= new ArrayList<AnalysisStatus>();
			dao																			= new KonwledgeBuildingDAO();

			int analysisCount = 0;

			analysisCount = (Integer)dao.getCountAnalysisStatus();

			if(analysisCount > 0)
			{
				analysisStatus.addAll(dao.selectAnalysisStatus());

				for(AnalysisStatus status:analysisStatus)
				{
					long start_time									= System.currentTimeMillis();

					Date now = new Date();
					System.out.println("===== Knowledge Building : Start ("+now+") =====");
					System.out.println();
					System.out.println("Category ID : "+status.getCategoryId());
					System.out.println("Crawl Sites : "+status.getCrawlingSiteSeqs());
					System.out.println("Search - Keywords : "+status.getSearchwords());
					System.out.println("Search - Sub Keywords : "+status.getCrawlwords());
					System.out.println("Search - Use Object Dictionary : "+status.getSearchObjectDictUse());
					System.out.println("Search - Use Category Name : "+status.getSearchCategoryNameUse());
					System.out.println("Search - Use Naver Shopping Object : "+status.getNaverShopping());
					System.out.println("Naver Shopping Category IDs : "+status.getCrawlingCategoryIDs());
					System.out.println("Extract Aspect From Clues : "+status.getExtractAspectFromClue());
					System.out.println("Extract Aspect From General Expressions : "+status.getExtractAspectFromExpr());
					System.out.println("Aspect Clues : "+status.getCluewords());
					System.out.println();

					String category 									= status.getCategoryId();

					String crawlDirectory 					= baseDirectory +"/crawl/kor";
					String categoryDirectory 				= crawlDirectory +"/"+category;
					String dataDirectory 						= categoryDirectory+"/chk";
					String scdDirectory 							= categoryDirectory+"/scd";
					String encodeDirectory 				= categoryDirectory+"/ecd";

					String resourceDirectory				= baseDirectory+"/resource";

					String dictDirectory							= resourceDirectory+"/dict";
					String clueDirectory 						= resourceDirectory +"/clue";

					String attrDirectoryFromClue		= resourceDirectory+"/attr/"+category+"_clue.txt";
					String attrDirectoryFromExpr 	= resourceDirectory+"/attr/"+category+"_expr.txt";

					String fileMatched 							= resourceDirectory+"/expr/new/"+category+".txt";
					String fileMissed 								= resourceDirectory+"/expr/new/"+category+"_missed.txt";
					String fileMatchedConflict 			= resourceDirectory+"/expr/new/"+category+"_conflict.txt";
					String fileMissedConflict 				= resourceDirectory+"/expr/new/"+category+"_missed_conflict.txt";
					String fileExprOld							= resourceDirectory+"/expr/old/"+category;

					String tmpDirectory 						= baseDirectory +"/tmp";

					ArrayList<String> objects  			= new ArrayList<String>();
					ScdDupChecker sdc							= new ScdDupChecker();
					File file													= null;
					File[] subFile										= null;

					objects.addAll(dao.selectCrawlObjects(category));

					kmStandAdmin.initSystem(category, categoryDirectory, tmpDirectory, clueDirectory, status, objects);

					now = new Date();
					System.out.println("===== Knowledge Building : Crawling Start ("+now+") =====");
					System.out.println();
					try{
						execute(baseDirectory+"/bin/crawl_init.sh "+category+" kor");

						if(status.getNaverShopping().equals("true")){
							execute(baseDirectory+"/bin/crawl_transaction_naverShopping.sh "+category+" kor");

							file = new File(scdDirectory);
							subFile = file.listFiles();
							for (int i = 0; i < subFile.length; i++) {
								AutoCharsetConverter.convertEncode(scdDirectory+"/"+subFile[i].getName().toString(),encodeDirectory+"/"+subFile[i].getName().toString());
								execute("rm "+scdDirectory+"/"+subFile[i].getName());
								sdc.get_naverShopping_object(encodeDirectory+"/"+subFile[i].getName().toString(), categoryDirectory+"/clue/"+category+".object");
							}
						}

						kmStandAdmin.makeSeed(category, categoryDirectory, status);

						execute(baseDirectory+"/bin/crawl_transaction.sh "+category+" kor");

					}
					catch(Exception e)
					{
						dao.updateAnalysisStatus(status.getCategoryId(), "-1");
						continue;
					}
					now = new Date();
					System.out.println("===== Knowledge Building : Crawling End ("+now+") =====");
					System.out.println();

					now = new Date();
					System.out.println("===== Knowledge Building : SCD Parsing Start ("+now+") =====");
					System.out.println();

					try{
						file = new File(scdDirectory);
						subFile = file.listFiles();
						for (int i = 0; i < subFile.length; i++) {
							AutoCharsetConverter.convertEncode(scdDirectory+"/"+subFile[i].getName().toString(), encodeDirectory+"/"+subFile[i].getName().toString());
							execute("rm "+dataDirectory+"/"+scdDirectory+"/"+subFile[i].getName().toString());
						}

						file = new File(encodeDirectory);
						subFile = file.listFiles();
						for (int i = 0; i < subFile.length; i++) {
							sdc.do_dup_check(subFile[i].getName(),encodeDirectory+"/",dataDirectory+"/");
						}
					}
					catch(Exception e)
					{
						dao.updateAnalysisStatus(status.getCategoryId(), "-1");
						continue;
					}

					now = new Date();
					System.out.println("===== Knowledge Building : SCD Parsing End ("+now+") =====");
					System.out.println();


					now = new Date();
					System.out.println("===== Knowledge Building : Aspect Extracting Start ("+now+") =====");
					System.out.println();

					AspectExtractor AE				= new AspectExtractor();

					try{

						dao.updateAnalysisStatus(status.getCategoryId(), "3");

						System.out.println();

						if(!kmStandAdmin.CLUEJK.equals(""))
							AE.clueJKtext.addAll(Arrays.asList(kmStandAdmin.CLUEJK.split(",")));

						if(!kmStandAdmin.ATTRJK.equals(""))
							AE.attrJKtext.addAll(Arrays.asList(kmStandAdmin.ATTRJK.split(",")));

						if(!kmStandAdmin.NOUNPOS.equals(""))
							AE.clueNOUNpos.addAll(Arrays.asList(kmStandAdmin.NOUNPOS.split(",")));

						//clue setting
						AE.clue.addAll(AE.setWordList(clueDirectory+"/"+category+".clue"));

						//금칙어 setting
						AE.stopwords.addAll(AE.setWordList(dictDirectory+"/stopword.txt"));

						//감성사전 setting
						AE.sentimentDic.set_dict(dictDirectory+"/GENERAL.txt", 	AE.nlpapi);

						file = new File(dataDirectory);
						System.out.println(file.getName());
						AE.startAnalysis(AE, file,"UTF-8", status.getExtractAspectFromClue(), status.getExtractAspectFromExpr());
						AE.docs.clear();

						AE.saveAttrFromClue(attrDirectoryFromClue);
						AE.saveAttrFromExpr(attrDirectoryFromExpr);
					}
					catch(Exception e)
					{
						dao.updateAnalysisStatus(status.getCategoryId(),"-1");
						continue;
					}

					now = new Date();
					System.out.println("===== Knowledge Building : Aspect Extracting End ("+now+") =====");
					System.out.println();

					now = new Date();
					System.out.println("===== Knowledge Building : Aspect-Expression Extracting Start ("+now+") =====");
					System.out.println();

					try{

						dao.updateAnalysisStatus(status.getCategoryId(), "4");
						// TODO Auto-generated method stub
						AspectExpressionExtractor AEE		= new AspectExpressionExtractor();

						if(!kmStandAdmin.ATTRJK.equals(""))
							AspectExpressionExtractor.attrJKtext.addAll(Arrays.asList(kmStandAdmin.ATTRJK.split(",")));

						if(!kmStandAdmin.NOUNPOS.equals(""))
							AspectExpressionExtractor.attrNOUNpos.addAll(Arrays.asList(kmStandAdmin.NOUNPOS.split(",")));

						AspectExpressionExtractor.sentimentDic.set_dict(dictDirectory+"/GENERAL.txt",AspectExpressionExtractor.nlpapi);
						AspectExpressionExtractor.sentimentDic.print_dict();

						try{
							AEE.do_synonym_mapper(dictDirectory+"/synonym.txt");
							AEE.do_represent_mapper(dictDirectory+"/RepAttr.txt");
						}catch(Exception e){e.printStackTrace();}

						try{
							AEE.set_ExpressionOld(fileExprOld);
						} catch(Exception e){e.printStackTrace();}

						System.out.println("Representation Attributes Count = "+AspectExpressionExtractor.mapRepAttr.size());
						System.out.println("Old Expression Count = "+AspectExpressionExtractor.mapOldExpr.size());

						//clue setting
						AspectExpressionExtractor.clue.addAll(AE.setWordList(clueDirectory+"/"+category+".clue"));

						AspectExpressionExtractor.category = category;

						//속성 세팅
						if(status.getExtractAspectFromClue().equals("true"))
						{
							AspectExpressionExtractor.treeAttr.addAll(AEE.setWordList(attrDirectoryFromClue));
						}
						if(status.getExtractAspectFromExpr().equals("true"))
						{
							AspectExpressionExtractor.treeAttr.addAll(AEE.setWordList(attrDirectoryFromExpr));
						}

						System.out.println("Null Attr Count = "+AspectExpressionExtractor.clue.size());

						//속성수
						System.out.println("Attr Count = "+AspectExpressionExtractor.treeAttr.size());

						file = new File(dataDirectory);
						subFile = file.listFiles();
						for (int i = 0; i < subFile.length; i++)
						{
							System.out.println(subFile[i].toString());
							AEE.startAnalysis(AEE, subFile[i].toString(),"UTF-8");
							AspectExpressionExtractor.docs.clear();
						}

						AEE.saveResultMatched(fileMatched,fileMatchedConflict, fileExprOld,"Admin");
						AEE.saveResultMissed(fileMissed,fileMissedConflict, fileExprOld,"Admin");

					}
					catch(Exception e)
					{
						dao.updateAnalysisStatus(status.getCategoryId(), "-1");
						continue;
					}

					dao.updateAnalysisStatus(status.getCategoryId(), "6");

					try{

						saveResult(category, fileMatched, fileMissed, fileMatchedConflict, fileMissedConflict);
					}
					catch(Exception e)
					{
						dao.updateAnalysisStatus(status.getCategoryId(), "-1");
						continue;
					}

					dao.updateAnalysisStatus(status.getCategoryId(), "5");

					now = new Date();
					System.out.println("===== Knowledge Building : Aspect-Expression Extracting End ("+now+") =====");
					System.out.println();

					System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024+"MB");
					long end_time								= System.currentTimeMillis();
					System.out.println("Run time : "+UtilTimer.timeDiff(start_time,end_time));

					System.out.println();
					now = new Date();
					System.out.println("===== Knowledge Building : End ("+now+") =====");
				}
			}
			Thread.sleep(120000);
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
