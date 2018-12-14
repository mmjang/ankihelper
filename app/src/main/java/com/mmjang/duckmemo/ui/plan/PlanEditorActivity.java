package com.mmjang.duckmemo.ui.plan;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.mmjang.duckmemo.R;
import com.mmjang.duckmemo.data.Settings;
import com.mmjang.duckmemo.data.database.ExternalDatabase;
import com.mmjang.duckmemo.data.plan.OutputPlanPOJO;
import com.mmjang.duckmemo.util.Constant;
import com.mmjang.duckmemo.data.dict.DictionaryRegister;
import com.mmjang.duckmemo.data.dict.IDictionary;
import com.mmjang.duckmemo.MyApplication;
import com.mmjang.duckmemo.data.plan.OutputPlan;
import com.mmjang.duckmemo.util.Utils;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PlanEditorActivity extends AppCompatActivity {

    private String planNameToEdit;
    private OutputPlanPOJO planForEdit;
    private Map<Long, String> deckList;
    private Map<Long, String> modelList;
    private List<IDictionary> dictionaryList;
    private List<FieldsMapItem> fieldsMapItemList;
    private IDictionary currentDictionary;
    private long currentDeckId;
    private long currentModelId;
    //views
    private EditText planNameEditText;
    private Spinner dictionarySpinner;
    private TextView dictionaryIntroductionTextView;
    private Spinner deckSpinner;
    private Spinner modelSpinner;
    private RecyclerView fieldsSpinnersContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(Settings.getInstance(this).getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_editor);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
