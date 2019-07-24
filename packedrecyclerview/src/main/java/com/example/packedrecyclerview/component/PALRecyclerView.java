package com.example.packedrecyclerview.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.example.packedrecyclerview.R;

/**
 * 封装上拉加载，下拉刷新
 */
public class PALRecyclerView extends LinearLayout
        implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "pal";

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FooterRecyclerView recyclerView;

    public void setPullAndLoadListener(PullAndLoadListener pullAndLoadListener) {
        this.pullAndLoadListener = pullAndLoadListener;
    }

    private PullAndLoadListener pullAndLoadListener;

    public PALRecyclerView(Context context) {
        this(context, null);
    }

    public PALRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PALRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.pal_rv, this, false);
        addView(view);
        swipeRefreshLayout = view.findViewById(R.id.pal_swiperefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        recyclerView = view.findViewById(R.id.pal_recyclerview);
        //设置加载更多的监听
        recyclerView.setOnLoadMoreListener(new FooterRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pullAndLoadListener != null) {
                    swipeRefreshLayout.setEnabled(false);
                    pullAndLoadListener.onLoadMore();
                }
            }
        });
    }

    public void setLayoutManager(LinearLayoutManager linearLayoutManager) {
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    public void setAdapter(RecyclerView.Adapter adapter) {
        if (adapter != null)
            recyclerView.setAdapter(adapter);
    }


    @Override
    public void onRefresh() {
        //todo 上拉后，正在加载时，下拉不能刷新
        if (pullAndLoadListener != null) {
            loadMoreComplete();
            recyclerView.setRefreshing(true);
            pullAndLoadListener.onRefresh();
        } else {
            Log.d(TAG, "onRefresh: pullAndLoadListener isnull");
        }
    }


    public void loadMoreComplete() {
        recyclerView.loadMoreComplete();
        swipeRefreshLayout.setEnabled(true);
    }

    public void refresh() {
        swipeRefreshLayout.setRefreshing(true);
        onRefresh();
    }

    public void refreshComplete() {
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.setRefreshing(false);
    }

    public void setNoMore() {
        recyclerView.setNoMore();
        swipeRefreshLayout.setEnabled(true);
    }


    public interface PullAndLoadListener {
        void onRefresh();

        void onLoadMore();
    }
}
