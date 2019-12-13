package com.example.newsgateway;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Fragments extends Fragment {

    private static final String TAG = "Article Fragments";

    public static final String ARTICLE = "ARTICLE";
    public static final String INDEX = "INDEX";
    public static final String TOTAL = "TOTAL";

    TextView title;
    TextView date;
    TextView author;
    TextView content;
    ImageView photo;
    TextView count;
    Articles articles;
    int counter;
    String imageURL;
    View v;

    public static final Fragments newFragment(Articles article, int index, int total)
    {
        Fragments fragments = new Fragments();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ARTICLE, article);
        bundle.putInt(INDEX, index);
        bundle.putInt(TOTAL, total);

        fragments.setArguments(bundle);
        return fragments;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        articles = (Articles) getArguments().getSerializable(ARTICLE);
        counter = getArguments().getInt(INDEX)+1;
        int total = getArguments().getInt(TOTAL);
        String endLine = counter +" of "+total;


        v = inflater.inflate(R.layout.fragment, container, false);
        title = (TextView)v.findViewById(R.id.headline);
        date = (TextView) v.findViewById(R.id.date);
        author = (TextView) v.findViewById(R.id.author);
        content = (TextView) v.findViewById(R.id.content);
        count = (TextView) v.findViewById(R.id.index);
        photo = (ImageView) v.findViewById(R.id.photo);

        count.setText(endLine);
        if(articles.getTitle() != null){ title.setText(articles.getTitle());
        }
        else{
            title.setText("");}

        if(articles.getPublishedAt() !=null && !articles.getPublishedAt().isEmpty()) {

            String sDate1 = articles.getPublishedAt();

            Date date1 = null;
            String publisheddate = "";
            try {
                if(sDate1 != null){

                    date1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(sDate1);}
                String pattern = "MMM dd, yyyy HH:mm";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                publisheddate = simpleDateFormat.format(date1);
                date.setText(publisheddate);
            } catch (ParseException e) {
            }
        }
        if(articles.getDescription()!=null) {author.setText(articles.getDescription());}
        else{author.setText("");}

        if(articles.getAuthor() != null) {content.setText(articles.getAuthor());}
        else{content.setText("");}

        author.setMovementMethod(new ScrollingMovementMethod());

        if(articles.getUrlToImage()!=null){

             imageURL = articles.getUrlToImage();

            if (imageURL != null) {
                Picasso picasso = new Picasso.Builder(getActivity()).listener(new Picasso.Listener() {
                    @Override
                    public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {

                        final String changedUrl = imageURL.replace("http:", "https:");
                        picasso.load(changedUrl)
                                .fit()
                                .centerCrop()
                                .error(R.drawable.brokenimage)
                                .placeholder(R.drawable.placeholder)
                                .into(photo);
                    }
                }).build();
                picasso.load(imageURL)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.placeholder)
                        .into(photo);
            } else {
                Picasso.with(getActivity()).load(imageURL)
                        .fit()
                        .centerCrop()
                        .error(R.drawable.brokenimage)
                        .placeholder(R.drawable.missingimage)
                        .into(photo);
            }



        }

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(articles.getArticleUrl()));
                startActivity(intent);
            }
        });

        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(articles.getArticleUrl()));
                startActivity(intent);
            }
        });

        author.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(articles.getArticleUrl()));
                startActivity(intent);
            }
        });


        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(articles.getArticleUrl()));
                startActivity(intent);
            }
        });

        return v;
    }



}

