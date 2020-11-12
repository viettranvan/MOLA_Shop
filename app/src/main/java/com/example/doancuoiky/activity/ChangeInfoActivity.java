package com.example.doancuoiky.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.doancuoiky.GlobalVariable;
import com.example.doancuoiky.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class ChangeInfoActivity extends AppCompatActivity {

    private ImageView goBackChangeInfo;
    private RadioButton male,female;
    private Button update;
    private TextView birthday;
    private EditText edtFullName, edtEmail, edtIdentify, edtPhoneNumber, edtAddress;
    String _name,_email,_citizen,_phone,_address,_gender,_birthday;
    private ImageView avatar, openFile;

    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_info);

        anhXa();
        setOnClick();
    }

    private void setOnClick() {
        goBackChangeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        birthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog();
            }
        });

        openFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //check runtime permission
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                        // permission not granted, request it
                        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                        //show pop up
                        requestPermissions(permissions,PERMISSION_CODE);
                    }else{
                        // permission already granted
                        pickImageFromGallery();
                    }
                }else{
                    // system os is less then marshmallow
                    pickImageFromGallery();
                }
            }
        });

        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChangeInfoActivity.this,male.getText(),Toast.LENGTH_SHORT).show();
            }
        });

        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChangeInfoActivity.this,female.getText(),Toast.LENGTH_SHORT).show();
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getDataInfo();
                onChangeInfo();
            }
            
        });
    }

    private void pickImageFromGallery() {
        // intent to pick image
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    // handle result for runtime permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case PERMISSION_CODE:{
                if(grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // permission was granted
                    pickImageFromGallery();
                }
                else{
                    // permission was denied
                    Toast.makeText(this, "Vui lòng cấp quyền...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // handle result for pick image
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            // set image to image view
            avatar.setImageURI(data.getData());
        }
    }

    private void goToMainActivity() {
        Intent intent = new Intent(ChangeInfoActivity.this,MainActivity.class);
                intent.putExtra("gotoProfile","profile");
        startActivity(intent);
    }

    private void showDateDialog() {

        Calendar calendar = Calendar.getInstance();

        int YEAR = calendar.get(Calendar.YEAR);
        int MONTH = calendar.get(Calendar.MONTH); // tháng render từ 0-11
        int DATE = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int dateOfMonth) {
                String strDate = dateOfMonth + "/" + (month+1) + "/" + year;
                birthday.setText(strDate);
            }
        }, YEAR,MONTH , DATE);

        datePickerDialog.show();
    }

    private void anhXa() {
        goBackChangeInfo = findViewById(R.id.iv_back_change_info);
        male = findViewById(R.id.rd_gender_male_change_info);
        female = findViewById(R.id.rd_gender_female_change_info);
        update = findViewById(R.id.btn_update_change_info);
        birthday = findViewById(R.id.tv_birthday_change_info);
        edtFullName = findViewById(R.id.edt_username_change_info);
        edtEmail = findViewById(R.id.edt_email_change_info);
        edtIdentify = findViewById(R.id.edt_identification_card_change_info);
        edtPhoneNumber = findViewById(R.id.edt_phone_number_change_info);
        edtAddress = findViewById(R.id.edt_address_change_info);
        avatar = findViewById(R.id.img_avatar_change_info);
        openFile = findViewById(R.id.img_open_folder);

        edtFullName.setText(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_USER_NAME));
        edtEmail.setText(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_EMAIL));
        edtIdentify.setText(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_CITIZEN_IDENTIFICATION));

        if(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_CITIZEN_IDENTIFICATION).length() > 0
        && !GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_CITIZEN_IDENTIFICATION).equals("null")){

        }
        edtPhoneNumber.setText(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_PHONE_NUMBER));
        edtAddress.setText(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_ADDRESS));

        if(GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_GENDER).length() == 0 ||
                GlobalVariable.arrayProfile.get(GlobalVariable.INDEX_GENDER).equals("0")){
            male.setChecked(true);
            female.setChecked(false);
        }
        else{
            male.setChecked(false);
            female.setChecked(true);
        }

        getDataInfo();


    }

    private void getDataInfo() {
        _name = edtFullName.getText().toString();
        _email = edtEmail.getText().toString();
        _birthday = formatDate(birthday.getText().toString());
        if(male.isChecked()){
            _gender = "0";
        }else{
            _gender = "1";
        }
        _citizen = edtIdentify.getText().toString();
        _phone = edtPhoneNumber.getText().toString();
        _address = edtAddress.getText().toString();
    }

    private void onChangeInfo(){
        StringRequest request = new StringRequest(Request.Method.POST, GlobalVariable.USER_UPDATE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject object = new JSONObject(response);
                            JSONObject result = object.getJSONObject("result");

                            int code = result.getInt("code");
                            if(code == 0){

                                AlertDialog.Builder builder = new AlertDialog.Builder(ChangeInfoActivity.this);

                                builder.setTitle("Thông báo");
                                builder.setMessage("Xác nhận cập nhật ?");

                                builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        setDataAraayProfile();
                                        goToMainActivity();
                                        finish();
                                    }
                                });

                                builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                    }
                                });
                                builder.show();

                            }
                            else{
                                Toast.makeText(ChangeInfoActivity.this, "Cập nhật thất bại",
                                        Toast.LENGTH_LONG).show();
                                Log.d("TAG1", "error1: ");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChangeInfoActivity.this, "Cập nhật thất bại => " + e.toString(), Toast.LENGTH_SHORT).show();
                            Log.d("TAG1", "error: => " + e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ChangeInfoActivity.this, "Cập nhật thất bại",
                        Toast.LENGTH_LONG).show();
                Log.d("TAG1", "error2: => " + error.toString());

            }
        }){

            @Override
            protected Map<String, String> getParams()
            {
                getDataInfo();
                Map<String, String>  params = new HashMap<String, String>();
                params.put("username",GlobalVariable.validateNameFirstUpperCase(_name));
                params.put("email", _email);
                params.put("birthday", _birthday);
                params.put("gender", _gender);
                params.put("citizen_identification", _citizen);
                params.put("phone_number", _phone);
                params.put("address", _address);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", GlobalVariable.TOKEN);
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(ChangeInfoActivity.this);

        queue.add(request);

    }

    private void setDataAraayProfile() {
        getDataInfo();
        GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_USER_NAME,_name);
        GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_ADDRESS,_address);
        GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_CITIZEN_IDENTIFICATION, _citizen);
        GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_PHONE_NUMBER,_phone);
        GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_GENDER,_gender);
//        GlobalVariable.arrayProfile.set(GlobalVariable.INDEX_BIRTHDAY,_birthday);
    }

    // doi lai cho dung dinh dang cua database yyyy-mm-dd
    private String formatDate(String date){
        String  year    = date.substring(6,10);
        String month    = date.substring(3, 5);
        String day      = date.substring(0,2);
        String result = year + "-" + month + "-" +day;

        return result;
    }
}