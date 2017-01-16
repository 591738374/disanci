package com.example.mobilephoneplayer.Adapter;

import android.content.Context;
import android.text.format.Formatter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.bean.MediaIterm;
import com.example.mobilephoneplayer.utils.Utils;

import java.util.ArrayList;

/**
 * Created by WZ on 2017/1/8.
 */
public class LocalVideoAdapter extends BaseAdapter {
    private final boolean isVideo;
    private Context mContext;
    private ArrayList<MediaIterm> mediaIterms;
    private Utils utils;

    public LocalVideoAdapter(Context mContext, ArrayList<MediaIterm> mediaIterms, boolean b) {
        this.mContext = mContext;
        this.mediaIterms = mediaIterms;
        utils = new Utils();
        this.isVideo=b;
    }

    @Override
    public int getCount() {
        return mediaIterms.size();
    }

    @Override
    public MediaIterm getItem(int i) {
        return mediaIterms.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view==null){
            view=View.inflate(mContext, R.layout.iterm_local_video,null);
            holder=new ViewHolder();
            holder.iv_icon= (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_name= (TextView) view.findViewById(R.id.tv_name);
            holder.tv_duration= (TextView) view.findViewById(R.id.tv_duration);
            holder.tv_size= (TextView) view.findViewById(R.id.tv_size);
            view.setTag(holder);

        }else {
            holder= (ViewHolder) view.getTag();
        }
        //根据位置得到对应的数据
        MediaIterm mediaIterm=mediaIterms.get(i);
        holder.tv_name.setText(mediaIterm.getName());
        holder.tv_size.setText(Formatter.formatFileSize(mContext,mediaIterm.getSize()));
        holder.tv_duration.setText(utils.stringForTime((int) mediaIterm.getDuration()));

        if (!isVideo){
            //音频
            holder.iv_icon.setImageResource(R.drawable.music_default_bg);
        }
        return view;
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView iv_icon;
    }
}
