# Demo running documentation-Android



### Updates：

2019-05-01 v6.0.0：

- Optimize face detection, improve detection rate and improve performance
- Added the Light-Makeup function 
- Optimize the Facefusion 
- Optimize thesegmentation precision
- Tongue tracking trackface logic support, Getfaceinfo support
- Added Avatar pinch function, need FUEditor 6.0.0 or above
- Optimize Beauty filter 
- Fix mebedtls symbol conflicts
- Hairdressing and Animoji props support FUEditor v5.6.0 and above. The rest of the props are compatible with any SDK.

Documentations：

- [Beautification Filters User Specification](./Beautification_Filters_User_Specification.md)
- [Make Up Parameter Specification](./Make_Up_Parameter_Specification.md)
- [Face Transfer Interface Documentation](./Face_Transfer_Interface_Documentation.md)

Project updates：

- The beauty props part of the interface has changed, please note the synchronization code
- Please refer to this document and code comments for tongue tracking
- anim_model.bundle and ardata_ex.bundle are abandoned，Please delete the relevant data and code

------
### Contents：
[TOC]

------
### 1. Introduction

This document shows you how to run the Android Demo of the Faceunity Nama SDK and experience the functions of the Faceunity Nama SDK.

FULiveDemoDroid is an integrated example of the integrated tracking and video effects development kit (Nama SDK) on the Android platform.

Features facial tracking, beauty, Animoji, props stickers, AR masks, face transfer, expression recognition, music filters, background segmentation, gesture recognition, distorting mirrors, portrait lighting, and portrait drivers.

Added a main interface to display the list of Faceunity products. The new version of Demo will control which products users can use based on client certificate permissions.  

------
### 2. File structure

This section describes the structure of the Demo file, the various directories, and the functions of important files.

```
+FULiveDemoDroid
  +app 			      // app 
    +src
      +main
        +assets       // Assets 
          +image      // source image
        +effects      // props files
          +bundle     // bundle files
          +res        // icon
        +java         // Java source code
        +makeup       // makeup resource
          +material   // stickers
          +res        // icon
        +poster       // Facefusion
        +res          // App resource files
  +faceunity          // faceunity module
    +libs             // nama.jar 
    +src
      +main
        +assets       // Nama resource files     
        +java         // Java source code
        +jniLibs      // Nama so 
  +docs		    	  // contents
  +README.md	 	  // README
```

------
### 3. Demo Running 

#### 3.1 Develop environment
##### 3.1.1 Platform
```
Android API 18 or above，GLES 2.0 or above
```
##### 3.1.2 Develop environment
```
Android Studio 3.0 or above
```

#### 3.2 Preparing 

- [Download FULiveDemoDroid](https://github.com/Faceunity/FULiveDemoDroid)
- Get certificates:
  1. call **0571-88069272** 
  2. send mail to **marketing@faceunity.com** 

#### 3.3 Configurations

The certificate issued by the Android platform is the authpack.java file. If you have obtained the authentication certificate, put the certificate file in the project faceunity module com.faceunity.fulivedemo package.

#### 3.4 Compile and running

- Click the Sync button , or Build-->Make Projects.

![AS-Make-Project](imgs/as-make-project.png)

- Click the Run button，deployed to the phone.

![AS-Run](imgs/as-run.png)

- Running 

![fulivedemo](imgs/fulivedemo.png)

------
### 4. FAQ 

Any problems , please feel free to contact our technical support ,thank you !