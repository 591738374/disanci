package com.example.mobilephoneplayer;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.RadioGroup;

import com.example.mobilephoneplayer.basefragment.BaseFragment;
import com.example.mobilephoneplayer.fragment.LocalAudioFragment;
import com.example.mobilephoneplayer.fragment.LocalVideoFragment;
import com.example.mobilephoneplayer.fragment.NetAudioFragment;
import com.example.mobilephoneplayer.fragment.NetVideoFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ArrayList<BaseFragment> fagments;
    private RadioGroup rg_main;
    /**
     * Fragment页面的下标位置
     */
    private int position;
    /**
     * 缓存的Fragment
     */
    private Fragment tempFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        isGrantExternalRW(this);
        //初始化Fragment
        initFragment();
        rg_main = (RadioGroup) findViewById(R.id.rg_main);
        //RadioGroup设置监听
        initRadioGroupLitener();
    }

    private void initRadioGroupLitener() {
        rg_main.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                switch (i) {
                    case R.id.rb_local_video:
                        position = 0;
                        break;
                    case R.id.rb_local_audio:
                        position = 1;
                        break;
                    case R.id.rb_net_audio:
                        position = 2;
                        break;
                    case R.id.rb_net_video:
                        position = 3;
                        break;
                }
                //Fragment-当前的Fragment
                Fragment currentFragment = fagments.get(position);
                switchFragment(currentFragment);
            }
        });
        //默认选中本地视频
        rg_main.check(R.id.rb_local_video);
    }

    private void switchFragment(Fragment currentFragment) {
        if (tempFragment != currentFragment) {
            //开启事务
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            //切换
            if (currentFragment != null) {
                //是否添加过
                if (!currentFragment.isAdded()) {
                    //隐藏之前的
                    if (tempFragment != null) {
                        ft.hide(tempFragment);
                    }
                    //添加
                    ft.add(R.id.fl_main_content, currentFragment);
                } else {
                    if (tempFragment != null) {
                        ft.hide(tempFragment);
                    }
                    //显示
                    ft.show(currentFragment);
                }
                ft.commit();
            }
            tempFragment = currentFragment;
        }
    }

    /**
     * 初始化Fragment
     * 有先后顺序要求
     */
    private void initFragment() {
        fagments = new ArrayList<BaseFragment>();
        fagments.add(new LocalVideoFragment());
        fagments.add(new LocalAudioFragment());
        fagments.add(new NetAudioFragment());
        fagments.add(new NetVideoFragment());
    }

    /**
     * 解决安卓6.0以上版本不能读取外部存储权限的问题
     *
     * @param activity
     * @return
     */
    public static boolean isGrantExternalRW(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && activity.checkSelfPermission(
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            activity.requestPermissions(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, 1);
            return false;
        }
        return true;
    }
}
