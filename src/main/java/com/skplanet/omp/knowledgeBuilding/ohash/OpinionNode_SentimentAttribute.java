/**
 * 
 */
package com.skplanet.omp.knowledgeBuilding.ohash;

import java.util.HashMap;
import java.util.Map;

import com.skplanet.nlp.NLPDoc;

/**
 * 속성별 map을 구성하기 위한 속성 클래스 
 * @author  kardozo
 */
public class OpinionNode_SentimentAttribute
{/**
	 * 
	 */
	public String attribute_name								= null;
	
	public String feature_name									= null;
				
	/**
	 * @uml.property  name="roots"
	 */
	public Map<String, OpinionNode_SentimentExpression> roots	= null;
	
	public OpinionNode_SentimentAttribute()
	{
		//init_sentiment_attr( null );
	}

	
	public OpinionNode_SentimentAttribute(String attr)
	{
		init_sentiment_attr( attr );
	}
	
	public void init_sentiment_attr( String attr ) 
	{
		String [] features = null;
		
		features = attr.split("\t");
				
		if( features.length == 1) {
			attribute_name		= features[0];
		} else if ( features.length == 2 ) {
			attribute_name		= features[0];
			feature_name		= features[1];
		}
		
		roots				= new HashMap<String, OpinionNode_SentimentExpression>(1);
	}
	
	/**
	 * @return
	 * @uml.property  name="roots"
	 */
	public Map<String, OpinionNode_SentimentExpression> getRoots( )
	{
		return this.roots;
	}
	
	public String getAttribute()
	{
		return this.attribute_name;
	}
	
	public String getFeature()
	{
		return this.feature_name;
	}
	
}
