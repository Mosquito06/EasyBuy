package kr.or.dgit.bigdata.easybuy;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment.LogoFragment;

public class StartActivity extends AppCompatActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.mainLayoutFrame, new LogoFragment()).commit();


    }
}