package com.example.android.newsstagei;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

public class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    private static final int SUCCESS_CODE = 200;
    static String contributor;

    private QueryUtils() {
    }

    /**
     * Query the given dataset and return a list of objects.
     */
    public static List<News> fetchNewsData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of objects
        List<News> newsListExtracted = extractFeatureFromJson(jsonResponse);

        // Return the list of objects
        return newsListExtracted;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
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
            if (urlConnection.getResponseCode() == SUCCESS_CODE) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
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
     * Return a list of objects that has been built up from
     * parsing the given JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding objects to
        List<News> newsListExtracted = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsJSON);

            JSONObject response = baseJsonResponse.getJSONObject("response");
            JSONArray newsArray = response.getJSONArray("results");

            // For each news in the newsArray, create an object
            for (int i = 0; i <= newsArray.length(); i++) {

                // Get a single object at position i within the list of objects
                JSONObject currentNews = newsArray.getJSONObject(i);

                String webTitle = currentNews.getString("webTitle");

                String sectionName = currentNews.getString("sectionName");
                String url = currentNews.getString("webUrl");
                String webPublicationDate = currentNews.getString("webPublicationDate");

                String thumbnail;
                try {
                    JSONObject fields = currentNews.getJSONObject("fields");
                    thumbnail = fields.getString("thumbnail");
                } catch (JSONException e) {
                    Log.e("QueryUtils", "Problem parsing the news JSON results", e);
                    thumbnail = null;
                }

                Bitmap bmp = null;
                try {
                    InputStream in = new java.net.URL(thumbnail).openStream();
                    bmp = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.e("Error loading thumbnail", e.getMessage());
                    e.printStackTrace();
                }

                JSONArray tags = currentNews.getJSONArray("tags");

                if (tags != null && tags.length() > 0) {
                    JSONObject contributorJSON = tags.getJSONObject(0);
                    contributor = contributorJSON.getString("webTitle");
                } else

                {
                    contributor = null;
                }

                // Boolean which will be used to randomly set different cell background
                Boolean isNextHeadlineBigger = false;

                try {
                    String nextWebTitle = newsArray.getJSONObject(i + 1).getString("webTitle");
                    Log.i("Next: ", nextWebTitle);

                    if (webTitle.length() <= nextWebTitle.length()) {
                        isNextHeadlineBigger = true;
                    } else {
                        isNextHeadlineBigger = false;
                    }
                } catch (JSONException e) {
                    Log.e("QueryUtils", "Problem parsing the news JSON results", e);
                }

                News news = new News(url, webTitle, sectionName, webPublicationDate, contributor, bmp, isNextHeadlineBigger);
                newsListExtracted.add(news);

            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }

        return newsListExtracted;
    }

}
