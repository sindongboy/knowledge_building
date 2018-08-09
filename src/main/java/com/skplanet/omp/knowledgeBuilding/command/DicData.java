package com.skplanet.omp.knowledgeBuilding.command;

import java.util.List;

/**
 * 기 사전 데이터를 로드하기 위해 사용되는 자료구조.
 * @version     	0.1
 * @since     	2012.01.27
 * @author     	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier     	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file     	DicData.java
 * @history      2012.01.27	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 * @modifier   	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file   	DicData.java
 * @history     2012.01.27	 v0.1 *	클래스 최초 생성.	한영섭.<br>
 * @uml.dependency  supplier="NLP_API.Morph_Analyzer"
 */

public class DicData {


	/**
	 * @uml.property  name="value"
	 */
	List<DicDataSub> dicDataSub;

	/**
	 * @return
	 * @uml.property  name="value"
	 */
	public List<DicDataSub> getDicDataSub() {
		return dicDataSub;
	}

	/**
	 * @param dicDataSub
	 * @uml.property  name="value"
	 */
	public void setDicDataSub(List<DicDataSub> dicDataSub) {
		this.dicDataSub = dicDataSub;
	}
	
}
