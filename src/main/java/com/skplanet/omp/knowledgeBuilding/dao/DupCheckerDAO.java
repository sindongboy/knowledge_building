package com.skplanet.omp.knowledgeBuilding.dao;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.skplanet.omp.knowledgeBuilding.command.InsertAttrData;
import com.skplanet.omp.knowledgeBuilding.command.InsertDicData;


/** 
 * DaoOrigin을 상속 받아 지식사전의 중복을 체크하기 위한 클래스.
 * 
 * @version	0.1
 * @since	2012.01.31
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	DupCheckerDAO.java
 * @history  2012.01.31	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 * 
 */
public class DupCheckerDAO {

	private static SqlSessionFactory sqlmap = null;
	private String resource = "com/skplanet/omp/knowledgeBuilding/sql/mybatisConfDic.xml";
	private Reader reader = null;
	private SqlSession session = null;
	private boolean  res;
	
	/**
	 * Constructor
	 */
	public DupCheckerDAO(){

		super();

		try {
			reader =  org.apache.ibatis.io.Resources.getResourceAsReader(resource);

			if(sqlmap == null){
				sqlmap = new SqlSessionFactoryBuilder().build(reader);
			}

			reader.close();
		} catch (IOException e) {
			e.printStackTrace();
			new Exception();
		}

	}
	
	/**
	 * name: insertAttr
	 * description: 속성 등록하기
	 * @return
	 */
	public String checkCategoryID( String CategoryID) {
		
		String CategoryIDs = "";
		
		try{			
			System.out.println("1");	
			session = sqlmap.openSession();
			System.out.println("2");	
			session.selectOne("KnowledgeMapperDic.selectCategoryID",CategoryID);
			System.out.println("3");	
			session.commit();
			System.out.println("4");	
			
			while(true){	
							
				String ParentCategoryID =  (String) session.selectOne("KnowledgeMapperDic.selectCategoryID",CategoryID);
				session.commit();
				
				if(!ParentCategoryID.equals("0")){
					if(CategoryIDs.equals("")){
						CategoryIDs = ParentCategoryID;
					}else{
						CategoryIDs = CategoryIDs + "," + ParentCategoryID;
					}
				}else{
					break;
				}
			}
		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return CategoryIDs;
	}

	/**
	 * name: insertDic
	 * description: 속성 불러오기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean dupCheck(String AttributeName, String CategoryID) {
		 boolean temp = false;
		try{
			temp = false;
			CategoryID = CategoryID.replace(",", " OR ");
			String sql = "(CategoryID = " + CategoryID + ") AND AttributeName = " +  AttributeName;
			
			session = sqlmap.openSession();
			List<String> attr =  session.selectList("KnowledgeMapperDic.selectDupCheck",sql);
			session.commit();

			if(attr.isEmpty()){
				temp = true;
			}else{
				temp=false;
			}
		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return temp;
	}
	
}
