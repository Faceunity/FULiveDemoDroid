# FULiveDemoDroid

FULiveDemoDroid 是 Faceunity 的面部跟踪和虚拟道具功能在Android SDK中的集成，作为一款集成示例。

## SDK v4.7 更新

- 修复android 8.0及以上版本个别分辨率花屏的问题
- 修复了其他一些BUG

具体更新内容可以到[这里](https://github.com/Faceunity/FULiveDemoDroid/tree/master/docs/FUNama%20SDK%20v4.7%20%E6%9B%B4%E6%96%B0%E6%96%87%E6%A1%A3.md)查看详细文档。


## 库文件
  - nama.jar 函数调用接口
  - libnama.so 人脸跟踪及道具绘制核心库
  
## 数据文件
目录 app/src/main/assets/ 下的 \*.bundle 为程序的数据文件。数据文件中都是二进制数据，与扩展名无关，用bundle只是为了避免打包时额外的压缩。实际在app中使用时，打包在程序内或者从网络接口下载这些数据都是可行的，只要在相应的函数接口传入正确的二进制数据即可。

其中 v3.bundle 是所有道具共用的数据文件，缺少该文件会导致系统初始化失败。其他每一个文件对应一个道具。自定义道具制作的文档和工具请联系我司获取。
  
## 集成方法
我们的系统需要EGL context的环境进行GPU绘制，并且所有API需要在同一线程调用。如果接入环境中没有OpenGL环境无法提供EGL context,可以调用 `fuCreateEGLContext` 进行创建，并且只需要在初始化时创建一次，并且请注意在退出需要销毁时调用`fuReleaseEGLContext`。

添加库文件的方式有以下2种,本质并无区别：

- 将 nama.jar 放在工程的 app/libs/ 文件夹下。将对应平台的 libnama.so 拷贝至 app/src/main/jniLibs/ 对应文件夹下。
之后在代码中加入
```Java
import com.faceunity.wrapper.faceunity
```
即可调用人脸跟踪及虚拟道具相关函数。
- gradle文件中增加`compile 'com.faceunity:nama:x.y.z'`，其中`x.y.z`为具体的版本号，和release note一致。

下面以使用GLSurfaceView搭配SDK Camera API为例，集成步骤主要分三步，另外全部API请参考`函数接口及参数说明`一节。

#### 环境初始化

在 GLSurfaceView 的 Renderer 的回调函数 onSurfaceCrated 中进行环境初始化，读取人脸数据 v3.bundle 文件，然后调用 fuSetup 。其中 g_auth_package 为密钥数组，没有密钥的话则传入 null 进行测试。

```Java
    InputStream is = mContext.getAssets().open("v3.bundle");
    byte[] v3data = new byte[is.available()];
    is.read(v3data);
    is.close();    
    faceunity.fuSetup(v3data, null, g_auth_package);
```

#### 道具的加载与销毁

请参考 fuCreateItemFromPackage 和 fuDestroyItem 文档注释。

道具加载：
```Java
    InputStream is = mContext.getAssets().open("YelloEar.bundle");
    byte[] item_data = new byte[is.available()];
    is.read(item_data);
    is.close();
    m_items[0] = faceunity.fuCreateItemFromPackage(item_data);
```

`v3.3.8`之后已经支持道具异步加载，详细代码示例请参考demo的`CreateItemHandler`相关。

美颜加载：
```Java
    InputStream is = mContext.getAssets().open("face_beautification.bundle");
    byte[] item_data = new byte[is.available()];
    is.read(item_data);
    is.close();
    m_items[1] = faceunity.fuCreateItemFromPackage(item_data);
```

#### 道具绘制

Android平台上不同的绘制接口有很大的性能差异，目前性能最优的接口是 fuDualInputToTexture ，其中要求输入的图像分别以内存数组 byte[] 以及 OpenGL 纹理的方式输入，所需要的数据传输代价最小。这2个参数的获取根据Android SDK Camera的API，分别得到对应的texture和byte[]数组。

fuDualInputTexture参数里的flags为0是代表`TEXTURE_2D`,为1时代表`TEXTURE_EXTERNAL_OES`。需要注意，Android Camera默认的类型是`TEXTURE_EXTERNAL_OES`。

以GLSurfaceView对接为例，的Renderer的回调函数onDrawFrame中，使用fuDualInputToTexture后会得到新的texture，返回的texture类型为TEXTURE_2D。将生成的新的texture进行绘制显示即可实现虚拟道具工具的集成预览，建议额外注意texture的类型。同时，在onDrawFrame中，可以调用fuIsTracking来判断实时人脸跟踪识别状态。

fuDualInputTexture调用例程如
```Java
    newTexId = faceunity.fuDualInputToTexture(m_cur_image, texId, 1, texWidth, texHeight, m_frame_id++, m_items);
```

## 视频美颜

美颜功能实现步骤与道具类似，首先加载美颜道具，并将 fuCreateItemFromPackage 返回的美颜道具handle保存下来，如例程中的 m_items[1]。

之后，将该handle和其他需要绘制的道具一起传入绘制接口即可。加载美颜道具后不需设置任何参数，即可启用默认设置的美颜的效果。

美颜道具主要包含五个模块的内容，滤镜，美白和红润，磨皮，美型。每个模块可以调节的参数如下。

#### 滤镜

在目前版本中提供以下滤镜：

普通滤镜：

```Java
"origin", "delta", "electric", "slowlived", "tokyo", "warm"
```

美颜滤镜：

```Java
"ziran", "danya", "fennen", "qingxin", "hongrun"
```

其中 "origin" 为原图滤镜，其他滤镜属于风格化滤镜及美颜滤镜。滤镜由参数 filter_name 指定。切换滤镜时，通过 fuItemSetParams 设置美颜道具的参数，如下：

```Java
//  Set item parameters - filter
faceunity.fuItemSetParam(m_items[1], "filter_name", "nature");
```

另外滤镜开放了滤镜强度接口，详细信息可到[这里](https://github.com/Faceunity/FULiveDemoDroid/tree/master/docs/%E8%A7%86%E9%A2%91%E7%BE%8E%E9%A2%9C%E6%9B%B4%E6%96%B0.md)查看详细信息。

#### 美白和红润

通过参数 color_level 来控制美白程度。该参数的推荐取值范围为[0, 1]，0为无效果，0.5为默认效果，大于1为继续增强效果。

设置参数的例子代码如下：

```Java
//  Set item parameters - whiten
faceunity.fuItemSetParam(m_items[1], "color_level", 1.0);
```
新版美颜新增红润调整功能。参数名为 red_level 来控制红润程度。使用方法基本与美白效果一样。该参数的推荐取值范围为[0, 1]，0为无效果，0.5为默认效果，大于1为继续增强效果。


#### 磨皮

新版美颜中，控制磨皮的参数有两个：blur_level、use_old_blur。

参数 blur_level 指定磨皮程度。该参数的推荐取值范围为[0, 6]，0为无效果，对应7个不同的磨皮程度。

参数 use_old_blur 指定是否使用旧磨皮。该参数设置为0即使用新磨皮，设置为大于0即使用旧磨皮

设置参数的例子代码如下：

```Java
//  Set item parameters - blur
fuItemSetParamd(g_items[1], "blur_level", 6.0);

//  Set item parameters - use old blur
fuItemSetParamd(g_items[1], "use_old_blur", 1.0);
```

如果对默认的7个磨皮等级不满意，想进一步自定义磨皮效果，可以联系我司获取内部参数调节的方式。

#### 美型

目前我们支持四种基本脸型：女神、网红、自然、默认。由参数 face_shape 指定：默认（3）、女神（0）、网红（1）、自然（2）。

```C
//  Set item parameters - shaping
fuItemSetParamd(g_items[1], "face_shape", 3);
```

在上述四种基本脸型的基础上，我们提供了以下三个参数：face_shape_level、eye_enlarging、cheek_thinning。

参数 face_shape_level 用以控制变化到指定基础脸型的程度。该参数的取值范围为[0, 1]。0为无效果，即关闭美型，1为指定脸型。

若要关闭美型，可将 face_shape_level 设置为0。

```C
//  Set item parameters - shaping level
fuItemSetParamd(g_items[1], "face_shape_level", 1.0);
```

参数 eye_enlarging 用以控制眼睛大小。此参数受参数 face_shape_level 影响。该参数的推荐取值范围为[0, 1]。大于1为继续增强效果。

```C
//  Set item parameters - eye enlarging level
fuItemSetParamd(g_items[1], "eye_enlarging", 1.0);
```

参数 cheek_thinning 用以控制脸大小。此参数受参数 face_shape_level 影响。该参数的推荐取值范围为[0, 1]。大于1为继续增强效果。

```C
//  Set item parameters - cheek thinning level
fuItemSetParamd(g_items[1], "cheek_thinning", 1.0);
```

## 手势识别
目前我们的手势识别功能也是以道具的形式进行加载的。一个手势识别的道具中包含了要识别的手势、识别到该手势时触发的动效、及控制脚本。加载该道具的过程和加载普通道具、美颜道具的方法一致。

线上例子中 heart.bundle 为爱心手势演示道具。将其作为道具加载进行绘制即可启用手势识别功能。手势识别道具可以和普通道具及美颜共存，类似美颜将 m_items 扩展为三个并在最后加载手势道具即可。

自定义手势道具的流程和2D道具制作一致，具体打包的细节可以联系我司技术支持。


## 注意

所有Faceunity的函数都需要在有OpenGL context的同一线程中运行。

建议在GL context lost相关的事件里释放掉所有道具，到了绘制的时候再通过判断重新创建出来。如Activity的onPause生命周期时，进行资源的回收及并主动调用fuOnDeviceLost。

## 鉴权

我们的系统通过标准TLS证书进行鉴权。客户在使用时先从发证机构申请证书，之后将证书数据写在客户端代码中，客户端运行时发回我司服务器进行验证。在证书有效期内，可以正常使用库函数所提供的各种功能。没有证书或者证书失效等鉴权失败的情况会限制库函数的功能，在开始运行一段时间后自动终止。

证书类型分为**两种**，分别为**发证机构证书**和**终端用户证书**。

#### - 发证机构证书
**适用对象**：此类证书适合需批量生成终端证书的机构或公司，比如软件代理商，大客户等。

发证机构的二级CA证书必须由我司颁发，具体流程如下。

1. 机构生成私钥
机构调用以下命令在本地生成私钥 CERT_NAME.key ，其中 CERT_NAME 为机构名称。
```
openssl ecparam -name prime256v1 -genkey -out CERT_NAME.key
```

2. 机构根据私钥生成证书签发请求
机构根据本地生成的私钥，调用以下命令生成证书签发请求 CERT_NAME.csr 。在生成证书签发请求的过程中注意在 Common Name 字段中填写机构的正式名称。
```
openssl req -new -sha256 -key CERT_NAME.key -out CERT_NAME.csr
```

3. 将证书签发请求发回我司颁发机构证书

之后发证机构就可以独立进行终端用户的证书发行工作，不再需要我司的配合。

如果需要在终端用户证书有效期内终止证书，可以由机构自行用OpenSSL吊销，然后生成pem格式的吊销列表文件发给我们。例如如果要吊销先前误发的 "bad_client.crt"，可以如下操作：
```
openssl ca -config ca.conf -revoke bad_client.crt -keyfile CERT_NAME.key -cert CERT_NAME.crt
openssl ca -config ca.conf -gencrl -keyfile CERT_NAME.key -cert CERT_NAME.crt -out CERT_NAME.crl.pem
```
然后将生成的 CERT_NAME.crl.pem 发回给我司。

#### - 终端用户证书
**适用对象**：直接的终端证书使用者。比如，直接客户或个人等。

终端用户由我司或者其他发证机构颁发证书，对于Android平台，出于Android平台易于逆向工程的考虑，需要通过我司的证书工具生成一个`authpack.java`文件交给用户。该类含有一个静态方法，返回内容是加密之后的证书数据，类型为byte数组，形式如下：

```
public class authpack {
  ...
  public static byte[] A() {
    ...
  }
}
```

用户在库环境初始化时，需要提供该数组进行鉴权，具体参考 fuSetup 接口。没有证书、证书失效、网络连接失败等情况下，会造成鉴权失败，在控制台或者Android平台的log里面打出 "not authenticated" 信息，并在运行一段时间后停止渲染道具。

任何其他关于授权问题，请email：support@faceunity.com

## FAQ

### 为什么人脸无法识别

* 检查图像自身正确性
* 检查图像自身格式
* 检查传参图像宽高
* 检查库文件是否配套完全更新，如v3.bundle有无更新

### 为什么过了一段时间人脸识别失效了？
* 检查证书。如证书是否正确使用，是否过期。

### 后置摄像头道具镜像

参考demo使用`fuDualInputToTexture`或`fuRenderToNV21Image`时传参的`flag`，需要镜像道具时请设置上`faceunity.FU_ADM_FLAG_FLIP_X`。

## 函数接口及参数说明

```java
public static final int FU_ADM_FLAG_EXTERNAL_OES_TEXTURE = 1;
public static final int FU_ADM_FLAG_ENABLE_READBACK = 2;//<set this to additionally readback the rendering result to `img`
public static final int FU_ADM_FLAG_NV21_TEXTURE = 4;
public static final int FU_ADM_FLAG_I420_TEXTURE = 8;
public static final int FU_ADM_FLAG_I420_BUFFER = 16;
public static final int FU_ADM_FLAG_FLIP_X = 32;
public static final int FU_ADM_FLAG_FLIP_Y = 64;
public static final int FU_ADM_FLAG_RGBA_BUFFER = 128;
    
/**
\brief Initialization, must be called exactly once before all other functions.
  Unlike the native version, you CAN discard the buffers after `fuInit` returns.
\param v2data should contain contents of the "v2.bin" we provide
\param ardata should contain contents of the "ar.bin" we provide
\param authdata should be the constant array we provide in "authpack.h"
*/
void fuSetup(byte[] v2data,byte[] ardata,byte[] authdata);

/**
\brief Create an accessory item from a binary package, you can discard the data after the call.
  This function MUST be called in the same GLES context / thread as fuRenderItems.
\param data should contain the package data
\return an integer handle representing the item
*/
void fuCreateItemFromPackage(byte[] data);

/**
\brief Destroy an accessory item.
  This function MUST be called in the same GLES context / thread as the original fuCreateItemFromPackage.
  We MUST NOT destroy items in the wrong GLES context, or unpredictable things will happen.
  If the GLES context has been lost outside our control, we'd better just throw away the handle and let the resources leak.
\param item is the handle to be destroyed
*/
void fuDestroyItem(int item);

/**
\create a OpenGL ES 2.0 context
*/
void fuCreateEGLContext();

/**
void fuReleaseEGLContext();
*/

/**
 \brief Render a list of items on top of an NV21 image.
 This function needs a GLES 2.0+ context.
 \param img specifies the NV21 img. Its content will be overwritten by the rendered image when fuRenderToNV21Image returns
 \param w specifies the image width
 \param h specifies the image height
 \param frameid specifies the current frame id.
 To get animated effects, please increase frame_id by 1 whenever you call this.
 \param items contains the list of items
 \return a GLES texture containing a copy of the rendered image
 */
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items);
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags, int readback_w, int readback_h, byte[] readback_img);

public static native int fuRenderToI420Image(byte[] img, int w, int h, int frame_id, int[] items);
public static native int fuRenderToI420Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
public static native int fuRenderToI420Image(byte[] img, int w, int h, int frame_id, int[] items, int flags, int readback_w, int readback_h, byte[] readback_img);

public static native int fuRenderToRgbaImage(byte[] img, int w, int h, int frame_id, int[] items);
public static native int fuRenderToRgbaImage(byte[] img, int w, int h, int frame_id, int[] items, int flags);
public static native int fuRenderToRgbaImage(byte[] img, int w, int h, int frame_id, int[] items, int flags, int readback_w, int readback_h, byte[] readback_img);

/**
\brief The fastest Android interface
  This function needs a GLES 2.0+ context.
\param img specifies the NV21 img.
\param texid specifies the GLES texture whose content matches `img`
\param flags if the FU_ADM_FLAG_EXTERNAL_OES_TEXTURE bit is set in the flags, texid is interpreted as a GL_TEXTURE_EXTERNAL_OES texture
  otherwise, texid is interpreted as a GL_TEXTURE_2D texture
\param w specifies the image width
\param h specifies the image height
\param frameid specifies the current frame id. 
  To get animated effects, please increase frame_id by 1 whenever you call this.
\param items contains the list of items
\return a new GLES texture containing the rendered image
*/
int fuDualInputToTexture(byte[] img,int tex_in,int flags,int w,int h,int frame_id, int[] items);
int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] items, int readback_w, int readback_h, byte[] readback_img);


/**
\brief Release resources allocated by the Java version of fuInit and destroy all created items.
  If you ever intend to call the other functions again, you need to re-invoke fuInit before calling them.
*/
void fuDone();

/**
\brief Call this function when the GLES context has been lost and recreated.
  Our library isn't designed to cope with that... yet.
  So this function leaks resources on each call.
*/
void fuOnDeviceLost();

/**
\brief Set an item parameter
\param item specifies the item
\param name is the parameter name
\param value is the parameter value to be set
\return zero for failure, non-zero for success
*/
int fuItemSetParam(int item,String name,double value);
int fuItemSetParam(int item,String name,double[] value);
int fuItemSetParam(int item,String name,String value);

/**
\brief Get the face tracking status
\return zero for not tracking, non-zero for tracking
*/
int fuIsTracking();
```
