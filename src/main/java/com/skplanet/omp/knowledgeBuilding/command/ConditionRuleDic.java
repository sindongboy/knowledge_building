package com.skplanet.omp.knowledgeBuilding.command;

/** 
 * 조건을 추출 하기 위해 조건 rule이 데이터를 로드하기 위해 사용되는 자료구조.
 * 
 * @version	0.1
 * @since	2012.01.27
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	ConditionRuleDic.java
 * @history  2012.01.27	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 * 
 */
public class ConditionRuleDic {

	/**
	 * @uml.property  name="value"
	 */
	private String[] values;

	/** 
	 * Getter of the property <tt>value</tt>
	 * @return  Returns the values.
	 * @uml.property  name="value"
	 */
	public String[] getValue() {
		return values;
	}

	/** 
	 * Setter of the property <tt>value</tt>
	 * @param value  The values to set.
	 * @uml.property  name="value"
	 */
	public void setValue(String[] value) {
		values = value;
	}

	/**
	 * @uml.property  name="morph"
	 */
	private String[] morphs;

	/**
	 * Getter of the property <tt>morph</tt>
	 * @return  Returns the morphs.
	 * @uml.property  name="morph"
	 */
	public String[] getMorph() {
		return morphs;
	}

	/**
	 * Setter of the property <tt>morph</tt>
	 * @param morph  The morphs to set.
	 * @uml.property  name="morph"
	 */
	public void setMorph(String[] morph) {
		morphs = morph;
	}

}
