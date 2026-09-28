package com.mmjang.ankihelper.util;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;

/**
 * Created by Gao on 2017/6/27.
 */

public class DialogUtil {

    public static void showStartAnkiDialog(Context activityContext) {
        new AlertDialog.Builder(activityContext)
                .setMessage(activityContext.getString(R.string.plan_anki_not_started))
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                MyApplication.getAnkiDroid().startAnkiDroid();
                            }
                        })
                .show();
    }
}
