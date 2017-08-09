package com.tatait.turtleedu.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tatait.turtleedu.DB.CourseManager;
import com.tatait.turtleedu.DB.LessonsManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.adapter.CourseAdapter;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Course;
import com.tatait.turtleedu.model.Lesson;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;
import com.tatait.turtleedu.view.AutoLoadListView;

import java.util.ArrayList;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, AdapterView.OnItemClickListener, AutoLoadListView.OnLoadListener {
    private static final int PAGE_SIZE = 20;

    @Bind(R.id.lv_main_teach_list)
    private AutoLoadListView lvMainTeach;
    @Bind(R.id.fab_main)
    private FloatingActionButton fab;
    @Bind(R.id.nav_view)
    private NavigationView navigationView;
    @Bind(R.id.toolbar)
    private Toolbar toolbar;
    @Bind(R.id.drawer_layout)
    private DrawerLayout drawer;

    private ArrayList<Course.CourseData> mCourseDataList = new ArrayList<>();
    private CourseAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private int mIndex = 1;
    private CourseManager courseManager;
    private LessonsManager lessonsManager;
    private TextView teach_add;
    private View vNavigationHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        init();
        onLoad();
        updateLogin();
    }

    private void init() {
        courseManager = CourseManager.getInstance(getApplicationContext());
        lessonsManager = LessonsManager.getInstance(getApplicationContext());

        mAdapter = new CourseAdapter(mCourseDataList, getApplicationContext());
        if (navigationView != null) navigationView.setCheckedItem(R.id.nav_camera);
        View vHeader = LayoutInflater.from(this).inflate(R.layout.activity_main_teach_header, null);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        teach_add = (TextView) vHeader.findViewById(R.id.tv_main_teach_add);
        vHeader.setLayoutParams(params);
        if (Preferences.isDownLoadData()) {
            teach_add.setText(R.string.offline_data_success);
            teach_add.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_gray_color_5dp));
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
        } else {
            Preferences.saveIsDownLoadData(false);
            teach_add.setText(R.string.offline_data);
            teach_add.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_blue_color_5dp));
        }
        // 缓存全部
        teach_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Preferences.isDownLoadData()) {
                    AlertDialog.Builder changeDialog = new AlertDialog.Builder(MainActivity.this);
                    changeDialog.setMessage(getString(R.string.confirm_local_data_list));
                    changeDialog.setPositiveButton(R.string.local_data, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (courseManager != null && mCourseDataList != null && !mCourseDataList.isEmpty()) {
                                courseManager.deleteAll();
                                courseManager.addCourselists(mCourseDataList);
                                // 再统一写入数据库
                                addToSqlList();
                                ToastUtils.show(R.string.offline_data_start);
                            } else {
                                ToastUtils.show(R.string.class_data_null);
                            }
                        }
                    });
                    changeDialog.setNegativeButton(R.string.cancel, null);
                    changeDialog.show();
                } else {
                    ArrayList arrayList = courseManager.getCourseList();
                    int size = lessonsManager.getLessonSize(1);
                    if (arrayList.size() > 0 && size > 0) {
                        ToastUtils.show(R.string.has_offline_data);
                    } else {
                        ToastUtils.show(R.string.has_offline_data_miss);
                        Preferences.saveIsDownLoadData(false);
                        teach_add.setText(R.string.offline_data);
                        teach_add.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_blue_color_5dp));
                    }
                }
            }
        });
        lvMainTeach.addHeaderView(vHeader, null, false);
        lvMainTeach.setAdapter(mAdapter);
        lvMainTeach.setOnLoadListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        setTitle(R.string.class_title);
        vNavigationHeader = navigationView.getHeaderView(0);
        LinearLayout head_iv = (LinearLayout) vNavigationHeader.findViewById(R.id.ll_header_main);
        head_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Preferences.isLogin()) {
                    AlertDialog.Builder cleanDialog = new AlertDialog.Builder(MainActivity.this);
                    cleanDialog.setMessage(getResources().getString(R.string.confirm_logoff));
                    cleanDialog.setPositiveButton(R.string.logoff, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtils.show(R.string.has_logoff);
                            Preferences.saveUid("-1");
                            Preferences.saveUserName(getResources().getString(R.string.not_login));
                            Preferences.saveIsLogin(false);
                            updateLogin();
                        }
                    });
                    cleanDialog.setNegativeButton(R.string.cancel, null);
                    cleanDialog.show();
                } else {
                    startActivityForResult(new Intent(MainActivity.this, UserInfoActivity.class), 300);
                }
            }
        });
    }

    public void updateLogin() {
        TextView userName = (TextView) vNavigationHeader.findViewById(R.id.tv_user_name);
        ImageView userImg = (ImageView) vNavigationHeader.findViewById(R.id.iv_user_icon);
        if (Preferences.isLogin()) {
            userName.setText(Preferences.getUserName());
            userImg.setImageDrawable(getResources().getDrawable(R.drawable.icon_user_img));
        } else {
            userName.setText(Preferences.getUserName());
            userImg.setImageDrawable(getResources().getDrawable(R.drawable.class_per));
        }
    }

    private void addToSqlList() {
        // 循环执行插入数据库
        if (mCourseDataList != null && !mCourseDataList.isEmpty()) {
            for (int i = 1; i <= mCourseDataList.size(); i++) {
                final int cur = i;
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        new addLessonsToSql(cur).execute();// 开线程写入数据
                    }
                }, 500 * i);
            }
        }

    }

    // 开线程写入数据
    private class addLessonsToSql extends AsyncTask<Void, Void, Void> {
        int now;
        ArrayList<Lesson.LessonData> tempList = new ArrayList<>();

        private addLessonsToSql(int i) {
            now = i;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                HttpClient.getLessonByCourse(Integer.valueOf(now).toString(), new HttpCallback<Lesson>() {
                    @Override
                    public void onSuccess(Lesson response) {
                        if (response == null) {
                            return;
                        }

                        if (response.getData() != null) {
                            tempList.clear();
                            tempList.addAll(response.getData());
                            if (lessonsManager != null && tempList != null && tempList.size() > 0) {
                                lessonsManager.insertLists(now, tempList);
                            }
                        }
                        if (now == mCourseDataList.size()) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Preferences.saveIsDownLoadData(true);
                                    if (teach_add != null) {
                                        teach_add.setText(R.string.offline_data_success);
                                        teach_add.setBackgroundDrawable(getResources().getDrawable(R.drawable.corner_gray_color_5dp));
                                        if (mAdapter != null) mAdapter.notifyDataSetChanged();
                                    }
                                    ToastUtils.show(R.string.download_data_success);
                                }
                            }, 1000);
                        }
                    }

                    @Override
                    public void onFail(Exception e) {
                        e.printStackTrace();
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
        }
    }

    @Override
    protected void setListener() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Preferences.isLogin()){
                    startActivity(new Intent(MainActivity.this,NewProjectActivity.class));
                }else{
                    ToastUtils.show("请先登录！");
                    Intent userIntent = new Intent(MainActivity.this, UserInfoActivity.class);
                    startActivityForResult(userIntent, 300);
                }
            }
        });
        navigationView.setNavigationItemSelectedListener(this);
        lvMainTeach.setOnItemClickListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        final int id = item.getItemId();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (id == R.id.nav_camera) {           // 课程大纲
                } else if (id == R.id.nav_gallery) {   // 项目广场
                    Intent projectIntent = new Intent(MainActivity.this, ProjectActivity.class);
                    projectIntent.putExtra("type", "all");
                    startActivityForResult(projectIntent, 300);
                } else if (id == R.id.nav_slideshow) { // 我的项目
                    if (Preferences.isLogin()) {
                        Intent projectIntent = new Intent(MainActivity.this, ProjectActivity.class);
                        projectIntent.putExtra("type", "mine");
                        startActivityForResult(projectIntent, 300);
                    } else {
                        ToastUtils.show("请先登录！");
                        Intent userIntent = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivityForResult(userIntent, 300);
                    }
                } else if (id == R.id.nav_manage) {    // 草稿箱
                    if (Preferences.isLogin()) {
                        Intent projectIntent = new Intent(MainActivity.this, TempActivity.class);
                        startActivityForResult(projectIntent, 300);
                    } else {
                        ToastUtils.show("请先登录！");
                        Intent userIntent = new Intent(MainActivity.this, UserInfoActivity.class);
                        startActivityForResult(userIntent, 300);
                    }
                } else if (id == R.id.nav_share) {     // 意见与反馈
                    Intent feekIntent = new Intent(MainActivity.this, FeekBackActivity.class);
                    startActivityForResult(feekIntent, 300);
                } else if (id == R.id.nav_send) {      // 关于
                    Intent aboutIntent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivityForResult(aboutIntent, 300);
                }
            }
        }, 500);
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mCourseDataList != null && !mCourseDataList.isEmpty() && position >= 1 && mCourseDataList.get(position - 1) != null) {
            Course.CourseData courseData = mCourseDataList.get(position - 1);
            String cid = courseData.getCid();
            String name = courseData.getName();
            Intent lessonIntent = new Intent(MainActivity.this, LessonActivity.class);
            try {
                lessonIntent.putExtra("cid", cid);
                lessonIntent.putExtra("name", name);
                startActivityForResult(lessonIntent, 200);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onLoad() {
        if (Preferences.isDownLoadData() && mIndex != -1) {
            if (mCourseDataList == null) mCourseDataList = new ArrayList<>();
            if (courseManager != null && mCourseDataList.isEmpty()) {
                ArrayList temList = courseManager.getCourseList();
                if (temList.size() > 0) {
                    mCourseDataList.addAll(temList);
                    lvMainTeach.setEnable(false);
                    mIndex = -1;
                } else {
                    courseManager.deleteAll();
                    Preferences.saveIsDownLoadData(false);
                }
            }
            if (lvMainTeach != null) lvMainTeach.onLoadComplete();
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
        } else {
            if (mIndex != -1) {
                getCourseData(mIndex);
            }
        }
    }

    private void getCourseData(final int index) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        HttpClient.getCourse(index, PAGE_SIZE, new HttpCallback<Course>() {
            @Override
            public void onSuccess(Course response) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                lvMainTeach.onLoadComplete();
                if (response == null) {
                    return;
                }
                mCourseDataList.addAll(response.getData());
                if (response.getData().size() < PAGE_SIZE) {
                    lvMainTeach.setEnable(false);
                    mIndex = -1;
                } else {
                    mIndex += 1;
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                lvMainTeach.onLoadComplete();
                if (e instanceof RuntimeException) {
                    lvMainTeach.setEnable(false);
                    return;
                }
                ToastUtils.show(R.string.load_fail);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
        if (requestCode == 300 && navigationView != null) {
            navigationView.setCheckedItem(R.id.nav_camera);
        }
        updateLogin();
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (System.currentTimeMillis() - exitTime > 2000) {
                ToastUtils.show(R.string.press_exit);
                exitTime = System.currentTimeMillis();
            } else {
                // 程序退出
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(0);
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}