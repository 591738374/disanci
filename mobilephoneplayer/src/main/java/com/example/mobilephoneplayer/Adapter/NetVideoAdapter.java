package com.example.mobilephoneplayer.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.bean.MediaIterm;
import com.example.mobilephoneplayer.utils.Utils;

import org.xutils.common.util.DensityUtil;
import org.xutils.image.ImageOptions;

import java.util.ArrayList;

/**
 * Created by WZ on 2017/1/8.
 */
public class NetVideoAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<MediaIterm> mediaIterms;
    private Utils utils;
    private ImageOptions imageOptions;

    public NetVideoAdapter(Context mContext, ArrayList<MediaIterm> mediaIterms) {
        this.mContext = mContext;
        this.mediaIterms = mediaIterms;
        utils = new Utils();

        imageOptions = new ImageOptions.Builder()
                .setSize(DensityUtil.dip2px(120), DensityUtil.dip2px(120))
                .setRadius(DensityUtil.dip2px(5))
                // 如果ImageView的大小不是定义为wrap_content, 不要crop
                .setCrop(true)// 很多时候设置了合适的scaleType也不需要它.
                // 加载中或错误图片的ScaleType
                //setPlaceholderScaleType(ImageView.ScaleType.MATRIX
                .setImageScaleType(ImageView.ScaleType.CENTER_CROP)
                .setLoadingDrawableId(R.drawable.video_default)//加载过程中的默认图片
                .setFailureDrawableId(R.drawable.video_default)//就挨着出错的图片
                .build();

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
        if (view == null) {
            view = View.inflate(mContext, R.layout.iterm_net_video, null);
            holder = new ViewHolder();
            holder.iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            holder.tv_name = (TextView) view.findViewById(R.id.tv_name);
            holder.tv_duration = (TextView) view.findViewById(R.id.tv_duration);
            holder.tv_size = (TextView) view.findViewById(R.id.tv_size);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }
        //根据位置得到对应的数据
        MediaIterm mediaIterm = mediaIterms.get(i);
        holder.tv_name.setText(mediaIterm.getName());
        holder.tv_size.setText(mediaIterm.getDuration()+"秒 ");
        holder.tv_duration.setText(mediaIterm.getDesc());

        //请求图片
        //x.image().bind(holder.iv_icon,mediaIterm.getImageUrl(),imageOptions);
        //使用Glide或者Picasso请求图片
//        Picasso.with(mContext)
//                .load(mediaIterm.getImageUrl())
//                .placeholder(R.drawable.video_default)
//                .error(R.drawable.video_default)
//                .into(holder.iv_icon);
        Glide.with(mContext)
                .load(mediaIterm.getImageUrl())
                .placeholder(R.drawable.video_default)
                .error(R.drawable.video_default)
                .into(holder.iv_icon);
        return view;
    }

    class ViewHolder {
        TextView tv_name;
        TextView tv_duration;
        TextView tv_size;
        ImageView iv_icon;
    }
}
