package app.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import app.yy.geju.R;


/**
 * Created by GZYY on 17/1/9.
 */

public class DialogLoading extends Dialog {


    public DialogLoading(Context context) {
        super(context, R.style.sex_dialog);
    }

    public DialogLoading(Context context, int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_loading);

    }
}
