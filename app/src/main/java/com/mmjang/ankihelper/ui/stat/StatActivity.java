package com.mmjang.ankihelper.ui.stat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import androidx.core.app.NavUtils;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Switch;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.mmjang.ankihelper.R;
import com.mmjang.ankihelper.data.Settings;
import com.mmjang.ankihelper.data.history.HistoryStat;

import java.util.ArrayList;
import java.util.List;

public class StatActivity extends AppCompatActivity {
    HistoryStat mHistoryStat;
    BarChart mHourChart;
    LineChart mLastDaysChart;
    Spinner lastDaySpinner;
    ChipGroup mChipGroup;
    int mLastDays = 7;
    int[] dayMap = new int[]{1, 7, 30, 365, 3650};

    private static final int DARK_GREEN = Color.parseColor("#2d6d4b");
    private static final int DARK_PINK = Color.parseColor("#b05154");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(this);
        if(settings.getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mHistoryStat = new HistoryStat(mLastDays);
        mHourChart = findViewById(R.id.hourt_chart);
        mLastDaysChart = findViewById(R.id.last_days_chart);
        mChipGroup = findViewById(R.id.last_days_stat_chipgroup);
        lastDaySpinner = findViewById(R.id.spinner_last_days);
        plotData();

        lastDaySpinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        mLastDays = dayMap[i];
                        mHistoryStat = new HistoryStat(mLastDays);
                        mLastDaysChart.clear();
                        mHourChart.clear();
                        plotData();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                }
        );

        mChipGroup.setOnCheckedChangeListener(
                new ChipGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(ChipGroup chipGroup, int i) {
                        switch (i){
                            case R.id.chip_1:
                                mLastDays = dayMap[0];
                                break;
                            case R.id.chip_7:
                                mLastDays = dayMap[1];
                                break;
                            case R.id.chip_30:
                                mLastDays = dayMap[2];
                                break;
                            case R.id.chip_365:
                                mLastDays = dayMap[3];
                        }
                        mHistoryStat = new HistoryStat(mLastDays);
                        mLastDaysChart.clear();
                        mHourChart.clear();
                        plotData();
                    }
                }
        );
    }

    private void plotData() {
        @SuppressLint("StaticFieldLeak")
        AsyncTask<Void, Integer, List<int[][]>> asyncTask = new AsyncTask<Void, Integer, List<int[][]>>() {
            @Override
            protected List<int[][]> doInBackground(Void... voids) {
                List<int[][]> result = new ArrayList<>();
                result.add(mHistoryStat.getHourStatistics());
                result.add(mHistoryStat.getLastDaysStatistics());
                return result;
            }

            @Override
            protected void onPostExecute(List<int[][]> ints) {
                drawHourChart(ints.get(0));
                drawLastDaysChart(ints.get(1));
            }
        };
        asyncTask.execute();
    }

    private void drawLastDaysChart(int[][] data) {
        List<Entry> lookupEntries = new ArrayList<>();
        List<Entry> cardaddEntries = new ArrayList<>();
        for(int i = 0; i < data[0].length; i ++){
            lookupEntries.add(new Entry(i, data[1][i]));
            cardaddEntries.add(new Entry(i, data[2][i]));
        }
        if(lookupEntries.size() <= 1){
            mLastDaysChart.setVisibility(View.INVISIBLE);
        }else{
            mLastDaysChart.setVisibility(View.VISIBLE);
        }
        float lineWidth = 2;
        LineDataSet lineDataSet2 = new LineDataSet(lookupEntries, "Lookups");
        lineDataSet2.setColor(DARK_PINK);
        lineDataSet2.setLineWidth(lineWidth);
        lineDataSet2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawValues(false);
        LineDataSet lineDataSet3 = new LineDataSet(cardaddEntries, "Cards");
        lineDataSet3.setColor(DARK_GREEN);
        lineDataSet3.setLineWidth(lineWidth);
        lineDataSet3.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setDrawValues(false);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet2);
        dataSets.add(lineDataSet3);
        mLastDaysChart.setData(new LineData(dataSets));
        mLastDaysChart.getDescription().setText("");
        //mHourChart.getDescription().setTextAlign();
        mLastDaysChart.getXAxis().setDrawGridLines(false);
        mLastDaysChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLastDaysChart.getAxisRight().setEnabled(false);
        //mHourChart.getAxisLeft().setDrawGridLines(false);
        mLastDaysChart.getXAxis().setValueFormatter(
                new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return  + ((int)(-mLastDays + value + 1)) + "d";
                    }
                }
        );
        mLastDaysChart.getLegend().setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        mLastDaysChart.invalidate();
    }

    private void drawHourChart(int[][] data){
//        List<BarEntry> popupEntries = new ArrayList<>();
        List<BarEntry> lookupEntries = new ArrayList<>();
        List<BarEntry> cardaddEntries = new ArrayList<>();
        for(int i = 0; i < 24; i ++){
//            popupEntries.add(new BarEntry(i, data[0][i]));
            lookupEntries.add(new BarEntry(i, new float[] {data[1][i], data[2][i]}));
        }
        BarDataSet barDataSet = new BarDataSet(lookupEntries, "Bar");
        barDataSet.setStackLabels(new String[]{"Lookups", "Cards"});
        barDataSet.setDrawValues(false);
        barDataSet.setColors(DARK_PINK, DARK_GREEN);
        BarData barData = new BarData(barDataSet);
        mHourChart.setData(barData);
        mHourChart.getDescription().setText("hour");
        //mHourChart.getDescription().setTextAlign();
        mHourChart.getXAxis().setDrawGridLines(false);
        mHourChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mHourChart.getAxisRight().setEnabled(false);
        //mHourChart.getAxisLeft().setDrawGridLines(false);
        mHourChart.getXAxis().setValueFormatter(
                new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return ((int) value) + "";
                    }
                }
        );
        mHourChart.getXAxis().setLabelCount(24);
        mHourChart.getXAxis().setAxisMinimum(-0.5f);
        mHourChart.getXAxis().setAxisMaximum(23.5f);
        mHourChart.getLegend().setEnabled(false);
        mHourChart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
