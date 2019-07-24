package com.example.myrecyclerview;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;

import com.example.packedrecyclerview.component.PALRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PalActivity extends AppCompatActivity {

    private static final String TAG = "pal";
    private List<String> list = new ArrayList<>();
    private PALRecyclerView palRecyclerView;
    private MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pal);
//        initData();
        initView();
    }

    private void initData() {
        for (int i = 0; i < 3; i++) {
            list.add(i + "");
        }
    }

    private void initView() {
        palRecyclerView = findViewById(R.id.pal);
        palRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new MyAdapter(list);
        palRecyclerView.setAdapter(myAdapter);
        initEvent();
        palRecyclerView.refresh();
    }

    int times = 0;

    private void initEvent() {
        palRecyclerView.setPullAndLoadListener(new PALRecyclerView.PullAndLoadListener() {
            @Override
            public void onRefresh() {
                Log.d(TAG, "onRefresh: 开始刷新");
                times = 0;
                final int count = (int) (Math.random() * 10);
                new Handler().postDelayed(
                        new Runnable() {
                            @Override
                            public void run() {
                                list.clear();
                                for (int i = 0; i < (count + 1); i++) {
                                    list.add("刷新后,共" + (count + 1) + "条数据，第" + i + "条");
                                }
                                myAdapter.notifyDataSetChanged();
                                palRecyclerView.refreshComplete();
                            }
                        }, 1000
                );

            }

            @Override
            public void onLoadMore() {
                Log.d(TAG, "onLoadMore: 开始加载");

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (times < 3) {
                            times++;
                            for (int i = 0; i < 3; i++) {
                                list.add("load more数据：" + i);
                            }
                            myAdapter.notifyDataSetChanged();
                            palRecyclerView.loadMoreComplete();
                        } else {
                            palRecyclerView.setNoMore();
                        }

                    }
                }, 2000);
            }
        });
    }

}
