package app.logic.pojo;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by apple on 17/4/27.
 */

public class ContactInfo implements Serializable {
    /**联系人名**/
    private String mName;
    /**联系人电话号码**/
    private String mPhoneNumber;
    /**联系人ID**/
    private String mId;
    /**联系人名字字母**/
    private String mNameLetter;
    private int mOperatorType; //运营商判断，可有可无
    public void setLetter(String letter) {
        mNameLetter = letter;
    }
    public String getLetter() {
        return mNameLetter;
    }
    public void setName(String name) {
        mName = name;
    }
    public String getName() {
        return mName == null? "" : mName;
    }
    public void setPhoneNumber(String number) {
        mPhoneNumber = number;
        if (isMobileNumber(number) || isUnicomNumber(number)) {
            setmOperatorType(0);
        } else {
            setmOperatorType(1);
        }
    }
    public String getPhoneNumber() {
        return mPhoneNumber;
    }
    public void setId(String id) {
        mId = id;
    }
    public String getId() {
        return mId;
    }
    public int getmOperatorType() {
        return mOperatorType;
    }
    public void setmOperatorType(int mOperatorType) {
        this.mOperatorType = mOperatorType;
    }
    public static final String MOBILENUMBER_EX = "^1(3[4-9]|4[7]|5[012789]|8[23478]|7[8])\\d{8}$";//移动
    public static final String TELECOMNUMBER_EX = "^1(3[3]|5[3]|8[019]|9[8])\\d{8}$";//电信
    public static final String UNICOMNUMBER_EX = "^1(3[012]|4[5]|5[56]|8[56])\\d{8}$";//联通
    public static final String MOBILENUMBER_EX_2 = "^1(3[4-9]|4[7]|5[012789]|8[23478]|7[8])\\d*$";//移动
    public static boolean isMobileNumber(String caller){
        return ex(caller, MOBILENUMBER_EX);
    }
    public static boolean isUnicomNumber(String caller){
        return ex(caller, UNICOMNUMBER_EX);
    }
    public static boolean isTelecomNumber(String caller){
        return ex(caller, TELECOMNUMBER_EX);
    }
    public static boolean ex(String caller,String ex){
        Pattern pattern = Pattern.compile(ex);
        Matcher matcher = pattern.matcher(caller);
        return matcher.matches();
    }
}
