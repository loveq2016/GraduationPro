package app.logic.activity.friends;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import app.logic.pojo.ContactInfo;

/**
 * Created by apple on 17/4/27.
 */

public class ContactsFetcherHelper implements Runnable {
    private static final String TAG = ContactsFetcherHelper.class.getSimpleName();

    public interface OnFetchContactsListener {
        void onFetcherContactsComplete(List<ContactInfo> list);
    }

    /**
     * 查询联系人信息
     * @param context
     */
    public static void queryContactInfo(final Context context,OnFetchContactsListener listener) {

        ContactsFetcherHelper mContactsFetcherHelper = new ContactsFetcherHelper();
        mContactsFetcherHelper.start(context,listener);
//        mContactsFetcherHelper.start(context, new ContactsFetcherHelper.OnFetchContactsListener() {
//
//            @Override
//            public void onFetcherContactsComplete(final List<ContactInfo> list) {
//                if (list != null && list.size() > 0) {
//                    Log.d(TAG, "list.size() = " + list.size());
//
//                    for (ContactInfo ci:list) {
//
//                        Log.d(TAG, "ci.getPhoneNumber() = " + ci.getPhoneNumber()+"  ci.getName() = "+ci.getName());
//                    }
//
//                }
//                else {
//                    Log.d(TAG, "list = " + list);
//                }
//            }
//        });
    }

    private OnFetchContactsListener mListener;
    private boolean mCancel = false;
    private Context mContext;
    private boolean mIsFetching = false;
    private Thread mFetchThread;

    public void start(Context context, OnFetchContactsListener l) {
        if (mIsFetching) {
            return;
        }
        mContext = context;
        mCancel = false;
        mIsFetching = true;
        mListener = l;
        mFetchThread = new Thread(this);
        mFetchThread.start();
    }

    public void cancel() {
        mCancel = true;
    }

    @Override
    public void run() {
        List<ContactInfo> list = new ArrayList<ContactInfo>();
        Set<String> set = new HashSet<String>();
        if (!mCancel) {
            Log.d(TAG, "getPhoneContactHighVersion");
            //读取手机里的手机联系人
            getPhoneContactHighVersion(list, set);
        }
        if (!mCancel) {
            Log.d(TAG, "getSimContact");
            //读取Sim卡中的手机联系人
            getSimContact("content://icc/adn", list, set);
        }

        if (!mCancel) {
            Log.d(TAG, "getSimContact");
            getSimContact("content://sim/adn", list, set);
        }
        if (!mCancel && null != mListener) {
            mIsFetching = false;
            mListener.onFetcherContactsComplete(list);
        }
    }

    // 从本机中取号
    private void getPhoneContactHighVersion(List<ContactInfo> list,
                                            Set<String> set) {
        // 得到ContentResolver对象
        try {
            if (null == mContext) {
                return;
            }
            ContentResolver cr = mContext.getContentResolver();
            if (null == cr) {
                return;
            }
            // 取得电话本中开始一项的光标
            Uri uri = ContactsContract.Contacts.CONTENT_URI;
            Cursor cursor = null;
            try {
                String[] projection = { ContactsContract.Contacts._ID,
                        ContactsContract.Contacts.DISPLAY_NAME,"sort_key"};
                cursor = cr.query(uri, projection, null, null,
                        null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            while (!mCancel && null != cursor && cursor.moveToNext()) {
                int nameFieldColumnIndex = cursor
                        .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                int idCol = cursor.getInt(cursor
                        .getColumnIndex(ContactsContract.Contacts._ID));
                int sort_key_index = cursor.getColumnIndex("sort_key");
                Log.d("BBB", "sort_key_index=" + sort_key_index);

                // 取得联系人名字
                // 取得联系人ID
                Cursor phone = cr.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=?",
                        new String[] { Integer.toString(idCol) }, null);//
                // 再类ContactsContract.CommonDataKinds.Phone中根据查询相应id联系人的所有电话；

                // 取得电话号码(可能存在多个号码)
                while (!mCancel && phone.moveToNext()) {
                    String strPhoneNumber = formatMobileNumber(phone
                            .getString(phone
                                    .getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)));
                    boolean b = isUserNumber(strPhoneNumber);
                    if (b) {
                        ContactInfo cci = new ContactInfo();
                        Log.d("BBB", "getPhoneContactHighVersion strPhoneNumber=" + strPhoneNumber);
                        cci.setName(cursor.getString(nameFieldColumnIndex));
                        cci.setLetter(cursor.getString(sort_key_index));
                        Log.d("BBB", "letter=" + cci.getLetter());
                        cci.setPhoneNumber(strPhoneNumber);
                        cci.setId(String.valueOf(idCol));
                        list.add(cci);
                        set.add(cci.getPhoneNumber());
                    }
                }
                phone.close();
            }
            if(null != cursor) {
                cursor.close();
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void getSimContact(String adn, List<ContactInfo> list,
                               Set<String> set) {
        // 读取SIM卡手机号,有两种可能:content://icc/adn与content://sim/adn
        Cursor cursor = null;
        try {

            Intent intent = new Intent();
            intent.setData(Uri.parse(adn));
            Uri uri = intent.getData();
            Log.d("getSimContact uri= ", uri.toString());
            cursor = mContext.getContentResolver().query(uri, null,
                    null, null, null);
            if (cursor != null) {
                while (!mCancel && cursor.moveToNext()) {
                    // 取得联系人名字
                    int nameIndex = cursor.getColumnIndex("name");
                    // 取得电话号码
                    int numberIndex = cursor.getColumnIndex("number");

                    //得到联系人头像ID
//                    int photoid = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_ID);

                    String number = cursor.getString(numberIndex);
                    Log.d("simContact nameIndex=",number);
                    if (isUserNumber(number)) {// 是否是手机号码
                        ContactInfo sci = new ContactInfo();
                        sci.setPhoneNumber(formatMobileNumber(number));
                        sci.setName(cursor.getString(nameIndex));
                        if (!isContain(set, sci.getPhoneNumber())) {// //是否在LIST内存在
                            list.add(sci);
                            set.add(sci.getPhoneNumber());
                        }
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
            if(cursor != null)
                cursor.close();
        }
    }

    private String formatMobileNumber(String num2) {
        String num;
        if (num2 != null) {
            // 有的通讯录格式为135-1568-1234
            num = num2.replaceAll("-", "");
            // 有的通讯录格式中- 变成了空格
            num = num.replaceAll(" ", "");
            if (num.startsWith("+86")) {
                num = num.substring(3);
            } else if (num.startsWith("86")) {
                num = num.substring(2);
            } else if (num.startsWith("86")) {
                num = num.substring(2);
            }
        } else {
            num = "";
        }
        // 有些手机号码格式 86后有空格
        return num.trim();
    }

    private boolean isUserNumber(String num) {
        if (null == num || "".equalsIgnoreCase(num)) {
            return false;
        }
        boolean re = false;
        if (num.length() == 11) {
            if (num.startsWith("1")) {
                re = true;
            }
        }
        return re;
    }

    private boolean isContain(Set<String> set, String un) {
        return set.contains(un);
    }
}
