package com.gold.kds517.funmedia_new.models;

import java.io.Serializable;

/**
 * Created by RST on 2/25/2017.
 */

public class SeasonModel implements Serializable {
    int total;
    String id, url, ext;

    public SeasonModel(int total, String url, String ext) {
        this.total = total;
        this.url = url;
        this.ext = ext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }
}
