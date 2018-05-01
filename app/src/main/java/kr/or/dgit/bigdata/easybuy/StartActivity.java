package kr.or.dgit.bigdata.easybuy;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.messaging.FirebaseMessaging;

import kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment.LogoFragment;

public class StartActivity extends AppCompatActivity{
    public static final String CONTEXTPATH = "http://skykim10908.cafe24.com/SA_Project";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().subscribeToTopic("EasyBuy");

        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().add(R.id.mainLayoutFrame, new LogoFragment()).commit();


    }
}
