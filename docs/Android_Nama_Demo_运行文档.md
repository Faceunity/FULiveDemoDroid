# Demo运行说明文档-Android

级别：Public 
更新日期：2020-01-19

------
### 最新更新内容：

2020-01-19 v6.6.0：

__版本整体说明：__SDK 6.6.0 主要针对美颜、美妆进行效果优化，性能优化，稳定性优化，同时新增部分特性，使得美颜、美妆效果进入行业顶尖水平。建议对美颜、美妆需求较高的B端用户更新SDK。

__注意!!!：__此版本由于底层替换原因，表情识别跟踪能力稍有降低，特别是Animoji、表情触发道具的整体表情表现力稍有减弱。Animoji的皱眉、鼓嘴、嘟嘴等动作表现效果比之较差，表情触发道具的发怒（皱眉）、鼓嘴、嘟嘴的表情触发道具较难驱动。其余ARMesh、哈哈镜、明星换脸、动态人像（活照片）的面部跟踪整体稍有10%的效果减弱。故用到表情驱动的功能重度B端用户，仍建议使用SDK6.4.0版，使用其余功能（美颜叠加贴纸等其余功能）的场景不受影响，表情识别跟踪能力将在下一版进行优化更新。

- 美颜优化：
  1). 新增美型6款功能，包括开眼角、眼距、眼睛角度、长鼻、缩人中、微笑嘴角。
   2). 新增17款滤镜，其中包含8款自然系列滤镜、8款质感灰系列滤镜、1款个性滤镜。
   3). 优化美颜中亮眼、美牙效果。
   4). 优化美颜中3个脸型，调整优化使得V脸、窄脸、小脸效果更自然。
- 美妆优化：
  1). 新增13套自然系组合妆，13套组合妆是滤镜+美妆的整体效果，可自定义。
   2). 新增3款口红质地：润泽、珠光、咬唇。
   3). 提升美妆点位准确度 ，人脸点位由209点增加至 239点。
   4). 优化美妆素材叠加方式，使得妆容效果更加服帖自然。
   5). 优化粉底效果，更加贴合人脸轮廓。
- 提升人脸点位跟踪灵敏度，快速移动时跟踪良好，使美颜美妆效果跟随更紧密。
- 提升人脸点位的稳定性，解决了半张脸屏幕、大角度、遮挡等场景的阈值抖动问题，点位抖动问题也明显优化。
- 提升人脸跟踪角度，人脸最大左右偏转角提升至70度，低抬头检测偏转角也明显提升。
- 架构升级，支持底层AI算法能力和业务逻辑拆分，优化性能，使得系统更加容易扩展和更新迭代：
  1). 新增加接口 fuLoadAIModelFromPackage 用于加载AI能力模型。
   2). 新增加接口 fuReleaseAIModel 用于释放AI能力模型。
   3). 新增加接口 fuIsAIModelLoaded 判断AI能力是否已经加载。

__注1__：从SDK 6.6.0 开始，为了更新以及迭代更加方便，由原先一个nama.so拆分成两个库nama.so以及fuai.so，其中nama.so为轻量级渲染引擎，fuai.so为算法引擎。升级6.6.0时，需添加fuai库。

__注2__:  更新SDK 6.6.0 时，在fuSetup之后，需要马上调用 fuLoadAIModelFromPackage 加载 ai_faceprocessor.bundle !!!

__注3__:  SDK 6.6.0 进行较大的架构调整 , 架构上拆分底层算法能力和业务场景，使得SDK更能够按需复用算法模块，节省内存开销，算法能力模块后期更容易维护升级，使用方式详见新增加的一组接口定义 fuLoadAIModelFromPackage / fuReleaseAIModel / fuIsAIModelLoaded。

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