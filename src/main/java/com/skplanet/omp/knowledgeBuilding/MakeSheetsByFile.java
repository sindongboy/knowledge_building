package com.skplanet.omp.knowledgeBuilding;

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

/** 
 *  FILE로 구축된 감성사전을 감성분석을 위한 sheet로 저장하는 클래스<P>
 * 
 * @version	0.1
 * @since	2012.01.16
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	MakeSheetsFromFile.java
 * @history
 *  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class MakeSheetsByFile {
	
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
		BufferedReader in			= new BufferedReader(new FileReader("D:/knowledgeBuilding/dict/RepAttr_basket_1228_2.txt")); 
		
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
				
				System.out.println(tabArray[0]+","+attr);
			}
			
			if(mapRepAttr.containsKey(attr+"\t"+expr)){
				//String repTemp = mapRepAttr.get(attr+"\t"+expr);
				//if(repTemp.equals(tabArray[2]))  continue;
				//repTemp = repTemp +","+  tabArray[2];
				//mapRepAttr.remove(attr+"\t"+expr);
				//mapRepAttr.put(attr+"\t"+expr, repTemp);
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
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		BufferedReader in			= new BufferedReader(new FileReader("D:/knowledgeBuilding/dict/BASKET_DICT_ALL_NEW_SYNM_0401.txt")); 
		BufferedWriter	out			= null;
		BufferedWriter	outRep			= new BufferedWriter(new FileWriter("D:/knowledgeBuilding/dict/sheet/0401/UnMatchedRep.txt"));
		int attrSeq 	= 0;
		String cate 	= ""; 
		String line 	= "";
		String strArray[];
		int attrCnt = 0;
		
		MakeSheetsByFile tester = new MakeSheetsByFile();
		tester.do_synonym_mapper();
		tester.do_represent_mapper();
		
		
		DBManager dbm = new DBManager();

		String CategoryName = null;
		String CategoryPath = null;
		String CategoryID = null;
		String TempID = null;
		String TempIDs = "";
		
		//사전에 넣기위한 변수들
		String expr 			= "";
		String value 		= "";
		String attr			= "";
		String attrTmp			= "";
		
		while((line = in.readLine()) != null)
		{
			strArray = line.split("\t");
		
			if(!cate.equals(strArray[0])){
				System.out.println("");
				if(out != null)	out.close();
				
				attrCnt = 0;
				TempIDs = "";
				cate = strArray[0];
				out			= new BufferedWriter(new FileWriter("D:/knowledgeBuilding/dict/sheet/0401/"+cate+"_sheet.txt"));
				CategoryName = dbm.selectOne("SELECT CategoryName FROM kmcategory WHERE CategoryId = "+cate,"CategoryName");
				CategoryPath = dbm.selectOne("SELECT CategoryPath FROM kmcategory WHERE CategoryId = "+cate,"CategoryPath");
				CategoryID = cate;
				
				System.out.println("CategoryID : "+CategoryID+", CategoryName : "+CategoryName);
				
				TempID = CategoryID;
				//부모 카테고리명 불러오기
				while(true){		
					TempID = dbm.selectOne("SELECT ParentCategoryId FROM kmcategory WHERE CategoryId = "+TempID,"ParentCategoryId");
					
					if(TempID.equals("0")){
						break;
					}
					if(TempIDs.equals("")){
						TempIDs = TempID ;		
					}else{
						TempIDs = TempID +"/"+ TempIDs ;
					}
				}
				System.out.println("^CATEGORY_ID:"+CategoryID);	
				out.write("^CATEGORY_ID:"+CategoryID+"\n");
				System.out.println("^CATEGORY_NAME:"+CategoryName);
				out.write("^CATEGORY_NAME:"+CategoryName+"\n");
				System.out.println("^CATEGORY_PATH_NAME:"+CategoryPath);
				out.write("^CATEGORY_PATH_NAME:"+CategoryPath+"\n");
				System.out.println("^CATEGORY_PATH_ID:"+TempIDs+"\n");
				out.write("^CATEGORY_PATH_ID:"+TempIDs+"\n");
				TempIDs = "";
			}
			attr = strArray[1].trim().toUpperCase();
			expr = strArray[2].trim().toUpperCase();
			value = strArray[3].trim().toUpperCase();
			
			if(!attr.equals(attrTmp))
			{
				out.write("\n");
				//System.out.println("OM"+(attrCnt)+".FEA:"+attr);
				//out.write("OM"+(attrCnt+1)+".FEA:"+attr+"\n");
				
				if(mapAttrSynm.containsKey(attr)){
					
					String temp = "";
					
					TreeSet<String> tempSet = mapAttrSynm.get(attr);
									
					for(String tempRep:tempSet){
						temp = temp +"\t"+tempRep;
					}					
					out.write( "OM"+(attrCnt+1)+".FEA:"+attr+temp+"\n");
					
					tempSet = new TreeSet<String>();
				}
				else
				{
					out.write("OM"+(attrCnt+1)+".FEA:"+attr+"\n");
				}
				
				attrCnt = attrCnt + 1;
				attrTmp = attr;
			}
			
			if(mapRepAttr.containsKey(attr+"\t"+expr)){
				//System.out.println("OM"+(attrCnt)+".EXP:"+expr + "\t"+value);
				out.write("OM"+(attrCnt)+".EXP:"+expr + "\t"+value+ "\t"+mapRepAttr.get(attr+"\t"+expr)+"\n");
			}
			else
			{
				System.out.println("OM"+(attrCnt)+".EXP:"+expr + "\t"+value);
				out.write("OM"+(attrCnt)+".EXP:"+expr + "\t"+value+"\n");
				outRep.write(attr+"\t"+expr + "\t"+value+"\t"+CategoryName+"\n");
			}			
		}
		in.close();
		out.close();
		outRep.close();
	}
}
