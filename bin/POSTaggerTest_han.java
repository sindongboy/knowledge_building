import java.io.*;
import java.util.*;
import com.skplanet.mnlp.*;
import com.skplanet.mnlp.segmenter.*;
import com.skplanet.mnlp.tagger.*;


public class POSTaggerTest_han
{

    public static void main(String[] args)
    {
        if( args!=null && args.length==5)
        {
	    String lang = args[0];
            String rsc_path = args[1];
            String input_file_path = args[2];
	    String output_file_path = args[3];
            int n_best = Integer.parseInt( args[4] );

            POSTagFromFile( lang, rsc_path, input_file_path, output_file_path, n_best );
        }
        else
        {
            System.out.println("usage : <lang:\"eng\"> <resource_dir_path> <input file_to_segment> <output file_to_segment> <n_best>");
        }
    }

   public static void POSTagFromFile(String lang, String rsc_path, String input_file_path, String output_file_path, int n_best)
    {
        try
        {
	    File file = new File(input_file_path);
	    File[] subFile = file.listFiles();
	    for (int x = 0; x < subFile.length; x++) {	    
	    
            int ch = 0;
            String contents = "";
            StringBuffer buffer = new StringBuffer(1024);
            BufferedReader br = new BufferedReader( new InputStreamReader( new FileInputStream(subFile[x].toString()), "UTF-8")  );
	    BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(output_file_path+"/"+subFile[x].getName()),"UTF-8"));

            while( (ch = br.read())!=-1 )
            {
                buffer.append((char)ch);
            }

            br.close();
            if( buffer.length()==0 ) return;

            contents = buffer.toString();
            {
                Segmenter segmenter = new Segmenter();
                segmenter.Init(lang, rsc_path);			// lang = "eng"             
								// rsc_path = "$MNLP_HOME_DIR_PATH/resource/bin_data"
                segmenter.DoSegment( contents, "", true );	// contents = "분석할 문서"
								// delemeter = ""
                POSTagger t = new POSTagger();
                t.Init(lang, rsc_path);

                ArrayList<Sentence> segment_result = segmenter.get_segment_result();
                for( int i=0; i<segment_result.size(); i++ )
                {
                    Sentence s = segment_result.get(i);
                    t.DoPOSTag( s, n_best );
                    //  t.PrintResult();

                     ///* Example of Tagger Result Iteration
                        ArrayList< MorphInfo > tagger_result = t.get_tagger_result();
                        for(int m=0; m<tagger_result.size(); m++)
                        {
                            MorphInfo mi = tagger_result.get(m);
                                 
                            ArrayList< POSTagInfo > best_path = mi.get_best_path();
                            for(int b=0; b<best_path.size(); b++ )
                            {
                                POSTagInfo bp = best_path.get(b);
				out.write(bp.get_root()+" /"+bp.get_tag()+"/ ");
                            }                
	                }
			out.write("\n");
			out.flush();
                     // */

                }
                t.Free();
                segmenter.Free();
            }
		out.close();
	    }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
}
