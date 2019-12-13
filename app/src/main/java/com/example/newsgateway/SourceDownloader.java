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

public class SourceDownloader extends AsyncTask<String, Integer, String> {

    private static final String TAG = "SourceDownloader";
    private StringBuilder sb1;
    private boolean noDataFound=false;
    boolean dataNotFound =true;
    private MainActivity mainActivity;
    private String category;
    private Uri.Builder buildURL = null;
    private ArrayList<Source> sourceList = new ArrayList <>();
    private ArrayList<String> categoryList = new ArrayList <>();
    private String API_KEY ="d2306d7c21ae4136aa2089bd7ec619f1";
    private String NewsAPI;

    public SourceDownloader(MainActivity ma, String category){
        mainActivity = ma;
        if(category.equalsIgnoreCase("all") || category.equalsIgnoreCase("")) {
            this.category = "";
            NewsAPI ="https://newsapi.org/v2/sources?language=en&country=us&apiKey="+API_KEY;
        }
        else
        {
            String QUERY1= "https://newsapi.org/v2/sources?language=en&country=us&category=";
            String QUERY2 ="&apiKey="+API_KEY;
            NewsAPI = QUERY1+category+QUERY2;
            this.category = category;
        }

    }



    @Override
    protected String doInBackground(String... strings) {

        buildURL = Uri.parse(NewsAPI).buildUpon();
        connectToAPI();
        if(!dataNotFound) {
            parseJSON(sb1.toString());
        }
        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        for(int j=0;j<sourceList.size();j++)
        {
            String temp = sourceList.get(j).getsCategory();
            if(!categoryList.contains(temp))
                categoryList.add(temp);
        }
        mainActivity.initialiseSource(sourceList,categoryList);
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
                dataNotFound =false;

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
                JSONArray sources = jObjMain.getJSONArray("sources");
                for(int i=0;i<sources.length();i++){
                    JSONObject src = (JSONObject) sources.get(i);
                    Source srcObj = new Source();
                    srcObj.setsId(src.getString("id"));
                    srcObj.setsCategory(src.getString("category"));
                    srcObj.setsName(src.getString("name"));
                    srcObj.setsUrl(src.getString("url"));
                    sourceList.add(srcObj);
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
