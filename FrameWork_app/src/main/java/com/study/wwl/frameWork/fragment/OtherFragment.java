package com.study.wwl.frameWork.fragment;

import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.study.wwl.frameWork.base.BaseFragment;

/**
 * Created by wilon on 2016/8/31.
 */
public class OtherFragment extends BaseFragment {


    private static final String TAG = OtherFragment.class.getSimpleName();//"CommonFrameFragment"
    private TextView textView;

    @Override
    protected View initView() {
        Log.e(TAG, "其他Fragment页面被初始化了...");
        textView = new TextView(mContext);
        textView.setTextSize(20);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.RED);
        return textView;
    }

    @Override
    protected void initData() {
        super.initData();
        Log.e(TAG, "其他Fragment数据被初始化了...");
        textView.setText("其他页面");
    }
}
