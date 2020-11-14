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

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

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
import com.example.doancuoiky.fragment.ProductFragment;
import com.example.doancuoiky.modal.Product;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, HomeFragment.goToCartOnClickListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavigationView menuNavigationView;
    private long backPressedTime;
    private Toast mToast;
    private AHBottomNavigation ahBottomNavigation;
    private AHBottomNavigationViewPager ahBottomNavigationViewPager;
    private TextView toolBarTitle;

    private View viewEndAnimation;
    private ImageView viewAnimation;

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

            if(Objects.requireNonNull(getIntent().getExtras()).getBoolean("loginTrue")){
                GlobalVariable.isLogin = true;
                checkLogin();
            }
        }

        setDataProfile();

        checkLogin();
    }

    private void initData() {

        if(GlobalVariable.arrayCart == null){
            GlobalVariable.arrayCart = new ArrayList<>();
//            GlobalVariable.arrayCart.add(new Cart("00a","001","test","4gb","small",100000,R.drawable.meow,1));
        }


        if(GlobalVariable.arrayProfile == null){
            GlobalVariable.arrayProfile = new ArrayList<>();
        }

        if(GlobalVariable.arrayProduct == null){
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

        if (GlobalVariable.arrayMobile == null) {
            GlobalVariable.arrayMobile = new ArrayList<>();
            int size = GlobalVariable.arrayProduct.size();
            for(int i = 0;i < size;i++){
                if (GlobalVariable.arrayProduct.get(i).getProductTypeID().equals("001")) {
                    GlobalVariable.arrayMobile.add(GlobalVariable.arrayProduct.get(i));
                }
            }

        }
        if (GlobalVariable.arrayLaptop == null) {
            GlobalVariable.arrayLaptop = new ArrayList<>();
            int size = GlobalVariable.arrayProduct.size();
            for(int i = 0;i < size;i++){
                if (GlobalVariable.arrayProduct.get(i).getProductTypeID().equals("002")) {
                    GlobalVariable.arrayLaptop.add(GlobalVariable.arrayProduct.get(i));
                }
            }
        }

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
                        String birthday = data.getString("birthday");

                        Log.d("TAG11", "onResponse: " + avatar);

                        // validate data thành rỗng nếu data trống hoặc null
                        if(address.length() == 0 || address.equals("null")){
                            address = "";
                        }
                        if(citizen_identification.length() == 0 || citizen_identification.equals("null")){
                            citizen_identification = "";
                        }
                        if(phone_number.length() == 0 || phone_number.equals("null")){
                            phone_number = "";
                        }
                        if(gender.length() == 0 || gender.equals("null")){
                            gender = "";
                        }
                        if(rate.length() == 0 || rate.equals("null")){
                            rate = "";
                        }
                        if(birthday.length() == 0 || birthday.equals("null")){
                            birthday = "";
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
                        GlobalVariable.arrayProfile.add(birthday);


                        String imageName = "no_avatar";
                        String PACKAGE_NAME = getApplicationContext().getPackageName();
                        int imgID = getResources().getIdentifier(PACKAGE_NAME + ":drawable/" + imageName, null, null);
                        headerAvatar.setImageResource(imgID);

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
                public Map<String, String> getHeaders() {
                    Map<String, String> params = new HashMap<>();
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
            menu.findItem(R.id.nav_rate).setVisible(true); // hiện rate
            headerLoggedIn.setVisibility(View.VISIBLE);
            headerNotLoggedIn.setVisibility(View.GONE);
        }
        else{
            menu.findItem(R.id.nav_login).setVisible(true);
            menu.findItem(R.id.nav_logout).setVisible(false); // ẩn logout
            menu.findItem(R.id.nav_profile).setVisible(false); // ẩn profile
            menu.findItem(R.id.nav_rate).setVisible(false); // ẩn rate
            headerLoggedIn.setVisibility(View.GONE);
            headerNotLoggedIn.setVisibility(View.VISIBLE);
        }
    }

    private void actionToolBar() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolBarTitle.setText(getString(R.string.title_toolbar_home)); // trang chủ
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
        ahBottomNavigation  = findViewById(R.id.AHBottomNavigation);
        ahBottomNavigationViewPager = findViewById(R.id.AHBottomNavigationViewPager);
        viewEndAnimation = findViewById(R.id.view_end_animation);
        viewAnimation = findViewById(R.id.view_animation);
        toolBarTitle = findViewById(R.id.tv_toolbar_title);

        View headerView = menuNavigationView.getHeaderView(0);
        headerNotLoggedIn = headerView.findViewById(R.id.header_drawer_not_logged_in);
        headerLoggedIn = headerView.findViewById(R.id.header_drawer_logged_in);
        headerAvatar = headerView.findViewById(R.id.drawer_menu_avatar);
        headerName = headerView.findViewById(R.id.drawer_menu_name);
        headerEmail = headerView.findViewById(R.id.drawer_menu_email);

    }

    // bottom tab
    private void setUpViewPager(){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
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
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {
                ahBottomNavigation.setCurrentItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {}
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
            case R.id.nav_home:
                drawerLayout.closeDrawer(GravityCompat.START);
                ahBottomNavigation.setCurrentItem(0);
                break;
            case R.id.nav_mobile:
                ProductFragment.showAllMobileProduct();
                drawerLayout.closeDrawer(GravityCompat.START);
                ahBottomNavigation.setCurrentItem(1);
                break;
            case R.id.nav_laptop:
                drawerLayout.closeDrawer(GravityCompat.START);
                ProductFragment.showAllLaptopProduct();
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
                GlobalVariable.arrayProfile = null;
                checkLogin();

                finish();
                startActivity(new Intent(this,MainActivity.class));
                break;
            case R.id.nav_share:
                Intent intentSupport = new Intent(MainActivity.this,SupportActivity.class);
                startActivity(intentSupport);
                break;
            case R.id.nav_rate:
                    drawerLayout.closeDrawer(GravityCompat.START);
                    DialogAppRate();
                break;
        }
        return true;
    }

    private void DialogAppRate() {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_app_rate);

        Button btnExit = dialog.findViewById(R.id.btn_exit_app_rating);
        Button btnSubmit = dialog.findViewById(R.id.btn_submit_app_rating);
        final RatingBar ratingBar = dialog.findViewById(R.id.rating_bar_app_name);

        String strRating = GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_RATE);
        if(strRating.length() == 0 || strRating.equals("null")){
            strRating = "0";
        }
        int currentRating = Integer.parseInt(strRating);
        ratingBar.setRating(currentRating);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rating = (int) ratingBar.getRating();
                if(rating == 0){
                    Toast.makeText(MainActivity.this, "Vui lòng chọn số sao bạn muốn đánh giá", Toast.LENGTH_SHORT).show();
                }
                else{
                    int ratingINT = (int) ratingBar.getRating();
                    updateAppRating(String.valueOf(ratingINT), dialog);
                }
            }
        });
        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void updateAppRating(final String _rate, final Dialog dialog) {
        StringRequest request = new StringRequest(Request.Method.POST, GlobalVariable.USER_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject result = object.getJSONObject("result");

                            int code = result.getInt("code");
                            if(code == 0){
                                Toast.makeText(MainActivity.this, "Cám ơn về đánh giá của bạn", Toast.LENGTH_SHORT).show();
                                GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_RATE,_rate);
                                dialog.dismiss();
                            }
                            else{
                                Toast.makeText(MainActivity.this, "Cập nhật thất bại",
                                        Toast.LENGTH_LONG).show();
                                Log.d("TAG1", "error1: ");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(MainActivity.this, "Cập nhật thất bại", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Cập nhật thất bại",
                        Toast.LENGTH_LONG).show();

            }
        }){

            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<>();
                params.put("email",GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_EMAIL));
                params.put("rate", _rate);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", GlobalVariable.TOKEN);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(MainActivity.this);

        queue.add(request);
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
                toolBarTitle.setText(getString(R.string.title_toolbar_home)); // trang chủ
                break;
            case 1:
                setSupportActionBar(toolbar);
                toolBarTitle.setText(getString(R.string.title_toolbar_product)); // danh mục sản phẩm
                break;
            case 2:
                setSupportActionBar(toolbar);
                toolBarTitle.setText(getString(R.string.title_toolbar_search)); // tìm kiếm

                break;
            case 3:
                setSupportActionBar(toolbar);
                toolBarTitle.setText(getString(R.string.title_toolbar_cart)); // giỏ hàng
                break;
            case 4:
                setSupportActionBar(toolbar);
                toolBarTitle.setText(getString(R.string.title_toolbar_profile)); // thông tin cá nhân
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
