# Demo运行说明文档-Android

级别：Public 
更新日期：2019-06-27

------
### 最新更新内容：

2019-06-27 v6.2.0：

- 优化人脸检测，提高正脸大角度检测率。

- 优化背景分割，提高边缘稳定性。

- 优化手势识别，提供15种手势；手势模型支持独立运行，见FUCreator文档。

- 优化人脸美妆，提高准确度，支持更丰富的效果。

- 修复多人脸舌头跟踪相互影响问题。 

- 修复avatar模式下fxaa抗锯齿失效问题。

- 废弃高精度模型 armesh_ex.bundle，以及对应的接口 fuLoadExtendedARData。

- 废弃人脸表情动画模型 anim_model.bundle, 以及对应的接口 fuLoadAnimModel。

  注：废弃的数据以及接口，可能引起编译不通过，移除代码即可。

文档：

- [美颜道具功能文档](./美颜道具功能文档.md)
- [美妆道具功能文档](./美妆道具功能文档.md)
- [海报换脸功能文档](./海报换脸功能文档.md)
- [表情动图功能文档](./表情动图功能文档.md)
- [质感美颜功能文档](./质感美颜功能文档.md)

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
  +app 			      // app 模块
    +src
      +main
        +assets       // Assets 文件
          +image      // 原始图片
        +effects      // 道具文件
          +bundle     // bundle 文件
          +res        // icon
        +java         // Java 源码
        +makeup       // 美妆资源
          +material   // 五官美妆贴纸
          +res        // icon
        +poster       // 海报换脸
        +res          // App 资源文件
  +faceunity          // faceunity 模块
    +libs             // nama.jar 文件
    +src
      +main
        +assets       // Nama 资源文件     
        +java         // Java 源码
        +jniLibs      // Nama so 文件
  +docs		    	  // 文档目录
  +README.md	 	  // 工程说明文档
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