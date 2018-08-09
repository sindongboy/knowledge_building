package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 지식구축(확장) 시 표현의 빈도와 긍부정도를 저장하는 클래스
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	ExprData.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class ExprData {
	/**
	 * @uml.property  name="count"
	 */
	int count;
	/**
	 * @uml.property  name="value"
	 */
	String value;

	String text;
	/**
	 * @return
	 * @uml.property  name="count"
	 */
	public int getCount() {
		return count;
	}
	/**
	 * @param count
	 * @uml.property  name="count"
	 */
	public void setCount(int count) {
		this.count = count;
	}
	/**
	 * @return
	 * @uml.property  name="value"
	 */
	public String getValue() {
		return value;
	}
	/**
	 * @param value
	 * @uml.property  name="value"
	 */
	public void setValue(String value) {
		this.value = value;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	
	
}
