package kr.or.dgit.bigdata.easybuy.kr.or.dgit.bigdata.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import kr.or.dgit.bigdata.easybuy.R;
import kr.or.dgit.bigdata.easybuy.StartActivity;

/**
 * Created by KDH on 2018-04-27.
 */

public abstract class ChartFragment extends Fragment {

    protected abstract void setUpChart(ArrayList<staticData> datas);


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        SharedPreferences loginPref = getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
        int userId = loginPref.getInt("userNum", -1);
        Log.d("userId", userId + "");

        new StaticRequestTask().execute(userId);
    }

    public class staticData{
        int boardNum;
        String boardTitle;
        int orderPrice;

        @Override
        public String toString() {
            return "staticData{" +
                    "boardNum=" + boardNum +
                    ", boardTitle='" + boardTitle + '\'' +
                    ", orderPrice=" + orderPrice +
                    '}';
        }
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
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.static_frame_layout, new EmptyFragment()).commit();
                }else{
                    ArrayList<staticData> datas = new ArrayList<>();
                    for(int i = 0; i < orders.length(); i++){
                        JSONObject dataOrder = orders.getJSONObject(i);

                        staticData staticData = new staticData();
                        staticData.orderPrice = dataOrder.getInt("orderPrice");

                        JSONObject dataBoard = dataOrder.getJSONObject("boardNum");

                        staticData.boardNum = dataBoard.getInt("boardNum");
                        staticData.boardTitle = dataBoard.getString("boardTitle");

                        datas.add(staticData);
                    }

                    setUpChart(datas);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
