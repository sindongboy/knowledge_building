package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 지식사전 데이터를 읽어오기 위한 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	SelectDicData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class SelectDicData {
	/**
	 * @uml.property  name="expressionName"
	 */
	String ExpressionName;
	/**
	 * @uml.property  name="expressionValue"
	 */
	String ExpressionValue;
	String RepresentationName;

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
}
