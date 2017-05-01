package app.utils.helpers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.WindowManager;

/**
 * SiuJiYung create at 2016年6月28日 下午5:47:10
 */

public class YYUtils {

    /**
     * 是否是手机号码
     *
     * @param txt
     * @return
     */
    public static boolean isMobilePhoneNumber(String txt) {
        return matchRex(txt, "^((13[0-9])|(15[^4,//D])|(18[0,5-9]))//d{8}$");
    }

    /**
     * 是否是固定电话
     *
     * @param txt
     * @return
     */
    public static boolean isTelephoneNumber(String txt) {
        return matchRex(txt, "\\d{3}-\\d{8}|\\d{4}-\\d{7}");
    }

    /**
     * 是否是合法的IP地址
     *
     * @param txt
     * @return
     */
    public static boolean isIPAddress(String txt) {
        return matchRex(txt, "\\d+\\.\\d+\\.\\d+\\.\\d+");
    }

    /**
     * 是否是Email
     *
     * @param txt
     * @return
     */
    public static boolean isEmail(String txt) {
        return matchRex(txt, "\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
    }

    /**
     * 是否是中国邮政编码
     *
     * @param txt
     * @return
     */
    public static boolean isZNPostCode(String txt) {
        return matchRex(txt, "[1-9]\\d{5}(?!\\d)");
    }

    /**
     * 是否是腾讯QQ号码
     *
     * @param txt
     * @return
     */
    public static boolean isTencenQQNumber(String txt) {
        return matchRex(txt, "[1-9][0-9]{4,}");
    }

    /**
     * 是否是URL
     *
     * @param txt
     * @return
     */
    public static boolean isURL(String txt) {
        return matchRex(txt, "[a-zA-z]+://[^\\s]*");
    }

    /**
     * 匹配帐号是否合法(字母开头，允许5-16字节，允许字母数字下划线)
     *
     * @param txt
     * @return
     */
    public static boolean canUserForAccount(String txt) {
        return matchRex(txt, "^[a-zA-Z][a-zA-Z0-9_]{4,15}");
    }

    /**
     * 是否为居民身份证号码
     *
     * @param txt
     * @return
     */
    public static boolean isZNIDCardNumber(String txt) {
        return matchRex(txt, "\\d{15}|\\d{18}");
    }

    /**
     * 测试字符串是否符合某个正则表达式
     *
     * @param src
     * @param rex
     * @return
     */
    public static boolean matchRex(String src, String rex) {
        Pattern p = Pattern.compile(rex);
        Matcher m = p.matcher(src);
        return m.matches();
    }

    /**
     * 检测是否有中文字符
     *
     * @param txt
     * @return
     */
    public static boolean isContainChinese(String txt) {
        Pattern p = Pattern.compile("[\\u4e00-\\u9fa5]");
        Matcher m = p.matcher(txt);
        if (m.find()) {
            return true;
        }
        return false;
    }

    /**
     * dp转换成px
     *
     * @param dp
     * @param context
     * @return
     */
    public static int dp2px(int dp, Context context) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * 获取屏幕size
     *
     * @param act
     * @return
     */
    @SuppressLint("NewApi")
    public static Point getDisplaySize(Activity act) {
        WindowManager m = act.getWindowManager();
        Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point outSize = new Point();
            d.getSize(outSize);
            return outSize;
        } else {
            Point p = new Point(d.getWidth(), d.getHeight());
            return p;
        }

    }

}
