package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 속성번호와 속성명을 로드하기 위해 사용되는 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	AttrMapData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class AttrMapData {
	/**
	 * @uml.property  name="attributeId"
	 */
	int AttributeId;
	/**
	 * @uml.property  name="synonymId"
	 */
	String SynonymId;
	/**
	 * @uml.property  name="synonymName"
	 */
	String SynonymName;
	/**
	 * @uml.property  name="writer"
	 */
	String Writer;
	
	/**
	 * @return
	 * @uml.property  name="synonymName"
	 */
	public String getSynonymName() {
		return SynonymName;
	}
	/**
	 * @param synonymName
	 * @uml.property  name="synonymName"
	 */
	public void setSynonymName(String synonymName) {
		SynonymName = synonymName;
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
	/**
	 * @return
	 * @uml.property  name="attributeId"
	 */
	public int getAttributeId() {
		return AttributeId;
	}
	/**
	 * @param attributeId
	 * @uml.property  name="attributeId"
	 */
	public void setAttributeId(int attributeId) {
		AttributeId = attributeId;
	}
	/**
	 * @return
	 * @uml.property  name="synonymId"
	 */
	public String getSynonymId() {
		return SynonymId;
	}
	/**
	 * @param synonymId
	 * @uml.property  name="synonymId"
	 */
	public void setSynonymId(String synonymId) {
		SynonymId = synonymId;
	}
}
