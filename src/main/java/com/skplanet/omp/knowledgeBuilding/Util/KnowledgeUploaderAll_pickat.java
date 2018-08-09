package com.skplanet.omp.knowledgeBuilding.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;

import com.skplanet.omp.knowledgeBuilding.command.AttrData;
import com.skplanet.omp.knowledgeBuilding.command.InsertAttrData;
import com.skplanet.omp.knowledgeBuilding.command.InsertDicData;
import com.skplanet.omp.knowledgeBuilding.dao.InsertDicDAO;

/** 
 * 정재된 기 분석 사전을 DB에 적재하는 클래스
 * 
 * @version	0.1
 * @since	2012.01.16
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	KnowledgeUploader.java
 * @history
 *  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class KnowledgeUploaderAll_pickat {	

	public static HashMap<String, TreeSet<String>> mapCateNew;
	
	public static TreeMap<String, String> mapMappingIDtoCategoryID;
	
	public static TreeSet<String> attrList = new TreeSet<String>();	
	public static TreeSet<String> cateList = new TreeSet<String>();

	
	public static TreeMap<String, TreeSet<String>> mapAttrSynm;
	public static TreeMap<String, TreeSet<String>> mapAttrCate;
	
	public static TreeMap<String, String> mapAttrAll;
	
	public static TreeMap<String, String> mapRepAttr;
	

	public void do_synonym_mapper() throws IOException
	{	
		BufferedReader dic = new BufferedReader(new FileReader( "/svc/omp/knowledgeBuilding/resource/synonym_pickat_1227.txt")); 
		
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
					synmOrg = tabArray[0].trim().replace(" ", "").toUpperCase();
											
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
					attr = attrOrg.replace(" ", "").toUpperCase();
					
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
			System.out.println(entry.getKey() +"\t"+entry.getValue());
		}
		
		System.out.println("=========================== all ============================");
		
		for(Entry<String, String> entry:mapAttrAll.entrySet()){
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		}
	}
	
	public void do_represent_mapper() throws IOException
	{	
		BufferedReader in			= new BufferedReader(new FileReader("/svc/omp/knowledgeBuilding/resource/RepAttr_0401.txt")); 
		
		mapRepAttr 						= new TreeMap< String , String >( );
		
		String line, attr, expr;
		String tabArray[] = null;

		
		while((line = in.readLine()) != null)
		{
			tabArray 		= line.split("\t");
			
			attr 	= tabArray[0].toUpperCase();
			expr 	= tabArray[1].toUpperCase();
			
			if(mapAttrAll.containsKey(attr)){
				attr 	= mapAttrAll.get(attr);
			}
			
			if(mapRepAttr.containsKey(attr+"\t"+expr)){
				//String repTemp = mapRepAttr.get(tabArray[0]+"\t"+tabArray[1]);
				//if(repTemp.equals(tabArray[2])) continue;
				//repTemp = repTemp +","+  tabArray[2];
				//mapRepAttr.remove(tabArray[0]+"\t"+tabArray[1]);
				//mapRepAttr.put(tabArray[0]+"\t"+tabArray[1], repTemp);
				//repTemp = "";
			}
			else
			{
				mapRepAttr.put(attr+"\t"+expr, tabArray[2]);
			}
		}
				
		in.close();
		
		for(	Entry<String, String> entry:mapRepAttr.entrySet()){
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		}	
		
	}
	
	public void do_synonym_converter() throws IOException
	{	
		BufferedReader in			= new BufferedReader(new FileReader("D:/knowledgeBuilding/dict/BASKET_DICT_ADD_1228.txt")); 
		BufferedWriter	out			= new BufferedWriter(new FileWriter("D:/knowledgeBuilding/dict/BASKET_DICT_ADD_SYNM_1228.txt"));
		
		String line;
		String tabArray[] = null;

		
		while((line = in.readLine()) != null)
		{
			tabArray 		= line.split("\t");
			
			if(mapAttrAll.containsKey(tabArray[1]))
			{
				System.out.println(tabArray[0]+"\t"+mapAttrAll.get(tabArray[1])+"\t"+tabArray[2]+"\t"+tabArray[3]);
				out.write(tabArray[0]+"\t"+mapAttrAll.get(tabArray[1])+"\t"+tabArray[2]+"\t"+tabArray[3]+"\n");
			}
			else
			{
				//System.out.println(tabArray[0]+"\t"+tabArray[1]+"\t"+tabArray[2]+"\t"+tabArray[3]);
				out.write(tabArray[0]+"\t"+tabArray[1]+"\t"+tabArray[2]+"\t"+tabArray[3]+"\n");
			}
							
		}				
		out.close();
		in.close();
	}
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader dic = new BufferedReader(new FileReader( "/svc/omp/knowledgeBuilding/resource/PICKAT_DICT_ALL_SYNM_MN_1227.txt")); 
		String line, attr, expr, cate;
		String strArray[];
		InsertAttrData AttrData = new InsertAttrData();
		AttrData SynmData = new AttrData();
		InsertDicData DicData = new InsertDicData();
		InsertDicDAO dao = new InsertDicDAO();
		
		int attrSeq = 13144;
		attr 	= "";
		cate 	= ""; 
		expr 	= "";
		
		KnowledgeUploaderAll_pickat test = new KnowledgeUploaderAll_pickat();
		
		//dao.deleteKnowledgeAll();
		
		test.do_synonym_mapper();
		
		//test.do_synonym_converter();
		
		test.do_represent_mapper();
		
		
		while((line = dic.readLine()) != null)
		{
			strArray = line.split("\t");
			
			if((!attr.equals(strArray[1].trim().toUpperCase())) ||(!cate.equals(strArray[0]))){
				AttrData.setCategoryId(strArray[0]);
				AttrData.setAttributeName(strArray[1].trim().toUpperCase());
				AttrData.setWriter("한영섭");
				dao.insertAttr(AttrData);
				attr = strArray[1].trim().toUpperCase();
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
			
			
			expr	= strArray[2].trim().toUpperCase();
			/*
			if(!strArray[4].equals("")){
				DicData.setRepresentationId(strArray[4]);
			}
			else
			{
				DicData.setRepresentationId("NULL");
			}
			*/
			
			DicData.setRepresentationId("미할당");
			
			if(mapRepAttr.containsKey(attr+"\t"+expr)){
				DicData.setRepresentationId(mapRepAttr.get(attr+"\t"+expr));
			}		
			else
			{			
				if(mapAttrSynm.containsKey(attr))
				{
					for(String synm:mapAttrSynm.get(attr))
					{
						if(mapRepAttr.containsKey(synm+"\t"+expr))
						{
							DicData.setRepresentationId(mapRepAttr.get(synm+"\t"+expr));
						}					
					}
				}
			}
			
			DicData.setAttributeId(attrSeq);
			DicData.setExpressionName(expr);
			DicData.setExpressionValue(strArray[3]);
			DicData.setWriter("한영섭");

			if(DicData.getRepresentationId().equals("미할당")){
				System.out.println(cate+"\t"+attr+"\t"+DicData.getExpressionName()+"\t"+DicData.getExpressionValue()+"\t"+DicData.getRepresentationId());
			}
			if(DicData.getRepresentationId().equals("NULL")){
				System.out.println(cate+"\t"+attr+"\t"+DicData.getExpressionName()+"\t"+DicData.getExpressionValue()+"\t"+DicData.getRepresentationId());
			}
			//System.out.println("ATTR NAME = "+attr+", EXPR NAME = "+strArray[2]+", ATTR SEQ = "+attrSeq+", CATE ID = "+cate+", Rep Name = "+DicData.getRepresentationId());
			dao.insertDic(DicData);	

			DicData = new InsertDicData();
			
		}
	}
}
