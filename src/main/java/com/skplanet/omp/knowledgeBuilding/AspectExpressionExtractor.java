package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.omp.knowledgeBuilding.Database.DBManager;
import com.skplanet.omp.knowledgeBuilding.command.ExprData;
import com.skplanet.omp.knowledgeBuilding.dict.SentimentDict;
import com.skplanet.omp.knowledgeBuilding.ohash.OpinionNode_SentimentExpression;
import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.morph.MorphAnalyzer;
import com.skplanet.nlp.morph.Morphs;
import com.skplanet.nlp.phrase.PhraseCodes;
import com.skplanet.nlp.phrase.PhraseItem;
import com.skplanet.nlp.utils.UtilTimer;


/**
 * 속성-표현후보 추출기 - 분석대상문서와 추출된 속성후보를 이용하여 속성-표현 후보를 추출하는 프로그램<P>
 *
 * @version   0.1
 * @since   2012.09.03
 * @author   한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier   한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file   AspectExpressionExtractor.java
 * @history   2012.09.03 * v0.1 클래스 최초 생성. 한영섭.<br>
 * @history   2012.11.05 * v0.1 주석 업데이트. 한영섭.<br>
 * @history   2012.12.28* v0.1 정도부사 출현시 윈도우 사이즈 증가 로직 업데이트. 한영섭.<br>
 * @history   2013.02.25 * v0.1 Null 속성 표현 추출 로직 추가. 한영섭.<br>
 * @history   2013.03.18 * v0.1 주석 업데이트. 한영섭.<br>
 */
public class AspectExpressionExtractor
{
    /**
     *  표현1을 저장하기 위한 Treemap
     */
    public static TreeMap<String, ExprData> map;

    /**
     *  표현2를 저장하기 위한 Treemap
     */
    public static TreeMap<String, Integer> mapMissed;

    /**
     *  OLD표현를 저장하기위한 Treeset
     */
    public static TreeSet<String> mapOldExpr;

    /**
     *  표현1에 해당하는 원문을 저장하기위한 HashMap
     */
    public static HashMap<String, String> mapClueAttr;

    /**
     *  표현2에 해당하는 원문을 저장하기위한 HashMap
     */
    public static HashMap<String, String> mapClueAttrMissed;

    /**
     *  NLP 분석 결과를 저장하기 위한 List
     */
    public static List<NLPDoc>  docs;

    /**
     *  분석대상 문서의 전체 term 카운트를 계산하기 위한 static 변수
     */
    public static int termcnt;

    /**
     *  분석되는 해당 카테고리 명(번호)를 저장하는 변수
     */
    public static String category;

    /**
     *  속성 형태소 목록 HashSet
     */
    public static HashSet<String> attrNOUNpos;

    /**
     *  조사 목록 HashSet
     */
    public static HashSet<String> attrJKtext;

    /**
     *  불용 속성 목록 HashSet
     */
    public static HashSet<String> attrStopword;

    /**
     *  분석시 추출된 속성 후보와 빈도 HashMap
     */
    public static HashMap<String, Integer> attrmap;

    /**
     *  속성 후보 리스트 TreeSet
     */
    public static TreeSet<String> treeAttr;

    /**
     *  CLUE 리스트 HashSet
     */
    public static TreeSet<String> clue;

    /**
     * NLP 분석을 위한 NLPAPI 객체
     * @uml.property  name="nlpapi"
     * @uml.associationEnd
     */
    public static NLPAPI nlpapi;

    /**
     * 감성사전 형태의 일반표현(GENERAL)
     * @uml.property  name="sentimentDic"
     * @uml.associationEnd
     */
    public static SentimentDict sentimentDic;

    /**
     *  속성동의어 목록1 HashMap (속성,동의어)
     */
    public static HashMap<String, TreeSet<String>> mapAttrSynm;

    /**
     *  속성동의어 목록2 HashMap (동의어,속성)
     */
    public static TreeMap<String, String> mapAttrAll;

    /**
     *  속성동의어 전체 목록
     */
    public static TreeSet<String> attrList;

    /**
     *  대표속성 목록 TreeMap
     */
    public static TreeMap<String, String> mapRepAttr;

    /**
     * DB 접속을 위한 DBManager 객체
     * @uml.property  name="dbm"
     * @uml.associationEnd
     */
    public static DBManager dbm;

    /**
     * AspectExpressionExtractor의 생성자.<br>
     * 초기 세팅을 위해 init()을 수행한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public AspectExpressionExtractor()
    {
        try
        {
            init();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 지식구축을 위한 초기 세팅<br>
     *  - nlp 객체 초기화<br>
     *  - 추출된 지식을 저장하기 위한 MAP 초기화<br>
     *  - 표현을 추출하기 위해 사용되는 '조사' 초기화 <br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void init() throws Exception
    {

        nlpapi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);

        // in,out file object
        map  = new TreeMap< String , ExprData >( );
        mapMissed = new TreeMap< String , Integer >( );
        mapClueAttr  = new HashMap< String , String >( );
        mapClueAttrMissed = new HashMap< String , String >( );
        mapOldExpr   = new TreeSet<String>();
        attrmap  = new HashMap< String , Integer >( );
        treeAttr  = new TreeSet<String>();
        clue   = new TreeSet<String>();
        sentimentDic  = new SentimentDict();
        attrList  = new TreeSet<String>();
        mapAttrSynm  = new HashMap< String , TreeSet<String> >( );
        mapAttrAll  = new TreeMap< String , String >( );
        attrJKtext  = new HashSet<String>();
        attrStopword = new HashSet<String>();
        attrNOUNpos  = new HashSet<String>();
// dbm  = new DBManager();

        attrNOUNpos.addAll(Arrays.asList("nng,nnp,nnk,eng,xsn,nnb,sp".split(",")));
        attrJKtext.addAll(Arrays.asList("을,은,는,이,가,도,까지,면에서,측면에서,에,에도,에는,에서는,에서도".split(",")));
        attrStopword.addAll(Arrays.asList("경우".split(",")));
    }


    /**
     * 형태소 분석된 문서와 추출된 aspect를 이용하여 속성-표현 후보를 추출한다.<br>
     * 1. 형태소정보을 기반으로 추출<br>
     * 2. 청킹정보를 기반으로 추출<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void extractAspectExpression() throws IOException
    {
        String attr, attrChunk, contextFull;

        Iterator<NLPDoc> docit =  docs.iterator();

        while(docit.hasNext())
        {
            NLPDoc doc = docit.next();

            contextFull  = new String(doc.getText());

            for(int i=0;i<doc.getMorphs().getCount()-1;i++){
                PhraseItem chunkItem = doc.getMorphs().getMorph(i).getChunkItem();

                attrChunk = "";
                attr = "";

                if(chunkItem != null)
                {
                    if(chunkItem.getPosStr().equals("ne")||chunkItem.getPosStr().equals("ncp")){
                        attrChunk = chunkItem.getKeyword();
                    }
                }

                if(attrNOUNpos.contains(doc.getMorphs().getMorph(i).getPosStr()))
                {
                    if(!doc.getMorphs().getMorph(i).getPosStr().equals("sp") && !doc.getMorphs().getMorph(i).getTextStr().equals(" "))
                    {
                        attr = doc.getMorphs().getMorph(i).getTextStr().trim();
                    }

                    if(attrStopword.contains(attr))
                        continue;

                    if(i-1 > 0){
                        for(int b=i-1;b>=0;b--){
                            if(attrNOUNpos.contains(doc.getMorphs().getMorph(b).getPosStr())){
                                if(doc.getMorphs().getMorph(b).getPosStr().equals("sp"))
                                {
                                    if((!doc.getMorphs().getMorph(b).getTextStr().contains(".")) || (!doc.getMorphs().getMorph(b).getTextStr().contains("-"))|| (!doc.getMorphs().getMorph(b).getTextStr().contains("/"))|| (!doc.getMorphs().getMorph(b).getTextStr().contains(",")))
                                    { //|| (!doc.getMorphs().getMorph(b).getTextStr().contains("-")))
                                        break;
                                    }
                                }
                                if(attrStopword.contains(doc.getMorphs().getMorph(b).getTextStr().toString()))
                                    continue;
                                if(doc.getTokens().getToken(doc.getMorphs().getMorph(b).getTokenNumber()).getNumber() == doc.getTokens().getToken(doc.getMorphs().getMorph(b+1).getTokenNumber()).getNumber()){
                                    attr = doc.getMorphs().getMorph(b).getTextStr().toString().trim()+attr;
                                }
                                else
                                {
                                    attr = doc.getMorphs().getMorph(b).getTextStr().toString().trim()+" "+attr;
                                }
                                attr = attr.trim();
                            }else{
                                break;
                            }
                        }
                    }

                    if(i < doc.getMorphs().getCount()-1){
                        for(int b=i+1;b<=doc.getMorphs().getCount()-1;b++){
                            if(attrNOUNpos.contains(doc.getMorphs().getMorph(b).getPosStr())){
                                i++;
                                if(doc.getMorphs().getMorph(b).getPosStr().equals("sp"))
                                {
                                    if((!doc.getMorphs().getMorph(b).getTextStr().contains(".")) || (!doc.getMorphs().getMorph(b).getTextStr().contains("-")) || (!doc.getMorphs().getMorph(b).getTextStr().contains("/")) || (!doc.getMorphs().getMorph(b).getTextStr().contains(",")))
                                    { //|| (!doc.getMorphs().getMorph(b).getTextStr().contains("-")))

                                        break;
                                    }
                                }
                                if(attrStopword.contains(doc.getMorphs().getMorph(b).getTextStr().toString()))
                                    continue;
                                if(doc.getTokens().getToken(doc.getMorphs().getMorph(b).getTokenNumber()).getNumber() == doc.getTokens().getToken(doc.getMorphs().getMorph(b-1).getTokenNumber()).getNumber()){
                                    attr = attr+doc.getMorphs().getMorph(b).getTextStr().toString().trim();
                                }
                                else
                                {
                                    attr = attr+" "+doc.getMorphs().getMorph(b).getTextStr().toString().trim();
                                }
                                attr = attr.trim();
                            }else{
                                break;
                            }
                        }
                    }


                    if(treeAttr.contains(attr)||treeAttr.contains(attrChunk))
                    {
                        if(i>2){
                            if(doc.getMorphs().getMorph(i-1).getPosStr().equals("etm") && (doc.getMorphs().getMorph(i-2).getPosStr().contains("va") || doc.getMorphs().getMorph(i-2).getPosStr().contains("vv")||doc.getMorphs().getMorph(i-2).getPosStr().contains("vcp")))
                            {
                                if(" 하 되 있 없 않 대하 위하 그렇 이렇 저렇 싶 보 같 게 기 지 음 ".contains(doc.getMorphs().getMorph(i-2).getTextStr())) continue;

                                //표현어 저장
                                if(doc.getMorphs().getMorph(i-2).getPosStr().contains("vcp")){
                                    if(doc.getMorphs().getMorph(i-3).getPosStr().contains("nng")){
                                        if(!attrChunk.equals(""))
                                        {
                                            do_save_context(attrChunk,doc.getMorphs().getMorph(i-3).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-3).getTokenNumber()).getText()), contextFull);
                                        }
                                        if(!attr.equals(""))
                                        {
                                            do_save_context(attr,doc.getMorphs().getMorph(i-3).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-3).getTokenNumber()).getText()), contextFull);
                                        }
                                    }
                                }
                                else
                                {
                                    if(!attrChunk.equals(""))
                                    {
                                        if(attrChunk.endsWith("면") && treeAttr.contains(attrChunk.substring(0, attrChunk.length()-1))){
                                            attrChunk = attrChunk.substring(0, attrChunk.length()-1);
                                        }
                                        do_save_context(attrChunk,doc.getMorphs().getMorph(i-2).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-2).getTokenNumber()).getText()), contextFull);
                                    }
                                    if(!attr.equals(""))
                                    {
                                        do_save_context(attr,doc.getMorphs().getMorph(i-2).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-2).getTokenNumber()).getText()), contextFull);
                                    }

                                }
                            }
                            //System.out.println("i = "+i);

                            for(int x = i-1;x > 0 && x > i-4;x--)
                            {
                                chunkItem = doc.getMorphs().getMorph(x).getChunkItem();

                                if( chunkItem != null )
                                {
                                    String chunkText = chunkItem.getKeyword();
                                    //if(chunkItem.getPosStr().equals("vv") || chunkItem.getPosStr().equals("va"))
                                    if((chunkItem.getType() == PhraseCodes.PHRASE_PREDI) || (chunkItem.getType() == PhraseCodes.PHRASE_GYESA))
                                    {

                                        if(!attrChunk.equals("")&& treeAttr.contains(attrChunk))
                                        {
                                            do_save_context(attrChunk, chunkText,  chunkText, contextFull);
                                        }
                                        if(!attr.equals("") && treeAttr.contains(attr))
                                        {
                                            do_save_context(attr, chunkText,  chunkText, contextFull);
                                        }
                                    }
                                }
                                chunkItem = null;
                            }
                        }

                        //if(attrJKtext.contains(doc.getMorphs().getMorph(i+1).getTextStr()))
                        //{
                        for(int l=i+2; l<doc.getMorphs().getCount()-1&&l<i+4; l++)
                        {
                            if(doc.getMorphs().getMorph(l).getPosStr().contains("va") || doc.getMorphs().getMorph(l).getPosStr().contains("vv"))
                            {
                                if(" 하 되 있 없 않 대하 위하 그렇 이렇 저렇 싶 보 같 게 기 지 음 ".contains(doc.getMorphs().getMorph(l).getTextStr())) continue;
                                //표현어 저장
                                if(!attrChunk.equals(""))
                                {
                                    do_save_context(attrChunk,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                }
                                if(!attr.equals(""))
                                {
                                    do_save_context(attr,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                }
                            }

                            if(attrNOUNpos.contains(doc.getMorphs().getMorph(l).getPosStr())&& doc.getMorphs().getMorph(l+1).getPosStr().contains("jk"))
                            {
                                if( doc.getMorphs().getMorph(l+1).getPosStr().contains("ec") || doc.getMorphs().getMorph(l+1).getPosStr().contains("ep") || doc.getMorphs().getMorph(l+1).getPosStr().contains("ss") )
                                {
                                    if(!attrChunk.equals(""))
                                    {
                                        do_save_context(attrChunk,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                    }
                                    if(!attr.equals(""))
                                    {
                                        do_save_context(attr,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                    }
                                }
                            }
                            chunkItem = doc.getMorphs().getMorph(l).getChunkItem();

                            if( chunkItem != null )
                            {
                                String chunkText = chunkItem.getKeyword();
                                //if(chunkItem.getPosStr().equals("vv") || chunkItem.getPosStr().equals("va"))
                                if((chunkItem.getType() == PhraseCodes.PHRASE_PREDI) || (chunkItem.getType() == PhraseCodes.PHRASE_GYESA))
                                {
                                    if(!attrChunk.equals(""))
                                    {
                                        do_save_context(attrChunk, chunkText,  chunkText, contextFull);
                                    }
                                    if(!attr.equals(""))
                                    {
                                        do_save_context(attr, chunkText,  chunkText, contextFull);
                                    }
                                    //break;
                                }
                            }
                            chunkItem = null;
                        }
                        //}
                    }
                }
            }
            contextFull = "";
            doc = null;
        }
    }

    /**
     * 형태소 분석된 문서와 CLUE를 이용하여 NULL속성-표현 후보를 추출한다.<br>
     * 1. 형태소정보을 기반으로 추출<br>
     * 2. 청킹정보를 기반으로 추출<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void extractNullExpression() throws IOException
    {
        String attr, attrChunk, contextFull;

        Iterator<NLPDoc> docit =  docs.iterator();

        while(docit.hasNext())
        {
            NLPDoc doc = docit.next();

            contextFull  = new String(doc.getText());

            for(int i=0;i<doc.getMorphs().getCount()-1;i++){
                PhraseItem chunkItem = doc.getMorphs().getMorph(i).getChunkItem();

                attrChunk = "";
                attr = "";

                if(chunkItem != null)
                {
                    if(chunkItem.getPosStr().equals("ne")||chunkItem.getPosStr().equals("ncp")){
                        attrChunk = chunkItem.getKeyword();
                    }
                }

                if(attrNOUNpos.contains(doc.getMorphs().getMorph(i).getPosStr()))
                {
                    if(!doc.getMorphs().getMorph(i).getPosStr().equals("sp") && !doc.getMorphs().getMorph(i).getTextStr().equals(" "))
                    {
                        attr = doc.getMorphs().getMorph(i).getTextStr().trim();
                    }

                    if(attrStopword.contains(attr))
                        continue;

                    if(i-1 > 0){
                        for(int b=i-1;b>=0;b--){
                            if(attrNOUNpos.contains(doc.getMorphs().getMorph(b).getPosStr())){
                                if(doc.getMorphs().getMorph(b).getPosStr().equals("sp"))
                                {
                                    if((!doc.getMorphs().getMorph(b).getTextStr().contains(".")) || (!doc.getMorphs().getMorph(b).getTextStr().contains("-"))|| (!doc.getMorphs().getMorph(b).getTextStr().contains("/"))|| (!doc.getMorphs().getMorph(b).getTextStr().contains(",")))
                                    { //|| (!doc.getMorphs().getMorph(b).getTextStr().contains("-")))
                                        break;
                                    }
                                }
                                if(attrStopword.contains(doc.getMorphs().getMorph(b).getTextStr().toString()))
                                    continue;
                                if(doc.getTokens().getToken(doc.getMorphs().getMorph(b).getTokenNumber()).getNumber() == doc.getTokens().getToken(doc.getMorphs().getMorph(b+1).getTokenNumber()).getNumber()){
                                    attr = doc.getMorphs().getMorph(b).getTextStr().toString().trim()+attr;
                                }
                                else
                                {
                                    attr = doc.getMorphs().getMorph(b).getTextStr().toString().trim()+" "+attr;
                                }
                                attr = attr.trim();
                            }else{
                                break;
                            }
                        }
                    }

                    if(i < doc.getMorphs().getCount()-1){
                        for(int b=i+1;b<=doc.getMorphs().getCount()-1;b++){
                            if(attrNOUNpos.contains(doc.getMorphs().getMorph(b).getPosStr())){
                                i++;
                                if(doc.getMorphs().getMorph(b).getPosStr().equals("sp"))
                                {
                                    if((!doc.getMorphs().getMorph(b).getTextStr().contains(".")) || (!doc.getMorphs().getMorph(b).getTextStr().contains("-"))|| (!doc.getMorphs().getMorph(b).getTextStr().contains("/"))|| (!doc.getMorphs().getMorph(b).getTextStr().contains(",")))
                                    { //|| (!doc.getMorphs().getMorph(b).getTextStr().contains("-")))

                                        break;
                                    }
                                }
                                if(attrStopword.contains(doc.getMorphs().getMorph(b).getTextStr().toString()))
                                    continue;
                                if(doc.getTokens().getToken(doc.getMorphs().getMorph(b).getTokenNumber()).getNumber() == doc.getTokens().getToken(doc.getMorphs().getMorph(b-1).getTokenNumber()).getNumber()){
                                    attr = attr+doc.getMorphs().getMorph(b).getTextStr().toString().trim();
                                }
                                else
                                {
                                    attr = attr+" "+doc.getMorphs().getMorph(b).getTextStr().toString().trim();
                                }
                                attr = attr.trim();
                            }else{
                                break;
                            }
                        }
                    }


                    if(clue.contains(attr)||clue.contains(attrChunk))
                    {
                        if(i>2){
                            if(doc.getMorphs().getMorph(i-1).getPosStr().equals("etm") && (doc.getMorphs().getMorph(i-2).getPosStr().contains("va") || doc.getMorphs().getMorph(i-2).getPosStr().contains("vv")||doc.getMorphs().getMorph(i-2).getPosStr().contains("vcp")))
                            {
                                if(" 하 되 있 없 않 대하 위하 그렇 이렇 저렇 싶 보 같 게 기 지 음 ".contains(doc.getMorphs().getMorph(i-2).getTextStr())) continue;

                                //표현어 저장
                                if(doc.getMorphs().getMorph(i-2).getPosStr().contains("vcp")){
                                    if(doc.getMorphs().getMorph(i-3).getPosStr().contains("nng")){
                                        if(!attrChunk.equals(""))
                                        {
                                            do_save_context("NULL",doc.getMorphs().getMorph(i-3).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-3).getTokenNumber()).getText()), contextFull);
                                        }
                                        if(!attr.equals(""))
                                        {
                                            do_save_context("NULL",doc.getMorphs().getMorph(i-3).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-3).getTokenNumber()).getText()), contextFull);
                                        }
                                    }
                                }
                                else
                                {
                                    if(!attrChunk.equals(""))
                                    {
                                        do_save_context("NULL",doc.getMorphs().getMorph(i-2).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-2).getTokenNumber()).getText()), contextFull);
                                    }
                                    if(!attr.equals(""))
                                    {
                                        do_save_context("NULL",doc.getMorphs().getMorph(i-2).getTextStr(), new String(doc.getTokens().getToken(doc.getMorphs().getMorph(i-2).getTokenNumber()).getText()), contextFull);
                                    }

                                }
                            }
                            //System.out.println("i = "+i);

                            for(int x = i-1;x > 0 && x > i-4;x--)
                            {
                                chunkItem = doc.getMorphs().getMorph(x).getChunkItem();

                                if( chunkItem != null )
                                {
                                    String chunkText = chunkItem.getKeyword();
                                    //if(chunkItem.getPosStr().equals("vv") || chunkItem.getPosStr().equals("va"))
                                    if((chunkItem.getType() == PhraseCodes.PHRASE_PREDI) || (chunkItem.getType() == PhraseCodes.PHRASE_GYESA))
                                    {

                                        if(!attrChunk.equals("")&& clue.contains(attrChunk))
                                        {
                                            do_save_context("NULL", chunkText,  chunkText, contextFull);
                                        }
                                        if(!attr.equals("") && clue.contains(attr))
                                        {
                                            do_save_context("NULL", chunkText,  chunkText, contextFull);
                                        }
                                    }
                                }
                                chunkItem = null;
                            }
                        }

                        //if(attrJKtext.contains(doc.getMorphs().getMorph(i+1).getTextStr()))
                        //{
                        for(int l=i+2; l<doc.getMorphs().getCount()-1&&l<i+4; l++)
                        {
                            if(doc.getMorphs().getMorph(l).getPosStr().contains("va") || doc.getMorphs().getMorph(l).getPosStr().contains("vv"))
                            {
                                if(" 하 되 있 없 않 대하 위하 그렇 이렇 저렇 싶 보 같 게 기 지 음 ".contains(doc.getMorphs().getMorph(l).getTextStr())) continue;
                                //표현어 저장
                                if(!attrChunk.equals(""))
                                {
                                    do_save_context(attrChunk,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                }
                                if(!attr.equals(""))
                                {
                                    do_save_context(attr,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                }
                            }

                            if(attrNOUNpos.contains(doc.getMorphs().getMorph(l).getPosStr())&& doc.getMorphs().getMorph(l+1).getPosStr().contains("jk"))
                            {
                                if( doc.getMorphs().getMorph(l+1).getPosStr().contains("ec") || doc.getMorphs().getMorph(l+1).getPosStr().contains("ep") || doc.getMorphs().getMorph(l+1).getPosStr().contains("ss") )
                                {
                                    if(!attrChunk.equals(""))
                                    {
                                        do_save_context(attrChunk,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                    }
                                    if(!attr.equals(""))
                                    {
                                        do_save_context(attr,doc.getMorphs().getMorph(l).getTextStr(),  new String(doc.getTokens().getToken(doc.getMorphs().getMorph(l).getTokenNumber()).getText()),contextFull);
                                    }
                                }
                            }
                            chunkItem = doc.getMorphs().getMorph(l).getChunkItem();

                            if( chunkItem != null )
                            {
                                String chunkText = chunkItem.getKeyword();
                                //if(chunkItem.getPosStr().equals("vv") || chunkItem.getPosStr().equals("va"))
                                if((chunkItem.getType() == PhraseCodes.PHRASE_PREDI) || (chunkItem.getType() == PhraseCodes.PHRASE_GYESA))
                                {
                                    if(!attrChunk.equals(""))
                                    {
                                        do_save_context("NULL", chunkText,  chunkText, contextFull);
                                    }
                                    if(!attr.equals(""))
                                    {
                                        do_save_context("NULL", chunkText,  chunkText, contextFull);
                                    }
                                    //break;
                                }
                            }
                            chunkItem = null;
                        }
                        //}
                    }
                }
            }
            contextFull = "";
            doc = null;
        }
    }

    /**
     * 추출된 속성-표현 후보를 원본 문장과 함께 저장한다.<br>
     * - HASH MAP을 사용하여 중복을 제거 하였음<br>
     * - 추출된 표현을 서비스에서 HIGHLIGHTING 하기 위해 구분자([[@],[@]])를 입력.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void do_save_context(String attr, String expr, String exprOrg, String context){
        ExprData exprData = new ExprData();
        attr = attr.toUpperCase();
        String clueExpr = attr+"\t"+expr+"다";
        String contextTemp = "";
        context = context.replaceAll("<[^<|>]*>", "");
        int count = 0;

        String attrList[] = attr.split(",");

        for(String attrOrg:attrList){

            if(attrOrg.endsWith("면") && treeAttr.contains(attrOrg.substring(0, attrOrg.length()-1))){
                attrOrg = attrOrg.substring(0, attr.length()-1).trim();
            }

            if(attrOrg.endsWith("측면") && treeAttr.contains(attrOrg.substring(0, attrOrg.length()-2))){
                attrOrg = attrOrg.substring(0, attrOrg.length()-2).trim();
            }

            //사전에 등록된 표현인 경우
            if(sentimentDic.getAttributes().get(0).roots.containsKey(expr+":en"))
            {
                OpinionNode_SentimentExpression exp = sentimentDic.getAttributes().get(0).roots.get(expr+":en");
                //표현이 이미 저장된 경우
                if(map.containsKey(clueExpr))
                {
                    exprData = map.get(clueExpr);
                    if(exprData.getCount() < 2)
                    {
                        exprData.setValue(exp.getExpressionValue());
                        exprData.setCount(exprData.getCount()+1);
                        map.remove(clueExpr);
                        map.put(clueExpr, exprData);
                        context = context.replace(exprOrg, " [[[@] "+exprOrg+ " [@]]] ");
                        contextTemp= mapClueAttr.get(clueExpr);
                        context = contextTemp+"^^^&^^^"+context;
                        mapClueAttr.remove(clueExpr);
                        mapClueAttr.put(clueExpr, context);
                        exprData = new ExprData();
                    }
                }
                else
                {
                    exprData = new ExprData();
                    exprData.setValue(exp.getExpressionValue());
                    exprData.setCount(1);
                    map.put(clueExpr, exprData);
                    context = context.replace(exprOrg, " [[@]  "+exprOrg+ " [@]]  ");
                    mapClueAttr.put(clueExpr,context);
                    exprData = new ExprData();
                }
            }
            else
            {
                if(mapMissed.containsKey(clueExpr))
                {
                    count = mapMissed.get(clueExpr);
                    if(count < 2)
                    {
                        count = count + 1;
                        mapMissed.remove(clueExpr);
                        mapMissed.put(clueExpr, count);
                        context = context.replace(exprOrg, " [[[@]  "+exprOrg+ "  [@]]]  ");
                        contextTemp= mapClueAttrMissed.get(clueExpr);
                        context = contextTemp+"^^^&^^^"+context;
                        mapClueAttrMissed.remove(clueExpr);
                        mapClueAttrMissed.put(clueExpr, context);
                    }
                }
                else
                {
                    mapMissed.put(clueExpr, 1);
                    context = context.replace(exprOrg, "  [[@]  "+exprOrg+ "  [@]]  ");
                    mapClueAttrMissed.put(clueExpr, context);
                }
            }
        }
    }


    /**
     * 청킹 TEXT 정보를 읽어오기 위한 임시 함수<br>
     * - getTextStr
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public String getChunkText(Morphs morphs, PhraseItem phrase)
    {
        int bom = phrase.getBom();
        int eom = phrase.getEom();

        boolean convedFlag = false;
        StringBuffer sbuffer = new StringBuffer();

        for (int m = bom; m <= eom; m++)
        {
            char[] conved = MorphAnalyzer.morphRevision(morphs, m, m + 1);

            if (m < eom && conved != null)
            {
                if (convedFlag)
                {
                    sbuffer.append(Arrays.copyOfRange(conved, 1,
                            conved.length));
                }
                else
                {
                    sbuffer.append(conved);
                }

                convedFlag = true;
            }
            else
            {
                char[] mText = morphs.getMorph(m).getText();

                if (convedFlag)
                {
                    sbuffer.append(Arrays.copyOfRange(mText, 1,
                            mText.length));
                }
                else
                {
                    sbuffer.append(mText);
                }
                convedFlag = false;
            }
        }

        return sbuffer.toString();
    }

    /**
     * 청킹 POSITION 정보를 읽어오기 위한 임시 함수<br>
     * - getPosStr
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public String getChunkPOS(PhraseItem chunkItem)
    {
        return new String(chunkItem.getPos());
    }

    /**
     * 속성, CLUE 등의 단어를 중복제거하여 MAP에 넣기 위한 함수<br>
     * 콤마(,)로 구분된 문자를 잘라 MAP에 넣는다.
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param filename
     * @exception none
     */
    public HashSet< String> setWordList(String filename) throws Exception, IOException{

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filename),"UTF-8"));
        HashSet< String> words = new HashSet< String>();
        String line = null;

        while((line = in.readLine()) != null)
        {
            words.add(line.split("\t")[0].trim());
        }

        return words;
    }

    /**
     * 분석 실행<br>
     * 1. 인코딩를 이용해 분석대상 문서를 읽는다.<br>
     * 2. 문서를 형태소 분석한다.<br>
     * 3. 50000문장 단위로 묶어서 지식을 추출한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param AspectExpressionExtractor, fileIn, encoding
     * @exception none
     */
    public void startAnalysis(AspectExpressionExtractor tester, String fileIn, String encoding) throws IOException
    {
        docs = new ArrayList<NLPDoc>();
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(fileIn),encoding);
        BufferedReader in = new BufferedReader(fileReader);

        String line = null;

        while((line = in.readLine()) != null)
        {
            docs.addAll(nlpapi.doAnalyze(line));

            if(docs.size() > 50000){
                System.out.println("Doc count : "+docs.size());
                tester.extractAspectExpression();
                tester.extractNullExpression();
                docs.clear();
            }
        }
        if(docs.size() > 0)
        {
            System.out.println("Doc count : "+docs.size());
            tester.extractAspectExpression();
            tester.extractNullExpression();
            docs.clear();
        }
        in.close();
    }

    /**
     * 대표속성 초기화<br>
     * - 분석시 대표속성을 처리하기 위해 대표속성 사전을 세팅한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileDict
     * @exception none
     */
    public void do_represent_mapper(String fileDict) throws IOException
    {
        System.out.println("call do_represent_mapper");
        BufferedReader in = new BufferedReader(new FileReader(fileDict));

        mapRepAttr  = new TreeMap< String , String >( );

        String line;
        String tabArray[] = null;


        while((line = in.readLine()) != null)
        {
            tabArray  = line.split("\t");

            if(mapRepAttr.containsKey(tabArray[0].toUpperCase()+"\t"+tabArray[1])){
                String repTemp = mapRepAttr.get(tabArray[0].toUpperCase()+"\t"+tabArray[1]);
                try
                {
                    if(repTemp.equals(tabArray[2]))
                    {
                        continue;
                    }
                }
                catch( ArrayIndexOutOfBoundsException e)
                {
                    continue;
                }
                repTemp = repTemp +","+  tabArray[2];
                mapRepAttr.remove(tabArray[0].toUpperCase()+"\t"+tabArray[1]);
                mapRepAttr.put(tabArray[0].toUpperCase()+"\t"+tabArray[1], repTemp);
                repTemp = "";
            }
            else
            {
                mapRepAttr.put(tabArray[0].toUpperCase()+"\t"+tabArray[1], tabArray[2]);
            }
        }
        in.close();
        System.out.println("end of do_represent_mapper");
    }

    /**
     * 동의어 초기화<br>
     * - 분석시 동의어를 처리하기 위해 동의어 사전을 세팅한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileDict
     * @exception none
     */
    public void do_synonym_mapper(String fileDict) throws IOException
    {
        System.out.println("call do_synonym_mapper");
        BufferedReader dic = new BufferedReader(new FileReader(fileDict));

        String line, attr, attrOrg, synmOrg;
        String tabArray[] = null;
        String comArray[] = null;

        mapAttrAll  = new TreeMap< String , String >( );

        while((line = dic.readLine()) != null)
        {
            tabArray  = line.split("\t");

            comArray = tabArray[1].split(",");

            for(String synm:comArray){
                synm = synm.toUpperCase();
                if(!synm.startsWith("{"))
                {
                    synmOrg = tabArray[0].trim().replace(" ", "");

                    if(mapAttrSynm.containsKey(synmOrg))
                    {
                        attrList.addAll(mapAttrSynm.get(synmOrg));
                        attrList.add(synm);
                        mapAttrSynm.put(synmOrg, attrList);
                        attrList = new TreeSet<String>();
                    }
                    else
                    {
                        if(tabArray[0].trim().contains(" ")) attrList.add(tabArray[0].trim());
                        attrList.add(synm);
                        mapAttrSynm.put(synmOrg, attrList);
                        attrList = new TreeSet<String>();
                    }
                }
                else
                {
                    attrOrg = synm.replace("{", "");
                    attrOrg = attrOrg.replace("}", "");
                    attr = attrOrg.replace(" ", "");
                    attr = attr.toUpperCase();
                    if(mapAttrSynm.containsKey(attr))
                    {
                        attrList.addAll(mapAttrSynm.get(attr));
                        if(!attrOrg.equals(attr)) attrList.add(attrOrg);
                        attrList.add(attr.trim());
                        mapAttrSynm.put(attr, attrList);
                        attrList = new TreeSet<String>();
                    }
                    else
                    {
                        if(!attrOrg.equals(attr)) attrList.add(attrOrg);
                        attrList.add(attr.trim());
                        mapAttrSynm.put(attr, attrList);
                        attrList = new TreeSet<String>();
                    }
                }
            }
        }
        for(Entry<String, TreeSet<String>> entry:mapAttrSynm.entrySet()){

            mapAttrAll.put(entry.getKey(),entry.getKey());

            for(String value:entry.getValue()){
                mapAttrAll.put(value,entry.getKey());
            }
        }
        System.out.println("end of do_synonym_mapper");
    }

    /**
     * OLD 표현 초기화<br>
     * 중복정재를 최소화 하기 위해 이전에 추출된 표현을 MAP에 저장한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileExprOld
     * @exception none
     */
    public boolean set_ExpressionOld(String fileExprOld) throws IOException
    {
        System.out.println("call set_ExpressionOld");

        String fileOldOut[] = {fileExprOld+".txt",fileExprOld+"_missed.txt",fileExprOld+"_conflict.txt",fileExprOld+"_missed_conflict.txt"};
        String line = null;
        String lineArray[] = null;
        InputStreamReader fileReader = null;

        for(String fileName:fileOldOut)
        {
            File file = new File(fileName);

            if(file.exists())
            {
                fileReader = new InputStreamReader(new FileInputStream(file.getPath()),"UTF-8");
                BufferedReader in = new BufferedReader(fileReader);
                while((line = in.readLine()) != null)
                {
                    lineArray = line.split("\t");
                    mapOldExpr.add(lineArray[0].toUpperCase()+"\t"+lineArray[1]);
                }
            }
        }
        if(mapOldExpr.size() > 0)
        {
            System.out.println("mapOldExpr's size > 0");
            return true;
        }
        else
        {
            System.out.println("mapOldExpr's size <= 0");
            return false;
        }


    }

    /**
     * 분석결과 저장 - 표현1<br>
     * 분석된 결과중 일반표현 사전과 매칭되어 긍부정도가 존재하는 속성-표현 후보를 저장한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileOut, fileConflict, fileExprOld
     * @exception none
     */
    public void saveResultMatched(String fileOut, String fileConflict, String fileExprOld, String runType)throws IOException{

        BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
        BufferedWriter conflict = new BufferedWriter(new FileWriter(fileConflict));
        BufferedWriter outOld = new BufferedWriter(new FileWriter(fileExprOld+".txt",true));
        BufferedWriter conflictOld = new BufferedWriter(new FileWriter(fileExprOld+"_conflict.txt",true));
        ExprData exprData  = new ExprData();
        String attr_expr, key, synm,repAttr;
        String keyValue[]  = null;
        String text  = "";
        String[] textTemps = null;
        String textTemp = "";

        for (Entry<String, ExprData> entry : map.entrySet()) {

            synm = "";
            repAttr = "";
            key = entry.getKey();
            keyValue = key.split("\t");
            if (mapAttrSynm.containsKey(keyValue[0])) {
                synm = mapAttrSynm.get(keyValue[0]).toString();
            }
            exprData = entry.getValue();
            attr_expr = mapClueAttr.get(key);

            if (mapRepAttr.containsKey(key)) {
                repAttr = mapRepAttr.get(key);
            } else {
                repAttr = "미할당";

                if (mapAttrSynm.containsKey(keyValue[0])) {
                    for (String synmTemp : mapAttrSynm.get(keyValue[0])) {
                        if (mapRepAttr.containsKey(synmTemp + "\t" + keyValue[1])) {
                            repAttr = mapRepAttr.get(synmTemp + "\t" + keyValue[1]);
                        }
                    }
                }
            }

            if (!mapOldExpr.contains(key)) {

                // 원문 중복처리
                textTemps = attr_expr.split("\\^\\^\\^\\&\\^\\^\\^");

                for (int i = 0; i < textTemps.length; i++) {
                    if (!textTemp.equals(textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[@\\]", "").replaceAll("\\[@\\]\\]\\]", "").replaceAll("\\[\\[@\\]", "").replaceAll("\\[@\\]\\]", ""))) {
                        if (!text.equals("")) {
                            if (runType.equals("Admin")) {
                                text = text + "^^^&^^^";
                            } else {
                                text = text + "\t";
                            }
                        }
                        if (runType.equals("Admin")) {
                            text = text + textTemps[i].toString();
                        } else {
                            text = text + textTemps[i].toString().replaceAll("\\[\\[\\[@\\]", "[").replaceAll("\\[@\\]\\]\\]", "]").replaceAll("\\[\\[@\\]", " \t").replaceAll("\\[@\\]\\]", " \t");
                        }
                    }
                    textTemp = textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[@\\]", "").replaceAll("\\[@\\]\\]\\]", "").replaceAll("\\[\\[@\\]", "").replaceAll("\\[@\\]\\]", "");
                }

                out.append(key + "\t" + synm + "\t" + exprData.getValue() + "\t" + repAttr + "\t" + category + "\t" + text.replaceAll("\"", "") + " \n");
                outOld.append(key + "\t" + synm + "\t" + exprData.getValue() + "\t" + repAttr + "\t" + category + "\t" + text.replaceAll("\"", "") + " \n");

                out.flush();
                outOld.flush();

                text = "";
                textTemp = "";
                textTemps = null;
            } else {
                // 원문 중복처리
                textTemps = attr_expr.split("\\^\\^\\^\\&\\^\\^\\^");

                for (int i = 0; i < textTemps.length; i++) {
                    if (!textTemp.equals(textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[@\\]", "").replaceAll("\\[@\\]\\]\\]", "").replaceAll("\\[\\[@\\]", "").replaceAll("\\[@\\]\\]", ""))) {
                        if (!text.equals("")) {
                            if (runType.equals("Admin")) {
                                text = text + "^^^&^^^";
                            } else {
                                text = text + "\t";
                            }
                        }
                        if (runType.equals("Admin")) {
                            text = text + textTemps[i].toString();
                        } else {
                            text = text + textTemps[i].toString().replaceAll("\\[\\[\\[@\\]", "[").replaceAll("\\[@\\]\\]\\]", "]").replaceAll("\\[\\[@\\]", "\t").replaceAll("\\[@\\]\\]", "\t");
                        }
                    }
                    textTemp = textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[@\\]", "").replaceAll("\\[@\\]\\]\\]", "").replaceAll("\\[\\[@\\]", "").replaceAll("\\[@\\]\\]", "");
                }

                conflict.append(key + "\t" + synm + "\t" + exprData.getValue() + "\t" + repAttr + "\t" + category + "\t" + text.replaceAll("\"", "") + " \n");
                conflictOld.append(key + "\t" + synm + "\t" + exprData.getValue() + "\t" + repAttr + "\t" + category + "\t" + text.replaceAll("\"", "") + " \n");

                conflict.flush();
                conflictOld.flush();

                text = "";
                textTemp = "";
                textTemps = null;
            }
        }
        conflict.close();
        out.close();
        conflictOld.close();
        outOld.close();
    }

    /**
     * 분석결과 저장 - 표현2<br>
     * 분석된 결과중 일반표현 사전과 매칭되지 않아 긍부정도가 존재하지 않는 속성-표현 후보를 저장한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileOut, fileConflict, fileExprOld
     * @exception none
     */
    public void saveResultMissed(String fileOut, String fileConflict, String fileExprOld, String runType)throws IOException{
        BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
        BufferedWriter conflict = new BufferedWriter(new FileWriter(fileConflict));
        BufferedWriter outOld = new BufferedWriter(new FileWriter(fileExprOld+"_missed.txt",true));
        BufferedWriter conflictOld = new BufferedWriter(new FileWriter(fileExprOld+"_missed_conflict.txt",true));

        String attr_expr, key, synm,repAttr;
        String keyValue[]  = null;
        String text  = "";
        String[] textTemps = null;
        String textTemp = "";

        for(Entry<String, Integer> entry:mapMissed.entrySet()){
            synm = "";
            repAttr = "";
            key = entry.getKey();
            keyValue = key.split("\t");
            if(mapAttrSynm.containsKey(keyValue[0])){
                synm = mapAttrSynm.get(keyValue[0]).toString();
            }
            attr_expr =mapClueAttrMissed.get(key);

            if(mapRepAttr.containsKey(key)){
                repAttr = mapRepAttr.get(key);
            }
            else
            {
                repAttr = "미할당";

                if(mapAttrSynm.containsKey(keyValue[0]))
                {
                    for(String synmTemp:mapAttrSynm.get(keyValue[0]))
                    {
                        if(mapRepAttr.containsKey(synmTemp+"\t"+keyValue[1]))
                        {
                            repAttr = mapRepAttr.get(synmTemp+"\t"+keyValue[1]);
                        }
                    }
                }
            }

            if(!mapOldExpr.contains(key)){

                // 원문 중복처리
                textTemps = attr_expr.split("\\^\\^\\^\\&\\^\\^\\^");

                for(int i = 0 ; i < textTemps.length ; i++ )
                {
                    if(!textTemp.equals(textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[@\\]", "").replaceAll("\\[@\\]\\]\\]", "").replaceAll("\\[\\[@\\]", "").replaceAll("\\[@\\]\\]", "")))
                    {
                        if(!text.equals(""))
                        {
                            if(runType.equals("Admin"))
                            {
                                text = text + "^^^&^^^";
                            }
                            else
                            {
                                text = text + "\t";
                            }
                        }
                        if(runType.equals("Admin"))
                        {
                            text = text + textTemps[i].toString();
                        }
                        else
                        {
                            text = text + textTemps[i].toString().replaceAll("\\[\\[\\[@\\]", "[").replaceAll("\\[@\\]\\]\\]", "]").replaceAll("\\[\\[@\\]", "\t").replaceAll("\\[@\\]\\]", "\t");
                        }
                    }
                    textTemp = textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[@\\]", "").replaceAll("\\[@\\]\\]\\]", "").replaceAll("\\[\\[@\\]", "").replaceAll("\\[@\\]\\]", "");
                }

                out.append(key+"\t"+synm+"\t"+repAttr+"\t"+category+"\t"+text.replaceAll("\"","")+" \n");
                outOld.append(key+"\t"+synm+"\t"+repAttr+"\t"+category+"\t"+text.replaceAll("\"","")+" \n");

                out.flush();
                outOld.flush();
                text = "";
                textTemp = "";
                textTemps = null;
            }
            else
            {
                // 원문 중복처리
                textTemps = attr_expr.split("\\^\\^\\^\\&\\^\\^\\^");

                for(int i = 0 ; i < textTemps.length ; i++ )
                {
                    if(!textTemp.equals(textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[\\@\\]", "").replaceAll("\\[\\@\\]\\]\\]", "").replaceAll("\\[\\[\\@\\]", "").replaceAll("\\[\\@\\]\\]", "")))
                    {
                        if(!text.equals(""))
                        {
                            if(runType.equals("Admin"))
                            {
                                text = text + "^^^&^^^";
                            }
                            else
                            {
                                text = text + "\t";
                            }
                        }
                        if(runType.equals("Admin"))
                        {
                            text = text + textTemps[i].toString();
                        }
                        else
                        {
                            text = text + textTemps[i].toString().replaceAll("\\[\\[\\[\\@\\]", "[").replaceAll("\\[\\@\\]\\]\\]", "]").replaceAll("\\[\\[\\@\\]", "\t").replaceAll("\\[\\@\\]\\]", "\t");
                        }
                    }
                    textTemp = textTemps[i].toString().replaceAll(" ", "").replaceAll("\\[\\[\\[\\@\\]", "").replaceAll("\\[\\@\\]\\]\\]", "").replaceAll("\\[\\[\\@\\]", "").replaceAll("\\[\\@\\]\\]", "");
                }

                conflict.append(key+"\t"+synm+"\t"+repAttr+"\t"+category+"\t"+text.replaceAll("\"","")+" \n");
                conflictOld.append(key+"\t"+synm+"\t"+repAttr+"\t"+category+"\t"+text.replaceAll("\"","")+" \n");

                conflict.flush();
                conflictOld.flush();

                text = "";
                textTemp = "";
                textTemps = null;
            }
        }
        conflict.close();
        out.close();
        conflictOld.close();
        outOld.close();
    }

    /**
     * 분석 main<br>
     * - 속성-표현 후보 추출 TEST를 위한 Main() 함수.<br>
     * -  실재로 동작하는 main()은 KnowledgeBuildingTransaction_Admin.java 과 KnowledgeBuildingTransaction_StandAlone.java에<br>
     *  존재한다. KnowledgeBuildingTransaction_Admin은 관리자 도구를 통해 자동으로 동작하는 배치 프로그램이고<br>
     *  KnowledgeBuildingTransaction_StandAlone.java 커맨드 형태로 동작하는 프로그램이다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param args
     * @exception none
     */
    public static void main(String[] args) throws Exception
    {
        long start_time = System.currentTimeMillis();

        // TODO Auto-generated method stub
        AspectExpressionExtractor AEE = new AspectExpressionExtractor();

        category = "motel";
        String MAINDIRECTORY   = "/Users/1002400/Documents/workspace/omp/knowledgeBuilding/";
// String dataDirectory  = MAINDIRECTORY + "/resource/data";
// String dataDirectory = "/Users/1002400/Documents/X-Life/to_xlife/comments";
// String dataDirectory = "/Users/1002400/Documents/X-Life/pickat_review/content_only";
        String dataDirectory = "/Users/1002400/Documents/DMP/accom/split_data/motel";
        String attrDirectoryFromClue = MAINDIRECTORY+"/resource/attr/attr_" + category + "_clue.txt";
        String attrDirectoryFromExpr  = MAINDIRECTORY+"/resource/attr/attr_" + category + "_expr.txt";
        String fileDict  = MAINDIRECTORY+"/resource/dict/";
        String fileMatched  = MAINDIRECTORY+"/resource/expr/new/"+category+".txt";
        String fileMissed  = MAINDIRECTORY+"/resource/expr/new/"+category+"_missed.txt";
        String fileMatchedConflict  = MAINDIRECTORY+"/resource/expr/new/"+category+"_conflict.txt";
        String fileMissedConflict  = MAINDIRECTORY+"/resource/expr/new/"+category+"_missed_conflict.txt";
        String fileExprOld = MAINDIRECTORY+"/resource/expr/old/"+category;

        sentimentDic.set_dict(fileDict+"GENERAL.txt", nlpapi);
        sentimentDic.print_dict();

        AEE.do_synonym_mapper(fileDict+"PICKAT_DICT_ALL_SYNM_MN_1227.txt");
        AEE.do_represent_mapper(fileDict+"RepAttr.txt");
        AEE.set_ExpressionOld(fileExprOld);

        //clue setting
// AspectExpressionExtractor.clue.addAll(AEE.setWordList(MAINDIRECTORY+"/clue/bmt/"+category+".clue"));

        //속성 세팅
        treeAttr.addAll(AEE.setWordList(attrDirectoryFromClue));
        treeAttr.addAll(AEE.setWordList(attrDirectoryFromExpr));

        System.out.println("Null Attr Count = "+AspectExpressionExtractor.clue.size());

        //속성수
        System.out.println("Attr size = "+treeAttr.size());

        File file = new File(dataDirectory);
        File[] subFile = file.listFiles();
        for (int i = 0; i < subFile.length; i++)
        {
            System.out.println(subFile[i].toString());
            AEE.startAnalysis(AEE, subFile[i].toString(),"UTF-8");
            docs.clear();
        }

        AEE.saveResultMatched(fileMatched,fileMatchedConflict, fileExprOld,"Admin");
        AEE.saveResultMissed(fileMissed,fileMissedConflict, fileExprOld,"Admin");

        System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024+"MB");
        long end_time = System.currentTimeMillis();
        System.out.println("Run time : "+UtilTimer.timeDiff(start_time,end_time));
    }

}
