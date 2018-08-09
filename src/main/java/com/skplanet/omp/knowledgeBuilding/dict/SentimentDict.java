package com.skplanet.omp.knowledgeBuilding.dict;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.skplanet.nlp.NLPAPI;
import com.skplanet.nlp.NLPDoc;
import com.skplanet.nlp.config.Configuration;
import com.skplanet.nlp.morph.Morph;
import com.skplanet.nlp.morph.Morphs;
import com.skplanet.omp.knowledgeBuilding.dict.PosCompare;
import com.skplanet.omp.knowledgeBuilding.ohash.OpinionNode_SentimentAttribute;
import com.skplanet.omp.knowledgeBuilding.ohash.OpinionNode_SentimentExpression;


/**
 * @author  6042
 */
public class SentimentDict {


    private int MAX_CATEGORY_PATH_LEN = 5;

    private int category_id = 0;

    private String category_name = null;

    private String[] category_path_name = null;

    private int[] category_path_id = null;

    /**
     * @uml.property  name="attributes"
     */
    private ArrayList<OpinionNode_SentimentAttribute> attributes = null;

    private Map<String, String> hash_attributes = null;

    public SentimentDict( )
    {
        init();
    }

    private void init()
    {
        category_id = 0;

        category_name  = new String();

        category_path_name = new String[MAX_CATEGORY_PATH_LEN];

        category_path_id = new int[MAX_CATEGORY_PATH_LEN];

        attributes = new ArrayList<OpinionNode_SentimentAttribute>();

        hash_attributes = new HashMap<String, String>(1);
    }

    public HashMap<String, String> getHashAttributes()
    {
        return (HashMap<String, String>) hash_attributes;
    }

    /**
     * @return
     */
    public int getCategoryId()
    {
        return category_id;
    }

    /**
     * @param category id
     */
    public void setCategoryId(String id)
    {
        this.category_id = Integer.parseInt(id);
    }

    /**
     * @return
     */
    public String getCategoryName()
    {
        return category_name;
    }

    /**
     * @param
     */
    public void setCategoryName(String name)
    {
        this.category_name = name;
    }

    /**
     * @return
     */
    public int[] getCategoryPathId()
    {
        return category_path_id;
    }

    /**
     * @param
     */
    public void setCategoryPathId(String ids)
    {
        String[] parr = ids.split(",");

        for(int i=parr.length-1, j=0; i>=0; i--) {
            if( parr[i].isEmpty() ) continue;

            if( parr[i].equals(0)) break;

            this.category_path_id[j++] = Integer.parseInt(parr[i]);

        }
    }

    /**
     * @return
     */
    public String[] getCategoryPathName()
    {
        return category_path_name;
    }

    /**
     * @param
     */
    public void setCategoryPathName(String names)
    {
        String[] parr = names.split("/");

        for(int i=parr.length-1, j=0; i>=0; i--) {
            if( parr[i].isEmpty() ) continue;

            if( parr[i].equals(null)) break;

            this.category_path_name[j++] = parr[i];
        }
    }

    /**
     * @return
     * @uml.property  name="attributes"
     */
    public ArrayList<OpinionNode_SentimentAttribute> getAttributes()
    {
        return this.attributes;
    }

    /**
     * 데이터를 hash에 삽입한다.
     * @param keys 표현
     * @param value 표현값
     * @param feature 속성
     */

    public void insert_data(String keys, String value, OpinionNode_SentimentAttribute attribute)
    {
        OpinionNode_SentimentExpression sdata = null;

        int data_len = keys.length();
        int value_len = value.length();

        if(data_len < 1 || value_len < 1)
            return;

        sdata  = new OpinionNode_SentimentExpression(value);

        attribute.getRoots().put(keys, sdata);
    }

    private int set_dict_header(String[] arr) {

        if(arr.length < 2) {
            System.err.println("The data format is wrong.");
            System.exit(-1);
        }

        if(arr[0].equals("^CATEGORY_ID") ) {
            setCategoryId(arr[1]);
            return 1;
        }

        if(arr[0].equals("^CATEGORY_NAME") ) {
            setCategoryName(arr[1]);
            return 1;
        }

        if(arr[0].equals("^CATEGORY_PATH_NAME") ) {
            setCategoryPathName(arr[1]);
            return 1;
        }

        if(arr[0].equals("^CATEGORY_PATH_ID") ) {
            setCategoryPathId(arr[1]);
            return 1;
        }

        return 0;
    }

    private static boolean is_dict_type( String sr)
    {
        if( sr.equals(":et") || sr.equals(":en") || sr.equals(":qm") ) {
            return false;
        }
        return true;
    }

    private int set_dict_expression(String str, NLPAPI na, OpinionNode_SentimentAttribute attribute)
    {
        String delim = "\\s+";

        String data_delim = " ";

        StringBuffer sb = new StringBuffer();

        String value = new String();

        String[] arr = str.split(delim);

        sb.delete(0, sb.length());
        for(int i=0; i<arr.length-1; i++) sb.append(arr[i]).append(data_delim);

        value = arr[arr.length-1];

//System.out.println( " KEY :" + sb + ", VALUE :" + new String(value));

        List<NLPDoc> list = na.doAnalyze(sb.toString());
        NLPDoc doc = list.get(0);
        Morphs morphs = doc.getMorphs();

        sb.delete(0, sb.length());
        for(int i=0; i<morphs.getCount(); i++) {

            Morph morph = morphs.getMorph(i);

            if(PosCompare.is_ignore_pos(morph.getPos())) continue;

            String sr = PosCompare.regard_morph(morphs, i);

            if( sr == null) {
                sb.append(morph.getTextStr());
            } else {
                sb.append(sr);
            }
        }

//System.out.println( " MORPHS SBUF : " + sb.toString());

        if( !is_dict_type(sb.toString())) {
//System.out.println( " WRONG FORMAT : " + sb.toString());
            return 0;
        }

//System.out.println( " INSERT_DATA  1: " + sb.toString().toLowerCase() + ", 2:" + value + "/" + attribute.getAttribute());

        insert_data(sb.toString().toLowerCase(), value ,attribute );

        return 1;
    }

    public int set_dict( String file_name, NLPAPI na ) throws Exception
    {
        int header_count  = 0;

        String delim    = ":";

        String[] features = null;

        int feature_count = 0;

        int exp_count = 0;

        OpinionNode_SentimentAttribute attribute = null;

        try
        {
            BufferedReader in = new BufferedReader(new FileReader(file_name));
            String line = null;

            while((line = in.readLine()) != null)
            {
                line = line.trim();

                if("".equals(line))
                    continue;

                String[] arr = line.split(delim);

                if(header_count < 4) {
                    header_count += set_dict_header(arr);
                    continue;
                }

                if( arr[0].substring(0, 2).equals("OM") &&
                        arr[0].substring(arr[0].length()-3, arr[0].length()).equals("FEA") ) {

                    if( attribute != null ) {
                        attributes.add(attribute);
                        //System.out.println("INSERT HASH:" +attribute.getAttribute().replaceAll(" ",""));
                        hash_attributes.put(attribute.getAttribute().replaceAll(" ",""), attribute.getAttribute());
                    }

                    attribute = new OpinionNode_SentimentAttribute(arr[1]);
                }

                if( arr[0].substring(0, 2).equals("OM") &&
                        arr[0].substring(arr[0].length()-3, arr[0].length()).equals("EXP") ) {

                    //if( attribute == null ) //System.out.println( "널이야??");
                    exp_count += set_dict_expression(arr[1], na, attribute);

                }

            }

            if( attribute != null ) {
                attributes.add(attribute);
                //System.out.println("INSERT HASH:" +attribute.getAttribute().replaceAll(" ",""));
                hash_attributes.put(attribute.getAttribute().replaceAll(" ",""), attribute.getAttribute());
            }

            in.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        return exp_count;
    }

    public void print_dict() {
        System.out.println( "^CATEGORY_ID:" + this.getCategoryId());
        System.out.println( "^CATEGORY_NAME:" + this.getCategoryName());

        System.out.print( "^CATEGORY_PATH_NAME:");
        String[] names = this.getCategoryPathName();
        for(int i=0; i<names.length; i++) {
            System.out.print("/"+names[i]);
        }
        System.out.println();

        System.out.print( "^CATEGORY_PATH_ID:");
        int[] ids = this.getCategoryPathId();
        for(int i=0; i<ids.length; i++) {
            System.out.print("/"+ids[i]);
        }
        System.out.println();
    }


    public static void main(String[] args) throws Exception
    {
        NLPAPI na = new NLPAPI("nlp_api.properties", Configuration.CLASSPATH_LOAD);
        //String f_dict = args[0];

        String f_dict = new String("C:/OMP/DICT/SENTIMENT_TEST/50_DSLR.txt");

        SentimentDict sdict = new SentimentDict();

        sdict.set_dict(f_dict,na);

        sdict.print_dict();

        OpinionNode_SentimentExpression exp = sdict.getAttributes().get(0).roots.get("알:en없:en");

        System.out.println( exp.getExpressionValue() );

    }



}
