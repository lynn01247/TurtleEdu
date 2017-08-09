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
import com.tatait.turtleedu.adapter.WaterFallAdapter;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Project;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class ProjectActivity extends BaseActivity {
    @Bind(R.id.toolbar)
    private Toolbar toolbar;
    @Bind(R.id.recyclerview)
    private RecyclerView mRecyclerView;
    @Bind(R.id.swipe_ly)
    private SwipeRefreshLayout mSwipeLayout;
    @Bind(R.id.load_more)
    private TextView load_more;
    @Bind(R.id.ll_project)
    private LinearLayout ll_project;
    @Bind(R.id.ll_new_project)
    private LinearLayout ll_new_project;
    @Bind(R.id.btn_new_project)
    private TextView new_project;

    private List<Project.ProjectData> mProjectDataList = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private WaterFallAdapter mAdapter;
    private String type = "", uid = "1", token = "";
    private int pageSize = 10, sort = 1, index = 1, total = 0, sum = 0, pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);

        setSupportActionBar(toolbar);
        init();
        onLoad();
    }

    private void init() {
        type = getIntent().getStringExtra("type");
        token = Preferences.getUserToken();
        uid = Preferences.getUid();
        index = 1;
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        mProgressDialog.show();

        // 设置布局管理器为2列，纵向
//        RecyclerView.LayoutManager mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mAdapter = new WaterFallAdapter(this, mProjectDataList);
        mRecyclerView.setAdapter(mAdapter);
        // 如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        mRecyclerView.setHasFixedSize(true);
        // 新建项目
        new_project.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProjectActivity.this, NewProjectActivity.class));
            }
        });
    }

    private void onLoad() {
        if (type != null && "all".equals(type)) {
            toolbar.setTitle("项目广场");
            getAllProject(true, uid, index, pageSize, sort);
        }
        if (type != null && "mine".equals(type)) {
            toolbar.setTitle("我的项目");
            getMyProject(true, uid, token, index, pageSize, sort);
        }
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
                index = 1;
                if (type != null && "all".equals(type)) {
                    getAllProject(true, uid, index, pageSize, sort);
                } else {
                    getMyProject(true, uid, token, index, pageSize, sort);
                }
            }
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    visibleItemCount = linearManager.getChildCount();
                    totalItemCount = linearManager.getItemCount();
                    pastVisiblesItems = linearManager.findFirstVisibleItemPosition();
                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            // 判断点
                            load_more.setVisibility(View.GONE);
                            loading = false;
                            index = index + 1;
                            if (total >= index) {
                                if (type != null && "all".equals(type)) {
                                    getAllProject(false, uid, index, pageSize, sort);
                                } else {
                                    getMyProject(false, uid, token, index, pageSize, sort);
                                }
                                load_more.setText(" ↑↑↑ 上拉加载更多 ↑↑↑ ");
                            } else {
                                load_more.setText("已经到底啦！");
                            }
                        } else {
                            load_more.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.class_update_time) {
                    sort = 4;
                } else if (id == R.id.class_score) {
                    sort = 1;
                } else if (id == R.id.class_view_count) {
                    sort = 2;
                } else if (id == R.id.class_good_count) {
                    sort = 3;
                }
                onLoad();
                return true;
            }
        });

        mAdapter.setOnItemClickListener(new WaterFallAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (mProjectDataList != null && !mProjectDataList.isEmpty() && position >= 0 && mProjectDataList.get(position) != null) {
                    Project.ProjectData projectData = mProjectDataList.get(position);
                    String pid = projectData.getPid();
                    String userName = projectData.getUserName();
                    String name = projectData.getName();
                    String code = projectData.getCode();
                    String updateTime = projectData.getUpdateTime();
                    Intent projectDetailIntent = new Intent(ProjectActivity.this, ProjectDetailActivity.class);
                    try {
                        projectDetailIntent.putExtra("pid", pid);
                        projectDetailIntent.putExtra("userName", userName);
                        projectDetailIntent.putExtra("name", name);
                        projectDetailIntent.putExtra("code", code);
                        projectDetailIntent.putExtra("updateTime", updateTime);
                        startActivityForResult(projectDetailIntent, 400);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * 通过反射，设置menu显示icon
     */
    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        if (menu != null) {
            if (menu.getClass() == MenuBuilder.class) {
                try {
                    Method m = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                } catch (Exception e) {
                    Log.e("onPrepareOptionsPanel", e.getMessage());
                }
            }
        }
        return super.onPrepareOptionsPanel(view, menu);
    }

    private void getAllProject(final boolean isUpRefresh, String uid, int pageIndex, final int pageSize, int sort) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        HttpClient.getAllProject(uid, Integer.valueOf(pageIndex).toString(), Integer.valueOf(pageSize).toString(), Integer.valueOf(sort).toString(),
                new HttpCallback<Project>() {
                    @Override
                    public void onSuccess(Project response) {
                        mSwipeLayout.setRefreshing(false);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        if (response == null) {
                            return;
                        }
                        loading = true;
                        index = Integer.parseInt(response.getPage() == null ? "1" : response.getPage());
                        total = Integer.parseInt(response.getTotal() == null ? "0" : response.getTotal());
                        sum = Integer.parseInt(response.getCount() == null ? "0" : response.getCount());
                        if (type != null && "all".equals(type)) {
                            toolbar.setTitle("项目广场(" + index * pageSize + "/" + sum + ")");
                        } else {
                            toolbar.setTitle("我的项目(" + index * pageSize + "/" + sum + ")");
                        }
                        if (isUpRefresh) {
                            mProjectDataList.clear();
                            //为了保险起见可以先判断当前是否在刷新中（旋转的小圈圈在旋转）....
                            if (mSwipeLayout.isRefreshing()) {
                                //关闭刷新动画
                                mSwipeLayout.setRefreshing(false);
                            }
                        }
                        mProjectDataList.addAll(response.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mSwipeLayout.setRefreshing(false);
                        loading = true;
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        ToastUtils.show(R.string.load_fail);
                    }
                });
    }

    private void getMyProject(final boolean isUpRefresh, String uid, String token, int pageIndex, int pageSize, int sort) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        HttpClient.getMyProject(uid, token, Integer.valueOf(pageIndex).toString(), Integer.valueOf(pageSize).toString(), Integer.valueOf(sort).toString(),
                new HttpCallback<Project>() {
                    @Override
                    public void onSuccess(Project response) {
                        mSwipeLayout.setRefreshing(false);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        if (response == null) {
                            return;
                        } else if (!"1".equals(response.getCode())) {
                            ll_project.setVisibility(View.GONE);
                            ll_new_project.setVisibility(View.VISIBLE);
                            ToastUtils.show(response.getInfo());
                            return;
                        } else {
                            ll_project.setVisibility(View.VISIBLE);
                            ll_new_project.setVisibility(View.GONE);
                        }
                        index = Integer.parseInt(response.getPage() == null ? "1" : response.getPage());
                        total = Integer.parseInt(response.getTotal() == null ? "0" : response.getTotal());
                        sum = Integer.parseInt(response.getCount() == null ? "0" : response.getCount());
                        if (type != null && "all".equals(type)) {
                            toolbar.setTitle("项目广场(" + sum + ")");
                        } else {
                            toolbar.setTitle("我的项目(" + sum + ")");
                        }
                        if (isUpRefresh) {
                            mProjectDataList.clear();
                            //为了保险起见可以先判断当前是否在刷新中（旋转的小圈圈在旋转）....
                            if (mSwipeLayout.isRefreshing()) {
                                //关闭刷新动画
                                mSwipeLayout.setRefreshing(false);
                            }
                        } else {
                            index = index + 1;
                        }
                        mProjectDataList.addAll(response.getData());
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFail(Exception e) {
                        mSwipeLayout.setRefreshing(false);
                        if (mProgressDialog != null && mProgressDialog.isShowing()) {
                            mProgressDialog.dismiss();
                        }
                        ToastUtils.show(R.string.load_fail);
                    }
                });
    }
}