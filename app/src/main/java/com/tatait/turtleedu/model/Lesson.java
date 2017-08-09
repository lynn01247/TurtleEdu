package com.tatait.turtleedu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 课程表JavaBean
 * Created by Lynn on 2016/1/11.
 */
public class Lesson {
    // 回参code
    @SerializedName("code")
    private String code;
    // 接口信息
    @SerializedName("info")
    private String info;
    // 请求当前页
    @SerializedName("page")
    private String page;
    // 请求每页数
    @SerializedName("pageSize")
    private String pageSize;
    // 请求总页数
    @SerializedName("total")
    private String total;

    // 用户数据
    @SerializedName("data")
    private List<LessonData> data;

    // 数据属性
    public static class LessonData {
        // 课程id
        @SerializedName("cid")
        public String cid;
        // 课时id
        @SerializedName("lid")
        public String lid;
        // 课时名称
        @SerializedName("name")
        public String name;
        // 课时内容
        @SerializedName("content")
        public String content;
        // 课时命令
        @SerializedName("command")
        public String command;
        // 课时提示
        @SerializedName("tips")
        public String tips;
        // 课时备注
        @SerializedName("remark")
        public String remark;
        // 课时是否学习过
        @SerializedName("is_study")
        public String is_study;

        public String getCid() {
            return cid;
        }

        public void setCid(String cid) {
            this.cid = cid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLid() {
            return lid;
        }

        public void setLid(String lid) {
            this.lid = lid;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String getTips() {
            return tips;
        }

        public void setTips(String tips) {
            this.tips = tips;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public String getIs_study() {
            return is_study;
        }

        public void setIs_study(String is_study) {
            this.is_study = is_study;
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public List<LessonData> getData() {
        return data;
    }

    public void setData(List<LessonData> data) {
        this.data = data;
    }
}