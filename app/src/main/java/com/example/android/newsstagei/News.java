package com.example.android.newsstagei;

import android.graphics.Bitmap;

public class News {

    private String mUrl;
    private String mWebTitle;
    private String mSectionName;
    private String mWebPublicationDate;
    private String mContributor;
    private Bitmap mBitmap;
    private Boolean mNextIsBigger;

    public News(String url, String webTitle, String sectionName, String webPublicationDate, String contributor, Bitmap bitmap, Boolean next) {
        mUrl = url;
        mWebTitle = webTitle;
        mSectionName = sectionName;
        mWebPublicationDate = webPublicationDate;
        mContributor = contributor;
        mBitmap = bitmap;
        mNextIsBigger = next;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getWebTitle() {
        return mWebTitle;
    }

    public String getSectionName() {
        return mSectionName;
    }

    public String getWebPublicationDate() {
        return mWebPublicationDate;
    }

    public String getContributor() {
        return mContributor;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public Boolean getNextIsBigger() {
        return mNextIsBigger;
    }

}
