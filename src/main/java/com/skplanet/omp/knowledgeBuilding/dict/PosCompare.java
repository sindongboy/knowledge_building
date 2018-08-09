package com.skplanet.omp.knowledgeBuilding.dict;

import com.skplanet.nlp.morph.Morphs;

import com.skplanet.nlp.phrase.Phrases;
import com.skplanet.nlp.sys.trie.TrieCodes;
import com.skplanet.nlp.sys.trie.htrie.HTrieNode;
import com.skplanet.nlp.sys.trie.htrie.HashTrie;
import com.skplanet.nlp.sys.ucs.UCSConverter;

public class PosCompare {
		
	public static byte[] pos_str_end = " cr ign ".getBytes();

	//서술어
	public static byte[] pos_str_v   = " vv va ".getBytes();
	
	//종경어미
	public static byte[] pos_str_e   = " ef etn ec ".getBytes();
	
    //의존명사,선어말 어미,접두사,점미사,지정사...
	public static byte[] pos_str_etc = " nnb ep xs xp vcp vcn cr ign sw ".getBytes();
	
    //명사
	//public static byte[] pos_str_n = " nng nnp nnk ".getBytes();
	public static byte[] pos_str_n = " nng nnp nnk ".getBytes();
	
	//원형복원
	public static byte[] pos_str_rv = " vv va vcp ".getBytes();
	
	

	//관형형 전성어미
	public static byte[] pos_str_etm = " etm ".getBytes();
		
	//private static byte[] pos_str_etc = " ep xs xp vcp vcn cr ign ".getBytes();	
	
	// 주제추출 대상
	public static byte[] pos_str_on = " nnK np nr nng nnp eng unk sn ".getBytes();
	
	// 주제어 추출 ( 주제어 추정시 뒤에붙는 품사 격조사, 보조사, 접속조사, 기타기호 )
	public static byte[] pos_str_j = " jks jkc jkg jko jkb jkv jkq jx jc sw ".getBytes();
	
	
	// 감성패턴 매칭 비추출 엔트리
	public static byte[] pos_str_sentiment = " sw ep ic emo jks ".getBytes();
	
	// 감성패턴 매칭 키워드 추출 엔트리
	public static byte[] pos_str_sentiment_n = " np nng nnp en unk sn ".getBytes();
	
	// geleral 사전 추출 엔트리
	public static byte[] pos_str_sentiment_g = " vv va ".getBytes();
	
	// phrases 분석 제외
	public static byte[] pos_str_sentiment_p = " ncp nsp ne ".getBytes();
	
	public PosCompare () 
	{
		
	}
	
	public static boolean is_pos_sentiment_p( byte[] pos)
	{
		if(UCSConverter.utf8IsExist(pos_str_sentiment_p, pos)) {
			//System.out.println( " IGNOR etc : " + new String(pos));
			return true;
		}
		return false;
	}
	
	public static boolean is_pos_sentiment_g( byte[] pos)
	{
		if(UCSConverter.utf8IsExist(pos_str_sentiment_g, pos)) {
			//System.out.println( " IGNOR etc : " + new String(pos));
			return true;
		}
		return false;
	}
	
	public static boolean isIgnorePosSentiemnt(byte[] pos)
	{
		/*
		//조사
    	if(pos[0]=='j') {
//System.out.println( " IGNOR J계열 : " + new String(pos));
    		return true;
    	}
    	*/
    	
    	//
    	if(UCSConverter.utf8IsExist(pos_str_sentiment, pos)) {
//System.out.println( " IGNOR etc : " + new String(pos));
    		return true;
    	}
    	
    	return false;
	}
	
	public static boolean is_pos_sentiment_n( byte[] pos)
	{
		if(UCSConverter.utf8IsExist(pos_str_sentiment_n, pos)) {
			//System.out.println( " IGNOR etc : " + new String(pos));
			return true;
		}
		return false;
	}
	
	public static boolean is_pos_object_hash_trie(byte[] pos)
	{
		if(UCSConverter.utf8IsExist(pos_str_j, pos)) {
			//System.out.println( " IGNOR etc : " + new String(pos));
			return true;
		}
		return false;
	}
	
	public static boolean is_pos_object_extractor(byte[] pos)
	{
		if(UCSConverter.utf8IsExist(pos_str_on, pos)) {
			//System.out.println( " IGNOR etc : " + new String(pos));
			return true;
		}
		return false;
	}
	
	public static boolean is_pos_sentiment_extractor_find_etm(byte[] pos) {
		//System.out.println( new String(pos) );

    	if(UCSConverter.utf8IsExist(pos_str_etm, pos)) {
//System.out.println( " IGNOR etc : " + new String(pos));
    		return true;
    	}
    	
    	return false;
	}
	
	public static boolean is_pos_sentiment_extractor_find_e(byte[] pos) {
		//System.out.println( new String(pos) );

    	//의존명사,선어말 어미,접두사,점미사,지정사...
    	if(UCSConverter.utf8IsExist(pos_str_e, pos)) {
//System.out.println( " IGNOR etc : " + new String(pos));
    		return true;
    	}
    	
    	return false;
	}
	
	public static boolean is_pos_sentiment_extractor_search(byte[] pos) {
		//System.out.println( new String(pos) );
		
		//조사
    	if(pos[0]=='j') {
//System.out.println( " IGNOR J계열 : " + new String(pos));
    		return true;
    	}
    	
    	//의존명사,선어말 어미,접두사,점미사,지정사...
    	if(UCSConverter.utf8IsExist(pos_str_etc, pos)) {
//System.out.println( " IGNOR etc : " + new String(pos));
    		return true;
    	}
    	
    	return false;
	}
	
	public static boolean is_pos_vr( byte[] pos ) {
		if(UCSConverter.utf8IsExist(pos_str_rv, pos)) {
    		return true;
    	}
    	
    	return false;
	}
	
	public static boolean is_pos_n( byte[] pos)
	{
    	if(UCSConverter.utf8IsExist(pos_str_n, pos)) {
    		return true;
    	}
    	
    	return false;
	}
	
	public static int is_pos_va(String pos)
	{	
		if( pos.equals("va") ) return 1;

    	return 0;
	}
	
	public static boolean is_ignore_pos(byte[] pos)
	{
		//System.out.println( new String(pos) );
		
		//조사
    	if(pos[0]=='j') {
//System.out.println( " IGNOR J계열 : " + new String(pos));
    		return true;
    	}
    	
    	//의존명사,선어말 어미,접두사,점미사,지정사...
    	if(UCSConverter.utf8IsExist(pos_str_etc, pos)) {
//System.out.println( " IGNOR etc : " + new String(pos));
    		return true;
    	}
    	
    	return false;
	}
	
	public static String regard_morph(Morphs morphs, int morph_number) 
	{
		StringBuffer sbuf = new StringBuffer();
		String str_tmp = null;

		
		String lpos  = ( morph_number > 0)? morphs.getMorph(morph_number-1).getPosStr() : "";
		String pos   = morphs.getMorph(morph_number).getPosStr();
		String rpos  = ( morph_number + 1 < morphs.getCount() )? morphs.getMorph(morph_number+1).getPosStr() : "";
		String rrpos = ( morph_number + 2 < morphs.getCount() )? morphs.getMorph(morph_number+2).getPosStr() : "";
		
		String morph  = morphs.getMorph(morph_number).getTextStr();
		String rmorph = ( morph_number + 1 < morphs.getCount() )? morphs.getMorph(morph_number+1).getTextStr(): null;
		
//System.out.println(" STR_POS : " + pos );
			
	    //종결어
		str_tmp = "ef";
		if( pos.length() >= str_tmp.length() ) {
			if( pos.substring(0, str_tmp.length()).compareToIgnoreCase(str_tmp.substring(0, str_tmp.length())) == 0 ) {
				sbuf.append(":en");      
//System.out.println("JCOMPARE 'ef' => " + sbuf);
	        	return sbuf.toString();
	    	}
		}
		

	    //긍정 지정사
		str_tmp = "vcp";
		if( pos.length() >= str_tmp.length() ) {
			if( pos.substring(0, str_tmp.length()).compareToIgnoreCase(str_tmp.substring(0, str_tmp.length())) == 0 ) {
				sbuf.append(morph);
				sbuf.append(":vc");			
//System.out.println("COMPARE 'vcp' => " + sbuf);
				return sbuf.toString();
			}
		}
		
		
	    //관형형 전성 어미
		str_tmp = "etm";
		if( pos.length() >= str_tmp.length() ) {
			if( pos.substring(0, str_tmp.length()).compareToIgnoreCase(str_tmp.substring(0, str_tmp.length())) == 0 ) {
		
				sbuf.append(":en");
//System.out.println("JCOMPARE 'etm' => " + sbuf);
				return sbuf.toString();
			}
		}
		
		//명사형 전성 어미
		str_tmp = "etn";
		if( pos.length() >= str_tmp.length() ) {
			if( pos.substring(0, str_tmp.length()).compareToIgnoreCase(str_tmp.substring(0, str_tmp.length())) == 0 ) {
				sbuf.append(":en");			
//System.out.println("COMPARE 'etn' => " + sbuf);
        		return sbuf.toString();
			}
		}
		
	    //연결 어미
		str_tmp = "ec";
		if( pos.length() >= str_tmp.length() ) {
			if( pos.substring(0, str_tmp.length()).compareToIgnoreCase(str_tmp.substring(0, str_tmp.length())) == 0 ) {
		
				sbuf.append(":en");			
//System.out.println("COMPARE 'ec' => " + sbuf);
        		return sbuf.toString();
			}
	    }
		
		str_tmp = "?";
		if( pos.length() >= str_tmp.length() ) {
			if( pos.substring(0, str_tmp.length()).compareToIgnoreCase(str_tmp.substring(0, str_tmp.length())) == 0 ) {
				sbuf.append(":qm");			
//System.out.println("COMPARE '?' => " + sbuf);
        		return sbuf.toString();		
			}
		}
		
		return null;
	}
}


