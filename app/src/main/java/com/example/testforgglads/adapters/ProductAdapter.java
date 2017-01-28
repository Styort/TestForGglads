package com.example.testforgglads.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.testforgglads.R;
import com.example.testforgglads.ViewHolder;
import com.example.testforgglads.models.Product;

import java.util.List;

/**
 * Created by Виктор on 26.01.2017.
 */

public class ProductAdapter extends ArrayAdapter{
    Activity context;
    List<Product> productList;
    public ProductAdapter(Context context, int resource, List objects) {
        super(context, resource, objects);
        this.context = (Activity) context;
        this.productList = objects;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = context.getLayoutInflater();
            convertView = inflater.inflate(R.layout.list_item, null);
            viewHolder = new ViewHolder();
            viewHolder.nameTV = (TextView) convertView.findViewById(R.id.name_tv);
            viewHolder.descTV = (TextView) convertView.findViewById(R.id.desc_tv);
            viewHolder.upvoteTV = (TextView) convertView.findViewById(R.id.like_tv);
            viewHolder.commentsTV = (TextView) convertView.findViewById(R.id.comment_tv);
            viewHolder.thumbnailIV = (ImageView) convertView.findViewById(R.id.thumbnail_iv);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String prodName, prodDesc;
        prodName = productList.get(position).productName;
        prodDesc = productList.get(position).productDesc;
        if(prodName.length() > 25 ){
           prodName = prodName.substring(0,25) + "...";
        }
        if(prodDesc.length() > 60 ){
            prodDesc = prodDesc.substring(0,60) + "...";
        }
        viewHolder.nameTV.setText(prodName);
        viewHolder.descTV.setText(prodDesc);
        viewHolder.upvoteTV.setText(String.valueOf(productList.get(position).upvotes));
        viewHolder.commentsTV.setText(String.valueOf(productList.get(position).productComments));
        Glide.with(context).load(productList.get(position).productThumbnail).into(viewHolder.thumbnailIV);

        return convertView;
    }
}
