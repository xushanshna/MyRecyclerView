package com.example.packedrecyclerview.component;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.packedrecyclerview.R;

/**
 * recyclerview的加载中的footer
 */
public class FooterView extends LinearLayout {
    /**
     * footer的状态
     */
    public final static int STATE_LOADING = 0;
    public final static int STATE_COMPLETE = 1;
    public final static int STATE_NOMORE = 2;

    private TextView mText;//文字提示
    private String loadingHint;
    private String noMoreHint;
    private String loadingDoneHint;

    private ProgressBar progressBar;//进度条

    public FooterView(Context context) {
        this(context, null);
    }

    public FooterView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FooterView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(LinearLayout.HORIZONTAL);
        setLayoutParams(new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        progressBar = new ProgressBar(getContext());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                getResources().getDimensionPixelSize(R.dimen.foot_pb_size),
                getResources().getDimensionPixelSize(R.dimen.foot_pb_size));
        progressBar.setLayoutParams(layoutParams);
        addView(progressBar);


        mText = new TextView(getContext());
        mText.setTextSize(getResources().getDimension(R.dimen.foot_hint_size));
        mText.setText(getContext().getString(R.string.footer_loading));
        if (TextUtils.isEmpty(loadingHint)) {
            loadingHint = getContext().getString(R.string.footer_loading);
        }
        if (TextUtils.isEmpty(loadingDoneHint)) {
            loadingDoneHint = getContext().getString(R.string.footer_load_complete);
        }
        if (TextUtils.isEmpty(noMoreHint)) {
            noMoreHint = getContext().getString(R.string.footer_load_nomore);
        }
        addView(mText);
    }


    public void setState(int state) {
        switch (state) {
            case STATE_LOADING:
                mText.setText(loadingHint);
                this.setVisibility(View.VISIBLE);
                break;
            case STATE_COMPLETE:
                mText.setText(loadingDoneHint);
                this.setVisibility(View.GONE);
                break;
            case STATE_NOMORE:
                mText.setText(noMoreHint);
                progressBar.setVisibility(GONE);
                this.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

}
