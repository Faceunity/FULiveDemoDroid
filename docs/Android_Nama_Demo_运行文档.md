# Demo运行说明文档-Android

级别：Public 
更新日期：2019-09-25

------
### 最新更新内容：

2019-09-25 v6.4.0：

- 新增美体瘦身功能，支持瘦身、长腿、美臀、细腰、肩部调整，一键美体。
- 优化美颜功能中精细磨皮，性能以及效果提升，提升皮肤细腻程度，更好保留边缘细节。
- 优化美发功能，边缘稳定性及性能提升。
- 优化美妆功能，性能提升，CPU占有率降低，Android中低端机表现明显。
- 优化手势识别功能，性能提升，CPU占有率降低，在Android机型表现明显。
- 修复人脸检测多人脸偶现crash问题。
- 修复捏脸功能中模型截断问题。
- 关闭美颜道具打印冗余log。

文档：

- [美颜道具功能文档](./美颜道具功能文档.md)
- [美妆道具功能文档](./美妆道具功能文档.md)
- [海报换脸功能文档](./海报换脸功能文档.md)
- [表情动图功能文档](./表情动图功能文档.md)
- [质感美颜功能文档](./质感美颜功能文档.md)
- [美体道具功能文档](./美体道具功能文档.md)

工程案例更新：

- 美颜道具部分接口已改变，请注意同步代码
- 舌头跟踪相关请查看本文档及代码注释
- anim_model.bundle以及ardata_ex.bundle已弃用，请删除相关数据及代码

------
### 目录：

本文档内容目录：

[TOC]

------
### 1. 简介

本文档旨在说明如何将Faceunity Nama SDK的 Android Demo运行起来，体验Faceunity Nama SDK的功能。

FULiveDemoDroid 是 Android 平台上，集成相芯人脸跟踪及视频特效开发包（简 Nama SDK）的集成示例。

集成了 Faceunity 面部跟踪、美颜、Animoji、道具贴纸、AR面具、换脸、表情识别、音乐滤镜、背景分割、手势识别、哈哈镜、人像光照以及人像驱动等功能。

Demo新增了一个展示Faceunity产品列表的主界面，新版Demo将根据客户证书权限来控制用户可以使用哪些产品。  

------
### 2. Demo文件结构

本小节，描述Demo文件结构，各个目录，以及重要文件的功能。

```
+FULiveDemoDroid
  +app 			                           // app 模块
    +src
      +main
        +assets                            
          +avatar                          // Avatar 捏脸
            +avatar_hair                   // Avatar 头发道具
            -avatar_background.bundle      // Avatar 背景道具
            -avatar_head.bundle            // Avatar 头部道具
            -avatar_color.json             // 颜色配置
          +beautify_body                   // 美体
            -BodySlim.bundle               // 美体道具
          +beautify_face                   // 美颜
            -face_beautification.bundle    // 美颜道具
          +cartoon_filter                  // 卡通滤镜
            -fuzzytoonfilter.bundle        // 卡通滤镜道具
          +change_face                     // 海报换脸
            +template_xx                   // 模板资源
            -change_face.bundle            // 海报换脸道具
          +effect                          // 各种道具
            +animoji                       // Animoji
            +ar                            // AR 面具
            +background                    // 背景分割
            +beautify_hair                 // 美发
            +change                        // 换脸
            +expression                    // 表情识别
            +facewarp                      // 哈哈镜
            +gesture                       // 手势识别
            +musicfilter                   // 音乐滤镜
            +normal                        // 道具贴纸
            +portrait_drive                // 人像驱动
          +image                           // 图片
          +light_makeup                    // 轻美妆
            +blusher...                    // 腮红等资源
            -light_makeup.bundle           // 轻美妆道具
          +live_photo                      // 表情动图
            +resource_xx                   // 五官资源
            +template_xx                   // 模板资源
            -photolive.bundle              // 表情动图道具
          +makeup                          // 美妆
            +combination_xx                // 组合妆资源
            +common_resource               // 通用妆容资源
            -face_makeup.bundle            // 美妆道具
            -makeup_color_setup.json       // 颜色配置
            -new_face_tracker.bundle       // 75+134 点位人脸识别道具
        +java                              // Java 源码
        +res                               // App 资源文件

  +faceunity                               // faceunity 模块
    +libs                                  
      -nama.jar                            // nama.jar
    +src
      +main
        +assets                           
          -fxaa.bundle                     // 3D 绘制抗锯齿数据包
          -tongue.bundle                   // 舌头道具
          -v3.bundle                       // 人脸识别核心数据包
        +java                              // Java 源码
        +jniLibs                           // Nama so 库
  +docs		    	                       // 开发文档目录
  +README.md	 	                       // 工程说明文档
```

------

### 3. 运行Demo 

#### 3.1 开发环境
##### 3.1.1 支持平台
```
Android API 18 及以上，GLES 2.0 及以上
```
##### 3.1.2 开发环境
```
Android Studio 3.0 及以上
```

#### 3.2 准备工作 

- 下载 [FULiveDemoDroid](https://github.com/Faceunity/FULiveDemoDroid) 工程
- 获取证书:
  1. 拨打电话 **0571-88069272** 
  2. 发送邮件至 **marketing@faceunity.com** 进行咨询。

#### 3.3 相关配置

Android 端发放的证书为 authpack.java 文件，如果您已经获取到鉴权证书，将证书文件放到工程中 faceunity 模块 com.faceunity.fulivedemo 包下即可。

#### 3.4 编译运行

- 点击 Sync 按钮，同步一下工程。或者 Build-->Make Projects。

![AS-Make-Project](imgs/as-make-project.png)

- 点击 Run 按钮运行，部署到手机上。

![AS-Run](imgs/as-run.png)

- Demo 运行效果。

![fulivedemo](imgs/fulivedemo.png)

------
### 4. 常见问题 

如有使用问题，请联系技术支持。