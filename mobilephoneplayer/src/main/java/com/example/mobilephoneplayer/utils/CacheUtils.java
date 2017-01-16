package com.example.mobilephoneplayer.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.mobilephoneplayer.service.MusicPlayerService;

/**
 * Created by WZ on 2017/1/12.
 */

public class CacheUtils {
    /**
     * 得到缓存的文本数据
     * @param mContext
     * @param key
     * @return
     */
    public static String getString(Context mContext, String key) {
        SharedPreferences sp=mContext.getSharedPreferences("WZ",Context.MODE_PRIVATE);

        return sp.getString(key,null);
    }

    /**
     * 保存数据
     * @param mContext
     * @param key
     * @param values
     */
    public static void putString(Context mContext, String key, String values) {
        SharedPreferences sp=mContext.getSharedPreferences("WZ",Context.MODE_PRIVATE);
        //sp.edit().putString(key,values).commit();
        SharedPreferences.Editor editor=sp.edit();
        editor.putString(key,values);
        editor.commit();

    }

    /**
     * 保存播放模式
     * @param context
     * @param key
     * @param values
     */
    public static void setPlayMode(Context context, String key, int values) {
        SharedPreferences sp=context.getSharedPreferences("WZ",Context.MODE_PRIVATE);
        sp.edit().putInt(key,values).commit();
    }

    /**
     * 得到保存的播放模式
     * @param context
     * @param key
     * @return
     */
    public static int getPlayMode(Context context, String key) {
        SharedPreferences sp=context.getSharedPreferences("WZ",Context.MODE_PRIVATE);
        return sp.getInt(key, MusicPlayerService.REPEAT_NOMAL);
    }
}
