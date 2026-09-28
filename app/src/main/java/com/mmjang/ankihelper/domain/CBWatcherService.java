package com.mmjang.ankihelper.domain;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ClipboardManager.OnPrimaryClipChangedListener;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.mmjang.ankihelper.MyApplication;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.ui.popup.PopupActivity;
import com.mmjang.ankihelper.util.Constant;

public class CBWatcherService extends Service {
    private final OnPrimaryClipChangedListener listener = this::performClipboardCheck;
    private ClipboardManager pm;

    @Override
    public void onCreate() {
        pm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        pm.addPrimaryClipChangedListener(listener);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
        stopForeground(true);
        Log.d("CB", "onDestroy ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        pm.addPrimaryClipChangedListener(listener);
        //notification
        String CHANNEL_ONE_ID = "com.mmjang.ankihelper";
        String CHANNEL_ONE_NAME = "CBService";
        NotificationChannel notificationChannel = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.enableLights(false);
            notificationChannel.setShowBadge(true);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (manager != null) {
                manager.createNotificationChannel(notificationChannel);
            }
        }
        long[] vibList = new long[1];
        vibList[0] = 10L;
        Intent intentStart = new Intent(getApplicationContext(), PopupActivity.class);
        intentStart.setAction(Intent.ACTION_SEND);
        intentStart.setType("text/plain");
        intentStart.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentStart.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intentStart.putExtra(Intent.EXTRA_TEXT, Constant.USE_CLIPBOARD_CONTENT_FLAG);
        int pendingIntentFlags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            pendingIntentFlags |= PendingIntent.FLAG_IMMUTABLE;
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intentStart, pendingIntentFlags);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ONE_ID)
                .setSmallIcon(R.drawable.icon_light)
                .setContentTitle(getResources().getText(R.string.app_name))
                .setContentIntent(pendingIntent)
                .setWhen(System.currentTimeMillis());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            builder = builder.setContentText(getString(R.string.str_clipboard_service_running_version_q));
        } else {
            builder = builder.setContentText(getString(R.string.str_clipboard_service_running));
        }
        Notification noti = builder.build();
        noti.flags |= Notification.FLAG_FOREGROUND_SERVICE;
        startForeground(2333, noti);
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void performClipboardCheck() {
        Log.d("clip", "clip_changed");
        if (!Settings.getInstance(MyApplication.getContext()).getMoniteClipboardQ()) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10+ (API 29+) 系统限制后台服务直接读取剪切板与后台启动 Activity
            // 用户需通过点击前台服务通知弹出划词界面
            return;
        }
        ClipboardManager cb = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (cb != null && cb.hasPrimaryClip()) {
            ClipData clipData = cb.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                CharSequence textSeq = clipData.getItemAt(0).coerceToText(this);
                if (textSeq != null) {
                    String text = textSeq.toString();
                    Intent intent = new Intent(getApplicationContext(), PopupActivity.class);
                    intent.setAction(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    intent.putExtra(Intent.EXTRA_TEXT, text);
                    try {
                        startActivity(intent);
                    } catch (Exception e) {
                        Log.e("CBWatcherService", "Cannot start activity from background", e);
                    }
                }
            }
        }
    }

    static boolean isEnglish(String content) {
        //String puncs = "!()_+{}|:<>?-=[]\;.,\\\"";
        double nonEnglishCharThreahold = 0.2;
        //判断是否是网址
        String[] urlHeads = {
                "http://",
                "https://",
                "ftp://"
        };

        for (String s : urlHeads) {
            if (content.startsWith(s)) {
                return false;
            }
        }


        int len = content.length();
        if (len == 0) {
            return false;
        }
        int notEnglishCount = 0;
        for (int i = 0; i < len; i++) {
            char c = content.charAt(i);
            //isPunctuationOrBlank(c);
            if ((c <= 'z' && c >= 'a') || (c <= 'Z' && c >= 'A') || (c <= '9' && c >= '0') || isPunctuationOrBlank(c)) {

            } else {
                notEnglishCount++;
            }
        }
        double ratio = ((double) notEnglishCount) / ((double) len);
        return ratio <= nonEnglishCharThreahold;
    }

    public static boolean isPunctuationOrBlank(char c) {
        return c == ' '
                || c == ','
                || c == '.'
                || c == '!'
                || c == '?'
                || c == ':'
                || c == ';'
                || c == '('
                || c == ')'
                || c == '~'
                || c == '"'
                || c == '“'
                || c == '”'
                ;
    }
}
