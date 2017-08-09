package com.tatait.turtleedu.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.TextView;

import com.tatait.turtleedu.DB.LessonsManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.adapter.LessonAdapter;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Lesson;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;
import com.tatait.turtleedu.view.AutoLoadListView;

import java.util.ArrayList;
import java.util.List;

public class LessonActivity extends BaseActivity implements AdapterView.OnItemClickListener, AutoLoadListView.OnLoadListener {
    @Bind(R.id.lv_lesson_list)
    private AutoLoadListView lvLesson;

    private List<Lesson.LessonData> mLessonDataList = new ArrayList<>();
    private LessonAdapter mAdapter;
    private ProgressDialog mProgressDialog;
    private String cid;
    private LessonsManager lessonsManager;
    private int mIndex = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lesson);
        init();
        onLoad();
    }

    private void init() {
        lessonsManager = LessonsManager.getInstance(getApplicationContext());
        mAdapter = new LessonAdapter(mLessonDataList, getApplicationContext());
        View vHeader = LayoutInflater.from(this).inflate(R.layout.activity_lesson_header, null);
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        vHeader.setLayoutParams(params);
        TextView lesson_header_id = (TextView) vHeader.findViewById(R.id.lesson_header_id);
        TextView lesson_header_title = (TextView) vHeader.findViewById(R.id.lesson_header_title);
        lvLesson.addHeaderView(vHeader, null, false);
        lvLesson.setAdapter(mAdapter);
        lvLesson.setOnLoadListener(this);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.loading));
        String title = getIntent().getStringExtra("name");
        cid = getIntent().getStringExtra("cid");
        setTitle(title);
        lesson_header_id.setText(getString(R.string.class_mission, cid));
        lesson_header_title.setText(title);
    }

    @Override
    protected void setListener() {
        lvLesson.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        jumpToTurtle(position);
    }

    @Override
    public void onBackPressed() {
        setResult(500);
        finish();
    }

    private void jumpToTurtle(int position) {
        if (mLessonDataList != null && !mLessonDataList.isEmpty() && position >= 1 && mLessonDataList.get(position - 1) != null) {
            Lesson.LessonData lessonData = mLessonDataList.get(position - 1);
            String cid = lessonData.getCid();
            String name = lessonData.getName();
            String command = lessonData.getCommand();
            String content = lessonData.getContent();
            String lid = lessonData.getLid();
            String tips = lessonData.getTips();
            String remark = lessonData.getRemark();

            Intent turtleIntent = new Intent(LessonActivity.this, TurtleActivity.class);
            try {
                if (position <= 1) {
                    turtleIntent.putExtra("hasPre", false);
                } else {
                    turtleIntent.putExtra("hasPre", true);
                }
                if (position >= mLessonDataList.size()) {
                    turtleIntent.putExtra("hasNext", false);
                } else {
                    turtleIntent.putExtra("hasNext", true);
                }
                turtleIntent.putExtra("position", position);
                turtleIntent.putExtra("cid", cid);
                turtleIntent.putExtra("name", name);
                turtleIntent.putExtra("command", command);
                turtleIntent.putExtra("content", content);
                turtleIntent.putExtra("lid", lid);
                turtleIntent.putExtra("tips", tips);
                turtleIntent.putExtra("remark", remark);
                startActivityForResult(turtleIntent, 100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            int position = data.getIntExtra("position", 0);
            if (position == 0) return;
            if (mLessonDataList == null) return;
            if (requestCode == 100 && (resultCode == 101 || resultCode == 102)) { // 上一页
                jumpToTurtle(position);
            }
        } else {
            if (mAdapter != null) {
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void onLoad() {
        if (Preferences.isDownLoadData() && mIndex != -1) {
            if (mLessonDataList == null) mLessonDataList = new ArrayList<>();
            if (lessonsManager != null && mLessonDataList.isEmpty()) {
                ArrayList temList = lessonsManager.getLesson(Long.parseLong(cid));
                if (temList.size() > 0) {
                    mLessonDataList.addAll(temList);
                    lvLesson.setEnable(false);
                    mIndex = -1;
                } else {
                    lessonsManager.deleteAll();
                    Preferences.saveIsDownLoadData(false);
                }
            }
            if (lvLesson != null) lvLesson.onLoadComplete();
            if (mAdapter != null) mAdapter.notifyDataSetChanged();
        } else {
            if (mIndex != -1) {
                getLessonData(cid);
            }
        }
    }

    private void getLessonData(String cid) {
        if (mProgressDialog != null && !mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        HttpClient.getLessonByCourse(cid, new HttpCallback<Lesson>() {
            @Override
            public void onSuccess(Lesson response) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                if (response == null) {
                    return;
                }
                lvLesson.onLoadComplete();
                mLessonDataList.addAll(response.getData());
                lvLesson.setEnable(false);
                mIndex = -1;
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFail(Exception e) {
                if (mProgressDialog != null && mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                }
                lvLesson.onLoadComplete();
                if (e instanceof RuntimeException) {
                    lvLesson.setEnable(false);
                    return;
                }
                ToastUtils.show(R.string.load_fail);
            }
        });
    }
}