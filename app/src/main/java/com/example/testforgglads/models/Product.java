package com.example.testforgglads.models;

/**
 * Created by Виктор on 26.01.2017.
 */

public class Product {
    public String productName;
    public String productDesc;
    public String productThumbnail;
    public String productScreenshot;
    public String productUrl;
    public int productComments;
    public int categoryID;
    public int postID;
    public int upvotes;

    public Product(String productName, String productDesc, String productThumbnail, String productScreenshot, String productUrl,
                   int productComments, int categoryID, int postID, int upvotes) {
        this.productName = productName;
        this.productDesc = productDesc;
        this.productThumbnail = productThumbnail;
        this.productScreenshot = productScreenshot;
        this.productUrl = productUrl;
        this.productComments = productComments;
        this.categoryID = categoryID;
        this.postID = postID;
        this.upvotes = upvotes;
    }

    public int getPostID() {
        return postID;
    }
}
