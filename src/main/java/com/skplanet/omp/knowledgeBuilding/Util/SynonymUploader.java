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

import com.skplanet.omp.knowledgeBuilding.Database.DBManager;

public class SynonymUploader {
	public static TreeMap<String, TreeSet<String>> mapAttrSynm;
	public static TreeMap<String, TreeSet<String>> mapAttrCate;
	
	public static TreeMap<String, String> mapAttrAll;
	
	public static TreeMap<String, String> mapRepAttr;
	
	public static TreeSet<String> attrList = new TreeSet<String>();	
	public static TreeSet<String> cateList = new TreeSet<String>();

	public void do_synonym_mapper() throws IOException
	{	
		BufferedReader dic = new BufferedReader(new FileReader( "D:/knowledgeBuilding/dict/synonym_basket_1227.txt")); 
		
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
			
			synmOrg = tabArray[0].trim().replace(" ", "");
			
			for(String synm:comArray){
				if(!synm.startsWith("{"))
				{										
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
						if(!attrOrg.equals(attr)) attrList.add(synmOrg);
						attrList.add(synmOrg.trim());
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
		
		System.out.println("=========================== add prime attr  ============================");
		
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
	
	public void do_synonym_converter() throws IOException
	{	
		BufferedReader in			= new BufferedReader(new FileReader("D:/knowledgeBuilding/dict/BASKET_DICT_ALL_NEW_SYNM_0401.txt")); 
		BufferedWriter	out			= new BufferedWriter(new FileWriter("D:/knowledgeBuilding/dict/BASKET_DICT_ALL_NEW_SYNM_0401_NEW.txt"));
		
		String line;
		String tabArray[] = null;

		
		while((line = in.readLine()) != null)
		{
			tabArray 		= line.split("\t");
			
			if(mapAttrAll.containsKey(tabArray[1]))
			{
				//System.out.println(mapAttrAll.get(tabArray[0])+"\t"+tabArray[1]+"\t"+tabArray[2]+"\t"+tabArray[3]);
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
	
	public void do_represent_mapper() throws IOException
	{	
		BufferedReader in			= new BufferedReader(new FileReader("D:/11REVIEW/dict/RepAttr.txt")); 
		
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
		
		for(	Entry<String, String> entry:mapRepAttr.entrySet()){
			System.out.println(entry.getKey()+"\t"+entry.getValue());
		}	
		
	}
	
	public void do_synonym_uploader() throws Exception
	{
		DBManager dbm = new DBManager();
		
		for(Entry<String, TreeSet<String>> entry:mapAttrSynm.entrySet()){
			for(String value:entry.getValue()){
				dbm.insert("INSERT INTO  kmattributesynonym (SynonymName, Registrant, RegistedDate, Updater, UpdatedDate) VALUES ('"+value+"', '한영섭',NOW(), '한영섭',NOW())");
				System.out.println("INSERT INTO  kmattributesynonym (SynonymName, Registrant, RegistedDate) VALUES ('"+value+"', '한영섭',NOW())");
				}
		}
		dbm.close();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		SynonymUploader synonymUploader = new SynonymUploader();
		
		/*
		String[] rep = "가격_성능_재질_품질_디자인_크기_무게_색상_냄새_향_상품평_설치_구성품_사용성_양_고객지원_종류_구매_웰빙_브랜드_선호도_포장_성분_제품일반".split("_");
		
		DBManager dbm = new DBManager();
		
		for(String repp:rep){
			dbm.insert("INSERT INTO  kmrepresentationattribute (RepresentationName, Registrant, RegistedDate, Updater, UpdatedDate) VALUES ('"+repp+"', '한영섭',NOW(), '한영섭',NOW())");
			System.out.println("INSERT INTO  kmrepresentationattribute (RepresentationName, Registrant, RegistedDate, Updater, UpdatedDate) VALUES ('"+repp+"', '한영섭',NOW(), '한영섭',NOW())");
		}
		dbm.close();
		*/
		synonymUploader.do_synonym_mapper();
		
		//synonymUploader.do_synonym_uploader();
		
		//synonymUploader.do_represent_mapper();
		
		synonymUploader.do_synonym_converter();
		
	}
}
