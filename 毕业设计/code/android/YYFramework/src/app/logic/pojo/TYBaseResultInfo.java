package app.logic.pojo;

/**
 * 
 * @author SiuJiYung
 *
 */
public class TYBaseResultInfo {
	private boolean success;
	private String msg;
	private int code;
	private String result;
	

	public boolean isSuccess() {
		return success;
	}

	public String getMsgAsString() {
		return (String)msg;
	}
	
	public Object getMsgAsObj(){
		return msg;
	}
	
	public String getMsg(){
		return msg;
	}

	public int getCode() {
		return code;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	

	
}
