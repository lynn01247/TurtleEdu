package com.tatait.turtleedu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 课程表JavaBean
 * Created by Lynn on 2016/1/11.
 */
public class Course {
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
    private List<CourseData> data;

    // 数据属性
    public static class CourseData {
        // 课程id
        @SerializedName("cid")
        public String cid;
        // 课程名称
        @SerializedName("name")
        public String name;
        // 课程描述
        @SerializedName("instruction")
        public String instruction;
        // 课程数量
        @SerializedName("count")
        public String count;
        // 课程描是否学习过
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

        public String getInstruction() {
            return instruction;
        }

        public void setInstruction(String instruction) {
            this.instruction = instruction;
        }

        public String getCount() {
            return count;
        }

        public void setCount(String count) {
            this.count = count;
        }

        public String getIs_study() {
            return is_study;
        }

        public void setIs_study(String is_study) {
            this.is_study = is_study;
        }

        public CourseData(String _id, String _name, String _count, String _is_study) {
            this.cid = _id;
            this.name = _name;
            this.count = _count;
            this.is_study = _is_study;
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

    public List<CourseData> getData() {
        return data;
    }

    public void setData(List<CourseData> data) {
        this.data = data;
    }
}