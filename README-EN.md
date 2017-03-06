# FULiveDemo

## Library Files
  - funama.h header of function call interfaces
  - FURenderer.h header of Objective-C interfaces
  - libnama.a core library of face tracking and items rendering    
  
## Data Files
*.bundle files under the faceunity / directory are program data files. All data files use the binary format and are extension-independent. Actually either packaging these data into the program or downloading them from network interfaces is feasible for practical use in app, as long as proper binary data are passed to corresponding function interfaces.

Among them, v3.bundle is shared by all items. Initialization will fail without this file. Each of the other files corresponds to one item. Please contact with our company if you need documents and tools to customize items.
  
## Integration Method
Firstly copy library files to the project directory as well as the XCode project. Afterwards include FURenderer.h in the code. Then functions are ready to be called.

```C
#import "FURenderer.h"
```

The Faceunity interface are usually used in the thread of the video stream callback.

```C
- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection

```
We will take the AVFoundation callback as an example.

**Firstly Create an OpenGL Context:**

```C
//If EAGLContext has already existed in the current environment,this step can be omitted, but EAGLContext function [EAGLContext setCurrentContext:curContext] must be called.
if(!mcontext){
    mcontext = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
}
if(!mcontext || ![EAGLContext setCurrentContext:mcontext]){
    NSLog(@"faceunity: failed to create / set a GLES2 context");
}

```
**Faceunity initialization:** where g_auth_package is an array of the key. The key must be configured well so that SDK can work correctly. Please note: after our app starts, initializing Faceunity once is enough, so be sure not to initialize repeatedly.

```C
int size = 0;
void *v3 = [self mmap_bundle:@"v3.bundle" psize:&size];
        
[[FURenderer shareRenderer] setupWithData:v3 ardata:NULL authPackage:&g_auth_package authSize:sizeof(g_auth_package)];
```

**Load Items:** declare an int array, and store the item handle returned from fuCreateItemFromPackage

```C
int items[2];

- (void)reloadItem
{
    if (items[0] != 0) {
        NSLog(@"faceunity: destroy item");
        fuDestroyItem(items[0]);
    }
    
    int size = 0;
    
    // load selected
    void *data = [self mmap_bundle:[_demoBar.selectedItem stringByAppendingString:@".bundle"] psize:&size];
    items[0] = fuCreateItemFromPackage(data, size);
    
    NSLog(@"faceunity: load item");
}

- (void *)mmap_bundle:(NSString *)bundle psize:(int *)psize {
    
    // Load item from predefined item bundle
    NSString *str = [[[NSBundle mainBundle] resourcePath] stringByAppendingPathComponent:bundle];
    const char *fn = [str UTF8String];
    int fd = open(fn,O_RDONLY);
    
    int size = 0;
    void* zip = NULL;
    
    if (fd == -1) {
        NSLog(@"faceunity: failed to open bundle");
        size = 0;
    }else
    {
        size = [self getFileSize:fd];
        zip = mmap(nil, size, PROT_READ, MAP_SHARED, fd, 0);
    }
    
    *psize = size;
    return zip;
}

- (int)getFileSize:(int)fd
{
    struct stat sb;
    sb.st_size = 0;
    fstat(fd, &sb);
    return (int)sb.st_size;
}

```
**Render Items:** call renderPixelBuffer function to render items. frameID records the current amount of processed frame images and is related to animation playing. itemCount represents the number of items passed into the interface.

```C
CVPixelBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);

[[FURenderer shareRenderer] renderPixelBuffer:pixelBuffer withFrameId:frameID items:items itemCount:1];
```
**The specific code is as follows:**

```C

- (void)captureOutput:(AVCaptureOutput *)captureOutput didOutputSampleBuffer:(CMSampleBufferRef)sampleBuffer fromConnection:(AVCaptureConnection *)connection
{
  
  	//If EAGLContext has already existed in the current environment,this step can be omitted, but EAGLContext function [EAGLContext setCurrentContext:curContext] must be called.
   	#warning Do not carry out this step in asynchronous threads.
   	if(!mcontext){
   		 mcontext = [[EAGLContext alloc] initWithAPI:kEAGLRenderingAPIOpenGLES2];
	}
	if(!mcontext || ![EAGLContext setCurrentContext:mcontext]){
   		NSLog(@"faceunity: failed to create / set a GLES2 context");
	}
    
    //Faceunity Initialization 
    #warning Do not carry out this step in asynchronous threads.
    if (!fuInit)
    {
        fuInit = YES;
        int size = 0;
        void *v3 = [self mmap_bundle:@"v3.bundle" psize:&size];
        
        [[FURenderer shareRenderer] setupWithData:v3 ardata:NULL authPackage:&g_auth_package authSize:sizeof(g_auth_package)];
    }
    
    //swich decals,3D items
    #warning If you need to load items asynchronously, you should stop calling other Faceunity interfaces, otherwise the program will crash.
    if (needReloadItem) {
        needReloadItem = NO;
        [self reloadItem];
    }
        
    //Faceunity core interface, which applies items effects to images. Decals effects will achieve after executing the pixelBuffer function.
    #warning Do not carry out this step in asynchronous threads.
    CVPixelBufferRef pixelBuffer = CMSampleBufferGetImageBuffer(sampleBuffer);
    [[FURenderer shareRenderer] renderPixelBuffer:pixelBuffer withFrameId:frameID items:items itemCount:1];
    frameID += 1;
    
}
```

## Face Beautification in Video
The usage of face beautification is similar to that of items. Firstly load a face beautification item and store the face beautification item handle returned from fuCreateItemFromPackage:
  
```C
- (void)loadFilter
{
    int size = 0;
    
    void *data = [self mmap_bundle:@"face_beautification.bundle" psize:&size];
    
    items[1] = fuCreateItemFromPackage(data, size);
}
```

Afterwards pass this handle and other items which need to render to the rendering interface. Note that the last parameter of fuRenderItems() is the number of items to render. We will take a sticker item and a face beautification item for example. The default face beautification effect can be enabled without setting any parameters after loading face beautification items.

```C
[[FURenderer shareRenderer] renderPixelBuffer:pixelBuffer withFrameId:frameID items:items itemCount:2];
```

Face beautification items mainly contain four modules: filter, whiten, blur, shape. Each module's settable parameters are as follows.

#### Filter

We supply following filters in current version:
```C
"nature", "delta", "electric", "slowlived", "tokyo", "warm"
```

where "nature" is a default whitening filter and others are stylize ones.  Use fuItemSetParams function to set parameters of face beautification items when switching filters, for example:
```C
//  Set item parameters - filter
fuItemSetParams(items[1], "filter_name", "nature");
```

#### Whiten

When filter is set to whitening filter "nature", whitening level is controlled by color_level parameter. When filter is set to the other stylize filters, this parameter is used to control stylize level. This parameter takes the value of non-negative floating point numbers where 0 means no effect, 1 means the default effect, and over 1 means strengthening effects further.

The sample code of setting parameters is as follows:

```C
//  Set item parameters - whiten
fuItemSetParamd(items[1], "color_level", 1.0);
```

#### Blur

In the update version of face beautification, the blur parameter is changed to simple type: blur_level, which takes the value from 0 to 6 corresponding to 7 different blur levels.

The sample code of setting parameters is as follows:

```C
//  Set item parameters - blur
fuItemSetParamd(items[1], "blur_level", 6.0);
```

If you are not pleased with the default 7 blur levels and want to customize blur effects further, please contact with our company to obtain methods for adjusting parameters.

#### Shape

We support two types of shaping modes currently, thinning the cheek and enlarging eyes, controlled by cheek_thinning and eye_enlarging respectively. Both parameters take values of non-negative floating point numbers where 0 means no effect, 1 means the default effect, and over 1 means strengthening effect further.

The sample code of setting parameters is as follows:

```C
//  Set item parameters - shaping
fuItemSetParamd(items[1], "cheek_thinning", 1.0);
fuItemSetParamd(items[1], "eye_enlarging", 1.0);
```

## Gesture Recognition
Currently, our gesture recognition function is also loaded in the form of items. An item of gesture recognition concludes gestures to recognize, animations that are triggered when gestures are recognized, and control scripts. The process of loading those items is the same as common items and face beautification items.

In the online sample, heart.bundle is an item demo showing a love heart gesture. Load it as an item to render and then the gesture recognition function is enabled. Gesture recognition items can coexist with common items and face beautification, similar to face beautification extending the item to three and loading the gesture recognition items at last.


The procedure of gestures customization is the same as that of 2D items. Please contact with our company for technology support if you need specific package details.

## Objective-C Encapsulation Layer

We encapsulate fuSetup and fuRenderItemsEx functions based on original SDK and conclude three interfaces in total:

- fuSetup interface encapsulation

```C
+ (void)setupWithData:(void *)data ardata:(void *)ardata authPackage:(void *)package authSize:(int)size;

```
- Single Input Interface: input a pixelBuffer and return a pixelBuffer adding face beautification or items; support YUV and BGRA formats, and input formats are the same with output formats.


```C
- (CVPixelBufferRef)renderPixelBuffer:(CVPixelBufferRef)pixelBuffer withFrameId:(int)frameid items:(int*)items itemCount:(int)itemCount;
```
- Double Input Interface: input pixelBuffer and texture, and then return a FUOutput struct which contains pixelBuffer and texture adding face beautification or items. The input pixelBuffer supports YUV and BGRA formats while the input texture supports BGRA only. Input formats are in line with output formats.


```C
- (FUOutput)renderPixelBuffer:(CVPixelBufferRef)pixelBuffer bgraTexture:(GLuint)textureHandle withFrameId:(int)frameid items:(int *)items itemCount:(int)itemCount;

```
- FUOutput Struct: contains a pixelBuffer and a texture.

```C
typedef struct{
    CVPixelBufferRef pixelBuffer;
    GLuint bgraTextureHandle;
}FUOutput;

```

## Authentication

Our system is authenticated by the standard TLS certificate. Customers need to apply the certificate from the issuing authority at first and then write certificate data into the client code. When client's app start-up it will send authentication data to our company's server which will verify them. During the valid period of the certificate, all library functions are available for use. Authentication failures such as no certificate or invalid certificates will limit library functions and the system will terminate automatically at once.

Certificates have **two types**，**issuing authority certificate** and **terminal user certificate** respectively.

#### - Issuing Authority Certificate
**Suitable Users**: institutions or companies who need to generate mass terminal certificates, such as software agents, service provider, etc.

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

Terminal users can obtain certificates from our company or other issuing authorities. And our company will deliver users a header file generated by our certificate tool. This file contains a constant array which holds the certificate data in the following form:
```
static char g_auth_package[]={ ... }
```

The array is needed for authentication when users initialize the library environment. Refer to fuSetup interface for details. Under the circumstances of no certificate, invalid certificates, authentication failures will occur along with the console or log in Android platform printing "not authenticated" information, and the system will stop rendering items at once.

If you have any other questions about authorization, please contact us.

## Function Interfaces and Parameter Specification

```C
/**
\brief Initialize and authenticate your SDK instance to the FaceUnity server, must be called exactly once before all other functions.
  The buffers should NEVER be freed while the other functions are still being called.
  You can call this function multiple times to "switch pointers".
\param v2data should point to contents of the "v2.bin" we provide
\param ardata should point to contents of the "ar.bin" we provide
\param authdata is the pointer to the authentication data pack we provide. You must avoid storing the data in a file.
  Normally you can just `#include "authpack.h"` and put `g_auth_package` here.
\param sz_authdata is the authentication data size, we use plain int to avoid cross-language compilation issues.
  Normally you can just `#include "authpack.h"` and put `sizeof(g_auth_package)` here.
*/
void fuSetup(float* v2data,float* ardata,void* authdata,int sz_authdata);

/**
\brief Call this function when the GLES context has been lost and recreated.
  That isn't a normal thing, so this function could leak resources on each call.
*/
void fuOnDeviceLost();

/**
\brief Call this function to reset the face tracker on camera switches
*/
void fuOnCameraChange();

/**
\brief Create an accessory item from a binary package, you can discard the data after the call.
  This function MUST be called in the same GLES context / thread as fuRenderItems.
\param data is the pointer to the data
\param sz is the data size, we use plain int to avoid cross-language compilation issues
\return an integer handle representing the item
*/
int fuCreateItemFromPackage(void* data,int sz);

/**
\brief Destroy an accessory item.
  This function MUST be called in the same GLES context / thread as the original fuCreateItemFromPackage.
\param item is the handle to be destroyed
*/
void fuDestroyItem(int item);

/**
\brief Destroy all accessory items ever created.
  This function MUST be called in the same GLES context / thread as the original fuCreateItemFromPackage.
*/
void fuDestroyAllItems();

/**
\brief Render a list of items on top of a GLES texture or a memory buffer.
  This function needs a GLES 2.0+ context.
\param texid specifies a GLES texture. Set it to 0u if you want to render to a memory buffer.
\param img specifies a memory buffer. Set it to NULL if you want to render to a texture.
  If img is non-NULL, it will be overwritten by the rendered image when fuRenderItems returns
\param w specifies the image width
\param h specifies the image height
\param frameid specifies the current frame id. 
  To get animated effects, please increase frame_id by 1 whenever you call this.
\param p_items points to the list of items
\param n_items is the number of items
\return a new GLES texture containing the rendered image in the texture mode
*/
int fuRenderItems(int texid,int* img,int w,int h,int frame_id, int* p_items,int n_items);

/*\brief An I/O format where `ptr` points to a BGRA buffer. It matches the camera format on iOS. */
#define FU_FORMAT_BGRA_BUFFER 0
/*\brief An I/O format where `ptr` points to a single GLuint that is a RGBA texture. It matches the hardware encoding format on Android. */
#define FU_FORMAT_RGBA_TEXTURE 1
/*\brief An I/O format where `ptr` points to an NV21 buffer. It matches the camera preview format on Android. */
#define FU_FORMAT_NV21_BUFFER 2
/*\brief An output-only format where `ptr` is NULL. The result is directly rendered onto the current GL framebuffer. */
#define FU_FORMAT_GL_CURRENT_FRAMEBUFFER 3
/*\brief An I/O format where `ptr` points to a RGBA buffer. */
#define FU_FORMAT_RGBA_BUFFER 4

/**
\brief Generalized interface for rendering a list of items.
  This function needs a GLES 2.0+ context.
\param out_format is the output format
\param out_ptr receives the rendering result, which is either a GLuint texture handle or a memory buffer
\param in_format is the input format
\param in_ptr points to the input image, which is either a GLuint texture handle or a memory buffer
\param w specifies the image width
\param h specifies the image height
\param frameid specifies the current frame id. 
  To get animated effects, please increase frame_id by 1 whenever you call this.
\param p_items points to the list of items
\param n_items is the number of items
\return a GLuint texture handle containing the rendering result if out_format isn't FU_FORMAT_GL_CURRENT_FRAMEBUFFER
*/
int fuRenderItemsEx(
  int out_format,void* out_ptr,
  int in_format,void* in_ptr,
  int w,int h,int frame_id, int* p_items,int n_items);
  
/**
\brief Set an item parameter to a double value
\param item specifies the item
\param name is the parameter name
\param value is the parameter value to be set
\return zero for failure, non-zero for success
*/
int fuItemSetParamd(int item,char* name,double value);

/**
\brief Set an item parameter to a double array
\param item specifies the item
\param name is the parameter name
\param value points to an array of doubles
\param n specifies the number of elements in value
\return zero for failure, non-zero for success
*/
int fuItemSetParamdv(int item,char* name,double* value,int n);

/**
\brief Set an item parameter to a string value
\param item specifies the item
\param name is the parameter name
\param value is the parameter value to be set
\return zero for failure, non-zero for success
*/
int fuItemSetParams(int item,char* name,char* value);

/**
\brief Get an item parameter to a double value
\param item specifies the item
\param name is the parameter name
\return double value of the parameter
*/
double fuItemGetParamd(int item,char* name);

/**
\brief Get the face tracking status
\return zero for not tracking, non-zero for tracking
*/
int fuIsTracking();

/**
\brief Set the default orientation for face detection. The correct orientation would make the initial detection much faster.
One of 0..3 should work.
*/
void fuSetDefaultOrientation(int rmode);
```
