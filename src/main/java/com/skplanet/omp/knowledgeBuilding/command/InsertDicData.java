package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 지식사전을 DB에 저장하기 위한 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	InsertDicData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class InsertDicData {
	/**
	 * @uml.property  name="attributeId"
	 */
	int AttributeId;
	/**
	 * @uml.property  name="expressionName"
	 */
	String ExpressionName;
	/**
	 * @uml.property  name="expressionValue"
	 */
	String ExpressionValue;
	/**
	 * @uml.property  name="representationId"
	 */
	String RepresentationId;
	/**
	 * @uml.property  name="writer"
	 */
	String Writer;
	
	/**
	 * @return
	 * @uml.property  name="representationId"
	 */
	public String getRepresentationId() {
		return RepresentationId;
	}
	/**
	 * @param representationId
	 * @uml.property  name="representationId"
	 */
	public void setRepresentationId(String representationId) {
		RepresentationId = representationId;
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
	 * @uml.property  name="expressionName"
	 */
	public String getExpressionName() {
		return ExpressionName;
	}
	/**
	 * @param expressionName
	 * @uml.property  name="expressionName"
	 */
	public void setExpressionName(String expressionName) {
		ExpressionName = expressionName;
	}
	/**
	 * @return
	 * @uml.property  name="expressionValue"
	 */
	public String getExpressionValue() {
		return ExpressionValue;
	}
	/**
	 * @param expressionValue
	 * @uml.property  name="expressionValue"
	 */
	public void setExpressionValue(String expressionValue) {
		ExpressionValue = expressionValue;
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
