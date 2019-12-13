package com.example.newsgateway;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    static final String ACTION_MSG_TO_SERVICE = "1";
    static final String ACTION_NEWS_STORY = "2";
    static final String ARTICLE_LIST = "3";
    static final String SOURCE_ID = "4";


    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle toggle;
    private boolean serviceRunning = false;

    private ArrayList<String> srcList = new ArrayList <>();
    private ArrayList<String> catList = new ArrayList <>();
    private ArrayList<Source> sourceArrayList = new ArrayList <>();
    private ArrayList<Articles> articleArrayList = new ArrayList <>();
    private HashMap<String, Source> sourceDataMap = new HashMap<>();
    private Menu opt_menu;
    private NewsReceiver newsReceiver;
    private String currentNewsSource;
    private ColorAdapter colorAdapter;
    private PageAdapter pageAdapter;
    private List <Fragment> fragments;
    private ViewPager pager;
    private boolean stateFlag;
    private int currentSourcePointer;
    ArrayList<ContentDrawer> contentDrawers = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        if(!serviceRunning &&  savedInstanceState == null) {
            Intent intent = new Intent(MainActivity.this, Service.class);
            startService(intent);
            serviceRunning = true;
        }

        newsReceiver = new NewsReceiver();
        IntentFilter filter = new IntentFilter(MainActivity.ACTION_NEWS_STORY);
        registerReceiver(newsReceiver, filter);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.left_drawer);


        drawerList.setOnItemClickListener(
                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectItem(position);
                    }
                }
        );

        toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.string.drawer_open,
                R.string.drawer_close
        );

        colorAdapter = new ColorAdapter(this,contentDrawers);
        drawerList.setAdapter(colorAdapter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        fragments = new ArrayList<>();

        pageAdapter = new PageAdapter(getSupportFragmentManager());
        pager = findViewById(R.id.viewPager);
        pager.setAdapter(pageAdapter);

        if (sourceDataMap.isEmpty() && savedInstanceState == null )
            new SourceDownloader(this, "").execute();

    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        toggle.onConfigurationChanged(newConfig);
    }



    private void colorMenuOptions(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "business":
                color_menu_items(item,Color.CYAN);
                break;
            case "entertainment":
                color_menu_items(item,Color.YELLOW);
                break;
            case "sports":
                color_menu_items(item,Color.GREEN);
                break;
            case "science":
                color_menu_items(item,Color.CYAN);
                break;
            case "technology":
                color_menu_items(item,Color.MAGENTA);
                break;
            case "general":
                color_menu_items(item,Color.RED);
                break;
            case "health":
                color_menu_items(item,Color.BLUE);

        }
    }

    private void color_menu_items(MenuItem item, int color) {
        SpannableString spannableString = new SpannableString(item.getTitle());
        spannableString.setSpan(new ForegroundColorSpan(color), 0, spannableString.length(), 0);
        item.setTitle(spannableString);
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        toggle.syncState();
    }

    private void selectItem(int position) {
        currentNewsSource = srcList.get(position);
        Intent intent = new Intent(MainActivity.ACTION_MSG_TO_SERVICE);
        intent.putExtra(SOURCE_ID, currentNewsSource);
        sendBroadcast(intent);
        drawerLayout.closeDrawer(drawerList);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        MakeLayout layoutRestore = new MakeLayout();
        layoutRestore.setCategories(catList);
        layoutRestore.setSourceList(sourceArrayList);
        layoutRestore.setCurrentArticle(pager.getCurrentItem());
        layoutRestore.setCurrentSource(currentSourcePointer);
        layoutRestore.setArticleList(articleArrayList);
        outState.putSerializable("state", layoutRestore);
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        MakeLayout layoutRestore1 = (MakeLayout) savedInstanceState.getSerializable("state");
        stateFlag = true;
        articleArrayList = layoutRestore1.getArticleList();
        catList = layoutRestore1.getCategories();
        sourceArrayList = layoutRestore1.getSourceList();
        for(int i=0;i<sourceArrayList.size();i++){
            srcList.add(sourceArrayList.get(i).getsName());
            sourceDataMap.put(sourceArrayList.get(i).getsName(), (Source)sourceArrayList.get(i));
        }
        drawerList.clearChoices();
        colorAdapter.notifyDataSetChanged();
        drawerList.setOnItemClickListener(

                new ListView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        pager.setBackgroundResource(0);
                        currentSourcePointer = position;
                        selectItem(position);

                    }
                }
        );
        setTitle("News Gateway");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_item, menu);
        opt_menu=menu;
        if(stateFlag){
            opt_menu.add("All");
            for (String s : catList)
                opt_menu.add(s);
        }
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        if (toggle.onOptionsItemSelected(item)) {
            return true;
        }

        new SourceDownloader(this, item.getTitle().toString()).execute();
        colorMenuOptions(item);
        drawerLayout.openDrawer(drawerList);
        return super.onOptionsItemSelected(item);
    }

    public void initialiseSource(ArrayList<Source> sourceList, ArrayList<String> categoryList)
    {
        sourceDataMap.clear();
        contentDrawers.clear();
        srcList.clear();
        sourceArrayList.clear();
        sourceArrayList.addAll(sourceList);

        for(int i=0;i<sourceList.size();i++){
            srcList.add(sourceList.get(i).getsName());
            sourceDataMap.put(sourceList.get(i).getsName(), (Source)sourceList.get(i));
        }

        if(!opt_menu.hasVisibleItems()) {
            catList.clear();
            catList =categoryList;
            opt_menu.add("All");
            Collections.sort(categoryList);
            for (String s : categoryList)
                opt_menu.add(s);
        }
        for( Source s : sourceList){
            ContentDrawer drawerContent = new ContentDrawer();
            switch (s.getsCategory()){
                case "business":
                    drawerContent.setColor(Color.CYAN);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "entertainment":
                    drawerContent.setColor(Color.DKGRAY);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "sports":
                    drawerContent.setColor(Color.GREEN);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "science":
                    drawerContent.setColor(Color.GRAY);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "technology":
                    drawerContent.setColor(Color.LTGRAY);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "general":
                    drawerContent.setColor(Color.RED);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
                    break;
                case "health":
                    drawerContent.setColor(Color.BLUE);
                    drawerContent.setName(s.getsName());
                    contentDrawers.add(drawerContent);
            }
        }
        colorAdapter.notifyDataSetChanged();

    }


    private void doFragments(ArrayList<Articles> articles) {

        setTitle(currentNewsSource);
        for (int i = 0; i < pageAdapter.getCount(); i++)
            pageAdapter.notifyChangeInPosition(i);

        fragments.clear();

        for (int i = 0; i < articles.size(); i++) {
            Articles a = articles.get(i);

            fragments.add(Fragments.newFragment(articles.get(i), i, articles.size()));
        }

        pageAdapter.notifyDataSetChanged();
        pager.setCurrentItem(0);
        articleArrayList = articles;
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(newsReceiver);
        Intent intent = new Intent(MainActivity.this, NewsReceiver.class);
        stopService(intent);
        super.onDestroy();
    }






    private class PageAdapter extends FragmentPagerAdapter {
        private long baseId = 0;


        public PageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public long getItemId(int position) {
            // give an ID different from position when position has been changed
            return baseId + position;
        }

        public void notifyChangeInPosition(int n) {
            baseId += getCount() + n;
        }


    }

    class NewsReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case ACTION_NEWS_STORY:
                    ArrayList<Articles> artList;
                    if (intent.hasExtra(ARTICLE_LIST)) {
                        artList = (ArrayList <Articles>) intent.getSerializableExtra(ARTICLE_LIST);
                        doFragments(artList);
                    }
                    break;
            }
        }
    }



}


