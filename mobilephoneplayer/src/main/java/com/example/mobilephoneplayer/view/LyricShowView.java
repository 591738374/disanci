package com.example.mobilephoneplayer.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

import com.example.mobilephoneplayer.bean.LyricBean;

import java.util.ArrayList;

/**
 * Created by WZ on 2017/1/15.
 */
//自定义显示歌词的控件
public class LyricShowView extends TextView {
    private int width;
    private int height;
    private ArrayList<LyricBean> lyricBeens;
    private Paint paint;
    private Paint noPaint;
    /**
     * 歌词索引
     */
    private int index=0;
    private float textHeight=20;

    public LyricShowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        //创建画笔
        paint=new Paint();
        paint.setTextSize(20);
        paint.setColor(Color.GREEN);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setAntiAlias(true);

        noPaint=new Paint();
        noPaint.setTextSize(20);
        noPaint.setColor(Color.WHITE);
        noPaint.setTextAlign(Paint.Align.CENTER);
        noPaint.setAntiAlias(true);

        lyricBeens=new ArrayList<>();
        LyricBean lyricBean=new LyricBean();
        for (int i=0;i<1000;i++){
            lyricBean.setContent("aaaa"+i);
            lyricBean.setSleepTime(i+1000);
            lyricBean.setTimePoint(i*1000);
            //添加到集合中
            lyricBeens.add(lyricBean);
            //重新创建
            lyricBean=new LyricBean();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width=h;
        height=h;
    }
    /**
     * 绘制歌词
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (lyricBeens!=null&&lyricBeens.size()>0){
            //绘制歌词
            //当前句--绿色
            String content=lyricBeens.get(index).getContent();
            canvas.drawText(content,width/2,height/2,paint);
            //绘制前面部分
            float tempY=height/2;
            for (int i=index-1;i>=0;i--){
                tempY=tempY-textHeight;
                if (tempY<0){
                    break;
                }
                String preContent=lyricBeens.get(i).getContent();
                canvas.drawText(preContent,width/2,height/2, noPaint);
            }
            //绘制后面
            tempY=height/2;
            for (int i=index+1;i>lyricBeens.size();i++){
                tempY=tempY+textHeight;
                if (tempY>textHeight){
                    break;
                }
                String nextContent=lyricBeens.get(i).getContent();
                canvas.drawText(nextContent,width/2,height/2, noPaint);
            }
        }else {
            //没有歌词

            canvas.drawText("没有歌词...",width/2,height/2,paint);
        }
    }
}
