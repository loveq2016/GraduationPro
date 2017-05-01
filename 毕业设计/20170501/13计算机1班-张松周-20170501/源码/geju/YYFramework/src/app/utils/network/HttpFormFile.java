package app.utils.network;
import java.io.File;

/**
 * Http 表单文件参数
 * 
 * 比如：
 * <input type="file" name="upload_image"/>
 * <input type="file" name="upload_zip"/>
 * 
 * @author happen
 * 
 */
public class HttpFormFile {
	private File file;
	
	/**
	 * 参数名
	 */
	private String paramName;
	
	/**
	 * 内容类型
	 */
	private String contentType = "application/octet-stream";
	
	public HttpFormFile(String filePath, String paramName, String contentType) {
		this(paramName, contentType);
		file = new File(filePath);
	}
	
	public HttpFormFile(String paramName, File file, String contentType) {
		this(paramName, contentType);
		this.file = file;
	}
	
	private HttpFormFile(String paramName, String contentType) {
		this.paramName = paramName;
		setContentType(contentType);
	}

	/**
	 * 得到文件字节大小
	 * @return long 文件字节大小
	 */
	public long length() {
		return file.length();
	}
	
	public void setParamName(String paramName) {
		this.paramName = paramName;
	}
	
	public String getParamName() {
		return paramName;
	}

	public File getFile() {
		return file;
	}
	
	public void setFile(File file) {
		this.file = file;
	}
	
	public void setFile(String fileName) {
		this.file = new File(fileName);
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = (contentType == null ? this.contentType : contentType);
	}
}