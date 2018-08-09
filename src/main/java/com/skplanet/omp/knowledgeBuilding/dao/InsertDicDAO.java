package com.skplanet.omp.knowledgeBuilding.dao;

import java.io.IOException;
import java.io.Reader;
import java.sql.Connection;
import java.sql.Statement;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.skplanet.omp.knowledgeBuilding.command.AttrData;
import com.skplanet.omp.knowledgeBuilding.command.AttrMapData;
import com.skplanet.omp.knowledgeBuilding.command.InsertAttrData;
import com.skplanet.omp.knowledgeBuilding.command.InsertDicData;


/** 
 * DaoOrigin을 상속받아 정제된 감성 지식을 데이터 베이스에 직접 저장한다.
 * 
 * @version	0.1
 * @since	2012.01.31
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	InsertDicDAO.java
 * @history  2012.01.31	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 *  @history  2012.11.12	 v0.1 *	Max(attributeID) 읽어오기 추가.	한영섭.<br>
 * 
 */
public class InsertDicDAO {

	private static SqlSessionFactory sqlmap = null;
	private String resource = "com/skplanet/omp/knowledgeBuilding/sql/mybatisConfDic.xml";
	private Reader reader = null;
	private SqlSession session = null;
	
	/**
	 * Constructor
	 */
	public InsertDicDAO(){

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
	public void insertAttr(InsertAttrData AttrData) {

		try{
			//System.out.println(AttrData.getAttributeName());
			session = sqlmap.openSession();			
			//AttrData.setCategoryId((String)session.selectOne("KnowledgeMapperDic.selectCategoryID",AttrData.getCategoryId()));
			session.insert("KnowledgeMapperDic.inserAttrData",AttrData);
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}

	/**
	 * name: insertDic
	 * description: 속성 불러오기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void insertDic(InsertDicData DicData) {

		try{
			session = sqlmap.openSession();
			//System.out.println(DicData.getExpressionName());
			DicData.setRepresentationId((String)session.selectOne("KnowledgeMapperDic.selectRepresentationId",DicData.getRepresentationId()));
			//System.out.println(DicData.getRepresentationId());
			session.insert("KnowledgeMapperDic.inserDicData",DicData);
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * name: insertSynm
	 * description: 속성 동의어 입력하기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void insertSynm(AttrData attrData) {

		try{
			session = sqlmap.openSession();

			attrData.setWriter("한영섭");
			session.insert("KnowledgeMapperDic.inserSynmOrg",attrData);
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * name: insertDic
	 * description: 속성 불러오기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void insertSynmMap(int attrSeq, String synm) {

		AttrMapData attrMap = new AttrMapData();	
		
		try{
			session = sqlmap.openSession();
				
			attrMap.setAttributeId(attrSeq);
			attrMap.setSynonymId((String) session.selectOne("KnowledgeMapperDic.selectSynonymId", synm));
			
			if(attrMap.getSynonymId() == null)
			{
				attrMap.setSynonymName(synm);
				attrMap.setWriter("한영섭");
				
				session.insert("KnowledgeMapperDic.inserSynmOrg",attrMap);
				
				session.commit();
				
				attrMap.setSynonymId((String) session.selectOne("KnowledgeMapperDic.selectSynonymId", synm));
			}
			
			//System.out.println("attr seq = " + attrMap.getAttributeId() + ", synm = " + attrMap.getSynonymId());
			
			session.insert("KnowledgeMapperDic.insertSynonymMapId",attrMap);
			
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * name: insertSynm
	 * description: 속성 동의어 입력하기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void deleteKnowledgeAll() {

		try{
			session = sqlmap.openSession();
			Connection conn = session.getConnection();
			Statement stmt = conn.createStatement();

			stmt.execute("truncate table kmattributesynonym_old");
			conn.commit();
			stmt.execute("ALTER TABLE kmattributesynonym_old auto_increment=0");
			conn.commit();

			stmt.execute("truncate table kmattributesynonymmapper_old");
			conn.commit();
			stmt.execute("ALTER TABLE kmattributesynonymmapper_old auto_increment=0");
			conn.commit();

			stmt.execute("truncate table kmopinionattributes_old");
			conn.commit();
			stmt.execute("ALTER TABLE kmopinionattributes_old auto_increment=0");
			conn.commit();

			stmt.execute("truncate table kmopinionexpressions_old");
			conn.commit();
			stmt.execute("ALTER TABLE kmopinionexpressions_old auto_increment=0");
			conn.commit();

			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
}
