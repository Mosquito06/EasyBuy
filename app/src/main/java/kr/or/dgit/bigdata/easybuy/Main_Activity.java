package kr.or.dgit.bigdata.easybuy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
    int userNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_);

        Intent intent = getIntent();

        SharedPreferences loginPref = getSharedPreferences("login_info", Context.MODE_PRIVATE);
        String name = loginPref.getString("userName", "none");
        String id = loginPref.getString("userId", "none");
        userNum = loginPref.getInt("userNum", 0);

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");

        /*toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText(name + "님");*/

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        navName = (TextView) findViewById(R.id.nav_name);
        navId = (TextView) findViewById(R.id.nav_id);
        navName.setText(name + "님");
        navId.setText(id);

        new menuAsycTask().execute(userNum);

    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);

        }else{
            AlertDialog.Builder builder = new AlertDialog.Builder(Main_Activity.this);
            builder.setIcon(R.drawable.alert);
            builder.setTitle("Alert");
            builder.setMessage("정말로 종료하시겠습니까?");
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Main_Activity.this.finishAffinity();
                }
            });
            builder.setNegativeButton("NO", null);
            builder.setCancelable(false);
            builder.create().show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.action_button, menu);

        return true;
    }

    // nav 버튼 활성화
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.actionBtn_board) {
            new menuAsycTask().execute(userNum);

        } else if (item.getItemId() == R.id.actionBtn_order) {
            Intent intent = new Intent();
            intent.setClass(Main_Activity.this, Order_Activity.class);
            intent.putExtra("userNum", userNum);
            intent.putExtra("call", Order_Activity.ALL_ORDER_ACTIVITY);

            startActivity(intent);

        } else if (item.getItemId() == R.id.actionBtn_static) {


        }

        return super.onOptionsItemSelected(item);
    }

    // memu의 데이터를 담을 클래스 생성
    private abstract class ItemVO {
        public static final int TYPE_DIVISION = 0;
        public static final int TYPE_SECTION = 1;

        abstract int getType();
    }

    private class division extends ItemVO {
        String divisionName;

        public division(String divisionName) {
            this.divisionName = divisionName;
        }

        @Override
        int getType() {
            return ItemVO.TYPE_DIVISION;
        }
    }

    private class section extends ItemVO {
        String sectionName;
        int sectionNum;

        public section(String sectionName, int sectionNum) {
            this.sectionName = sectionName;
            this.sectionNum = sectionNum;
        }

        @Override
        int getType() {
            return ItemVO.TYPE_SECTION;
        }
    }


    // recycleview viewholder 작성
    private class divisionViewHolder extends RecyclerView.ViewHolder {
        TextView divisionText;


        public divisionViewHolder(View itemView) {
            super(itemView);
            divisionText = (TextView) itemView.findViewById(R.id.division_name);
        }
    }

    private class sectionViewHolder extends RecyclerView.ViewHolder {
        TextView sectionText;

        public sectionViewHolder(View itemView) {
            super(itemView);
            sectionText = (TextView) itemView.findViewById(R.id.section_name);
        }
    }

    // menu recycleview adapter 작성
    private class MenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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
            if (viewType == ItemVO.TYPE_DIVISION) {
                View view = LayoutInflater.from(Main_Activity.this).inflate(R.layout.division_layout, parent, false);
                return new divisionViewHolder(view);
            } else {
                View view = LayoutInflater.from(Main_Activity.this).inflate(R.layout.section_layout, parent, false);
                return new sectionViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder viewholder, final int position) {
            ItemVO item = menu.get(position);
            if (item.getType() == ItemVO.TYPE_DIVISION) {
                divisionViewHolder holder = (divisionViewHolder) viewholder;
                division division = (division) item;
                holder.divisionText.setText(division.divisionName);

            } else {
                sectionViewHolder holder = (sectionViewHolder) viewholder;
                section section = (section) item;
                holder.sectionText.setText(section.sectionName);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 메뉴 이벤트 설정
                        ItemVO item = menu.get(position);
                        if (item.getType() == ItemVO.TYPE_SECTION) {
                            section section = (section) item;
                            new setBoardAsynTask().execute(section.sectionNum);
                            if (drawer.isDrawerOpen(GravityCompat.START)) {
                                drawer.closeDrawer(GravityCompat.START);
                            }
                        }

                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return menu.size();
        }
    }

    // 로그인 시 데이터를 가져오기 위한 asyctask 작성
    private class menuAsycTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            String MappginPath = StartActivity.CONTEXTPATH + "/android/menu/";
            StringBuffer sb = getStringBuffer(MappginPath, integers[0]);

            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {

            ArrayList<ItemVO> menuList = new ArrayList<>();

            try {
                JSONObject obj = new JSONObject(result);
                JSONArray division = obj.getJSONArray("division");
                JSONArray section = obj.getJSONArray("section");
                JSONArray board = obj.getJSONArray("board");
                if (division != null) {
                    for (int i = 0; i < division.length(); i++) {
                        JSONObject getDivision = division.getJSONObject(i);
                        int divisionNum = getDivision.getInt("divisionNum");
                        menuList.add(new division(getDivision.getString("divisionName")));

                        if (section != null) {
                            for (int ii = 0; ii < section.length(); ii++) {
                                JSONObject getSection = section.getJSONObject(ii);
                                JSONObject section_division = getSection.getJSONObject("division");
                                int section_division_num = section_division.getInt("divisionNum");
                                if (divisionNum == section_division_num) {
                                    menuList.add(new section(getSection.getString("sectionName"), getSection.getInt("sectionNum")));
                                }
                            }
                        }
                    }
                }

                setBoardAdapter(board);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            navRecyclerView = (RecyclerView) findViewById(R.id.main_nav_recyclerView);
            MenuAdapter menuAdapter = new MenuAdapter(menuList);

            navRecyclerView.setAdapter(menuAdapter);
            navRecyclerView.setLayoutManager(new LinearLayoutManager(Main_Activity.this));
        }
    }

    // 메인 recyclerView 작업
    private class board {
        int boardNum;
        String imgPath;
        int boardTotal;
        int boardView;
        String boardTitle;
        Date boardRegdate;
        int boardPrice;
        String boardWriter;

        @Override
        public String toString() {
            return boardNum + " : " + imgPath + " : " + boardTotal + " : " + boardView + " : " + boardTitle + " : "
                    + boardRegdate + " : " + boardPrice + boardWriter;
        }
    }

    private class boardHolder extends RecyclerView.ViewHolder {
        ImageView boardImg;
        TextView boardTotal;
        TextView boardView;
        TextView boardTitle;
        TextView boardRegdate;
        TextView boardPrice;
        TextView boardWriter;
        TextView showOrder;

        public boardHolder(View itemView) {
            super(itemView);
            this.boardImg = itemView.findViewById(R.id.board_img);
            this.boardTotal = itemView.findViewById(R.id.board_total);
            this.boardView = itemView.findViewById(R.id.board_view);
            this.boardTitle = itemView.findViewById(R.id.board_title);
            this.boardRegdate = itemView.findViewById(R.id.board_date);
            this.boardPrice = itemView.findViewById(R.id.board_price);
            this.boardWriter = itemView.findViewById(R.id.board_writer);
            this.showOrder = itemView.findViewById(R.id.board_show_order);

        }
    }

    private class boardAdapter extends RecyclerView.Adapter<boardHolder> {
        ArrayList<board> data;

        public boardAdapter(ArrayList<board> data) {
            this.data = data;
        }

        @Override
        public boardHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(Main_Activity.this).inflate(R.layout.main_board_layout, parent, false);
            return new boardHolder(view);
        }

        @Override
        public void onBindViewHolder(boardHolder holder, final int position) {
            board board = data.get(position);
            Log.d("path : ", StartActivity.CONTEXTPATH + "/resources/upload" + board.imgPath);

            Picasso.with(Main_Activity.this).load(StartActivity.CONTEXTPATH + "/resources/upload" + board.imgPath)
                    .placeholder(R.drawable.basicimg).into(holder.boardImg);

            holder.boardTotal.setText("총 주문수량: " + board.boardTotal);
            holder.boardView.setText("조회수: " + board.boardView);
            holder.boardTitle.setText("제목: " + board.boardTitle);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String turnForm = dateFormat.format(board.boardRegdate);

            holder.boardRegdate.setText("등록날짜: " + turnForm);

            DecimalFormat priceFormat = new DecimalFormat("#,##0");
            String turnPrice = priceFormat.format(board.boardPrice);

            holder.boardPrice.setText("가격: " + turnPrice + " 원");
            holder.boardWriter.setText("등록: " + board.boardWriter);

            holder.showOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setClass(Main_Activity.this, Order_Activity.class);
                    intent.putExtra("boardNum", data.get(position).boardNum);
                    intent.putExtra("call", Order_Activity.ORDER_ACTIVITY);

                    startActivity(intent);

                }
            });
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }


    // 버튼 이벤트에 따른 데이터를 가져오는 AsynTask
    private class setBoardAsynTask extends AsyncTask<Integer, Void, String> {
        @Override
        protected String doInBackground(Integer... integers) {
            String MappginPath = StartActivity.CONTEXTPATH + "/android/board/";
            StringBuffer sb = getStringBuffer(MappginPath, integers[0]);

            return sb.toString();
        }


        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject obj = new JSONObject(result);
                JSONArray board = obj.getJSONArray("board");
                setBoardAdapter(board);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private StringBuffer getStringBuffer(String MappginPath, Integer value) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        HttpURLConnection connection = null;
        String line = null;
        try {
            URL url = new URL(MappginPath + value);
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

    private void setBoardAdapter(JSONArray board) throws JSONException {
        RecyclerView mainRecyclerView = (RecyclerView) findViewById(R.id.main_recyclerView);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.main_relativeLayout);
        TextView noneBoardTextView = (TextView) findViewById(R.id.noneBoardText);


        if (board.length() > 0) {
            if (mainRecyclerView == null) {
                relativeLayout.removeView(noneBoardTextView);

                mainRecyclerView = new RecyclerView(Main_Activity.this);
                mainRecyclerView.setId(R.id.main_recyclerView);

                RelativeLayout.LayoutParams recyclerViewLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                recyclerViewLayout.addRule(RelativeLayout.BELOW, R.id.main_toolbar);
                mainRecyclerView.setLayoutParams(recyclerViewLayout);

                relativeLayout.addView(mainRecyclerView);
            }

            ArrayList<Main_Activity.board> data = new ArrayList<>();

            for (int i = 0; i < board.length(); i++) {
                JSONObject boardObj = board.getJSONObject(i);


                Main_Activity.board boardData = new board();
                boardData.boardNum = boardObj.getInt("boardNum");
                boardData.boardTotal = boardObj.getInt("boardTotalCount");
                boardData.boardView = boardObj.getInt("boardCount");
                boardData.boardTitle = boardObj.getString("boardTitle");
                boardData.boardRegdate = new Date(boardObj.getLong("boardDate"));
                boardData.boardPrice = boardObj.getInt("boardPrice");

                JSONObject user = boardObj.getJSONObject("clientNum");
                boardData.boardWriter = user.getString("name");


                try {
                    JSONArray filesArray = boardObj.getJSONArray("files");

                    if (filesArray.length() > 0) {
                        JSONObject file = filesArray.getJSONObject(0);
                        boardData.imgPath = file.getString("filePath");

                    }
                } catch (JSONException e) {
                    Log.d("타입에러", e.getMessage());
                }

                data.add(boardData);
            }

            boardAdapter board_adapter = new boardAdapter(data);
            mainRecyclerView.setAdapter(board_adapter);

            GridLayoutManager boardManager = new GridLayoutManager(Main_Activity.this, 2);
            mainRecyclerView.setLayoutManager(boardManager);

            runAnimation(mainRecyclerView);

        } else {
            relativeLayout.removeView(mainRecyclerView);

            if (noneBoardTextView != null) {
                relativeLayout.removeView(noneBoardTextView);
            }

            TextView textView = new TextView(Main_Activity.this);
            textView.setId(R.id.noneBoardText);
            textView.setText("등록된 게시물이 존재하지 않습니다.");
            textView.setTextColor(getResources().getColor(R.color.blackColor));

            RelativeLayout.LayoutParams textViewLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            textViewLayout.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
            textView.setLayoutParams(textViewLayout);

            relativeLayout.addView(textView);

        }
    }

    public static void runAnimation(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(context, R.anim.layout_full_down);

        recyclerView.setLayoutAnimation(controller);
        recyclerView.getAdapter().notifyDataSetChanged();
        recyclerView.scheduleLayoutAnimation();

    }


}
