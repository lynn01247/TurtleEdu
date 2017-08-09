package com.tatait.turtleedu.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.tatait.turtleedu.DB.TempManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.model.Temp;
import com.tatait.turtleedu.utils.ToastUtils;

import java.util.List;

public class TempAdapter extends RecyclerView.Adapter implements View.OnClickListener {

    private Context mContext;
    private List<Temp> mData; //定义数据源
    private OnItemClickListener mOnItemClickListener = null;
    private TempManager tempManager;

    //定义构造方法，默认传入上下文和数据源
    public TempAdapter(Context context, List<Temp> data) {
        mContext = context;
        mData = data;
        tempManager = TempManager.getInstance(context.getApplicationContext());
    }

    @Override  //将ItemView渲染进来，创建ViewHolder
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.temp_recyclerview_item, null);
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final MyViewHolder holder2 = (MyViewHolder) holder;
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder2.itemView.setTag(position);
        final Temp temp = mData.get(position);
        if (temp != null) {
            holder2.projectName.setText("".equals(temp.getTname()) ? "未命名" : temp.getTname());
            holder2.projectCode.setText(temp.getCode());
            holder2.projectTime.setText(temp.getUpdate_time());
            holder2.tempDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder cleanDialog = new AlertDialog.Builder(mContext);
                    cleanDialog.setMessage("是否删除该草稿项目");
                    cleanDialog.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mData.size() > 0 && mData.get(position) != null) {
                                tempManager.deleteById(temp.getTid());
                                mData.remove(position);
                                notifyDataSetChanged();
                                ToastUtils.show("删除成功");
                            }
                        }
                    });
                    cleanDialog.setNegativeButton(R.string.cancel, null);
                    cleanDialog.show();
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
    private class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView projectName, projectCode, projectTime;
        private ImageView tempDel;

        private MyViewHolder(View itemView) {
            super(itemView);
            projectName = (TextView) itemView.findViewById(R.id.project_name);
            projectCode = (TextView) itemView.findViewById(R.id.project_code);
            projectTime = (TextView) itemView.findViewById(R.id.project_time);
            tempDel = (ImageView) itemView.findViewById(R.id.temp_del);
        }
    }
}
