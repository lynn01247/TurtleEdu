package com.tatait.turtleedu.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.webkit.WebView;
import android.webkit.WebViewClient;
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
import com.qiniu.android.common.Zone;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.tatait.turtleedu.DB.TempManager;
import com.tatait.turtleedu.R;
import com.tatait.turtleedu.http.HttpCallback;
import com.tatait.turtleedu.http.HttpClient;
import com.tatait.turtleedu.model.Info;
import com.tatait.turtleedu.model.Temp;
import com.tatait.turtleedu.utils.Preferences;
import com.tatait.turtleedu.utils.QiNiuAuth;
import com.tatait.turtleedu.utils.StringUtils;
import com.tatait.turtleedu.utils.ToastUtils;
import com.tatait.turtleedu.utils.binding.Bind;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewProjectActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "NewProjectActivity";
    @Bind(R.id.rl_new_project_click)
    private RelativeLayout rl_new_project_click;
    @Bind(R.id.toolbar)
    private Toolbar toolbar;
    @Bind(R.id.turtle_input_command)
    private EditText inputCommand;
    @Bind(R.id.turtle_command_tips)
    private TextView turtle_command_tips;
    @Bind(R.id.new_project_title)
    private TextView new_project_title;
    @Bind(R.id.new_project_tip)
    private TextView new_project_tip;
    @Bind(R.id.iv_new_project_tip)
    private ImageView iv_new_project_tip;
    @Bind(R.id.btn_history)
    private Button btn_history;
    @Bind(R.id.btn_just_save)
    private Button btn_just_save;
    @Bind(R.id.btn_save_temp)
    private Button btn_save_temp;
    @Bind(R.id.btn_input)
    private Button btn_input;
    @Bind(R.id.btn_reset)
    private Button btn_reset;
    @Bind(R.id.btn_submit)
    private Button btn_submit;
    @Bind(R.id.ll_btn)
    private LinearLayout ll_btn;
    @Bind(R.id.ll_submit_btn)
    private LinearLayout ll_submit_btn;
    @Bind(R.id.ll_temp_btn)
    private LinearLayout ll_temp_btn;
    @Bind(R.id.new_project_title2)
    private EditText new_project_title2;
    @Bind(R.id.new_project_temp_title)
    private EditText new_project_temp_title;
    @Bind(R.id.btn_up)
    private Button btn_up;
    @Bind(R.id.btn_temp_up)
    private Button btn_temp_up;
    @Bind(R.id.btn_submit2)
    private Button btn_submit2;
    @Bind(R.id.btn_temp_submit)
    private Button btn_temp_submit;
    @Bind(R.id.webView)
    private BridgeWebView mWebView;
    private ProgressDialog mProgressDialog, mSubmitProgressDialog, mUploadPicProgressDialog;

    private StringBuffer historySb = new StringBuffer();
    private boolean clickHistory = false;
    private static final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 200;

    private boolean isTempSave = false;
    private TempManager tempManager;
    private int tempId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_project);
        init();
        requestPermission();
        initWebView();
    }

    private void init() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(getString(R.string.initing));
        mSubmitProgressDialog = new ProgressDialog(this);
        mSubmitProgressDialog.setMessage(getString(R.string.uploading));
        mUploadPicProgressDialog = new ProgressDialog(this);
        mUploadPicProgressDialog.setMessage(getString(R.string.pic_uploading));

        tempManager = TempManager.getInstance(getApplicationContext());
        setTitle("新建项目");
        String tid = getIntent().getStringExtra("tid");
        String tname = getIntent().getStringExtra("tname");
        String code = getIntent().getStringExtra("code");
        String history = getIntent().getStringExtra("history");
//        String updateTime = getIntent().getStringExtra("updateTime");
        if(!StringUtils.isNullOrEmpty(tname)){
            tempId = Integer.parseInt(tid);
            turtle_command_tips.setText(history);
            inputCommand.setText(code);
            btn_input.setText(R.string.order_play);
        }
        historySb.append(getString(R.string.o_history)).append("\n");

        if (!"".equals(Preferences.getInputCommand())) {
            AlertDialog.Builder cleanDialog = new AlertDialog.Builder(NewProjectActivity.this);
            cleanDialog.setMessage(getResources().getString(R.string.confirm_return_just_save));
            cleanDialog.setPositiveButton(R.string.just_save_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    turtle_command_tips.setText(Preferences.getHistoryCommand());
                    inputCommand.setText(Preferences.getInputCommand());
                    btn_input.setText(R.string.order_play);
                }
            });
            cleanDialog.setNegativeButton(R.string.cancel, null);
            cleanDialog.show();
        } else {
            btn_input.setText(R.string.order_input);
        }
        hideSoftKeyboard();
    }

    @Override
    protected void setListener() {
        btn_history.setOnClickListener(this);
        new_project_title.setOnClickListener(this);
        btn_just_save.setOnClickListener(this);
        btn_input.setOnClickListener(this);
        btn_reset.setOnClickListener(this);
        rl_new_project_click.setOnClickListener(this);
        // 提交
        btn_submit.setOnClickListener(this);
        btn_up.setOnClickListener(this);
        btn_submit2.setOnClickListener(this);
        // 草稿箱
        btn_save_temp.setOnClickListener(this);
        btn_temp_up.setOnClickListener(this);
        btn_temp_submit.setOnClickListener(this);
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
            case R.id.rl_new_project_click:
                hideSoftKeyboard();
                break;
            case R.id.btn_history:
                hideSoftKeyboard();
                if (clickHistory) {
                    historySb.setLength(0);
                    historySb.append(getString(R.string.o_history)).append("\n");
                } else {
                    clickHistory = true;
                    btn_history.setText(R.string.clear_history);
                }
                turtle_command_tips.setText(historySb);
                break;
            case R.id.new_project_title:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                if (new_project_tip.getVisibility() == View.VISIBLE) {
                    new_project_tip.setVisibility(View.GONE);
                    iv_new_project_tip.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.up_angle));
                } else {
                    new_project_tip.setVisibility(View.VISIBLE);
                    iv_new_project_tip.setImageDrawable(getApplicationContext().getResources().getDrawable(R.drawable.down_angle));
                }
                break;
            case R.id.btn_just_save: // 暂存
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                AlertDialog.Builder cleanDialog = new AlertDialog.Builder(NewProjectActivity.this);
                cleanDialog.setMessage(getResources().getString(R.string.confirm_just_save));
                cleanDialog.setPositiveButton(R.string.just_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Preferences.saveInputCommand(inputCommand.getText().toString());
                        Preferences.saveHistoryCommand(historySb.toString());
                        ToastUtils.show("数据已暂存");
                    }
                });
                cleanDialog.setNegativeButton(R.string.cancel, null);
                cleanDialog.show();
                break;
            case R.id.btn_temp_submit: // 保存草稿
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                final String input = inputCommand.getText().toString();
                if (StringUtils.isNullOrEmpty(input)) {
                    ToastUtils.show("本项目没有有效输入指令，无法保存草稿箱");
                    return;
                }
                if (isTempSave && tempId != -1) {
                    AlertDialog.Builder tempDialog = new AlertDialog.Builder(NewProjectActivity.this);
                    tempDialog.setMessage("本次临时项目已有草稿范本，是否更新保存最新数据？");
                    tempDialog.setPositiveButton("更新保存", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            tempManager.update(Long.parseLong(tempId + ""), new_project_temp_title.getText().toString(), input, historySb.toString());
                        }
                    });
                    tempDialog.setNegativeButton(R.string.cancel, null);
                    tempDialog.show();
                } else {
                    isTempSave = true;
                    tempId = tempManager.getTempCount();
                    Temp temp = new Temp();
                    temp.setTid(Integer.valueOf(tempId).toString());
                    temp.setCode(input);
                    temp.setTname(new_project_temp_title.getText().toString());
                    temp.setHistory(historySb.toString());
                    SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//设置日期格式
                    String date = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
                    temp.setUpdate_time(date);
                    tempManager.addTemp(temp);
                    ToastUtils.show("成功保存到草稿！");
                }
                break;
            case R.id.btn_input:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                if (inputCommand != null && !StringUtils.isEmpty2(inputCommand.getText().toString())) {
                    // 执行命令
                    String command = inputCommand.getText().toString();
                    historySb.append(command).append("\n");
                    turtle_command_tips.setText(historySb);

                    String commandStr = command;
                    if (command.contains("\n")) {
                        commandStr = command.replaceAll("\\n", "\n ");
                    }
                    mWebView.loadUrl("javascript:nativebyindex('" + commandStr + "')");
                    // 显示或隐藏按钮
                    if (btn_reset != null) btn_reset.setVisibility(View.VISIBLE);
                    if (btn_submit != null) btn_submit.setVisibility(View.VISIBLE);
                } else {
                    ToastUtils.show("请输入指令！");
                }
                break;
            case R.id.btn_reset:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();
                historySb.setLength(0);
                inputCommand.setText("");
                btn_input.setText(R.string.order_input);
                historySb.append(getString(R.string.o_history)).append("\n");
                turtle_command_tips.setText(R.string.o_area);
                mWebView.loadUrl("javascript:resetbyindex()");
                break;
            case R.id.btn_submit:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                // 提交，进入下一步
                ll_btn.setVisibility(View.GONE);
                ll_submit_btn.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_up:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                // 返回上一步
                ll_btn.setVisibility(View.VISIBLE);
                ll_submit_btn.setVisibility(View.GONE);
                break;
            case R.id.btn_save_temp:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                ll_btn.setVisibility(View.GONE);
                ll_temp_btn.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_temp_up:
                clickHistory = false;
                btn_history.setText(R.string.input_history);
                hideSoftKeyboard();

                // 返回上一步
                ll_btn.setVisibility(View.VISIBLE);
                ll_temp_btn.setVisibility(View.GONE);
                break;
            case R.id.btn_submit2:
                hideSoftKeyboard();
                if (Preferences.isLogin()) {
                    if (new_project_title2 != null && !StringUtils.isEmpty2(new_project_title2.getText().toString())) {
                        AlertDialog.Builder submitDialog = new AlertDialog.Builder(NewProjectActivity.this);
                        submitDialog.setMessage("是否提交该项目？");
                        submitDialog.setPositiveButton("提交", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String title = new_project_title2.getText().toString();
                                Bitmap bitmap = captureWebView(mWebView);
                                String file = saveToSD(bitmap, title);
                                if ("".equals(file)) {
                                    ToastUtils.show("项目效果图未生成！");
                                } else {
                                    if (inputCommand != null && !"".equals(inputCommand.getText().toString())) {
                                        uploadQiNiu(file, title);
                                    } else {
                                        ToastUtils.show("项目的指令不能为空！");
                                    }
                                }
                            }
                        });
                        submitDialog.setNegativeButton(R.string.cancel, null);
                        submitDialog.show();
                    } else {
                        ToastUtils.show("请输入项目名称！");
                        return;
                    }
                } else {
                    ToastUtils.show("您的登录状态已失效，请重新登录！");
                    return;
                }
                break;
        }
    }

    private void uploadQiNiu(String fileName, final String title) {
        if (mUploadPicProgressDialog != null && !mUploadPicProgressDialog.isShowing()) {
            mUploadPicProgressDialog.show();
        }
        // 上传图片到七牛
        QiNiuAuth qiNiuAuth = QiNiuAuth.create("Wlh1v5gsbScAqNjd71CQy9IWYevfmFq-txvrtR10", "UYEkE2JYYC2z3drpR9EHVOz9yELIFO0I_BRpKlsx");
        String up_token = qiNiuAuth.uploadToken("turtle-img:" + title);
        // 重用uploadManager。一般地，只需要创建一个uploadManager对象
        com.qiniu.android.storage.Configuration config = new com.qiniu.android.storage.Configuration.Builder()
                .chunkSize(256 * 1024)  //分片上传时，每片的大小。 默认256K
                .putThreshhold(512 * 1024)  // 启用分片上传阀值。默认512K
                .connectTimeout(10) // 链接超时。默认10秒
                .responseTimeout(60) // 服务器响应超时。默认60秒
                .recorder(null)  // recorder分片上传时，已上传片记录器。默认null
                .recorder(null, null)  // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .zone(Zone.zone2) // 设置区域，指定不同区域的上传域名、备用域名、备用IP。
                .build();
        UploadManager uploadManager = new UploadManager(config);
        File data = new File(fileName);
        uploadManager.put(data, title, up_token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        if (mUploadPicProgressDialog != null && mUploadPicProgressDialog.isShowing()) {
                            mUploadPicProgressDialog.dismiss();
                        }
                        //res包含hash、key等信息，具体字段取决于上传策略的设置
                        if (info.isOK()) {
                            Message message = new Message();
                            message.obj = title;
                            message.what = 0x50001;
                            mHandler.handleMessage(message);
                        } else {
                            Log.i("qiniu", "Upload Fail");
                            //如果失败，这里可以把info信息上报自己的服务器，便于后面分析上传错误原因
                            ToastUtils.show("图片上传失败！请稍候再试！失败原因：" + info.error);
                        }
                        Log.i("qiniu", key + ",\r\n " + info + ",\r\n " + res);
                    }
                }, null);
    }

    /**
     * 回调函数，用于异步刷新主界面的UI或者弹出提示
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x50001:
                    String title = msg.obj == null ? "no_pic.jpg" : msg.obj.toString();
                    // 上传项目到服务器
                    String code = inputCommand.getText().toString();
                    submit(title, code, "http://opikgg887.bkt.clouddn.com/" + title);
                    break;
            }
        }
    };

    private void initWebView() {
        /**
         * 当系统版本大于5.0时 开启enableSlowWholeDocumentDraw 获取整个html文档内容
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mWebView.enableSlowWholeDocumentDraw();
        }
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
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setSupportZoom(true); //支持缩放
        mWebView.requestFocusFromTouch();
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
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
     * 当build target为23时，需要动态申请权限
     */
    private void requestPermission() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 200:
                boolean writeAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                Log.d(TAG, "writeAcceped--" + writeAccepted);
                break;

        }
    }

    /**
     * 获取项目截图
     */
    public static Bitmap captureWebView(WebView webView) {
        webView.destroyDrawingCache();
        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();
        Bitmap bmp = webView.getDrawingCache();
//        Picture snapShot = webView.capturePicture();
//        Bitmap bmp = Bitmap.createBitmap(snapShot.getWidth(), snapShot.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(bmp);
//        snapShot.draw(canvas);
//        canvas.save();
//        canvas.restore();
//        //      webView.dispatchDraw(canvas);
//        webView.destroyDrawingCache();
        return bmp;
    }

    /**
     * 存储到sdcard
     */
    public static String saveToSD(Bitmap bmp, String title) {
        //判断sd卡是否存在
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            //文件名
            Long timestamp = new Date().getTime();
            String filePath = Environment.getExternalStorageDirectory().getPath() + "/TurtleEdu_Pic/";
            File fileExist = new File(filePath);
            //如果文件夹不存在则创建
            if (!fileExist.exists() && !fileExist.isDirectory()) {
                fileExist.mkdir();
            }
            String uid = Preferences.getUid();
            String fileName = filePath + uid + title + timestamp + ".jpg";

            File file = new File(fileName);
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i(TAG, "file path:" + file.getAbsolutePath());
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(file);
                if (fos != null) {
                    //第一参数是图片格式，第二参数是图片质量，第三参数是输出流
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.flush();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return fileName;
        }
        return "";
    }

    /**
     * 上传提交项目
     */
    private void submit(String title, String code, String coverURL) {
        if (mSubmitProgressDialog != null && !mSubmitProgressDialog.isShowing()) {
            mSubmitProgressDialog.show();
        }
        String uid = Preferences.getUid();
        String token = Preferences.getUserToken();
        HttpClient.postNewPraise(uid, token, title, code, coverURL, new HttpCallback<Info>() {
            @Override
            public void onSuccess(Info response) {
                if (mSubmitProgressDialog != null && mSubmitProgressDialog.isShowing()) {
                    mSubmitProgressDialog.dismiss();
                }
                if (response == null) {
                    return;
                } else {
                    ToastUtils.show(response.getInfo());
                    if ("1".equals(response.getCode())) {
                        AlertDialog.Builder submitDialog = new AlertDialog.Builder(NewProjectActivity.this);
                        submitDialog.setMessage("是否跳转到【我的项目】中预览？");
                        submitDialog.setPositiveButton("去预览", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent projectIntent = new Intent(NewProjectActivity.this, ProjectActivity.class);
                                projectIntent.putExtra("type", "mine");
                                startActivity(projectIntent);
                                finish();
                            }
                        });
                        submitDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        submitDialog.show();
                    }
                }
            }

            @Override
            public void onFail(Exception e) {
                if (mSubmitProgressDialog != null && mSubmitProgressDialog.isShowing()) {
                    mSubmitProgressDialog.dismiss();
                }
                ToastUtils.show(R.string.load_fail);
            }
        });
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
        if (inputCommand != null && !"".equals(inputCommand.getText().toString())) {
            AlertDialog.Builder cleanDialog = new AlertDialog.Builder(NewProjectActivity.this);
            cleanDialog.setMessage("是否确定退出编辑？您也可以使用【暂存】功能暂存数据哦！");
            cleanDialog.setPositiveButton("暂存本次数据后退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences.saveInputCommand(inputCommand.getText().toString());
                    Preferences.saveHistoryCommand(historySb.toString());
                    setResult(500);
                    finish();
                }
            });
            cleanDialog.setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setResult(500);
                    finish();
                }
            });
            cleanDialog.show();
        } else {
            setResult(500);
            finish();
        }
    }
}