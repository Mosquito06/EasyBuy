package kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

import kr.or.dgit.bigdata.easybuy.R;

/**
 * Created by KDH on 2018-04-26.
 */

public class PieFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.piechart_layout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setupPieChart();
    }

    private void setupPieChart() {
        List<PieEntry> pieEntries = new ArrayList<>();
        for(int i = 1; i <= 5; i++){
            pieEntries.add(new PieEntry(i, "테스트" + i));
        }

        // 추가한 데이터의 출력 파이 색상 및 파이 간 간격 설정
        PieDataSet dataSet = new PieDataSet(pieEntries, "테스트 중입니다.");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        dataSet.setSliceSpace(3);

        // 출력되는 데이터의 글자 색상 및 크기 설정
        PieData data = new PieData(dataSet);
        data.setValueTextSize(getResources().getDimension(R.dimen.pieTextSize));
        data.setValueFormatter(new PercentFormatter()); // 퍼센트 기호 출력


        PieChart chart = (PieChart) getActivity().findViewById(R.id.static_piechart);
        chart.setData(data);

        // 퍼센트 값 호출여부
        chart.setUsePercentValues(true);

        // 파이차트 중앙 크기
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(10);

        // 애니메이션
        chart.animateY(1500);

        // 출력 항목 표시 세팅
        Legend legend = chart.getLegend();
        legend.setPosition(Legend.LegendPosition.LEFT_OF_CHART);
        legend.setXEntrySpace(7);
        legend.setYEntrySpace(5);

        chart.invalidate();
    }
}
