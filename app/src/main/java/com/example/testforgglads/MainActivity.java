package com.example.testforgglads;

import android.content.Intent;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.testforgglads.adapters.ProductAdapter;
import com.example.testforgglads.models.Product;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ListView productLV;
    ArrayList<String> categList = new ArrayList<>();
    ArrayList<Product> productList = new ArrayList<>();
    ProductAdapter productAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private String access_token = "591f99547f569b05ba7d8777e2e0824eea16c440292cce1f8dfb3952cc9937ff";
    private Spinner mSpinner;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadCategories();
        bindViews();

        toolbar.setTitle(getResources().getString(R.string.app_name));
    }

    //считывание всех категорий
    private void loadCategories() {
        Ion.with(getApplicationContext())
                .load("https://api.producthunt.com/v1/categories")
                .setHeader("Authorization", "Bearer " + access_token)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.w("links", result.toString());
                        if (result != null && result.has("categories")) {
                            JsonArray categArrJSON = result.getAsJsonArray("categories");
                            for (int i = 0; i < categArrJSON.size(); i++) {
                                JsonObject obj = categArrJSON.get(i).getAsJsonObject();
                                categList.add(obj.get("name").toString().replace("\"", ""));
                            }
                        }
                        initCategAdapter();
                        //выбираем категорию Tech
                        for (int i = 0; i < mSpinner.getCount(); i++) {
                            if (mSpinner.getItemAtPosition(i).toString().equalsIgnoreCase("Tech")) {
                                mSpinner.setSelection(i);
                                break;
                            }
                        }
                    }
                });
    }


    public static boolean containsId(ArrayList<Product> list, long id) {
        for (Product object : list) {
            if (object.getPostID() == id) {
                return true;
            }
        }
        return false;
    }


    private void initCategAdapter() {
        ArrayAdapter<String> myAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.custom_spinner_item,
                categList);
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(myAdapter);
        myAdapter.notifyDataSetChanged();
    }


    private void initListAdapter() {
        productAdapter = new ProductAdapter(this, R.layout.list_item, productList);
        productLV.setAdapter(productAdapter);
        productAdapter.notifyDataSetChanged();
    }

    private void bindViews() {
        productLV = (ListView) findViewById(R.id.elements_lv);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        mSpinner = (Spinner) findViewById(R.id.spinner_nav_cat);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //фильтруем по категории
                String categ = parent.getItemAtPosition(position).toString().toLowerCase();
                loadPosts(categ);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        productLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, PostInfoActivity.class);
                intent.putExtra("name", productList.get(position).productName);
                intent.putExtra("desc", productList.get(position).productDesc);
                intent.putExtra("screenshot", productList.get(position).productScreenshot);
                intent.putExtra("upvote", productList.get(position).upvotes);
                intent.putExtra("comments", productList.get(position).productComments);
                intent.putExtra("url", productList.get(position).productUrl);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
                loadPosts(mSpinner.getSelectedItem().toString().toLowerCase());
            }
        }, 1000);
    }

    private void initService() {
        Intent serviceIntent = new Intent(this, MyService.class);
        serviceIntent.putExtra("elementCount", productList.size());
        serviceIntent.putExtra("category", mSpinner.getSelectedItem().toString().toLowerCase());
        startService(serviceIntent);
    }

    public void loadPosts(String category){
        productList.clear();
        Ion.with(getApplicationContext())
                .load("https://api.producthunt.com/v1/categories/"+category+"/posts")
                .setHeader("Authorization", "Bearer " + access_token)
                .setLogging("Ion", Log.DEBUG)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.w("links", result.toString());
                        if (result != null && result.has("posts")) {
                            JsonArray categArrJSON = result.getAsJsonArray("posts");
                            for (int i = 0; i < categArrJSON.size(); i++) {
                                JsonObject obj = categArrJSON.get(i).getAsJsonObject();
                                JsonObject thumbnailObj = obj.getAsJsonObject("thumbnail");
                                JsonObject screenshotObj = obj.getAsJsonObject("screenshot_url");

                                int id = obj.get("id").getAsInt();
                                //если пост с таким id уже есть в списке, то не добавляем
                                if (!containsId(productList, id)) {
                                    productList.add(new Product(obj.get("name").toString().replace("\"", ""),
                                            obj.get("tagline").toString().replace("\"", ""),
                                            thumbnailObj.get("image_url").toString().replace("\"", ""),
                                            screenshotObj.get("300px").toString().replace("\"", ""),
                                            obj.get("discussion_url").toString().replace("\"", ""),
                                            obj.get("comments_count").getAsInt(),
                                            obj.get("category_id").getAsInt(),
                                            id,
                                            obj.get("votes_count").getAsInt()));
                                }
                            }
                        }
                        initListAdapter();
                        initService();
                    }
                });
    }
}
