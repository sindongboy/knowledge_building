package com.skplanet.omp.knowledgeBuilding.dict;

public class CategoryDict {
	
	private int MAX_CATEGORY_PATH_LEN		= 5;

	private int category_id					= 0;
	
	private	String category_name			= null;
	
	private String[] category_path_name		= null;
	
	private int[] category_path_id			= null;
			
	public CategoryDict()
	{
		init();
	}
	
	private void init()
	{
		category_id							= 0;
		
		category_name 						= new String();
		
		category_path_name					= new String[MAX_CATEGORY_PATH_LEN];
		
		category_path_id					= new int[MAX_CATEGORY_PATH_LEN];
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
		this.category_id					= Integer.parseInt(id);
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
		this.category_name					= name;
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
		String[] parr = ids.split("/");
				
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
		
		//System.out.println( parr.length);
		
		for(int i=parr.length-1,j=0; i>=0; i--) {
			if( parr[i].isEmpty() ) continue;
			
			if( parr[i].equals(null)) break;

			this.category_path_name[j++] = parr[i];
		}
	}
}
