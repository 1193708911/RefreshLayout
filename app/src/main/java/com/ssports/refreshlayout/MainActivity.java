package com.ssports.refreshlayout;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView mListview;
    private SmartRefreshLayout mSmartView;
    private MyAdapter myAdapter;

    private List<String> strings = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListview = findViewById(R.id.listview);
        mSmartView = findViewById(R.id.smart_view);

        bindData();

        bindListener();
    }

    private void initData() {
        strings.clear();
        for (int index = 0; index < 10; index++) {
            strings.add("当前的条数为" + index);
        }
    }

    private void bindData() {

        myAdapter = new MyAdapter();

        mListview.setAdapter(myAdapter);


    }

    private void bindListener() {
        mSmartView.setOnRefreshListener(new SmartRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.e(TAG, "onRefresh: ");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        initData();
                        myAdapter.notifyDataSetChanged();
                        mSmartView.endRefresh();
                    }
                }, 1000);


            }
        });


        mSmartView.setOnLoadMoreListener(new SmartRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.e(TAG, "onLoadMore: ");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        strings.add("张三" + 1);
                        strings.add("李四" + 1);
                        myAdapter.notifyDataSetChanged();
                        mSmartView.endLoadMore();
                    }
                }, 1000);


            }
        });


        mListview.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSmartView.autoRefresh();
            }
        }, 500);

    }


    class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return strings.size();
        }

        @Override
        public Object getItem(int position) {
            return strings.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            TextView textView = new TextView(MainActivity.this);
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100);
            textView.setLayoutParams(layoutParams);
            textView.setText(strings.get(position));

            return textView;
        }
    }
}
