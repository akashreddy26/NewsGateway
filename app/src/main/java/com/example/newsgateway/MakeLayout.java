package com.example.newsgateway;

import java.io.Serializable;
import java.util.ArrayList;

public class MakeLayout implements Serializable {


    private ArrayList<Source> sourceList = new ArrayList<Source>();
    private ArrayList<Articles> articleList = new ArrayList <Articles>();
    private ArrayList<String> categories = new ArrayList <String>();
    private int currentSource;
    private int currentArticle;

    public ArrayList <Source> getSourceList() {
        return sourceList;
    }

    public void setSourceList(ArrayList <Source> sourceList) {
        this.sourceList = sourceList;
    }

    public ArrayList <Articles> getArticleList() {
        return articleList;
    }

    public void setArticleList(ArrayList <Articles> articleList) {
        this.articleList = articleList;
    }

    public ArrayList <String> getCategories() {
        return categories;
    }

    public void setCategories(ArrayList <String> categories) {
        this.categories = categories;
    }

    public int getCurrentSource() {
        return currentSource;
    }

    public void setCurrentSource(int currentSource) {
        this.currentSource = currentSource;
    }

    public int getCurrentArticle() {
        return currentArticle;
    }

    public void setCurrentArticle(int currentArticle) {
        this.currentArticle = currentArticle;
    }
}

