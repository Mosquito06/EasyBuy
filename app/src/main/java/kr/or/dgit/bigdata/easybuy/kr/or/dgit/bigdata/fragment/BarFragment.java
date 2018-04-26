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

public class BarFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.barchart_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences loginPref = getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        int userId = loginPref.getInt("userNum", -1);
        Log.d("userId", userId + "");

        new StaticRequestTask().execute(userId);

        setupBarChart();
    }

    private void setupBarChart() {
        List<BarEntry> barEntries = new ArrayList<>();
        for(int i = 1; i <= 4; i++){
            barEntries.add(new BarEntry(i, i*10));
        }

        int totalGroup = 5; // 총 데이터 수 설정

        List<String> barLegendSet = new ArrayList<>();
        for(int i = 1; i <= 4; i++){
            barLegendSet.add("테스트" + i);
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "바 차트 테스트중입니다.");
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
        xAxis.setGranularity(1.1f);
        xAxis.setGranularityEnabled(true);
        xAxis.setCenterAxisLabels(true);
        xAxis.setDrawGridLines(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(barLegendSet));


        chart.invalidate();

    }


    private class StaticRequestTask extends AsyncTask<Integer, Void, String> {

        @Override
        protected String doInBackground(Integer... Integer) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            HttpURLConnection connection = null;
            String line = null;
            try {
                URL url = new URL(StartActivity.CONTEXTPATH + "/android/static/" + Integer[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                if (connection != null) {
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    }
                }

                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    connection.disconnect();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray orders = obj.getJSONArray("orders");

                if(orders.length() == 0){
                    // 결과가 없을 경우 처리
                }else{

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private class staticData{
        int boardNum;
        String boardTitle;
        int orderPrice;
    }


}
