package com.example.mobilephoneplayer.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.example.mobilephoneplayer.IMusicPlayerService;
import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.activity.SystemAudioPlayerActivity;
import com.example.mobilephoneplayer.bean.MediaIterm;
import com.example.mobilephoneplayer.utils.CacheUtils;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by WZ on 2017/1/13.
 */

public class MusicPlayerService extends Service {
    public static final String OPEN_COMPLETE = "open_complete";
    /**
     * AIDL生成的类
     */
    IMusicPlayerService.Stub stub = new IMusicPlayerService.Stub() {
        MusicPlayerService service = MusicPlayerService.this;

        @Override
        public void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat, double aDouble, String aString) throws RemoteException {

        }

        @Override
        public void openAudio(int position) throws RemoteException {
            service.openAudio(position);
        }

        @Override
        public void start() throws RemoteException {
            service.start();
        }

        @Override
        public void pause() throws RemoteException {
            service.pause();
        }

        @Override
        public void stop() throws RemoteException {

        }

        @Override
        public String getAudioName() throws RemoteException {
            return service.getAudioName();
        }

        @Override
        public String getArtistName() throws RemoteException {
            return service.getArtistName();
        }

        @Override
        public int getCurrentPosition() throws RemoteException {
            return service.getCurrentPosition();
        }

        @Override
        public int getDuration() throws RemoteException {
            return service.getDuration();
        }

        @Override
        public void next() throws RemoteException {
            service.next();
        }

        @Override
        public void pre() throws RemoteException {
            service.pre();
        }

        @Override
        public int getPlayMode() throws RemoteException {
            return service.getPlayMode();
        }

        @Override
        public void setPlayMode(int mode) throws RemoteException {
            service.setPlayMode(mode);
        }

        @Override
        public boolean isPlaying() throws RemoteException {
            return mediaPlayer.isPlaying();
        }

        @Override
        public void seekTo(int progress) throws RemoteException {
            mediaPlayer.seekTo(progress);
        }
    };
    private ArrayList<MediaIterm> mediaIterms;
    /**
     * 音频是否加载完成
     */
    private boolean isLoaded = false;
    private MediaIterm mediaIterm;
    private int position;
    /**
     * 播放器
     */
    private MediaPlayer mediaPlayer;
    private NotificationManager nm;
    /**
     * 顺序播放
     */
    public static final int REPEAT_NOMAL = 1;
    /**
     * 单曲播放
     */
    public static final int REPEAT_SINGLE = 2;
    /**
     * 循环播放
     */
    public static final int REPEAT_ALL = 3;
    private int playmode = REPEAT_NOMAL;
    private boolean isNext=false;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return stub;
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
                ContentResolver resolver = getContentResolver();
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
                //音频加载完成
                isLoaded = true;
            }
        }.start();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        playmode = CacheUtils.getPlayMode(this, "playmode");
        getDataFromLcoal();
    }

    /**
     * 根据位置打开一个音频并且播放
     *
     * @param position
     */
    void openAudio(int position) {
        if (mediaIterms != null && mediaIterms.size() > 0) {
            mediaIterm = mediaIterms.get(position);
            this.position = position;
            //MediaPlayer
            if (mediaPlayer != null) {
                mediaPlayer.reset();//上一曲重置
                mediaPlayer = null;
            }
            mediaPlayer = new MediaPlayer();
            //设置三个监听
            mediaPlayer.setOnPreparedListener(new MyOnPreparedListener());
            mediaPlayer.setOnCompletionListener(new MyCompletionListener());
            mediaPlayer.setOnErrorListener(new MyOnOnErrorListener());
            //设置地址
            try {
                mediaPlayer.setDataSource(mediaIterm.getData());
                mediaPlayer.prepareAsync();
                isNext=false;
            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (!isLoaded) {
            Toast.makeText(this, "没有加载完成", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * 开始播放音频
     */
    void start() {
        mediaPlayer.start();
        //创建状态栏通知
        nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        Intent intent = new Intent(this, SystemAudioPlayerActivity.class);
        intent.putExtra("notification", true);//表示来自状态栏
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            notification = new Notification.Builder(this)
                    .setSmallIcon(R.drawable.notification_music_playing)
                    .setContentTitle("321音乐")
                    .setContentText("正在播放" + getAudioName())
                    .setContentIntent(pendingIntent)
                    .build();
            //点击后还存在属性
            notification.flags = Notification.FLAG_ONGOING_EVENT;
        }
        nm.notify(1, notification);
    }

    /**
     * 暂停
     */
    void pause() {
        mediaPlayer.pause();
        //移除状态栏的通知
        nm.cancel(1);
    }

    /**
     * 停止
     */
    void stop() {
    }

    /**
     * 得到歌曲的名称
     */
    String getAudioName() {
        if (mediaIterm != null) {
            return mediaIterm.getName();
        }
        return "";
    }

    /**
     * 得到歌曲演唱者的名字
     */
    String getArtistName() {
        if (mediaIterm != null) {
            return mediaIterm.getArtist();
        }
        return "";
    }

    /**
     * 得到歌曲的当前播放进度
     */
    int getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    /**
     * 得到歌曲的当前总进度
     */
    int getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    /**
     * 播放下一首歌曲
     */
    void next() {
        //设置下一曲对应的位置
        setNextPosition();
        //根据对应的位置去播放
        openNextAudio();
    }

    private void openNextAudio() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            if (position<=mediaIterms.size()-1){
                openAudio(position);
            }else {
                position = mediaIterms.size() - 1;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position<=mediaIterms.size()-1){
                openAudio(position);
            }else {
                position = mediaIterms.size() - 1;
            }
        }
    }

    private void setNextPosition() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            position++;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            if (!isNext){
                isNext=false;
                position++;
                if (position > mediaIterms.size() - 1) {
                    position = 0;
                }
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position++;
            if (position > mediaIterms.size() - 1) {
                position = 0;
            }
        } else {
            position++;
        }
    }

    /**
     * 播放上一首歌曲
     */
    void pre() {
        //设置上一曲对应的位置
        setPretPosition();
        //根据对应的位置去播放
        openPreAudio();
    }

    private void openPreAudio() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            if (position>=0){
                openAudio(position);
            }else {
                position = 0;
            }
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            openAudio(position);

        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            openAudio(position);
        } else {
            if (position>=0){
                openAudio(position);
            }else {
                position = 0;
            }
        }
    }

    private void setPretPosition() {
        int playmode = getPlayMode();
        if (playmode == MusicPlayerService.REPEAT_NOMAL) {
            position--;
        } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
            if (!isNext){
                isNext=false;
                position--;
                if (position < 0) {
                    position = mediaIterms.size() - 1;
                }
            }
        } else if (playmode == MusicPlayerService.REPEAT_ALL) {
            position--;
            if (position < 0) {
                position =  mediaIterms.size() - 1;
            }
        } else {
            position--;
        }
    }

    /**
     * 得到播放模式
     */
    int getPlayMode() {
        return playmode;
    }

    /**
     * 设置播放模式
     */
    void setPlayMode(int mode) {
        this.playmode = mode;
        CacheUtils.setPlayMode(this, "playmode", mode);
    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            notifyChange(OPEN_COMPLETE);
            start();
        }
    }

    private void notifyChange(String action) {
        Intent intent = new Intent();
        intent.setAction(action);
        //发广播
        sendBroadcast(intent);
    }

    private class MyCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            isNext=true;
            next();
        }
    }

    private class MyOnOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            next();
            return true;
        }
    }
}
