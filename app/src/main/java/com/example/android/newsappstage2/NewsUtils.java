package com.example.android.newsappstage2;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class NewsUtils {


    /**
     * Tag for the log messages.
     */
    public static final String LOG_TAG = NewsUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link NewsUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private NewsUtils() {
    }

    /**
     * Query the Guardian API and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {

//        // Just to see how the {@link loadingIndicator} works, we will delay the thread for 2 seconds
//        // before it starts.
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back.
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link NewsItem}s.
        List<News> newsList = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link NewsItem}s.
        return newsList;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news stories JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link News} objects that has been built up from
     * parsing a JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding news stories to
        List<News> newsList = new ArrayList<>();

        // Try to parse the news stories JSON result/response. If there's a problem with the way
        // the JSON is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string.
            JSONObject root = new JSONObject(newsJSON);

            // Create a JSONObject from the JSON root object.
            JSONObject response = root.getJSONObject("response");

            // Extract the JSONArray associated with the key called "results",
            // which represents a list of results (or news stories).
            JSONArray resultsArray = response.getJSONArray("results");

            // For each news item in the resultsArray, create an {@link NewsItem} object.
            for (int i = 0; i < resultsArray.length(); i++) {

                // Get a single news item at position i within the list of news stories.
                JSONObject newsItem = resultsArray.getJSONObject(i);

                // For a given news item, extract the info we need (title, section, author, date, webUrl).
                String title = newsItem.getString("webTitle");
                String section = newsItem.getString("sectionName");
                String date = newsItem.getString("webPublicationDate");
                String webUrl = newsItem.getString("webUrl");
                //For the author/contributors, we need to extract the "tags" JSONArray, then the JSONObject,
                //then the "webTitle" String.
                JSONArray tagsArray = newsItem.getJSONArray("tags");
                //Put the author to be null in case if there is no details about it.
                String author = "";
                //We need to check if there is a contributor, and how many they are.
                int numberOfContributors = tagsArray.length();
                if (numberOfContributors > 0) {
                    //Add a comma if the current contributor is not the first one AND not the last one.
                    for (int j = 0; j < numberOfContributors; j++) {
                        if (j > 0 && j < numberOfContributors - 1) {
                            author += ", ";
                        }
                        //Add an "and" if the current contributor is not the first one AND it is the last one.
                        if (j > 0 && j == numberOfContributors - 1) {
                            author += " and ";
                        }
                        //Extract the name of the contributor and add it the the author's string.
                        JSONObject tagItem = tagsArray.getJSONObject(j);
                        author += tagItem.getString("webTitle");
                    }
                }

                // Create a new {@link NewsItem} object with the title, section, author, date,
                // and webUrl from the JSON response, and add it to the list of newsList.
                newsList.add(new News(title, section, author, date, webUrl));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the news stories JSON results", e);
        }

        // Return the list of news stories.
        return newsList;
    }
}