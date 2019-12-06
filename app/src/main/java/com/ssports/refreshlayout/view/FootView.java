package com.ssports.refreshlayout.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

/**
 * create  by tomcat on 2019-12-06
 */
public class FootView extends AppCompatTextView {

    public FootView(Context context) {
        super(context);
        this.setText("加载中...");
    }

    public FootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setText("加载中...");
    }


}
