package com.tatait.turtleedu.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * 项目表JavaBean
 * Created by Lynn on 2016/1/11.
 */
public class Project {
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
    // 总数
    @SerializedName("count")
    private String count;

    // 用户数据
    @SerializedName("data")
    public List<ProjectData> data;

    // 数据属性
    public static class ProjectData {
        // 项目id
        @SerializedName("pid")
        public String pid;
        // 用户ID
        @SerializedName("uid")
        public String uid;
        // 项目名称
        @SerializedName("name")
        public String name;
        // 项目代码
        @SerializedName("code")
        public String code;
        // 项目简介
        @SerializedName("summary")
        public String summary;
        // 项目受欢迎程度
        @SerializedName("score")
        public String score;
        // 项目预览图
        @SerializedName("coverURL")
        public String coverURL;
        // 项目评论数
        @SerializedName("commentCount")
        public String commentCount;
        // 项目点赞数
        @SerializedName("praiseCount")
        public String praiseCount;
        // 项目浏览量
        @SerializedName("viewCount")
        public String viewCount;
        // 项目创建时间
        @SerializedName("createTimeUnix")
        public String createTimeUnix;
        // 项目更新时间
        @SerializedName("updateTimeUnix")
        public String updateTimeUnix;
        // 项目更新时间
        @SerializedName("updateTime")
        public String updateTime;
        // 项目作者
        @SerializedName("userName")
        public String userName;
        // 项目作者头像
        @SerializedName("userImg")
        public String userImg;
        // 是否点赞过
        @SerializedName("hasPraised")
        public String hasPraised;
        // 图片的高度
        @SerializedName("imgHeight")
        public String imgHeight;

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getSummary() {
            return summary;
        }

        public void setSummary(String summary) {
            this.summary = summary;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        public String getCoverURL() {
            return coverURL;
        }

        public void setCoverURL(String coverURL) {
            this.coverURL = coverURL;
        }

        public String getCommentCount() {
            return commentCount;
        }

        public void setCommentCount(String commentCount) {
            this.commentCount = commentCount;
        }

        public String getPraiseCount() {
            return praiseCount;
        }

        public void setPraiseCount(String praiseCount) {
            this.praiseCount = praiseCount;
        }

        public String getViewCount() {
            return viewCount;
        }

        public void setViewCount(String viewCount) {
            this.viewCount = viewCount;
        }

        public String getCreateTimeUnix() {
            return createTimeUnix;
        }

        public void setCreateTimeUnix(String createTimeUnix) {
            this.createTimeUnix = createTimeUnix;
        }

        public String getUpdateTimeUnix() {
            return updateTimeUnix;
        }

        public void setUpdateTimeUnix(String updateTimeUnix) {
            this.updateTimeUnix = updateTimeUnix;
        }

        public String getImgHeight() {
            return imgHeight;
        }

        public void setImgHeight(String imgHeight) {
            this.imgHeight = imgHeight;
        }

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getUserImg() {
            return userImg;
        }

        public void setUserImg(String userImg) {
            this.userImg = userImg;
        }

        public String getHasPraised() {
            return hasPraised;
        }

        public void setHasPraised(String hasPraised) {
            this.hasPraised = hasPraised;
        }

        public String getUpdateTime() {
            return updateTime;
        }

        public void setUpdateTime(String updateTime) {
            this.updateTime = updateTime;
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

    public List<ProjectData> getData() {
        return data;
    }

    public void setData(List<ProjectData> data) {
        this.data = data;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }
}