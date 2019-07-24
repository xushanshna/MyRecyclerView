package com.example.packedrecyclerview.component;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

/**
 * 实现上拉加载的recyclerview
 */
public class FooterRecyclerView extends RecyclerView {
    private static final String TAG = "pal";
    private FooterAdapter footerAdapter;//封装的adapter
    private boolean isUP = false;//是否在上滑
    private boolean isRefreshing = false;//是否正在刷新
    private boolean isLoadingMore = false;//是否在加载更多
    private FooterView footerView;

    private OnLoadMoreListener onLoadMoreListener;//加载更多的监听

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    public void setRefreshing(boolean refreshing) {
        isRefreshing = refreshing;
    }

    public FooterRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public FooterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        footerView = new FooterView(getContext());
        footerView.setVisibility(GONE);
    }


    public void loadMoreComplete() {
        isLoadingMore = false;
        footerView.setState(FooterView.STATE_COMPLETE);
    }

    public void setNoMore() {
        isLoadingMore = false;
        footerView.setState(FooterView.STATE_NOMORE);
    }

    float oldy;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                oldy = event.getY();
                break;
            case MotionEvent.ACTION_UP:
//                Log.d(TAG, "onTouchEvent: 位置变化：" + (oldy - event.getY()));
//                Log.d(TAG, "onTouchEvent: " + ViewConfiguration.get(getContext()).getScaledTouchSlop());
                if (oldy - event.getY() > ViewConfiguration.get(getContext()).getScaledTouchSlop()) {
                    isUP = true;
                    Log.d(TAG, "onTouch: 上滑");
                } else {
                    if (!isLoadingMore) {
                        isUP = false;
                        Log.d(TAG, "onTouch: 下拉");
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private int lastVisibleItemPosition;

    @Override
    public void onScrollStateChanged(int state) {
        super.onScrollStateChanged(state);
        if (isUP && !isRefreshing
                && (lastVisibleItemPosition + 1) == footerAdapter.getItemCount()
                && !isLoadingMore
                && state == RecyclerView.SCROLL_STATE_IDLE
                && onLoadMoreListener != null
        ) {
            isLoadingMore = true;
            footerView.setState(FooterView.STATE_LOADING);
            onLoadMoreListener.onLoadMore();
        }
    }

    @Override
    public void onScrolled(int dx, int dy) {
        super.onScrolled(dx, dy);
        LayoutManager layoutManager = getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            int[] into = new int[((StaggeredGridLayoutManager) layoutManager).getSpanCount()];
            ((StaggeredGridLayoutManager) layoutManager).findLastVisibleItemPositions(into);
            lastVisibleItemPosition = findMax(into);
        } else {
            lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
        }
//        linearLayoutManager = (LinearLayoutManager) getLayoutManager();
//        lastVisibleItemPosition = linearLayoutManager.findLastVisibleItemPosition();
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private AdapterDataObserver adapterDataObserver = new AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            if (footerAdapter != null) {
                footerAdapter.notifyDataSetChanged();
            }
        }
    };

    @Override
    public void setAdapter(@Nullable Adapter adapter) {
        footerAdapter = new FooterAdapter(adapter);
        super.setAdapter(footerAdapter);
        //监听适配器数据变化，将用户adapter的数据变化传递到封装的适配器中
        adapter.registerAdapterDataObserver(adapterDataObserver);
        adapterDataObserver.onChanged();
    }


    /**
     * 在用户的adapter上进行包装，添加footer
     */
    private class FooterAdapter extends RecyclerView.Adapter<ViewHolder> {

        private static final int TYPE_FOOTER = 10000;
        private RecyclerView.Adapter adapter;

        public FooterAdapter(RecyclerView.Adapter adapter) {
            this.adapter = adapter;
        }

        @Override
        public int getItemViewType(int position) {
            if (isFooter(position)) {
                return TYPE_FOOTER;
            }
            if (adapter != null) {
                return adapter.getItemViewType(position);
            }
            return super.getItemViewType(position);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
            if (viewType == TYPE_FOOTER) {
                return new SimpleViewHolder(footerView);
            }
            return adapter.onCreateViewHolder(viewGroup, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
            if (!isFooter(position)) {
                adapter.onBindViewHolder(viewHolder, position);
            }

        }

        @Override
        public int getItemCount() {
            int adjCount = 1;//多返回的数量
            return adapter == null ? 0 : getRealCount() + adjCount;
        }

        public boolean isFooter(int position) {
            return position == getItemCount() - 1;
        }

        public int getRealCount() {
            return adapter == null ? 0 : adapter.getItemCount();
        }

        private class SimpleViewHolder extends RecyclerView.ViewHolder {
            public SimpleViewHolder(View itemView) {
                super(itemView);
            }
        }

    }


    public interface OnLoadMoreListener {
        public void onLoadMore();
    }

}
