package com.mmjang.ankihelperrefactor.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.ankihelperrefactor.R;
import com.mmjang.ankihelperrefactor.app.Definition;
import com.mmjang.ankihelperrefactor.app.Esdict;
import com.mmjang.ankihelperrefactor.app.IDictionary;
import com.mmjang.ankihelperrefactor.app.Ode2;
import com.mmjang.ankihelperrefactor.app.OutputPlan;

import org.litepal.crud.DataSupport;
import org.litepal.tablemanager.Connector;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;

public class TestActivity extends Activity implements View.OnClickListener{
    final Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    //Bitmap bitmap = (Bitmap) msg.obj;
                    //imageView.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(TestActivity.this, "finished!", Toast.LENGTH_SHORT).show();
                    String html =((List<Definition>) msg.obj).get(0).getDisplayHtml();
                    tv.setText(Html.fromHtml(html));
                    break;
                case 2:
                    // ...
                    break;
                default:
                    break;
            }
        }
    };

    ProgressBar progressBar;
    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        tv = (TextView) findViewById(R.id.showHtml);
        progressBar.setVisibility(View.GONE);
        View view = findViewById(R.id.plansManager);
        view.setOnClickListener(this);
        findViewById(R.id.testAsync).setOnClickListener(this);
        findViewById(R.id.open_popup).setOnClickListener(this);




    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.plansManager:
                Intent intent = new Intent(this, PlansManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.open_popup:
                Intent intent2 = new Intent(this, PopupActivity.class);
                startActivity(intent2);
                break;
            case R.id.testAsync:
                progressBar.setVisibility(View.VISIBLE);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try  {
                            //Your code goes here
                            Log.d("clicked", "yes");
                            IDictionary es = new Esdict(TestActivity.this);
                            List<Definition> d = es.wordLookup("abundancia");
                       //     Log.d("async_test", d.get(0).getDisplayHtml());
                            Message message = mHandler.obtainMessage();
                            message.obj = d;
                            message.what = 1;
                            mHandler.sendMessage(message);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                thread.start();
                break;
        }
    }

}
