package com.example.doancuoiky.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doancuoiky.GlobalVariable;
import com.example.doancuoiky.R;
import com.example.doancuoiky.adapter.OrderDetailAdapter;
import com.example.doancuoiky.modal.OrderDetail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OrderDetailActivity extends AppCompatActivity {

    ImageView goBack;
    ListView lvOderDetail;
    ArrayList<OrderDetail> orderDetailsList;
    TextView tvTotal, tvOrderStatus;
    Button btnDeleteOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        anhXa();
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        btnDeleteOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(OrderDetailActivity.this, "delete", Toast.LENGTH_SHORT).show();
            }
        });

        Intent intent = getIntent();
        int pos = intent.getIntExtra("gotoOrderDetail", 0);
        int status = intent.getIntExtra("orderStatus", 0);
        int total = intent.getIntExtra("orderTotal", 0);

        getListOrderDetail(pos);
        DecimalFormat decimalFormat = new DecimalFormat("###,###,###");
        String _total = decimalFormat.format(total) + " đ";
        tvTotal.setText(_total);

        switch (status) {
            case 0:
                tvOrderStatus.setText(getResources().getString(R.string.text_status_0));
                btnDeleteOrder.setVisibility(View.VISIBLE);
                break;
            case 1:
                tvOrderStatus.setText(getResources().getString(R.string.text_status_1));
                btnDeleteOrder.setVisibility(View.GONE);
                break;
            case 2:
                tvOrderStatus.setText(getResources().getString(R.string.text_status_2));
                btnDeleteOrder.setVisibility(View.GONE);
                break;
        }


    }

    private void anhXa() {
        goBack = findViewById(R.id.iv_back_order_detail);
        lvOderDetail = findViewById(R.id.lv_order_detail);
        tvTotal = findViewById(R.id.tv_order_total_order_detail);
        tvOrderStatus = findViewById(R.id.tv_order_status_order_detail);
        btnDeleteOrder = findViewById(R.id.btn_delete_order_detail);

        if (orderDetailsList == null) {
            orderDetailsList = new ArrayList<>();
        }

    }

    private void getListOrderDetail(final int position) {
        StringRequest request = new StringRequest(StringRequest.Method.POST, GlobalVariable.GET_ORDER_DETAIL_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject result = object.getJSONObject("result");
                            int code = Integer.parseInt(result.getString("code"));
                            if (code == 0) {
                                JSONArray data = object.getJSONArray("data");
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject obj = (JSONObject) data.get(i);
                                    String id_bill = obj.getString("id_bill");
                                    String id_product = obj.getString("id_product");
                                    String quanlity = obj.getString("quanlity");
                                    orderDetailsList.add(new OrderDetail(id_bill, id_product, quanlity));
                                }
                                OrderDetailAdapter adapter = new OrderDetailAdapter(OrderDetailActivity.this,
                                        R.layout.item_order_detail, orderDetailsList);
                                lvOderDetail.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", GlobalVariable.TOKEN);
                return params;
            }

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id_bill", GlobalVariable.arrayOrder.get(position).getId_bill_order());
                return params;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(OrderDetailActivity.this);
        queue.add(request);
    }
}