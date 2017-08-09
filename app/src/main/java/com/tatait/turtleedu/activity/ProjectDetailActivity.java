package com.tatait.turtleedu.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.github.lzyzsd.jsbridge.BridgeWebView;
import com.github.lzyzsd.jsbridge.BridgeWebViewClient;
import com.github.lzyzsd.jsbridge.CallBackFunction;
import com.github.lzyzsd.jsbridge.DefaultHandler;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Info;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.StringUtils;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;

import java.lang.reflect.Method;

public class ProjectDetailActivity extends BaseActivity implements View.OnClickListener {
    @Bind(R.id.toolbar)
    private Toolbar toolbar;
    @Bind(R.id.turtle_input_command)
    private EditText inputCommand;
    @Bind(R.id.turtle_command_tips)
    private TextView turtle_command_tips;
    @Bind(R.id.btn_tips)
    private Button btn_tips;
    @Bind(R.id.btn_command)
    private Button btn_command;
    @Bind(R.id.btn_input)
    private Button btn_input;
    @Bind(R.id.btn_stop)
    private Button btn_stop;
    @Bind(R.id.btn_reset)
    private Button btn_reset;
    @Bind(R.id.webView)
    private BridgeWebView mWebView;
    private ProgressDialog mProgressDialog;
    private String pid, code, tips;

    private StringBuffer tipsSb = new StringBuffer();
    private StringBuffer commandSb = new StringBuffer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_detail);
        init();
        initWebView();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.initing));

        pid = getIntent().getStringExtra("pid");
        code = getIntent().getStringExtra("code");

        String name = getIntent().getStringExtra("name");
        String userName = getIntent().getStringExtra("userName");
        String updateTime = getIntent().getStringExtra("updateTime");

        tips = name + "(作者：" + userName + ")\n" + updateTime;
        tipsSb.append(getString(R.string.o_inteduce)).append("\n").append(tips);
        commandSb.append(getString(R.string.o_command)).append("\n").append(code);
        inputCommand.setText(code);
        btn_input.setText(R.string.order_play);
        setTitle(name);
        hideSoftKeyboard();
        if (pid != null && !"".equals(pid)) viewThisProject();
    }

    private void viewThisProject() {
        HttpClient.postViewProject(pid, new HttpCallback<Info>() {
            @Override
            public void onSuccess(Info response) {
                if (response != null) {
                    ToastUtils.show(response.getInfo());
                }
            }

            @Override
            public void onFail(Exception e) {
            }
        });
    }

    @Override
    protected void setListener() {
        btn_tips.setOnClickListener(this);
        btn_command.setOnClickListener(this);
        btn_input.setOnClickListener(this);
        btn_stop.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
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
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.hengping) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else if (id == R.id.shuping) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.project_detail, menu);
        MenuItem hengping = menu.findItem(R.id.hengping);
        MenuItem shuping = menu.findItem(R.id.shuping);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            hengping.setVisible(false);
            shuping.setVisible(true);
        } else {
            hengping.setVisible(true);
            shuping.setVisible(false);
        }
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_tips:
                turtle_command_tips.setText(tipsSb);
                break;
            case R.id.btn_command:
                turtle_command_tips.setText(commandSb);
                break;
            case R.id.btn_stop:
                mWebView.loadUrl("javascript:stopbyinde()");
                break;
            case R.id.btn_input:
                hideSoftKeyboard();
                if (inputCommand != null && StringUtils.isEmpty2(inputCommand.getText().toString())) {
                    inputCommand.setText(code);
                    btn_input.setText(R.string.order_play);
                } else if (inputCommand != null) {
                    // 执行命令
                    String command = inputCommand.getText().toString();
                    if (command.contains("\n")) {
                        command = command.replaceAll("\\n", "\n ");
                    }
                    mWebView.loadUrl("javascript:nativebyindex('" + command + "')");
                    // 显示或隐藏按钮
                    if (btn_reset != null) btn_reset.setVisibility(View.VISIBLE);
                    if (btn_stop != null) btn_stop.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.btn_reset:
                hideSoftKeyboard();
                inputCommand.setText("");
                tipsSb.setLength(0);
                commandSb.setLength(0);
                btn_input.setText(R.string.order_input);
                tipsSb.append(getString(R.string.o_inteduce)).append("\n").append(tips);
                commandSb.append(getString(R.string.o_command)).append("\n").append(code);
                turtle_command_tips.setText(R.string.o_area);
                mWebView.loadUrl("javascript:resetbyindex()");
                break;
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
                    if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT // 竖屏状态
                            && !Preferences.isNeverTipScreen()) {
                        AlertDialog.Builder changeDialog = new AlertDialog.Builder(ProjectDetailActivity.this);
                        changeDialog.setTitle(R.string.advice_heng);
                        changeDialog.setMessage(getString(R.string.confirm_tip_screen));
                        changeDialog.setNegativeButton(R.string.keep_shu, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Preferences.saveNeverTipScreen(true);
                            }
                        });
                        changeDialog.setPositiveButton(R.string.change_heng, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                            }
                        });
                        changeDialog.show();
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
}