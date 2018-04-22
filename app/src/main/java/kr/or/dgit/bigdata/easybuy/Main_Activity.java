package kr.or.dgit.bigdata.easybuy;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.zip.Inflater;

import kr.or.dgit.bigdata.easybuy.R;

public class Main_Activity extends AppCompatActivity {
    Toolbar toolbar;
    TextView toolbarText;
    TextView navName;
    TextView navId;
    DrawerLayout drawer;
    NavigationView nav;
    ActionBarDrawerToggle toggle;
    RecyclerView navRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);

        Intent intent = getIntent();
        String name = intent.getStringExtra("userName");
        String id = intent.getStringExtra("userId");
        int userNum = intent.getIntExtra("userNum", 0);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");

        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText(name + "님");

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        navName = (TextView) findViewById(R.id.nav_name);
        navId = (TextView) findViewById(R.id.nav_id);
        navName.setText(name +"님");
        navId.setText(id);

        new menuAsycTask().execute(userNum);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private abstract class ItemVO{
        public static final int TYPE_DIVISION = 0;
        public static final int TYPE_SECTION = 1;
        abstract int getType();
    }

    private class division extends ItemVO{
        String divisionName;

        public division(String divisionName) {
            this.divisionName = divisionName;
        }

        @Override
        int getType() {
            return ItemVO.TYPE_DIVISION;
        }
    }

    private class section extends ItemVO{
        String sectionName;

        public section(String sectionName) {
            this.sectionName = sectionName;
        }

        @Override
        int getType() {
            return ItemVO.TYPE_SECTION;
        }
    }

    private class divisionViewHolder extends RecyclerView.ViewHolder{
        TextView divisionText;


        public divisionViewHolder(View itemView) {
            super(itemView);
            divisionText = (TextView) itemView.findViewById(R.id.division_name);
        }
    }

    private class sectionViewHolder extends RecyclerView.ViewHolder{
        TextView sectionText;


        public sectionViewHolder(View itemView) {
            super(itemView);
            sectionText = (TextView) itemView.findViewById(R.id.section_name);
        }
    }

    private class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        ArrayList<ItemVO> menu;


        public MenuAdapter(ArrayList<ItemVO> menuData) {
            super();
            this.menu = menuData;
        }

        @Override
        public int getItemViewType(int position) {
            return menu.get(position).getType();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType == ItemVO.TYPE_DIVISION){
                View view = LayoutInflater.from(Main_Activity.this).inflate(R.layout.division_layout, parent, false);
                return new divisionViewHolder(view);
            }else{
                View view = LayoutInflater.from(Main_Activity.this).inflate(R.layout.section_layout, parent, false);
                return new sectionViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewholder, final int position) {
            ItemVO item = menu.get(position);
            if(item.getType() == ItemVO.TYPE_DIVISION){
                divisionViewHolder holder = (divisionViewHolder) viewholder;
                division division = (division) item;
                holder.divisionText.setText(division.divisionName);

            }else{
                sectionViewHolder holder = (sectionViewHolder) viewholder;
                section section = (section) item;
                holder.sectionText.setText(section.sectionName);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 메뉴 이벤트 설정

                        Toast.makeText(Main_Activity.this, "position : " + position , Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return menu.size();
        }
    }

    private class menuAsycTask extends AsyncTask<Integer, Void, String>{
        @Override
        protected String doInBackground(Integer... strings) {
            StringBuffer sb = new StringBuffer();
            BufferedReader br = null;
            HttpURLConnection connection = null;
            String line = null;
            try{
                URL url = new URL(StartActivity.CONTEXTPATH + "/android/menu?clientNum=" + strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

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
            TextView resultText = (TextView) findViewById(R.id.test_text);
            ArrayList<ItemVO> menuList = new ArrayList<>();

            try {
                JSONObject obj = new JSONObject(result);
                JSONArray division = obj.getJSONArray("division");
                JSONArray section = obj.getJSONArray("section");
                JSONArray board = obj.getJSONArray("board");
                if(division != null){
                    for(int i = 0; i < division.length(); i++){
                        JSONObject getDivision = division.getJSONObject(i);
                        int divisionNum = getDivision.getInt("divisionNum");
                        menuList.add(new division(getDivision.getString("divisionName")));

                        if(section != null){
                            for(int ii = 0; ii < section.length(); ii++){
                                JSONObject getSection = section.getJSONObject(ii);
                                JSONObject section_division = getSection.getJSONObject("division");
                                int section_division_num = section_division.getInt("divisionNum");
                                if(divisionNum == section_division_num){
                                    menuList.add(new section(getSection.getString("sectionName")));
                                }
                            }
                        }
                    }
                }

                if(board != null){
                    JSONObject boardObj = board.getJSONObject(0);
                    JSONArray filesArray = boardObj.getJSONArray("files");
                    JSONObject file = filesArray.getJSONObject(0);
                    file.getString("filePath");

                    // resultText.setText(boardObj.toString());
                    Log.d("imgPath", StartActivity.CONTEXTPATH + "/resources/upload" + file.getString("filePath"));

                    ImageView img = (ImageView) findViewById(R.id.test_img);
                    Picasso.with(Main_Activity.this).load(StartActivity.CONTEXTPATH + "/resources/upload" + file.getString("filePath"))
                            .placeholder(R.drawable.alert).resize(300, 300).into(img);

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            navRecyclerView = (RecyclerView) findViewById(R.id.main_nav_recyclerView);
            MenuAdapter menuAdapter = new MenuAdapter(menuList);

            navRecyclerView.setAdapter(menuAdapter);
            navRecyclerView.setLayoutManager(new LinearLayoutManager(Main_Activity.this));
        }
    }

}
