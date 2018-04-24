package kr.or.dgit.bigdata.easybuy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Order_Activity extends AppCompatActivity {
    public static final int ORDER_ACTIVITY = 0;
    public static final int ALL_ORDER_ACTIVITY = 1;

    Button allBtn;
    Button readyBtn;
    Button ingBtn;
    Button completeBtn;
    Button cancelBtn;
    RecyclerView orderRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_);

        allBtn = (Button) findViewById(R.id.order_all_btn);
        readyBtn = (Button) findViewById(R.id.order_ready_btn);
        ingBtn = (Button) findViewById(R.id.order_ing_btn);
        completeBtn = (Button) findViewById(R.id.order_complete_btn);
        cancelBtn = (Button) findViewById(R.id.order_cancel_btn);
        orderRecyclerView = (RecyclerView) findViewById(R.id.order_recyclerView);

        setAsynTask(-1);


    }

    public void setOrderByBtn(View view) {
        switch (view.getId()) {
            case R.id.order_all_btn:
                setAsynTask(0);
                break;
            case R.id.order_ready_btn:
                setAsynTask(1);
                break;
            case R.id.order_ing_btn:
                setAsynTask(2);
                break;
            case R.id.order_complete_btn:
                setAsynTask(3);
                break;
            case R.id.order_cancel_btn:
                setAsynTask(4);
                break;
        }
    }

    private void setAsynTask(int optionValue) {
        Intent intent = getIntent();
        int call_activity = intent.getIntExtra("call", -1);


        if (call_activity != -1 && call_activity == ORDER_ACTIVITY) {
            int boardNum = intent.getIntExtra("boardNum", -1);
            new setOrderAsynTask().execute(call_activity, boardNum, optionValue);

        } else if (call_activity != -1 && call_activity == ALL_ORDER_ACTIVITY) {
            int userNum = intent.getIntExtra("userNum", -1);
            new setOrderAsynTask().execute(call_activity, userNum, optionValue);

        }
    }

    private class setOrderAsynTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            // 첫번째 매개변수(integers[0])는 호출한 activity 판별
            // 두번째 매개변수(integers[1])는 서버에 호출할 int 변수
            // 세번째 매개변수(integers[2])는 각 버튼에 따른 int 변수

            int call_check = integers[0];

            if (call_check == ORDER_ACTIVITY) {
                String MappingPath = StartActivity.CONTEXTPATH + "/android/order/";
                return getResultString(integers, MappingPath);
            } else {
                String MappingPath = StartActivity.CONTEXTPATH + "/android/client/";
                return getResultString(integers, MappingPath);
            }
        }


        @Override
        protected void onPostExecute(String result) {

        }


    }

    private String getResultString(Integer[] integers, String MappingPath) {
        StringBuffer sb;
        int btn_option = integers[2];

        if (btn_option != -1) {
            sb = getStringBuffer(MappingPath, integers[1], integers[2]);
        } else {
            sb = getStringBuffer(MappingPath, integers[1], -1);
        }
        return sb.toString();
    }

    private StringBuffer getStringBuffer(String MappingPath, Integer... value) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        HttpURLConnection connection = null;
        String line = null;
        URL url = null;
        Log.d("value[0]", value[0] + "");
        Log.d("value[1]", value[1] + "");

        try {
            if (value[1] != -1) {
                url = new URL(MappingPath + value[0] + "/" + value[1]);
            } else {
                url = new URL(MappingPath + value[0]);
            }

            Log.d("url", url.toString());

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
        return sb;
    }


}
