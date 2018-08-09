package com.skplanet.omp.knowledgeBuilding.command;

/**
 * 표현, 긍부정도, 동의표현, 속성필수여부 정보를 로드하기 위해 사용되는 자료구조
 * @version  	0.1
 * @since  	2012.01.27
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	DicDataSub.java
 * @history  2012.01.27	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class DicDataSub {
	/**
	 * @uml.property  name="exp_nm"
	 */
	String exp_nm;
	/**
	 * @uml.property  name="pos_neg_prcnt"
	 */
	String pos_neg_prcnt;
	/**
	 * @uml.property  name="syno"
	 */
	String syno;
	/**
	 * @uml.property  name="exp_essential_flag"
	 */
	String exp_essential_flag;
	
	/**
	 * @return
	 * @uml.property  name="exp_essential_flag"
	 */
	public String getExp_essential_flag() {
		return exp_essential_flag;
	}
	/**
	 * @param exp_essential_flag
	 * @uml.property  name="exp_essential_flag"
	 */
	public void setExp_essential_flag(String exp_essential_flag) {
		this.exp_essential_flag = exp_essential_flag;
	}
	/**
	 * @return
	 * @uml.property  name="syno"
	 */
	public String getSyno() {
		return syno;
	}
	/**
	 * @param syno
	 * @uml.property  name="syno"
	 */
	public void setSyno(String syno) {
		this.syno = syno;
	}
	/**
	 * @return
	 * @uml.property  name="exp_nm"
	 */
	public String getExp_nm() {
		return exp_nm;
	}
	/**
	 * @param exp_nm
	 * @uml.property  name="exp_nm"
	 */
	public void setExp_nm(String exp_nm) {
		this.exp_nm = exp_nm;
	}
	/**
	 * @return
	 * @uml.property  name="pos_neg_prcnt"
	 */
	public String getPos_neg_prcnt() {
		return pos_neg_prcnt;
	}
	/**
	 * @param pos_neg_prcnt
	 * @uml.property  name="pos_neg_prcnt"
	 */
	public void setPos_neg_prcnt(String pos_neg_prcnt) {
		this.pos_neg_prcnt = pos_neg_prcnt;
	}	
}
