package kr.or.dgit.bigdata.easybuy;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

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
        @Override
        protected String doInBackground(String... strings) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            HttpURLConnection connection = null;
            String line = null;
            try{
                URL url = new URL("http://192.168.0.49:8080/saproject/android/login");
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
        protected void onPostExecute(String s) {
            TextView result = (TextView)getView().findViewById(R.id.resultText);
            result.setText(s);
        }
    }

}
