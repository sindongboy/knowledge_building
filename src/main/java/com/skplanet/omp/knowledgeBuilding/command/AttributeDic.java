package com.skplanet.omp.knowledgeBuilding.command;

import java.util.List;

/**
 * 속성을 추출 하기 위해 속성 사전 데이터를 로드하기 위해 사용되는 자료구조.
 * @version  	0.1
 * @since  	2012.01.27
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	AttributeDic.java
 * @history   2012.01.27	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 */
public class AttributeDic {

	/**
	 * @uml.property  name="value"
	 */
	private List<AttrData> attrData;

	/**
	 * @return
	 * @uml.property  name="value"
	 */
	public List<AttrData> getAttrData() {
		return attrData;
	}

	/**
	 * @param attrData
	 * @uml.property  name="value"
	 */
	public void setAttrData(List<AttrData> attrData) {
		this.attrData = attrData;
	}
}
