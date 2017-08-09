package com.tatait.turtleedu.model;

import com.google.gson.annotations.SerializedName;

/**
 * 草稿箱JavaBean
 * Created by Lynn on 2017/1/11.
 */
public class Temp {
    // 序号
    @SerializedName("tid")
    private String tid;
    // 标题
    @SerializedName("tname")
    private String tname;
    // 代码
    @SerializedName("code")
    private String code;
    // 历史
    @SerializedName("history")
    private String history;
    // 历史
    @SerializedName("update_time")
    private String update_time;

    public Temp() {
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getTname() {
        return tname;
    }

    public void setTname(String tname) {
        this.tname = tname;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public Temp(String _id, String _name, String _code, String _history, String _update_time) {
        this.tid = _id;
        this.tname = _name;
        this.code = _code;
        this.history = _history;
        this.update_time = _update_time;
    }
}