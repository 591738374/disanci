package com.example.mobilephoneplayer.bean;

import java.io.Serializable;

/**
 * Created by WZ on 2017/1/8.
 */

public class MediaIterm implements Serializable {
    private String name;
    private long duration;
    private long size;
    private String data;//播放地址
    private String artist;//艺术家

    /**
     * 图片路径
     */
    private String imageUrl;

    public String getHeightUrl() {
        return heightUrl;
    }

    public void setHeightUrl(String heightUrl) {
        this.heightUrl = heightUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    /**
     * 描述
     */
    private String desc;

    private String heightUrl;

    public MediaIterm() {
    }

    @Override
    public String toString() {
        return "MediaIterm{" +
                "name='" + name + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                ", artist='" + artist + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", desc='" + desc + '\'' +
                ", heightUrl='" + heightUrl + '\'' +
                '}';
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {

        return name;
    }

    public long getDuration() {
        return duration;
    }

    public long getSize() {
        return size;
    }

    public String getData() {
        return data;
    }

    public String getArtist() {
        return artist;
    }
}
