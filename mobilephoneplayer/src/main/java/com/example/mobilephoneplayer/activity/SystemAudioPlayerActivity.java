package com.example.mobilephoneplayer.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.mobilephoneplayer.IMusicPlayerService;
import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.service.MusicPlayerService;
import com.example.mobilephoneplayer.utils.Utils;

public class SystemAudioPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    private ImageView ivIcon;
    private TextView tvArtist;
    private TextView tvName;
    private TextView tvTime;
    private SeekBar seekbarAudio;
    private Button btnAudioPalymode;
    private Button btnAudioPre;
    private Button btnAudioStartPause;
    private Button btnAudioNext;
    private Button btnSwichLyrc;
    private int position;

    private MyReceiver receiver;
    /**
     * 进度更新
     */
    private static final int PROGRESS = 1;
    private Utils utils;
    private boolean notification;


    /**
     * Find the Views in the layout<br />
     * <br />
     * Auto-created on 2017-01-13 20:20:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    private void findViews() {
        setContentView(R.layout.activity_system_audio_player);
        tvArtist = (TextView) findViewById(R.id.tv_artist);
        tvName = (TextView) findViewById(R.id.tv_name);
        tvTime = (TextView) findViewById(R.id.tv_time);
        seekbarAudio = (SeekBar) findViewById(R.id.seekbar_audio);
        btnAudioPalymode = (Button) findViewById(R.id.btn_audio_palymode);
        btnAudioPre = (Button) findViewById(R.id.btn_audio_pre);
        btnAudioStartPause = (Button) findViewById(R.id.btn_audio_start_pause);
        btnAudioNext = (Button) findViewById(R.id.btn_audio_next);
        btnSwichLyrc = (Button) findViewById(R.id.btn_swich_lyrc);
        ivIcon = (ImageView) findViewById(R.id.iv_icon);
        ivIcon.setBackgroundResource(R.drawable.animation_list);
        AnimationDrawable drawable = (AnimationDrawable) ivIcon.getBackground();
        drawable.start();

        btnAudioPalymode.setOnClickListener(this);
        btnAudioPre.setOnClickListener(this);
        btnAudioStartPause.setOnClickListener(this);
        btnAudioNext.setOnClickListener(this);
        btnSwichLyrc.setOnClickListener(this);
        //设置拖动监听
        seekbarAudio.setOnSeekBarChangeListener(new MyOnSeekBarChangeListener());

        
    }

    /**
     * Handle button click events<br />
     * <br />
     * Auto-created on 2017-01-13 20:20:06 by Android Layout Finder
     * (http://www.buzzingandroid.com/tools/android-layout-finder)
     */
    @Override
    public void onClick(View v) {
        if (v == btnAudioPalymode) {
            // Handle clicks for btnAudioPalymode
            changePlayMode();

        } else if (v == btnAudioPre) {
            // Handle clicks for btnAudioPre
            try {
                service.pre();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnAudioStartPause) {
            // Handle clicks for btnAudioStartPause
            try {
                if (service.isPlaying()) {
                    //暂停
                    service.pause();
                    //按钮状态-->设置为播放
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_start_selector);
                } else {
                    //播放
                    service.start();
                    //按钮状态-->设置为暂停
                    btnAudioStartPause.setBackgroundResource(R.drawable.btn_audio_pause_selector);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnAudioNext) {
            // Handle clicks for btnAudioNext
            try {
                service.next();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        } else if (v == btnSwichLyrc) {
            // Handle clicks for btnSwichLyrc
        }
    }

    private void changePlayMode() {
        try {
            int playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_NOMAL) {
                playmode = MusicPlayerService.REPEAT_SINGLE;
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                playmode = MusicPlayerService.REPEAT_ALL;
            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                playmode = MusicPlayerService.REPEAT_NOMAL;
            } else {
                playmode = MusicPlayerService.REPEAT_NOMAL;
            }
            //保存到服务中
            service.setPlayMode(playmode);
            checkButtonStatu();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void checkButtonStatu() {

        int playmode = 0;
        try {
            playmode = service.getPlayMode();
            if (playmode == MusicPlayerService.REPEAT_NOMAL) {
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            } else if (playmode == MusicPlayerService.REPEAT_SINGLE) {
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_playmode_single_selector);

            } else if (playmode == MusicPlayerService.REPEAT_ALL) {
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_playmode_all_selector);

            } else {
                btnAudioPalymode.setBackgroundResource(R.drawable.btn_audio_playmode_normal_selector);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private IMusicPlayerService service;
    private ServiceConnection conn = new ServiceConnection() {
        /**
         * 当连接成功后回调
         * @param componentName
         * @param iBinder
         */
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            service = IMusicPlayerService.Stub.asInterface(iBinder);

            if (service != null) {
                //从列表进入
                if (!notification) {
                    try {
                        service.openAudio(position);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                } else {
                    showViewData();
                }
            }
        }

        /**
         * 当断开的时候回调
         * @param componentName
         */
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        findViews();
        getData();
        //绑定方式启动服务
        startAndBindService();

    }

    /**
     * 接受广播
     */
    private void initData() {
        receiver = new MyReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MusicPlayerService.OPEN_COMPLETE);
        registerReceiver(receiver, intentFilter);
        utils = new Utils();


    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (MusicPlayerService.OPEN_COMPLETE.equals(intent.getAction())) {
                showViewData();
            }
        }
    }

    /**
     * 显示视图的数据
     */
    private void showViewData() {
        try {
            tvArtist.setText(service.getArtistName());
            tvName.setText(service.getAudioName());
            //得到总时长
            int duration = service.getDuration();
            seekbarAudio.setMax(duration);
            //更新进度
            handler.sendEmptyMessage(PROGRESS);
            checkButtonStatu();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PROGRESS:
                    try {
                        int currentPosition = service.getCurrentPosition();
                        tvTime.setText(utils.stringForTime(currentPosition) + "/" + utils.stringForTime(service.getDuration()));

                        //SeekBar进度更新
                        seekbarAudio.setProgress(currentPosition);

                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    removeMessages(PROGRESS);
                    sendEmptyMessageDelayed(PROGRESS, 1000);
                    break;
            }

        }
    };

    @Override
    protected void onDestroy() {
        if (conn != null) {
            unbindService(conn);
            conn = null;
        }
        handler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void startAndBindService() {
        Intent intent = new Intent(this, MusicPlayerService.class);
        //绑定服务
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        //启动服务
        startService(intent);

    }

    private void getData() {
        //true:从状态栏进入
        //false:从Listview中进入
        notification = getIntent().getBooleanExtra("notification", false);
        if (!notification) {
            /**
             * 得到播放的位置
             */
            position = getIntent().getIntExtra("position", 0);
        }

    }

    private class MyOnSeekBarChangeListener implements SeekBar.OnSeekBarChangeListener {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (fromUser) {
                try {
                    service.seekTo(progress);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    }
}
