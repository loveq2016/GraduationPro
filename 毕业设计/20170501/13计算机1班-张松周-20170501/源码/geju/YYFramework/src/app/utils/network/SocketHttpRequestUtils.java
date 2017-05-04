package app.utils.network;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.ql.utils.debug.QLLog;

import android.text.TextUtils;

/**
 * 模拟Http协议提交请求，主要是拿来上传文件到服务器用的
 * 普通Http请求可以用API
 * 
 * 比如可以模拟提交如下表单：
 * <form method="post" action="http://192.168.1.101:8888/upload/doUpload" enctype="multipart/form-data">
 *       <input type="text" name="username">
 *       <input type="text" name="id">
 *       <input type="file" name="upload_image"/>
 *       <input type="file" name="upload_zip"/>
 * </form>
 * 
 * @author happen
 */
public class SocketHttpRequestUtils {
	public static final String TAG = "SocketHttpRequestUtils";
	
	/**
	 * 数据分割线
	 */
	private static final String  SET_BOUNDARY = "---------------------------7da2137580612";
	private static final String DATA_BOUNDARY = "-----------------------------7da2137580612";
	private static final String DATA_END_LINE = "-----------------------------7da2137580612--";
	
	/**
	 * Http Post方式请求
	 * @param urlPath
	 * @param requestForm
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	public static HashMap<String, String> post(String urlPath, HttpRequestForm requestForm) 
									throws UnknownHostException, IOException {
        URL url = new URL(urlPath);
        int port = (url.getPort() == -1 ? 8080 : url.getPort());
        Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
        OutputStream os = socket.getOutputStream();

        // 写出HTTP Header
        os.write(createHttpHeader(url, requestForm).getBytes());
        // 写出文本参数
        os.write(createTextParamsInfo(requestForm.getTextParams()).getBytes());
        // 写出文件参数和文件数据
        int len = 0;
        byte[] buffer = new byte[1024];
        for (HttpFormFile formFile : requestForm.getFileParams()) {
        	InputStream is;
        
        	// 写出文件信息
        	os.write(createFileParamInfo(formFile).getBytes());
        	// 写出文件数据
        	is = new FileInputStream(formFile.getFile());
        	while ((len = is.read(buffer, 0, 1024)) != -1) {
        		os.write(buffer, 0, len);
        	}
        	is.close();
        	
        	// 写出换行
        	os.write("\r\n".getBytes());
        }
        // 写出结尾数据分割线
        os.write((DATA_END_LINE + "\r\n").getBytes());
        os.flush();
        
        // 处理服务器响应
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuffer sbBuffer = new StringBuffer();
        String tmp = null;
//        03-20 14:48:01.150: I/(27896): HTTP/1.1 200 OK
//        03-20 14:48:01.150: I/(27896): Date: Thu, 20 Mar 2014 06:45:19 GMT
//        03-20 14:48:01.150: I/(27896): Server: Microsoft-IIS/6.0
//        03-20 14:48:01.150: I/(27896): X-Powered-By: ASP.NET
//        03-20 14:48:01.150: I/(27896): X-AspNet-Version: 4.0.30319
//        03-20 14:48:01.150: I/(27896): Cache-Control: private
//        03-20 14:48:01.150: I/(27896): Content-Type: text/html; charset=utf-8
//        03-20 14:48:01.150: I/(27896): Content-Length: 12

        HashMap<String, String> responseHeader = new HashMap<String, String>();
        while (!TextUtils.isEmpty((tmp = br.readLine()))) {
			sbBuffer.append(tmp);
			try{
				String[] _tmpListStrings = tmp.split(":");
				if (_tmpListStrings != null && _tmpListStrings.length > 1) {
					responseHeader.put(_tmpListStrings[0], _tmpListStrings[1]);
				}
			}catch(NullPointerException e){}
			tmp = null;
		}
        String responseHeadString = sbBuffer.toString();
        QLLog.i(TAG+"00000000", responseHeadString);
//         读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
//        if(responseHeadString == null || responseHeadString.indexOf("200") == -1) {
//            return false;
//        }
        // 关闭打开的流
        os.close();
        br.close();
        socket.close();
		
		return responseHeader;
	}

	/**
	 * 生成请求信息
	 * @param textParams
	 * @return String
	 */
	private static String createHttpHeader(URL url, HttpRequestForm requestForm) {
		StringBuilder httpHeader = new StringBuilder();
		long contentLength = getContentLength(requestForm);
		int port = (url.getPort() == -1 ? 8080 : url.getPort());
		
		// Request Method
        httpHeader.append("POST " + url.getPath() + " HTTP/1.1\r\n");
        // Accept 
        httpHeader.append("Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg"
        		+ ", application/x-shockwave-flash, application/xaml+xml"
        		+ ", application/vnd.ms-xpsdocument, application/x-ms-xbap"
        		+ ", application/x-ms-application, application/vnd.ms-excel"
        		+ ", application/vnd.ms-powerpoint, application/msword, */*\r\n");
        httpHeader.append("Accept-Language: zh-CN\r\n");
        // ContentType，Boundary 
        // 注意：boundary前面少2个--
        httpHeader.append("Content-Type: multipart/form-data; boundary=" + SET_BOUNDARY + "\r\n");
        // Content-Length
        httpHeader.append("Content-Length: "+ contentLength + "\r\n");
        // Connection
        httpHeader.append("Connection: Keep-Alive\r\n");
        // Host
        httpHeader.append("Host: "+ url.getHost() + ":" + port +"\r\n\r\n");
		
		return httpHeader.toString();
	}
	
	/**
	 * 生成文本类型参数的模拟信息
	 * @param textParams
	 * @return String
	 */
	private static String createTextParamsInfo(Map<String, String> textParams) {
		StringBuilder textInfoEntity = new StringBuilder();
		
		// 得到所有文本类型参数
		for (Map.Entry<String, String> param : textParams.entrySet()) {
			textInfoEntity.append(DATA_BOUNDARY + "\r\n");
			textInfoEntity.append("Content-Disposition: form-data; name=\""+ param.getKey() + "\"\r\n");
			textInfoEntity.append("\r\n");
			textInfoEntity.append(param.getValue() + "\r\n");
		}
		
		return textInfoEntity.toString();
	}
	
	/**
	 * 生成文件类型参数的的模拟信息，这里没用包含文件数据
	 * @param formFile
	 * @return String
	 */
	private static String createFileParamInfo(HttpFormFile formFile) {
        StringBuilder fileInfoEntity = new StringBuilder();
     
		fileInfoEntity.append(DATA_BOUNDARY + "\r\n");
		fileInfoEntity.append("Content-Disposition: form-data; name=\"" + formFile.getParamName() + "\"; ");
		fileInfoEntity.append("filename=\""+ formFile.getFile().getName() + "\"\r\n");
		fileInfoEntity.append("Content-Type: "+ formFile.getContentType() + "\r\n\r\n");
		
		return fileInfoEntity.toString();
	}

	/**
	 * 得到整个提交表单数据的长度
	 * @param requestForm
	 * @return String
	 */
	private static long getContentLength(HttpRequestForm requestForm) {
		long length;
		
		// 文本表单
		length = createTextParamsInfo(requestForm.getTextParams()).getBytes().length;
		
		// 文件表单和文件数据大小
		for (HttpFormFile formFile : requestForm.getFileParams()) {
			length += createFileParamInfo(formFile).getBytes().length;
			length += formFile.length();
		}
		
		// 结尾数据分割线
		length += (DATA_END_LINE + "\r\n").getBytes().length;
		
		return length;
	}
}
