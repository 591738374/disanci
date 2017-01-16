package com.example.mobilephoneplayer.fragment;

import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.basefragment.BaseFragment;

import butterknife.ButterKnife;

/**
 * Created by WZ on 2017/1/6.
 */

public class NetAudioFragment extends BaseFragment {

    private static final String TAG=NetAudioFragment.class.getSimpleName();

    private TextView textView;
    @Override
    public View intiView() {
        textView=new TextView(mContext);

        View view=View.inflate(mContext, R.layout.fragment_net_audio,null);
        ButterKnife.bind(this,view);
        return view;


    }


    @Override
    protected void initData() {
        super.initData();
        Log.e(TAG,"网络音频数据初始化了");
    }



    @Nullable
    private int i=0;


    @Override
    protected void flushData() {
        super.flushData();
        textView.setText("网络音乐刷新数据"+i++);

    }



}
