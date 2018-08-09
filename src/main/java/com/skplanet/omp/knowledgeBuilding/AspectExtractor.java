package com.skplanet.omp.knowledgeBuilding;

import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.TreeSet;

import com.skplanet.nlp.config.Configuration;
import com.skplanet.omp.knowledgeBuilding.Database.DBManager;
import com.skplanet.omp.knowledgeBuilding.dict.SentimentDict;
import com.skplanet.omp.knowledgeBuilding.ohash.OpinionNode_SentimentAttribute;
import com.skplanet.omp.knowledgeBuilding.ohash.OpinionNode_SentimentExpression;
import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.phrase.PhraseItem;
import com.skplanet.nlp.utils.UtilTimer;

/**
 * 속성후보 추출기 - 분석대상문서와 CLUE, 일반표현사전을 이용하여 속성 후보를 추출하는 프로그램
 * <P>
 *
 * @version 0.4
 * @since 2012.09.03
 * @author 한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier 한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file AspectExtractor.java
 * @history 2012.09.03 * v0.1 클래스 최초 생성. 한영섭.<br>
 * @history 2012.11.05 * v0.2주석 업데이트. 한영섭.<br>
 * @history 2012.12.28* v0.3 정도부사 출현시 윈도우 사이즈 증가 로직 업데이트. 한영섭.<br>
 * @history 2013.03.18 * v0.4 주석 업데이트. 한영섭.<br>
 */
public class AspectExtractor {
    /**
     * 분석시 추출된 Clue기반 속성 후보와 빈도 TreeMap
     */
    public TreeMap<String, Integer> mapClueToAttr;

    /**
     * 분석시 추출된 Clue기반 속성 후보와 원문 TreeMap
     */
    public HashMap<String, String> mapClueToClueAttr;

    /**
     * 분석시 추출된 표현기반 속성 후보와 빈도 TreeMap
     */
    public TreeMap<String, Integer> mapExprToAttr;

    /**
     * 분석시 추출된 표현기반 속성 후보와 원문 TreeMap
     */
    public HashMap<String, String> mapExptToExprAttr;

    /**
     * NLP 분석 결과를 저장하기 위한 List
     */
    public List<NLPDoc> docs;

    /**
     * 분석대상 문서의 전체 clue 카운트를 계산하기 위한 static 변수
     */
    public int clueCount;

    /**
     * 분석대상 문서의 전체 term 카운트를 계산하기 위한 static 변수
     */
    public int termCount;

    /**
     * CLUE 리스트 HashSet
     */
    public HashSet<String> clue;

    /**
     * Clue 조사 목록 HashSet
     */
    public HashSet<String> clueJKtext;

    /**
     * 속성 형태소 목록 HashSet
     */
    public HashSet<String> clueNOUNpos;

    /**
     * 속성 조사 목록 HashSet
     */
    public HashSet<String> attrJKtext;

    /**
     * 분석시 추출된 속성과 빈도 HashMap
     */
    public HashMap<String, Integer> mapAttr;

    /**
     * 속성 불용어 목록 HashSet
     */
    public TreeSet<String> stopwords;

    /**
     * NLP 분석을 위한 NLPAPI 객체
     *
     * @uml.property name="nlpapi"
     * @uml.associationEnd
     */
    public NLPAPI nlpapi;

    /**
     * 감성사전 형태의 일반표현(GENERAL)
     *
     * @uml.property name="sentimentDic"
     * @uml.associationEnd
     */
    public SentimentDict sentimentDic;

    /**
     * DB 접속을 위한 DBManager 객체
     *
     * @uml.property name="dbm"
     * @uml.associationEnd
     */
//	public DBManager dbm;

    /**
     * AspectExtractor 생성자.<br>
     * 초기 세팅을 위해 init()을 수행한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public AspectExtractor() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 지식구축을 위한 초기 세팅<br>
     * - nlp 객체 초기화<br>
     * - 추출된 지식을 저장하기 위한 MAP 초기화<br>
     * - 표현을 추출하기 위해 사용되는 '조사', '형태소' 초기화 <br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void init() throws Exception {
        // NLP 객체 선언
        docs = new ArrayList<NLPDoc>();
        nlpapi = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);

        // 분석을 위한 MAP
        mapClueToAttr = new TreeMap<String, Integer>();
        mapClueToClueAttr = new HashMap<String, String>();
        mapExprToAttr = new TreeMap<String, Integer>();
        mapExptToExprAttr = new HashMap<String, String>();
        mapAttr = new HashMap<String, Integer>();
        clue = new HashSet<String>();
        stopwords = new TreeSet<String>();
        sentimentDic = new SentimentDict();
        clueJKtext = new HashSet<String>();
        attrJKtext = new HashSet<String>();
        clueNOUNpos = new HashSet<String>();

//		dbm = new DBManager();

        clueJKtext.addAll(Arrays.asList("에서,은,는,에는,이,에,의,으로".split(",")));
        attrJKtext.addAll(Arrays
                .asList("을,은,는,이,가,도,까지,면에서,측면에서,에,에도,에는,에서는,에서도".split(",")));
        clueNOUNpos.addAll(Arrays.asList("nng,nnp,nnk,eng,xsn,nnb,sp"
                .split(",")));
    }

    /**
     * 형태소 분석된 문서와 제공된 clue를 이용하여 aspect 후보를 추출한다.<br>
     * 1. 형태소정보을 기반으로 추출<br>
     * 2. 청킹정보를 기반으로 추출<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void extractAspectFromClue() throws IOException {
        String context, contextFull;
        String attrTemp = "";
        int morphCount = 0;
        int termCountTemp = 0;
        Iterator<NLPDoc> docit = this.docs.iterator();

        while (docit.hasNext()) {
            NLPDoc doc = docit.next();
            morphCount = doc.getMorphs().getCount();
            contextFull = new String(doc.getText());
            for (int i = 0; i < morphCount - 1; i++) {
                // 명사 + 조사
                if ((clueNOUNpos.contains(doc.getMorphs().getMorph(i)
                        .getPosStr()))
                        && doc.getMorphs().getMorph(i + 1).getPosStr()
                        .contains("jk")) {
                    // 전체 명사
                    termCountTemp++;
                    // clue + 조사
                    if (clue.contains(doc.getMorphs().getMorph(i).getTextStr())) {
                        if (clueJKtext.contains(doc.getMorphs().getMorph(i + 1)
                                .getTextStr())) {
                            // 전체 clue 수
                            clueCount++;
                            i = i + 2;
                            if (i <= morphCount - 3) {
                                PhraseItem chunkItem = doc.getMorphs()
                                        .getMorph(i).getChunkItem();
                                if (chunkItem != null) {
                                    String chunkText = chunkItem.getKeyword();

                                    if (chunkItem.getPosStr().equals("ne")
                                            || chunkItem.getPosStr().equals(
                                            "ncp")) {
                                        if (stopwords.contains(chunkText)) {
                                            break;
                                        }

                                        contextFull = contextFull.replaceAll(
                                                chunkText, "\t" + chunkText
                                                        + "\t");
                                        // 속성 후보 저장
                                        if (mapClueToAttr
                                                .containsKey(chunkText)) {
                                            int count = mapClueToAttr
                                                    .get(chunkText);
                                            if (count <= 4) {
                                                count = count + 1;
                                                mapClueToAttr.remove(chunkText);
                                                mapClueToAttr.put(chunkText,
                                                        count);
                                                context = mapClueToClueAttr
                                                        .get(chunkText);
                                                context = context + "^^&^^"
                                                        + contextFull;
                                                // mapClueToClueAttr.remove(attrTemp);
                                                mapClueToClueAttr.put(
                                                        chunkText, context);
                                            }
                                            context = "";
                                        } else {
                                            mapClueToAttr.put(chunkText, 1);
                                            mapClueToClueAttr.put(chunkText,
                                                    contextFull);
                                            context = "";
                                        }
                                        contextFull = new String(doc.getText());
                                        chunkText = "";
                                        chunkItem = null;
                                        break;
                                    }
                                    chunkText = "";
                                }
                                chunkItem = null;

                                if (stopwords.contains(doc.getMorphs()
                                        .getMorph(i).getTextStr())) {
                                    continue;
                                }
                                if (clueNOUNpos.contains(doc.getMorphs()
                                        .getMorph(i).getPosStr())) {
                                    if (!doc.getMorphs().getMorph(i)
                                            .getPosStr().equals("sp")
                                            && !doc.getMorphs().getMorph(i)
                                            .getTextStr().equals(" ")) {
                                        attrTemp = doc.getMorphs().getMorph(i)
                                                .getTextStr();
                                    }
                                    // 속성 후보 명사 추출 - 조사가 나올때까지 연속된 명사 추출
                                    for (int k = i + 1; k < morphCount - 1; k++) {
                                        if (clueNOUNpos.contains(doc
                                                .getMorphs().getMorph(k)
                                                .getPosStr())) {
											/*
											 * if(doc.getMorphs().getMorph(k).
											 * getPosStr().equals("sp")) {
											 * if((!doc
											 * .getMorphs().getMorph(k).getTextStr
											 * ().contains(".")) ||
											 * (!doc.getMorphs
											 * ().getMorph(k).getTextStr
											 * ().contains("-"))) { break; } }
											 */
                                            if (doc.getMorphs().getMorph(k - 1)
                                                    .getTokenNumber() == doc
                                                    .getMorphs().getMorph(k)
                                                    .getTokenNumber()) {
                                                attrTemp = attrTemp
                                                        + doc.getMorphs()
                                                        .getMorph(k)
                                                        .getTextStr();
                                            } else {
                                                attrTemp = attrTemp
                                                        + " "
                                                        + doc.getMorphs()
                                                        .getMorph(k)
                                                        .getTextStr();
                                            }
                                        } else {
                                            if (doc.getMorphs().getMorph(k)
                                                    .getPosStr().contains("jk")
                                                    && (attrJKtext.contains(doc
                                                    .getMorphs()
                                                    .getMorph(k)
                                                    .getTextStr()))) {
                                                // 문장 출력
                                                contextFull = contextFull
                                                        .replaceAll(
                                                                attrTemp.trim(),
                                                                "\t"
                                                                        + attrTemp
                                                                        .trim()
                                                                        + "\t");
                                                // 속성 후보 저장
                                                if (mapClueToAttr
                                                        .containsKey(attrTemp
                                                                .trim())) {
                                                    int count = mapClueToAttr
                                                            .get(attrTemp
                                                                    .trim());
                                                    if (count <= 4) {
                                                        count = count + 1;
                                                        mapClueToAttr
                                                                .remove(attrTemp
                                                                        .trim());
                                                        mapClueToAttr
                                                                .put(attrTemp
                                                                                .trim(),
                                                                        count);
                                                        context = mapClueToClueAttr
                                                                .get(attrTemp
                                                                        .trim());
                                                        context = context
                                                                + "^^&^^"
                                                                + contextFull;
                                                        // mapClueToClueAttr.remove(attrTemp);
                                                        mapClueToClueAttr
                                                                .put(attrTemp
                                                                                .trim(),
                                                                        context);
                                                    }
                                                    context = "";
                                                } else {
                                                    mapClueToAttr.put(
                                                            attrTemp.trim(), 1);
                                                    mapClueToClueAttr.put(
                                                            attrTemp.trim(),
                                                            contextFull);
                                                    context = "";
                                                }
                                                contextFull = new String(
                                                        doc.getText());
                                                attrTemp = "";
                                                break;
                                            } else {
                                                i++;
                                                attrTemp = "";
                                                break;
                                            }
                                        }
                                        i++;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        termCount = termCount + termCountTemp;
    }

    /**
     * 형태소 분석된 문서와 제공된 기본표현을 이용하여 aspect 후보를 추출한다.<br>
     * 1. 형태소정보을 기반으로 추출<br>
     * 2. 청킹정보를 기반으로 추출<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void extractAspectFromExpr() throws IOException {
        String context;
        StringBuffer sb = new StringBuffer();
        Iterator<NLPDoc> docit = docs.iterator();

        while (docit.hasNext()) {
            NLPDoc doc = docit.next();
            for (int a = 2; a < doc.getMorphs().getCount() - 1; a++) {
                if (doc.getMorphs().getMorph(a).getPosStr().contains("va")
                        || doc.getMorphs().getMorph(a).getPosStr()
                        .contains("vv")) {

                    if (" 하 되 있 없 않 대하 위하 그렇 이렇 저렇 싶 보 같 ".contains(doc
                            .getMorphs().getMorph(a).getTextStr()))
                        continue;

                    if (sentimentDic.getAttributes().get(0).roots
                            .containsKey(doc.getMorphs().getMorph(a)
                                    .getTextStr()
                                    + ":en")) {
                        int flag = 0;
                        int start = 0;
                        int end = 4;
                        sb = new StringBuffer();
                        for (int b = a - 1; b > 0 && b > a - end; b--) {
                            if (doc.getMorphs().getMorph(b).getPosStr()
                                    .contains("ma")) {
                                end++;
                            }
                            if (doc.getMorphs().getMorph(b).getPosStr()
                                    .contains("jk")) {
                                flag = 1;
                                start = b - 1;
                                break;
                            }
                        }
                        if (flag == 1) {
                            for (int c = start; c >= 0; c--) {
                                if (clueNOUNpos.contains(doc.getMorphs()
                                        .getMorph(c).getPosStr())) {
                                    if (sb.length() == 0) {
                                        sb.append(doc.getMorphs().getMorph(c)
                                                .getTextStr());
                                    } else {

                                        if (doc.getMorphs().getMorph(c)
                                                .getPosStr().contains("sp")
                                                && c - 1 > 0) {
                                            if (!clueNOUNpos.contains(doc
                                                    .getMorphs()
                                                    .getMorph(c - 1)
                                                    .getPosStr())
                                                    && !doc.getMorphs()
                                                    .getMorph(c - 1)
                                                    .getTextStr()
                                                    .equals(" ")) {
                                                break;
                                            }
                                        }

                                        if (doc.getTokens()
                                                .getToken(
                                                        doc.getMorphs()
                                                                .getMorph(c)
                                                                .getTokenNumber())
                                                .getNumber() == doc
                                                .getTokens()
                                                .getToken(
                                                        doc.getMorphs()
                                                                .getMorph(c + 1)
                                                                .getTokenNumber())
                                                .getNumber()) {
                                            context = doc.getMorphs()
                                                    .getMorph(c).getTextStr()
                                                    .toString().trim()
                                                    + sb.toString();
                                            sb = new StringBuffer();
                                            sb.append(context);
                                        } else {
                                            context = doc.getMorphs()
                                                    .getMorph(c).getTextStr()
                                                    .toString().trim()
                                                    + " " + sb.toString();
                                            sb = new StringBuffer();
                                            sb.append(context);
                                        }

                                    }
                                } else {
                                    break;
                                }
                            }
                        }

                        if (sb.length() > 1 && sb.length() < 20) {
                            saveMapExprToAttr(sb.toString());
                        }
                        sb = new StringBuffer();
                        PhraseItem chunkItem = doc.getMorphs().getMorph(a - 2)
                                .getChunkItem();
                        if (chunkItem != null) {
                            String chunkText = chunkItem.getKeyword();

                            if (chunkItem.getPosStr().equals("ne")
                                    || chunkItem.getPosStr().equals("ncp")) {
                                saveMapExprToAttr(chunkText);
                            }
                            chunkText = "";
                        }
                        chunkItem = null;
                        if (a + 2 < doc.getMorphs().getCount() - 1) {
                            if ((clueNOUNpos.contains(doc.getMorphs()
                                    .getMorph(a + 2).getPosStr()))
                                    && doc.getMorphs().getMorph(a + 1)
                                    .getPosStr().contains("etm")) {
                                if (!doc.getMorphs().getMorph(a + 2)
                                        .getPosStr().equals("sp")
                                        && !doc.getMorphs().getMorph(a + 2)
                                        .getTextStr().equals(" ")) {
                                    sb.append(doc.getMorphs().getMorph(a + 2)
                                            .getTextStr().trim());
                                }
                                if (a + 3 < doc.getMorphs().getCount() - 1) {
                                    for (int b = a + 3; b <= doc.getMorphs()
                                            .getCount() - 1; b++) {
                                        if (clueNOUNpos.contains(doc
                                                .getMorphs().getMorph(b)
                                                .getPosStr())) {
                                            if (doc.getMorphs().getMorph(b)
                                                    .getPosStr().equals("sp")) {
                                                if ((!doc.getMorphs()
                                                        .getMorph(b)
                                                        .getTextStr()
                                                        .contains("."))
                                                        || (!doc.getMorphs()
                                                        .getMorph(b)
                                                        .getTextStr()
                                                        .contains("-"))
                                                        || (!doc.getMorphs()
                                                        .getMorph(b)
                                                        .getTextStr()
                                                        .contains("/"))
                                                        || (!doc.getMorphs()
                                                        .getMorph(b)
                                                        .getTextStr()
                                                        .contains(","))) {
                                                    break;
                                                }
                                            }

                                            if (doc.getTokens()
                                                    .getToken(
                                                            doc.getMorphs()
                                                                    .getMorph(b)
                                                                    .getTokenNumber())
                                                    .getNumber() == doc
                                                    .getTokens()
                                                    .getToken(
                                                            doc.getMorphs()
                                                                    .getMorph(
                                                                            b - 1)
                                                                    .getTokenNumber())
                                                    .getNumber()) {
                                                context = sb.toString()
                                                        + doc.getMorphs()
                                                        .getMorph(b)
                                                        .getTextStr()
                                                        .toString()
                                                        .trim();
                                            } else {
                                                context = sb.toString()
                                                        + " "
                                                        + doc.getMorphs()
                                                        .getMorph(b)
                                                        .getTextStr()
                                                        .toString()
                                                        .trim();
                                            }
                                            sb = new StringBuffer();
                                            sb.append(context.trim());
                                        } else {
                                            break;
                                        }
                                    }
                                    if (sb.length() > 1 && sb.length() < 20) {
                                        saveMapExprToAttr(sb.toString());
                                    }
                                    sb = new StringBuffer();
                                    chunkItem = doc.getMorphs().getMorph(a + 2)
                                            .getChunkItem();
                                    if (chunkItem != null) {
                                        String chunkText = chunkItem
                                                .getKeyword();

                                        if (chunkItem.getPosStr().equals("ne")
                                                || chunkItem.getPosStr()
                                                .equals("ncp")) {
                                            saveMapExprToAttr(chunkText.trim());
                                        }
                                        chunkText = "";
                                    }
                                    chunkItem = null;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 추출된 aspect 후보의 원문을 Map에 저장한다.
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param sb
     *            추출된 후보 정보 StringBuffer
     * @exception none
     */
    public void saveMapExprToAttr(String sb) {
        int count;

        sb = sb.toUpperCase();

        if (sb.startsWith(".") || sb.startsWith("/") || sb.startsWith("-")
                || sb.startsWith(",")) {
            sb = sb.substring(sb.indexOf(" ") + 1, sb.length());
        }

        if (mapExprToAttr.containsKey(sb.toString())) {
            count = mapExprToAttr.get(sb.toString()) + 1;
            // count=count+1;
            // mapExprToAttr.remove(sb.toString());
            mapExprToAttr.put(sb.toString(), count);
        } else {
            mapExprToAttr.put(sb.toString(), 1);
        }
    }

    /**
     * 추출된 aspect 후보의 빈도를 계산하여 Map에 저장한다.
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param none
     * @exception none
     */
    public void calcAttributeCount() throws IOException {
        String key = "";
        String contextFull = "";
        Iterator<NLPDoc> docit = docs.iterator();

        while (docit.hasNext()) {
            NLPDoc doc = docit.next();

            contextFull = new String(doc.getText());

            for (Entry<String, Integer> entry : mapClueToAttr.entrySet()) {
                key = entry.getKey();

                if (contextFull.contains(key)) {
                    if (mapAttr.containsKey(key)) {
                        int count = mapAttr.get(key);
                        // mapAttr.remove(key);
                        mapAttr.put(key, count + 1);
                    } else {
                        mapAttr.put(key, 1);
                    }
                }
                key = "";
            }
        }
    }

    /**
     * 추출된 Clue 기반의 속성 후보를 지정된 파일에 저장한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileOut
     *            출력파일
     * @exception none
     */
    public void saveAttrFromClue(String fileOut) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));

        if (!mapAttr.isEmpty()) {

            for (Entry<String, Integer> entry : mapAttr.entrySet()) {

                String key = entry.getKey();

                if (stopwords.contains(key)) {
                    continue;
                }

                double valueB = entry.getValue();

                double valueA = mapClueToAttr.get(key);

                String clue_attr = mapClueToClueAttr.get(key);
                String[] text;

                if (valueB > 1 && valueA > 1) {
                    double pmiA = valueA * termCount / (clueCount * valueB);
                    double PMI = Math.log(pmiA) / Math.log(2);

                    String PMI_A = new DecimalFormat("#.###").format(PMI);

                    text = clue_attr.split("\\^\\^\\&\\^\\^");
                    for (int i = 0; i < text.length; i++) {
                        out.write(key + "\t"
                                + new DecimalFormat("#").format(valueA) + "\t "
                                + new DecimalFormat("#").format(clueCount)
                                + "\t " + new DecimalFormat("#").format(valueB)
                                + "\t " + PMI_A + "\t"
                                + text[i].replaceAll("\"", "") + "\n");
                    }
                }
            }
        }
        out.close();
    }

    /**
     * 추출된 표현 기반의 속성 후보를 지정된 파일에 저장한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param fileOut
     *            출력파일
     * @exception none
     */
    public void saveAttrFromExpr(String fileOut) throws IOException {
        BufferedWriter out = new BufferedWriter(new FileWriter(fileOut));
        String key;
        int attrCnt;

        for (Entry<String, Integer> entry : mapExprToAttr.entrySet()) {
            key = entry.getKey();
            attrCnt = entry.getValue();
            if (attrCnt > 1 && (!stopwords.contains(key))) {
                out.append(key + "\t" + attrCnt + "\n");
            }
        }
        out.close();
    }

    /**
     * 분석 실행<br>
     * 1. 인코딩를 이용해 분석대상 문서를 읽는다.<br>
     * 2. 문서를 형태소 분석한다.<br>
     * 3. 50000문장 단위로 묶어서 지식을 추출한다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param AspectExtractor
     *            속성추출기, fileIn 입력파일, encoding 파일인코딩
     * @exception IOException
     */
    public void startAnalysis(AspectExtractor tester, File fileIn, String encoding, String extractAspectFromClue, String extractAspectFromExpr) throws IOException { docs = new ArrayList<NLPDoc>();
        BufferedReader in = new BufferedReader(new FileReader(fileIn));

        String line = null;

        while ((line = in.readLine()) != null) {
            docs.addAll(nlpapi.doAnalyze(line));

            if (docs.size() > 50000) {
                System.out.println("Doc count : " + docs.size());
                if (extractAspectFromClue.equals("true")) {
                    System.out.println("Start clue!");
                    tester.extractAspectFromClue();
                    tester.calcAttributeCount();
                }
                if (extractAspectFromExpr.equals("true")) {
                    System.out.println("Start expr!");
                    tester.extractAspectFromExpr();
                }
                tester.docs = new ArrayList<NLPDoc>();
            }
        }

        if (docs.size() > 0) {
            System.out.println("Doc count : " + docs.size());
            if (extractAspectFromClue.equals("true")) {
                System.out.println("Start clue!");
                tester.extractAspectFromClue();
                tester.calcAttributeCount();
            }
            if (extractAspectFromExpr.equals("true")) {
                System.out.println("Start expr!");
                tester.extractAspectFromExpr();
            }
            tester.docs = new ArrayList<NLPDoc>();
        }
    }

    /**
     * 속성, CLUE 등의 단어를 중복제거하여 MAP에 넣기 위한 함수<br>
     * 콤마(,)로 구분된 문자를 잘라 MAP에 넣는다.
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param filename
     */
    public HashSet<String> setWordList(String filename) throws Exception,
            IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(
                new FileInputStream(filename), "UTF-8"));
        HashSet<String> words = new HashSet<String>();
        String line = null;

        while ((line = in.readLine()) != null) {
            words.add(line.trim().toUpperCase());
        }

        return words;
    }

    /**
     * 분석 main<br>
     * - 속성-표현 후보 추출 TEST를 위한 Main() 함수.<br>
     * - 실재로 동작하는 main()은 KnowledgeBuildingTransaction_Admin.java 과
     * KnowledgeBuildingTransaction_StandAlone.java에<br>
     * 존재한다. KnowledgeBuildingTransaction_Admin은 관리자 도구를 통해 자동으로 동작하는 배치 프로그램이고<br>
     * KnowledgeBuildingTransaction_StandAlone.java 커맨드 형태로 동작하는 프로그램이다.<br>
     *
     * @since 2012.09.03
     * @author 한영섭
     * @param args
     */
    public static void main(String[] args) throws Exception {
        long start_time = System.currentTimeMillis();

        AspectExtractor tester = new AspectExtractor();

        String category = "tstore";
        String mainDirectory = "D:/knowledgeBuilding";
        String fileDict = mainDirectory + "/dict/GENERAL.txt";
        String dataDirectory = mainDirectory + "/data/bmt/" + category + "/chk";
        String attrDirectoryFromClue = mainDirectory + "/attr/bmt/" + category
                + "/" + category + "_clue.txt";
        String attrDirectoryFromExpr = mainDirectory + "/attr/bmt/" + category
                + "/" + category + "_expr.txt";

        // clue setting
//		tester.clue.addAll(tester.setWordList(mainDirectory + "/clue/bmt/"
//				+ category + ".clue"));

//		System.out.println(tester.clue);

        // 금칙어 setting
        tester.stopwords.addAll(tester.setWordList(mainDirectory
                + "/dict/stopword.txt"));

        // 감성사전 setting
        tester.sentimentDic.set_dict(fileDict, tester.nlpapi);

        tester.sentimentDic.print_dict();

        Map<String, OpinionNode_SentimentExpression> exp = tester.sentimentDic
                .getAttributes().get(0).roots;

        for (Entry<String, OpinionNode_SentimentExpression> entry : exp
                .entrySet()) {
            System.out.println(entry.getKey() + ":"
                    + entry.getValue().getExpressionValue());
        }

        File file = new File(dataDirectory);
        System.out.println(file.getName());
        tester.startAnalysis(tester, file, "UTF-8", "false", "true");
        tester.docs.clear();

        // tester.saveAttrFromClue(attrDirectoryFromClue);
        tester.saveAttrFromExpr(attrDirectoryFromExpr);

        System.out.println("Used Memory : "
                + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime()
                .freeMemory()) / 1024) / 1024 + "MB");
        long end_time = System.currentTimeMillis();
        System.out.println("Run time : "
                + UtilTimer.timeDiff(start_time, end_time));
    }
}
