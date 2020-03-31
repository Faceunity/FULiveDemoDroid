# Demo运行说明文档-Android

级别：Public 

更新日期：2020-03-19

SDK版本: 6.7.0

------
### 最新更新内容：

**2020-3-19 v6.7.0:**

1. 美颜效果优化

   - 新增去黑眼圈、去法令纹功能
   - 解决瘦脸边缘变形问题（边缘剪裁）
   - 老美型效果调优
   - 提供美颜推荐参数（6套）

2. 解决表情跟踪问题（解决了6.6版表情系数问题及表情灵活度问题）—— FaceProcessor模块优化

   - 解决舌头灵敏度，达到原有SDK6.4.0效果版本
   - 解决Animoji、表情识别灵活度问题，达到原有SDK6.4.0效果版本
   - 解决优化了表情动图的鼻子跟踪效果问题

3. 美妆效果优化，点位优化（点位不准，后续优化点位准确性）

   - 优化口红点位与效果，解决张嘴、正脸、低抬头、左右转头、抿嘴动作的口红溢色
   - 优化美瞳点位效果，是美瞳效果稳定
   - 腮红效果优化，解决了仰头角度下腮红强拉扯问题

4. NAMA整体性能优化

   - 算法部分：优化FaceProcessor模块性能

5. Nama库大小裁剪优化
6. 交互端曝光聚焦效果优化——对标抖音效果
7. 新增判断是否完成初始化接口
8. 添加fuRenderToTexture接口在api说明文档

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
            -avatarHairX.bundle            // Avatar 头发道具
            -avatar_background.bundle      // Avatar 背景道具
            -avatar_head.bundle            // Avatar 头部道具
            -avatar_color.json             // 颜色配置
          +cartoon_filter                  // 卡通滤镜
            -fuzzytoonfilter.bundle        // 卡通滤镜道具
          +change_face                     // 海报换脸
            +template_xx                   // 模板资源
            -change_face.bundle            // 海报换脸道具
          +effect                          // 各种道具
            +animoji                       // Animoji
            +ar                            // AR 面具
            +background                    // 背景分割
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
            +combination_bundle            // 组合妆 bundle 资源
            +config_json                   // 组合妆 json 资源
            +item_bundle                   // 美妆子妆 bundle 资源
            -color_setup.json              // 颜色配置
        +java                              // Java 源码
        +res                               // App 资源文件

  +faceunity                               // faceunity 模块
    +libs                                  
      -nama.jar                            // nama.jar
    +src
      +main
        +assets
          +AI_model                        // AI 能力模型
            -ai_bgseg.bundle               // 背景分割AI能力模型
            -ai_bgseg_green.bundle         // 绿幕背景分割AI能力模型
            -ai_face_processor.bundle      // 人脸面具及人脸面罩AI能力模型，需要默认加载
            -ai_hairseg.bundle             // 头发分割AI能力模型
            -ai_gesture.bundle             // 手势识别AI能力模型
            -ai_facelandmarks75.bundle     // 脸部特征点75点AI能力模型
 			-ai_facelandmarks209.bundle    // 脸部特征点209点AI能力模型
			-ai_facelandmarks239.bundle    // 脸部特征点239点AI能力模型
			-ai_humanpose.bundle           // 人体2D点位AI能力模型
			-tongue.bundle                 // 舌头跟踪数据包
          -body_slim.bundle                // 美体道具
          -face_beautification.bundle      // 美颜道具
          -face_makeup.bundle              // 美妆道具
          -fxaa.bundle                     // 3D 绘制抗锯齿数据包
          -hair_gradient.bundle            // 美发渐变色道具
          -hair_normal.bundle              // 美发正常色道具
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