package com.example.mobilephoneplayer.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
import com.example.mobilephoneplayer.Adapter.NetVideoAdapter;
import com.example.mobilephoneplayer.R;
import com.example.mobilephoneplayer.activity.SystemVideoPlayerActivity;
import com.example.mobilephoneplayer.basefragment.BaseFragment;
import com.example.mobilephoneplayer.bean.MediaIterm;
import com.example.mobilephoneplayer.utils.CacheUtils;
import com.example.mobilephoneplayer.utils.Constant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;

/**
 * Created by WZ on 2017/1/6.
 */

public class NetVideoFragment extends BaseFragment {
    /**
     * 数据集合
     */
    private ArrayList<MediaIterm> mediaItems=new ArrayList<>();
    private NetVideoAdapter adapter;


    @ViewInject(R.id.lv_net_video)
    private ListView lv_net_video;
    @ViewInject(R.id.tv_no_media)
    TextView tv_no_media;
    @ViewInject(R.id.refresh)
    private MaterialRefreshLayout refreshLayout;

    @Override
    public View intiView() {
        View view = View.inflate(mContext, R.layout.fragment_net_video, null);
        //把View送入到Utils框中
        x.view().inject(NetVideoFragment.this, view);
        //才初始化好的
        lv_net_video.setOnItemClickListener(new MyOnItemClickListener());

        //监听下拉和上拉刷新
        refreshLayout.setMaterialRefreshListener(new MyMaterialRefreshListener());

        return view;
    }

    @Override
    protected void initData() {
        super.initData();
        String json=CacheUtils.getString(mContext,Constant.NET_URL);
        if (!TextUtils.isEmpty(json)){
            processData(json);
        }

        getDataFromNet();
    }

    /**
     * 使用xutils3联网请求数据
     */
    private void getDataFromNet() {
        //网络的路径
        RequestParams params = new RequestParams(Constant.NET_URL);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.e("TA", "xUtils3联网请求成功==" + result);
                Log.e("TAG", "线程名称==" + Thread.currentThread().getName());
                CacheUtils.putString(mContext, Constant.NET_URL,result);

                processData(result);
                if (!isLoadMore) {
                    //完成刷新
                    refreshLayout.finishRefresh();
                } else {
                    //把上拉的隐藏
                    refreshLayout.finishRefreshLoadMore();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.e("TAG", "xUtils3请求失败了==" + ex.getMessage());
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 解析json数据：gson解析，fastjson解析和手动解析（原生的api）
     * 显示数据-设置适配器
     *
     * @param json
     */
    private void processData(String json) {
        if (!isLoadMore) {
            mediaItems= parsedJson(json);
            Log.e("TAG", "mediaItems.get(0).getName()==" + mediaItems.get(0).getName());

            if (mediaItems != null && mediaItems.size() > 0) {
                //有数据
                tv_no_media.setVisibility(View.GONE);
                adapter = new NetVideoAdapter(mContext, mediaItems);
                //设置适配器
                lv_net_video.setAdapter(adapter);

            } else {
                tv_no_media.setVisibility(View.VISIBLE);
            }
        } else {
            //加载更多
            ArrayList<MediaIterm> iterms = parsedJson(json);
            mediaItems.addAll(iterms);
            //刷新适配器
            adapter.notifyDataSetChanged();//getCount-->getView
        }

    }

    private ArrayList<MediaIterm> parsedJson(String json) {
        ArrayList<MediaIterm> mediaItems = new ArrayList<MediaIterm>();

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("trailers");

            for (int i = 0; i < jsonArray.length(); i++) {
                MediaIterm mediaIterm = new MediaIterm();
                mediaItems.add(mediaIterm);

                JSONObject jsonObjectItem = (JSONObject) jsonArray.get(i);
                String name = jsonObjectItem.optString("movieName");
                mediaIterm.setName(name);
                String desc = jsonObjectItem.optString("videoTitle");
                mediaIterm.setDesc(desc);
                String url = jsonObjectItem.optString("url");
                mediaIterm.setData(url);
                String hightUrl = jsonObjectItem.optString("hightUrl");
                mediaIterm.setHeightUrl(hightUrl);
                String coverImg = jsonObjectItem.optString("coverImg");
                mediaIterm.setImageUrl(coverImg);
                int videoLength = jsonObjectItem.optInt("videoLength");
                mediaIterm.setDuration(videoLength);

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return mediaItems;
    }


    protected void flushData() {
        super.flushData();
    }

    private class MyOnItemClickListener implements android.widget.AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Intent intent = new Intent(mContext, SystemVideoPlayerActivity.class);
            //第一参数：播放路径 第二参数：路径对应的类型
            //intent.setDataAndType(Uri.parse(mediaIterm.getData()),"video/*");
            Bundle bundle = new Bundle();
            //列表数据
            bundle.putSerializable("vidoelist", mediaItems);
            intent.putExtras(bundle);
            //传递点击的位置
            intent.putExtra("position", i);
            startActivity(intent);

        }
    }

    //是否加载更多
    private boolean isLoadMore = false;

    private class MyMaterialRefreshListener extends MaterialRefreshListener {
        @Override
        public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
            //Toast.makeText(mContext, "下拉刷新", Toast.LENGTH_SHORT).show();
            isLoadMore = false;
            getDataFromNet();
        }

        /**
         * 加载更多的回调
         *
         * @param materialRefreshLayout
         */
        @Override
        public void onRefreshLoadMore(MaterialRefreshLayout materialRefreshLayout) {
            super.onRefreshLoadMore(materialRefreshLayout);
            isLoadMore = true;
            //Toast.makeText(mContext, "加载更多", Toast.LENGTH_SHORT).show();
            getDataFromNet();

        }
    }
}
