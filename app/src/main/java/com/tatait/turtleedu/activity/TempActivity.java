package com.tatait.turtleedu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tatait.turtleedu.DB.TempManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.adapter.TempAdapter;
import com.tatait.turtleedu.adapter.WaterFallAdapter;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Project;
import com.tatait.turtleedu.model.Temp;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class TempActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    private Toolbar toolbar;
    @Bind(R.id.recyclerview)
    private RecyclerView mRecyclerView;
    @Bind(R.id.swipe_ly)
    private SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.ll_project)
    private LinearLayout ll_project;
    @Bind(R.id.ll_new_project)
    private LinearLayout ll_new_project;

    private List<Temp> mTempDataList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private TempAdapter mAdapter;
    private TempManager tempManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);

        setSupportActionBar(toolbar);
        init();
        onLoad();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        tempManager = TempManager.getInstance(getApplicationContext());
        // 设置布局管理器为2列，纵向
//        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new TempAdapter(this, mTempDataList);
        mRecyclerView.setAdapter(mAdapter);
        // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        toolbar.setTitle("草稿箱");
    }

    private void onLoad() {
        getTempProject(true);
    }

    @Override
    protected void setListener() {
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        mSwipeLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        mSwipeLayout.setColorSchemeResources(R.color.colorAccent, R.color.colorPrimary, R.color.colorPrimaryDark);
        //设置在上下拉刷新的监听
        mSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getTempProject(true);
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter.setOnItemClickListener(new TempAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mTempDataList != null && !mTempDataList.isEmpty() && position >= 0 && mTempDataList.get(position) != null) {
                    Temp temp = mTempDataList.get(position);
                    String tid = temp.getTid();
                    String tname = temp.getTname();
                    String code = temp.getCode();
                    String updateTime = temp.getUpdate_time();
                    String history = temp.getHistory();
                    Intent tempIntent = new Intent(TempActivity.this, NewProjectActivity.class);
                    try {
                        tempIntent.putExtra("tid", tid);
                        tempIntent.putExtra("tname", tname);
                        tempIntent.putExtra("code", code);
                        tempIntent.putExtra("updateTime", updateTime);
                        tempIntent.putExtra("history", history);
                        startActivityForResult(tempIntent, 400);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode == 400) {
            onLoad();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void getTempProject(final boolean isUpRefresh) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        List<Temp> list = tempManager.getTempList();
        mSwipeLayout.setRefreshing(false);
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
        // 刷新数据
        if (isUpRefresh) {
            mTempDataList.clear();
            if (mSwipeLayout.isRefreshing()) {
                //关闭刷新动画
                mSwipeLayout.setRefreshing(false);
            }
        }
        if(list.isEmpty()){
            ll_project.setVisibility(View.GONE);
            ll_new_project.setVisibility(View.VISIBLE);
            return;
        }else{
            ll_project.setVisibility(View.VISIBLE);
            ll_new_project.setVisibility(View.GONE);
        }
        mTempDataList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }
}