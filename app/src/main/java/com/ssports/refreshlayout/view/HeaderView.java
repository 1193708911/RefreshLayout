package com.ssports.refreshlayout.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * create  by tomcat on 2019-12-06
 */
public class HeaderView extends AppCompatTextView {


    public HeaderView(Context context) {
        super(context);
        this.setText("正在刷新");

    }

    public HeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setText("正在刷新");
    }


}
