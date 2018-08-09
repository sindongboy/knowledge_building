package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.TreeMap;

import com.skplanet.omp.knowledgeBuilding.Database.DBManager;
import com.skplanet.omp.knowledgeBuilding.command.AnalysisStatus;
import com.skplanet.omp.knowledgeBuilding.dao.KonwledgeBuildingDAO;
import com.skplanet.nlp.speller.ngram.Jaso;
import com.skplanet.nlp.utils.UtilPropertyReader;
import com.skplanet.nlp.utils.UtilTimer;

/**
 * 공통표현추출기 - 구축된 감성지식을 이용하여 유사한 속성-표현을 상위사전으로 추천한다<P>
 * 
 * @version  	0.2
 * @since  	2012.12.03
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	CommonExprExtractor.java
 * @history   2012.12.03	* v0.1	클래스 최초 생성.	한영섭.<br>
 * @history   2013.03.18	* v0.2주석 업데이트.	한영섭.<br>
 */
public class CommonExprExtractor {

	private String 	PROP_FILE_NAME					= "config/knowledgeBuilding.properties";

	private final String	MAVEN_BASE_DIR			= "../";

	private String MAINDIRECTORY 						= "D:/knowledgeBuilding/dict";

	private String STANDALONE_MAIN 					= "/svc/omp/knowledgeBuilding/StandAlone";
	
	private String DICT_LOCATION   						="/svc/omp/knowledgeBuilding/resource/dict";
	
	private String DATA_LOCATION 							= "/svc/omp/knowledgeBuilding/StandAlone/data";
	
	private String CLUE_LOCATION 							= "/svc/omp/knowledgeBuilding/StandAlone/clue/test.clue";
	
	private String TOPICNAME 									= "test";
	
	private String NOUNPOS 										= "nng,nnp,nnk,eng,xsn,nnb";

	private String CLUEJK 											= "에서,은,는,에는,이,에,의,으로";

	private String ATTRJK 											= "은,는,이,가,도,까지,면에서,측면에서,에,에도,에는,에서는,에서도";
	
	private String CHARSET 										= "UTF-8";
	
	/**
	 *  대표속성 목록 TreeMap
	 */
	public static TreeMap<String, String> mapRepAttr;
	
	public CommonExprExtractor()
	{
		try
		{
			init();
		}catch(Exception e){
			e.printStackTrace();
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

	//edit distance를 구한다(levenshtein)
	public static int getEditDistance(String str1, String str2)   
	{   
		int strLen1 = str1.length();   
		int strLen2 = str2.length();   

		int[][] dist = new int[strLen1+1][strLen2+1];   

		for(int i=0 ; i <= strLen1 ; i++)    dist[i][0] = i;   
		for(int i=0 ; i <= strLen2 ; i++)    dist[0][i] = i;  

		for(int i=1 ; i <= strLen1 ; i++)  
		{  
			for(int j=1 ; j <= strLen2 ; j++)  
			{  
				if(str1.charAt(i-1) == str2.charAt(j-1))  
					dist[i][j] = Math.min(dist[i-1][j-1], Math.min(dist[i-1][j]+1, dist[i][j-1]+1));  
				else  
					dist[i][j] = 1 + Math.min(dist[i-1][j-1], Math.min(dist[i-1][j], dist[i][j-1]));  
			}  
		}  
		return dist[strLen1][strLen2];  
	}

	/**
	 * 대표속성 초기화<br>
	 *	- 분석시 대표속성을 처리하기 위해 대표속성 사전을 세팅한다.<br>
	 *  
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param fileDict 사전파일위치
	 * @exception none
	 */
	public void do_represent_mapper(String fileDict) throws IOException
	{	
		BufferedReader in			= new BufferedReader(new FileReader(fileDict)); 
		
		mapRepAttr 						= new TreeMap< String , String >( );
		
		String line;
		String tabArray[] = null;
	
		
		while((line = in.readLine()) != null)
		{
			tabArray 		= line.split("\t");
			
			if(mapRepAttr.containsKey(tabArray[0]+"\t"+tabArray[1])){
				String repTemp = mapRepAttr.get(tabArray[0]+"\t"+tabArray[1]);
				if(repTemp.equals(tabArray[2])) continue;
				repTemp = repTemp +","+  tabArray[2];
				mapRepAttr.remove(tabArray[0]+"\t"+tabArray[1]);
				mapRepAttr.put(tabArray[0]+"\t"+tabArray[1], repTemp);
				repTemp = "";
			}
			else
			{
				mapRepAttr.put(tabArray[0]+"\t"+tabArray[1], tabArray[2]);
			}
		}		
		in.close();		
	}
	
	/**
	 * 공통표현추출<br>
	 *	1. 선택한 카테고리와 하위 모든 카테고리를 포함하는 구축된 감성지식을 로딩한다.<br>
	 * 2. 동일 레벨 카테고리 별로 유사도를 비교하여 2/3이상에 존재하는 표현의 경우 상위 카테고리 표현을 추출한다..<br>
	 * 3. 추출된 결과를 DB에 저장한다.<br>
	 *  
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param none
	 * @exception none
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
			CommonExprExtractor commonExpr = new CommonExprExtractor();
	
			commonExpr.do_represent_mapper(commonExpr.MAINDIRECTORY+"/resource/dict/RepAttr.txt");
			
			while(true){
				DBManager dbm = new DBManager();
	
				try{
					ArrayList<String> analysisStatus = new ArrayList<String>();
					
					dbm.selectAll("SELECT CategoryId FROM kmbuildingstatus WHERE StatusType = 7","CategoryId", analysisStatus);
					
					if(analysisStatus.size() > 0)
					{
						for(String status:analysisStatus)
						{				
							long start_time									= System.currentTimeMillis();
				
							Date now = new Date();					  
							System.out.println("===== Knowledge Building : Start ("+now+") =====");
								
							System.out.println("작업 요청 확인!!");
							dbm.insert("UPDATE kmbuildingstatus SET StatusType = 8 WHERE StatusType = 7 AND CategoryId = '"+status+"'");
							
							commonExpr.init();
							
							TreeMap<Integer, HashMap<String,ArrayList<String>>> mapLevel = new TreeMap<Integer, HashMap<String,ArrayList<String>>>();
							HashMap<String,ArrayList<String>> mapElement = new HashMap<String,ArrayList<String>>();
							HashMap<String,ArrayList<String>> mapElementTemp = new HashMap<String,ArrayList<String>>();
							TreeMap<String, HashMap<String,HashSet<String>>> mapCateAttrExpr = new TreeMap<String, HashMap<String,HashSet<String>>>();
							HashMap<String,HashSet<String>> mapAttrExpr1 = new HashMap<String,HashSet<String>>();
							HashMap<String,HashSet<String>> mapAttrExpr2 = new HashMap<String,HashSet<String>>();
							HashMap<String,HashSet<String>> mapAttrExpr3 = new HashMap<String,HashSet<String>>();
							HashMap<String,String> mapExprValue = new HashMap<String,String>();
							HashSet<String> exprs = new HashSet<String>();
							HashSet<String> exprBs = new HashSet<String>();
							ArrayList<String> CategoryIDs = new ArrayList<String>();
							//BufferedWriter	out			= new BufferedWriter(new FileWriter(commonExpr.MAINDIRECTORY+"commonAEs2.txt"));
							
							String patthern = "####.##";
							DecimalFormat decimalformat = new DecimalFormat(patthern);
							
							String query = "";
							int flag = 0;
							int categoryLevel = 10001;
							
							System.out.println("Loading Category Information...");
							//카테고리 0 부터 하위 카테고리 탐색하여 카테고리 번호를 등록한다.
							while(flag == 0)
							{					
								if(categoryLevel == 10001){	
									query = "SELECT CategoryId FROM kmcategory where ParentCategoryId = '"+status+"'";				
									dbm.selectAll(query, "CategoryId", CategoryIDs);
									mapElementTemp.put(status,CategoryIDs);						
									mapLevel.put(categoryLevel, mapElementTemp);
									mapElementTemp = new HashMap<String,ArrayList<String>>();
									CategoryIDs = new ArrayList<String>();
								}
								else
								{
									flag = 1;
									mapElement = mapLevel.get(categoryLevel-1);
									
									for(Entry<String, ArrayList<String>> entry:mapElement.entrySet())
									{
										for(String cateID :entry.getValue())
										{						
											query = "SELECT CategoryId FROM kmcategory where ParentCategoryId = "+cateID;
														dbm.selectAll(query, "CategoryId", CategoryIDs);
														if(CategoryIDs.size() > 0){	
												flag = 0;
															if(!mapLevel.containsKey(categoryLevel))
												{
													mapElementTemp.put(cateID,CategoryIDs);
													mapLevel.put(categoryLevel, mapElementTemp);
												}
												else
												{
													mapElementTemp = mapLevel.get(categoryLevel);
													//mapLevel.remove(categoryLevel);
													mapElementTemp.put(cateID,CategoryIDs);
													mapLevel.put(categoryLevel, mapElementTemp);
												}
											}
											mapElementTemp = new HashMap<String,ArrayList<String>>();
											CategoryIDs = new ArrayList<String>();
										}
									}
									mapElement = new HashMap<String,ArrayList<String>>();
								}
								categoryLevel++;
							}
						
									//하위 레벨의 카테고리 부터 탐색
							System.out.println("Extraction Attribute-Expressions...");
							for(int mapLevelKey:mapLevel.descendingKeySet()){				
								mapElement = mapLevel.get(mapLevelKey);
						
								for(Entry<String, ArrayList<String>> entry:mapElement.entrySet()){
								
									query = "select concat(AttributeName, ExpressionName) as attrexpr from kmopinionattributes a join kmopinionexpressions b on a.AttributeId = b.AttributeId where a.CategoryId = "+entry.getKey();
									ArrayList<String> retParentValues = new ArrayList<String>();
									dbm.selectAll(query, "attrexpr", retParentValues);		
								
									if(entry.getValue().size() > 1){
										ArrayList<String> cates = new ArrayList<String>();
										ArrayList<String[]> retValues = new ArrayList<String[]>();
										cates = entry.getValue();				
										
										//동일 레벨 카테고리 번호를 이용하여 db에서 속성 표현을 읽어온다
										for(String subCateID:cates){
											retValues = new ArrayList<String[]>();
											query = "select AttributeName, ExpressionName, ExpressionValue from kmopinionattributes a join kmopinionexpressions b on a.AttributeId = b.AttributeId where a.CategoryId = "+subCateID;
											dbm.selectAllMult(query, 3, retValues);					
											
											for(String[] rows:retValues){
											
												//동일 레벨의 전체 속성 표현
												if(!mapAttrExpr1.containsKey(rows[0])){
													exprs.add(rows[1]);
													mapAttrExpr1.put(rows[0],exprs);
												}
												else
												{
													exprs.addAll(mapAttrExpr1.get(rows[0]));
													//mapAttrExpr1.remove(rows[0]);
													exprs.add(rows[1]);					
													mapAttrExpr1.put(rows[0],exprs);
												}
											
												exprs = new HashSet<String>();
												//동일 레벨의 카테고리별 속성 표현
												if(!mapAttrExpr2.containsKey(rows[0])){
													exprs.add(rows[1]);
													mapAttrExpr2.put(rows[0],exprs);
												}
												else
												{
													exprs.addAll(mapAttrExpr2.get(rows[0]));
													//mapAttrExpr2.remove(rows[0]);
													exprs.add(rows[1]);
													mapAttrExpr2.put(rows[0],exprs);
												}
												mapExprValue.put(subCateID+rows[0]+rows[1], rows[2]);
												exprs = new HashSet<String>();
											}								
											mapCateAttrExpr.put(subCateID, mapAttrExpr2);
											mapAttrExpr2 = new HashMap<String,HashSet<String>>();
										}
										for(Entry<String, HashSet<String>> b:mapAttrExpr1.entrySet()){
											String attr = b.getKey();
											for(String exprA:b.getValue())
											{
												int exprCnt = 0;
												String exprTemp = "";
												String exprValue = "";
											
												for(Entry<String, HashMap<String, HashSet<String>>> a:mapCateAttrExpr.entrySet()){
													String currentCateNum = a.getKey();
													mapAttrExpr3.putAll(a.getValue());
													if(mapAttrExpr3.containsKey(attr))
													{
														exprBs.addAll(mapAttrExpr3.get(attr));
														for(String exprB:exprBs)
														{
															if(exprB.equals(exprA))
															{
																exprCnt++;
																if(exprValue.equals("")) {
																	exprValue = mapExprValue.get(currentCateNum+attr+exprB);
																}else if(!exprValue.equals("C")){
																	if(!exprValue.equals(mapExprValue.get(currentCateNum+attr+exprB)))
																	{
																		exprValue = "C";
																	}
																}
																if(!exprTemp.equals("")) exprTemp = exprTemp + ", ";													
																exprTemp = exprTemp + exprB+"("+mapExprValue.get(currentCateNum+attr+exprB)+", 1.0, "+currentCateNum+")";
																break;
															}
															else
															{
																//자소분리
																String jasoA = Jaso.decompose(exprA.replaceAll(" ", ""));
																String jasoB = Jaso.decompose(exprB.replaceAll(" ", ""));
			
																int distance = 0;
			
																//edit distance(levenshtein)
																if(jasoA.length() >= jasoB.length())
																{												
																	distance = getEditDistance(jasoA,jasoB);
																}
																else
																{
																	distance = getEditDistance(jasoB, jasoA);
																}
			
																//distance를 통한 유사도 구하기
																double result = (double)(Math.min(jasoA.length(), jasoB.length()) - distance) / Math.min(jasoA.length(), jasoB.length());
			
																//유사도가 0.65 이상이면 상위사전 후보등록(exprCnt++)
																if(result >= 0.65)
																{
																	exprCnt++;
																	
																	if(exprValue.equals("")) {
																		exprValue = mapExprValue.get(currentCateNum+attr+exprB);
																	}else if(!exprValue.equals("C")){
																		if(!exprValue.equals(mapExprValue.get(currentCateNum+attr+exprB)))
																		{
																			exprValue = "C";
																		}
																	}
																	
																	if(!exprTemp.equals("")) exprTemp = exprTemp + ", ";
																	exprTemp = exprTemp + exprB+"("+mapExprValue.get(currentCateNum+attr+exprB)+", "+decimalformat.format(result)+", "+currentCateNum+")";
																	break;
																}
															}						
														}								
														exprBs = new HashSet<String>();
														mapAttrExpr3 = new HashMap<String,HashSet<String>>();
													}
													mapAttrExpr3 = new HashMap<String,HashSet<String>>();
												}
												//유사도가 0.65 이상인 표현이 등장한 카테고리가 0.66(2/3) 이상인 경우 상위사전으로 추천(파일에 입력)
												if((double)((double)exprCnt/(double)entry.getValue().size()) > (double)0.66)
												{
													if(!retParentValues.contains(b.getKey()+exprA)){
														
														int dup = Integer.parseInt(dbm.selectOne("select count(*) as cnt from kmbuildingcommonexpression where CategoryId = '"+entry.getKey()+"' and AttributeName = '"+b.getKey()+"' and ExpressionName ='"+exprA+"'", "cnt"));

														String representationId = "1";
														
														if(mapRepAttr.containsKey(b.getKey()+"\t"+exprA)){
															
															String reqName = mapRepAttr.get(b.getKey()+"\t"+exprA);
															
															representationId = dbm.selectOne("select RepresentationId from kmrepresentationattribute where RepresentationName = '"+reqName+"'", "RepresentationId");
														}
														
														if(dup == 0){
															dbm.insert("INSERT INTO kmbuildingcommonexpression"+
																				"(CategoryId, AttributeName, ExpressionName,ExpressionText, ExpressionValue, RepresentationId, BuildCategoryId)"+
																				"VALUES ('"+entry.getKey()+"','"+b.getKey()+"','"+exprA+"','"+exprTemp+"','"+exprValue+"','"+representationId+"', '"+status+"')");
														}
													}
												}
												exprTemp = "";
												exprCnt = 0;
											}
										}
										mapCateAttrExpr = new TreeMap<String, HashMap<String,HashSet<String>>>();
										mapAttrExpr1 = new HashMap<String,HashSet<String>>();
									}
									mapExprValue = new HashMap<String,String>();
								}
								mapElement = new HashMap<String,ArrayList<String>>();
							}
							
							//out.close();
							dbm.insert("UPDATE kmbuildingstatus SET StatusType = 9 WHERE CategoryId = '"+status+"' AND StatusType = 8");
							
							now = new Date();		
							System.out.println("===== Knowledge Building : Common Expression Extracting End ("+now+") =====");				
							System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024+"MB");
							long end_time								= System.currentTimeMillis();		
							System.out.println("Run time : "+UtilTimer.timeDiff(start_time,end_time));
							System.out.println("=====================================================================================");
							
						}						
					}
				}
				catch(Exception e)
				{
					dbm.insert("UPDATE kmbuildingstatus SET StatusType = 8 WHERE StatusType = 7 AND CategoryId = '-1'");
					continue;
				}				
				dbm.close();
				Thread.sleep(60000);
			}		
	}
}
