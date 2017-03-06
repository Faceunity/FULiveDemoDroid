# FULiveDemoDroid

FULiveDemoDroid 是集成了 Faceunity 面部跟踪和虚拟道具功能的 Android Demo。

## v3.2 爱心手势识别
在v3.2中加入了爱心手势识别，用户比出爱心手势，可以触发特定的道具动效。目前线上提供了一个简单的演示用手势道具，自定义手势道具的流程和2D道具制作一致，具体打包的细节可以联系我司技术支持。

手势识别的技术细节参见[这里](https://github.com/Faceunity/FULiveDemoDroid#手势识别)。

## v3.1 美颜更新
在v3.1中，全面更新了美颜的功能和效果。改进了磨皮算法，使得在细腻皮肤的同时充分保持皮肤的细节，减少涂抹感。增加智能美型功能，可以自然地实现瘦脸和大眼效果，并可根据需要进行调节。具体细节可以参见[这里](https://github.com/Faceunity/FULiveDemoDroid#视频美颜)。

## v3.0 重要更新
在v3.0中，全面升级了底层人脸数据库，数据库大小从原来的 10M 缩小到 3M ，同时取消了之前的 ar.mp3 数据。新的数据库可以支持稳定的全头模型，从而支持更好的道具定位、面部纹理；同时新的数据库强化了跟踪模块，从而提升虚拟化身道具的表情响应度和精度。

由于升级了底层数据表达，v2.0 版本下的道具将全面不兼容。我司制作的道具请联系我司获取升级之后的道具包。自行制作的道具请联系我司获取道具升级工具和技术支持。

v2.0 版本的系统仍然保留在 v2 分支中，但不再进行更新。

## Libraries
  - nama.jar function call interfaces
  - libnama.so core library of face tracking and items rendering
  
## Data Files
*.mp3 under the directory app/src/main/assets/ is the program data file. Data files use the binary format and are extension-independent. Using mp3 as an extension avoids extra compression when packaging data. Actually either packaging these data into the program or downloading them from network interfaces is feasible for our app in use, as long as proper binary data are passed to corresponding function interfaces.

v3.map3 is shared by all items. Initialization will fail without this file. Each of the other files corresponds to one item. Please contact with our company if you need documents and tools to customize items.
  
## Integration Method
Our system needs the environment of EGL context to render on GPU and all APIs need to be called in the same thread. If the access environment has no OpenGL to provide EGL context, you can call `fuCreateEGLContext` to create it only once during initialization.

Put name.jar into the file under the project's app/libs/ files. Copy libname.so of the correspoingding platform to relative files under app/src/main/jniLibs/. Afterwards include the following in the code:
```Java
import com.faceunity.wrapper.faceunity
```
Then functions related to face tracking and virtual items are available to call.

We will take GLSurfaceView along with Camera as an example to illustrate the integration procedure which contains 3 steps. Moreover, please refer to function interfaces and parameter specification for all APIs


#### Environment Initialization

Initialize the environment in the callback function onSurfaceCrated of Renderer in GLSurfaceView; read the face data file v3.mp3 and then call fuSetup where g_auth_package is an array of the cryptographic key. Pass null for test if you do not own the cryptographic key.

```Java
    InputStream is = mContext.getAssets().open("v3.mp3");
    byte[] v3data = new byte[is.available()];
    is.read(v3data);
    is.close();    
    faceunity.fuSetup(v3data, null, g_auth_package);
```

#### Items Loading and Destruction

Please refer to fuCreateItemFromPackage and fuDestroyItem documentation annotation. 

Load Items：
```Java
    InputStream is = mContext.getAssets().open("YelloEar.mp3");
    byte[] item_data = new byte[is.available()];
    is.read(item_data);
    is.close();
    m_items[0] = faceunity.fuCreateItemFromPackage(item_data);
```

Load Face Beautification
```Java
    InputStream is = mContext.getAssets().open("face_beautification.mp3");
    byte[] item_data = new byte[is.available()];
    is.read(item_data);
    is.close();
    m_items[1] = faceunity.fuCreateItemFromPackage(item_data);
```

#### Render Items

Different rendering interfaces on the Android platform have great performance differences. Currently,the optimal interface is fuDualInputToTexture which has the least input cost and requires the input images to be stored in memory array byte[] and OpenGL textrue respectively. To obtain these two parameters, the corresponding texture and byte[] can be get from Android SDK Camera APIs.

The parameter flags in fuDualInputTexture represents `TEXTURE_2D` and `TEXTURE_EXTERNAL_OES` when being 1. Please note that the default type of Android Camera is `TEXTURE_EXTERNAL_OES`。

Take GLSurfaceView docking as an example. In the callback function onDrawFrame of Renderer, use fuDualInputToTexture to generate a new texture which is TEXTURE_2D type. Render and display the new texture, then the integration preview of virtual items tools can achieve. We recommend you to pay more attention to the type of texture. Meanwhile, calling fuIsTracking in onDrawFrame can estimate situations of real-time face tracking and recognition.

The sample of calling fuDualInputTexture is as follows.
```Java
    newTexId = faceunity.fuDualInputToTexture(m_cur_image, texId, 1, texWidth, texHeight, m_frame_id++, m_items);
```

## Face Beautification in Video

The implementation of face beautification resembles that of items. Firstly load a face beautification item and store the face beautification item handle returned from fuCreateItemFromPackage, such as m_items[1] in the sample.

Afterwards pass this handle and other items which need to render to the rendering interface. The default face beautification effect can be enabled without setting any parameters after loading face beautification items.

Face beautification items mainly contain four modules: filter, whiten, blur, shape. Each module's settable parameters are as follows.

#### Filter

Filters provided in the current version are as follows:
```Java
"nature", "delta", "electric", "slowlived", "tokyo", "warm"
```

where "nature" is a default whitening filter and others are stylize ones.  Use function fuItemSetParams to set parameters of face beautification items when switching filters,for example:
```C
//  Set item parameters - filter
faceunity.fuItemSetParam(m_items[1], "filter_name", "nature");
```

#### Whiten

When filter is set to whitening filter "nature", whitening level is controlled by color_level parameter. When filter is set to the other stylize filters, this parameter is used to control stylize level. This parameter take the value of nonnegtive floating point numbers where 0 means no effect, 1 means the default effect, and over 1 means strengthening effects further.

The sample code of setting parameters is as follows:

```C
//  Set item parameters - whiten
faceunity.fuItemSetParam(m_items[1], "color_level", 1.0);
```

#### Blur

In the update version of face beautification, the blur parameter is changed to compound type: blur_level, which takes the value from 0 to 6 corresponding to 7 different blur levels.

The sample code of setting parameters is as follows:


```C
//  Set item parameters - blur
faceunity.fuItemSetParam(m_items[1], "blur_level", 5.0);
```

If you are not pleased with the default 7 blur levels and want to customize blur effects further, please contact with our company to obtain methods for adjusting parameters.

#### Shape

We support two types of shaping modes currently, thinning the cheek and enlarging eyes, controlled by cheek_thinning and eye_enlarging respectively. Both parameters take values of nonnegtive floating point numbers where 0 means no effect, 1 means the default effect, and over 1 means strengthening effect further.

The sample code of setting parameters is as follows:

```C
//  Set item parameters - shaping
faceunity.fuItemSetParam(m_items[1], "cheek_thinning", 1.0);
faceunity.fuItemSetParam(m_items[1], "eye_enlarging", 1.0);
```

## Gesture Recognition
Currently, our gesture recognition function is also loaded in the form of items. An item of gesture recognition concludes gestures to recognize, animations that are triggered when gestures are recognized, and control scripts. The process of loading those items is the same as common items and face beautification items.

In the online sample, heart.bundle is an item demo showing a love heart gesture. Load it as an item to render and then the gesture recognition function is enabled. Gesture recognition items can coexist with common items and face beautification, similar to face beautification extending the item to three and loading the gesture recognition items at last.

The procedure of gestures customization is the same as that of 2D items. Please contact with our company for technology support if you need specific package details.


## Note

All functions of Faceunity need to execute in the same thread with OpenGL context.

We advise to release all items in events related to GL context lost and re-create them depending on judgement when rendering. For example, recovery resources and call fuOnDeviveceLost actively during the lifespan of Activity's onPause.

## Authentication

Our system is authenticated by the standard TLS certificate. Customers need to apply the certificate from the issuing authority at first and then write certificate data into the client code. Clients in runtime will send data to our company's server which will verify them. During the valid period of the certificate, all library functions are available for use. Authentication failures such as no certificate or invalid certificates will limit library functions and the system will terminate automatically after running for a while.

Certificates have **two types**，**issuing authority certificate** and **terminal user certificate** respectively.

#### - Issuing Authority Certificate
**Suitable Users**: institutions or companies who need to generate mass terminal certificates, such as software agents, big customers, etc.

Issuing authority CA certificate II must be issued by our company. The specific procedure is as follows.

1. The institution generates the private key
 The institution calls the following command to generate the private keys CERT_NAME.key locally where CERT_NAME is the institution name.
```
openssl ecparam -name prime256v1 -genkey -out CERT_NAME.key
```

2. The institution generates a certificate signing request using the private key. The institution call the following command with the local private key to generate certificate signing request CERT_NAME.csr. Fill the institution official name in Common Name field during the generation of certificate signing request.
```
openssl req -new -sha256 -key CERT_NAME.key -out CERT_NAME.csr
```

3. The certificate signing request is returned and our company issues the authority certificate.

Afterwards, issuing authorities can distribute certificates to terminal users independently and do not need to cooperate with our company.

If it is necessary to terminate the terminal users' certificates during the certificate valid period, the authority can revoke certificates on its own by OpenSSL and then send us the revoking list files in the pem format. For example, if revoking the previous mis-distributed certificate "bad_client.crt", the authority can operate as follows:
```
openssl ca -config ca.conf -revoke bad_client.crt -keyfile CERT_NAME.key -cert CERT_NAME.crt
openssl ca -config ca.conf -gencrl -keyfile CERT_NAME.key -cert CERT_NAME.crt -out CERT_NAME.crl.pem
```
Then send the generated CERT_NAME.crl.pem to our company.

#### - Terminal User Certificate
**Suitable Users**：direct terminal certificate users, such as direct customers, individuals, etc.

Terminal users can obtain certificates from our company or other issuing authorities. As for the Android platform, since it is apt for reverse Engineering, our company will deliver a `authpack.java` file to users through our certificate tool. This class contains a static method which return a byte array with encrypted certificate data in it. The form is as follows:

```
public class authpack {
  ...
  public static byte[] A() {
    ...
  }
}
```

The array is needed for authentication when users initialize the library environment. Refer to fuSetup interface for details. Under the circumstances of no certificate, invalid certificates or network connection failures,authentication failures will occur along with the console or log in Android platform printing "not authenticated" information, and the system will stop rendering items after running for a while.

If you have any other questions about authorization, please email us: support@faceunity.comsupport@faceunity.com

## FAQ
### Why face recognition function does not work after a period?
Check the certificate, such as whether the certificate has been applied properly or expired.



## Function Interfaces and Parameter Specification

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
void fuReleaseEGLContext();
*/

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
