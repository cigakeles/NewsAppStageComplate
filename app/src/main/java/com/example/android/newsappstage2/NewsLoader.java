package com.example.android.newsappstage2;


import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    private String mUrl;

    /**
     * New NewsLoader.
     *
     * @param context of the activity
     * @param url     url for
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // Background thread.

    @Override
    public List<News> loadInBackground() {
        // Don't perform the request if the first URL is null.
        if (mUrl == null) {
            return null;
        }
        // Perform the network request, parse the response, and extract a list of earthquakes.
        List<News> News = NewsUtils.fetchNewsData(mUrl);
        return News;
    }
}