package com.example.mobilephoneplayer.fragment;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mobilephoneplayer.Adapter.LocalVideoAdapter;
import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.activity.SystemAudioPlayerActivity;
import com.example.mobilephoneplayer.basefragment.BaseFragment;
import com.example.mobilephoneplayer.bean.MediaIterm;

import java.util.ArrayList;

/**
 * Created by WZ on 2017/1/6.
 */

public class LocalAudioFragment extends BaseFragment {
    private TextView tv_no_media;
    private ListView local_video_listview;
    private ArrayList<MediaIterm> mediaIterms;
    private LocalVideoAdapter adapter;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            //设置设配器
            if (mediaIterms != null && mediaIterms.size() > 0) {
                //有数据，隐藏文本
                tv_no_media.setVisibility(View.GONE);
                adapter = new LocalVideoAdapter(mContext, mediaIterms,false);
                local_video_listview.setAdapter(adapter);
            } else {
                //没有数据，显示文本
                tv_no_media.setText("没有本地音频...");
                tv_no_media.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public View intiView() {
        View view = View.inflate(mContext, R.layout.fragment_local_video, null);
        tv_no_media = (TextView) view.findViewById(R.id.tv_no_media);
        local_video_listview = (ListView) view.findViewById(R.id.local_video_listview);
        //设置iterm的监听
        local_video_listview.setOnItemClickListener(new LocalAudioFragment.MyOnItemClickListener());

        return view;
    }

    class MyOnItemClickListener implements AdapterView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(mContext, SystemAudioPlayerActivity.class);
            //传递点击的位置
            intent.putExtra("position", i);
            startActivity(intent);
        }
    }


    @Override
    protected void initData() {
        super.initData();
        //在子线程中加载视频
        getDataFromLcoal();

    }

    /**
     * 子线程中得到音频
     */
    private void getDataFromLcoal() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                //初始化数据集合
                mediaIterms = new ArrayList<MediaIterm>();
                ContentResolver resolver = mContext.getContentResolver();
                //sdcard的视频路径
                Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Audio.Media.DISPLAY_NAME, //视频名字
                        MediaStore.Audio.Media.DURATION, //视频时长，毫秒
                        MediaStore.Audio.Media.SIZE, //文件大小，byte
                        MediaStore.Audio.Media.DATA, //播放路径
                        MediaStore.Audio.Media.ARTIST //艺术家
                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        MediaIterm mediaIterm = new MediaIterm();
                        //添加到集合中
                        mediaIterms.add(mediaIterm);
                        String name = cursor.getString(0);
                        mediaIterm.setName(name);
                        long duration = cursor.getLong(1);
                        mediaIterm.setDuration(duration);
                        long size = cursor.getLong(2);
                        mediaIterm.setSize(size);
                        String data = cursor.getString(3);
                        mediaIterm.setData(data);
                        String artist = cursor.getString(4);
                        mediaIterm.setArtist(artist);

                    }
                    cursor.close();
                }
                //发消息，切换到主线程
                handler.sendEmptyMessage(2);
            }
        }.start();
    }

    @Override
    protected void flushData() {
        super.flushData();
    }
}
