package com.study.wwl.myapplication;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private ListView mListView;
    private static  String URL = "http://www.imooc.com/api/teacher?type=4&num=30";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.lv_main);
        new NewsAsyncTask().execute(URL);
    }

    /**
     * 将URL对应的json格式数据转化为我们所封装的NewsBean中
     * @param url
     * @return
     */
    private List<NewsBean> getJsonData(String url){
        List<NewsBean> newsBeanList = new ArrayList<>();
        try{
            String jsonStrig = readStream(new URL(url).openStream());
           // Log.d("Tag",jsonStrig);
            JSONObject jsonObject;
            NewsBean newsBean;
            try{
                jsonObject = new JSONObject(jsonStrig);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for(int i = 0;i< jsonArray.length();i++){
                    jsonObject = jsonArray.getJSONObject(i);
                    newsBean = new NewsBean();
                    newsBean.newsTconUrl = jsonObject.getString("picSmall");
                    newsBean.newsTitle = jsonObject.getString("name");
                    newsBean.newsContent = jsonObject.getString("description");
                    newsBeanList.add(newsBean);
                }
            }catch (JSONException e){
                e.printStackTrace();
            }


        }catch (MalformedURLException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return newsBeanList;
    }

    /**
     * 通过InputStream解析网页返回的数据
     * @param is
     * @return
     */
    private String readStream(InputStream is){
        InputStreamReader isr;
        String result = "";
        try{
            String line = "";
            isr = new InputStreamReader(is,"utf-8");   //将字节流转化为字符流
            BufferedReader br = new BufferedReader(isr);//把字符流缓存在buffer中
            while((line = br.readLine())!= null){
                result += line;  //读取到了json 格式的字符串
            }
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 实现网络的异步访问
     */
    class NewsAsyncTask extends AsyncTask<String,Void,List<NewsBean>> {
        @Override
        protected List<NewsBean> doInBackground(String... params) {
            return getJsonData(params[0]);
        }

        @Override
        protected void onPostExecute(List<NewsBean> newsBeans) {
            super.onPostExecute(newsBeans);
            NewsAdapter adapter = new NewsAdapter(MainActivity.this,newsBeans,mListView);
            mListView.setAdapter(adapter);
        }
    }
}
