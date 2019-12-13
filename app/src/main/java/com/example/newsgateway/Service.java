package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

public class Service extends android.app.Service {

    private static final String TAG = "Service";
    private boolean isRunning = true;
    private ServiceReceiver serviceReceiver;
    private ArrayList<Articles> articleList = new ArrayList <Articles>();

    public Service() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        serviceReceiver = new ServiceReceiver();
        IntentFilter filter1 = new IntentFilter(MainActivity.ACTION_MSG_TO_SERVICE);
        registerReceiver(serviceReceiver, filter1);

        new Thread(new Runnable() {
            @Override
            public void run() {

                while (isRunning) {
                    while(articleList.isEmpty()){
                        try {
                            Thread.sleep(250);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Intent intent = new Intent();
                    intent.setAction(MainActivity.ACTION_NEWS_STORY);
                    intent.putExtra(MainActivity.ARTICLE_LIST, articleList);
                    sendBroadcast(intent);
                    articleList.clear();
                }
                Log.i(TAG, "Service was properly stopped");
            }
        }).start();

        return android.app.Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        isRunning = false;
        Toast.makeText(this, "Service Stopped", Toast.LENGTH_SHORT).show();
    }

    public void setArticles(ArrayList<Articles> list){
        articleList.clear();
        articleList.addAll(list);

    }

    class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            switch (intent.getAction()) {
                case MainActivity.ACTION_MSG_TO_SERVICE:
                    String sourceId ="";
                    String temp="";
                    if (intent.hasExtra(MainActivity.SOURCE_ID)) {
                        sourceId = intent.getStringExtra(MainActivity.SOURCE_ID);
                        temp=sourceId.replaceAll(" ","-");
                    }

                    new ArticleDownloader(Service.this, temp).execute();
                    break;
            }

        }
    }
}


