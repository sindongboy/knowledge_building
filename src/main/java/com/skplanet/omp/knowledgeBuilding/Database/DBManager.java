/**
 * 
 */
package com.skplanet.omp.knowledgeBuilding.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import java.util.ArrayList;

public class DBManager
{
	private Connection conn					= null;
	
	private String db_url					= "jdbc:mysql://61.250.47.180:3306/omp?zeroDateTimeBehavior=convertToNull";
	private String db_id					= "omp";
	private String db_pass					= "wjswoddlek150!";
	private String db_name					= "omp";

	public DBManager()
	{
		try{
			init();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			
		}
	}
	
	private void init() throws Exception
	{
		connect();
	}
	
	private void connect() throws Exception
	{
		Class.forName("org.gjt.mm.mysql.Driver");
		conn								= DriverManager.getConnection(db_url, db_id, db_pass);
	}
	
	public String selectOne(String query, String columnName)
	{
		String retValue = null;
		Statement stmt = null;
		
		try{
			stmt	= conn.createStatement();
			ResultSet r_set	= stmt.executeQuery(query);
			
			r_set.first();
			retValue = r_set.getString(columnName);
			
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}

		return retValue;
	}
	
	public void selectAll(String query, String columnName, ArrayList retValues)
	{
		Statement stmt = null;
		
		try{
			stmt	= conn.createStatement();
			ResultSet r_set	= stmt.executeQuery(query);
			
			while(r_set.next()){
				retValues.add(r_set.getString(columnName));
			}
			
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void selectAllMult(String query, int columnCnt, ArrayList<String[]> retValues)
	{
		Statement stmt = null;
		String[] retValue = new String[columnCnt];
		try{
			stmt	= conn.createStatement();
			ResultSet r_set	= stmt.executeQuery(query);
			
			while(r_set.next()){
				for(int i=0;i<columnCnt;i++){
					retValue[i] = r_set.getString(i+1);
				}
				retValues.add(retValue);
				retValue = new String[columnCnt];
			}
			
			stmt.close();
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			try{
				stmt.close();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void select_prepared(String query) throws Exception
	{
		PreparedStatement p_stmt			= conn.prepareStatement(query);
		
		//setString, setInt 등으로 p_stmt의 인자를 세팅
		
		ResultSet r_set						= p_stmt.executeQuery();
		
		//결과 ROW 개수만큼 루프
		while(r_set.next())
		{
			//getString, getInt 등의 함수로 결과 불러옴.
		}
		
		p_stmt.close();
	}
	
	public boolean insert(String query) throws Exception
	{
		Statement stmt						= conn.createStatement();
		boolean is_success					= stmt.execute(query);

		stmt.close();
		
		return is_success;
	}
	
	public void close()
	{
		if(conn == null)
			return;
		
		try{
			conn.close();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
