package kr.or.dgit.bigdata.easybuy;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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

        toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        toolbar.setTitle("");

        toolbarText = (TextView) findViewById(R.id.toolbar_text);
        toolbarText.setText(name + "님");

        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.main_drawer);
        toggle = new ActionBarDrawerToggle(this, drawer, R.string.drawer_open, R.string.drawer_close);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();

        /*nav = (NavigationView) findViewById(R.id.main_nav);

        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_header, nav);

        navName = (TextView) view.findViewById(R.id.nav_name);
        navId = (TextView) view.findViewById(R.id.nav_id);
        navName.setText(name +"님");
        navId.setText(id);*/

        navName = (TextView) findViewById(R.id.nav_name);
        navId = (TextView) findViewById(R.id.nav_id);
        navName.setText(name +"님");
        navId.setText(id);

        ArrayList<ItemVO> menuList = new ArrayList<>();
        menuList.add(new division("과실류"));
        menuList.add(new section("사과"));
        menuList.add(new section("사과"));
        menuList.add(new section("사과"));
        menuList.add(new section("사과"));
        menuList.add(new division("과실류"));
        menuList.add(new section("사과"));
        menuList.add(new section("사과"));
        menuList.add(new section("사과"));
        menuList.add(new section("사과"));

        navRecyclerView = (RecyclerView) findViewById(R.id.main_nav_recyclerView);
        MenuAdapter menuAdapter = new MenuAdapter(menuList);

        navRecyclerView.setAdapter(menuAdapter);
        navRecyclerView.setLayoutManager(new LinearLayoutManager(this));


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
        public void onBindViewHolder(RecyclerView.ViewHolder viewholder, int position) {
            ItemVO item = menu.get(position);
            if(item.getType() == ItemVO.TYPE_DIVISION){
                divisionViewHolder holder = (divisionViewHolder) viewholder;
                division division = (division) item;
                holder.divisionText.setText(division.divisionName);

            }else{
                sectionViewHolder holder = (sectionViewHolder) viewholder;
                section section = (section) item;
                holder.sectionText.setText(section.sectionName);
            }
        }

        @Override
        public int getItemCount() {
            return menu.size();
        }
    }

}
