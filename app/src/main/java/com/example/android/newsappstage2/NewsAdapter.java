package com.example.android.newsappstage2;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewsAdapter extends ArrayAdapter<News> {

    public NewsAdapter(Context context, ArrayList<News> news) {
        super(context, 0, news);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.activity_main, parent, false);
        }

        // Get the {@link NewsClass} object located at this position in the list
        News currentNewsClass = getItem(position);

        // Find the TextView with News Title in the activity_main.xml layout with the ID category
        TextView newsTitleTextView = listItemView.findViewById(R.id.newsTitle);

        // Display the title of the current news in that TextView
        assert currentNewsClass != null;
        newsTitleTextView.setText(currentNewsClass.getNewsTitle());

        // Find the TextView with News Category in the activity_main.xml layout with the ID category
        TextView newsCategoryTextView = listItemView.findViewById(R.id.newsCategory);

        // Display the category of the current news in that TextView
        newsCategoryTextView.setText(currentNewsClass.getNewsCategory());

        // Find the TextView with News Author in the activity_main.xml layout with the ID category
        TextView newsAuthorTextView = listItemView.findViewById(R.id.newsAuthor);

        // Display the category of the current news in that TextView
        newsAuthorTextView.setText(currentNewsClass.getNewsAuthor());

        // Find the TextView with News Date in the activity_main.xml layout with the ID category
        TextView newsDateTextView = listItemView.findViewById(R.id.newsDate);

        // Display the category of the current news in that TextView
        SimpleDateFormat dateFormatJSON = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("EE dd MMM yyyy");

        try {
            Date dateNews = dateFormatJSON.parse(currentNewsClass.getNewsDate());

            String date = dateFormat2.format(dateNews);
            newsDateTextView.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Return the whole list item layout (containing ! TextView and an ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }
}
