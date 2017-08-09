package com.tatait.turtleedu.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tatait.turtleedu.DB.CourseManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.model.Course;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.binding.Bind;
import com.tatait.turtleedu.utils.binding.ViewBinder;

import java.util.List;

/**
 * 课程列表适配器
 * Created by Lynn on 2015/12/22.
 */
public class CourseAdapter extends BaseAdapter {
    private List<Course.CourseData> mData;
    private OnMoreClickListener mListener;
    private CourseManager courseManager;
    private Context mAppContext;

    public CourseAdapter(List<Course.CourseData> data, Context mContext) {
        this.mData = data;
        this.mAppContext = mContext;
        courseManager = CourseManager.getInstance(mContext);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_teach, parent, false);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        Course.CourseData courseData = mData.get(position);
        if (Preferences.isDownLoadData()) {
            holder.tv_study_num.setVisibility(View.VISIBLE);
            int isStudy = 0;
            if (courseData != null && courseManager != null && courseData.getCid() != null) {
                isStudy = courseManager.getStudyNum(Long.parseLong(courseData.getCid()));
            }
            holder.tv_study_num.setText("已学习" + isStudy + "/" + courseData.getCount());

        } else {
            holder.tv_study_num.setVisibility(View.GONE);
        }
        holder.tvName.setText(courseData.getName());
        String cid = courseData.getCid();
        String instruction = "第" + cid + "章节.";
        if ("".equals(cid)) instruction = "暂无课程简介";
        holder.tvInstruction.setText(instruction);
        holder.ivMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != mData.size() - 1;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        mListener = listener;
    }

    private static class ViewHolder {
        @Bind(R.id.tv_study_num)
        TextView tv_study_num;
        @Bind(R.id.iv_cover)
        ImageView ivCover;
        @Bind(R.id.tv_name)
        TextView tvName;
        @Bind(R.id.tv_instruction)
        TextView tvInstruction;
        @Bind(R.id.iv_more)
        ImageView ivMore;
        @Bind(R.id.v_divider)
        View vDivider;

        private ViewHolder(View view) {
            ViewBinder.bind(this, view);
        }
    }
}