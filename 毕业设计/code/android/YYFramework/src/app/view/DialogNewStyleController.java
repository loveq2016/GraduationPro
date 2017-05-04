package app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import app.yy.geju.R;

/**
 * Created by GZYY on 17/1/4.
 * <p>
 * <p>
 * 新版本的对话框，contentView要自己添加
 */

public class DialogNewStyleController extends Dialog {

    /**
     * default
     */
    private final static int MODE_TITLE_EDIT = 0;
    private final static int MODE_ONLY_CONTENT = 1;

    private final static int MODE_TITLE_IMA_EDIT = 2;

    private LayoutInflater inflater;
    private View contentView;
    private Context context;

    private int mode = MODE_TITLE_EDIT;

    public DialogNewStyleController(Context context) {
        super(context, R.style.sex_dialog);
        init(context, null);
    }

    public DialogNewStyleController(Context context, View contentView) {
        super(context, R.style.sex_dialog);
        init(context, contentView);
    }

    public DialogNewStyleController(Context context, int mode) {
        super(context, R.style.sex_dialog);
        init(context, null);
        this.mode = mode;
    }


    private void init(Context context, View contentView) {
        this.contentView = contentView;
        this.context = context;
        inflater = LayoutInflater.from(context);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (contentView == null) {
            contentView = LayoutInflater.from(context).inflate(R.layout.dialog_new_edit_view, null);
        }

//        switch (mode) {
//            case MODE_TITLE_EDIT:
//                contentView = inflater.inflate(R.layout.dialog_new_edit_view, null);
//                break;
//            case MODE_ONLY_CONTENT:
//                contentView = inflater.inflate(R.layout.dialog_new_notitle_view, null);
//                break;
//            case MODE_TITLE_IMA_EDIT:
//                contentView = inflater.inflate(R.layout.dialog_add_friend_view, null);
//                break;
//        }
        setContentView(contentView);
    }


    private interface OnDialogClickListener {
        void onClick(View view);
    }

}
