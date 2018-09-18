# Nama API Android Reference

---

**fuSetup   初始化接口：**

```java
public static native int fuSetup(byte[] v3data, byte[] ardata, byte[] authdata);
```

接口说明：

初始化系统环境，加载系统数据，并进行网络鉴权。必须在调用SDK其他接口前执行，否则会引发崩溃。app启动后只需要setup一次faceunity即可，其中 authpack.A() 密钥数组声明在 authpack.java 中。

参数说明：

`v3data` v3.bundle 文件路径

`ardata` 已废弃，传 null 即可

`authdata` 密钥数组，必须配置好密钥，SDK才能正常工作

---

**fuDualInputToTexture  视频处理接口（双输入）：**

```
public static native int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] h);
```

参数说明：

`img ` 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA

`tex_in ` 图像数据纹理ID

`flags ` flags，可以指定数据img数据格式，返回纹理ID的道具镜像等

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuDualInputToTexture  视频处理接口（双输入，byte[]数据以指定的宽高回写到指定的数组中）：**

```
public static native int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] items, int readback_w, int readback_h, byte[] readback_img);
```

参数说明：

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

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuDualInputToTextureMasked    视频处理接口（双输入，多人脸多道具）：**

```
public static native int fuDualInputToTextureMasked(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] items, int[] masks);
```

参数说明：

`img ` 图像数据byte[]，支持的格式为：NV21（默认）、I420、RGBA

`tex_in ` 图像数据纹理ID

`flags ` flags，可以指定数据img数据格式，返回纹理ID的道具镜像等

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`masks ` 与items中道具handle一一对应，为相应道具handle所要绘制在第几张人脸上（要求与items个数相同）。int[]中每个值按照按位与计算（1为第一张人脸，2为第二张人脸，3为第一张与第二张人脸...以此类推）（美颜道具会绘制在所有人脸上）

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuRenderToNV21Image   视频处理接口（单输入）：**

```
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

接口说明：

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

参数说明：

`img ` 图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuRenderToNV21Image   视频处理接口（单输入，byte[]数据以指定的宽高回写到指定的数组中）：**

```
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags, int readback_w, int readback_h, byte[] readback_img);
```

参数说明：

`img ` 图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

`readback_w ` 需要回写的图像数据的宽

`readback_h ` 需要回写的图像数据的高

`readback_img ` 需要回写的图像数据byte[]

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuRenderToNV21ImageMasked 视频处理接口（单输入，多人脸多道具）：**

```
public static native int fuRenderToNV21ImageMasked(byte[] img, int w, int h, int frame_id, int[] items, int[] masks);
```

参数说明：

`img ` 图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`masks ` 与items中道具handle一一对应，为相应道具handle所要绘制在第几张人脸上（要求与items个数相同）。int[]中每个值按照按位与计算（1为第一张人脸，2为第二张人脸，3为第一张与第二张人脸...以此类推）（美颜道具会绘制在所有人脸上）

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuRenderToI420Image   视频处理接口（单输入,I420数据格式）：**

```
public static native int fuRenderToI420Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

接口说明：

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

参数说明：

`img ` I420的图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuRenderToRgbaImage   视频处理接口（单输入,Rgba数据格式）：**

```
public static native int fuRenderToRgbaImage(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

接口说明：

本接口默认带有数据回写功能，会以相同宽高回写到对应的img数组中。

参数说明：

`img ` Rgba的图像数据byte[]，被处理过的的图像数据会回写到该byte[]中

`w ` 图像数据的宽

`h ` 图像数据的高

`frame_id ` 当前处理的视频帧序数

`items ` 包含多个道具句柄的int数组

`flags ` flags，可以指定返回纹理ID的道具镜像等

返回值：

`int ` 被处理过的的图像数据纹理ID

---

**fuRenderToYUVImage    视频处理接口（单输入,YUV数据格式）：**

```
public static native int fuRenderToYUVImage(byte[] y_buffer, byte[] u_buffer, byte[] v_buffer, int y_stride, int u_stride, int v_stride, int w, int h, int frame_id, int[] items, int flags);
```

接口说明：

+ 将 items 中的道具绘制到 YUV 三通道的图像中

参数说明：

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

---

**fuBeautifyImage   视频处理接口（只美颜不进行人脸识别）：**

```
public static native int fuBeautifyImage(int tex_in, int flags, int w, int h, int frame_id, int[] items);
```

接口说明：

只美颜不进行人脸识别,运行该方法后不能通过`fuGetFaceInfo`获取人脸信息。

参数说明：

`tex_in ` 图像数据纹理ID

`flags `  flags，可以指定返回纹理ID的道具镜像等

`w ` 图像宽度

`h ` 图像高度

`frame_id ` 当前处理的视频帧序数，每次处理完对其进行加 1 操作，不加 1 将无法驱动道具中的特效动画

`items ` 包含多个道具句柄的 int 数组，包括普通道具、美颜道具、手势道具等

---

**fuAvatarToTexture 视频处理接口（依据fuTrackFace获取到的人脸信息来绘制画面）：**

```
public static native int fuAvatarToTexture(float[] landmarks, float[] expression, float[] rotation, float[] rmode, int flags, int w, int h, int frame_id, int[] items, int isTracking);
```

接口说明：

依据fuTrackFace获取到的人脸信息来绘制画面

参数说明：

`landmarks ` 2D人脸特征点，返回值为75个二维坐标，长度75*2

`expression `  表情系数，长度46

`rotation ` 人脸三维旋转，返回值为旋转四元数，长度4

`rmode ` 人脸朝向，0-3分别对应手机四种朝向，长度1

`flags `  flags，可以指定返回纹理ID的道具镜像等

`w ` 图像宽度

`h ` 图像高度

`frame_id ` 当前处理的视频帧序数，每次处理完对其进行加 1 操作，不加 1 将无法驱动道具中的特效动画

`items ` 包含多个道具句柄的 int 数组，包括普通道具、美颜道具、手势道具等

`isTracking ` 是否识别到人脸，可直接传入`fuIsTracking`方法获取到的值

---

**fuOnCameraChange  切换摄像头时需调用的接口：**

```
public static native void fuOnCameraChange();
```

接口说明：

在相机数据来源发生切换时调用（例如手机前/后置摄像头切换），用于重置人脸跟踪状态

---

**fuCreateItemFromPackage   通过道具二进制文件创建道具接口：**

```
public static native int fuCreateItemFromPackage(byte[] data);
```

接口说明：

通过道具二进制文件创建道具句柄

参数说明：

`data ` 道具二进制文件

返回值：

`int ` 创建的道具句柄

---

**fuDestroyItem 销毁单个道具接口：**

```
public static native void fuDestroyItem(int item);
```

接口说明：

通过道具句柄销毁道具，并释放相关资源，销毁道具后请将道具句柄设为 0 ，以避免 SDK 使用无效的句柄而导致程序出错。

参数说明：

`item ` 道具句柄

---

**fuDestroyAllItems 销毁所有道具接口：**

```
public static native void fuDestroyAllItems();
```

接口说明：

销毁全部道具，并释放相关资源，销毁道具后请将道具句柄数组中的句柄设为 0 ，以避免 SDK 使用无效的句柄而导致程序出错。

---

**fuItemSetParam    为道具设置参数接口（三个接口）：**

```
public static native int fuItemSetParam(int item, String name, double value);
public static native int fuItemSetParam(int item, String name, double[] value);
public static native int fuItemSetParam(int item, String name, String value);
```

接口说明：

为道具设置参数

参数说明：

`item ` 道具句柄

`name ` 参数名

`value ` 参数值：只支持 double 、 double[] 、String

返回值：

`int ` 执行结果：返回 0 代表设置失败，大于 0 表示设置成功

---

**fuItemGetParam    从道具中获取 double 型参数值接口：**

```
public static native double fuItemGetParam(int item,String name);
```

接口说明：

从道具中获取 double 型参数值

参数说明：

`item ` 道具句柄

`name ` 参数名

返回值：

`double ` 参数值

---

**fuItemGetParamString  从道具中获取 String 型参数值接口：**

```
public static native String fuItemGetParamString(int item,String name);
```

接口说明：

从道具中获取 String 型参数值

参数说明：

`item ` 道具句柄

`name ` 参数名

返回值：

`String ` 参数值

---

**fuIsTracking  判断是否检测到人脸接口：**

```
public static native int fuIsTracking();
```

接口说明：

判断是否检测到人脸

返回值：

`int ` 检测到的人脸个数，返回 0 代表没有检测到人脸

---

**fuSetMaxFaces 开启多人检测模式接口：**

```
public static native int fuSetMaxFaces(int n);
```

接口说明：

开启多人检测模式，最多可同时检测 8 张人脸

参数说明：

`maxFaces ` 设置多人模式开启的人脸个数，最多支持 8 个

返回值：

`int ` 上一次设置的人脸个数

---

**fuTrackFace   人脸信息跟踪接口：**

```
public static native void fuTrackFace(byte[] img, int flags, int w, int h);
```

接口说明：

该接口只对人脸进行检测

参数说明：

`img ` 图像数据byte[]

`flags ` 输入图像格式：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` 图像数据的宽度

`h ` 图像数据的高度

返回值：

`int ` 检测到的人脸个数，返回 0 代表没有检测到人脸

---

**fuGetFaceInfo 获取人脸信息接口：**

```
public static native int fuGetFaceInfo(int face_id, String name, float[] value);
```

接口说明：

+ 在程序中需要先运行过视频处理接口或 **人脸信息跟踪接口** 后才能使用该接口来获取人脸信息；
+ 该接口能获取到的人脸信息与我司颁发的证书有关，普通证书无法通过该接口获取到人脸信息；
+ 什么证书能获取到人脸信息？能获取到哪些人脸信息？请看下方：

```java
	landmarks: 2D人脸特征点，返回值为75个二维坐标，长度75*2
	证书要求: LANDMARK证书、AVATAR证书

	landmarks_ar: 3D人脸特征点，返回值为75个三维坐标，长度75*3
	证书要求: AVATAR证书

	rotation: 人脸三维旋转，返回值为旋转四元数，长度4
	证书要求: LANDMARK证书、AVATAR证书

	translation: 人脸三维位移，返回值一个三维向量，长度3
	证书要求: LANDMARK证书、AVATAR证书

	eye_rotation: 眼球旋转，返回值为旋转四元数,长度4
	证书要求: LANDMARK证书、AVATAR证书

	rotation_raw: 人脸三维旋转（不考虑屏幕方向），返回值为旋转四元数，长度4
	证书要求: LANDMARK证书、AVATAR证书

	expression: 表情系数，长度46
	证书要求: AVATAR证书

	projection_matrix: 投影矩阵，长度16
	证书要求: AVATAR证书

	face_rect: 人脸矩形框，返回值为(xmin,ymin,xmax,ymax)，长度4
	证书要求: LANDMARK证书、AVATAR证书

	rotation_mode: 人脸朝向，0-3分别对应手机四种朝向，长度1
	证书要求: LANDMARK证书、AVATAR证书
```

参数说明：

`face_id ` 被检测的人脸 ID ，未开启多人检测时传 0 ，表示检测第一个人的人脸信息；当开启多人检测时，其取值范围为 [0 ~ maxFaces-1] ，取其中第几个值就代表检测第几个人的人脸信息

`name ` 人脸信息参数名： "landmarks" , "eye_rotation" , "translation" , "rotation" ....

`value ` 作为容器使用的 float 数组指针，获取到的人脸信息会被直接写入该 float 数组。

返回值

`int ` 返回 1 代表获取成功，返回 0 代表获取失败

---

**fuAvatarBindItems 将普通道具绑定到avatar道具的接口：**

```
public static native int fuAvatarBindItems(int avatar_item, int[] items, int[] contracts);
```

接口说明：

+ 该接口主要应用于 P2A 项目中，将普通道具绑定到 avatar 道具上，从而实现道具间的数据共享，在视频处理时只需要传入 avatar 道具句柄，普通道具也会和 avatar 一起被绘制出来。
+ 普通道具又分免费版和收费版，免费版有免费版对应的 contract 文件，收费版有收费版对应的文件，当绑定时需要同时传入这些 contracts 文件才能绑定成功。注： contract 的创建和普通道具创建方法一致

参数说明：

`avatar_item ` avatar 道具句柄

`items ` 需要被绑定到 avatar 道具上的普通道具的句柄数组

`itemsCount ` 句柄数组包含的道具句柄个数

`contracts ` contract 道具的句柄数组

返回值：

`int ` 被绑定到 avatar 道具上的普通道具个数

---

**fuAvatarUnbindItems   将普通道具从avatar道具上解绑的接口：**

```
public static native int fuAvatarUnbindItems(int avatar_item, int[] items);
```

接口说明：

该接口可以将普通道具从 avatar 道具上解绑，主要应用场景为切换道具或去掉某个道具

参数说明：

`avatar_item ` avatar 道具句柄

`items ` 需要从 avatar 道具上的解除绑定的普通道具的句柄数组

返回值：

`int ` 从 avatar 道具上解除绑定的普通道具个数

---
**绑定道具接口：**

```
public static native int fuBindItems(int item_src, int[] items);
```

接口说明：

该接口可以将一些普通道具绑定到某个目标道具上，从而实现道具间的数据共享，在视频处理时只需要传入该目标道具句柄即可

参数说明：

`item_src ` 目标道具句柄

`items `  需要被绑定到目标道具上的其他道具的句柄数组

返回值：

`int ` 被绑定到目标道具上的普通道具个数

---

**fuUnbindAllItems  解绑所有道具接口：**

```
public static native int fuUnbindAllItems(int item_src);
```

接口说明：

该接口可以解绑绑定在目标道具上的全部道具

参数说明：

`item_src` 目标道具句柄

返回值：

`int ` 从目标道具上解除绑定的普通道具个数

---

**fuGetVersion  获取 SDK 版本信息接口：**

```
public static native String fuGetVersion();
```

接口说明：

获取当前 SDK 版本号

返回值：

`String ` 版本信息

----

**fuGetModuleCode   获取 SDK 鉴权后证书可用的鉴权码：**

```
public static native int fuGetModuleCode(int i);
```

接口说明：

获取 SDK 鉴权后证书可用的鉴权码

参数说明：

`i` 传0即可

返回值：

`int ` 鉴权码

----

**fuLoadExtendedARData  加载AR高精度数据包，并开启该功能：**

```
public static native int fuLoadExtendedARData(byte[] ardata);
```

接口说明：

加载AR高精度数据包，并开启该功能

参数说明：

`ardata` AR高精度数据包byte[]

----

**fuLoadAnimModel   加载表情动画数据包，并开启该功能：**

```
public static native int fuLoadAnimModel(byte[] dat0);
```

接口说明：

加载表情动画数据包，并开启该功能

参数说明：

`dat0` 表情动画数据包byte[]

----

**fuSetExpressionCalibration    开启表情校准功能：**

```
public static native void fuSetExpressionCalibration(int i);
```

接口说明：

开启表情校准功能

参数说明：

`i` 0为关闭表情校准，2为被动校准。

----