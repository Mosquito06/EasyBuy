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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import kr.or.dgit.bigdata.easybuy.Main_Activity;
import kr.or.dgit.bigdata.easybuy.R;
import kr.or.dgit.bigdata.easybuy.StartActivity;

/**
 * Created by DGIT3-12 on 2018-04-18.
 */

public class LoginFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.login_loyout, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button loginBtn = (Button) getView().findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(this);

    }


    @Override
    public void onClick(View view) {
        EditText id = (EditText) getView().findViewById(R.id.loginInputId);
        EditText pw = (EditText) getView().findViewById(R.id.loginInpugPw);

        String idText = id.getText().toString();
        String pwText = pw.getText().toString();

        if(idText.equals("") || idText == null){
            Toast.makeText(getActivity().getApplicationContext(), "아이디를 입력하세요.", Toast.LENGTH_SHORT).show();
            id.requestFocus();
            return;
        }

        if(pwText.equals("") || pwText == null){
            Toast.makeText(getActivity().getApplicationContext(), "패스워드를 입력하세요.", Toast.LENGTH_SHORT).show();
            pw.requestFocus();
            return;
        }

        new LoginRequestTask().execute(idText, pwText);

    }

    private class LoginRequestTask extends AsyncTask<String, Void, String>{
        User loginUser = null;

        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            HttpURLConnection connection = null;
            String line = null;
            try{
                URL url = new URL(StartActivity.CONTEXTPATH + "/android/login");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                connection.setDoInput(true);

                Uri.Builder builder = new Uri.Builder();
                builder.appendQueryParameter("id", strings[0]);
                builder.appendQueryParameter("pw", strings[1]);
                String query = builder.build().getEncodedQuery();

                OutputStream os = connection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(query);
                writer.flush();
                writer.close();
                os.close();

                if(connection != null){
                    if(connection.getResponseCode() == HttpURLConnection.HTTP_OK){
                        br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    }
                }

                while((line = br.readLine()) != null){
                    sb.append(line);
                }

            }catch(Exception e){
                e.printStackTrace();
            }finally {
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
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            try {
                JSONObject object = new JSONObject(result);
                if(object.getString("result").equals("not exist")){
                    setAlert(builder, "존재하지 않는 아이디입니다.");
                }else if(object.getString("result").equals("pw error")){
                    setAlert(builder, "잘못된 비밀번호 입니다.");
                }else if(object.getString("result").equals("error")){
                    setAlert(builder, "잠시 후 다시 시도해주세요.");
                }else{
                    JSONObject user = new JSONObject(object.getString("loginUser"));
                    loginUser = new User();
                    loginUser.clientNum = user.getInt("clientNum");
                    loginUser.id = user.getString("id");
                    loginUser.name = user.getString("name");
                    loginUser.email = user.getString("email");
                    loginUser.phone = user.getString("phone");
                    loginUser.homeTel = user.getString("homeTel");
                    loginUser.address = user.getString("address");

                    AlertDialog.Builder success = setAlert(builder, loginUser.name + "님 환영합니다.");
                    success.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(loginUser != null){
                                SharedPreferences loginPref = getActivity().getSharedPreferences("login_info", Context.MODE_PRIVATE);
                                SharedPreferences.Editor userPref =  loginPref.edit();

                                userPref.putInt("userNum", loginUser.clientNum);
                                userPref.putString("userId", loginUser.id);
                                userPref.putString("userName", loginUser.name);
                                userPref.putString("userPhone", loginUser.phone);
                                userPref.putString("userAddress", loginUser.address);
                                userPref.commit();


                                Intent intent = new Intent();
                                intent.setClass(getActivity(), Main_Activity.class);
                                startActivity(intent);
                            }
                        }
                    });
                }

                builder.create().show();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        private AlertDialog.Builder setAlert(AlertDialog.Builder builder, String message){
            builder.setIcon(R.drawable.alert);
            builder.setTitle("Alert");
            builder.setMessage(message);
            builder.setPositiveButton("OK", null);
            builder.setCancelable(false);

            return builder;
        }

    }

    private class User {
        public int clientNum;
        public String id;
        public String name;
        public String email;
        public String phone;
        public String homeTel;
        public String address;

        @Override
        public String toString() {
            return "User{" +
                    "clientNum=" + clientNum +
                    ", id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", email='" + email + '\'' +
                    ", phone='" + phone + '\'' +
                    ", homeTel='" + homeTel + '\'' +
                    ", address='" + address + '\'' +
                    '}';
        }
    }

}
