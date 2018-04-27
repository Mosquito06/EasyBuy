package kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import kr.or.dgit.bigdata.easybuy.Main_Activity;
import kr.or.dgit.bigdata.easybuy.R;
import kr.or.dgit.bigdata.easybuy.StartActivity;

/**
 * Created by KDH on 2018-04-26.
 */

public class BarFragment extends ChartFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barchart_layout, container, false);

        return view;
    }

    @Override
    protected void setUpChart(ArrayList<staticData> datas) {
        List<BarEntry> barEntries = new ArrayList<>();

        for(int i = 0; i < datas.size(); i++){
            barEntries.add(new BarEntry(i, datas.get(i).orderPrice));
        }

        List<String> barLegendSet = new ArrayList<>();
        for(int i = 0; i < datas.size(); i++){
            barLegendSet.add(datas.get(i).boardTitle);
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "매출현황");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setDrawValues(true); // barchart 상단에 값 표시 옵션

        BarData data = new BarData(dataSet);
        data.setValueTextSize(getResources().getDimension(R.dimen.pieTextSize));
        data.setValueFormatter(new LargeValueFormatter()); // data 값 표시 형식 지정

        BarChart chart = (BarChart) getActivity().findViewById(R.id.static_barchart);
        chart.setData(data);


        // 애니메이션
        chart.animateY(1500);

        // 출력 항목 표시 세팅
        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.ABOVE_CHART_LEFT);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);

        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barLegendSet));

        xAxis.setGranularity(01);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.invalidate();

    }
}
