package app.utils.db.sqlite;


public class DbMapEntity implements java.io.Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4976796020482895496L;
	
	private int id;
	
	private String key;
	
	private String value;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getValue() {
		return value;
	}
	
	public void setValue(String value) {
		this.value = value;
	}
	
}
