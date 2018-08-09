package com.skplanet.omp.knowledgeBuilding.command;

import java.util.ArrayList;

/**
 * 지식구축 요청내역을 저장하기 위해 사용되는 자료구조
 * @version  	0.1
 * @since  	2012.01.16
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	AnalysisStatus.java
 * @history  2012.01.16	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class AnalysisStatus {
	/**
	 * @uml.property  name="categoryId"
	 */
	String CategoryId;	
	/**
	 * @uml.property  name="statusType"
	 */
	String StatusType;
	/**
	 * @uml.property  name="cluewords"
	 */
	String Cluewords;
	/**
	 * @uml.property  name="crawlingCategoryIDs"
	 */
	String CrawlingCategoryIDs;
	/**
	 * @uml.property  name="crawlingSiteSeqs"
	 */
	String CrawlingSiteSeqs;
	/**
	 * @uml.property  name="crawlwords"
	 */
	String Crawlwords;
	
	/**
	 * @uml.property  name="CategoryName"
	 */
	String CategoryName;

	/**
	 * @uml.property  name="searchwords"
	 */
	String Searchwords;
		
	/**
	 * @uml.property  name="SearchObjectDictUse"
	 */
	String SearchObjectDictUse;
	
	/**
	 * @uml.property  name="naverShopping"
	 */
	String naverShopping;
	
	/**
	 * @uml.property  name="searchCategoryNameUse"
	 */
	String searchCategoryNameUse;
	
	/**
	 * @uml.property  name="extractAspectFromClue"
	 */
	private String extractAspectFromClue;
	
	/**
	 * @uml.property  name="extractAspectFromExpr"
	 */
	private String extractAspectFromExpr;
	
	/**
	 * @uml.property  name="extractPattern1"
	 */
	private String extractPattern1;
	
	/**
	 * @uml.property  name="extractPattern2"
	 */
	private String extractPattern2;
	
	/**
	 * @uml.property  name="extractPattern3"
	 */
	private String extractPattern3;
		
	
	public String getExtractPattern1() {
		return extractPattern1;
	}

	public void setExtractPattern1(String extractPattern1) {
		this.extractPattern1 = extractPattern1;
	}

	public String getExtractPattern2() {
		return extractPattern2;
	}

	public void setExtractPattern2(String extractPattern2) {
		this.extractPattern2 = extractPattern2;
	}

	public String getExtractPattern3() {
		return extractPattern3;
	}

	public void setExtractPattern3(String extractPattern3) {
		this.extractPattern3 = extractPattern3;
	}

	public String getExtractAspectFromClue() {
		return extractAspectFromClue;
	}

	public void setExtractAspectFromClue(String extractAspectFromClue) {
		this.extractAspectFromClue = extractAspectFromClue;
	}

	public String getExtractAspectFromExpr() {
		return extractAspectFromExpr;
	}

	public void setExtractAspectFromExpr(String extractAspectFromExpr) {
		this.extractAspectFromExpr = extractAspectFromExpr;
	}

	public String getSearchCategoryNameUse() {
		return searchCategoryNameUse;
	}

	public void setSearchCategoryNameUse(String searchCategoryNameUse) {
		this.searchCategoryNameUse = searchCategoryNameUse;
	}

	public String getNaverShopping() {
		return naverShopping;
	}

	public void setNaverShopping(String naverShopping) {
		this.naverShopping = naverShopping;
	}

	public String getSearchObjectDictUse() {
		return SearchObjectDictUse;
	}

	public void setSearchObjectDictUse(String searchObjectDictUse) {
		SearchObjectDictUse = searchObjectDictUse;
	}

	public String getSearchwords() {
		return Searchwords;
	}
	
	public void setSearchwords(String Searchwords) {
		this.Searchwords = Searchwords;
	}
	
	public String getCategoryName() {
		return CategoryName;
	}
	public void setCategoryName(String categoryName) {
		CategoryName = categoryName;
	}
	/**
	 * @return
	 * @uml.property  name="crawlingCategoryIDs"
	 */
	public String getCrawlingCategoryIDs() {
		return CrawlingCategoryIDs;
	}
	/**
	 * @param crawlingCategoryIDs
	 * @uml.property  name="crawlingCategoryIDs"
	 */
	public void setCrawlingCategoryIDs(String crawlingCategoryIDs) {
		CrawlingCategoryIDs = crawlingCategoryIDs;
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
	 * @uml.property  name="statusType"
	 */
	public String getStatusType() {
		return StatusType;
	}
	/**
	 * @param statusType
	 * @uml.property  name="statusType"
	 */
	public void setStatusType(String statusType) {
		StatusType = statusType;
	}
	/**
	 * @return
	 * @uml.property  name="cluewords"
	 */
	public String getCluewords() {
		return Cluewords;
	}
	/**
	 * @param cluewords
	 * @uml.property  name="cluewords"
	 */
	public void setCluewords(String cluewords) {
		Cluewords = cluewords;
	}
	/**
	 * @return
	 * @uml.property  name="crawlingSiteSeqs"
	 */
	public String getCrawlingSiteSeqs() {
		return CrawlingSiteSeqs;
	}
	/**
	 * @param crawlingSiteSeqs
	 * @uml.property  name="crawlingSiteSeqs"
	 */
	public void setCrawlingSiteSeqs(String crawlingSiteSeqs) {
		CrawlingSiteSeqs = crawlingSiteSeqs;
	}
	/**
	 * @return
	 * @uml.property  name="crawlwords"
	 */
	public String getCrawlwords() {
		return Crawlwords;
	}
	/**
	 * @param crawlwords
	 * @uml.property  name="crawlwords"
	 */
	public void setCrawlwords(String crawlwords) {
		Crawlwords = crawlwords;
	}	
}
