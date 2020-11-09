package com.example.doancuoiky.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;


import android.content.Intent;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;
import com.aurelhubert.ahbottomnavigation.notification.AHNotification;
import com.example.doancuoiky.GlobalVariable;
import com.example.doancuoiky.R;
import com.example.doancuoiky.adapter.ViewPagerAdapter;
import com.example.doancuoiky.fragment.HomeFragment;
import com.example.doancuoiky.modal.Cart;
import com.example.doancuoiky.modal.Product;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.goToCartOnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView menuNavigationView;
    private long backPressedTime;
    private ListView listView;
    private Toast mToast;
    private AHBottomNavigation ahBottomNavigation;
    private AHBottomNavigationViewPager ahBottomNavigationViewPager;
    private ViewPagerAdapter adapter;
    private TextView toolBarTitle;

    private View viewEndAnimation;
    private ImageView viewAnimation;

    private View headerView;
    private LinearLayout headerNotLoggedIn,headerLoggedIn;
    private ImageView headerAvatar;
    private TextView headerName,headerEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        anhXa();

        initData();

        setUpViewPager(); /*chuyển trang bằng cách click vào icon hoặc vuốt*/

        actionToolBar(); // toolbar, mở drawer menu

        menuNavigationView.setNavigationItemSelectedListener(this);

        if(GlobalVariable.arrayCart.size() > 0){
            setCountProductInCart(GlobalVariable.arrayCart.size());
        }

        if(getIntent().getExtras() != null){

            Intent intent = getIntent();

            // chuyen den man hinh profile
            String toProfile = intent.getStringExtra("gotoProfile");
            if (toProfile != null && toProfile.contentEquals("profile")) {
                GlobalVariable.isLogin=true;
                checkLogin();
                ahBottomNavigation.setCurrentItem(4);
            }

            // chuyen den man hinh gio hang
            String toCart = intent.getStringExtra("gotoCart");
            if (toCart != null && toCart.contentEquals("cart")) {
                ahBottomNavigation.setCurrentItem(3);
            }

            if(getIntent().getExtras().getBoolean("loginTrue") == true){
                GlobalVariable.isLogin = true;
                checkLogin();
            }
        }

        setDataProfile();

        checkLogin();
    }

    private void initData() {
        if(GlobalVariable.arrayCart != null){

        }else{
            GlobalVariable.arrayCart = new ArrayList<>();

//            GlobalVariable.arrayCart.add(new Cart("00a","001","test","4gb","small",100000,R.drawable.meow,1));
        }

        if(GlobalVariable.arrayProfile != null){

        }
        else{
            GlobalVariable.arrayProfile = new ArrayList<>();

        }

        if(GlobalVariable.arrayProduct != null){

        }else{
            GlobalVariable.arrayProduct = new ArrayList<>();
            GlobalVariable.arrayProduct.add(new Product("001","001","Điện thoại realme",
                    getString(R.string.mota),"4gb-64gb",4000000,R.drawable.realme_banner_resize));
            GlobalVariable.arrayProduct.add(new Product("002","001","Điện thoại iphone XR",
                    getString(R.string.mota),"4gb-64gb",10000000,R.drawable.iphone));
            GlobalVariable.arrayProduct.add(new Product("003","001","Điện thoại samsung",
                    getString(R.string.mota),"4gb-64gb",8000000,R.drawable.samsum_banner_resize));
            GlobalVariable.arrayProduct.add(new Product("004","002","Laptop Asus",
                    getString(R.string.mota),"4gb-64gb",10500500,R.drawable.laptop_asus));
            GlobalVariable.arrayProduct.add(new Product("005","002","Laptop dell",
                    getString(R.string.mota),"4gb-64gb",9000000,R.drawable.laptop_dell));
            GlobalVariable.arrayProduct.add(new Product("006","001","Điện thoại samsung",
                    getString(R.string.mota),"4gb-64gb",8000000,R.drawable.meow));
            GlobalVariable.arrayProduct.add(new Product("007","002","Laptop Asus",
                    getString(R.string.mota),"4gb-64gb",10500500,R.mipmap.ic_launcher));
            GlobalVariable.arrayProduct.add(new Product("008","002","Laptop dell",
                    getString(R.string.mota),"4gb-64gb",9000000,R.drawable.iphone1));
        }

//        if(GlobalVariable.arrayMobile == null){
//            GlobalVariable.arrayMobile = new ArrayList<>();
//            for(int i = 0;i < GlobalVariable.arrayProduct.size();i++){
//                if(GlobalVariable.arrayProduct.get(i).getProductTypeID().equals("001")){
//                    GlobalVariable.arrayMobile.add(GlobalVariable.arrayProduct.get(i));
//                }
//            }
//        }
//
//        if(GlobalVariable.arrayLaptop== null){
//            GlobalVariable.arrayLaptop = new ArrayList<>();
//            for(int i = 0;i < GlobalVariable.arrayProduct.size();i++){
//                if(GlobalVariable.arrayLaptop.get(i).getProductTypeID().equals("002")){
//                    GlobalVariable.arrayLaptop.add(GlobalVariable.arrayProduct.get(i));
//                }
//            }
//        }
    }

    private void setDataProfile() {

        if(GlobalVariable.isLogin){

            RequestQueue queue = Volley.newRequestQueue(MainActivity.this);
            StringRequest request = new StringRequest(Request.Method.GET, GlobalVariable.USER_INFO_URL,
                    new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject object = new JSONObject(response);
                        JSONObject data = object.getJSONObject("data");

                        String id_user = data.getString("id_user");
                        String email = data.getString("email");
                        String loginname = data.getString("loginname");
                        String username = data.getString("username");
                        String address = data.getString("address");
                        String citizen_identification = data.getString("citizen_identification");
                        String phone_number = data.getString("phone_number");
                        String gender = data.getString("gender");
                        String acc_created = data.getString("acc_created");
                        String avatar = data.getString("avatar");
                        String rate = data.getString("rate");

                        if(address.length() == 0 || address.equals("null")){
                            address = "";
                        }
                        if(citizen_identification.length() == 0 || citizen_identification.equals("null")){
                            citizen_identification = "";
                        }

                        GlobalVariable.arrayProfile.add(id_user);
                        GlobalVariable.arrayProfile.add(email);
                        GlobalVariable.arrayProfile.add(loginname);
                        GlobalVariable.arrayProfile.add(username);
                        GlobalVariable.arrayProfile.add(address);
                        GlobalVariable.arrayProfile.add(citizen_identification);
                        GlobalVariable.arrayProfile.add(phone_number);
                        GlobalVariable.arrayProfile.add(gender);
                        GlobalVariable.arrayProfile.add(acc_created);
                        GlobalVariable.arrayProfile.add(avatar);
                        GlobalVariable.arrayProfile.add(rate);

                        if(GlobalVariable.arrayProfile.get(9).length() > 0){
                            Picasso.with(MainActivity.this)
                                .load(GlobalVariable.arrayProfile.get(9))
                                .into(headerAvatar);
                        }

                        headerName.setText(GlobalVariable.arrayProfile.get(3));
                        headerEmail.setText(GlobalVariable.arrayProfile.get(1));

                    } catch (JSONException e) {
                        Log.d("TAG1", "error1 => "  + "\n");
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("TAG1", "error => " + error.toString() + "\n");

                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", GlobalVariable.TOKEN);
                    return params;
                }
            };

            queue.add(request);

        }
    }

    private void checkLogin() {
        Menu menu = menuNavigationView.getMenu();

        if(GlobalVariable.isLogin){
            menu.findItem(R.id.nav_login).setVisible(false);
            menu.findItem(R.id.nav_logout).setVisible(true); // hiện logout
            menu.findItem(R.id.nav_profile).setVisible(true); // hiện profile
            headerLoggedIn.setVisibility(View.VISIBLE);
            headerNotLoggedIn.setVisibility(View.GONE);

        }
        else{
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(false); // ẩn logout
            menu.findItem(R.id.nav_profile).setVisible(false); // ẩn profile
            headerLoggedIn.setVisibility(View.GONE);
            headerNotLoggedIn.setVisibility(View.VISIBLE);
        }
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolBarTitle.setText("Trang chủ");
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    private void anhXa() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        menuNavigationView = findViewById(R.id.navigation_view);
        listView = findViewById(R.id.listview);
        ahBottomNavigation  = findViewById(R.id.AHBottomNavigation);
        ahBottomNavigationViewPager = findViewById(R.id.AHBottomNavigationViewPager);
        viewEndAnimation = findViewById(R.id.view_end_animation);
        viewAnimation = findViewById(R.id.view_animation);
        toolBarTitle = findViewById(R.id.tv_toolbar_title);

        headerView = menuNavigationView.getHeaderView(0);
        headerNotLoggedIn = headerView.findViewById(R.id.header_drawer_not_logged_in);
        headerLoggedIn = headerView.findViewById(R.id.header_drawer_logged_in);
        headerAvatar = headerView.findViewById(R.id.drawer_menu_avatar);
        headerName = headerView.findViewById(R.id.drawer_menu_name);
        headerEmail = headerView.findViewById(R.id.drawer_menu_email);

    }

    // bottom tab
    private void setUpViewPager(){
        adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        ahBottomNavigationViewPager.setAdapter(adapter);
        ahBottomNavigationViewPager.setPagingEnabled(true);

        // Create items
        AHBottomNavigationItem tab_home = new AHBottomNavigationItem(R.string.tab_home, R.mipmap.baseline_home_black_24, R.color.tab_home);
        AHBottomNavigationItem tab_product = new AHBottomNavigationItem(R.string.tab_product, R.mipmap.icon_product, R.color.tab_product);
        AHBottomNavigationItem tab_search = new AHBottomNavigationItem(R.string.tab_search, R.mipmap.baseline_search_black_24, R.color.tab_product);
        AHBottomNavigationItem tab_cart = new AHBottomNavigationItem(R.string.tab_cart, R.mipmap.icon_cart, R.color.tab_cart);
        AHBottomNavigationItem tab_profile = new AHBottomNavigationItem(R.string.tab_profile, R.mipmap.baseline_person_black_24, R.color.tab_profile);

        // Add items
        ahBottomNavigation.addItem(tab_home);
        ahBottomNavigation.addItem(tab_product);
        ahBottomNavigation.addItem(tab_search);
        ahBottomNavigation.addItem(tab_cart);
        ahBottomNavigation.addItem(tab_profile);

        ahBottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {
                ahBottomNavigationViewPager.setCurrentItem(position);
                // đặt lại toolbar title tương ứng với tab
                setToolbarTitle(position);

                return true;
            }
        });

        // chuyen fragment bang cach vuot
        ahBottomNavigationViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                ahBottomNavigation.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            if(backPressedTime + 2000 > System.currentTimeMillis()){
                mToast.cancel();
                super.onBackPressed();
                return;
            }
            else{
                mToast = Toast.makeText(MainActivity.this,"Nhấn back thêm 1 lần nữa để thoát",Toast.LENGTH_SHORT);
                mToast.show();
            }
            backPressedTime = System.currentTimeMillis();
        }
    }

    // su kien khi item trong drawer menu dc click
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.nav_mobile:
//                CartFragment cartFragment = new CartFragment();
//                loadFragment(cartFragment);
//                drawerLayout.closeDrawer(GravityCompat.START);
                Toast.makeText(this,"mobile",Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_share:
                Toast.makeText(this,"share: " + GlobalVariable.TOKEN ,Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_rate:
                Toast.makeText(this,"rate: " + GlobalVariable.arrayProfile.size() ,Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_laptop:
                drawerLayout.closeDrawer(GravityCompat.START);
                ahBottomNavigation.setCurrentItem(1);
                break;

            case R.id.nav_login:
                Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(intent);
//                Toast.makeText(MainActivity.this,"login",Toast.LENGTH_SHORT).show();
                break;

            case R.id.nav_profile:
                drawerLayout.closeDrawer(GravityCompat.START);
                ahBottomNavigation.setCurrentItem(4);
                break;
            case R.id.nav_logout:
                drawerLayout.closeDrawer(GravityCompat.START);
                GlobalVariable.isLogin = false;
                GlobalVariable.TOKEN = null;
                GlobalVariable.arrayProfile.clear();
                checkLogin();

                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
        }
        return true;
    }

    public void loadFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.main_activity,fragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }

    public View getViewEndAnimation() {
        return viewEndAnimation;
    }

    public ImageView getViewAnimation() {
        return viewAnimation;
    }

    public void setCountProductInCart(int count){
        AHNotification notification = new AHNotification.Builder()
                .setText(String.valueOf(count))
                .setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.bg_red))
                .setTextColor(ContextCompat.getColor(MainActivity.this, R.color.white))
                .build();
        ahBottomNavigation.setNotification(notification, 3);
    }

    private void setToolbarTitle(int position){
        switch (position){
            case 0:
                setSupportActionBar(toolbar);
                toolBarTitle.setText("Trang chủ");
                break;
            case 1:
                setSupportActionBar(toolbar);
                toolBarTitle.setText("Danh mục sản phẩm");
                break;
            case 2:
                setSupportActionBar(toolbar);
                toolBarTitle.setText("Tìm kiếm");

                break;
            case 3:
                setSupportActionBar(toolbar);
                toolBarTitle.setText("Giỏ hàng");
                break;
            case 4:
                setSupportActionBar(toolbar);
                toolBarTitle.setText("Thông tin cá nhân");
                break;
        }
        toolbar.setNavigationIcon(R.drawable.ic_action_menu);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    // chuyen den man hinh gio hang
    @Override
    public void onCartIconClickListener() {
        ahBottomNavigation.setCurrentItem(3);
    }

}
