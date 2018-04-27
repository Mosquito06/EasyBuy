package kr.or.dgit.bigdata.easybuy;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment.BarFragment;
import kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment.ChartFragment;
import kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment.LogoFragment;
import kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment.PieFragment;

public class Static_Activity extends AppCompatActivity {
    FragmentManager fm;
    ChartFragment chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_static_);

        chart = new PieFragment();

        fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.static_frame_layout, chart).commit();
    }

    public void setStaticBtn(View view) {
        switch(view.getId()){
            case R.id.static_floating_pie_btn:
                chart = new PieFragment();
                fm.beginTransaction().replace(R.id.static_frame_layout, chart).commit();
                break;
            case R.id.static_floating_bar_btn:
                chart = new BarFragment();
                fm.beginTransaction().replace(R.id.static_frame_layout, chart).commit();
                break;
        }

    }
}
