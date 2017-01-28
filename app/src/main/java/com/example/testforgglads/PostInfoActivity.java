package com.example.testforgglads;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

public class PostInfoActivity extends AppCompatActivity {
    private TextView descTV, upvoteTV, commentsTV;
    private ImageView screenshotIV;
    private Button buttGetIt;
    private Toolbar toolbar;
    private String name, desc, screenshot, productUrl;
    private int upvote,comments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_info);
        bindViews();
        setSupportActionBar(toolbar);
        getExtraData();
        //add name of post to toolbar
        getSupportActionBar().setTitle(name);
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        initViews();
    }

    private void getExtraData() {
        Intent intent = getIntent();

        name = intent.getStringExtra("name");
        desc = intent.getStringExtra("desc");
        screenshot = intent.getStringExtra("screenshot");
        productUrl = intent.getStringExtra("url");
        upvote = intent.getIntExtra("upvote", 0);
        comments = intent.getIntExtra("comments", 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void bindViews() {
        descTV = (TextView) findViewById(R.id.desc_prod_info_tv);
        upvoteTV = (TextView) findViewById(R.id.like_tv);
        commentsTV = (TextView) findViewById(R.id.comment_tv);
        screenshotIV = (ImageView) findViewById(R.id.screenshot_prod_iv);
        buttGetIt = (Button) findViewById(R.id.butt_getit);
        toolbar = (Toolbar) findViewById(R.id.my_toolbar);
    }

    private void initViews() {
        descTV.setText(desc);
        Glide.with(this).load(screenshot).into(screenshotIV);
        upvoteTV.setText(String.valueOf(upvote));
        commentsTV.setText(String.valueOf(comments));

        buttGetIt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(productUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

}
