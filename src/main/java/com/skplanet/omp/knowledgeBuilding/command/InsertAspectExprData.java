package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 속성-표현을 사전에 저장하기 위해 사용되는 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	InsertAspectExprData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class InsertAspectExprData {
	public String getDomainCategoryId() {
		return domainCategoryId;
	}
	public void setDomainCategoryId(String domainCategoryId) {
		this.domainCategoryId = domainCategoryId;
	}
	/**
	 * @uml.property  name="attributeName"
	 */
	String AttributeName;
	/**
	 * @uml.property  name="categoryId"
	 */
	String CategoryId;
	/**
	 * @uml.property  name="expressionId"
	 */
	String ExpressionId;
	/**
	 * @uml.property  name="expressionName"
	 */
	String ExpressionName;
	/**
	 * @uml.property  name="expressionText"
	 */
	String ExpressionText;
	/**
	 * @uml.property  name="expressionType"
	 */
	String ExpressionType;
	/**
	 * @uml.property  name="expressionValue"
	 */
	String ExpressionValue;
	/**
	 * @uml.property  name="representationId"
	 */
	String RepresentationId;
	/**
	 * @uml.property  name="attributeSynonym"
	 */
	String AttributeSynonym;
	
	String domainCategoryId;
	
	
	/**
	 * @return
	 * @uml.property  name="attributeSynonym"
	 */
	public String getAttributeSynonym() {
		return AttributeSynonym;
	}
	/**
	 * @param attributeSynonym
	 * @uml.property  name="attributeSynonym"
	 */
	public void setAttributeSynonym(String attributeSynonym) {
		AttributeSynonym = attributeSynonym;
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
	 * @uml.property  name="expressionId"
	 */
	public String getExpressionId() {
		return ExpressionId;
	}
	/**
	 * @param expressionId
	 * @uml.property  name="expressionId"
	 */
	public void setExpressionId(String expressionId) {
		ExpressionId = expressionId;
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
	 * @uml.property  name="expressionText"
	 */
	public String getExpressionText() {
		return ExpressionText;
	}
	/**
	 * @param expressionText
	 * @uml.property  name="expressionText"
	 */
	public void setExpressionText(String expressionText) {
		ExpressionText = expressionText;
	}
	/**
	 * @return
	 * @uml.property  name="expressionType"
	 */
	public String getExpressionType() {
		return ExpressionType;
	}
	/**
	 * @param expressionType
	 * @uml.property  name="expressionType"
	 */
	public void setExpressionType(String expressionType) {
		ExpressionType = expressionType;
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
}
