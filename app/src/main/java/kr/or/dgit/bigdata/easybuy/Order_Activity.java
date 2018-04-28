package kr.or.dgit.bigdata.easybuy;

import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Order_Activity extends AppCompatActivity {
    public static final int ORDER_ACTIVITY = 0;
    public static final int ALL_ORDER_ACTIVITY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_);
        setAsynTask(-1, false, -1);

    }

    public void setOrderByBtn(View view) {
        switch (view.getId()) {
            case R.id.order_floating_all_btn:
                setAsynTask(0, false, -1);
                break;
            case R.id.order_floating_ready_btn:
                setAsynTask(1, false, -1);
                break;
            case R.id.order_floating_ing_btn:
                setAsynTask(2, false, -1);
                break;
            case R.id.order_floating_complete_btn:
                setAsynTask(3, false, -1);
                break;
            case R.id.order_floating_cancel_btn:
                setAsynTask(4, false, -1);
                break;
        }
    }

    private void setAsynTask(int optionValue, boolean isUpdate, int orderNum) {
        Intent intent = getIntent();
        int call_activity = intent.getIntExtra("call", -1);


        if (isUpdate) {
                int boardNum;
                int userNum;
            if (call_activity != -1 && call_activity == ORDER_ACTIVITY) {
                boardNum = intent.getIntExtra("boardNum", -1);
                new setOrderAsynTask().execute(2, orderNum, optionValue, ORDER_ACTIVITY, boardNum);

            } else if (call_activity != -1 && call_activity == ALL_ORDER_ACTIVITY) {
                userNum = intent.getIntExtra("userNum", -1);
                new setOrderAsynTask().execute(2, orderNum, optionValue, ALL_ORDER_ACTIVITY, userNum);

            }

        } else if (call_activity != -1 && call_activity == ORDER_ACTIVITY) {
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
            } else if (call_check == ALL_ORDER_ACTIVITY) {
                String MappingPath = StartActivity.CONTEXTPATH + "/android/client/";
                return getResultString(integers, MappingPath);
            } else {
                String MappingPath = StartActivity.CONTEXTPATH + "/android/update/";
                return getResultString(integers, MappingPath);
            }
        }


        @Override
        protected void onPostExecute(String result) {
            FrameLayout order_main_layout = (FrameLayout) findViewById(R.id.order_main_frameLayout);
            FrameLayout order_sub_layout = (FrameLayout) findViewById(R.id.order_sub_frameLayout);
            TextView noneOrderTextView = (TextView) findViewById(R.id.noneOrderText);
            RecyclerView order_recyclerView = (RecyclerView) findViewById(R.id.order_recyclerView);

            ArrayList<orderData> data = new ArrayList<>();

            try {
                JSONObject obj = new JSONObject(result);
                JSONArray orders = obj.getJSONArray("orders");

                if (orders.length() == 0) {

                    if (order_recyclerView != null) {
                        order_sub_layout.removeView(order_recyclerView);
                    }

                    if (noneOrderTextView != null) {
                        order_main_layout.removeView(noneOrderTextView);
                    }

                    TextView textView = new TextView(Order_Activity.this);
                    textView.setId(R.id.noneOrderText);
                    textView.setText("접수된 주문이 존재하지 않습니다.");
                    textView.setTextColor(getResources().getColor(R.color.blackColor));

                    // 자식 뷰가 포함될 부모 뷰의 레이아웃의 params을 사용
                    FrameLayout.LayoutParams textViewLayout = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    textViewLayout.gravity = Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL;

                    textView.setLayoutParams(textViewLayout);
                    order_main_layout.addView(textView);


                } else {
                    for (int i = 0; i < orders.length(); i++) {
                        JSONObject orderObj = orders.getJSONObject(i);
                        JSONObject clientObj = orders.getJSONObject(i).getJSONObject("clientNum");
                        JSONObject boardObj = orders.getJSONObject(i).getJSONObject("boardNum");

                        orderData order = new orderData();
                        order.orderNum = orderObj.getInt("orderNum");
                        order.orderTitle = boardObj.getString("boardTitle");
                        order.orderDate = new Date(orderObj.getLong("orderDate"));
                        order.orderAmount = orderObj.getInt("orderAmount");
                        order.price = orderObj.getInt("orderPrice");
                        order.recipient = orderObj.getString("orderRecipient");
                        order.address = orderObj.getString("recipientAddress");
                        order.phone = orderObj.getString("recipientPhone");
                        order.orderStatus = orderObj.getString("orderStatus");

                        String status = order.orderStatus;

                        if (status.equals("READY")) {
                            order.viewType = 2;
                        } else if (status.equals("ING")) {
                            order.viewType = 1;
                        } else {
                            order.viewType = 0;
                        }

                        data.add(order);
                    }

                    if (order_recyclerView == null) {
                        order_main_layout.removeView(noneOrderTextView);
                    }

                    order_sub_layout.removeView(order_recyclerView);

                    order_recyclerView = new RecyclerView(Order_Activity.this);
                    order_recyclerView.setId(R.id.order_recyclerView);

                    RelativeLayout.LayoutParams recyclerViewLayout = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    order_recyclerView.setLayoutParams(recyclerViewLayout);

                    order_sub_layout.addView(order_recyclerView);

                    order_recyclerView = (RecyclerView) findViewById(R.id.order_recyclerView);
                    order_recyclerView.setAdapter(new orderAdapter(data));
                    order_recyclerView.setLayoutManager(new LinearLayoutManager(Order_Activity.this));
                    order_recyclerView.addItemDecoration(new orderDecoration());

                    Main_Activity.runAnimation(order_recyclerView);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private String getResultString(Integer[] integers, String MappingPath) {
        StringBuffer sb;
        int btn_option = integers[2];

        if(integers[0] == 2){
            // 주문번호, 변경옵션, 호출 액티비티 종류, 고객 혹은 게시판 번호
            sb = getStringBuffer(MappingPath, "PUT", integers[1], integers[2], integers[3], integers[4]);
            return sb.toString();
        }

        if (btn_option != -1) {
            sb = getStringBuffer(MappingPath, "GET",integers[1], integers[2], -1);
        } else {
            sb = getStringBuffer(MappingPath, "GET", integers[1], -1, -1);
        }
        return sb.toString();
    }

    private StringBuffer getStringBuffer(String MappingPath, String method, Integer... value) {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        HttpURLConnection connection = null;
        String line = null;
        URL url = null;

        try {

            if(value[2] != -1){
                url = new URL(MappingPath + value[0] + "/" + value[1] + "/" + value[2] + "/" + value[3]);
            }else if (value[1] != -1) {
                url = new URL(MappingPath + value[0] + "/" + value[1]);
            } else {
                url = new URL(MappingPath + value[0]);
            }

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(method);

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


    // order recyclerView 작업
    private class orderData {
        int orderNum;
        String orderTitle;
        Date orderDate;
        int orderAmount;
        int price;
        String recipient;
        String address;
        String phone;
        String orderStatus;
        int viewType;

        @Override
        public String toString() {
            return "orderData{" +
                    "orderNum=" + orderNum +
                    ", orderTitle='" + orderTitle + '\'' +
                    ", orderDate=" + orderDate +
                    ", orderAmount=" + orderAmount +
                    ", price=" + price +
                    ", recipient='" + recipient + '\'' +
                    ", address='" + address + '\'' +
                    ", phone='" + phone + '\'' +
                    ", orderStatus='" + orderStatus + '\'' +
                    '}';
        }
    }

    private class orderHolder extends RecyclerView.ViewHolder {
        TextView numText;
        TextView titleText;
        TextView dateText;
        TextView amountText;
        TextView priceText;
        TextView recipientText;
        TextView addressText;
        TextView phoneText;
        TextView statusText;
        Button ingBtn;
        Button completeBtn;


        public orderHolder(View itemView) {
            super(itemView);

            this.numText = itemView.findViewById(R.id.order_orderNum_text);
            this.titleText = itemView.findViewById(R.id.order_orderTitle_text);
            this.dateText = itemView.findViewById(R.id.order_orderDate_text);
            this.amountText = itemView.findViewById(R.id.order_orderAmount_text);
            this.priceText = itemView.findViewById(R.id.order_orderPrice_text);
            this.recipientText = itemView.findViewById(R.id.order_recipient_text);
            this.addressText = itemView.findViewById(R.id.order_address_text);
            this.phoneText = itemView.findViewById(R.id.order_phone_text);
            this.statusText = itemView.findViewById(R.id.order_status_text);
            this.ingBtn = itemView.findViewById(R.id.order_setIng_btn);
            this.completeBtn = itemView.findViewById(R.id.order_setComplete_btn);
        }
    }

    private class orderAdapter extends RecyclerView.Adapter<orderHolder> {
        ArrayList<orderData> data;

        public orderAdapter(ArrayList<orderData> data) {
            super();
            this.data = data;
        }

        @Override
        public int getItemViewType(int position) {
            return data.get(position).viewType;
        }

        @Override
        public orderHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = null;

            if (viewType == 2) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_layout_before, parent, false);
            } else if (viewType == 1) {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_layout_ing, parent, false);
            } else {
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_layout_after, parent, false);
            }
            return new orderHolder(view);
        }

        @Override
        public void onBindViewHolder(orderHolder holder, final int position) {
            orderData order = data.get(position);
            final int orderNum = order.orderNum;

            if (order.viewType == 2) {
                setCommonHolder(holder, order);

                holder.ingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAsynTask(1, true, orderNum);
                    }
                });

                holder.completeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAsynTask(2, true, orderNum);
                    }
                });

            } else if (order.viewType == 1) {
                setCommonHolder(holder, order);

                holder.completeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setAsynTask(2, true, orderNum);
                    }
                });

            } else {
                setCommonHolder(holder, order);
            }


        }

        private void setCommonHolder(orderHolder holder, orderData order) {
            holder.numText.setText(String.valueOf(order.orderNum));
            holder.titleText.setText(order.orderTitle);

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String turnForm = dateFormat.format(order.orderDate);
            holder.dateText.setText(turnForm);

            holder.amountText.setText(String.valueOf(order.orderAmount));

            DecimalFormat priceFormat = new DecimalFormat("#,##0");
            String turnPrice = priceFormat.format(order.price);
            holder.priceText.setText(turnPrice);

            holder.recipientText.setText(order.recipient);
            holder.addressText.setText(order.address);
            holder.phoneText.setText(order.phone);

            String turnStatus = order.orderStatus.equals("READY") ? "배송전" : order.orderStatus.equals("ING") ? "배송준비" :
                    order.orderStatus.equals("COMPLETE") ? "배송완료" : "취소주문";
            holder.statusText.setText(turnStatus);
        }

        @Override
        public int getItemCount() {
            return data.size();
        }
    }

    private class orderDecoration extends RecyclerView.ItemDecoration {
        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);

            outRect.set(10, 20, 10, 20);
            view.setBackgroundColor(0xffece9e9);
            ViewCompat.setElevation(view, 20.0f);

        }
    }
}
