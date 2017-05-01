package org.ql.utils.db;

import java.util.Map;

interface DBInterface {

/////////////////////////////////////////////GET//////////////////////////////////////////////////////////
	public boolean getBoolean(String key);
	
	public boolean getBoolean(String key,boolean defValue);
	
	public  int getInt(String key);
	
    public int getInt(String key,int defValue) ;

    public double getDouble(String key);
    
    public double getDouble(String key,double defValue);
    
    public float getFloat(String key);
    
    public float getFloat(String key,float defValue);
    
    public long getLong(String key);
    
    public long getLong(String key,long defValue);
    
    public String getString(String key);
    
    public String getString(String key,String defValue);
    
/////////////////////////////////////////////GET//////////////////////////////////////////////////////////
/////////////////////////////////////////////SET//////////////////////////////////////////////////////////
    
	public boolean putBoolean(String key,boolean value);
	
	public boolean putInt(String key,int value);
    
	public boolean putDouble(String key,double value);
	
	public boolean putFloat(String key,float value);
	
	public boolean putLong(String key,long value);
	
	public boolean putString(String key,String value);
    
/////////////////////////////////////////////SET//////////////////////////////////////////////////////////
/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////
	
	public boolean remove(String key);
	
	public boolean clear();
	
	public Map<String,?> getAll();
	
	public boolean contains(String key);
	
	public boolean isEmpty();
	
/////////////////////////////////////////////OTHER//////////////////////////////////////////////////////////
}
