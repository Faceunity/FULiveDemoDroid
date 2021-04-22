# Live Photo documentation

## Contents

[TOC]



## 1.Function

Live photo is a technique that uses 2D blendshape technology to create a static photo by user-added features.

## 2.Interface

The interface of the different graphs is encapsulated in the bundle, and only the set interface of the bundle can be called.

### 

```
group_type:[],     //An array of input types 0-6 corresponds             		'leye','reye','nose','mouth','lbrow','rbrow','face'
group_points:[],	  //All input points, the number of points is determined by group_type
target_width:0,	      //Enter the width of the image
target_height:0,	  //Enter the height of the image
is_front:1,			//Is it a front camera, set for android when switching cameras, ios does not need to set
is_use_cartoon：1.0  //0 is off 1 is on, it will run with cartoon point after opening, and the effect of closing eyes is better.
use_interpolate2：1.0 //Interpolation switch, 0 is off 1 is on, and opening will add a circle to the point, but when the distance between the five senses is too close, it is easy to affect each other. When closing, there will be no encirclement, and the influence of single and five official will be relatively large.


```

### Set the input picture interface

```
para：tex_input
Use the layer interface fuCreateTexForItem to pass the RGBA buffer
```



## 3.Generate point 

The client calculates the position of the current point according to the rotation, zooming, and panning of the facial features. The original proportional point can be obtained according to the get interface above. The image coordinate system uses the top left corner of the image as the origin.

## 4.Intrinsic template saving 

Need client to save

1. Input picture
2. the width and height of picture
3. group_type array 
4. group_points array

This information can be directly set into the bundle at the next load.

## 5.Making template 

1. The closer the eye shape is to the human eye, the better the effect (ellipse)
2. Do not get too close to each other between the five senses, they will affect each other, and there may be intersections.
3. Pay attention to the number of facial features, each of which is limited to 3