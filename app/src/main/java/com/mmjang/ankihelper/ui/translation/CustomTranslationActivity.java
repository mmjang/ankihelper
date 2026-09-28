package com.mmjang.ankihelper.ui.translation;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.TextView;

import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;

public class CustomTranslationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Settings settings = Settings.getInstance(this);
        setContentView(R.layout.activity_custom_translation);
        if(Settings.getInstance(this).getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }else{
            setTheme(R.style.AppTheme);
        }
        setContentView(R.layout.activity_custom_translation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView introduction = findViewById(R.id.textview_custom_translation_introduction);
        introduction.setMovementMethod(LinkMovementMethod.getInstance());

        EditText appid = findViewById(R.id.edittext_baidufanyi_appid);
        EditText secret = findViewById(R.id.edittext_baidufanyi_key);

        appid.setText(settings.getUserBaidufanyiAppId());
        secret.setText(settings.getUserBaidufanyiAppKey());

        appid.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.setUserBaidufanyiAppId(charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );

        secret.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        settings.setUserBaidufanyiAppKey(charSequence.toString().trim());
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                }
        );
    }
}
