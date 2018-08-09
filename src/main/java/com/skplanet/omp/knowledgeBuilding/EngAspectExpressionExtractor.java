package com.skplanet.omp.knowledgeBuilding;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import java.util.Date;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.skplanet.omp.knowledgeBuilding.command.ExprData;

/**
 *  관리도구로 부터 요청을 받아 속성과 속성-표현후보 추출기를 실행하는 Transaction 클래스<p>
 * 
 * @version  	0.1
 * @since  	2012.03.03
 * @author  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @modifier  	한영섭 han042@opensns.co.kr (주)OPENSNS
 * @file  	EngAspectExpressionExtractor.java
 * @history   2013.03.03	* v0.1	클래스 최초 생성.	한영섭.<br>
 * @history   2013.03.18	* v0.1주석 업데이트.	한영섭.<br>
 */
public class EngAspectExpressionExtractor {
	

	public static TreeMap<String, Integer> aspectCount;
	public static TreeMap<String, ExprData> engAttrExprMap;
	public static TreeSet<String> mapEngOldExpr;
	public String P1;
	public String P2;
	public String P3;
	
	public EngAspectExpressionExtractor() {
		try
		{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * 외부에서 지식구축 환경설정을 로딩하기 위한 클래스<br>
	 *   
	 * @since 2012.09.03
	 * @author 한영섭
	 * @param none
	 * @exception none
	 */
	public void init() throws Exception
	{
		engAttrExprMap 			= new TreeMap< String , ExprData >( );
		mapEngOldExpr  			= new TreeSet<String>();
		
		String words = "[\\w\\-'&0-9]+";
		String NN = "(?:"+words+" \\/NN.?\\/\\s?)";
		String VB = words+" \\/VB.?\\/"; 
		String RB = "(?:"+words+" \\/RB.?\\/)"; 
		String JJ = words+" \\/JJ.?\\/";           
		String CC = words+" \\/CC\\/";             

		String VBD = words+" \\/VBD\\/";
		String VBN = words+" \\/VBN\\/";
		String RP = "(?:"+words+" \\/RP\\/)"; 
		
		String NP = NN+"{1,3}";
		String JP = JJ+"{1,2}";                
		String ADJP = JJ+"( "+CC+" "+JJ+")*";		
		
		this.P1 = "(("+JJ+" )?"+NP+") "+VB+" ("+RB+" )*(("+JJ+" "+NP+"|"+JJ+"( "+CC+" "+JJ+")*))"; 
		
		this.P2 = "("+JJ+") ("+NP+")"; 
		
		this.P3 = "(("+JJ+" )?"+NP+") "+VB+" ("+RB+" )*((("+RB+" )*"+VBN+" ("+JJ+"|"+RP+"|"+RB+")*))"; 
	}
	
	public static void startAspectAnalysis(String pattern, String text, int aspectPos, int exprPos){
		Matcher m = Pattern.compile(pattern).matcher(text);
			
		 while(m.find()){
			 String attr = m.group(aspectPos).replaceAll("\\/[A-Z]*\\/", "").toString().toUpperCase().trim();

			  if(aspectCount.containsKey(attr))
			  {
				  int count = aspectCount.get(attr);
				  aspectCount.put(attr, count+1);
			  }
			  else
			  {
				  aspectCount.put(attr, 1);
			  }
		  }
	}
	
	public void startAspectExprAnalysis(String pattern, String textOrg, int aspectPos, int exprPos){
		
		Matcher m = Pattern.compile(pattern).matcher(textOrg);
		
		String WrdPosR = "((?:[^\\/]+ \\/[^\\/]{1,4}\\/\\s?){0,9})$";		
		String WrdPosL = "^((?:[^\\/]+ \\/[^\\/]{1,4}\\/\\s?){0,9})";

		while(m.find()){
			//속성, 표현 추출
			String attr = m.group(aspectPos).replaceAll("\\/[A-Z]*\\/", "").toUpperCase().trim();
			String expr = m.group(exprPos).replaceAll("\\/[A-Z]*\\/", "").toUpperCase().trim();

			//원문보기 왼쪽
			Matcher m1 = Pattern.compile(WrdPosR).matcher(textOrg.substring(0, m.start()));
			String left = "";
			if(m1.find()) left =  m1.group();

			//원문보기 오른쪽
			Matcher m2 = Pattern.compile(WrdPosL).matcher(textOrg.substring(m.end(), textOrg.length()));
			String right = "";
			if(m2.find()) right =  m2.group();

			//원문보기 전체
			String text = left.replaceAll("\\/[A-Z]*\\/", "").toUpperCase().trim()+ "<b> \t:"+m.group().replaceAll("\\/[A-Z]*\\/", "").toUpperCase().trim()+":\t </b> "+right.replaceAll("\\/[A-Z]*\\/", "").toUpperCase().trim();

			ExprData exprData 										= new ExprData();
			
			if(!aspectCount.containsKey(attr)) continue;
			
			int count = aspectCount.get(attr);

			if(count > 1){
				if(engAttrExprMap.containsKey(attr+"\t"+expr))
				{
					exprData = engAttrExprMap.get(attr+"\t"+expr);
					int exprCount = exprData.getCount();
					if(exprCount < 4)
					{
						exprData.setCount(exprCount+1);
						exprData.setText(exprData.getText()+" </br> "+text);										
						engAttrExprMap.put(attr+"\t"+expr, exprData);
					}
				}
				else
				{
					exprData.setCount(1);
					exprData.setText(text.replaceAll("\\/[A-Z]*\\/", "").toUpperCase().trim());
					engAttrExprMap.put(attr+"\t"+expr, exprData);	
				}
				exprData = new ExprData();
			}				
		}
	}
	
	
	//p1으로 돌려서 aspect 만 뽑아낸다.	
	public void startAnalysis(String fileIn,String pattern1,String pattern2,String pattern3) throws IOException
	{
		BufferedReader	in						= new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));
		
		aspectCount 			= new TreeMap<String, Integer>();
		engAttrExprMap 	= new TreeMap< String , ExprData >( );
		
		String line;
		
		while((line = in.readLine()) != null)
		{	
			if(pattern1.equals("true"))
			{
				startAspectAnalysis(this.P1, line, 1, 4);
			}
			if(pattern2.equals("true"))
			{
				startAspectAnalysis(this.P2, line, 2, 1);
			}
			if(pattern3.equals("true"))
			{
				startAspectAnalysis(this.P3, line, 1, 4);		
			}
		}

		in.close();
		
		if(aspectCount.size() > 0){ 
			in						= new BufferedReader(new InputStreamReader(new FileInputStream(fileIn),"UTF-8"));		
			while((line = in.readLine()) != null)
			{
				if(pattern1.equals("true")) startAspectExprAnalysis(this.P1, line, 1, 4);
				if(pattern2.equals("true")) startAspectExprAnalysis(this.P2, line, 2, 1);
				if(pattern3.equals("true")) startAspectExprAnalysis(this.P3, line, 1, 4);
			}
			in.close();
		}
		
		
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

		String line = null;
		String lineArray[] = null;
		InputStreamReader fileReader = null;
		
		File file = new File(fileExprOld);
			
		if(file.exists())
		{
			fileReader = new InputStreamReader(new FileInputStream(file.getPath()),"UTF-8");
			BufferedReader	in			= new BufferedReader(fileReader);	
			while((line = in.readLine()) != null)
			{
				lineArray = line.split("\t");
				mapEngOldExpr.add(lineArray[0].toUpperCase()+"\t"+lineArray[1].toUpperCase());					
			}
		}
	
		if(mapEngOldExpr.size() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub		
		EngAspectExpressionExtractor eee = new EngAspectExpressionExtractor();
		
		engAttrExprMap 											= new TreeMap< String , ExprData >( );
		
		Date now = new Date();
		System.out.println("===== Knowledge Building : Start ("+now+") =====");
		
		File file = new File(args[0]);
		
		if(!file.exists()){
			System.out.println("Input file is wrong :  " + file.toString());
		}		
		
		try{
			BufferedWriter out			= new BufferedWriter(new OutputStreamWriter(new FileOutputStream(args[1]),"UTF-8"));
			
			System.out.println("Input file : " + args[0]);
			eee.startAnalysis(file.toString(),"true","true","true");

			for(Entry< String , ExprData > entry:engAttrExprMap.entrySet()){
				out.write(entry.getKey()+"\t"+entry.getValue().getCount()+ "\t" +entry.getValue().getText()+"\n");
			}

			out.close();
		}
		catch(Exception e)
		{
			System.out.println("Exception occured !!");
		}		
		System.out.println("Output file : " + args[1]);
		
		now = new Date();
		System.out.println("===== Knowledge Building : end ("+now+") =====");
	}

}
