package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 속성을 DB에 저장하기 위해 사용되는 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	InsertAttrData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class InsertAttrData {
	/**
	 * @uml.property  name="categoryId"
	 */
	String CategoryId;
	/**
	 * @uml.property  name="attributeName"
	 */
	String AttributeName;
	/**
	 * @uml.property  name="writer"
	 */
	String Writer;
	
	/**
	 * @return
	 * @uml.property  name="categoryId"
	 */
	public String getCategoryId() {
		return CategoryId;
	}
	/**
	 * @param categoryId
	 * @uml.property  name="categoryId"
	 */
	public void setCategoryId(String categoryId) {
		CategoryId = categoryId;
	}
	/**
	 * @return
	 * @uml.property  name="attributeName"
	 */
	public String getAttributeName() {
		return AttributeName;
	}
	/**
	 * @param attributeName
	 * @uml.property  name="attributeName"
	 */
	public void setAttributeName(String attributeName) {
		AttributeName = attributeName;
	}
	/**
	 * @return
	 * @uml.property  name="writer"
	 */
	public String getWriter() {
		return Writer;
	}
	/**
	 * @param writer
	 * @uml.property  name="writer"
	 */
	public void setWriter(String writer) {
		Writer = writer;
	}
}
