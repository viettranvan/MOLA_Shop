package com.example.doancuoiky;

import com.example.doancuoiky.modal.Cart;
import com.example.doancuoiky.modal.Order;
import com.example.doancuoiky.modal.Product;

import java.util.ArrayList;
import java.util.Calendar;


public class GlobalVariable {
    public static boolean isLogin = false;

    public static ArrayList<Order> arrayOrder;
    public static ArrayList<Cart> arrayCart;
    public static ArrayList<Product> arrayProduct;
    public static ArrayList<Product> arrayMobile;
    public static ArrayList<Product> arrayLaptop;

    public static String TOKEN = null;
    public static ArrayList<String> arrayProfile;
    public static final int INDEX_ID_USER       = 0;
    public static final int INDEX_EMAIL         = 1;
    public static final int INDEX_LOGIN_NAME    = 2;
    public static final int INDEX_USER_NAME     = 3;
    public static final int INDEX_ADDRESS       = 4;
    public static final int INDEX_CITIZEN_IDENTIFICATION = 5;
    public static final int INDEX_PHONE_NUMBER  = 6;
    public static final int INDEX_GENDER        = 7;
    public static final int INDEX_ACC_CREATE    = 8;
    public static final int INDEX_AVATAR        = 9;
    public static final int INDEX_RATE          = 10;
    public static final int INDEX_BIRTHDAY      = 11;


    public static final String PASSWORD_PATTERN = "^" +
                                "(?=.*[0-9])" +         //at least 1 digit
                                "(?=.*[a-z])" +         //at least 1 lower case letter
                                "(?=.*[A-Z])" +         //at least 1 upper case letter
//                                "(?=.*[a-zA-Z])" +      //any letter
                                "(?=.*[@#$%^&+=])" +    //at least 1 special character
                                "(?=\\S+$)" +           //no white spaces
                                ".{8,30}" +               //at least 8 characters
                                "$";
    public static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
            + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    public static final String PHONE_PATTERN  = "0[0-9]{9,10}";


    private static final String localhost = "http://192.168.2.105:5000/";
    public static final String LOGIN_URL = localhost + "api/login";
    public static final String USER_INFO_URL = localhost + "api/user";
    public static final String USER_SIGN_UP_URL = localhost + "api/user";
    public static final String USER_UPDATE_URL = localhost + "api/update-user";
    public static final String USER_CHANGE_PASSWORD_URL = localhost + "api/change-password";
    public static final String PRODUCT_IMAGE_URL = localhost + "api/products-img";



    // Global Function
    public static String  validateNameFirstUpperCase(String string){
        char[] chars = string.toLowerCase().toCharArray();
        boolean found = false;
        for (int i = 0; i < chars.length; i++) {
            if (!found && Character.isLetter(chars[i])) {
                chars[i] = Character.toUpperCase(chars[i]);
                found = true;
            } else if (Character.isWhitespace(chars[i]) || chars[i]=='.' || chars[i]=='\'') { // You can add other chars here
                found = false;
            }
        }
        return String.valueOf(chars);
    }

    // format date theo định dạng ngày tháng của VN dd-MM-YYYY
    public static String formatDateInVN(String date) {

        String day, month, year;
        int int_year, int_month, int_day;

        int_year = Integer.parseInt(date.substring(0, 4));
        int_month = Integer.parseInt(date.substring(5, 7));
        int_day = Integer.parseInt(date.substring(8, 10));

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, int_day);
        cal.set(Calendar.MONTH, int_month - 1);
        cal.set(Calendar.YEAR, int_year);
        cal.add(Calendar.DAY_OF_MONTH, 1);  // tăng lên 1 ngày

        if(cal.get(Calendar.DAY_OF_MONTH) < 10){
            day = "0" + cal.get(Calendar.DAY_OF_MONTH);
        }else{
            day = "" + cal.get(Calendar.DAY_OF_MONTH);
        }

        if((cal.get(Calendar.MONTH) + 1) < 10){
            month = "0" + (cal.get(Calendar.MONTH) + 1);
        }else{
            month = "" + (cal.get(Calendar.MONTH) + 1);
        }
        year    = "" + cal.get(Calendar.YEAR);

        return day + "/" + month + "/" + year;
    }
}

