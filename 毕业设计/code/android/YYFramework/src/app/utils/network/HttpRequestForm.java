package app.utils.network;
import java.util.Map;

/**
 * Http表单
 * 包含文本类型参数和文件类型参数
 * @author happen
 */
public class HttpRequestForm {
	/**
	 * 表单文本类型参数
	 */
	private Map<String, String> textParams;
	
	/**
	 * 表单文件类型参数
	 */
	private HttpFormFile[] fileParams;
	
	public HttpRequestForm(Map<String, String> textParams) {
		this.textParams = textParams;
	}
	
	public HttpRequestForm(HttpFormFile[] fileParams) {
		this.fileParams = fileParams;
	}
	
	public HttpRequestForm(HttpFormFile fileParams) {
		this(new HttpFormFile[] {fileParams});
	}
	
	public HttpRequestForm(Map<String, String> textParams, HttpFormFile[] fileParams) {
		this.textParams = textParams;
		this.fileParams = fileParams;
	}
	
	public HttpRequestForm(Map<String, String> textParams, HttpFormFile fileParams) {
		this(textParams, new HttpFormFile[] {fileParams});
	}
	
	public Map<String, String> getTextParams() {
		return textParams;
	}

	public void setTextParams(Map<String, String> textParams) {
		this.textParams = textParams;
	}

	public HttpFormFile[] getFileParams() {
		return fileParams;
	}

	public void setFileParams(HttpFormFile[] fileParams) {
		this.fileParams = fileParams;
	}
}
