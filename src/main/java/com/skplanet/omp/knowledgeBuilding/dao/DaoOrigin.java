package com.skplanet.omp.knowledgeBuilding.dao;

/** 
 * Database에 접근하기 위한 기능이 정의 되어있는 api 클래스.
 * 
 * @version	0.1
 * @since	2012.01.27
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	DaoOrigin.java
 * @history  2012.01.27	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 * 
 */
import java.io.Reader;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

/**
 * @uml.dependency  supplier="dao.mybatisAPI" stereotypes="Soyatec::Access"
 */
abstract class DaoOrigin {

	/**
	 * @uml.property  name="sql_map"
	 */
	private SqlSessionFactory sql_map = null;

	/**
	 * Getter of the property <tt>sql_map</tt>
	 * 
	 * @return Returns the sql_map.
	 * @uml.property name="sql_map"
	 */
	public SqlSessionFactory getSql_map() {
		return sql_map;
	}

	/**
		 */
	abstract public void select();
	
	/**
			 */
	abstract public void delete();

	/**
				 */
	abstract public void insert();
	
	/**
					 */
	abstract public void update();
	
	/**
						 */
	abstract public void isValid();
	
	/**
	 * Setter of the property <tt>sql_map</tt>
	 * 
	 * @param sql_map
	 *            The sql_map to set.
	 * @uml.property name="sql_map"
	 */
	public void setSql_map(SqlSessionFactory sql_map) {
		this.sql_map = sql_map;
	}

	/**
	 * @uml.property  name="resource"
	 */
	private String resource;

	/**
	 * Getter of the property <tt>resource</tt>
	 * 
	 * @return Returns the resource.
	 * @uml.property name="resource"
	 */
	public String getResource() {
		return resource;
	}

	/**
	 * Setter of the property <tt>resource</tt>
	 * 
	 * @param resource
	 *            The resource to set.
	 * @uml.property name="resource"
	 */
	public void setResource(String resource) {
		this.resource = resource;
	}

	/**
	 * @uml.property  name="reader"
	 */
	private Reader reader = null;

	/**
	 * Getter of the property <tt>reader</tt>
	 * 
	 * @return Returns the reader.
	 * @uml.property name="reader"
	 */
	public Reader getReader() {
		return reader;
	}

	/**
	 * Setter of the property <tt>reader</tt>
	 * 
	 * @param reader
	 *            The reader to set.
	 * @uml.property name="reader"
	 */
	public void setReader(Reader reader) {
		this.reader = reader;
	}

	/**
	 * @uml.property  name="session"
	 */
	private SqlSession session = null;

	/**
	 * Getter of the property <tt>session</tt>
	 * 
	 * @return Returns the session.
	 * @uml.property name="session"
	 */
	public SqlSession getSession() {
		return session;
	}

	/**
	 * Setter of the property <tt>session</tt>
	 * 
	 * @param session
	 *            The session to set.
	 * @uml.property name="session"
	 */
	public void setSession(SqlSession session) {
		this.session = session;
	}

	/**
	 * @uml.property  name="mapper_name"
	 */
	private String mapper_name = null;

	/**
	 * Getter of the property <tt>mapper_name</tt>
	 * 
	 * @return Returns the mapper_name.
	 * @uml.property name="mapper_name"
	 */
	public String getMapper_name() {
		return mapper_name;
	}

	/**
	 * Setter of the property <tt>mapper_name</tt>
	 * 
	 * @param mapper_name
	 *            The mapper_name to set.
	 * @uml.property name="mapper_name"
	 */
	public void setMapper_name(String mapper_name) {
		this.mapper_name = mapper_name;
	}

}
