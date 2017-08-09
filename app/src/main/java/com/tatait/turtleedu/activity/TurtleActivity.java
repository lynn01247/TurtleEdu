package com.tatait.turtleedu.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.tatait.turtleedu.DB.CourseManager;
import com.tatait.turtleedu.DB.LessonsManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.StringUtils;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;

public class TurtleActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.rl_turtle_all)
    private RelativeLayout rl_turtle_all;
    @Bind(R.id.turtle_content)
    private TextView mTurtleContent;
    @Bind(R.id.tv_show_more)
    private TextView tvShowMore;
    @Bind(R.id.ll_text)
    private LinearLayout ll_text;
    @Bind(R.id.ll_complete)
    private LinearLayout ll_complete;
    @Bind(R.id.ll_teach_complete)
    private LinearLayout ll_teach_complete;
    @Bind(R.id.iv_mission_teach)
    private ImageView iv_mission_teach;
    @Bind(R.id.tv_mission_teach)
    private TextView tv_mission_teach;
    @Bind(R.id.iv_mission_tips)
    private ImageView iv_mission_tips;
    @Bind(R.id.tv_mission_tips)
    private TextView tv_mission_tips;
    @Bind(R.id.iv_mission_command)
    private ImageView iv_mission_command;
    @Bind(R.id.tv_mission_command)
    private TextView tv_mission_command;
    @Bind(R.id.rl_left)
    private RelativeLayout mRlLeft;
    @Bind(R.id.rl_right)
    private RelativeLayout mRlRight;
    @Bind(R.id.show_left)
    private RelativeLayout mShowLeft;
    @Bind(R.id.show_right)
    private RelativeLayout mShowRight;
    @Bind(R.id.show_more)
    private RelativeLayout mShowMore;
    @Bind(R.id.spread)
    private ImageView mImageSpread;
    @Bind(R.id.shrink_up)
    private ImageView mImageShrinkUp;
    @Bind(R.id.turtle_input_command)
    private EditText inputCommand;
    @Bind(R.id.turtle_command_tips)
    private TextView turtle_command_tips;
    @Bind(R.id.turtle_tips)
    private TextView turtle_tips;
    @Bind(R.id.turtle_up)
    private TextView turtle_up;
    @Bind(R.id.turtle_down)
    private TextView turtle_down;
    @Bind(R.id.btn_tips)
    private Button btn_tips;
    @Bind(R.id.btn_command)
    private Button btn_command;
    @Bind(R.id.btn_history)
    private Button btn_history;
    @Bind(R.id.btn_input)
    private Button btn_input;
    @Bind(R.id.btn_reset)
    private Button btn_reset;
    @Bind(R.id.webView)
    private BridgeWebView mWebView;
    private ProgressDialog mProgressDialog;
    private String cid, lid, tips, command;
    private int position;
    private LessonsManager lessonsManager;
    private CourseManager courseManager;

    private static final int VIDEO_CONTENT_DESC_MAX_LINE = 2; // 默认展示最大行数2行
    private static final int SHRINK_UP_STATE = 1;             // 收起状态
    private static final int SPREAD_STATE = 2;                // 展开状态
    private static int mState = SHRINK_UP_STATE;              //默认收起状态

    private StringBuffer tipsSb = new StringBuffer();
    private StringBuffer commandSb = new StringBuffer();
    private StringBuffer historySb = new StringBuffer();
    private boolean clickHistory = false;

    private String completeNum = "000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_turtle);
        init();
        initWebView();
    }

    private void init() {
        courseManager = CourseManager.getInstance(getApplicationContext());
        lessonsManager = LessonsManager.getInstance(getApplicationContext());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.initing));
        position = getIntent().getIntExtra("position", 0);
        cid = getIntent().getStringExtra("cid");
        lid = getIntent().getStringExtra("lid");
        String title = getIntent().getStringExtra("name");
        String content = getIntent().getStringExtra("content");
        command = getIntent().getStringExtra("command");
        tips = getIntent().getStringExtra("tips");
        boolean hasPre = getIntent().getBooleanExtra("hasPre", false);
        boolean hasNext = getIntent().getBooleanExtra("hasNext", false);

        if (hasPre) {
            turtle_up.setVisibility(View.VISIBLE);
        } else {
            turtle_up.setVisibility(View.GONE);
        }
        if (hasNext) {
            turtle_down.setVisibility(View.VISIBLE);
        } else {
            turtle_down.setVisibility(View.GONE);
        }

        mTurtleContent.setText(content);
        if (StringUtils.isEmpty2(tips)) {
            btn_tips.setVisibility(View.INVISIBLE);
        } else {
            btn_tips.setVisibility(View.VISIBLE);
        }

        tipsSb.append(getString(R.string.o_tips)).append("\n").append(tips);
        commandSb.append(getString(R.string.o_command)).append("\n").append(command);
        historySb.append(getString(R.string.o_history)).append("\n");
        setTitle(title);
        // 缓存本地后，加载本地学习进度
        if (Preferences.isDownLoadData()) {
            completeNum = lessonsManager.getStudyState(Long.parseLong(cid), Long.parseLong(lid));
            if (completeNum != null && !"000".equals(completeNum)) {
                if ("1".equals(completeNum.substring(0, 1))) {
                    // 设置完成任务一
                    iv_mission_teach.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                    // 设置字体灰色
                    tv_mission_teach.setTextColor(getResources().getColor(R.color.grey_0));
                    // 设置中划线并加清晰
                    tv_mission_teach.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                }
                if ("1".equals(completeNum.substring(1, 2))) {
                    // 设置完成任务二
                    iv_mission_tips.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                    // 设置字体灰色
                    tv_mission_tips.setTextColor(getResources().getColor(R.color.grey_0));
                    // 设置中划线并加清晰
                    tv_mission_tips.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    turtle_command_tips.setText(tipsSb);
                }
                if ("1".equals(completeNum.substring(2, 3))) {
                    // 设置完成任务三
                    iv_mission_command.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                    // 设置字体灰色
                    tv_mission_command.setTextColor(getResources().getColor(R.color.grey_0));
                    // 设置中划线并加清晰
                    tv_mission_command.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                }
            }
        } else {
            ll_teach_complete.setVisibility(View.GONE);
        }
        hideSoftKeyboard();
    }

    @Override
    protected void setListener() {
        rl_turtle_all.setOnClickListener(this);
        mRlLeft.setOnClickListener(this);
        mRlRight.setOnClickListener(this);
        mShowLeft.setOnClickListener(this);
        mShowRight.setOnClickListener(this);
        mShowMore.setOnClickListener(this);
        ll_teach_complete.setOnClickListener(this);
        btn_tips.setOnClickListener(this);
        btn_command.setOnClickListener(this);
        btn_history.setOnClickListener(this);
        btn_input.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        turtle_up.setOnClickListener(this);
        turtle_down.setOnClickListener(this);
        inputCommand.addTextChangedListener(new TextWatcher() {
            private CharSequence temp;//监听前的文本

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (temp.length() > 0) {
                    btn_input.setText(R.string.order_play);
                } else {
                    btn_input.setText(R.string.order_input);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_turtle_all:
                hideSoftKeyboard();
                break;
            case R.id.show_more: {
                if (mState == SPREAD_STATE) {
                    mTurtleContent.setMaxLines(VIDEO_CONTENT_DESC_MAX_LINE);
                    tvShowMore.setText(R.string.spress);
                    mImageShrinkUp.setVisibility(View.GONE);
                    mImageSpread.setVisibility(View.VISIBLE);
                    mState = SHRINK_UP_STATE;
                    // 区域显隐
                    if (Preferences.isDownLoadData()) {// 判断是否有本地进度
                        ll_teach_complete.setVisibility(View.VISIBLE);
                    } else {
                        ll_teach_complete.setVisibility(View.GONE);
                    }
                    ll_text.setVisibility(View.VISIBLE);
                    ll_complete.setVisibility(View.GONE);
                    mRlLeft.setVisibility(View.GONE);
                    mRlRight.setVisibility(View.GONE);
                } else if (mState == SHRINK_UP_STATE) {
                    mTurtleContent.setMaxLines(Integer.MAX_VALUE);
                    tvShowMore.setText(R.string.spress_no);
                    mImageShrinkUp.setVisibility(View.VISIBLE);
                    mImageSpread.setVisibility(View.GONE);
                    mState = SPREAD_STATE;
                    // 区域显隐
                    ll_teach_complete.setVisibility(View.GONE);
                    ll_text.setVisibility(View.VISIBLE);
                    ll_complete.setVisibility(View.GONE);
                    mRlLeft.setVisibility(View.GONE);
                    mRlRight.setVisibility(View.GONE);
                    if (Preferences.isDownLoadData()) {// 判断是否有本地进度
                        // 设置完成任务一
                        iv_mission_teach.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                        // 设置字体灰色
                        tv_mission_teach.setTextColor(getResources().getColor(R.color.grey_0));
                        // 设置中划线并加清晰
                        tv_mission_teach.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                        setIsStudy(1);
                    }
                }
                break;
            }
            case R.id.btn_tips:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                if (Preferences.isDownLoadData()) {// 判断是否有本地进度
                    // 设置完成任务二
                    iv_mission_tips.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                    // 设置字体灰色
                    tv_mission_tips.setTextColor(getResources().getColor(R.color.grey_0));
                    // 设置中划线并加清晰
                    tv_mission_tips.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);

                    setIsStudy(2);
                }
                turtle_command_tips.setText(tipsSb);
                break;
            case R.id.btn_command:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                if (Preferences.isDownLoadData()) {// 判断是否有本地进度
                    // 设置完成任务二
                    iv_mission_tips.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                    // 设置字体灰色
                    tv_mission_tips.setTextColor(getResources().getColor(R.color.grey_0));
                    // 设置中划线并加清晰
                    tv_mission_tips.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                    setIsStudy(2);
                }
                turtle_command_tips.setText(commandSb);
                break;
            case R.id.btn_history:
                if (clickHistory) {
                    historySb.setLength(0);
                    historySb.append(getString(R.string.o_history)).append("\n");
                } else {
                    clickHistory = true;
                    btn_history.setText(R.string.clear_history);
                }
                turtle_command_tips.setText(historySb);
                break;
            case R.id.btn_input:
                clickHistory = false;
                btn_history.setText(R.string.input_history);

                hideSoftKeyboard();
                if (inputCommand != null && StringUtils.isEmpty2(inputCommand.getText().toString())) {
                    inputCommand.setText(command);
                    btn_input.setText(R.string.order_play);
                } else if (inputCommand != null) {
                    // 生成输入历史
                    historySb.append(inputCommand.getText()).append("\n");
                    if (turtle_command_tips != null) turtle_command_tips.setText(historySb);
                    // 执行命令
                    mWebView.loadUrl("javascript:nativebyindex('" + inputCommand.getText() + "')");
                    // 显示或隐藏按钮
                    if (btn_reset != null) btn_reset.setVisibility(View.VISIBLE);
                    if (btn_history != null) btn_history.setVisibility(View.VISIBLE);
                    if (turtle_tips != null) turtle_tips.setVisibility(View.VISIBLE);
                    // 收起内容教学区
                    if (Preferences.isDownLoadData()) {// 判断是否有本地进度
                        ll_text.setVisibility(View.GONE);
                        ll_complete.setVisibility(View.GONE);
                        mRlLeft.setVisibility(View.VISIBLE);
                        mRlRight.setVisibility(View.VISIBLE);

                        // 设置完成任务三
                        iv_mission_command.setImageDrawable(getResources().getDrawable(R.drawable.complete));
                        // 设置字体灰色
                        tv_mission_command.setTextColor(getResources().getColor(R.color.grey_0));
                        // 设置中划线并加清晰
                        tv_mission_command.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
                        setIsStudy(3);
                    } else {
                        ll_text.setVisibility(View.GONE);
                        ll_complete.setVisibility(View.GONE);
                        mRlLeft.setVisibility(View.VISIBLE);
                        mRlRight.setVisibility(View.GONE);
                    }
                }
                break;
            case R.id.btn_reset:
                clickHistory = false;
                btn_history.setText(R.string.input_history);

                hideSoftKeyboard();
                inputCommand.setText("");
                tipsSb.setLength(0);
                commandSb.setLength(0);
                btn_input.setText(R.string.order_input);
                tipsSb.append(getString(R.string.o_tips)).append("\n").append(tips);
                commandSb.append(getString(R.string.o_command)).append("\n").append(command);
                turtle_command_tips.setText(R.string.o_area);
                mWebView.loadUrl("javascript:resetbyindex()");
                break;
            case R.id.show_left:
                ll_text.setVisibility(View.GONE);
                ll_complete.setVisibility(View.GONE);
                mRlLeft.setVisibility(View.VISIBLE);
                if (Preferences.isDownLoadData()) {// 判断是否有本地进度
                    mRlRight.setVisibility(View.VISIBLE);
                } else {
                    mRlRight.setVisibility(View.GONE);
                }
                if (turtle_tips != null) turtle_tips.setVisibility(View.VISIBLE);
                break;
            case R.id.show_right:
                ll_text.setVisibility(View.GONE);
                ll_complete.setVisibility(View.GONE);
                mRlLeft.setVisibility(View.VISIBLE);
                mRlRight.setVisibility(View.VISIBLE);
                if (turtle_tips != null) turtle_tips.setVisibility(View.VISIBLE);
                break;
            case R.id.rl_left:
                ll_text.setVisibility(View.VISIBLE);
                ll_complete.setVisibility(View.GONE);
                mRlLeft.setVisibility(View.GONE);
                mRlRight.setVisibility(View.GONE);
                if (turtle_tips != null) turtle_tips.setVisibility(View.GONE);
                break;
            case R.id.rl_right:
                ll_text.setVisibility(View.GONE);
                ll_complete.setVisibility(View.VISIBLE);
                mRlLeft.setVisibility(View.GONE);
                mRlRight.setVisibility(View.GONE);
                if (turtle_tips != null) turtle_tips.setVisibility(View.GONE);
                break;
            case R.id.ll_teach_complete:
                ll_text.setVisibility(View.GONE);
                ll_complete.setVisibility(View.VISIBLE);
                mRlLeft.setVisibility(View.GONE);
                mRlRight.setVisibility(View.GONE);
                if (turtle_tips != null) turtle_tips.setVisibility(View.GONE);
                break;
            case R.id.turtle_up:
                AlertDialog.Builder upDialog = new AlertDialog.Builder(this);
                upDialog.setMessage(getResources().getString(R.string.confirm_change, "上"));
                upDialog.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentUp = new Intent();
                        intentUp.putExtra("position", position - 1);
                        setResult(101, intentUp);
                        onBackPressed();
                    }
                });
                upDialog.setNegativeButton(R.string.cancel, null);
                upDialog.show();
                break;
            case R.id.turtle_down:
                AlertDialog.Builder downDialog = new AlertDialog.Builder(this);
                downDialog.setMessage(getResources().getString(R.string.confirm_change, "下"));
                downDialog.setPositiveButton(R.string.jump, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intentUp = new Intent();
                        intentUp.putExtra("position", position + 1);
                        setResult(102, intentUp);
                        onBackPressed();
                    }
                });
                downDialog.setNegativeButton(R.string.cancel, null);
                downDialog.show();
                break;
        }
    }

    private void setIsStudy(int num) {
        if (num == 1 && !"1".equals(completeNum.substring(0, 1))) {
            completeNum = replaceIndex(0, completeNum, "1");
        } else if (num == 2 && !"1".equals(completeNum.substring(1, 2))) {
            completeNum = replaceIndex(1, completeNum, "1");
        } else if (num == 3 && !"1".equals(completeNum.substring(2, 3))) {
            completeNum = replaceIndex(2, completeNum, "1");
        }
        // 完成任务(只有下载过离线课程的才会记录)
        if (Preferences.isDownLoadData() && lessonsManager != null) {
            // 该课时未完成，则更新该课时的学习状态
            lessonsManager.update(Long.parseLong(cid), Long.parseLong(lid), completeNum);// 设置学习进度
            if (courseManager != null) {
                // 该课时如果全部学完，才刷新学习数目
                if (lessonsManager.isStudy(Long.parseLong(cid), Long.parseLong(lid))) {
                    int isStudy = lessonsManager.getStudyNum(Long.parseLong(cid));
                    courseManager.update(Long.parseLong(cid), Integer.valueOf(isStudy).toString());// 设置为已学习数目
                }
            }
        }
    }

    private void initWebView() {
        // 设置具体WebViewClient
        mWebView.setWebViewClient(new MyWebViewClient(mWebView));
        // set HadlerCallBack
        mWebView.setDefaultHandler(new myHadlerCallBack());
        // setWebChromeClient
        mWebView.setWebChromeClient(new WebChromeClient() {
            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType, String capture) {
                this.openFileChooser(uploadMsg);
            }

            @SuppressWarnings("unused")
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String AcceptType) {
                this.openFileChooser(uploadMsg);
            }

            private void openFileChooser(ValueCallback<Uri> uploadMsg) {
            }
        });
        mWebView.loadUrl("file:///android_asset/jslogo/index.html");
        if (mProgressDialog != null) {
            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }
            }, 1000);
        }
    }

    /**
     * 自定义的WebViewClient
     */
    private class MyWebViewClient extends BridgeWebViewClient {
        private MyWebViewClient(BridgeWebView webView) {
            super(webView);
        }
    }

    /**
     * 自定义回调
     */
    private class myHadlerCallBack extends DefaultHandler {
        @Override
        public void handler(String data, CallBackFunction function) {
            if (function != null) {
                ToastUtils.show(data);
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(500);
        finish();
    }

    /**
     * 替换固定位置的字符
     */
    private static String replaceIndex(int index, String res, String str) {
        return res.substring(0, index) + str + res.substring(index + 1);
    }
}