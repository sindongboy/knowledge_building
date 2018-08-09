package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.skplanet.omp.knowledgeBuilding.command.AttrData;
import com.skplanet.omp.knowledgeBuilding.command.InsertAttrData;
import com.skplanet.omp.knowledgeBuilding.command.InsertDicData;
import com.skplanet.omp.knowledgeBuilding.dao.InsertDicDAO;
import com.skplanet.nlp.utils.UtilPropertyReader;

/** 
 * 정재된 기 분석 사전을 DB에 적재하는 클래스<br>
 * 사전 전체를 삭제하고 입력된 사전 파일을 업로드 한다.<br>
 * 
 * @version	0.1
 * @since	2012.01.16
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	KnowledgeUploaderAll.java
 * @history
 *  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class KnowledgeUploaderAll {	

	/**
	 * 지식구축 설정 파일의 위치 
	 */
	private final String 	PROP_FILE_NAME			= "config/knowledgeBuilding.properties";
	
	private final String	MAVEN_BASE_DIR			= "../";
	
	private String MAINDIRECTORY 						= "/svc/omp/knowledgeBuilding";
	
	private String STANDALONE_MAIN 					= "/svc/omp/knowledgeBuilding/StandAlone";
	
	private String TOPICNAME 									= "test";
	
	private String NOUNPOS 										= "nng,nnp,nnk,eng,xsn,nnb";

	private String CLUEJK 											= "에서,은,는,에는,이,에,의,으로";

	private String ATTRJK 											= "은,는,이,가,도,까지,면에서,측면에서,에,에도,에는,에서는,에서도";
	
	private String CHARSET 										= "UTF-8";
	
	public static HashMap<String, TreeSet<String>> mapCateNew;
	
	public static TreeMap<String, String> mapMappingIDtoCategoryID;
	
	public static TreeSet<String> attrList = new TreeSet<String>();	
	public static TreeSet<String> cateList = new TreeSet<String>();

	
	public static TreeMap<String, TreeSet<String>> mapAttrSynm;
	public static TreeMap<String, TreeSet<String>> mapAttrCate;
	
	public static TreeMap<String, String> mapAttrAll;
	
	public static TreeMap<String, String> mapRepAttr;
	

	public KnowledgeUploaderAll()
	{
		try
		{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void do_synonym_mapper(String filename) throws IOException
	{	
		BufferedReader dic = new BufferedReader(new FileReader(filename)); 
		
		String line, attr, attrOrg, synmOrg;
		String tabArray[] = null;
		String comArray[] = null;
		
		mapAttrSynm 	= new TreeMap< String , TreeSet<String> >( );
		mapAttrCate 		= new TreeMap< String , TreeSet<String> >( );
		
		mapAttrAll 		= new TreeMap< String , String >( );
		
		while((line = dic.readLine()) != null)
		{
			tabArray 		= line.split("\t");
			
			comArray		= tabArray[1].split(",");
			
			for(String synm:comArray){
				if(!synm.startsWith("{"))
				{
					synmOrg = tabArray[0].trim().replace(" ", "");
											
					if(mapAttrSynm.containsKey(synmOrg))
					{
						attrList.addAll(mapAttrSynm.get(synmOrg));
						attrList.add(synm);
						mapAttrSynm.put(synmOrg, attrList);
						attrList = new TreeSet<String>();								
					}
					else
					{
						if(tabArray[0].trim().contains(" "))	attrList.add(tabArray[0].trim());
						attrList.add(synm);
						mapAttrSynm.put(synmOrg, attrList);
						attrList = new TreeSet<String>();									
					}
				}
				else
				{
					attrOrg = synm.replace("{", "");
					attrOrg = attrOrg.replace("}", "");
					attr = attrOrg.replace(" ", "");
					
					if(mapAttrSynm.containsKey(attr))
					{
						attrList.addAll(mapAttrSynm.get(attr));
						if(!attrOrg.equals(attr)) attrList.add(attrOrg);
						attrList.add(attr.trim());
						mapAttrSynm.put(attr, attrList);
						attrList = new TreeSet<String>();								
					}
					else
					{
						if(!attrOrg.equals(attr)) attrList.add(attrOrg);
						attrList.add(attr.trim());
						mapAttrSynm.put(attr, attrList);
						attrList = new TreeSet<String>();	
					}					
				}
			}			
		}
		
		for(Entry<String, TreeSet<String>> entry:mapAttrSynm.entrySet()){
			
			if(!entry.getValue().contains(entry.getKey())){
				entry.getValue().add(entry.getKey());
			}
		}
		
		
		for(Entry<String, TreeSet<String>> entry:mapAttrSynm.entrySet()){
			
			mapAttrAll.put(entry.getKey(),entry.getKey());
			
			for(String value:entry.getValue()){
				mapAttrAll.put(value,entry.getKey());
			}
		}
	}
	
	public void do_represent_mapper(String filename) throws IOException
	{	
		BufferedReader in			= new BufferedReader(new FileReader(filename)); 
		
		mapRepAttr 						= new TreeMap< String , String >( );
		
		String line;
		String tabArray[] = null;

		
		while((line = in.readLine()) != null)
		{
			tabArray 		= line.split("\t");
			
			if(mapRepAttr.containsKey(tabArray[0]+"\t"+tabArray[1])){
				//String repTemp = mapRepAttr.get(tabArray[0]+"\t"+tabArray[1]);
				//if(repTemp.equals(tabArray[2])) continue;
				//repTemp = repTemp +","+  tabArray[2];
				//mapRepAttr.remove(tabArray[0]+"\t"+tabArray[1]);
				//mapRepAttr.put(tabArray[0]+"\t"+tabArray[1], repTemp);
				//repTemp = "";
			}
			else
			{
				mapRepAttr.put(tabArray[0]+"\t"+tabArray[1], tabArray[2]);
			}
		}
		
		in.close();
		
		for(	Entry<String, String> entry:mapRepAttr.entrySet()){
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		}	
		
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		// TODO Auto-generated method stub		
		if((args.length!=1)&&(args[0].trim().equals(""))){
			System.out.println("Wrong Parameter !");
		}
		else
		{
			Date now = new Date();					  
			System.out.println("===== Knowledge Upload : Start ("+now+") =====");
			
			KnowledgeUploaderAll uploader = new KnowledgeUploaderAll();
				
			File inputfile = new File(uploader.MAINDIRECTORY+"/dict/"+args[0]);
			
			if(inputfile.exists() && inputfile.isFile()){
			
				String line, attr, cate;
				String strArray[];
				InsertAttrData AttrData = new InsertAttrData();
				AttrData SynmData = new AttrData();
				InsertDicData DicData = new InsertDicData();
				InsertDicDAO dao = new InsertDicDAO();
				
				int attrSeq = 0;
				attr 	= "";
				cate 	= ""; 
					
				BufferedReader dic = new BufferedReader(new FileReader(uploader.MAINDIRECTORY+"/dict/"+args[0])); 

				dao.deleteKnowledgeAll();
				
				uploader.do_synonym_mapper(uploader.MAINDIRECTORY+"/dict/synonym.txt");
				
				uploader.do_represent_mapper(uploader.MAINDIRECTORY+"/dict/RepAttr.txt");
						
				while((line = dic.readLine()) != null)
				{
					strArray = line.split("\t");
				
					
					if((!attr.equals(strArray[1])) ||(!cate.equals(strArray[0]))){
						AttrData.setCategoryId(strArray[0]);
						AttrData.setAttributeName(strArray[1]);
						AttrData.setWriter("한영섭");
						dao.insertAttr(AttrData);
						attr = strArray[1];
						cate = strArray[0];
						attrSeq++;
						
						//동의어 넣기				
						if(mapAttrSynm.containsKey(attr))
						{
							for(String synm:mapAttrSynm.get(attr)){
								SynmData.setAttr_nm(attr);
								SynmData.setAttr_seq(attrSeq);

								dao.insertSynmMap(attrSeq,synm);
								
								SynmData = new AttrData();
							}
						}			
					}
					if(mapRepAttr.containsKey(attr+"\t"+strArray[2])){
						DicData.setRepresentationId(mapRepAttr.get(attr+"\t"+strArray[2]));
					}		
					else
					{
						DicData.setRepresentationId("NULL");
					}
					
					
					if(mapAttrSynm.containsKey(attr))
					{
						for(String synm:mapAttrSynm.get(attr))
						{
							if(mapRepAttr.containsKey(synm+"\t"+strArray[2]))
							{
								DicData.setRepresentationId(mapRepAttr.get(synm+"\t"+strArray[2]));
							}					
						}
					}
		
					DicData.setAttributeId(attrSeq);
					DicData.setExpressionName(strArray[2]);
					DicData.setExpressionValue(strArray[3]);
					DicData.setWriter("한영섭");
					
					//System.out.println("ATTR NAME = "+attr+", EXPR NAME = "+strArray[2]+", ATTR SEQ = "+attrSeq+", CATE ID = "+cate+", Rep Name = "+DicData.getRepresentationId());
					dao.insertDic(DicData);	
					
					DicData = new InsertDicData();
				}
				dic.close();
				now = new Date();					  
				System.out.println("===== Knowledge Upload: End ("+now+") =====");
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
