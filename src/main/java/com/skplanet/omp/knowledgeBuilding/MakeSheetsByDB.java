package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.skplanet.omp.knowledgeBuilding.Database.DBManager;

/** 
 *  DB에 구축된 감성사전을 감성분석을 위한 sheet로 저장하는 클래스<P>
 * 
 * @version	0.1
 * @since	2012.09.16
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	MakeSheetsFromDB.java
 * @history
 *  2012.09.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class MakeSheetsByDB {
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		BufferedWriter	out			= null;
		
		//사전에 넣기위한 변수들
		
		
		int attrCnt = 0;
		
		ArrayList<String> CategoryIDs = new ArrayList<String>();
		ArrayList<String> AttributeIDs = new ArrayList<String>();
		ArrayList<String> Synonyms = new ArrayList<String>();
		ArrayList<String[]> DicDatas = new ArrayList<String[]>();
		
		DBManager dbm = new DBManager();

		String attr							= "";
		String CategoryName 	= "";
		String CategoryPath 		= "";
		String synonym 				= "";
		String TempID 					= "";
		String TempIDs 					= "";

		String query = "SELECT CategoryId FROM kmopinionattributes GROUP BY CategoryId";
		
		dbm.selectAll(query, "CategoryId", CategoryIDs);
		
		System.out.println(CategoryIDs);
		
		for(String CategoryId:CategoryIDs){
			out			= new BufferedWriter(new FileWriter("D:/knowledgeBuilding/dict/sheetFromDB/"+CategoryId+"_sheet.txt"));
			
			CategoryName = dbm.selectOne("SELECT CategoryName FROM kmcategory WHERE CategoryId = "+CategoryId,"CategoryName");
			CategoryPath = dbm.selectOne("SELECT CategoryPath FROM kmcategory WHERE CategoryId = "+CategoryId,"CategoryPath");
			TempID = CategoryId;
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
			
			System.out.println("^CATEGORY_ID:"+CategoryId);	
			out.write("^CATEGORY_ID:"+CategoryId+"\n");
			System.out.println("^CATEGORY_NAME:"+CategoryName);
			out.write("^CATEGORY_NAME:"+CategoryName+"\n");
			System.out.println("^CATEGORY_PATH_NAME:"+CategoryPath);
			out.write("^CATEGORY_PATH_NAME:"+CategoryPath+"\n");
			System.out.println("^CATEGORY_PATH_ID:"+TempIDs+"\n");
			out.write("^CATEGORY_PATH_ID:"+TempIDs+"\n");
			TempIDs = "";
			out.flush();
			
			query = "SELECT AttributeId FROM kmopinionattributes WHERE CategoryId = "+CategoryId;			
			dbm.selectAll(query, "AttributeId", AttributeIDs);
			attrCnt = 0;
			
			System.out.println(AttributeIDs);
			
			for(String AttributeID:AttributeIDs){
				synonym="";
				attrCnt++;
				attr = dbm.selectOne("SELECT AttributeName FROM kmopinionattributes WHERE AttributeId = "+AttributeID,"AttributeName");
				
				query = "select count(b.SynonymName) as synCnt ";
				query = query + "from kmattributesynonymmapper a ";
				query = query + "Join kmattributesynonym b ";
				query = query + "on a.SynonymId = b.SynonymId ";
				query = query + "where a.AttributeId = "+AttributeID;
				
				int synCnt = Integer.parseInt(dbm.selectOne(query,"synCnt"));
				
				if(synCnt > 0){
				
					query = "select b.SynonymName ";
					query = query + "from kmattributesynonymmapper a ";
					query = query + "Join kmattributesynonym b ";
					query = query + "on a.SynonymId = b.SynonymId ";
					query = query + "where a.AttributeId = "+AttributeID;
					
					dbm.selectAll(query, "SynonymName", Synonyms);
					
					System.out.println(Synonyms);
					
					for(String syn:Synonyms){
						synonym = synonym+"\t"+syn;
					}
				}
				
				System.out.println("\nOM"+(attrCnt)+".FEA:"+attr+synonym);
				out.write("\nOM"+(attrCnt)+".FEA:"+attr+synonym+"\n");
			
				query = "SELECT a.ExpressionName, a.ExpressionValue, b.RepresentationName ";
				query = query + "FROM kmopinionexpressions a ";
				query = query + "JOIN kmrepresentationattribute b ";
				query = query + "ON a.RepresentationId = b.RepresentationId ";
				query = query + "WHERE AttributeId = "+ AttributeID;
				
				dbm.selectAllMult(query, 3, DicDatas);
				
				for(String[] DicData:DicDatas){
					System.out.println("OM"+(attrCnt)+".EXP:"+DicData[0] + "\t"+DicData[1] + "\t" +  DicData[2]);
					out.write("OM"+(attrCnt)+".EXP:"+DicData[0] + "\t"+DicData[1] + "\t" +  DicData[2]+"\n");
				}
				Synonyms.clear();
				DicDatas.clear();
			}
			AttributeIDs.clear();
			
			out.close();
		}
		CategoryIDs.clear();
		out.close();
		dbm.close();
	}
}
