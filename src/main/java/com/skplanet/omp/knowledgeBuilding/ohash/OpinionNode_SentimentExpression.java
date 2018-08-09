/**
 * 
 */
package com.skplanet.omp.knowledgeBuilding.ohash;


/**
 * 
 * @author kardozo
 *
 */
public class OpinionNode_SentimentExpression
{
	/**
	 * 
	 */
	
	private String expression_value						= null;
	
	public OpinionNode_SentimentExpression(String value)
	{
		init(value);
	}
	
	public void init(String value)
	{
		this.expression_value							= value;
	}

	public String getExpressionValue()
	{		
		return expression_value;
	}
	
	public void setExpressionValue(String value)
	{
		this.expression_value							= value;
	}
}
