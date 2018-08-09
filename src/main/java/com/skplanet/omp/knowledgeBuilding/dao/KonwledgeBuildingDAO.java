package com.skplanet.omp.knowledgeBuilding.dao;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.skplanet.omp.knowledgeBuilding.command.AnalysisStatus;
import com.skplanet.omp.knowledgeBuilding.command.InsertAspectExprData;


/** 
 * DaoOrigin을 상속받아 지식사전을 관리하기 위한 클래스.
 * 
 * @version	0.1
 * @since	2012.01.31
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	InsertDicDAO.java
 * @history  2012.01.31	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 * 
 */
public class KonwledgeBuildingDAO {

	private static SqlSessionFactory sqlmap = null;
	private String resource = "com/skplanet/omp/knowledgeBuilding/sql/mybatisConfDic.xml";
	private Reader reader = null;
	private SqlSession session = null;
	
	/**
	 * Constructor
	 */
	public KonwledgeBuildingDAO(){

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
	 * name: selectAnalysisStatus
	 * description: 지식구축 요청 읽기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<AnalysisStatus>  selectAnalysisStatus( ) {

		ArrayList<AnalysisStatus> analysisStatus = new ArrayList<AnalysisStatus>();
		
		try{
			session = sqlmap.openSession();			
			//AttrData.setCategoryId((String)session.selectOne("KnowledgeMapperDic.selectCategoryID",AttrData.getCategoryId()));			
			
			analysisStatus.addAll(session.selectList("KnowledgeMapperDic.selectAnalysisStatus"));
			
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return analysisStatus;
	}
	
	/**
	 * name: getCountAnalysisStatus
	 * description: 지식구축 요청 count 읽기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public int  getCountAnalysisStatus( ) {
		
		int count = 0;
		
		try{
			session = sqlmap.openSession();
			
			count = (Integer)session.selectOne("KnowledgeMapperDic.getCountAnalysisStatus");
			
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return count;
	}
	
	/**
	 * name: selectCrawlObjects
	 * description: 지식구축 수집 OBJECT 읽기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<String>  selectCrawlObjects(String categoryID) {

		ArrayList<String> objects = new ArrayList<String>();
		
		try{
			session = sqlmap.openSession();
			
			objects.addAll(session.selectList("KnowledgeMapperDic.selectCrawlObjects",categoryID));
			
			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
		return objects;
	}
	
	public List<String> getChildCategoryIds(String parentCategoryId) throws Exception
	{
		List<String> childCategoryIds = new ArrayList<String>();
		
		return childCategoryIds;
	}
	
	public String getDomainCategoryId(String categoryId, SqlSession session) throws Exception
	{
		try
		{
			while (true)
			{
				if( categoryId.equals("1") || categoryId.equals("445") || categoryId.equals("449") || categoryId.equals("0") )
				{
					break;
				}
				
				categoryId = (String)session.selectOne("KnowledgeMapperDic.selectParentCategoryIdByCategoryId",categoryId);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
			
			return null;
		}
		
		return categoryId;
	}
	
	/**
	 * name: insertAspectExpr
	 * description: 추출된 속성-표현 저장하기
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void insertAspectExpr(InsertAspectExprData aspectExprData) {

		try{
			session = sqlmap.openSession();
			
			int exprExist = (Integer)session.selectOne("KnowledgeMapperDic.selectExpressionId",aspectExprData);
			
			if(exprExist == 0)
			{
				aspectExprData.setDomainCategoryId(getDomainCategoryId(aspectExprData.getCategoryId(), session));
				aspectExprData.setRepresentationId((String)session.selectOne("KnowledgeBuildingMapper.selectRepresentationId",aspectExprData));				
				
				session.insert("KnowledgeMapperDic.insertAspectExpression",aspectExprData);
				session.commit();
			}
		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * name: deleteAspectExpr
	 * description: 지식 추출전 이전 데이터 삭제
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void deleteAspectExpr(String CategoryId) {

		try{
			session = sqlmap.openSession();

			session.delete("KnowledgeBuildingMapper.deleteAspectExpression",CategoryId);

			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
	
	/**
	 * name: updateAnalysisStatus
	 * description: 지식구축 상태 업데이트
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public void updateAnalysisStatus(String CategoryId, String StatusType) {

		try{
			
			AnalysisStatus analysisStatus = new AnalysisStatus();
			
			analysisStatus.setCategoryId(CategoryId);
			analysisStatus.setStatusType(StatusType);
			
			session = sqlmap.openSession();

			session.insert("KnowledgeBuildingMapper.updateAnalysisStatus",analysisStatus);

			session.commit();

		} catch (Exception e) {
			session.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}
}
