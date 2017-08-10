# TurtleEdu

### 海龟之家，专注学习海龟语言（Logo），紧跟教程，快速学习，分享成功  
### 基于Logo语言，结合webView与JS的交互，寓教于乐的编程教学

## 效果图

### 整体效果

<img src="/screen_pic/pic.png" title="" width="270" height="486" /> <br>
<img src="/screen_pic/pic6.png" title="" width="270" height="486" /> <br>
<img src="/screen_pic/1.gif" title="" width="270" height="486" /> <br>

### 下载离线课程

<img src="/screen_pic/pic1.png" title="" width="270" height="486" /> <br>
<img src="/screen_pic/pic2.png" title="" width="270" height="486" /> <br>
<img src="/screen_pic/3.gif" title="" width="270" height="486" /> <br>

### 观看教学课程或者项目广场别人共享的项目

<img src="/screen_pic/pic4.png" title="" width="270" height="486" /> <br>
<img src="/screen_pic/4.gif" title="" width="270" height="486" /> <br>

### 自己的草稿箱

<img src="/screen_pic/pic5.png" title="" width="270" height="486" /> <br>

### 项目横竖屏切换

<img src="/screen_pic/5.gif" title="" width="270" height="486" /> <br>

## 数据来源API接口 ##
### 1. 数据是自己写的后台，后台搭建在LeanCloud【非广告：这是免费的轻量级后台管理维护系统，可以基于Java、PHP等语言，具体信息请移步官网：https://leancloud.cn/】

部分接口示例:【更多信息，可以访问我当时开发另一个项目使用的API文档，本项目也是挂载在这个数据库里（懒得新建啦 ^_^ ）https://lynn01247.gitbooks.io/turtle_api_1-0/content/】
-----------------------------------------------------------------------------
// 根据page获取全部笔记信息
$turtle_api->get('/getAllProject', function(Request $request, Response $response) {
    // 校验当前登陆用户id字段
    if(isset($_REQUEST['uid']) && !empty($_REQUEST['uid'])) {
        $uid = $_REQUEST['uid'];
    }else{
      return json_encode(array(
        "code" => 0,
        "info" => "当前登陆用户id不能为空!",
        "data" => null
      ));
    }
    ...
    ...
    if($count > 0){// 获取全部项目信息
        ...
        ...
        return json_encode(array(
            "code"     => 1,
            "info"     => "查询项目成功!",
            "data"     => $data,
            "page"     => $pageIndex,
            "pageSize" => $pageSize,
            "count"    => $count,
            "total"    => ceil($count / $pageSize)
        ));
    }else{
        return json_encode(array(
            "code" => 0,
            "info" => "未查询到任何项目!",
            "data" => null
        ));
    }
});
-----------------------------------------------------------------------------
## 功能点记录：
- Material Design 页面跳转动画，触摸响应和共享元素转场动画
- 侧边栏动画效果：ActionBarDrawerToggle、DrawerLayout、ToolBar 的结合
- RecycleView + SwipeRefreshLayout 下拉刷新官方控件，自动触底加载更多数据
- DB SQLit 轻量级数据库存储数据
- 基于webView的项目截图与本地保存，并上传到七牛远程服务进行文件存储
- DrawerLayout 实现抽屉菜单
- Navigation 实现抽屉左边的导航
- ToolBar 实现沉浸式布局
- okhttp 封装请求 异步获取 Json数据
- Piccaso 图片自适应框架
- 多dimens 适配大部分系统
- CircleView 圆形头像
- 支持自定义登录：自主的注册登录接口，可以进行密码重置等操作
- 支持自定义分享【短信、邮件、文本、链接等 】

## 部分依赖的开源库和工具

下拉刷新上拉加载[SwipeRefreshLayout](参考官网，具体可以看干货：http://www.jianshu.com/p/d23b42b6360b)

网络请求[okhttp](技术博客:http://blog.csdn.net/lmj623565791/article/details/47911083)

强大的图片处理工具[picasso](https://github.com/square/picasso)
