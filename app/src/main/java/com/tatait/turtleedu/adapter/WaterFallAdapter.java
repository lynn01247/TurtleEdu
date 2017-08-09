package com.tatait.turtleedu.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.squareup.picasso.Picasso;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Info;
import com.tatait.turtleedu.model.Project;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.ScreenUtils;
import com.tatait.turtleedu.utils.ToastUtils;

import java.util.List;

public class WaterFallAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    //    private static final int[] ARR = new int[]{200, 300, 400};
    private Context mContext;
    private List<Project.ProjectData> mData; //定义数据源
    private OnItemClickListener mOnItemClickListener = null;

    //定义构造方法，默认传入上下文和数据源
    public WaterFallAdapter(Context context, List<Project.ProjectData> data) {
        mContext = context;
        mData = data;
    }

    @Override  //将ItemView渲染进来，创建ViewHolder
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item, null);
        view.setOnClickListener(this);
        return new MyViewHolder(view);
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    //define interface
    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override  //将数据源的数据绑定到相应控件上
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyViewHolder holder2 = (MyViewHolder) holder;

        //将position保存在itemView的Tag中，以便点击时进行获取
        holder2.itemView.setTag(position);

        final Project.ProjectData project = mData.get(position);
        if (project != null) {
            if (project.getCoverURL() != null && !"".equals(project.getCoverURL())) {
                Picasso.with(mContext.getApplicationContext()).load(project.getCoverURL()).error(R.drawable.load_error).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.userAvatar);
            } else {
                Picasso.with(mContext.getApplicationContext()).load(R.drawable.default_project).placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.userAvatar);
            }
//            int random = (int) (3 * Math.random());
//            holder2.userAvatar.getLayoutParams().height = ARR[random];
            holder2.userAvatar.getLayoutParams().height = 200;
            holder2.projectName.setText(project.getName());
            String score = "0";
            if (project.getScore() != null && !"".equals(project.getScore()) && !"0".equals(project.getScore())) {
                score = project.getScore();
            }
            holder2.projectScore.setText("得分：" + score);
            holder2.projectUserName.setText(project.getUserName());
            if (project.getUserImg() != null && !"".equals(project.getUserImg())) {
                Picasso.with(mContext.getApplicationContext()).load(project.getUserImg()).error(R.drawable.load_error).resize(ScreenUtils.dp2px(20), ScreenUtils.dp2px(20))
                        .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.projectUserImg);
            } else {
                Picasso.with(mContext.getApplicationContext()).load(R.drawable.default_user).resize(ScreenUtils.dp2px(20), ScreenUtils.dp2px(20))
                        .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.projectUserImg);
            }
            if ("true".equals(project.getHasPraised())) {
                Picasso.with(mContext.getApplicationContext()).load(R.drawable.project_good_count).resize(ScreenUtils.dp2px(20), ScreenUtils.dp2px(20))
                        .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.ivGoodCount);
                holder2.tvGoodCount.setTextColor(mContext.getApplicationContext().getResources().getColor(R.color.colorPrimary));
            } else {
                Picasso.with(mContext.getApplicationContext()).load(R.drawable.project_un_good_count).resize(ScreenUtils.dp2px(20), ScreenUtils.dp2px(20))
                        .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.ivGoodCount);
                holder2.tvGoodCount.setTextColor(mContext.getApplicationContext().getResources().getColor(R.color.grey));
            }
            String view = "1";
            if (project.getViewCount() != null && !"".equals(project.getViewCount()) && !"0".equals(project.getViewCount())) {
                view = project.getViewCount();
            }
            holder2.tvViewCount.setText(view);
            String talk = "0";
            if (project.getCommentCount() != null && !"".equals(project.getCommentCount()) && !"0".equals(project.getCommentCount())) {
                talk = project.getCommentCount();
            }
            holder2.tvTalkCount.setText(talk);
            String good = "0";
            if (project.getPraiseCount() != null && !"".equals(project.getPraiseCount()) && !"0".equals(project.getPraiseCount())) {
                good = project.getPraiseCount();
            }
            holder2.tvGoodCount.setText(good);
            String res = "";
            if (project.getUpdateTime() != null && !"".equals(project.getUpdateTime()) && !"0".equals(project.getUpdateTime())) {
                res = project.getUpdateTime();
            }
            holder2.projectTime.setText(res);
            holder2.llGoodCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Preferences.isLogin()) {
                        if("true".equals(project.getHasPraised())){
                            ToastUtils.show("您已点过赞啦!");
                            return;
                        }
                        String uid = Preferences.getUid();
                        String token = Preferences.getUserToken();
                        String pid = project.getPid();
                        HttpClient.postAddPraise(uid, token, pid, new HttpCallback<Info>() {
                            @Override
                            public void onSuccess(Info response) {
                                if (response != null) {
                                    holder2.tvGoodCount.setText((Integer.parseInt(project.getPraiseCount()) + 1) + "");
                                    Picasso.with(mContext.getApplicationContext()).load(R.drawable.project_good_count).resize(ScreenUtils.dp2px(20), ScreenUtils.dp2px(20))
                                            .placeholder(new ColorDrawable(Color.parseColor("#f5f5f5"))).into(holder2.ivGoodCount);
                                    holder2.tvGoodCount.setTextColor(mContext.getApplicationContext().getResources().getColor(R.color.colorPrimary));
                                    ToastUtils.show(response.getInfo());
                                }
                            }

                            @Override
                            public void onFail(Exception e) {
                            }
                        });
                    } else {
                        ToastUtils.show("登录后才能点赞哦！");
                        holder2.llGoodCount.setClickable(false);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                holder2.llGoodCount.setClickable(true);
                            }
                        }, 1000);
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    //定义自己的ViewHolder，将View的控件引用在成员变量上
    public class MyViewHolder extends RecyclerView.ViewHolder {
        private SimpleDraweeView userAvatar;
        private TextView projectName, projectScore, projectUserName, projectTime, tvViewCount, tvTalkCount, tvGoodCount;
        private LinearLayout llViewCount, llTalkCount, llGoodCount;
        private ImageView projectUserImg, ivViewCount, ivTalkCount, ivGoodCount;

        private MyViewHolder(View itemView) {
            super(itemView);
            userAvatar = (SimpleDraweeView) itemView.findViewById(R.id.user_avatar);
            projectName = (TextView) itemView.findViewById(R.id.project_name);
            projectScore = (TextView) itemView.findViewById(R.id.project_score);
            projectUserName = (TextView) itemView.findViewById(R.id.project_user_name);
            projectUserImg = (ImageView) itemView.findViewById(R.id.project_user_img);
            projectTime = (TextView) itemView.findViewById(R.id.project_time);
            llViewCount = (LinearLayout) itemView.findViewById(R.id.ll_view_count);
            ivViewCount = (ImageView) itemView.findViewById(R.id.iv_view_count);
            tvViewCount = (TextView) itemView.findViewById(R.id.tv_view_count);
            llTalkCount = (LinearLayout) itemView.findViewById(R.id.ll_talk_count);
            ivTalkCount = (ImageView) itemView.findViewById(R.id.iv_talk_count);
            tvTalkCount = (TextView) itemView.findViewById(R.id.tv_talk_count);
            llGoodCount = (LinearLayout) itemView.findViewById(R.id.ll_good_count);
            ivGoodCount = (ImageView) itemView.findViewById(R.id.iv_good_count);
            tvGoodCount = (TextView) itemView.findViewById(R.id.tv_good_count);
        }
    }
}
