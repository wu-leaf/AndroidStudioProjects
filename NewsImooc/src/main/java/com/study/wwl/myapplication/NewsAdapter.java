package com.study.wwl.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wilon on 2016/8/28.
 */
public class NewsAdapter extends BaseAdapter implements AbsListView.OnScrollListener{
    private List<NewsBean> mList;
    private LayoutInflater mInflater;
    private ImageLoader mImageLoader;
    private int mStart;
    private int mEnd;
    public   static String[] URLS; //数组存储所以图片的url地址
    private boolean mFirstIn;

    public NewsAdapter(Context context,List<NewsBean> data,ListView listView){
          mList = data;
          mInflater = LayoutInflater.from(context);
          mImageLoader = new ImageLoader(listView);
          URLS = new String[data.size()];
          for (int i = 0;i < data.size();i++){
            URLS[i] = data.get(i).newsTconUrl;
              //将图片的url转到静态数组里面
          }
        mFirstIn = true;

        //一定要注册对应的事件
        listView.setOnScrollListener(this);
    }
    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.item_layout,null);
            viewHolder.ivIconl = (ImageView)convertView.findViewById(R.id.iv_icon);
            viewHolder.tvContent = (TextView)convertView.findViewById(R.id.tv_content);
            viewHolder.tvTitle = (TextView)convertView.findViewById(R.id.tv_title);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.ivIconl.setImageResource(R.drawable.ic_launcher);

        String url = mList.get(position).newsTconUrl;
        viewHolder.ivIconl.setTag(url);//将ImageView和对应的url进行绑定

        //#1 多线程异步加载方式
        /*  new ImageLoader().showImageByThread(viewHolder.ivIconl,
                mList.get(position).newsTconUrl);*/

        //#2 AsyncTask异步加载方式
        //  new ImageLoader().showImageByAsyncTask(viewHolder.ivIconl,url);
        // 当用LruCache缓存时，此方法不能用，因为每次都new一个lrucache

        //#3 在本类中设定单一对象mImageLoader
        mImageLoader.showImageByAsyncTask(viewHolder.ivIconl,url);

        viewHolder.tvTitle.setText(mList.get(position).newsTitle);
        viewHolder.tvContent.setText(mList.get(position).newsContent);
        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE){
            //加载可见项
              mImageLoader.loadImages(mStart,mEnd);
        }else{
            //停止任务
            mImageLoader.cancelAllTasks();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
          mStart = firstVisibleItem;
          mEnd = firstVisibleItem + visibleItemCount;
        //第一次显示调用
        if (mFirstIn  && visibleItemCount > 0){
            //人为显示
            mImageLoader.loadImages(mStart,mEnd);
            mFirstIn = false;
        }
    }

    class ViewHolder{
        public TextView tvTitle,tvContent;
        public ImageView ivIconl;
    }
}
