package com.mmjang.ankihelper.ui.stat;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
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
    LineChart mHourChart;
    LineChart mLastDaysChart;
    Spinner lastDaySpinner;
    int mLastDays = 30;
    int[] dayMap = new int[]{7, 30, 365, 3650};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Settings settings = Settings.getInstance(this);
        if(settings.getPinkThemeQ()){
            setTheme(R.style.AppThemePink);
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);
        mHistoryStat = new HistoryStat(mLastDays);
        mHourChart = findViewById(R.id.hourt_chart);
        mLastDaysChart = findViewById(R.id.last_days_chart);
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
        float lineWidth = 2;
        LineDataSet lineDataSet2 = new LineDataSet(lookupEntries, "Lookups");
        lineDataSet2.setColor(Color.GREEN);
        lineDataSet2.setLineWidth(lineWidth);
        lineDataSet2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawValues(false);
        LineDataSet lineDataSet3 = new LineDataSet(cardaddEntries, "Cards");
        lineDataSet3.setColor(Color.RED);
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
                        return "-" + ((int)(mLastDays - value - 1)) + "d";
                    }
                }
        );
        mLastDaysChart.invalidate();
    }

    private void drawHourChart(int[][] data){
        List<Entry> popupEntries = new ArrayList<>();
        List<Entry> lookupEntries = new ArrayList<>();
        List<Entry> cardaddEntries = new ArrayList<>();
        for(int i = 0; i < 24; i ++){
            popupEntries.add(new Entry(i, data[0][i]));
            lookupEntries.add(new Entry(i, data[1][i]));
            cardaddEntries.add(new Entry(i, data[2][i]));
        }
        float lineWidth = 2;
        LineDataSet lineDataSet1 = new LineDataSet(popupEntries, "Popups");
        lineDataSet1.setColor(Color.BLUE);
        lineDataSet1.setLineWidth(lineWidth);
        lineDataSet1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet1.setDrawCircles(false);
        lineDataSet1.setDrawValues(false);
        LineDataSet lineDataSet2 = new LineDataSet(lookupEntries, "Lookups");
        lineDataSet2.setColor(Color.GREEN);
        lineDataSet2.setLineWidth(lineWidth);
        lineDataSet2.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet2.setDrawCircles(false);
        lineDataSet2.setDrawValues(false);
        LineDataSet lineDataSet3 = new LineDataSet(cardaddEntries, "Cards");
        lineDataSet3.setColor(Color.RED);
        lineDataSet3.setLineWidth(lineWidth);
        lineDataSet3.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        lineDataSet3.setDrawCircles(false);
        lineDataSet3.setDrawValues(false);
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet2);
        dataSets.add(lineDataSet3);
        mHourChart.setData(new LineData(dataSets));
        mHourChart.getDescription().setText("");
        //mHourChart.getDescription().setTextAlign();
        mHourChart.getXAxis().setDrawGridLines(false);
        mHourChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mHourChart.getAxisRight().setEnabled(false);
        //mHourChart.getAxisLeft().setDrawGridLines(false);
        mHourChart.getXAxis().setValueFormatter(
                new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        return ((int) value) + ":00";
                    }
                }
        );
        mHourChart.invalidate();
    }
}
