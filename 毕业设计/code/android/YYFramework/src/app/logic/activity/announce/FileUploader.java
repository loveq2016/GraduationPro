package app.logic.activity.announce;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

/**
 * Created by pexcn on 2017-04-01.
 */
public class FileUploader {
    private static final String BOUNDARY = "*****";
    private static final String CRLF = "\r\n";
    private static final String TWO_HYPHENS = "--";

//    private static Handler sHandler = new Handler(Looper.getMainLooper());

    /**
     * 上传文件到服务器
     */
    public static void uploadFile(final File file, final String RequestURL, final Map<String, String> param, final Callback callback) {

                String BOUNDARY = UUID.randomUUID().toString();
                String PREFIX = "--", LINE_END = "\r\n";
                String CONTENT_TYPE = "multipart/form-data";   //内容类型
                try {
                    URL url = new URL(RequestURL);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(30000);
                    conn.setConnectTimeout(30000);
                    conn.setDoInput(true);  //允许输入流
                    conn.setDoOutput(true); //允许输出流
                    conn.setUseCaches(false);  //不允许使用缓存
                    conn.setRequestMethod("POST");  //请求方式
                    conn.setRequestProperty("Charset", "utf-8");  //设置编码
                    conn.setRequestProperty("connection", "keep-alive");
                    conn.setRequestProperty("Content-Type", CONTENT_TYPE + ";boundary=" + BOUNDARY);
                    if (file != null) {
                        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                        StringBuffer sb = new StringBuffer();

                        String params = "";
                        if (param != null && param.size() > 0) {
                            Iterator<String> it = param.keySet().iterator();
                            while (it.hasNext()) {
                                sb = null;
                                sb = new StringBuffer();
                                String key = it.next();
                                String value = param.get(key);
                                sb.append(PREFIX).append(BOUNDARY).append(LINE_END);
                                sb.append("Content-Disposition: form-data; name=\"").append(key).append("\"").append(LINE_END).append(LINE_END);
                                sb.append(value).append(LINE_END);
                                params = sb.toString();
                                Log.i("TAG", key + "=" + params + "##");
                                dos.write(params.getBytes());
                            }
                        }
                        sb = new StringBuffer();
                        sb.append(PREFIX);
                        sb.append(BOUNDARY);
                        sb.append(LINE_END);
                        /**
                         * 这里重点注意：
                         * name里面的值为服务器端需要key, 只有这个key才可以得到对应的文件
                         * filename是文件的名字，包含后缀名的
                         */
                        sb.append("Content-Disposition: form-data; name=\"upfile\";filename=\"" + file.getName() + "\"" + LINE_END);
                        sb.append("Content-Type: image/pjpeg; charset=" + "utf-8" + LINE_END);
                        sb.append(LINE_END);
                        dos.write(sb.toString().getBytes());
                        InputStream is = new FileInputStream(file);
                        byte[] bytes = new byte[1024];
                        int len = 0;
                        while ((len = is.read(bytes)) != -1) {
                            dos.write(bytes, 0, len);
                        }
                        is.close();
                        dos.write(LINE_END.getBytes());
                        byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINE_END).getBytes();
                        dos.write(end_data);
                        dos.flush();

                        int res = conn.getResponseCode();
                        System.out.println("res=========" + res);
                        if (res == 200) {
                            InputStream input = conn.getInputStream();
                            final StringBuffer sb1 = new StringBuffer();
                            int ss;
                            while ((ss = input.read()) != -1) {
                                sb1.append((char) ss);
                            }
//                            sHandler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    callback.onSuccess(sb1.toString());
//                                }
//                            });
                            callback.onSuccess(sb1.toString());
                        }
                    }
                } catch (IOException e) {
                    callback.onFailed(e);
                    e.printStackTrace();
                }

    }

    public interface Callback {
        void onSuccess(String data);

        void onFailed(Exception e);
    }
}
