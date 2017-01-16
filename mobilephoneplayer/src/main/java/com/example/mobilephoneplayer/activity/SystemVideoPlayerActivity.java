package com.example.mobilephoneplayer.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.bean.MediaIterm;
import com.example.mobilephoneplayer.utils.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.mobilephoneplayer.R.id.videoview;

public class SystemVideoPlayerActivity extends Activity implements View.OnClickListener {
    private LinearLayout llTop;
    private TextView tvName;
    private ImageView ivBattery;
    private TextView tvSystetime;
    private Button btnVoice;
    private SeekBar seekbarVoice;
    private Button btnSwichePlayer;
    private LinearLayout llBottom;
    private TextView tvCurrenttime;
    private SeekBar seekbarVideo;
    private TextView tvDuration;
    private Button btnExit;
    private Button btnPre;
    private Button btnStartPause;
    private Button btnNext;
    private Button btnSwichScreen;
    private Utils utils;
    private MyBroadcastReceiver recevier;
    private TextView tv_loading;
    private LinearLayout ll_loading;
    //是否是网络视频
    private boolean isNetUrl = false;
    private LinearLayout ll_buffer;
    private TextView tv_buffer;

    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-09 20:07:49 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_video_player);
        videoView = (com.example.mobilephoneplayer.view.VideoView) findViewById(videoview);
        llTop = (LinearLayout) findViewById(R.id.ll_top);
        tvName = (TextView) findViewById(R.id.tv_name);
        ivBattery = (ImageView) findViewById(R.id.iv_battery);
        tvSystetime = (TextView) findViewById(R.id.tv_systetime);
        btnVoice = (Button) findViewById(R.id.btn_voice);
        seekbarVoice = (SeekBar) findViewById(R.id.seekbar_voice);
        btnSwichePlayer = (Button) findViewById(R.id.btn_swiche_player);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
        tvCurrenttime = (TextView) findViewById(R.id.tv_currenttime);
        seekbarVideo = (SeekBar) findViewById(R.id.seekbar_video);
        tvDuration = (TextView) findViewById(R.id.tv_duration);
        btnExit = (Button) findViewById(R.id.btn_exit);
        btnPre = (Button) findViewById(R.id.btn_pre);
        btnStartPause = (Button) findViewById(R.id.btn_start_pause);
        btnNext = (Button) findViewById(R.id.btn_next);
        btnSwichScreen = (Button) findViewById(R.id.btn_swich_screen);
        ll_loading = (LinearLayout) findViewById(R.id.ll_loading);
        tv_loading = (TextView) findViewById(R.id.tv_loading);
        ll_buffer = (LinearLayout) findViewById(R.id.ll_buffer);
        tv_buffer = (TextView) findViewById(R.id.tv_buffer);

        hideMediaController();//隐藏控制面板
        //获取音频的最大值15，当前值
        am = (AudioManager) getSystemService(AUDIO_SERVICE);
        currentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //和SeekBar关联
        seekbarVoice.setMax(maxVolume);
        seekbarVoice.setProgress(currentVolume);

        btnVoice.setOnClickListener(this);
        btnSwichePlayer.setOnClickListener(this);
        btnExit.setOnClickListener(this);
        btnPre.setOnClickListener(this);
        btnStartPause.setOnClickListener(this);
        btnNext.setOnClickListener(this);
        btnSwichScreen.setOnClickListener(this);
        //发消息
        handler.sendEmptyMessage(SHOW_NET_SPEAD);
    }

    /**
     * 隐藏控制面板
     */
    private void hideMediaController() {
        isShowMediaController = false;
        llTop.setVisibility(View.GONE);
        llBottom.setVisibility(View.GONE);
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-09 20:07:49 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnVoice) {
            // Handle clicks for btnVoice
            isMute = !isMute;
            updateVoice(currentVolume);

        } else if (v == btnSwichePlayer) {
            showSwichPlayerDialog();
            // Handle clicks for btnSwichePlayer
        } else if (v == btnExit) {
            // Handle clicks for btnExit
            finish();
        } else if (v == btnPre) {
            // Handle clicks for btnPre
            setPreVideo();
        } else if (v == btnStartPause) {
            startAndPause();
            // Handle clicks for btnStartPause
        } else if (v == btnNext) {
            // Handle clicks for btnNext
            setNextVideo();
        } else if (v == btnSwichScreen) {
            // Handle clicks for btnSwichScreen
            if (isFullScreen) {
                //设置默认
                setVideoType(VIDE_TYPE_DEFULT);
            } else {
                //设置全屏显示
                setVideoType(VIDE_TYPE_FULL);
            }
        }
        //移除消息
        handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        //重新发消息
        handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);

    }

    private void showSwichPlayerDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("提醒");
        builder.setMessage("当前播放使用自身播放器播放，当播放出现有声音没有画面的时候，请切换万能播放器");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startVitamioVideoPlayer();
            }
        });
        builder.show();

    }

    private void updateVoice(int progress) {
        if (isMute) {
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
            seekbarVoice.setProgress(0);
        } else {
            //第一个参数：声音的类型
            //第二个参数：声音的值：0~15
            //第三个参数：1，显示系统调声音的；0，不显示
            am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            seekbarVoice.setProgress(progress);
        }
        currentVolume = progress;
    }

    private void setNextVideo() {
        //1.判断一下列表
        if (mediaIterms != null && mediaIterms.size() > 0) {
            position++;
            if (position < mediaIterms.size()) {
                MediaIterm mediaIterm = mediaIterms.get(position);
                //显示加载页面
                ll_loading.setVisibility(View.VISIBLE);
                //设置标题
                tvName.setText(mediaIterm.getName());
                //设置播放地址
                isNetUrl = utils.isNetUrl(mediaIterm.getData());
                videoView.setVideoPath(mediaIterm.getData());
                checkButtonStatus();
                if (position == mediaIterms.size() - 1) {
                    Toast.makeText(this, "最后一个大片啦", Toast.LENGTH_SHORT).show();
                }
            } else {
                //越界
                position = mediaIterms.size() - 1;
                finish();
            }
        }
        //2. 单个的uri
        else if (uri != null) {
            finish();
        }
    }

    private void setPreVideo() {
        if (mediaIterms != null && mediaIterms.size() > 0) {
            position--;
            if (position >= 0) {
                MediaIterm mediaIterm = mediaIterms.get(position);
                //显示加载页面
                ll_loading.setVisibility(View.VISIBLE);
                //设置标题
                tvName.setText(mediaIterm.getName());
                isNetUrl = utils.isNetUrl(mediaIterm.getData());
                //设置播放地址
                videoView.setVideoPath(mediaIterm.getData());
                checkButtonStatus();
            } else {
                //越界
                position = 0;
            }
        }
    }

    private void checkButtonStatus() {
        //1.判断下一个列表
        if (mediaIterms != null && mediaIterms.size() > 0) {
            //1.其他设置默认
            setButtonEnable(true);
            //2.播放第0个，上个i设置成灰色
            if (position == 0) {
                btnPre.setBackgroundResource(R.drawable.btn_pre_gray);

                btnPre.setEnabled(false);
            }
            if (position == mediaIterms.size() - 1) {
                btnNext.setBackgroundResource(R.drawable.btn_next_gray);
                btnNext.setEnabled(false);
            }
        } else if (uri != null) {
            //上一个和下一个都要设置为灰色
            setButtonEnable(false);
        }
    }

    /**
     * 设置按钮的可点状态
     *
     * @param isEnable
     */
    private void setButtonEnable(boolean isEnable) {
        if (isEnable) {
            btnPre.setBackgroundResource(R.drawable.btn_pre_selector);
            btnNext.setBackgroundResource(R.drawable.btn_next_selector);
        } else {
            btnPre.setBackgroundResource(R.drawable.btn_pre_gray);
            btnNext.setBackgroundResource(R.drawable.btn_next_gray);
        }
        btnPre.setEnabled(isEnable);
        btnNext.setEnabled(isEnable);
    }

    //视频默认屏幕大小播放
    private static final int VIDE_TYPE_DEFULT = 1;
    //视频全屏播放
    private static final int VIDE_TYPE_FULL = 2;
    //视频是否全屏显示
    private boolean isFullScreen = false;
    private int screenWidth = 0;
    private int screenHeight = 0;
    private int videoWidth = 0;
    private int videoHeight = 0;

    private GestureDetector detector;
    //是否显示控制面板
    private boolean isShowMediaController = false;


    //隐藏控制面板
    private static final int HIDE_MEDIA_CONTROLLER = 1;
    private com.example.mobilephoneplayer.view.VideoView videoView;


    /**
     * 视频播放地址
     */
    private Uri uri;
    /**
     * 列表数据
     */
    private ArrayList<MediaIterm> mediaIterms;
    private int position;
    //音频管理者
    private AudioManager am;
    //当前音量
    private int currentVolume;

    /**
     * 最大音量：0~15
     */
    private int maxVolume;
    /**
     * 是否静音
     */
    private boolean isMute = false;

    private float startY;
    /**
     * 滑动的区域
     */
    private int touchRang = 0;

    /**
     * 当按下的时候的音量
     */
    private int mVol;
    private int prePosition;
    /**
     * 显示网络速度
     */
    private static final int SHOW_NET_SPEAD = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        //设置视频加载的监听
        setListener();
        setData();
    }

    private void initData() {
        utils = new Utils();
        //注册电量广播监听
        recevier = new MyBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        //监听电量变化
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(recevier, filter);
        //初始化手势识别器
        detector = new GestureDetector(this, new MySimpleOnGestureListener());
        //得到屏幕的宽和高
        DisplayMetrics outMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(outMetrics);//的到屏幕的参数类
        screenWidth = outMetrics.widthPixels;
        screenHeight = outMetrics.heightPixels;
        Log.e("TAG", "screenWidth==" + screenWidth + "  screenHeight==" + screenHeight);

    }

    private static final int PROGRESS = 0;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SHOW_NET_SPEAD:
                    if (isNetUrl && videoView.isPlaying()) {
                        String netSpead = utils.showNetSpeed(SystemVideoPlayerActivity.this);

                        //不为空
                        tv_loading.setText("正在加载...." + netSpead);
                        tv_buffer.setText("缓存中...." + netSpead);
                    }
                    removeMessages(SHOW_NET_SPEAD);
                    sendEmptyMessageDelayed(SHOW_NET_SPEAD, 1000);
                    break;
                case HIDE_MEDIA_CONTROLLER:
                    hideMediaController();//隐藏控制面板
                    break;
                case PROGRESS:
                    int currentPositoin = videoView.getCurrentPosition();
                    //设置视频更新
                    seekbarVideo.setProgress(currentPositoin);
                    //设置播放进度的时间
                    tvCurrenttime.setText(utils.stringForTime(currentPositoin));

                    //的到系统时间并更新
                    tvSystetime.setText(getSystemTime());

                    //设置视频缓存进度更新
                    if (isNetUrl) {
                        int buffer = videoView.getBufferPercentage();//0~100
                        //缓存进度
                        int secondaryProgress = buffer * seekbarVideo.getMax() / 100;
                        seekbarVideo.setSecondaryProgress(secondaryProgress);
                    }
                    if (isNetUrl && videoView.isPlaying()) {
                        int buffer = currentPositoin - prePosition;
                        //一秒之内播放的进度小于500毫秒就是卡了，否则不卡
                        if (buffer < 500) {
                            //卡显示缓冲
                            ll_buffer.setVisibility(View.VISIBLE);
                        } else {
                            //不卡就隐藏
                            ll_buffer.setVisibility(View.GONE);
                        }

                    }
                    prePosition = currentPositoin;
                    //不断发消息
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }
        }
    };

    private String getSystemTime() {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
        return format.format(new Date());
    }

    private void setListener() {
        //设置视频播放监听：准备好的监听，播放出错监听，播放完成监听
        videoView.setOnPreparedListener(new MyOnPreparedListener());
        videoView.setOnErrorListener(new MyOnErrorListener());
        videoView.setOnCompletionListener(new MyOnCompletionListener());
//        //设置控制面板
//        videoView.setMediaController(new MediaController(this));
        //设置视频的拖动监听
        seekbarVideo.setOnSeekBarChangeListener(new VideoOnSeekBarChangeListener());
        //设置监听滑动声音
        seekbarVoice.setOnSeekBarChangeListener(new VoiceOnSeekBarChangeListener());
    }

    /**
     * 得到播放的地址
     */
    private void getData() {
        //一个地址：从文件发起的单个播放请求
        uri = getIntent().getData();
        //得到视频列表
        mediaIterms = (ArrayList<MediaIterm>) getIntent().getSerializableExtra("vidoelist");
        position = getIntent().getIntExtra("position", 0);
    }

    private void setData() {
        if (mediaIterms != null && mediaIterms.size() > 0) {
            //根据位置获取播放视频的对象
            MediaIterm mediaIterm = mediaIterms.get(position);
            videoView.setVideoPath(mediaIterm.getData());
            tvName.setText(mediaIterm.getName());
            isNetUrl = utils.isNetUrl(mediaIterm.getData());
        } else if (uri != null) {
            //设置播放的地址
            videoView.setVideoURI(uri);
            tvName.setText(uri.toString());
            isNetUrl = utils.isNetUrl(uri.toString());
        }
        checkButtonStatus();

    }

    private class MyOnPreparedListener implements MediaPlayer.OnPreparedListener {
        /**
         * 当底层加载视频准备完成的时候回调
         *
         * @param mediaPlayer
         */
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            //得到原始视频的大小
            videoWidth = mediaPlayer.getVideoWidth();
            videoHeight = mediaPlayer.getVideoHeight();
            //设置默认大小
            setVideoType(VIDE_TYPE_DEFULT);
            //开始播放视频
            videoView.start();
            //准备好的时候
            //1.视频的总播放时长和SeeKBar关联起来
            int duration = videoView.getDuration();
            seekbarVideo.setMax(duration);
            //设置总时长
            tvDuration.setText(utils.stringForTime(duration));
            //发消息
            handler.sendEmptyMessage(PROGRESS);
            //隐藏加载等待页面
            ll_loading.setVisibility(View.GONE);

        }
    }

    private class MyOnErrorListener implements MediaPlayer.OnErrorListener {
        @Override
        public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
            //1.播放的视频不支持--tiaozhuan万能播放器播放
            startVitamioVideoPlayer();
            //2播放网络资源视频的时候，断网--提示--重试(3次)

            //视频内容有缺损
            return true;
        }
    }

    /**
     * 启动万能解码器
     */
    private void startVitamioVideoPlayer() {
//        if (videoView != null) {
//            videoView.stopPlayback();
//        }
//
//        Intent intent = new Intent(this, VitamioVideoPlayerActivity.class);
//        if (mediaIterms != null) {
//            Bundle bundle = new Bundle();
//            //列表数据
//            bundle.putSerializable("vidoelist", mediaIterms);
//            intent.putExtras(bundle);
//            //传递点击的位置
//            intent.putExtra("position", position);
//        } else if (uri != null) {
//            intent.setDataAndType(uri, "video/*");
//        }
//        startActivity(intent);
//        finish();
    }

    private class MyOnCompletionListener implements MediaPlayer.OnCompletionListener {
        @Override
        public void onCompletion(MediaPlayer mediaPlayer) {
            //1.单个视频-退出播放器
            //2.视频播放列表，播放下一个
            setNextVideo();
        }
    }

    private class VideoOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        /**
         * 状态变化的时候回调
         *
         * @param seekBar
         * @param i       当前改变的进度-要拖动到的位置
         * @param b       用户导致的改变true,否则false
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                videoView.seekTo(i);
            }
        }

        /**
         * 当手指按下的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 当手指离开的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //得到电量：0~100  记住 就这么写("level",0)
            int level = intent.getIntExtra("level", 0);
            //主线程
            setBattery(level);
        }
    }

    private void setBattery(int level) {
        if (level <= 0) {
            ivBattery.setImageResource(R.drawable.ic_battery_0);
        } else if (level <= 10) {
            ivBattery.setImageResource(R.drawable.ic_battery_10);

        } else if (level <= 20) {
            ivBattery.setImageResource(R.drawable.ic_battery_20);

        } else if (level <= 40) {
            ivBattery.setImageResource(R.drawable.ic_battery_40);

        } else if (level <= 60) {
            ivBattery.setImageResource(R.drawable.ic_battery_60);

        } else if (level <= 80) {
            ivBattery.setImageResource(R.drawable.ic_battery_80);

        } else if (level <= 100) {
            ivBattery.setImageResource(R.drawable.ic_battery_100);

        } else {
            ivBattery.setImageResource(R.drawable.ic_battery_100);

        }
    }

    class MySimpleOnGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);
            startAndPause();
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (isFullScreen) {
                //默认设置
                setVideoType(VIDE_TYPE_DEFULT);
            } else {
                //全屏设置
                setVideoType(VIDE_TYPE_FULL);
            }

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (isShowMediaController) {
                //隐藏
                hideMediaController();
                //把消息移除
                handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            } else {
                //显示
                showMediaController();
                //重发消息
                handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    private void setVideoType(int videTypeDefult) {
        switch (videTypeDefult) {
            case VIDE_TYPE_FULL:
                isFullScreen = true;
                videoView.setViewSize(screenWidth, screenHeight);
                Log.e("TAG", "screenWidth==" + screenWidth + ", screenHeight==" + screenHeight);
                //把按钮设置——默认
                btnSwichScreen.setBackgroundResource(R.drawable.btn_screen_defualt_selector);
                break;
            case VIDE_TYPE_DEFULT:
                isFullScreen = false;
                //设置原始画面大小
                int mVideoWidth = videoWidth;
                int mVideoHeight = videoHeight;
                /**
                 * 计算后的值
                 */
                int width = screenWidth;
                int height = screenHeight;

                if (mVideoWidth * height < width * mVideoHeight) {
                    width = height * mVideoWidth / mVideoHeight;
                } else if (mVideoWidth * height > width * mVideoHeight) {
                    height = width * mVideoHeight / mVideoWidth;
                }
                //把计算好的视频大小传递过去
                videoView.setViewSize(width, height);
                Log.e("TAG", "width==" + width + ", height==" + height);
                //把按钮设置--全屏
                btnSwichScreen.setBackgroundResource(R.drawable.btn_screen_full_selector);
                break;

        }
    }

    private void startAndPause() {
        if (videoView.isPlaying()) {//是否在播放
            //当前在播放要设置为暂停
            videoView.pause();
            //按钮状态-播放状态
            btnStartPause.setBackgroundResource(R.drawable.btn_start_select);
        } else {
            //当前暂停状态要设置播放状态
            videoView.start();
            //按钮状态-暂停状态
            btnStartPause.setBackgroundResource(R.drawable.btn_pause_selector);
        }
    }

    /**
     * 显示控制面板
     */
    private void showMediaController() {
        isShowMediaController = true;
        llTop.setVisibility(View.VISIBLE);
        llBottom.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        if (recevier != null) {
            unregisterReceiver(recevier);
            recevier = null;
        }
        //消息移除
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        detector.onTouchEvent(event);//把事件传递给手势识别器
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //1.按下
            //按下的时候记录起始坐标，最大的滑动区域（屏幕的高），当前的音量
            startY = event.getY();
            touchRang = Math.min(screenHeight, screenWidth);//screeHeight
            mVol = am.getStreamVolume(AudioManager.STREAM_MUSIC);
            //把消息移除
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            float endY = event.getY();
            //屏幕滑动的距离
            float distanceY = startY - endY;
            //滑动屏幕的距离 ： 总距离  = 改变的声音 ： 总声音

            //改变的声音 = （滑动屏幕的距离 / 总距离)*总声音
            float delta = (distanceY / touchRang) * maxVolume;
            // 设置的声音  = 原来记录的 + 改变的声音
            int volue = (int) Math.min(Math.max(mVol + delta, 0), maxVolume);
            //判断
            if (delta != 0) {
                updateVoiceProgress(volue);
            }

        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }


        return true;
    }

    private void updateVoiceProgress(int volue) {
        //第一个参数：声音的类型
        //第二个参数：声音的值：0~15
        //第三个参数：1，显示系统调声音的；0，不显示
        am.setStreamVolume(AudioManager.STREAM_MUSIC, volue, 0);
        seekbarVoice.setProgress(volue);
        if (volue <= 0) {
            //设置静音
            isMute = true;
        } else {
            isMute = false;
        }
        currentVolume = volue;
    }

    private class VoiceOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            if (b) {
                updateVoiceProgress(i);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
        }

        /**
         * 当手指离开的时候回调
         *
         * @param seekBar
         */
        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            //改变音量值
            currentVolume--;
            updateVoiceProgress(currentVolume);
            //移除消息
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            //发消息
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            currentVolume++;
            updateVoiceProgress(currentVolume);
            handler.removeMessages(HIDE_MEDIA_CONTROLLER);
            handler.sendEmptyMessageDelayed(HIDE_MEDIA_CONTROLLER, 4000);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
