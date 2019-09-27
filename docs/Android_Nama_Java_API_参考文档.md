# Android Nama Java API 参考文档

级别：Public
更新日期：2019-09-25
SDK版本: 6.4.0

------
### 最新更新内容：

2019-09-25 v6.4.0:

1. v6.4.0 接口无变动。

------

### 目录：

本文档内容目录：

[TOC]

------

### 1. 简介

本文是相芯人脸跟踪及视频特效开发包（以下简称 Nama SDK）的底层接口文档。该文档中的 Nama API 为底层 native 接口，可以直接用于 PC/iOS/Android NDK/Linux 上的开发。其中，iOS和Android平台上的开发可以利用SDK的应用层接口（Objective-C/Java），相比本文中的底层接口会更贴近平台相关的开发经验。

SDK相关的所有调用要求在同一个线程中顺序执行，不支持多线程。少数接口可以异步调用（如道具加载），会在备注中特别注明。SDK所有主线程调用的接口需要保持 OpenGL context 一致，否则会引发纹理数据异常。如果需要用到SDK的绘制功能，则主线程的所有调用需要预先初始化OpenGL环境，没有初始化或初始化不正确会导致崩溃。我们对OpenGL的环境要求为 GLES 2.0 以上。具体调用方式，可以参考各平台 demo。

底层接口根据作用逻辑归为五类：初始化、加载道具、主运行接口、销毁、功能接口、P2A相关接口。

------

### 2. APIs
#### 2.1 初始化

##### fuSetup 初始化接口

```java
@Deprecated
public static native int fuSetup(byte[] v3data, byte[] ardata, byte[] authdata);
public static native int fuSetup(byte[] v3data, byte[] authdata);
```

**接口说明：**

初始化系统环境，加载系统数据，并进行网络鉴权。必须在调用SDK其他接口前执行，否则会引发崩溃。

第一个接口已废弃，建议采用第二个。

**参数说明：**

`v3data` v3.bundle 字节数组

`ardata` 已废弃，传 null 即可

`authdata` 鉴权数据字节数组

**返回值：**

返回非0值代表成功，返回0代表失败。如初始化失败，可以通过 `fuGetSystemError` 获取错误代码。

**备注：**

App 启动后只需要 setup 一次即可，其中 authpack.A() 鉴权数据声明在 authpack.java 中。必须配置好有效的证书，SDK 才能正常工作。

根据应用需求，鉴权数据也可以运行时提供（如网络下载），不过要注意证书泄露风险，防止证书被滥用。

需要在有GL Context的地方进行初始化。  

##### fuSetupLocal 离线初始化接口

初始化系统环境，加载系统数据，并进行离线鉴权。必须在调用SDK其他接口前执行，否则会引发崩溃。

```java
public static native int fuSetupLocal(byte[] v3data, byte[] ardata, byte[] authdata);
```

**接口说明：**

初始化系统环境，加载系统数据，并进行离线鉴权。必须在调用SDK其他接口前执行，否则会引发崩溃。

**参数说明：**

`v3data` v3.bundle 字节数组

`ardata` 已废弃，传 null 即可

`authdata` 鉴权数据字节数组

**返回值：**

返回非0值代表成功，返回0代表失败。如初始化失败，可以通过 `fuGetSystemError` 获取错误代码。

**备注：**

App 启动后只需要 setup 一次即可，其中 authpack.A() 鉴权数据声明在 authpack.java 中。必须配置好有效的证书，SDK 才能正常工作。

根据应用需求，鉴权数据也可以运行时提供（如网络下载），不过要注意证书泄露风险，防止证书被滥用。

第一次需要联网鉴权，鉴权成功后，保存新的证书，后面不要联网。

需要在有GL Context的地方进行初始化。  

-----

#### 2.2 加载道具包

##### fuCreateItemFromPackage   通过道具二进制文件创建道具接口

```java
public static native int fuCreateItemFromPackage(byte[] data);
```

**接口说明：**

加载道具包，使其可以在主运行接口中被执行。一个道具包可能是一个功能模块或者多个功能模块的集合，加载道具包可以在流水线中激活对应的功能模块，在同一套SDK调用逻辑中实现即插即用。

**参数说明：**

`data ` 道具二进制文件

**返回值：**

`int ` 创建的道具句柄

**备注：**

该接口可以和主线程异步执行。为了避免加载道具阻塞主线程，建议异步调用该接口。

---

##### fuItemSetParam    为道具设置参数接口（三个重载接口）

```java
public static native int fuItemSetParam(int item, String name, double value);
public static native int fuItemSetParam(int item, String name, double[] value);
public static native int fuItemSetParam(int item, String name, String value);
```

**接口说明：**

修改或设定道具包中变量的值。可以支持的道具包的变量名、含义、及取值范围需要参考道具的文档。

**参数说明：**

`item ` 道具句柄

`name ` 参数名

`value ` 参数值，只支持 double 、 double[] 、String

**返回值：**

`int ` 执行结果：返回 0 代表设置失败，大于 0 表示设置成功

---

##### fuItemGetParam    从道具中获取 double 型参数值接口

```java
public static native double fuItemGetParam(int item,String name);
```

**接口说明：**

获取道具中 double 变量的值。可以支持的道具包的变量名、含义、及取值范围需要参考道具的文档。

**参数说明：**

`item ` 道具句柄

`name ` 参数名

**返回值：**

`double ` 参数值

**备注：**

该接口可以和主线程异步执行。

---

##### fuItemGetParamString   从道具中获取 String 型参数值接口

```java
public static native String fuItemGetParamString(int item,String name);
```

**接口说明：**

获取道具中 String 变量的值。可以支持的道具包的变量名、含义、及取值范围需要参考道具的文档。

**参数说明：**

`item ` 道具句柄

`name ` 参数名

**返回值：**

`String ` 参数值

**备注：**

该接口可以和主线程异步执行。

---

#### 2.3 主运行接口

##### fuDualInputToTexture  视频处理双输入接口

```java
public static native int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] h);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

**参数说明：**

`img ` 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA

`tex_in ` 图像数据纹理ID

`flags ` flags，可以指定数据img数据格式，返回纹理ID的道具镜像等

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

该输入模式可以减少一次 CPU-GPU 间数据传输，在 Android 平台上可以显著优化性能，因此**推荐**尽可能使用该接口。

---

##### fuDualInputToTexture  视频处理双输入接口，byte[]数据回写

```java
public static native int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] items, int readback_w, int readback_h, byte[] readback_img);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

**参数说明：**

`img ` 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA

`tex_in ` 图像数据纹理ID

`flags ` flags，可以指定数据img数据格式，返回纹理ID的道具镜像等

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`readback_w ` 需要回写的图像数据的宽

`readback_h ` 需要回写的图像数据的高

`readback_img ` 需要回写的图像数据byte[]

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuRenderToNV21Image   视频处理单输入接口

```java
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

**参数说明：**

`img ` 图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuRenderToNV21Image   视频处理单输入接口，byte[]数据回写

```java
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags, int readback_w, int readback_h, byte[] readback_img);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

**参数说明：**

`img ` 图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

`readback_w ` 需要回写的图像数据的宽

`readback_h ` 需要回写的图像数据的高

`readback_img ` 需要回写的图像数据byte[]

**返回值：**

`int ` 被处理过的的图像数据纹理ID

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuRenderToI420Image   视频处理单输入接口，I420数据格式

```java
public static native int fuRenderToI420Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

**参数说明：**

`img ` I420的图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuRenderToRgbaImage   视频处理单输入接口，Rgba数据格式

```java
public static native int fuRenderToRgbaImage(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

**参数说明：**

`img ` Rgba的图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuRenderToYUVImage    视频处理单输入接口，YUV数据格式

```java
public static native int fuRenderToYUVImage(byte[] y_buffer, byte[] u_buffer, byte[] v_buffer, int y_stride, int u_stride, int v_stride, int w, int h, int frame_id, int[] items, int flags);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行处理，并输出处理之后的图像数据。该接口会执行所有道具要求、且证书许可的功能模块，包括人脸检测与跟踪、美颜、贴纸或avatar绘制等。

将 items 中的道具绘制到 YUV 三通道的图像中。

**参数说明：**

`y_buffer ` Y帧图像数据byte[]

`u_buffer ` U帧图像数据byte[]

`v_buffer ` V帧图像数据byte[]

`y_stride ` Y帧stride

`u_stride ` U帧stride

`v_stride ` V帧stride

`w ` 图像宽度

`h ` 图像高度

`frame_id ` 当前处理的视频帧序数，每次处理完对其进行加 1 操作，不加 1 将无法驱动道具中的特效动画

`items ` 包含多个道具句柄的 int 数组，包括普通道具、美颜道具、手势道具等

`flags ` flags，可以指定返回纹理ID的道具镜像等

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuBeautifyImage   视频处理接口，只美颜不进行人脸识别

```java
public static native int fuBeautifyImage(int tex_in, int flags, int w, int h, int frame_id, int[] items);
```

**接口说明：**

将输入的图像数据，送入SDK流水线进行全图美化，并输出处理之后的图像数据。该接口仅执行图像层面的美化处理（包括滤镜、美肤），不执行人脸跟踪及所有人脸相关的操作（如美型）。由于功能集中，该接口所需计算更少，执行效率更高。

**参数说明：**

`tex_in ` 图像数据纹理ID

`flags `  flags，可以指定返回纹理ID的道具镜像等

`w ` 图像宽度

`h ` 图像高度

`frame_id ` 当前处理的视频帧序数，每次处理完对其进行加 1 操作，不加 1 将无法驱动道具中的特效动画

`items ` 包含多个道具句柄的 int 数组，包括普通道具、美颜道具、手势道具等

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该接口正常生效需要传入的道具中必须包含美颜道具（随SDK分发，文件名通常为`face_beautification.bundle`）。

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

---

##### fuAvatarToTexture 视频处理接口，依据fuTrackFace获取到的人脸信息来绘制画面

```java
public static native int fuAvatarToTexture(float[] pupilPos, float[] expression, float[] rotation, float[] rmode, int flags, int w, int h, int frame_id, int[] items, int isTracking);
```

**接口说明：**

依据fuTrackFace获取到的人脸信息来绘制画面。

**参数说明：**

`pupilPos ` 眼球方向，长度2

`expression `  表情系数，长度46

`rotation ` 人脸三维旋转，返回值为旋转四元数，长度4

`rmode ` 人脸朝向，0-3分别对应手机四种朝向，长度1

`flags `  flags，可以指定返回纹理ID的道具镜像等

`w ` 图像宽度

`h ` 图像高度

`frame_id ` 当前处理的视频帧序数，每次处理完对其进行加 1 操作，不加 1 将无法驱动道具中的特效动画

`items ` 包含多个道具句柄的 int 数组，包括普通道具、美颜道具、手势道具等

`isTracking ` 是否识别到人脸，可直接传入`fuIsTracking`方法获取到的值

**返回值：**

`int ` 被处理过的的图像数据纹理ID。返回值小于等于0为异常，具体信息通过`fuGetSystemError`获取。

**备注：**

该绘制接口需要OpenGL环境，环境异常会导致崩溃。

----

##### fuTrackFace   人脸信息跟踪接口

```java
public static native void fuTrackFace(byte[] img, int flags, int w, int h);
```

**接口说明：**

对于输入的图像数据仅执行人脸跟踪操作，其他所有图像和绘制相关操作均不执行，因此该函数没有图像输出。由于该函数不执行绘制相关操作，仅包含CPU计算，可以在没有OpenGL环境的情况下正常运行。该函数执行人脸跟踪操作后，结果产生的人脸信息通过 `fuGetFaceInfo` 接口进行获取。

**参数说明：**

`img ` 图像数据byte[]

`flags ` 输入图像格式：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` 图像数据的宽度

`h ` 图像数据的高度

**返回值：**

`int ` 检测到的人脸个数，返回 0 代表没有检测到人脸。

**备注：**

该接口不需要绘制环境，可以在渲染线程之外调用。

----

##### fuTrackFaceWithTongue  在跟踪人脸表情的同时，跟踪舌头blendshape系数

```java
public static native void fuTrackFaceWithTongue(byte[] img, int flags, int w, int h);
```

**接口说明：**

同``` fuTrackFace``` ，在跟踪人脸表情的同时，跟踪舌头blendshape系数。 对于输入的图像数据仅执行人脸跟踪操作，其他所有图像和绘制相关操作均不执行，因此该函数没有图像输出。由于该函数不执行绘制相关操作，仅包含CPU计算，可以在没有OpenGL环境的情况下正常运行。该函数执行人脸跟踪操作后，结果产生的人脸信息通过 ```fuGetFaceInfo``` 接口进行获取。

**参数说明：**

`img ` 图像数据byte[]

`flags ` 输入图像格式：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` 图像数据的宽度

`h ` 图像数据的高度

**返回值：**

`int ` 检测到的人脸个数，返回 0 代表没有检测到人脸。

**备注：**

需要加载 tongue.bundle,才能开启舌头跟踪。

---

#### 2.4 销毁道具包

##### fuDestroyItem 销毁单个道具接口

```java
public static native void fuDestroyItem(int item);
```

**接口说明：**

通过道具句柄销毁道具，并释放相关资源，销毁道具后请将道具句柄设为 0 ，以避免 SDK 使用无效的句柄而导致程序出错。

**参数说明：**

`item ` 道具句柄

__备注:__  

该函数调用后，会即刻释放道具标识符，道具占用的内存无法瞬时释放，需要等 SDK 后续执行主处理接口时通过 GC 机制回收。

---

##### fuDestroyAllItems 销毁所有道具接口

```java
public static native void fuDestroyAllItems();
```

**接口说明：**

销毁全部道具，并释放相关资源，销毁道具后请将道具句柄数组中的句柄设为 0 ，以避免 SDK 使用无效的句柄而导致程序出错。

__备注:__  

该函数会即刻释放系统所占用的资源。但不会破坏 ```fuSetup``` 的系统初始化信息，应用临时挂起到后台时可以调用该函数释放资源，再次激活时无需重新初始化系统。

----

##### fuOnDeviceLost 重置系统的 GL 状态

**接口说明：**

特殊函数，当 OpenGL context 被外部释放/破坏时调用，用于重置系统的 GL 状态。

```java
public static native void fuOnDeviceLost();
```

__备注:__  

该函数仅在无法在原 OpenGL context 内正确清理资源的情况下调用。调用该函数时，会尝试进行资源清理和回收，所有系统占用的内存资源会被释放，但由于 context 发生变化，OpenGL 资源相关的内存可能会发生泄露。

----

##### fuDestroyLibData 释放Tracker内存
**接口说明：**

特殊函数，当不再需要Nama SDK时，可以释放由 ```fuSetup```初始化所分配的人脸跟踪模块的内存，约30M左右。调用后，人脸跟踪以及道具绘制功能将失效， ```fuRenderItemEx ```，```fuTrackFace```等函数将失败。如需使用，需要重新调用 ```fuSetup```进行初始化。

```java
public static native void fuDestroyLibData();
```

---

#### 2.5 功能接口 - 系统

##### fuOnCameraChange  切换摄像头时调用

```java
public static native void fuOnCameraChange();
```

**接口说明：**

在相机数据来源发生切换时调用（例如手机前/后置摄像头切换），用于重置人脸跟踪状态

**备注：**

在其他人脸信息发生残留的情景下，也可以调用该函数来清除人脸信息残留。

-----
##### fuSetTongueTracking 开启舌头的跟踪

```java
public static native int fuSetTongueTracking(int enable);
```

**接口说明：**

开启舌头的跟踪。

**参数说明：**

`enable`: 1 开启舌头跟踪，0 关闭舌头跟踪

__备注:__  

当使用`fuTrackFaceWithTongue`接口时，加载了tongue.bundle后，需要`fuSetTongueTracking(1)`开启舌头跟踪的支持。 
如果道具本身带舌头bs，则不需要主动开启。

----

##### fuSetFaceTrackParam 设置人脸表情跟踪参数
```java
public static native int fuSetFaceTrackParam(String name, float value);
```
**接口说明：**

设置人脸表情跟踪相关参数，__建议使用默认参数__。

__参数说明：__  

`name`: 参数名

`value`: 参数值

- 设置 `name = "mouth_expression_more_flexible"` ，`value = [0,1]`，默认 `value = 0` ，从0到1，数值越大，嘴部表情越灵活。  

__返回值:__  

设置后状态，1 设置成功，0 设置失败。

-------

##### fuSetFaceDetParam 设置人脸检测器相关参数

```java
public static native int fuSetFaceDetParam(String name, float value);
```

**接口说明：**

设置人脸检测器相关参数，__建议使用默认参数__。

**参数说明：**

`name`: 参数名

`value`: 参数值

- 设置 `name == "use_new_cnn_detection"` ，且 `pvalue == 1` 则使用默认的CNN-Based人脸检测算法，否则 `pvalue == 0`则使用传统人脸检测算法。默认开启该模式。
- 设置 `name == "other_face_detection_frame_step"` ，如果当前状态已经检测到一张人脸后，可以通过设置该参数，每隔`step`帧再进行其他人脸检测，有助于提高性能，设置过大会导致延迟感明显，默认值10。。

如果`name == "use_new_cnn_detection"` ，且 `pvalue == 1` 已经开启：

- `name == "use_cross_frame_speedup"`，`pvalue==1`表示，开启交叉帧执行推理，每帧执行半个网络，下帧执行下半个网格，可提高性能。默认 `pvalue==0`关闭。
- `name == "enable_large_pose_detection"`，`pvalue==1`表示，开启正脸大角度(45度)检测优化。`pvalue==0`表示关闭。默认 `pvalue==1`开启。
- `name == "small_face_frame_step"`，`pvalue`表示每隔多少帧加强小脸检测。极小脸检测非常耗费性能，不适合每帧都做。默认`pvalue==5`。
- 检测小脸时，小脸也可以定义为范围。范围下限`name == "min_facesize_small"`，默认`pvalue==18`，表示最小脸为屏幕宽度的18%。范围上限`name == "min_facesize_big"`，默认`pvalue==27`，表示最小脸为屏幕宽度的27%。该参数必须在`fuSetup`前设置。

否则，当`name == "use_new_cnn_detection"` ，且 `pvalue == 0`时：

- `name == "scaling_factor"`，设置图像金字塔的缩放比，默认为1.2f。
- `name == "step_size"`，滑动窗口的滑动间隔，默认 2.f。
- `name == "size_min"`，最小人脸大小，多少像素。 默认 50.f 像素，参考640x480分辨率。
- `name == "size_max"`，最大人脸大小，多少像素。 默认最大，参考640x480分辨率。
- `name == "min_neighbors"`，内部参数, 默认 3.f
- `name == "min_required_variance"`， 内部参数, 默认 15.f

__返回值:__  

设置后状态，1 设置成功，0 设置失败。 

__备注:__  

`name == "min_facesize_small"`，`name == "min_facesize_small"`参数必须在`fuSetup`前设置。

-----

##### fuSetAsyncTrackFace 设置人脸跟踪异步

```java
public static native int fuSetAsyncTrackFace(int enable);
```

**接口说明：**

设置人脸跟踪异步接口。默认处于关闭状态。

**参数说明：**

`enable`: 1 开启异步跟踪，0 关闭异步跟踪。

__备注:__  

默认处于关闭状态。开启后，人脸跟踪会和渲染绘制异步并行，cpu占用略有上升，但整体速度提升，帧率提升。

----

##### fuIsTracking  判断是否检测到人脸

```java
public static native int fuIsTracking();
```

**接口说明：**

获取当前人脸跟踪状态，返回正在跟踪的人脸数量。

**返回值：**

`int ` 检测到的人脸个数，返回 0 代表没有检测到人脸

**备注：**

正在跟踪的人脸数量会受到 `fuSetMaxFaces` 函数的影响，不会超过该函数设定的最大值。

---

##### fuSetMaxFaces 设置系统跟踪的最大人脸数

```java
public static native int fuSetMaxFaces(int n);
```

**接口说明：**

设置系统跟踪的最大人脸数。默认值为1，该值增大会降低人脸跟踪模块的性能，推荐在所有可以设计为单人脸的情况下设置为1。

**参数说明：**

`n` 设置多人模式开启的人脸个数，最多支持 8 个

**返回值：**

`int ` 上一次设置的人脸个数

---

##### fuGetFaceInfo 获取人脸信息

```java
public static native int fuGetFaceInfo(int face_id, String name, float[] value);
```

**接口说明：**

在主接口执行过人脸跟踪操作后，通过该接口获取人脸跟踪的结果信息。获取信息需要证书提供相关权限，目前人脸信息权限包括以下级别：默认、Landmark、Avatar。

**参数说明：**

`face_id ` 被检测的人脸 ID ，未开启多人检测时传 0 ，表示检测第一个人的人脸信息；当开启多人检测时，其取值范围为 [0 ~ maxFaces-1] ，取其中第几个值就代表检测第几个人的人脸信息

`name ` 人脸信息参数名： "landmarks" , "eye_rotation" , "translation" , "rotation" ....

`value ` 作为容器使用的 float 数组指针，获取到的人脸信息会被直接写入该 float 数组。

**返回值**

`int ` 返回 1 代表获取成功，返回 0 代表获取失败，具体错误信息通过`fuGetSystemError`获取。如果返回值为 0 且无控制台打印，说明所要求的人脸信息当前不可用。

__备注:__  

所有支持获取的信息、含义、权限要求如下：

| 信息名称               | 长度                | 含义                                                         | 权限     |
| ---------------------- | ------------------- | ------------------------------------------------------------ | -------- |
| face_rect              | 4                   | 人脸矩形框，图像分辨率坐标，数据为 (x_min, y_min, x_max, y_max) | 默认     |
| rotation_mode          | 1                   | 识别人脸相对于设备图像的旋转朝向，取值范围 0-3，分别代表旋转0度、90度、180度、270度 | 默认     |
| failure_rate           | 1                   | 人脸跟踪的失败率，表示人脸跟踪的质量。取值范围为 0-2，取值越低代表人脸跟踪的质量越高 | 默认     |
| is_calibrating         | 1                   | 表示是否SDK正在进行主动表情校准，取值为 0 或 1。             | 默认     |
| focal_length           | 1                   | SDK当前三维人脸跟踪所采用的焦距数值                          | 默认     |
| landmarks              | 75x2                | 人脸 75 个特征点，图像分辨率坐标                             | Landmark |
| rotation               | 4                   | 人脸三维旋转，数据为旋转四元数\*                             | Landmark |
| translation            | 3                   | 人脸三维平移，数据为 (x, y, z)                               | Landmark |
| eye_rotation           | 4                   | 眼球旋转，数据为旋转四元数\*，上下22度，左右30度。           | Landmark |
| eye_rotation_xy        | 2                   | 眼球旋转，数据范围为[-1,1]，第一个通道表示水平方向转动，第二个通道表示垂直方向转动 | Landmark |
| expression             | 46                  | 人脸表情系数，表情系数含义可以参考《Expression Guide》       | Avatar   |
| expression_with_tongue | 56                  | 1-46为人脸表情系数，同上expression，表情系数含义可以参考《Expression Guide》。47-56为舌头blendshape系数 | Avatar   |
| armesh_vertex_num      | 1                   | armesh三维网格顶点数量                                       | armesh   |
| armesh_face_num        | 1                   | armesh三维网格三角面片数量                                   | armesh   |
| armesh_vertices        | armesh_vertex_num*3 | armesh三维网格顶点位置数据                                   | armesh   |
| armesh_uvs             | armesh_vertex_num*2 | armesh三维网格顶点纹理数据                                   | armesh   |
| armesh_faces           | armesh_face_num*3   | armesh三维网格三角片数据                                     | armesh   |

*注：*旋转四元数转换为欧拉角可以参考 [该网页](https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles)。

---

##### fuSetDefaultOrientation  设置默认的人脸朝向

```java
public static native int fuSetDefaultOrientation(int rmode);
```

**接口说明：**

设置默认的人脸朝向。正确设置默认的人脸朝向可以显著提升人脸首次识别的速度。

**参数说明：**

`rmode` 要设置的人脸朝向，取值范围为 0-3，分别对应人脸相对于图像数据旋转0度、90度、180度、270度。

**备注：**

一般来说，Android 前置摄像头一般设置参数 1，后置摄像头一般设置参数 3。部分手机存在例外。

----

##### fuGetVersion  获取 SDK 版本信息

```java
public static native String fuGetVersion();
```

**接口说明：**

获取当前 SDK 版本号。一个常量字符串指针，版本号表示如下：“主版本号\_子版本号\-版本校检值”

**返回值：**

`String ` 版本信息

----

##### fuGetModuleCode   获取 SDK 鉴权后证书可用的鉴权码

```java
public static native int fuGetModuleCode(int i);
```

**接口说明：**

获取 SDK 鉴权后证书可用的鉴权码

**参数说明：**

`i` 传0即可

**返回值：**

`int ` 鉴权码

----

##### fuGetSystemError 返回系统错误

```java
public static native int fuGetSystemError();
```

**接口说明：**

返回系统错误，该类错误一般为系统机制出现严重问题，导致系统关闭，因此需要重视。

**返回值：**

系统错误代码。

**备注：**

返回系统错误代码后，可以通过 `fuGetSystemErrorString` 函数解析最重要的错误信息。

系统错误代码及其含义如下：

| 错误代码 | 错误信息                          |
| -------- | --------------------------------- |
| 1        | 随机种子生成失败                  |
| 2        | 机构证书解析失败                  |
| 3        | 鉴权服务器连接失败                |
| 4        | 加密连接配置失败                  |
| 5        | 客户证书解析失败                  |
| 6        | 客户密钥解析失败                  |
| 7        | 建立加密连接失败                  |
| 8        | 设置鉴权服务器地址失败            |
| 9        | 加密连接握手失败                  |
| 10       | 加密连接验证失败                  |
| 11       | 请求发送失败                      |
| 12       | 响应接收失败                      |
| 13       | 异常鉴权响应                      |
| 14       | 证书权限信息不完整                |
| 15       | 鉴权功能未初始化                  |
| 16       | 创建鉴权线程失败                  |
| 17       | 鉴权数据被拒绝                    |
| 18       | 无鉴权数据                        |
| 19       | 异常鉴权数据                      |
| 20       | 证书过期                          |
| 21       | 无效证书                          |
| 22       | 系统数据解析失败                  |
| 0x100    | 加载了非正式道具包（debug版道具） |
| 0x200    | 运行平台被证书禁止                |

----

##### fuGetSystemErrorString 返回系统错误信息

```java
public static native String fuGetSystemErrorString(int code);
```

**接口说明：**

解析系统错误代码，并返回可读信息。

__参数说明:__  

`code`：系统错误代码，一般为 ```fuGetSystemError``` 所返回的代码。

__返回值:__  

一个常量字符串，解释了当前错误的含义。

__备注:__  

当多个错误存在的情况下，该函数会返回当前最为重要的错误信息。

----

##### fuCheckDebugItem 检查一个道具包是否为非正式道具包

```java
public static native int fuCheckDebugItem(byte[] data);
```

**接口说明：**

检查一个道具包是否为非正式道具包（debug版道具）。

__参数:__  

`data`：道具包的字节数组

__返回值:__  

返回值 0 代表该道具为正式道具，返回值 1 代表该道具为非正式道具（debug版道具），返回值 -1 代表该道具数据异常。

__备注:__  

如果系统加载过非正式版道具，会导致系统进入倒计时，并在倒计时结束时关闭。如果系统提示 “debug item used”，或系统在运行1分钟后停止，则需要利用该函数检查所有加载过的道具，如果有非正式道具需要进行正确的道具签名。

道具签名流程请联系技术支持。

----

#### 2.6 功能接口 - 效果

##### fuSetExpressionCalibration  开启表情校准功能

```java
public static native void fuSetExpressionCalibration(int mode);
```

**接口说明：**

设置人脸表情校准功能。该功能的目的是使表情识别模块可以更加适应不同人的人脸特征，以实现更加准确可控的表情跟踪效果。

该功能分为两种模式，主动校准 和 被动校准。

- 主动校准：该种模式下系统会进行快速集中的表情校准，一般为初次识别到人脸之后的2-3秒钟。在该段时间内，需要用户尽量保持无表情状态，该过程结束后再开始使用。该过程的开始和结束可以通过 ```fuGetFaceInfo``` 接口获取参数 ```is_calibrating```。
- 被动校准：该种模式下会在整个用户使用过程中逐渐进行表情校准，用户对该过程没有明显感觉。该种校准的强度比主动校准较弱。

默认状态为开启被动校准。

**参数说明：**

`mode` 0为关闭表情校准，1为主动校准，2为被动校准。

__备注:__  

当利用主处理接口处理静态图片时，由于需要针对同一数据重复调用，需要将表情校准功能关闭。

-----

##### fuLoadAnimModel   加载表情动画数据包，并启用表情优化功能

```java
public static native int fuLoadAnimModel(byte[] data);
```

**接口说明：**

加载表情动画数据包，并开启该功能。

表情优化功能可以使实时跟踪后得到的表情更加自然生动，但会引入一定表情延迟。

**参数说明：**

`data` 表情动画数据包的字节数组

__返回值:__  

返回值 1 代表加载成功，并启用表情优化功能。返回值 0 代表失败。

----

##### fuSetStrictTracking  启用更加严格的跟踪质量检测

```java
public static native void fuSetStrictTracking(int mode);
```

**接口说明：**

启用更加严格的跟踪质量检测。

**参数说明：**

`mode`：0 为禁用，1 为启用，默认为禁用状态。

-----

##### fuSetFocalLengthScale 修改系统焦距

```java
public static native void fuSetFocalLengthScale(float scale);
```

**接口说明：**

修改系统焦距（效果等价于focal length, 或FOV），影响三维跟踪、AR效果的透视效果。

**参数说明：**

`scale`：焦距调整的比例系数，1.0 为默认值。建议取值范围为 0.1 ~ 2.0。

__备注:__  

系数小于等于0为无效输入。

-----

#### 2.7 P2A 相关接口

##### fuAvatarBindItems 将普通道具绑定到avatar道具的接口

```java
public static native int fuAvatarBindItems(int avatar_item, int[] items, int[] contracts);
```

**接口说明：**

+ 该接口主要应用于 P2A 项目中，将普通道具绑定到 avatar 道具上，从而实现道具间的数据共享，在视频处理时只需要传入 avatar 道具句柄，普通道具也会和 avatar 一起被绘制出来。
+ 普通道具又分免费版和收费版，免费版有免费版对应的 contract 文件，收费版有收费版对应的文件，当绑定时需要同时传入这些 contracts 文件才能绑定成功。注： contract 的创建和普通道具创建方法一致

**参数说明：**

`avatar_item ` avatar 道具句柄

`items ` 需要被绑定到 avatar 道具上的普通道具的句柄数组

`itemsCount ` 句柄数组包含的道具句柄个数

`contracts ` contract 道具的句柄数组

**返回值：**

`int ` 被绑定到 avatar 道具上的普通道具个数

---

##### fuAvatarUnbindItems   将普通道具从avatar道具上解绑的接口

```java
public static native int fuAvatarUnbindItems(int avatar_item, int[] items);
```

**接口说明：**

该接口可以将普通道具从 avatar 道具上解绑，主要应用场景为切换道具或去掉某个道具

**参数说明：**

`avatar_item ` avatar 道具句柄

`items ` 需要从 avatar 道具上的解除绑定的普通道具的句柄数组

**返回值：**

`int ` 从 avatar 道具上解除绑定的普通道具个数

-----

##### fuBindItems  绑定道具接口

```java
public static native int fuBindItems(int item_src, int[] items);
```

**接口说明：**

该接口可以将一些普通道具绑定到某个目标道具上，从而实现道具间的数据共享，在视频处理时只需要传入该目标道具句柄即可

**参数说明：**

`item_src ` 目标道具句柄

`items `  需要被绑定到目标道具上的其他道具的句柄数组

**返回值：**

`int ` 被绑定到目标道具上的普通道具个数

---

##### fuUnbindAllItems  解绑所有道具接口

```java
public static native int fuUnbindAllItems(int item_src);
```

**接口说明：**

该接口可以解绑绑定在目标道具上的全部道具

**参数说明：**

`item_src` 目标道具句柄

**返回值：**

`int ` 从目标道具上解除绑定的普通道具个数

#### 2.8 废弃接口

##### fuLoadExtendedARData 

```java
public static native int fuLoadExtendedARData(byte[] ardata);
```
----
##### fuSetQualityTradeoff

```java
public static native void fuSetQualityTradeoff(float quality);
```

---

### 3. 输入输出格式列表

##### RGBA 数组

RGBA 格式的图像内存数组。

__数据格式标识符:__

FU_FORMAT_RGBA_BUFFER

__数据内容:__

连续内存空间，长度为 ```w*h*4```。数组元素为```int```，按 RGBA 方式表示颜色信息。

__输入输出支持:__

可输入 / 可输出

__备注:__

由于平台上的内存对齐要求，图像内存空间的实际宽度可能不等于图像的语义宽度。在主运行接口传入图像宽度时，应传入内存实际宽度。

------

##### BGRA 数组

BGRA 格式的图像内存数组。

__数据格式标识符:__

FU_FORMAT_BGRA_BUFFER

__数据内容:__

连续内存空间，长度为 ```w*h*4```。数组元素为```int```，按 BGRA 方式表示颜色信息。

__输入输出支持:__

可输入 / 可输出

__备注:__

由于平台上的内存对齐要求，图像内存空间的实际宽度可能不等于图像的语义宽度。在主运行接口传入图像宽度时，应传入内存实际宽度。

该格式为原生 iOS 的相机数据格式之一。

------

##### RGBA 纹理

RGBA 格式的 OpenGL 纹理。

__数据格式标识符:__

FU_FORMAT_RGBA_TEXTURE

__数据内容:__

一个 ```GLuint```，表示 OpenGL 纹理 ID。

__输入输出支持:__

可输入 / 可输出

------

##### RGBA OES 纹理

RGBA 格式的 OpenGL external OES 纹理。

__数据格式标识符:__

FU_FORMAT_RGBA_TEXTURE_EXTERNAL_OES

__数据内容:__

一个 ```GLuint```，表示 OpenGL external OES 纹理 ID。

__输入输出支持:__

仅输入

__备注:__

该格式为原生安卓相机数据格式之一。

------

##### NV21 数组

NV21 格式的图像内存数组。

__数据格式标识符:__

FU_FORMAT_NV21_BUFFER

__数据内容:__

连续内存，前一段是 Y 数据，长度为 ```w*h```，后一段是 UV 数据，长度为 ```2*((w+1)>>1)```（分辨率是Y的一半，但包含UV两个通道）。两段数据在内存中连续存放。

__输入输出支持:__

可输入 / 可输出

__备注:__
该格式要求UV数据交错存放（如：UVUVUVUV），如UV数据分开存放（UUUUVVVV），请用I420数组格式。

该格式为原生安卓相机数据格式之一。

------

##### NV12 数组

NV12 格式的图像内存数组。

__数据格式标识符:__

FU_FORMAT_NV12_BUFFER

__数据内容:__
结构体 ```TNV12Buffer```，其定义如下。

```c
typedef struct{
	void* p_Y; 
	void* p_CbCr;
	int stride_Y;
	int stride_CbCr;
}TNV12Buffer;
```

__参数:__

*p_Y*：指向 Y 数据的指针。

*p_CbCr*：指向 UV 数据的指针。

*stride_Y*：Y 数据每行的字节长度。

*stride_CbCr*：UV 数据每行的字节长度。

__输入输出支持:__

可输入 / 可输出

__备注:__

该格式与 NV21 数组格式非常类似，只是 UV 数据中 U 和 V 的交错排布相反。不过该格式支持 Y 数据和 UV 数据分别存放，不再要求数据整体连续。
该格式为原生iOS相机数据格式之一。

------

##### I420 数组

I420 格式的图像内存数组。

__数据格式标识符:__

FU_FORMAT_I420_BUFFER

__数据内容:__

连续内存，第一段是 Y 数据，长度为 ```w*h```，第二段是 U 数据，长度为 ```((w+1)>>1)```，第三段是 V 数据，长度为 ```((w+1)>>1)```（后两个通道分辨率是Y的一半）。三段数据在内存中连续存放。

__输入输出支持:__

可输入 / 可输出

__备注:__

该格式和 NV21 数组基本一致，区别在于 U 和 V 数据分别连续存放。

------

##### Android 双输入

针对 Android 原生相机数据的双输入格式。双输入分别指GPU数据输入——RGBA / NV21 / I420 格式的 OpenGL 纹理，以及CPU内存数据输入—— NV21/ RGBA / I420 格式的图像内存数组。

相比仅提供内存数组或纹理的单数据输入，该输入模式可以减少一次 CPU-GPU 间数据传输，在 Android 平台上可以显著优化性能，因此**推荐尽可能使用该接口**。

__数据格式标识符:__

FU_FORMAT_ANDROID_DUAL_MODE

__数据内容:__
结构体 ```TAndroidDualMode```，其定义如下。

```c
typedef struct{
	void* p_NV21;
	int tex;
	int flags;
}TAndroidDualMode;
```

__参数:__

*p_NV21*：指向内存图像数据的指针。

*tex*：OpenGL 纹理 ID。

*flags*：扩展功能标识符，所有支持的标识符及其功能如下。多个标识符通过“或”运算符连接。

| 扩展功能标识符                   | 含义                                         |
| -------------------------------- | -------------------------------------------- |
| FU_ADM_FLAG_EXTERNAL_OES_TEXTURE | 传入的纹理为OpenGL external OES 纹理         |
| FU_ADM_FLAG_ENABLE_READBACK      | 开启后将处理结果写回覆盖到传入的内存图像数据 |
| FU_ADM_FLAG_NV21_TEXTURE         | 传入的纹理为 NV21 数据格式                   |
| FU_ADM_FLAG_I420_TEXTURE         | 传入的纹理为 I420 数据格式                   |
| FU_ADM_FLAG_I420_BUFFER          | 传入的内存图像数据为 I420 数据格式           |
| FU_ADM_FALG_RGBA_BUFFER          | 传入的内存图像数据为 RGBA 数据格式           |

__输入输出支持:__
仅输入

------

##### 当前 FBO

指调用主处理接口时当前绑定的 OpenGL FBO。主处理接口可以直接将处理结果绘制到该 FBO 上。

__数据格式标识符:__

FU_FORMAT_GL_CURRENT_FRAMEBUFFER

__数据内容:__

无，数据指针直接传 NULL。

__输入输出支持:__

仅输出

__备注:__

需要在传入 FBO 前完成 FBO 的创建，包括颜色纹理的绑定，该 FBO 需通过 OpenGL 完备性检查。
如果有 3D 绘制内容，需要该 FBO 具备深度缓冲。

------

##### 指定 FBO

可以将外部已经准备好的 OpenGL FBO 传入，不一定在调用主处理接口时作为当前绑定的 FBO。 主处理接口可以直接将处理结果绘制到该 FBO 上。

__数据格式标识符:__

FU_FORMAT_GL_SPECIFIED_FRAMEBUFFER

__数据内容:__
结构体 ```TSPECFBO```，其定义如下。

```c
typedef struct{
	int fbo;
	int tex;
}TSPECFBO;
```

__参数:__

*fbo*：指定的 FBO ID。

*tex*：该 FBO 上绑定的颜色纹理 ID。

__输入输出支持:__

仅输出

__备注:__
需要在传入 FBO 前完成 FBO 的创建，包括颜色纹理的绑定，该 FBO 需通过 OpenGL 完备性检查。
如果有 3D 绘制内容，需要传入 FBO 具备深度缓冲。

------

##### Avatar 驱动信息

特殊的输入数据，不是图像数据，而是人脸驱动信息，用于驱动avatar模型。人脸驱动信息可以在主处理接口执行后获取，也可以外部输入，比如avatar动画录制的信息，或者用户交互产生的信息等。

__数据格式标识符:__

FU_FORMAT_AVATAR_INFO

__数据内容:__
结构体 ```TAvatarInfo```，其定义如下。

```c
typedef struct{	
	float* p_translation;	
	float* p_rotation;
	float* p_expression;
	float* rotation_mode;
	float* pupil_pos;
	int is_valid;
}TAvatarInfo;
```

__参数:__
*p_translation*：指向内存数据的指针，数据为3个float，表示人脸在相机空间的平移。其中，x/y 的单位为图像分辨率，z 是相机空间中人脸的深度。

*p_rotation*：指向内存数据的指针，数据为4个float，表示人头的三位旋转。旋转表示方式为四元数，需要经过换算转化成欧拉角旋转。

*p_expression*：指向内存数据的指针，数据为46个float，表示人脸的表情系数。表情系数的含义请参考《Expression Guide》。

*rotation_mode*：一个int，取值范围为 0-3，表示人脸相对于图像数据的旋转，分别代表旋转0度、90度、180度、270度。

*pupil_pos*：指向内存数据的指针，数据为2个float，表示瞳孔的参数坐标。该坐标本身不具有语义，一般直接从跟踪结果中获取。

*is_valid*：一个int，表示该 avatar 信息是否有效。该值为0的情况下系统不会处理对应 avatar 信息。

__输入输出支持:__

仅输入

__备注:__

该输入模式仅能配合 avatar 道具使用，加载人脸 AR 类道具会导致异常。

该输入模式会简化对传入图像数据的处理，在 avatar 应用情境下性能较高。此外，对于 avatar 的控制更加灵活，可以允许用户自由操控 avatar，如拖动 avatar 转头、触发特定表情等。

------

##### Avatar 驱动信息

特殊的输入数据，不是图像数据，而是人脸驱动信息，用于驱动avatar模型。人脸驱动信息可以在主处理接口执行后获取，也可以外部输入，比如avatar动画录制的信息，或者用户交互产生的信息等。

__数据格式标识符:__

FU_FORMAT_AVATAR_INFO

__数据内容:__
结构体 ```TAvatarInfo```，其定义如下。

```c
typedef struct{	
	float* p_translation;	
	float* p_rotation;
	float* p_expression;
	float* rotation_mode;
	float* pupil_pos;
	int is_valid;
}TAvatarInfo;
```

__参数:__

*p_translation*：指向内存数据的指针，数据为3个float，表示人脸在相机空间的平移。其中，x/y 的单位为图像分辨率，z 是相机空间中人脸的深度。

*p_rotation*：指向内存数据的指针，数据为4个float，表示人头的三位旋转。旋转表示方式为四元数，需要经过换算转化成欧拉角旋转。

*p_expression*：指向内存数据的指针，数据为46个float，表示人脸的表情系数。表情系数的含义请参考《Expression Guide》。

*rotation_mode*：一个int，取值范围为 0-3，表示人脸相对于图像数据的旋转，分别代表旋转0度、90度、180度、270度。

*pupil_pos*：指向内存数据的指针，数据为2个float，表示瞳孔的参数坐标。该坐标本身不具有语义，一般直接从跟踪结果中获取。

*is_valid*：一个int，表示该 avatar 信息是否有效。该值为0的情况下系统不会处理对应 avatar 信息。

__输入输出支持:__

仅输入

__备注:__  

该输入模式仅能配合 avatar 道具使用，加载人脸 AR 类道具会导致异常。

该输入模式会简化对传入图像数据的处理，在 avatar 应用情境下性能较高。此外，对于 avatar 的控制更加灵活，可以允许用户自由操控 avatar，如拖动 avatar 转头、触发特定表情等。

------

### 4. 常见问题 

#### 4.1 关于初始化

初始化需要在渲染线程，执行一次就好了。

#### 4.2 关于道具加载

建议异步加载道具，单独开一个 IO 线程，与渲染线程分离，这样保证画面不会出现卡顿。

**如有使用问题，请联系技术支持。**