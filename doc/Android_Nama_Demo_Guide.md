# Demo running document-Android

Level：Public 

Date：2021-04-19

SDK Version: 7.4.0

------
### Updates：

**2021-04-19 v7.4.0:**

- 【Demo layer】Reconstruct the special effects demo and change the process-oriented to object-oriented, so that the overall structure logic is clearer, and the customer use is more convenient. Meantime, it has many advantages such as saving memory, optimizing itemID automatic destruction logic, simplifying the process of user's incoming information, low coupling and improving the flexibility of the architecture 
- Added emotion recognition function. Supported 8 basic full emotion detection
- Added new content service module, which displays game props and delicate stickers, mainly including game, plot, headdress, atmosphere and other special effects props
- Added asynchronous interface to improve insufficient frame rate on low-end devices
- Optimize the body beautification performance, increase the frame rate of Android by 24%, and reduce the time consumption of IOS by 13%
- Optimize the performance of human image segmentation. The frame rate of Android increased by 39%, and the time consumption of IOS decreased by 39%
- Optimize the effect of human image segmentation, which includes optimizing the gap problem, so that the human image segmentation is more close to the human body, and no obvious gap left; improve the accuracy of human segmentation, and reduce the background error recognition
- Added a new playing strategy of human image segmentation. User-defined background interface is opened, which is convenient for the users to quickly change the background. Portrait stroke is supported, so that users can customize the width, distance, and color of the strokes
- Added animoji koala model, optimize the driving effect of animoji face, and improve the stability and sensitivity of the model after driving
- Optimize the beauty effect, which mainly includes the lipstick will no longer appear when the lip is covered. Improve the fit of cosmetic lenses and add more cosmetic lenses material

**2021-01-25 v7.3.2:**

- Optimize the driving performance of facial expression tracking
- Change fuSetup function to thread safety
- fuSetUp 、fuCreateItemFromPackage、fuLoadAIModel function increase exception handling, and strengthen robustness
- Fix the the effect of the custom mirror's function
- Fix the crash problem of SDK on MAC 10.11
- Fix the crash problem of SDK when stickers and Animoji are in mixed use

**2020-12-29 v7.3.0:**

- Optimize beauty performance. Compared with v7.2, the frame rate of standard beauty is increased by 29% and 17% in Android and IOS respectively.
- Optimize the performance of body beautification. Compared with v7.2, the performance is improved significantly. The frame rate of Android is increased by 26%, and the CPU is reduced by 32%; the frame rate of IOS increased by 11%, CPU reduced by 46%, and memory reduced by 45%.
- Optimize background segmentation performance. Compared with v7.2, the performance is significantly improved.The frame rate  of Android is increased by 64%, and the CPU is reduced by 25%; The frame rate of IOS is increased by 41%, CPU is reduced by 47%, and memory is reduced by 44%. Please use  ai_human_processor_mb_fast.bundle.
- Optimize the effect of body beautification. When large amplitude motion occures, the problem of great deformation of objects near the head and shoulder is solved. When the human body appears and disappears in the picture, the transition is more natural.Body beautification effect of occlusion is more stable and no high frequency and continuous shaking left.
- Optimize the expression recognition function. Improve the accuracy of recognition. A total of 17 kinds of expression can be recognized, and newly added FUAITYPE_FACEPROCESSOR_EXPRESSION_RECOGNIZER.
- Optimize the green screen matting effect and improve the edge accuracy.
- Optimize the driving effect of facial expression tracking, optimize the slow display of the first frame detection model, strengthen the subtle expression tracking, and optimize the problem that the model becomes smaller when the face rotates.
- Optimize the whole body Avatar tracking driving effect. For continuous high-frequency and large amplitude motion, such as dancing, the overall model stability, especially the arm stability, is improved, and the shaking problem is significantly improved.
- Optimize the problem of eyelid color overflow when eye brightness is used.
- Added face drag deformation function, you can use FUCreator 2.1.0 to edit deformation effect.
- Added thin round eyes in the beauty module. The effect is to make the whole eye enlarged, and the longitudinal amplification is extensively obvious.
- Added support gesture to callback API fuSetHandGestureCallBack. See API document for details.
- Remade the the props of flower control, rain control and snow control so as to solve the problem of incoherent tracking effect.

**2020-9-24 v7.2.0:**

1. Added green screen matting function, which supports to replace pictures, video background, etc.
2. Added thin cheekbones and thin mandible in the beauty module.
3. Optimize beauty performance and power consumption. Solved the problem of frame dropping when integrating into the third-party streaming service.
4. Optimize the effect and performance of gesture recognition, improve the recognition stability and gesture following effect, optimize the CPU occupancy of gesture recognition.
5. Optimize the the performance of each function in PC, and the frame rate is significantly improved. The frame rate of hair beautification, body beautification and background segmentation is increased by more than 30%, and the frame rate of beauty, Animoji, makeup, gesture and other functions is also increased by more than 10%.
6. Optimization package is increased. SDK is divided into Lite version and full function version. The lite version is smaller and contains face related functions (except for face changing posters).
7. Optimize the stability of face tracking and stickers.
8. Independent core algorithm SDK is provided. See algorithm SDK document for API document.
9. The API of face algorithm capability is encapsulated, and three core face capabilities, including face feature points, expression recognition and tongue movement, are added in the algorithm demo.

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
+Kotlin_FaceUnity_Demo
  +app 			                            // app
    +libs
      -fu_core-release.aar                  // special effect SDK
    +src
      +main
        +assets      
          +animoji                          // animation filter 
          +bg_seg_green                     // green screen matting 
          +change_face                      // poster face change 
          +effect                           // various props
            +action                         // action recognition
            +ar                             // AR mask
            +big_head                       // funny big head
            +expression                     // expression recognition
            +facewarp                       //  distorting mirror
            +gesture                        // gesture recognition 
            +musicfilter                    // music filter
            +normal                         // props stickers 
            +segment                        // human images segmentation 
          +graphics                         // graphic effect props
            -body_slim.bundle               // body beautification props
            -controller_cpp.bundle          // whole body Avatar props
            -face_beautification.bundle     // face beautification props
            -face_makeup.bundle             // face makeup props
            -fuzzytoonfilter.bundle         // animation filter props
            -fxaa.bundle                    // 3D rendering anti-aliasing
            -tongue.bundle                  // Tongue tracking data package
          +hair_seg                         // hair beautification
          +light_makeup                     // light makeup
            +blusher...                     // blusher and other resources
            -light_makeup.bundle            // light makeup props
          +makeup                           // face makeup
            +combination_bundle             // combination makeup bundle resources
            +config_json                    // combination makeup json resources
            +item_bundle                    // sub-makeup bundle resources
            -color_setup.json               // color configuration
          +model                            // algorithm capability model
            -ai_face_processor.bundle       // face recognition AI capability model
            -ai_face_processor_lite.bundle  // face recognition AI capability model，lite version
            -ai_hairseg.bundle              // hair recognition AI capability model
            -ai_hand_processor.bundle       // gesture recognition AI capability model
            -ai_human_processor.bundle      // human point AIcapability model
          +pta                              // whole body Avatar
            +boy                            // boy effect props
            +gesture                        // gesture algorithm model
            +girl                           // girl effect props
            -controller_config.bundle       // controller configuration files
            -default_bg.bundle              // white background
        +java                               // Java source code
        +res                                // App resource files

  +fu_ui                                    // UI module
    +src
      +main
        +java                              // Java source code
  +doc		    	                       // contents
  +README.md	 	                       // README
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
Android Studio 3.4 or above
```

#### 3.2 Preparing 

- [Download FULiveDemoDroid](https://github.com/Faceunity/FULiveDemoDroid)
- Get certificates:
  1. call **(86)-0571-89774660** 
  2. send mail to **marketing@faceunity.com** 

#### 3.3 Configurations

The certificate issued by the Android platform is the authpack.java file. If you have obtained the authentication certificate, put the certificate file in the project app module com.faceunity.app package.

#### 3.4 Compile and running

- Click the Sync button , or Build-->Make Projects.

![AS-Make-Project](nama/imgs/as-make-project.png)

- Click the Run button，deployed to the phone.

![AS-Run](nama/imgs/as-run.png)

- Running 

![fulivedemo](nama/imgs/fulivedemo.png)

------
### 4. FAQ 

Any problems, please feel free to contact our technical support, thank you !