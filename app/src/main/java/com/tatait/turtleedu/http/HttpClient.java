package com.tatait.turtleedu.http;

import com.tatait.turtleedu.model.Course;
import com.tatait.turtleedu.model.Info;
import com.tatait.turtleedu.model.Lesson;
import com.tatait.turtleedu.model.Project;
import com.tatait.turtleedu.model.User;
import com.tatait.turtleedu.utils.Preferences;
import com.zhy.http.okhttp.OkHttpUtils;

import okhttp3.Call;

/**
 * Created by Lynn on 2017/2/8.
 */
public class HttpClient {
    private static final String API_URL = "https://tatait.leanapp.cn/";
    private static final String METHOD_GET_COURSE = "getCourse";
    private static final String METHOD_GET_LESSON_BY_COURSE = "getLessonByCourse";
    private static final String METHOD_GET_ALL_PROJECT = "getAllProject";
    private static final String METHOD_GET_MY_PROJECT = "getMyProject";
    private static final String METHOD_GET_REGISTER = "registerUser";
    private static final String METHOD_GET_LOGIN = "loginUser";
    private static final String METHOD_POST_FEEK_BACK = "addFeekBack";
    private static final String METHOD_POST_VIEW_PROJECT = "viewProject";
    private static final String METHOD_POST_ADD_PRAISE = "addPraise";
    private static final String METHOD_POST_ADD_PROJECT = "addProject";
    private static final String PARAM_PASSWORD = "password";
    private static final String PARAM_MOBILE = "mobile";
    private static final String PARAM_IMGURL = "imgurl";
    private static final String PARAM_FROM = "from";
    private static final String PARAM_COVER_URL = "coverURL";
    private static final String PARAM_CODE = "code";
    private static final String PARAM_NAME = "name";
    private static final String PARAM_PID = "pid";
    private static final String PARAM_ID = "id";
    private static final String PARAM_UID = "uid";
    private static final String PARAM_PAGE_INDEX = "pageIndex";
    private static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String PARAM_CONTENT = "content";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_SORT = "sort";

    public static void getCourse(int index, int size, final HttpCallback<Course> callback) {
        OkHttpUtils.get().url(API_URL + METHOD_GET_COURSE)
                .addParams(PARAM_PAGE_INDEX, Integer.valueOf(index).toString())
                .addParams(PARAM_PAGE_SIZE, Integer.valueOf(size).toString())
                .build()
                .execute(new JsonCallback<Course>(Course.class) {
                    @Override
                    public void onResponse(Course response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getLessonByCourse(String cid, final HttpCallback<Lesson> callback) {
        OkHttpUtils.get().url(API_URL + METHOD_GET_LESSON_BY_COURSE)
                .addParams(PARAM_ID, cid)
                .build()
                .execute(new JsonCallback<Lesson>(Lesson.class) {
                    @Override
                    public void onResponse(Lesson response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getAllProject(String uid, String pageIndex, String pageSize, String sort, final HttpCallback<Project> callback) {
        OkHttpUtils.get().url(API_URL + METHOD_GET_ALL_PROJECT)
                .addParams(PARAM_UID, uid)
                .addParams(PARAM_PAGE_INDEX, pageIndex)
                .addParams(PARAM_PAGE_SIZE, pageSize)
                .addParams(PARAM_SORT, sort)
                .build()
                .execute(new JsonCallback<Project>(Project.class) {
                    @Override
                    public void onResponse(Project response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getMyProject(String uid, String token, String pageIndex, String pageSize, String sort, final HttpCallback<Project> callback) {
        OkHttpUtils.get().url(API_URL + METHOD_GET_MY_PROJECT)
                .addParams(PARAM_UID, uid)
                .addParams(PARAM_TOKEN, token)
                .addParams(PARAM_PAGE_INDEX, pageIndex)
                .addParams(PARAM_PAGE_SIZE, pageSize)
                .addParams(PARAM_SORT, sort)
                .build()
                .execute(new JsonCallback<Project>(Project.class) {
                    @Override
                    public void onResponse(Project response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getRegisterUser(String userName, String passwWord, final HttpCallback<User> callback) {
        OkHttpUtils.get().url(API_URL + METHOD_GET_REGISTER)
                .addParams(PARAM_NAME, userName)
                .addParams(PARAM_PASSWORD, passwWord)
                .addParams(PARAM_MOBILE, "1")
                .addParams(PARAM_IMGURL, "http://omzogcv8w.bkt.clouddn.com/turtleUser_" + ((int) (Math.random() * 50) + 1) + ".png")
                .addParams(PARAM_FROM, "TataMusic")
                .build()
                .execute(new JsonCallback<User>(User.class) {
                    @Override
                    public void onResponse(User response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void getLoginUser(String userName, String passwWord, final HttpCallback<User> callback) {
        OkHttpUtils.get().url(API_URL + METHOD_GET_LOGIN)
                .addParams(PARAM_NAME, userName)
                .addParams(PARAM_PASSWORD, passwWord)
                .build()
                .execute(new JsonCallback<User>(User.class) {
                    @Override
                    public void onResponse(User response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }

    public static void postFeekBack(String content, String phone, final HttpCallback<Info> callback) {
        OkHttpUtils.post().url(API_URL + METHOD_POST_FEEK_BACK)
                .addParams(PARAM_CONTENT, content + "【TurtleEdu:" + phone + "】")
                .addParams(PARAM_UID, Preferences.getUid())
                .addParams(PARAM_TOKEN, Preferences.getUserToken())
                .build()
                .execute(new JsonCallback<Info>(Info.class) {
                    @Override
                    public void onResponse(Info response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void postNewPraise(String uid, String token,String title,String code,String coverURL,final HttpCallback<Info> callback) {
        OkHttpUtils.post().url(API_URL + METHOD_POST_ADD_PROJECT)
                .addParams(PARAM_UID, uid)
                .addParams(PARAM_TOKEN, token)
                .addParams(PARAM_NAME, title)
                .addParams(PARAM_CODE, code)
                .addParams(PARAM_COVER_URL, coverURL)
                .build()
                .execute(new JsonCallback<Info>(Info.class) {
                    @Override
                    public void onResponse(Info response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void postViewProject(String pid, final HttpCallback<Info> callback) {
        OkHttpUtils.post().url(API_URL + METHOD_POST_VIEW_PROJECT)
                .addParams(PARAM_PID, pid)
                .build()
                .execute(new JsonCallback<Info>(Info.class) {
                    @Override
                    public void onResponse(Info response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
    public static void postAddPraise(String uid, String token,String pid,final HttpCallback<Info> callback) {
        OkHttpUtils.post().url(API_URL + METHOD_POST_ADD_PRAISE)
                .addParams(PARAM_PID, pid)
                .addParams(PARAM_UID, uid)
                .addParams(PARAM_TOKEN, token)
                .build()
                .execute(new JsonCallback<Info>(Info.class) {
                    @Override
                    public void onResponse(Info response, int id) {
                        callback.onSuccess(response);
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        callback.onFail(e);
                    }

                    @Override
                    public void onAfter(int id) {
                        callback.onFinish();
                    }
                });
    }
}