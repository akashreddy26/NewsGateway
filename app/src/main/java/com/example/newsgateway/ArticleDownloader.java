package com.example.newsgateway;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class ArticleDownloader extends AsyncTask<String, Integer, String> {

    private static final String TAG = "Article Downloading";

    private String sourceId;
    private Service service;
    private String API_KEY ="d2306d7c21ae4136aa2089bd7ec619f1";
    private String query1 ="https://newsapi.org/v2/everything?sources=";
    private String query2 = "&apiKey="+API_KEY;
    private Uri.Builder buildURL = null;
    private StringBuilder sb1;
    private boolean noDataFound=false;
    boolean isNoDataFound =true;
    private ArrayList<Articles> articleArrayList = new ArrayList <>();

    public ArticleDownloader(Service service, String sourceId){
        this.sourceId = sourceId;
        this.service= service;

    }

    @Override
    protected String doInBackground(String... strings) {
        String query ="";

        query = query1 +sourceId+ query2;
        buildURL = Uri.parse(query).buildUpon();
        connectToAPI();
        if(!isNoDataFound) {
            parseJSON(sb1.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        service.setArticles(articleArrayList);
    }


    public void connectToAPI() {

        String urlToUse = buildURL.build().toString();
        sb1 = new StringBuilder();
        try {
            URL url = new URL(urlToUse);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(conn.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND)
            {
                noDataFound=true;
            }
            else {
                conn.setRequestMethod("GET");
                InputStream is = conn.getInputStream();
                BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

                String line=null;
                while ((line = reader.readLine()) != null) {
                    sb1.append(line).append('\n');
                }
                isNoDataFound=false;

            }
        }
        catch(FileNotFoundException fe){
            Log.d(TAG, "FileNotFoundException ");
        }
        catch (Exception e) {
            Log.d(TAG, "Exception doInBackground: " + e.getMessage());
        }
    }


    private void parseJSON(String s) {
        try{
            if(!noDataFound){
                JSONObject jObjMain = new JSONObject(s);
                JSONArray articles = jObjMain.getJSONArray("articles");
                for(int i=0;i<articles.length();i++){
                    JSONObject art = (JSONObject) articles.get(i);
                    Articles artObj = new Articles();
                    artObj.setAuthor(art.getString("author"));
                    artObj.setDescription(art.getString("description"));
                    artObj.setPublishedAt(art.getString("publishedAt"));
                    artObj.setTitle(art.getString("title"));
                    artObj.setUrlToImage(art.getString("urlToImage"));
                    artObj.setArticleUrl(art.getString("url"));
                    articleArrayList.add(artObj);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}

