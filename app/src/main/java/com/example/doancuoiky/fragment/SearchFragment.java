package com.example.doancuoiky.fragment;

import android.accessibilityservice.AccessibilityService;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.inputmethodservice.Keyboard;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.doancuoiky.AnimationUtil;
import com.example.doancuoiky.GlobalVariable;
import com.example.doancuoiky.R;
import com.example.doancuoiky.activity.MainActivity;
import com.example.doancuoiky.activity.ProductDetailActivity;
import com.example.doancuoiky.adapter.PhotoAdapter;
import com.example.doancuoiky.adapter.ProductAdapter;
import com.example.doancuoiky.adapter.ProductNewAdapter;
import com.example.doancuoiky.adapter.ProductSearchAdapter;
import com.example.doancuoiky.adapter.SearchAdapter;
import com.example.doancuoiky.modal.Cart;
import com.example.doancuoiky.modal.Photo;
import com.example.doancuoiky.modal.Product;
import com.example.doancuoiky.modal.Search;

import java.nio.file.attribute.GroupPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;

import me.relex.circleindicator.CircleIndicator;

public class SearchFragment extends Fragment {

    private ListView lvData,lvHistory;
    private ArrayList<Search> arrayListProductName, arrayListHistory;
    private TextView clearHistory;
    private SearchAdapter adapterData,adapterHistory;
    private LinearLayout layoutHistory;
    private View view;
    EditText txtSearch;
    SearchView searchView;
    ArrayList<Product> arraySearch;
    TextView tvNoSearchResultReturn;

    private RecyclerView rcvSearch;
    ProductSearchAdapter searchAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_search, container, false);

        anhXa(view);
        checkData();
        setAdapterSearch();


        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                arrayListHistory.clear();
                adapterHistory.notifyDataSetChanged();
                checkData();
            }
        });

        searchAdapter.onGotoDetail(new ProductSearchAdapter.IClickGotoProductDetail() {
            @Override
            public void onClickGotoDetail(String idProduct) {
                Intent intent = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(),ProductDetailActivity.class);
                intent.putExtra("productDetail",idProduct);
                startActivity(intent);
            }
        });

        setHasOptionsMenu(true);
        return  view;
    }



    private void setDataSearch(String keyword){

        arraySearch.clear();
        for(int i = 0;i < GlobalVariable.arrayProduct.size();i++){
            String strRoot = GlobalVariable.arrayProduct.get(i).getProductName().toLowerCase();
            String srtKeyword = keyword.trim().toLowerCase();
            if(strRoot.contains(srtKeyword)){
                arraySearch.add(GlobalVariable.arrayProduct.get(i));
            }
        }
        if(arraySearch.size() == 0){
            tvNoSearchResultReturn.setVisibility(View.VISIBLE);
        }
        else{
            tvNoSearchResultReturn.setVisibility(View.GONE);
        }
        searchAdapter.notifyDataSetChanged();

    }

    private void setAdapterSearch(){
        searchAdapter = new ProductSearchAdapter(getContext(), arraySearch);
        rcvSearch.setHasFixedSize(true);
        rcvSearch.setLayoutManager(new GridLayoutManager(getContext(),2));
        rcvSearch.setAdapter(searchAdapter);
    }

    private void anhXa(View view) {

        if(arraySearch == null){
            arraySearch = new ArrayList<>();
        }

        lvData = view.findViewById(R.id.lv_search);
        lvHistory = view.findViewById(R.id.lv_history_search);
        clearHistory = view.findViewById(R.id.tv_delete_history_search_fragment);
        layoutHistory = view.findViewById(R.id.layout_history_search_search_fragment);
        rcvSearch = view.findViewById(R.id.rcv_search);
        tvNoSearchResultReturn = view.findViewById(R.id.tv_no_search_result_return);
        arrayListProductName = new ArrayList<Search>();

        // set data
        for(int i = 0;i < GlobalVariable.arrayProduct.size(); i++){
            arrayListProductName.add(new Search(GlobalVariable.arrayProduct.get(i).getProductName().trim()));
        }

        if(arrayListHistory == null){
            arrayListHistory = new ArrayList<Search>();
        }


        adapterData = new SearchAdapter(getContext(),R.layout.item_search,arrayListProductName);
        lvData.setAdapter(adapterData);

        adapterHistory = new SearchAdapter(getContext(),R.layout.item_search,arrayListHistory);
        lvHistory.setAdapter(adapterHistory);
    }

    // them icon search len thanh toolbar
    @Override
    public void onCreateOptionsMenu(@NonNull final Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.search,menu);

//        // auto focus search
        menu.findItem(R.id.ic_search_toolbar).expandActionView();

        MenuItem.OnActionExpandListener onActionExpandListener = new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                Toast.makeText(getContext(), "Expand" , Toast.LENGTH_SHORT).show();
                Log.d("TAG1", "Expand: focus:" +txtSearch.isFocusable() );
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                Toast.makeText(getContext(), "Collapse", Toast.LENGTH_SHORT).show();
                Log.d("TAG1", "Collapse: focus:" +txtSearch.isFocusable() );
                return true;
            }
        };

        menu.findItem(R.id.ic_search_toolbar).setOnActionExpandListener(onActionExpandListener);

        searchView = (SearchView) menu.findItem(R.id.ic_search_toolbar).getActionView();

        txtSearch = ((EditText)searchView.findViewById(androidx.appcompat.R.id.search_src_text));
        txtSearch.setHint("Nhập tên sản phẩm bạn muốn tìm kiếm");

        txtSearch.setHintTextColor(Color.LTGRAY);
        txtSearch.setTextColor(Color.WHITE);
        txtSearch.setFocusable(true);

        Log.d("TAG1", "onCreateOptionsMenu: focus:" +txtSearch.isFocusable() );

        adapterHistory.onCopyKeySearch(new SearchAdapter.IClickOnCopyKeySearch() {
            @Override
            public void onClickCopyKeySearch(int index) {
                String text = arrayListHistory.get(index).getTitle();
                if(menu.findItem(R.id.ic_search_toolbar).expandActionView()){
                    txtSearch.setText(text);
                }
            }
        });

        adapterData.onCopyKeySearch(new SearchAdapter.IClickOnCopyKeySearch() {
            @Override
            public void onClickCopyKeySearch(int index) {
                String text = arrayListProductName.get(index).getTitle();
                if(menu.findItem(R.id.ic_search_toolbar).expandActionView()){
                    txtSearch.setText(text);
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                checkAddHistory(query);
                Toast.makeText(getContext(), query, Toast.LENGTH_SHORT).show();
                txtSearch.clearFocus();
                lvData.setVisibility(View.GONE);
                hideKeyboard(view);
                checkFocus();

                showDataSearchReturn(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.length() > 0){
                    lvData.setVisibility(View.VISIBLE);
                    adapterData.getFilter().filter(newText);
                    layoutHistory.setVisibility(View.INVISIBLE);
                }
                else{
                    lvData.setVisibility(View.INVISIBLE);
                    rcvSearch.setVisibility(View.GONE);

                    checkData();
                }
                return true;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);

    }

    private void checkFocus() {

        txtSearch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    rcvSearch.setVisibility(View.GONE);
                    lvData.setVisibility(View.VISIBLE);
                } else {
                    lvData.setVisibility(View.GONE);
                }
            }
        });
    }

    private void showDataSearchReturn(String keyword) {
        rcvSearch.setVisibility(View.VISIBLE);
        setDataSearch(keyword);
    }

    private void checkAddHistory(String query){
        boolean flag = true;
        for(int i = 0;i < arrayListHistory.size();i++){
            if(arrayListHistory.get(i).getTitle().equals(query)){
                flag = false;
                return;
            }
        }
        if(flag){
            Search newHistory = new Search(query);
            arrayListHistory.add(newHistory);
            adapterHistory.notifyDataSetChanged();
        }
    }

    private void checkData(){
        if(arrayListHistory.size() <= 0){
            layoutHistory.setVisibility(View.INVISIBLE);
        }
        else{
            layoutHistory.setVisibility(View.VISIBLE);
        }
    }

    private void hideKeyboard(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
    }
}














