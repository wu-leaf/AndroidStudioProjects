package com.study.wwl.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.LruCache;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;


/**
 * Created by wilon on 2016/8/28.
 */
public class ImageLoader {
    private ImageView mImageView;
    private String mUrl;
    //创建cache
    private LruCache<String,Bitmap> mCache;
    private ListView mListView;
    private Set<NewsAsyncTask> mTask;


    public ImageLoader(ListView listView){
        mListView = listView;
        mTask = new HashSet<>();
        //获取最大可用内存
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        int cacheSize = maxMemory / 4;
        mCache = new LruCache<String,Bitmap>(cacheSize){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                //每次存入缓存的时候调用
                return value.getByteCount();
            }
        };
    }
    //增加到缓存
    public void addBitmapToCache(String url,Bitmap bitmap){
        if (getBitmapFromCache(url) == null){
            mCache.put(url,bitmap);
        }

    }
    //从缓存中获取数据
    public Bitmap getBitmapFromCache(String url){
           return mCache.get(url);
    }

    private Handler mHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mImageView.getTag().equals(mUrl)){
                mImageView.setImageBitmap((Bitmap)msg.obj);
            }
        }
    };
    //多线程方式
    public void showImageByThread(ImageView imageView, final String url){
        mImageView = imageView;
        mUrl = url;
        new Thread(){
            @Override
            public void run() {
                super.run();
                Bitmap bitmap = getBitmapFromURL(url);
                Message message = Message.obtain();
                message.obj = bitmap;
                mHandler.sendMessage(message);
            }
        }.start();
    }

    public Bitmap getBitmapFromURL(String urlString ){
        Bitmap bitmap;
        InputStream is = null;
       try {
           URL url = new URL(urlString);
           HttpURLConnection connection =(HttpURLConnection)url.openConnection();
           is = new BufferedInputStream(connection.getInputStream());
           bitmap = BitmapFactory.decodeStream(is);
           connection.disconnect();
           return bitmap;
       }catch (MalformedURLException e){
           e.printStackTrace();
       }catch (IOException e){
           e.printStackTrace();
       } finally
       {
           try{
               is.close();
           }catch (IOException e){
               e.printStackTrace();
           }
       }
        return  null;
    }
    //AsyncTask方式异步加载图片
    public void showImageByAsyncTask(ImageView imageView,String url){
        //从缓存中取出对应的图片
        Bitmap bitmap = getBitmapFromCache(url);
        //如果缓存中没有
        if (bitmap == null){
            //new NewsAsyncTask(url).execute(url);
            imageView.setImageResource(R.drawable.ic_launcher);
        }else{
            imageView.setImageBitmap(bitmap);
        }
    }
    public void cancelAllTasks(){
            if (mTask != null){
                for (NewsAsyncTask task :mTask){
                    task.cancel(false);
                }
            }
    }
    //用了加载所以从start到end 的所以图片
    public void loadImages(int start,int end){
        for (int i = start;i < end;i++){
            String url = NewsAdapter.URLS[i];
            //从缓存中取出对应的图片
            Bitmap bitmap = getBitmapFromCache(url);
            //如果缓存中没有
            if (bitmap == null){
              NewsAsyncTask task = new NewsAsyncTask(url);
                task.execute(url);
                mTask.add(task);
            }else{
                //通过Tag找到对应的imageView(#之前已设定不同的imageView有不同的Tag)
                ImageView imageView =(ImageView) mListView.findViewWithTag(url);
                imageView.setImageBitmap(bitmap);
            }
        }
    }


    private class NewsAsyncTask extends AsyncTask<String,Void,Bitmap>{
        private String mUrl;
       // private ImageView mImageView;
        public NewsAsyncTask(String url){
          //  mImageView = imageView;
            mUrl = url;
        }
        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            Bitmap bitmap = getBitmapFromURL(url);
            if (bitmap!=null){
                addBitmapToCache(url,bitmap);//把url对应的bitmap一起保存到缓存中
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            /*if (mImageView.getTag().equals(mUrl)){
                mImageView.setImageBitmap(bitmap);
            }*/

            //通过Tag找到对应的imageView(#之前已设定不同的imageView有不同的Tag)
            ImageView imageView = (ImageView)mListView.findViewWithTag(mUrl);
            if (imageView != null && bitmap !=null){
                imageView.setImageBitmap(bitmap);
            }
            mTask.remove(this);
        }
    }
}
