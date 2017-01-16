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
 * Created by 潘鹏程 on 2017/1/15.
 * 微信:13212223597
 * QQ:591738374
 * 作用:自定义显示歌词的控件
 */

public class LyircShowView extends TextView {


    private  int width;
    private int height;

    private ArrayList<LyricBean> lyricBeen;
    private Paint paint;
    private  Paint nopaint;

    //歌词的索引
    private int index=0;
    private float textHeight=20;

    public LyircShowView(Context context, AttributeSet attrs) {
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

        nopaint=new Paint();

        nopaint.setTextSize(20);

        nopaint.setColor(Color.WHITE);

        nopaint.setTextAlign(Paint.Align.CENTER);

        nopaint.setAntiAlias(true);





        //添加歌词列表

        lyricBeen=new ArrayList<>();

        LyricBean lyricBean=new LyricBean();

        for(int i=0;i<1000;i++){
            //歌词内容
            lyricBean.setContent("aaaaaaaaa"+i);
            //休眠时间

            lyricBean.setSleepTime(i+1000);
            //时间戳
            lyricBean.setTimePoint(i*1000);

           //添加到集合中

           lyricBeen.add(lyricBean);

            //重新创建
            lyricBean=new LyricBean();


        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        width=w;
        height=h;

    }

    //绘制歌词


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(lyricBeen!=null&&lyricBeen.size()>0){

          //绘制歌词
          //当前句 -绿色
          String content=  lyricBeen.get(index).getContent();

            canvas.drawText(content,width/2,height/2,paint);
          //绘制前面部分

            float tempY=height/2;
            for(int i=index-1;i>=0;i--){

                tempY=tempY-textHeight;

                if(tempY<0){

                 break;
                }

                String precontent= lyricBeen.get(i).getContent();
                canvas.drawText(precontent,width/2,tempY,nopaint);
            }
            //绘制后面部分


             tempY=height/2;
            for(int i=index+1;i<lyricBeen.size();i++){


                tempY=tempY+textHeight;
                if(tempY>height){

                    break;
                }

                String nextContent= lyricBeen.get(i).getContent();
                canvas.drawText(nextContent,width/2,tempY,nopaint);
            }



        }else {


            //没有歌词
            canvas.drawText("没有找到歌词...",width/2,height/2,paint);

        }


    }
}
