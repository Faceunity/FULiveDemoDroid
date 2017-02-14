# FULiveDemoDroid

FULiveDemoDroid 是 Faceunity 的面部跟踪和虚拟道具功能在Android SDK中的集成，作为一款集成示例。

## v3.1 美颜更新
在v3.1中，全面更新了美颜的功能和效果。改进了磨皮算法，使得在细腻皮肤的同时充分保持皮肤的细节，减少涂抹感。增加智能美型功能，可以自然地实现瘦脸和大眼效果，并可根据需要进行调节。

## v3.0 重要更新
在v3.0中，全面升级了底层人脸数据库，数据库大小从原来的 10M 缩小到 3M ，同时取消了之前的 ar.mp3 数据。新的数据库可以支持稳定的全头模型，从而支持更好的道具定位、面部纹理；同时新的数据库强化了跟踪模块，从而提升虚拟化身道具的表情响应度和精度。

由于升级了底层数据表达，v2.0 版本下的道具将全面不兼容。我司制作的道具请联系我司获取升级之后的道具包。自行制作的道具请联系我司获取道具升级工具和技术支持。

v2.0 版本的系统仍然保留在 v2 分支中，但不再进行更新。

## 库文件
  - nama.jar 函数调用接口
  - libnama.so 人脸跟踪及道具绘制核心库
  
## 数据文件
目录 app/src/main/assets/ 下的 \*.mp3 为程序的数据文件。数据文件中都是二进制数据，与扩展名无关，用mp3只是为了避免打包时额外的压缩。实际在app中使用时，打包在程序内或者从网络接口下载这些数据都是可行的，只要在相应的函数接口传入正确的二进制数据即可。

其中 v3.mp3 是所有道具共用的数据文件，缺少该文件会导致系统初始化失败。其他每一个文件对应一个道具。自定义道具制作的文档和工具请联系我司获取。
  
## 集成方法
我们的系统需要EGL context的环境进行GPU绘制，并且所有API需要在同一线程调用。如果接入环境中没有OpenGL环境无法提供EGL context,可以调用 `fuCreateEGLContext` 进行创建，并且只需要在初始化时创建一次。

将 nama.jar 放在工程的 app/libs/ 文件夹下。将对应平台的 libnama.so 拷贝至 app/src/main/jniLibs/ 对应文件夹下。
之后在代码中加入
```Java
import com.faceunity.wrapper.faceunity
```
即可调用人脸跟踪及虚拟道具相关函数。

下面以使用GLSurfaceView搭配Camera为例，集成步骤主要分三步，另外全部API请参考`函数接口及参数说明`一节。

#### 环境初始化

在 GLSurfaceView 的 Renderer 的回调函数 onSurfaceCrated 中进行环境初始化，读取人脸数据 v3.mp3 文件，然后调用 fuSetup 。其中 g_auth_package 为密钥数组，没有密钥的话则传入 null 进行测试。

```Java
    InputStream is = mContext.getAssets().open("v3.mp3");
    byte[] v3data = new byte[is.available()];
    is.read(v3data);
    is.close();    
    faceunity.fuSetup(v3data, null, g_auth_package);
```

#### 道具的加载与销毁

请参考 fuCreateItemFromPackage 和 fuDestroyItem 文档注释。

道具加载：
```Java
    InputStream is = mContext.getAssets().open("YelloEar.mp3");
    byte[] item_data = new byte[is.available()];
    is.read(item_data);
    is.close();
    m_items[0] = faceunity.fuCreateItemFromPackage(item_data);
```

美颜加载：
```Java
    InputStream is = mContext.getAssets().open("face_beautification.mp3");
    byte[] item_data = new byte[is.available()];
    is.read(item_data);
    is.close();
    m_items[1] = faceunity.fuCreateItemFromPackage(item_data);
```

#### 道具绘制

Android平台上不同的绘制接口有很大的性能差异，目前性能最优的接口是 fuDualInputToTexture ，其中要求输入的图像分别以内存数组 byte[] 以及 openGL 纹理的方式输入，所需要的数据传输代价最小。这2个参数的获取根据Android Camera的SDK，分别得到对应的texture和byte[]数组，具体实现可以参考Google的Grafika项目。

fuDualInputTexture参数里的flags为0是代表`TEXTURE_2D`,为1时代表`TEXTURE_EXTERNAL_OES`。Android Camera默认的类型是`TEXTURE_EXTERNAL_OES`。

在GLSurfaceView的Renderer的回调函数onDrawFrame中，使用fuDualInputToTexture后会得到新的texture，返回的texture类型为TEXTURE_2D。将生成的新的texture进行绘制显示即可实现虚拟道具工具的集成预览，绘制的texture的具体实现可参考Google的Grafika项目，建议额外注意texture的类型。此外，在onDrawFrame中，可以调用fuIsTracking来判断实时人脸跟踪识别状态。

fuDualInputTexture调用例程如
```Java
    newTexId = faceunity.fuDualInputToTexture(m_cur_image, texId, 1, texWidth, texHeight, m_frame_id++, m_items);
```

## 视频美颜

美颜功能实现步骤与道具类似，首先加载美颜道具，并将 fuCreateItemFromPackage 返回的美颜道具handle保存下来，如例程中的 m_items[1]。

之后，将该handle和其他需要绘制的道具一起传入绘制接口即可。加载美颜道具后不需设置任何参数，即可启用默认设置的美颜的效果。

美颜道具主要包含四个模块的内容，滤镜，美白，磨皮，美型。每个模块可以调节的参数如下。

#### 滤镜

在目前版本中提供以下滤镜：
```Java
"nature", "delta", "electric", "slowlived", "tokyo", "warm"
```

其中 "nature" 作为默认的美白滤镜，其他滤镜属于风格化滤镜。切换滤镜时，通过 fuItemSetParams 设置美颜道具的参数，如：
```C
//  Set item parameters - filter
faceunity.fuItemSetParam(m_items[1], "filter_name", "nature");
```

#### 美白

当滤镜设置为美白滤镜 "nature" 时，通过参数 color_level 来控制美白程度。当滤镜为其他风格化滤镜时，该参数用于控制风格化程度。该参数取值为大于等于0的浮点数，0为无效果，1为默认效果，大于1为继续增强效果。

设置参数的例子代码如下：

```C
//  Set item parameters - whiten
faceunity.fuItemSetParam(m_items[1], "color_level", 1.0);
```

#### 磨皮

新版美颜中磨皮的参数改为了一个复合参数 blur_level ，其取值范围为0-5，对应6个不同的磨皮程度。

设置参数的例子代码如下：

```C
//  Set item parameters - blur
faceunity.fuItemSetParam(m_items[1], "blur_level", 5.0);
```

如果对默认的6个磨皮等级不满意，想进一步自定义磨皮效果，可以联系我司获取内部参数调节的方式。

#### 美型

目前我们支持两种美型模式，瘦脸和大眼，分别由 cheek_thinning 和 eye_enlarging 控制效果的强弱。两个参数的取值都为大于等于0的浮点数，0为关闭效果，1为默认效果，大于1为进一步增强效果。

设置参数的例子代码如下：
```C
//  Set item parameters - shaping
faceunity.fuItemSetParam(m_items[1], "cheek_thinning", 1.0);
faceunity.fuItemSetParam(m_items[1], "eye_enlarging", 1.0);
```


## 注意

注意所有Faceunity的函数都需要在有OpenGL context的同一线程中运行。

Activity的onPause生命周期时，进行资源的回收及对应GLSurfaceView的pause并主动调用faceunity的onDeviceLost函数。

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
### 为什么过了一段时间人脸识别失效了？
检查证书。

### 为什么软编不显示道具？
七牛的推流SDK软编的时候需要联网认证有推流地址才会回调。软编效果和硬编预览一致。



## 函数接口及参数说明

```java
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
\brief Render a list of items on top of an NV21 image.
  This function needs a GLES 2.0+ context.
\param img specifies the NV21 img. Its content will be **overwritten** by the rendered image when fuRenderToNV21Image returns
\param w specifies the image width
\param h specifies the image height
\param frameid specifies the current frame id. 
  To get animated effects, please increase frame_id by 1 whenever you call this.
\param items contains the list of items
\return a GLES texture containing a copy of the rendered image
*/
int fuRenderToNV21Image(byte[] img,int w,int h,int frame_id, int[] items);

public static final int FU_ADM_FLAG_EXTERNAL_OES_TEXTURE=1;
public static final int FU_ADM_FLAG_ENABLE_READBACK=2;//<set this to additionally readback the rendering result to `img`
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


在最后应用中建议在GL context lost相关的事件里（比如Android.onPause）释放掉所有道具，到了绘制的时候再通过判断重新创建出来。
