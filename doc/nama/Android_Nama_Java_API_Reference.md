# Android Nama Java API Reference



### Updates：

2019-05-07 v6.0.2:

1. Added fuSetupLocal ，Support offline authentication

2019-03-31 v6.0.0：

1. Added fuSetFaceDetParam to set face detection parameters.
2. Added fuTrackFaceWithTongue to support tongue trackin.
3. Added fuSetTongueTracking, on/off tongue tracking.
4. Added the parameter expression_with_tongue(fuGetFaceInfo) to get the tongue information.
5. Abandoned fuLoadExtendedARData.

------

### Contents：

[TOC]

------

### 1. Introduction

This document is the infrastructure layer interface for FaceUnity face tracking and video effects development kit (Nama SDK for short). The Nama API in this document is the Native interface for direct development on PC/iOS/Android NDK/Linux. Development on the iOS and Android platforms can use the SDK's application-level interface (Objective-C/Java), which is closer to the platform-related development experience than the infrastructure layer interface in this article.

All SDK-related calls require sequential execution in the same thread, without support for multithreading. A small number of interfaces can be called asynchronously (such as props loading) and will be specifically noted in the remarks. Interfaces called by all main threads of the SDK need to keep the OpenGL context consistent, otherwise it will cause texture data exceptions. If you need to use the SDK's rendering function, all calls to the main thread need to pre-initialize the OpenGL environment, without initialization or incorrect initialization will cause a crash. Our environment requirement for OpenGL is GLES 2.0 or higher. For the specific call method, please refer to each platform demo.*

The infrastructure layer interface is classified into five categories according to the role of logic: initialization, propsloading, main running interface, destruction, functional interface, P2A related interface.

------

### 2. APIs
#### 2.1 Initialization

##### fuSetup 

```java
@Deprecated
public static native int fuSetup(byte[] v3data, byte[] ardata, byte[] authdata);
public static native int fuSetup(byte[] v3data, byte[] authdata);
```

**Interface ：**

Initialize the system environment, load system data, and perform network authentication. Must be executed before calling other interfaces of the SDK, otherwise it will cause a crash.

The first interface has been deprecated and the second one is recommended.

**Parameters：**

`v3data` v3.bundle : byte array

`ardata` : Abandoned. Pass Null 

`authdata` : Authentication data byte array

**Return Value：**

Returns a non-zero value for success and 0 for failure. If initialization fails, get the error code via ```fuGetSystemError```

**Comment：**

Once the app is launched, it only needs to be set once, where the authpack.A() authentication data is declared in authpack.java. A valid certificate must be configured for the SDK.

According to application requirements, authentication data can also be provided during runtime (such as network download), but it is necessary to pay attention to the risk of certificate leakage and prevent the abuse of certificates. ```int``` is used for data length to prevent cross-platform data type issues.  

Need to initialize in the place where there is GL Context.  

##### fuSetupLocal 

Initialize the system environment, load system data, and perform network authentication. Must be executed before calling other interfaces of the SDK, otherwise it will cause a crash.

```java
public static native int fuSetupLocal(byte[] v3data, byte[] ardata, byte[] authdata);
```

**Parameters：**

`v3data` v3.bundle : byte array

`ardata` : Abandoned. Pass Null 

`authdata` : Authentication data byte array

**Return Value：**

Returns a non-zero value for success and 0 for failure. If initialization fails, get the error code via ```fuGetSystemError```

**Comment：**

Once the app is launched, it only needs to be set once, where the authpack.A() authentication data is declared in authpack.java. A valid certificate must be configured for the SDK.

According to application requirements, authentication data can also be provided during runtime (such as network download), but it is necessary to pay attention to the risk of certificate leakage and prevent the abuse of certificates. ```int``` is used for data length to prevent cross-platform data type issues.  

Need to initialize in the place where there is GL Context.    

-----

#### 2.2 Prop package loading

##### fuCreateItemFromPackage   

```java
public static native int fuCreateItemFromPackage(byte[] data);
```

**Interface：**

Load the prop package so that it can be executed in the main run interface. A prop package may be a function module or a collection of multiple function modules. The prop loading package may activate the corresponding function module and implement plug and play in the same SDK calling logic.

**Parameters：**

`data ` : Prop binary

**Return Value：**

`int ` : Created prop handle

**Comment：**

This interface can be executed asynchronously with the main thread. In order to reduce the loader blocking the main thread, it is recommended to call this interface asynchronously.

---

##### fuItemSetParam    

```java
public static native int fuItemSetParam(int item, String name, double value);
public static native int fuItemSetParam(int item, String name, double[] value);
public static native int fuItemSetParam(int item, String name, String value);
```

**Interface：**

Modify or set the value of the variable in the prop package. support the prop package variable name, meaning, and range of values (please refer to the prop document).

**Parameters：**

`item ` ：prop handle

`name ` : parameter name 

`value ` :parameter value，only support double 、 double[] 、String

**Return Value：**

`int ` : returns 0 means failed, greater than 0 means success.

---

##### fuItemGetParam    

```java
public static native double fuItemGetParam(int item,String name);
```

**Interface：**

get the value of the variable in the prop item. support the prop package variable name, meaning, and range of values (please refer to the prop document)

**Parameters：**

`item ` ：prop handle

`name ` : parameter name 

**Return Value：**

`double ` :parameter value

**Comment：**

This interface can be executed asynchronously with the main thread.

---

##### fuItemGetParamString   

```java
public static native String fuItemGetParamString(int item,String name);
```

**Interface：**

Get the string type variable in the prop. support the prop package variable name, meaning, and range of values (please refer to the prop document)。

**Parameters：**

`item ` ：prop handle

`name ` : parameter name 

**Return Value：**

`String ` ：parameter value

**Comment：**

This interface can be executed asynchronously with the main thread.

---

#### 2.3 Main running interface

##### fuDualInputToTexture  

```java
public static native int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] h);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

**Parameters：**

`img ` ：the image data byte[]，support ：NV21（default）、I420、RGBA

`tex_in ` : image data texture id

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

`w ` :The width of the input image

`h ` :The height of the input image

`frame_id ` :The current processed frame sequence number

`items ` : Int array containing multiple prop handles

**Return Value：**

`int `: The texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

This input mode reduces CPU-to-GPU data transfer and significantly optimizes performance on the Android platform, so it is recommended to use this interface.

---

##### fuDualInputToTexture  

```java
public static native int fuDualInputToTexture(byte[] img, int tex_in, int flags, int w, int h, int frame_id, int[] items, int readback_w, int readback_h, byte[] readback_img);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

**Parameters：**

`img ` ：the image data byte[]，support ：NV21（default）、I420、RGBA

`tex_in ` : image data texture id

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

`w ` :The width of the input image

`h ` :The height of the input image

`frame_id ` :The current processed frame sequence number

`items ` : Int array containing multiple prop handles

`readback_w `  :  the width of the image data that needs to be written back

`readback_h ` ：the height of the image data that needs to be written back

`readback_img ` : the image data byte[] to be written back 

**Return Value：**

`int `: The texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

This input mode reduces CPU-to-GPU data transfer and significantly optimizes performance on the Android platform, so it is recommended to use this interface.

---

##### fuRenderToNV21Image  

```java
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

This interface defaults to data writeback and will be written back to the corresponding img array with the same width and height.

**Parameters：**

`img ` :Image data byte[], the processed image data will be written back to the byte[]

`w ` : the width of image data

`h ` : the height of image data

`frame_id ` :The current processed frame sequence number

`items ` : Int array containing multiple prop handles

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

**Return Value：**

`int `: The texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

---

##### fuRenderToNV21Image   

```java
public static native int fuRenderToNV21Image(byte[] img, int w, int h, int frame_id, int[] items, int flags, int readback_w, int readback_h, byte[] readback_img);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

This interface defaults to data writeback and will be written back to the corresponding img array with the same width and height.

**Parameters：**

`img ` :Image data byte[], the processed image data will be written back to the byte[]

`w ` : the width of image data

`h ` : the height of image data

`frame_id ` :The current processed frame sequence number

`items ` : Int array containing multiple prop handles

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

`readback_w `  :  the width of the image data that needs to be written back

`readback_h ` ：the height of the image data that needs to be written back

`readback_img ` : the image data byte[] to be written back 

**Return Value：**

`int `: The texture ID of the output image after processing

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

---

##### fuRenderToI420Image   

```java
public static native int fuRenderToI420Image(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

This interface defaults to data writeback and will be written back to the corresponding img array with the same width and height.

**Parameters：**

`img ` :Image data byte[], the processed image data will be written back to the byte[]

`w ` : the width of image data

`h ` : the height of image data

`frame_id ` :The current processed frame sequence number

`items ` : Int array containing multiple prop handles

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

**Return Value：**

`int `: The texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

---

##### fuRenderToRgbaImage   

```java
public static native int fuRenderToRgbaImage(byte[] img, int w, int h, int frame_id, int[] items, int flags);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

This interface defaults to data writeback and will be written back to the corresponding img array with the same width and height.

**Parameters：**

`img ` :Image data byte[], the processed image data will be written back to the byte[]

`w ` : the width of image data

`h ` : the height of image data

`frame_id ` :The current processed frame sequence number

`items ` : Int array containing multiple prop handles

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

**Return Value：**

`int `: The texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

---

##### fuRenderToYUVImage    

```java
public static native int fuRenderToYUVImage(byte[] y_buffer, byte[] u_buffer, byte[] v_buffer, int y_stride, int u_stride, int v_stride, int w, int h, int frame_id, int[] items, int flags);
```

**Interface：**

The input image data is sent to the SDK pipeline for processing, and output the processed image data. The interface will execute all the props required, and the certificate of the licensed function module, including face detection and tracking, beauty, stickers or avatar rendering.

Rending the props in items into the YUV three-channel image.

**Parameters：**

`y_buffer ` :Y frame image data byte[]

`u_buffer ` :U frame image data byte[]

`v_buffer ` :V frame image data byte[]

`y_stride ` :Y frame stride

`u_stride ` :U frame stride

`v_stride ` :V frame stride

`w ` : the width of image data

`h ` : the height of image data

`frame_id ` : the currently processed video frame number, which is incremented by 1 each time it is processed. Without adding 1 it will not be able to drive the effect animation in the item.

`items ` :an int array containing multiple prop handles, including common props, beauty props, gesture props, etc.

`flags ` flags: specify the data img data format, return the prop mirror of the texture ID, etc.

**Return Value：**

`int `: the texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

---

##### fuBeautifyImage   

```java
public static native int fuBeautifyImage(int tex_in, int flags, int w, int h, int frame_id, int[] items);
```

**Interface：**

The input image data is sent to the SDK pipeline for the whole picture to beautified, and the processed image data is output. This interface only performs image-level beautification (including filters, skins), and does not perform face tracking and all face-related operations (such as beauty). Due to the centralized functionality, this interface requires less computation and is more efficient to execute.

**Parameters：**

`tex_in ` : texture id for image data

`flags `  flags: specify the data img data format, return the prop mirror of the texture ID, etc.

`w ` : the width of image data

`h ` : the height of image data

`frame_id ` : the currently processed video frame number, which is incremented by 1 each time it is processed. Without adding 1 it will not be able to drive the effect animation in the item.

`items ` : an int array containing multiple prop handles, including common props, beauty props, gesture props, etc.

**Return Value：**

`int `: the texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

This interface will only take effect if you pass the beauty item (as the SDK is distributed, the file name is usually face_beautification.bundle).

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

---

##### fuAvatarToTexture 

```java
public static native int fuAvatarToTexture(float[] pupilPos, float[] expression, float[] rotation, float[] rmode, int flags, int w, int h, int frame_id, int[] items, int isTracking);
```

**Interface：**

The picture is rendering based on the face information obtained by the fuTrackFace.

**Parameters：**

`pupilPos ` :eye direction, length is 2

`expression `:  expression coefficient, length is 46

`rotation ` : the return value is the rotation quaternion, and the length is 4

`rmode ` : face orientation, 0-3 corresponds to the four orientations of the mobile phone, length 1

`flags `  flags: specify the data img data format, return the prop mirror of the texture ID, etc.

`w ` : the width of image 

`h ` : the height of image 

`frame_id ` : the currently processed video frame number, which is incremented by 1 each time it is processed. Without adding 1 it will not be able to drive the effect animation in the item.

`items ` : an int array containing multiple prop handles, including common props, beauty props, gesture props, etc.

`isTracking ` : whether the face is recognized or not, the value obtained by the fuIsTracking method can be directly passed.

**Return Value：**

`int `: the texture ID of the output image after processing. if the return value is less than or equal to 0, you can get the specific information via fuGetSystemError.

**Comment：**

The rendering interface requires an OpenGL environment, or an exception in the environment can cause a crash.

----

##### fuTrackFace   

```java
public static native void fuTrackFace(byte[] img, int flags, int w, int h);
```

**Interface：**

Only the face tracking operation is performed on the input image data, and all other image and rendering related operations are not executed, so the function has no image output. Since this function does not perform rendering-related operations and only contains CPU calculations, it can operate without an OpenGL environment. After the function executes the face tracking operation, the resulting face information is acquired through the```fuGetFaceInfo``` 

**Parameters：**

`img ` : image data byte[]

`flags ` format：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` : the width of image data

`h ` : the height of image data

**Return Value：**

`int ` : the number of faces detected, returning 0 means no face detected.

**Comment：**

This interface does not require a rendering environment and can be called outside of the rendering thread。

----

##### fuTrackFaceWithTongue  

```java
public static native void fuTrackFaceWithTongue(byte[] img, int flags, int w, int h);
```

**Interface：**

Same as ``` fuTrackFace ```，tracking the tongue's blendshape coefficient while tracking facial expressions.  
Only the face tracking operation is performed on the input image data, and all other image and rendering related operations are not executed, so the function has no image output. Since this function does not perform rendering-related operations and only contains CPU calculations, it can operate without an OpenGL environment. After the function executes the face tracking operation, the resulting face information is acquired through the```fuGetFaceInfo``` 

**Parameters：**

`img ` : image data byte[]

`flags ` format：`FU_FORMAT_RGBA_BUFFER` 、 `FU_FORMAT_NV21_BUFFER` 、 `FU_FORMAT_NV12_BUFFER` 、 `FU_FORMAT_I420_BUFFER`

`w ` : the width of image data

`h ` : the height of image data

**Return Value：**

`int ` : the number of faces detected, returning 0 means no face detected.

**Comment：**

Need to load tongue.bundle to make tongue tracking available.

---

#### 2.4 Destroy

##### fuDestroyItem 

```java
public static native void fuDestroyItem(int item);
```

**Interface：**

Destroy the props by the props handle and release the related resources. After destroying the props, set the prop handle to 0 to avoid the SDK from using invalid handles and causing program errors.

**Parameters：**

`item ` : prop handle

__Comment:__  

After the function is called, the prop identifier will be released immediately. The memory occupied by the prop cannot be released instantaneously. It needs to be recycled by the GC mechanism when the SDK executes the main processing interface.

---

##### fuDestroyAllItems 

```java
public static native void fuDestroyAllItems();
```

**Interface：**

Destroy the props by the props handle and release the related resources. After destroying the props, set the prop handle to 0 to avoid the SDK from using invalid handles and causing program errors.。

__Comment:__  

This function will immediately release the system's resources. However, ```fuSetup```'s system initialization information will not be destroyed. When the application is temporarily suspended to the background, the function can be called to release resources, and it is not necessary to reinitialize the system when it is activated again.

##### fuOnDeviceLost 

The special function, called when the OpenGL context is released/destroyed externally, is used to reset the system's GL state.

```java
public static native void fuOnDeviceLost();
```

__Comment:__  

This function is only called if the resource cannot be properly cleaned up in the original OpenGL context. When this function is called, it will try to clean up and recycle the resources. The memory resources occupied by all the systems will be released. However, due to the change of context, the memory related to OpenGL resources may leak.

---

#### 2.5 Functional interface - System

##### fuOnCameraChange  

```java
public static native void fuOnCameraChange();
```

**Interface：**

Called when the camera data source is switched (for example, front/back camera switch), to reset the face tracking status.

**Comment：**

In the case of other facial information remaining, you can also call this function to clear the residual face information 

-----
##### fuSetTongueTracking 

```java
public static native int fuSetTongueTracking(int enable);
```

**Interface：**

Turn on the tracking of the tongue

**Parameters：**

`enable`: 1 means on ，0 means off

__Comment:__  

When call the fuTrackFaceWithTongue interface, after loading the tongue.bundle, you need fuSetTongueTracking(1) to enable support for tongue tracking.

If the prop itself has a tongue bs, it does not need to be activated.

-------

##### fuSetFaceDetParam 

```java
public static native int fuSetFaceDetParam(String name, float value);
```

**Interface：**

Set the face detector related parameters, **it is recommended to use the default Parameters**

**Parameters：**

`name`: Parameters name

`value`: Parameters value

- `name == "use_new_cnn_detection"` ，and `pvalue == 1` the default CNN-Based face detection algorithm is used, otherwise pvalue == 0  uses the traditional face detection algorithm. This mode is turned on by default.
- name == "other_face_detection_frame_step"` ，If the current state has detected a face, you can set the parameter and perform other face detection every step` frame to help improve performance,default 10 frames.

if  `name == "use_new_cnn_detection"` ，and `pvalue == 1`  It's  on：

- `name == "use_cross_frame_speedup"`，`pvalue==1`Indicates that cross-frame execution is enabled, half of the network is executed per frame, and the lower half of the grid is executed in the next frame, which improves performance. Default pvalue==0   It's off.
- `name == "small_face_frame_step"`，`pvalue`Indicates how many frames are used to enhance small face detection. Very small face detection is very cost effective and not suitable for every frame. The default pvalue==5.
- When detecting small faces, small faces can also be defined as ranges. The lower limit of the range name == "min_facesize_small", the default pvalue==18, which means that the minimum face is 18% of the screen width. The upper limit of the range name == "min_facesize_big", the default pvalue==27, which means that the minimum face is 27% of the screen width. The Parameters must be set before fuSetup.

else，if `name == "use_new_cnn_detection"` ，and  `pvalue == 0`：

- `name == "scaling_factor"`，Set the zoom ratio of the image pyramid. The default is 1.2f.

- `name == "step_size"`，The sliding interval of the sliding window, default 2.f.

- `name == "size_min"`，The minimum face size, which pixels. The default is 50.f pixels, with reference to 640x480 resolution.

- `name == "size_max"`，Maximum face size, which pixels. The default is the largest, reference 640x480 resolution.

- `name == "min_neighbors"`，Internal Parameters, default 3.f

- `name == "min_required_variance"`， Internal Parameters, default 15.f

  


__Return Value:__  

After setting the status, 1 means successful, 0 means failed. 



__Comment:__  

`name == "min_facesize_small"`，`name == "min_facesize_small"` Parameters must be set before fuSetup.

-----

##### fuSetAsyncTrackFace 

```java
public static native int fuSetAsyncTrackFace(int enable);
```

**Interface：**

Set the face tracking asynchronous interface. It is off by default.

**Parameters：**

`enable`: 1 means turn on asynchronous tracking，0 means turn off.

__Comment:__  

It is off by default. After being turned on, the face tracking will be asynchronous with the rendering , and the CPU usage will increase slightly, but the overall speed will increase and the frame rate will increase.

-----

##### fuSetDefaultOrientation 

```java
public static native int fuSetDefaultOrientation(int rmode);
```

**Interface：**

Set the default face orientation. Correctly setting the default face orientation can significantly increase the speed of first face recognition.  

**Parameters：**

`rmode`：To set the face orientation, the value range is 0-3, corresponding to the rotation of the human face relative to the image data by 0 , 90 , 180 , and 270 degrees. 

__Comment:__  

The native camera data of the Android platform is a horizontal screen and needs to be set to speed up the first recognition. According to experience, Android front camera is usually set to Parameters 1, and the rear camera is usually set to Parameters 3, except for some mobile phones. The auto-calculated code can refer to FULiveDemo.

----

##### fuIsTracking  

```java
public static native int fuIsTracking();
```

**Interface：**

Get current face tracking status and return the number of faces being tracked.

**Return Value：**

`int ` :The number of faces detected, returning 0 means no face detected

**Comment：**

The number of faces being tracked will be affected by the ```fuSetMaxFaces``` function but will not exceed the maximum value set by this function.

---

##### fuSetMaxFaces 

```java
public static native int fuSetMaxFaces(int n);
```

**Interface：**

Sets the maximum number of faces tracked by the system. The default value is 1, which increases the performance of the face tracking module and is recommended to be set to 1 in all situations where it can be designed as a single face.

**Parameters：**

`n`:   Set the number of faces opened in multiplayer mode, up to 8

**Return Value：**

`int ` :The number of faces set last time

---

##### fuGetFaceInfo 

```java
public static native int fuGetFaceInfo(int face_id, String name, float[] value);
```

**Interface：**

After the face tracking operation is performed on the main interface, the face tracking result information is obtained through the interface. Obtaining information requires a certificate to provide related permissions. Currently the face information permission includes the following levels: Default, Landmark, and Avatar.

**Parameters：**

`face_id ` The face ID to be detected is 0 when the multi-person detection is not turned on, indicating that the face information of the first person is detected; when multi-person detection is turned on, the value range is [0 ~ maxFaces-1], which is taken as the first Several values represent the face information of the first few people.

`name `:  face information： "landmarks" , "eye_rotation" , "translation" , "rotation" ....

`value ` : As a float array pointer used by the container, the obtained face information is directly written to the float array.

**Return Value**

`int ` : Return 1 means success, and information is returned via pret, and return 0 indicates that the information obtain failed. The specific failure information is printed on the platform console. If the return value is 0 and no console prints, the required face information is currently unavailable.

__Comment:__  

The information, meaning, and permission requirements for all support obtaining are as follows：

| Name           | Length | Meaning                                                      | Permission |
| -------------- | ------ | ------------------------------------------------------------ | ---------- |
| face_rect      | 4      | Face rectangular frame, image resolution coordinates, data (x_min, y_min, x_max, y_max) | Default    |
| rotation_mode  | 1      | Identify the orientation of the face relative to the rotation of the device image. Values range from 0 to 3, representing 0, 90, 180, and 270 degrees, respectively. | Default    |
| failure_rate   | 1      | The failure rate of face tracking indicates the quality of face tracking. Value range from 0 to 2. The lower the value, the higher the quality of face tracking. | Default    |
| is_calibrating | 1      | Indicates whether the SDK is performing an active expression calibration with a value of 0 or 1. | Default    |
| focal_length   | 1      | The focus values on SDK's current 3D face tracking           | Default    |
| landmarks      | 75x2   | Face 75 feature points, image resolution coordinates         | Landmark   |
| rotation       | 4      | 3D face rotation, data rotation quaternion\*                 | Landmark   |
| translation    | 3      | Face 3D translation，data (x, y, z)                          | Landmark   |
| eye_rotation   | 4      | Eyeball rotation, data rotation quaternion\*                 | Landmark   |
| expression     | 46     | Face Expression Coefficient, Expression Coefficient Meaning Refer to《Expression Guide》 | Avatar     |

*Notes：Rotary quaternion to Euler angle can be referenced [Url](https://en.wikipedia.org/wiki/Conversion_between_quaternions_and_Euler_angles)。

---

##### fuGetVersion  

```java
public static native String fuGetVersion();
```

**Interface：**

Get the current SDK version. A constant string pointer, the version number is as follows：
“Major version number\_sub version number\-Version check value”

**Return Value：**

`String ` : version detail 

----

##### fuGetModuleCode   

```java
public static native int fuGetModuleCode(int i);
```

**Interface：**

Obtain the authentication code .

**Parameters：**

`i` : pass 0 

**Return Value：**

`int `:  authentication code

----

##### fuGetSystemError 

```java
public static native int fuGetSystemError();
```

**Interface：**

System error is returned. This type of error is generally caused by a serious problem in the system mechanism, resulting in the system being shut down. Therefore, attention must be paid.

**Return Value：**

system error code.

**Comment：**

After returning the system error code, the most important error message can be resolved by the fuGetSystemErrorString function.



System error code and its meaning are as follows：

| Code  | Info                                            |
| ----- | ----------------------------------------------- |
| 1     | Random seed generation failed                   |
| 2     | Agency certificate parse failed                 |
| 3     | Authentication server connection failed         |
| 4     | Configure the encrypted connection failed       |
| 5     | Parse client certificate failed                 |
|       | Client key parse failed                         |
| 7     | Establish encrypted connection failed           |
| 8     | Set the authentication server address failed    |
| 9     | Encrypted connection handshake failed           |
| 10    | Verify the encrypted connection failed          |
| 11    | Request send failed                             |
| 12    | Response reception failed                       |
| 13    | Authentication response exception               |
| 14    | Certificate authority information is incomplete |
| 15    | Authentication function is not initialized      |
| 16    | Create authentication thread failed             |
| 17    | Authentication data is rejected                 |
| 18    | No authentication data                          |
| 19    | Authentication data exception                   |
| 20    | Expired certificate                             |
| 21    | Invalid certificate                             |
| 22    | System data parsing failed                      |
| 0x100 | Unofficial package loaded                       |
| 0x200 | Operation platform is prohibited by certificate |

----

##### fuGetSystemErrorString 

```java
public static native String fuGetSystemErrorString(int code);
```

**Interface：**

Parses the system error code and returns readable information

__Parameters:__  

`code`：system error code，The code is usually returned by ```fuGetSystemError```.

__Return Value:__  

A constant string explaining the meaning of the current error.

__Comment:__  

When a complex number error exists, this function returns the most important error message.

----

##### fuCheckDebugItem 

```java
public static native int fuCheckDebugItem(byte[] data);
```

**Interface：**

Check if a package is an informal package（debug item）.

__参数:__  

`data`：the byte array of prop package

__Return Value:__  

Return  0 indicates that the item is a formal item, a return value of 1 indicates that the item is an informal item (debug  item), and Return -1 indicates that the item data is abnormal.

__Comment:__  

If the system loads an unofficial version of the item, it will cause the system to enter a countdown and close at the end of the countdown. If the system prompts "debug item used", or if the system stops after running for 1 minute, you need to use this function to check all loaded items. If there are unoffical items, you need to use the correct item signature.

Props signature process please contact our technical support.

----

#### 2.6 Functioncal - Effect

##### fuSetExpressionCalibration  

```java
public static native void fuSetExpressionCalibration(int mode);
```

**Interface：**

Set facial expression calibration function. The purpose of this function is to enable the facial expression recognition module to adapt to different people's facial features in order to achieve a more accurate and controllable expression tracking effect.

This function has two modes, active calibration and passive calibration.

- Active calibration: In this mode, the system will perform quick focused facial expression calibration, usually 2-3 seconds after the first recognition of the human face. During this period of time, the user needs to remain as expressionless as possible, and the process begins to be used again. The beginning and end of the process can be returned parameter```is_calibrating``` via the ```fuGetFaceInfo``` 
- Passive calibration: In this mode, the expression will be gradually adjusted during the entire using, and the user does not have a clear feeling about the process. The intensity of this calibration is weaker than the active calibration.

The default is passive calibration.

**Parameters：**

`mode`:  0 is to turn off expression calibration, 1 is active calibration, and 2 is passive calibration.

__Comment:__  

When executing the main processing interface to process still pictures, since it is necessary to repeatedly call the same data, it is necessary to turn off the expression calibration function.

-----

##### fuLoadAnimModel   

```java
public static native int fuLoadAnimModel(byte[] data);
```

**Interface：**

Load expression animation models and enable expression optimization.

The expression optimization function can make the expression obtained after real-time tracking more natural and vivid, but it will introduce a certain expression delay.

**Parameters：**

`data` : byte array of emoticon data packets

__Return Value:__  

Return Value 1 indicates that the loading is successful and the expression optimization function is enabled. Return Value 0 represents failure.

----

##### fuSetStrictTracking  

```java
public static native void fuSetStrictTracking(int mode);
```

**Interface：**

Enable more rigorous tracking quality inspection

**Parameters：**

`mode`：0 is disabled, 1 is enabled, and the default is disabled.

-----

##### fuSetFocalLengthScale 

```java
public static native void fuSetFocalLengthScale(float scale);
```

**Interface：**

Modify the system focal length (effect is equivalent to focal length, or FOV), affect the perspective effect of 3D tracking, AR effects.

**Parameters：**

`scale`：The scale factor of the focus adjustment, 1.0 is the default value. The recommended range is 0.1 ~ 2.0.

__Comment:__  

A coefficient less than or equal to 0 is an invalid input.

-----



#### 2.7 Abandoned

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

### 3. Input and output format list

##### RGBA Array

The image memory array for RGBA format

#### Data format identifier

FU_FORMAT_RGBA_BUFFER

#### Data content

Contiguous memory space, length ```w*h*4```. The array element is ```int```, which represents the color information in RGBA.

#### Input and output support

 Input/Output 

#### __Comment:__

Due to memory alignment requirements on the platform, the actual width of the image memory space may not equal the semantic width of the image. When the main running interface passes in the image width, the actual width of the memory should be passed in.

------

##### BGRA Array

The image memory array for BGRA format

__Data format identifier

FU_FORMAT_BGRA_BUFFER

#### Data content

Contiguous memory space, length ```w*h*4```. The array element is ```int```, which represents the color information in BGRA.

#### Input and output support

 Input/Output 

#### __Comment:__

Due to memory alignment requirements on the platform, the actual width of the image memory space may not equal the semantic width of the image. When the main running interface passes in the image width, the actual width of the memory should be passed in.

This format is one of the native iOS camera data formats.

------

##### RGBA TEXTURE

OpenGL texture for RGBA format.

#### Data format identifier

FU_FORMAT_RGBA_TEXTURE

#### Data content

A ```GLuint``` indicates an OpenGL texture ID.

#### Input and output support

 Input/Output 

------

##### RGBA OES TEXTURE

OpenGL external OES texture for RGBA format.

#### Data format identifier

FU_FORMAT_RGBA_TEXTURE_EXTERNAL_OES

#### Data content

A ```GLuint``` indicates an OpenGL external OES texture ID.

#### Input and output support

Only input

#### __Comment:__

This format is one of the native Android camera data formats.

------

##### NV21 Array

An image memory array for NV21 format.

#### Data format identifier

FU_FORMAT_NV21_BUFFER

#### Data content

Continuous memory, the previous section is Y data, length is ```w*h```, the last section is UV data, length is ```2*((w+1)>>1)```(resolution Is half of Y but contains two UV channels). Two pieces of data are stored continuously in memory.

#### Input and output support

Input/Output

#### __Comment:__

This format requires UV data interleaved (eg: UVUVUVUV), such as separate storage of UV data (UUUUVVVV), please use the I420 array format.

This format is one of the native Android camera data formats.

------

##### NV12 Array

An image memory array for NV12 format.

#### Data format identifier

FU_FORMAT_NV12_BUFFER

#### Data content

The structure ```TNV12Buffer``` is defined as follows.

```c
typedef struct{
	void* p_Y; 
	void* p_CbCr;
	int stride_Y;
	int stride_CbCr;
}TNV12Buffer;
```

#### __Parameters:__

*p_Y*：Pointer to Y data.

*p_CbCr*：Pointer to UV data.

*stride_Y*：The length in bytes of each line of Y data.

*stride_CbCr*：The byte length of each row of UV data.



#### Input and output support

Input/Output



#### __Comment:__

This format is very similar to the NV21 array format, except that the U and V cross arrangement in the UV data is the opposite. However, this format supports separate storage of Y data and UV data, and no longer requires continuous data as a whole.

This format is one of the native iOS camera data formats.

------

##### I420 Array

The image memory array for I420 format

#### Data format identifier

FU_FORMAT_I420_BUFFER

#### Data content

Continuous memory, the first segment is Y data, the length is ```w*h```, the second segment is U data, and the length is ```((w+1)>>1)```, the third The segment is V data and is of length ```((w+1)>>1)``` (the resolution of the latter two channels is half of Y). The three pieces of data are stored continuously in memory.

#### Input and output support

Input/Output

#### Comment

This format is basically the same as the NV21 array, except that the U and V data are stored continuously.

------

##### Android Dual Input

Dual input format for Android native camera data. Dual input refers to the GPU data input - OpenGL texture in RGBA / NV21 / I420 format, and CPU memory data input - NV21 / RGBA / I420 format image memory array.

This input mode can reduce CPU-GPU data transfer once compared to a single data input that only provides a memory array or texture, and can significantly optimize performance on the Android platform, so it is recommended to use this interface whenever possible.

#### Data format identifier

FU_FORMAT_ANDROID_DUAL_MODE

#### Data content

The structure ```TAndroidDualMode``` is defined as follows.

```c
typedef struct{
	void* p_NV21;
	int tex;
	int flags;
}TAndroidDualMode;
```

#### Parameters

*p_NV21*：Pointer to memory image data.

*tex*：OpenGL texture ID。

*flags*：Extended function identifiers, all supported identifiers and their functions are as follows. Multiple identifiers are connected by the "or" operator.

| Extended function identifiers    | Meaning                                                      |
| -------------------------------- | ------------------------------------------------------------ |
| FU_ADM_FLAG_EXTERNAL_OES_TEXTURE | The incoming texture is OpenGL external OES texture          |
| FU_ADM_FLAG_ENABLE_READBACK      | Write processing results back to the incoming memory image data after starting |
| FU_ADM_FLAG_NV21_TEXTURE         | The incoming texture is NV21 data format                     |
| FU_ADM_FLAG_I420_TEXTURE         | The incoming texture is I420 data format                     |
| FU_ADM_FLAG_I420_BUFFER          | Incoming memory image data is in I420 data format            |
| FU_ADM_FALG_RGBA_BUFFER          | Incoming memory image data is in RGBA data format            |

#### Input and output support

Only Input

------

##### Current FBO

Refers to the currently bound OpenGL FBO when the main processing interface is invoked. The main processing interface can directly render the processing result onto this FBO.

#### Data format identifier

FU_FORMAT_GL_CURRENT_FRAMEBUFFER

#### Data content

NULL, the data pointer passes NULL directly.

#### Input and output support

Only Output

#### Comment

The creation of the FBO needs to be completed before the incoming FBO, including the binding of the color texture, which needs to pass the OpenGL integrity check.

If you have 3D rendering content, you need the FBO to have depth buffering.

------

##### Specify FBO

You can pass an externally prepared OpenGL FBO, not necessarily the currently bound FBO when calling the main processing interface. The main processing interface can directly render the processing result onto this FBO.

#### Data format identifier

FU_FORMAT_GL_SPECIFIED_FRAMEBUFFER

#### Data content

The structure ```TSPECFBO``` is defined as follows.

```c
typedef struct{
	int fbo;
	int tex;
}TSPECFBO;
```

#### Parameters

*fbo*：The specified FBO ID.

*tex*：The color texture ID bound to this FBO.

#### Input and output support

Only Output

#### Comment

The creation of the FBO needs to be completed before the incoming FBO, including the binding of the color texture, which needs to pass the OpenGL integrity check.

If you have 3D rendering content, you need to pass the FBO with depth buffer.

------

##### Avatar Driver Info

Special input data, not image data, but face-driven information, is used to drive the avatar model. The face driver information may be obtained after the main processing interface is executed, or may be externally input, such as an avatar video recording information or a user interaction generated message.

#### Data format identifier

FU_FORMAT_AVATAR_INFO

#### Data content

The structure ```TAvatarInfo``` is defined as follows.

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

#### Parameters

*p_translation*：Pointer to memory data, the data is 3 floats, representing the translation of the human face in the camera space. Where x/y is the image resolution and z is the depth of the human face in the camera space.

*p_rotation*：Pointer to memory data, the data is 4 floats, representing the three-bit rotation of the human head. Rotation is represented by a quaternion and needs to be transformed into Euler angle rotation.

*p_expression*：Pointer to the memory data, the data is 46 floats, representing the facial expression coefficient. The meaning of the expression coefficient please refer to《Expression Guide》。

*rotation_mode*：An int, which ranges from 0 to 3, represents the rotation of the human face with respect to the image data, representing 0, 90, 180, and 270 degrees, respectively.

*pupil_pos*：Pointer to memory data, data is 2 floats, representing the Parameters coordinates of the pupil. The coordinates themselves have no semantics and are generally obtained directly from the tracking results.

*is_valid*：An int indicates whether the avatar information is valid. If the value is 0, the system will not process the corresponding avatar information.

#### Input and output support

Only Input

#### Comment

This input mode can only be used with avatar props. Loading face AR props will cause an exception.

This input mode simplifies the processing of incoming image data and the higher performance  under avatar application scenarios. In addition, the control over avatar is more flexible and allows the user to control avatar freely, such as dragging avatar turns, triggering specific expressions, and so on.

------

### 4. FAQ 

#### 4.1 About initialization

Initialization needs to be in the rendering thread, just execute it once.

#### 4.2 About props loading

It is recommended to load the props asynchronously, open an IO thread separately, and separate from the rendering thread, so as to ensure that the picture does not appear to be stuck.



Any question，please feel free to contact our technical support, thank you !