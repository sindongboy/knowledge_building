package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.skplanet.omp.common.charset.converter.AutoCharsetConverter;
import com.skplanet.omp.knowledgeBuilding.Util.ProcessOutputThread;
import com.skplanet.omp.knowledgeBuilding.Util.ScdDupChecker;
import com.skplanet.omp.knowledgeBuilding.command.AnalysisStatus;
import com.skplanet.omp.knowledgeBuilding.command.ExprData;
import com.skplanet.omp.knowledgeBuilding.command.InsertAspectExprData;
import com.skplanet.omp.knowledgeBuilding.dao.EngKonwledgeBuildingDAO;
import com.skplanet.nlp.utils.UtilPropertyReader;
import com.skplanet.nlp.utils.UtilTimer;

/**
 *  영문 관리도구로 부터 요청을 받아 속성과 속성-표현후보 추출기를 실행하는 영문 지식구축 Transaction 클래스<p>
 * 
 * @version  	0.1
 * @since  	2012.09.03
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	KnowledgeBuildingTransaction_Admin.java
 * @history   2012.09.03	* v0.1	클래스 최초 생성.	한영섭.<br>
 * @history   2012.11.06	* v0.1주석 업데이트.	한영섭.<br>
 */
public class EngKnowledgeBuildingTransaction_Admin {

	/**
	 * DB 접속을 위한 KonwledgeBuildingDAO 객체
	 * @uml.property  name="dao"
	 * @uml.associationEnd  
	 */
	public static EngKonwledgeBuildingDAO dao;

	/**
	 * 지식구축 설정 파일의 위치 
	 */
		
	private 		String		PROP_FILE_NAME			= "config/engKnowledgeBuilding.properties";
	
	private final 	String		MAVEN_BASE_DIR			= "../";
	
	private 		String 		MAINDIRECTORY 			= "/svc/omp/knowledgeBuilding";
	
	private 		String 		STANDALONE_MAIN 		= "/svc/omp/knowledgeBuilding/StandAlone";
	
	private 		String 		DATA_LOCATION 			= "/svc/omp/knowledgeBuilding/StandAlone/data";
	
	private 		String 		DICT_LOCATION   		= "/svc/omp/knowledgeBuilding/resource/dict";
	
	private 		String 		CLUE_LOCATION 			= "/svc/omp/knowledgeBuilding/StandAlone/clue/test.clue";
	
	private 		String 		TOPICNAME 				= "test";
	
	private 		String 		CHARSET 				= "UTF-8";
	
	private 		String		HUNGRY_ID				= "9020";						
	
	
	public EngKnowledgeBuildingTransaction_Admin()
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
	 * @since 2013.03.03
	 * @author 한영섭
	 * @param category 카테고리번호, categoryDirectory 카테고리 디렉토리, tmpDirectory 임시파일디렉토리, clueDirectory clue디렉토리, status 지식구축설정객체리스트
	 * @exception none
	 */
	public void initSystem(String category, String categoryDirectory, String tmpDirectory, AnalysisStatus status, ArrayList<String> objects) throws IOException{
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
		fileCategoryDirectory = new File(categoryDirectory+"/morph");
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

		/* 수집 사이트 ID 리스트 */
		String[] siteIDs = status.getCrawlingSiteSeqs().split(",");
		/* 네이버 쇼핑 카테고리 ID 리스트 */
		String[] categoryIDs = status.getCrawlingCategoryIDs().split(",");
		/* 보조 검색 키워드 리스트 */
		String[] crawlWords = status.getCrawlwords().split(",");

		/* 사이트 ID리스트가 담길 파일 */
		BufferedWriter out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/site.list"),"UTF-8"));

		/* 사이트ID 리스트의 개수만큼 순회 */
		for(String siteID:siteIDs){
			copyFileReal(tmpDirectory+"/inf/"+siteID+".inf",categoryDirectory+"/inf/"+siteID+".inf");
			copyFileReal(tmpDirectory+"/ptn/"+siteID+".ptn",categoryDirectory+"/inf/ptn/"+siteID+".ptn");
			copyFileReal(tmpDirectory+"/seed/"+siteID+".seed",categoryDirectory+"/inf/"+siteID+".seed");

			File fileSiteRunFile = new File(tmpDirectory+"/run/"+siteID+".run");
			
			/* 해당 사이트의 run 파일이 존재 한다면 그대로 복사 */
			if(fileSiteRunFile.exists()){
				copyFileReal(tmpDirectory+"/run/"+siteID+".run",categoryDirectory+"/run/"+siteID+".run");				
			}
			/* 해당 사이트의 run 파일이 존재 하지 않는다면 base.run 파일을 카테고리디렉토리 내로 복사 */
			else
			{
				copyFileReal(tmpDirectory+"/run/base.run",categoryDirectory+"/run/"+siteID+".run");
			}	
			
			/* site.list 파일에 사이트ID를 라인단위로 출력 */
			String str = siteID+"\n";
			out.write(new String(str.getBytes("UTF-8"), "MS949"));	
		}
		out.close();	

		/* 네이버 쇼핑 카테고리 ID를 저장할 파일 */
		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/cate/"+category+".cate"),"UTF-8"));

		int i = 1;
		/* 네이버 쇼핑 카테고리 ID리스트를 순회하며 .cate 파일에 저장 */
		for(String categoryID:categoryIDs)
		{
			if(categoryID.equals(""))			
				categoryID = "0";
			String str = category+"_"+i+" "+categoryID+"\n";
			out.write(new String(str.getBytes("UTF-8"), "MS949"));	
			i++;
		}
		
		out.close();	

		/* 보조 검색 키워드를 저장할 파일 */
		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/clue/"+category+".clue"),"UTF-8"));

		/* 보조 검색 키워드 리스트를 순회하며 .clue 파일에 라인 단위로 저장 */
		for(String crawlWord:crawlWords)
		{
			String str = crawlWord+"\n";
			out.write(str);	
		}
		out.close();	

		/* 주제어 및 검색어를 저장할 파일 */
		out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/clue/"+category+".object"),"UTF-8"));

		/* 검색어 */
		String searchwords[] = status.getSearchwords().split(",");
		
		/* 검색어 리스트를 순회하며 .object 파일에 라인 단위로 저장 */
		for(String searchword:searchwords){
			out.write(searchword.trim()+"\n");			
		}
		
		/* 주제어를 검색어로 활용할것인지 여부 */
		if(status.getSearchObjectDictUse().equals("true")){		
			/* 주제어 리스트를 순회하며 .object 파일에 라인 단위로 저장 */
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
	 * @since 2013.03.03
	 * @author 한영섭
	 * @param category 카테고리번호, categoryDirectory 카테고리 디렉토리, status 지식구축설정객체리스트
	 * @exception none
	 */
	@SuppressWarnings("deprecation")
	public void makeSeed(String category, String categoryDirectory, AnalysisStatus status) throws IOException{

		/* 수집 사이트 ID 리스트 */
		String[] 	siteIDs 	= status.getCrawlingSiteSeqs().split(",");
		String 		delimeter	= " ";
		
		for(String siteID:siteIDs){

			String line1 = "";
			String line2 = "";
			String line3 = "";		

			/* 수집 실행시 필요한 카테고리-사이트 수집 Seed 쓰기 버퍼 */
			BufferedWriter out							= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(categoryDirectory+"/inf/"+category+"_"+siteID+".seed"),"UTF-8"));
			
			/* Seed 생성을 위해 필요한 기본 사이트 Seed 읽기 버퍼 */
			BufferedReader in 							= new BufferedReader(new FileReader(categoryDirectory+"/inf/"+siteID+".seed")); 
			
			/* 사이트 Seed를 라인 단위로 순회 */
			while((line1 = in.readLine()) != null)
			{
				if(line1.trim().equals("")) 
					continue;
				
				/* 명령 상태가 카테고리 이름을 검색어로 사용하도록 설정 되어있다면 append */
				if(status.getSearchCategoryNameUse().equals("true")){		
					/* seed: [카테고리 이름] */
					out.append(line1+URLEncoder.encode(status.getCategoryName())+"\n");
				}			
				
				
				/* 수집 실행시 필요한 검색어 읽기버퍼 */
				BufferedReader objectFile 			= new BufferedReader(new FileReader(categoryDirectory+"/clue/"+category+".object")); 
				/* 오브젝트 파일을 라인 단위로 순회 */
				while((line2 = objectFile.readLine()) != null)
				{
					if(line2.trim().equals("")) 
						continue;
					
					if(status.getSearchCategoryNameUse().equals("true")){		
						/* seed: [카테고리 이름] [검색어] */
						out.append(line1+URLEncoder.encode(status.getCategoryName()+" "+line2)+"\n");
					}
					
					/* seed: [검색어] */
					out.append(line1+URLEncoder.encode(line2)+"\n");					
					
					
					/* 보조 검색어 읽기버퍼 */
					BufferedReader categoryClue 	= new BufferedReader(new FileReader(categoryDirectory+"/clue/"+category+".clue")); 
					/* 보조 검색어 파일을 라인 단위로 순회 */
					while((line3 = categoryClue.readLine()) != null)
					{
						if(line3.trim().equals(""))
							continue;
						
						if(status.getSearchCategoryNameUse().equals("true")){
							/* seed: [카테고리 이름] [보조 검색어] */
							out.append(line1+URLEncoder.encode(status.getCategoryName()+" "+line3)+"\n");
							/* seed: [카테고리 이름] [검색어] [보조 검색어] */
							out.append(line1+URLEncoder.encode(status.getCategoryName()+" "+line2 +" "+line3)+"\n");		
						}
						
						/* seed: [검색어] [ 보조 검색어] */
						out.append(line1+URLEncoder.encode(line2+" "+line3)+"\n");										
					}
					categoryClue.close();
				}
				objectFile.close();
				out.flush();
			}
			in.close();
			out.close();	
			
			System.out.println("[siteid]: " + siteID);
			if( siteID.equals(HUNGRY_ID)){
				System.out.println(">>>int's hungry");
				convertSeedForHungry(categoryDirectory+"/inf/"+category+"_"+siteID+".seed");
			}
		}	
	}
	
	/**
	 * seed를 Hungry 사이트에 맞는 수집 seed에 맞게 변경한다.
	 * @param inSeedName
	 * @param outSeedName
	 */
	public void convertSeedForHungry(String seedName){
		System.out.println("call convertSeedForHungry");
		File 	inSeedFile = new File(seedName);
		File 	outSeedFile = new File(seedName + ".tmp");
		String	seed = "";
		
		try {
			BufferedReader bufferedReader = new BufferedReader(new FileReader(inSeedFile));
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(outSeedFile));
			
			while( ( seed = bufferedReader.readLine()) != null) {
				System.out.println(">>>line 327");
				seed.replaceAll(" ", "+");
				bufferedWriter.write(seed + "/?");
				System.out.println(seed + "/?");
				bufferedWriter.newLine();
			}
			
			bufferedReader.close();
			bufferedWriter.close();
		} catch(IOException e) {
			System.out.println("error during with convert seed!");
			e.printStackTrace();
		}
		
		inSeedFile.delete();
		outSeedFile.renameTo(new File(seedName));
		
		System.out.println("done convertSeedForHungry!");
	}
	
	/**
	 * Webot을 실행하기 위해 프로그램 외부의 프로세스를 실행하는 클래스<br>
	 *  
	 * @since 2013.03.03
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
	 * @since 2013.03.03
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
	 * @since 2013.03.03
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
	 * @since 2013.03.03
	 * @author 한영섭
	 * @param args
	 * @exception none
	 */
	public static void main(String[] args) throws Exception {
		EngKnowledgeBuildingTransaction_Admin engKmAdmin = new EngKnowledgeBuildingTransaction_Admin();
		
		String baseDirectory					=	engKmAdmin.MAINDIRECTORY;
		
		while(true){
			
			ArrayList<AnalysisStatus> analysisStatus 	= new ArrayList<AnalysisStatus>();
			dao																			= new EngKonwledgeBuildingDAO();
			
			int analysisCount = 0;
			
			analysisCount = (Integer)dao.getCountAnalysisStatus();			

			/* kmbuildingsetupinfo 테이블의 상태값이 1, 2인 row가 1개 이상이면 */
			if(analysisCount > 0)	
			{
				/* 카테고리ID, 수집사이트, 검색 키워드 등의 수집 정보들을 리스트에 저장 */
				analysisStatus.addAll(dao.selectAnalysisStatus());	
				
				/* 분석 명령의 개수만큼 순회 */
				for(AnalysisStatus status:analysisStatus)	
				{					
					long start_time									= System.currentTimeMillis();

					Date now = new Date();
					System.out.println("===== Knowledge Building : Start ("+now+") =====\n");
					System.out.println("Category ID : "+status.getCategoryId());
					System.out.println("Crawl Sites : "+status.getCrawlingSiteSeqs());
					System.out.println("Search Keywords : "+status.getSearchwords());
					System.out.println("Search Sub Keywords : "+status.getCrawlwords());
					System.out.println("Search Use Object Dictionary : "+status.getSearchObjectDictUse());
					System.out.println("Extract Knowledge using Pattern 1 : "+status.getExtractPattern1());
					System.out.println("Extract Knowledge using Pattern 2 : "+status.getExtractPattern3());
					System.out.println("Extract Knowledge using Pattern 3 : "+status.getExtractPattern3());
					System.out.println();

					String category 									= status.getCategoryId();
					
					String crawlDirectory 					= baseDirectory +"/crawl/eng";					
					String categoryDirectory 				= crawlDirectory +"/"+category;
					String dataDirectory 						= categoryDirectory+"/chk";
					String scdDirectory 							= categoryDirectory+"/scd";
					String encodeDirectory 				= categoryDirectory+"/ecd";
					String morphDirectory 					= categoryDirectory+"/morph";
					String tmpDirectory 						= baseDirectory +"/tmp";
					
					/* 주제어 리스트 */
					ArrayList<String> objects  			= new ArrayList<String>(); 
					/* dup체커 */
					ScdDupChecker sdc							= new ScdDupChecker();
					
					/* 해당 카테고리의 모든 주에어 유의어를 리스트에 추가( 주제어 자신도 포함 ) */
					objects.addAll(dao.selectCrawlObjects(category));
					
					for(String object:objects)
					{
						System.out.println("[object]: " + object + "\n");
					}	
					
					/* 수집기, 분석 데이터 디렉토리 생성, 기본 환경 설정 파일 복사, 카테고리리스트 생성, 주제어리스트 생성 */ 
					engKmAdmin.initSystem(category, categoryDirectory, tmpDirectory, status, objects);		

					dao.deleteAspectExpr(category);
					
					now = new Date();		
					System.out.println("===== Knowledge Building : Crawling Start ("+now+") =====\n");
					
					File file;
					File[] subFile;
					
					try{
						/* tmp 디렉토리로의 모든 run파일을 카테고리 수집기로 복사 */
						execute(baseDirectory+"/bin/crawl_init.sh "+category+" eng");
						
						/* seed 생성 */
						engKmAdmin.makeSeed(category, categoryDirectory, status);
						
						execute(baseDirectory+"/bin/crawl_transaction.sh "+category+" eng");
	
						now = new Date();		
						System.out.println("===== Knowledge Building : Crawling End ("+now+") =====\n");
	
						now = new Date();		
						System.out.println("===== Knowledge Building : SCD Parsing Start ("+now+") =====\n");
											
						file = new File(scdDirectory);
						subFile = file.listFiles();
						
						for (int i = 0; i < subFile.length; i++) {
							AutoCharsetConverter.convertEncode(scdDirectory+"/"+subFile[i].getName().toString(), encodeDirectory+"/"+subFile[i].getName().toString());				
							execute("rm "+scdDirectory+"/"+subFile[i].getName().toString());
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
					System.out.println("===== Knowledge Building : SCD Parsing End ("+now+") =====\n");
					
					now = new Date();
					System.out.println("===== Knowledge Building : Morph Analyze Start ("+now+") =====\n");
					
					dao.updateAnalysisStatus(status.getCategoryId(), "3");
					
					try{					
						execute(baseDirectory+"/bin/engMorphAnalyzer.sh "+dataDirectory+" "+morphDirectory);
					}
					catch(Exception e)
					{
						continue;
					}
						System.out.println();
	
						now = new Date();		
						System.out.println("===== Knowledge Building : Morph Analyze End ("+now+") =====\n");
	
						now = new Date();		
						System.out.println("===== Knowledge Building : Aspect-Expression Extracting Start ("+now+") =====\n");
					try{	
						System.out.println();
						dao.updateAnalysisStatus(status.getCategoryId(), "4");
	
						EngAspectExpressionExtractor eee 							= new EngAspectExpressionExtractor();					
						EngAspectExpressionExtractor.engAttrExprMap	= new TreeMap< String , ExprData >( );
						BufferedWriter out		= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(baseDirectory+"/resource/expr/old/eng_"+category+".txt",true),"UTF-8"));
						
						eee.set_ExpressionOld(baseDirectory+"/resource/expr/old/eng_"+category+".txt");
						
						file = new File(morphDirectory);
						subFile = file.listFiles();
						for (int i = 0; i < subFile.length; i++) {
	
							eee.startAnalysis(subFile[i].toString(),status.getExtractPattern1(),status.getExtractPattern2(),status.getExtractPattern3());
							
							for(Entry< String , ExprData > entry:EngAspectExpressionExtractor.engAttrExprMap.entrySet()){
								out.append(entry.getKey()+"\t"+entry.getValue().getCount()+ "\t" +entry.getValue().getText()+"\n");
								
								InsertAspectExprData iaed = new InsertAspectExprData();
								iaed.setAttributeName(entry.getKey().split("\t")[0]);
								iaed.setCategoryId(category);
								iaed.setExpressionName(entry.getKey().split("\t")[1]);
								iaed.setExpressionText(entry.getValue().getText());
								iaed.setExpressionValue("0");
								iaed.setRepresentationId("1");
								iaed.setAttributeSynonym("");
								iaed.setExpressionType("2");
								
								if(EngAspectExpressionExtractor.mapEngOldExpr.contains(entry.getKey()))
								{
									iaed.setExpressionType("3");
								}
								else
								{
									iaed.setExpressionType("2");
								}							
								dao.insertEngAspectExpr(iaed);
							}
							out.flush();
							EngAspectExpressionExtractor.engAttrExprMap = new TreeMap< String , ExprData >( );
						}	
						out.close();
					}
					catch(Exception e)
					{
						dao.updateAnalysisStatus(status.getCategoryId(), "-1");
						continue;
					}
					
					dao.updateAnalysisStatus(status.getCategoryId(), "5");

					now = new Date();		
					System.out.println("===== Knowledge Building : Aspect-Expression Extracting End ("+now+") =====\n");

					System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024+"MB");
					long end_time								= System.currentTimeMillis();		
					System.out.println("Run time : "+UtilTimer.timeDiff(start_time,end_time));

					System.out.println();
					now = new Date();	
					System.out.println("===== Knowledge Building : End ("+now+") =====\n");
				}					
			}
			Thread.sleep(120000);
		}
	}
	
	/**
	 * 외부에서 지식구축 환경설정을 로딩하기 위한 클래스<br>
	 *   
	 * @since 2013.03.03
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
