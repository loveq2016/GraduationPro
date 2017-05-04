package org.ql.utils.network;


public class QLHttpReply {
	
	/**
	 * The reply code.
	 */
	private int code = 0;
	
	private Object replyMsg;
	
	private boolean cache;
	
	public boolean isCache() {
		return cache;
	}

	public void setCache(boolean cache) {
		this.cache = cache;
	}

	public int getCode() {
		return code;
	}
	
	public Object getReplyMsg() {
		return replyMsg;
	}
	
	public String getReplyMsgAsString() {
		return (String)replyMsg;
	}

	public void setReplyMsg(Object replyMsg) {
		this.replyMsg = replyMsg;
	}

	public void setCode(int code) {
		this.code = code;
	}



	/**
	 * code=200-299之间返回true
	 * Returns true if the code of the reply is in the range of success codes (2**).
	 * @return true if the code of the reply is in the range of success codes (2**).
	 */
	public boolean isSuccessCode() {
		int aux = code - 200;
		return aux >= 0 && aux < 100;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(getClass().getName());
		buffer.append(" [");
		buffer.append("code="+code);
		buffer.append(", replyMsg="+replyMsg);
		buffer.append("] ");
		return buffer.toString();
	}
}
