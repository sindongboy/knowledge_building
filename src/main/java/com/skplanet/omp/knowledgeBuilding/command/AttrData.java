package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 속성번호와 속성명을 로드하기 위해 사용되는 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	AttrData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class AttrData {
	/**
	 * @uml.property  name="categoryId"
	 */
	String CategoryId;	
	/**
	 * @uml.property  name="attr_seq"
	 */
	int attr_seq;
	/**
	 * @uml.property  name="attr_nm"
	 */
	String attr_nm;
	/**
	 * @uml.property  name="synonymName"
	 */
	String synonymName;
	/**
	 * @uml.property  name="synonymType"
	 */
	String synonymType;
	/**
	 * @uml.property  name="writer"
	 */
	String writer;
	
	/**
	 * @return
	 * @uml.property  name="writer"
	 */
	public String getWriter() {
		return writer;
	}
	/**
	 * @param writer
	 * @uml.property  name="writer"
	 */
	public void setWriter(String writer) {
		this.writer = writer;
	}
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
	 * @uml.property  name="synonymName"
	 */
	public String getSynonymName() {
		return synonymName;
	}
	/**
	 * @param synonymName
	 * @uml.property  name="synonymName"
	 */
	public void setSynonymName(String synonymName) {
		this.synonymName = synonymName;
	}
	/**
	 * @return
	 * @uml.property  name="synonymType"
	 */
	public String getSynonymType() {
		return synonymType;
	}
	/**
	 * @param synonymType
	 * @uml.property  name="synonymType"
	 */
	public void setSynonymType(String synonymType) {
		this.synonymType = synonymType;
	}
	/**
	 * @return
	 * @uml.property  name="attr_seq"
	 */
	public int getAttr_seq() {
		return attr_seq;
	}
	/**
	 * @param attr_seq
	 * @uml.property  name="attr_seq"
	 */
	public void setAttr_seq(int attr_seq) {
		this.attr_seq = attr_seq;
	}
	/**
	 * @return
	 * @uml.property  name="attr_nm"
	 */
	public String getAttr_nm() {
		return attr_nm;
	}
	/**
	 * @param attr_nm
	 * @uml.property  name="attr_nm"
	 */
	public void setAttr_nm(String attr_nm) {
		this.attr_nm = attr_nm;
	}
	
}
