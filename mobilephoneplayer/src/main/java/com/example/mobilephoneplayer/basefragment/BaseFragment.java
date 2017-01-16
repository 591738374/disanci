package com.example.mobilephoneplayer.basefragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by WZ on 2017/1/6.
 */

public abstract class BaseFragment extends Fragment {

    //上下文
    public Context mContext;

    /**
     * 当系统创建当前BaseFragment类的时候回调
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return intiView();
    }

    /**
     * 抽象方法，孩子必须实现
     *
     * @return
     */
    public abstract View intiView();

    /**
     * 当Activty创建成功的时候回调该方法
     * 初始化数据：
     * 联网请求数据
     * 绑定数据
     *
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    /**
     * 当子类需要：
     * 1.联网请求网络，的时候重写该方法
     * 2.绑定数据
     */
    protected void initData() {
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden){
            //当不隐藏时刷新数据
            flushData();
        }
    }
    //不创建碎片就能刷新数据
    protected void flushData(){

    };

}
