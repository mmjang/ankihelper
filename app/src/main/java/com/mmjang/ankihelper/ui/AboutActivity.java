package com.mmjang.ankihelper.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.mmjang.ankihelper.R;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ImageButton support = (ImageButton) findViewById(R.id.btn_support);
        TextView supportMessage = (TextView) findViewById(R.id.support_message);
        supportMessage.setMovementMethod(LinkMovementMethod.getInstance());

        support.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setAction("android.intent.action.VIEW");
                        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        String payUrl = "HTTPS://QR.ALIPAY.COM/FKX07507MEX6F7W8541C56";
                        intent.setData(Uri.parse("alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=" + payUrl));
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        } else {
                            intent.setData(Uri.parse(payUrl.toLowerCase()));
                            startActivity(intent);
                        }
                    }
                }
        );
    }
}
