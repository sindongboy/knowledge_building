package com.skplanet.omp.knowledgeBuilding.Util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.skplanet.nlp.utils.UtilTimer;

/** 
 * 지식구축에 사용될 수집원문에서 TITLE/BODY/COMMENT등의 DATA를 추출하여 
 * 지식구축 디렉토리에 저장한다.<p>
 * 
 * @version	0.1
 * @since	2012.08.31
 * @author	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file	ScdDupChecker.java
 * @history
 *  2012.08.31	* v0.1	클래스 최초 생성.	한영섭.<br>
 */
public class ScdDupChecker {
	
	public ScdDupChecker()
	{
		scdList 				= new HashMap< String , String >( );
		scdDocs			= new HashSet<String>();
		objects				= new HashSet<String>();		
	}
	
	public static HashMap<String, String> scdList;
	
	public static HashSet<String> scdDocs;
	
	public static HashSet<String> objects;
	
	/**
	 * 지식구축 대상 원문파일을 읽어 hashset에 저장한다.<br>
	 * 수집원문 형태이며, 중복이 제거된다.
	 * @param fileIn
	 * @param incoding
	 */
	public void do_dup_check(String fileIn, String scdDirectory, String chkDirectory) throws IOException
	{		
		BufferedReader in		= new BufferedReader(new InputStreamReader(new FileInputStream(scdDirectory+fileIn),"UTF-8"));	
		BufferedWriter out		= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chkDirectory+fileIn+".chk"),"UTF-8"));
	
		String line 						= null;
		StringBuffer sb				= new StringBuffer();
		StringBuffer sbTemp	= new StringBuffer();
		
		while((line = in.readLine()) != null)
		{
			line	= line.replace("\t","");
			line	= line.trim();

			if(line.startsWith("^FIELD:TITLE")||line.startsWith("^FIELD:BODY") ||line.startsWith("^FIELD:CONTENT"))
			{
				line 	= in.readLine().replaceAll("\t","").trim();
	
				String[] res = line.split("VALUE:");

				if(line.length() > 20)
				{
					sb.append(res[1]+"\n");
				}
				
				while((line = in.readLine()) != null){
					if(line.startsWith("^DOCSEQ:"))
					{
						break;
					}
					
					line	= line.replaceAll("\t","").trim();
					
					if(line.length() > 20)
					{
						sb.append(line+"\n");
					}						
				}
			}
			else	if(line.startsWith("^FIELD:COMMENT"))
			{
				
				line = in.readLine();
				
				while((line = in.readLine()) != null)
				{
					if(line.startsWith("^DOCSEQ:"))
					{
						break;
					}
					
					line				= line.replaceAll("\t","").trim();
					
					if(line.length() > 20)
					{
						sbTemp.append(line);
					}						
				}
				
				String cmtTemp 	= sbTemp.toString();
				String cmt 				= "";				
								
				int start					= cmtTemp.indexOf("<<<TITLE>>>:");
				int end 						= cmtTemp.indexOf("<<<BODY>>>:");				
				
				if(start+12 < end)
				{
					cmt 	= cmtTemp.substring(start+12, end);
					cmt		= cmt.replace("\t","");
					cmt		= cmt.trim();
					
					if(cmt.length() > 20)
					{
						sb.append(cmt+"\n");
					}	
				}
				
				start 			= cmtTemp.indexOf("<<<BODY>>>:");
				
				int end1	= cmtTemp.indexOf("<<<DATE>>>:");
				int end2 	= cmtTemp.indexOf("<<<WRITER>>>:");
				
				if(end1 < end2)
				{
					if(end1 < 0)
					{					
						end = end2;
					}
					else
					{
						end = end1;
					}
				}
				else
				{					
					if(end2 < 0)
					{
						end = end1;
					}
					else
					{
						end = end2;
					}
				}
				if(start+11 < end)
				{
					cmt 	= cmtTemp.substring(start+11, end);
					cmt		= cmt.replace("\t","");
					cmt		= cmt.trim();
					
					if(cmt.length() > 20)
					{
						sb.append(cmt+"\n");
					}
				}				
				sbTemp.setLength(0);
			}
			if(sb.length() > 10 && sb.length() <  50480)
			{
				scdDocs.add(sb.toString());
				sb.setLength(0);
			}
		}
		if(scdDocs.size() > 0)
		{
			for(String doc:scdDocs)
			{
				out.write(doc);
				out.flush();
			}
			scdDocs.clear();
		}
		out.close();
		in.close();
	}
	
	/**
	 * 지식구축 대상 원문파일을 읽어 hashset에 저장한다.<br>
	 * 수집원문 형태이며, 중복이 제거된다.
	 * @param fileIn
	 * @param incoding
	 */
	public void get_naverShopping_object(String scdFile, String objectFile) throws IOException
	{		
		BufferedReader in		= new BufferedReader(new InputStreamReader(new FileInputStream(scdFile),"UTF-8"));	
		BufferedWriter out		= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(objectFile, true),"UTF-8"));
	
		String line 				= null;
		String title				= "";
		String brand			= "";
		
		while((line = in.readLine()) != null)
		{
			line	= line.replace("\t","");
			line	= line.trim();

			if(line.startsWith("^FIELD:TITLE"))
			{
				line 	= in.readLine().replaceAll("\t","").trim();
	
				String[] res = line.split("VALUE:");

				title = res[1].trim();
			}
			else if(line.startsWith("^FIELD:BRAND"))
			{
				line 	= in.readLine().replaceAll("\t","").trim();
	
				String[] res = line.split("VALUE:");

				brand = res[1].trim();
			}
			
			if(title.length() > 0){
				
				title = title.replaceAll("\\(.*\\)|\\[.*\\]", "").replaceAll("  "," ");
				
				objects.add(title.trim());
				
				if(brand.equals("")) continue;
				
				if(title.contains(brand))
				{
					objects.add(title.replaceAll(brand, "").trim());
				}
				else
				{
					objects.add(brand.trim()+" "+title.trim());
				}				
			}
			
			title		= "";
			brand	= "";
		}
		
		for(String object:objects)
		{
			out.append(object+"\n");
		}
		objects = new HashSet<String>();		
		out.flush();
		out.close();
		in.close();
	}
	
	/**
	 * 지식구축 대상 원문파일을 읽어 hashset에 저장한다.<br>
	 * 파싱된 형태이며, 중복이 제거된다.
	 * @param fileIn
	 * @param incoding
	 */
	public void do_setdoc_parse(String fileIn, String scdDirectory, String chkDirectory,String incoding) throws IOException
	{
		BufferedReader in		= new BufferedReader(new InputStreamReader(new FileInputStream(scdDirectory+fileIn),incoding));
		BufferedWriter out		= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(chkDirectory+fileIn+".chk"),"UTF-8"));

		String line = null;
		
		StringBuffer sb					= new StringBuffer();
		while((line = in.readLine()) != null)
		{
			line				= line.trim();

			if(line.contains("^TITLE")){
				String[] res = line.split("TITLE:");
				if(res.length > 1){
					sb.append(res[1]);
					sb.append("\n");
				}
			}else	if(line.contains("^CONTENT")){
				String[] res = line.split("CONTENT:");
				if(res.length > 1){
					sb.append(res[1]);
					sb.append("\n");
				}
			}else	if(line.contains("^BODY")){
				String[] res = line.split("BODY:");
				if(res.length > 1){
					sb.append(res[1]);
					sb.append("\n");
				}
			}
			if(sb.length() > 0){
				scdDocs.add(sb.toString());
				sb.setLength(0);
			}
		}
		if(scdDocs.size() > 0)
		{
			for(String doc:scdDocs)
			{
				out.write(doc);
				out.flush();
			}
			scdDocs.clear();
		}
		out.close();
		in.close();
	}

	public void do_setScdList(String directory) throws IOException
	{
		String line 	= "";
		File file 		= null;
		File sub[] 	= null;
		file 					= new File(directory+"/loc");
		
		System.out.println(directory);
		if (file.isDirectory()) {
			sub = file.listFiles(); 
			for (int i = 0; i < sub.length; i++) {
				if (sub[i].isFile()){					
					InputStreamReader fileReader = null;		
					fileReader = new InputStreamReader(new FileInputStream(sub[i].getPath()),"MS949");		
					BufferedReader	in			= new BufferedReader(fileReader);	
					while((line = in.readLine()) != null)
					{
						String[] scdTemp = line.split(" ");
						scdTemp[0].replace("\\","/");
						String[] scd = scdTemp[0].split("/");
						String[] locTemp = sub[i].getName().split("\\.");
						String loc = locTemp[0];
						scdList.put(scd[scd.length-1], loc);
					}					
				}				
	       }
	     }
		for(Entry<String, String> entry:scdList.entrySet()){
			
			System.out.println(entry.getKey() +" : "+ entry.getValue());
		}
	}
	
	public static void main(String[] args) throws Exception
	{
		/*
		if(args.length!=3){
			System.out.println("Usage : java FeatureFrequencyCalculator [Doc Type:1 DOC, 2 COMMENT, 3 PARSE] [Category Number] [incoding : UTF-8, MS949]");
			System.out.println("Example : java FeatureFrequencyCalculator 1 50 UTF-8");			
		}else{
		*/
			long start_time								= System.currentTimeMillis();
			ScdDupChecker tester							= new ScdDupChecker();
			scdList 				= new HashMap< String , String >( );
			scdDocs			= new HashSet<String>();
			
			String cateNum				= "blog";
		
			String dataDirectory 		= "D:/knowledgeBuilding/data/bmt/3999_hydrocream/" + cateNum;	
			String scdDirectory 		= dataDirectory + "/scd/";
			String chkDirectory 		= dataDirectory + "/chk/";
			long end_time;
			
			//수집문서 scd -> 카테고리 번호 매핑
			tester.do_setScdList(dataDirectory);
			
			File chkdir = new File(chkDirectory);
			
			if(chkdir.exists() == false)
			{
				chkdir.mkdir();
			}
			
			File file = new File(scdDirectory);
					
			File sub[] 	= null;
			sub = file.listFiles(); 
			
			for (int i = 0; i < sub.length; i++) {

				start_time						= System.currentTimeMillis();

				tester.do_setdoc_parse(sub[i].getName(),scdDirectory,chkDirectory,"UTF-8");

				end_time								= System.currentTimeMillis();		
				System.out.println(sub[i].getName()+" Run time : "+UtilTimer.timeDiff(start_time,end_time));
			}

			System.out.println("Used Memory : " + ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory())/1024)/1024+"MB");
			end_time								= System.currentTimeMillis();		
			System.out.println("Run time : "+UtilTimer.timeDiff(start_time,end_time));
	}
}
