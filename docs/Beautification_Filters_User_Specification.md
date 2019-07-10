

# Fucntion

Beautification includes rosy, whitening, light-blur, heavy-blur, filter, face transfer, eyes brighten, teeth whiten function

# Paramerter Description

```
		filter para
		filter_name:	"origin",	//Beautification filter access by key value
		filter_level:  	1.0,  //filter level  0.0-1.0
		skin whiten
		color_level:   	0.2,  //，0 is off，0.0-1.0
		
		rosy para
		red_level:   	0.5,  //rosy level，0 is off，0.0-1.0
		
		blue para
		blur_level:		6.0,  //blur level，0.0-6.0
		skin_detect:	0.0,		//，0 is off，1 is on
		nonskin_blur_scale:0.45,	// 0.0-1.0
		heavy_blur:     1.0,		//0 is light blur，1 is heavy blur
		
		transfer para
		face_shape: 	3,              // 0:goddness 1:internet celebrity 2:nature 3:default 4:new nature
		face_shape_level: 1.0,			//all shape level 0.0-1.0
		eye_enlarging: 	0.5,		//0.0-1.0
		cheek_thinning:	0.0,  		//0.0-1.0
		cheek_narrow:   0.0,          //0.0-1.0
		cheek_small:   0.0,          //0.0-1.0
		cheek_oval:    0.0,          //0.0-1.0
		intensity_nose: 0.0,        //0.0-1.0
		intensity_forehead: 0.5,    //0.0-1.0
		intensity_mouth:0.5,       //0.0-1.0
		intensity_chin: 0.5,       //0.0-1.0
		change_frames:   0          //0 means the gradient is off, greater than 0 turns on the gradient, the value is the number of frames required for the gradient.
		
		
		eye_bright:     1,     		//0.0-1.0
		
		tooth_whiten:   1,			//0.0-1.0
		
		Global switch
		is_beauty_on:	1,			// 0 is off  1 is on
		
```

### face_shape parameter description

```
1.
face_shape 0 1 2 3
corresponding 0：goddness 1：internet celebrity 2：nature 3：default

eye_enlarging: 	default is 0.5,		//0.0-1.0
cheek_thinning:	default is 0.0,  		//0.0-1.0
2.
When face_shape is 4, it is finely deformed, and the adjustment of the nose and forehead chin is added.

eye_enlarging: 	default is 0.5,		//0.0-1.0
cheek_thinning:	default is 0.0,  		//0.0-1.0
intensity_nose: default is 0.0,        //0.0-1.0
intensity_forehead: default is 0.5,    //0.0-1.0
intensity_mouth:default is 0.5,       //0.0-1.0
intensity_chin: default is 0.5,       //0.0-1.0
3.
When face_shape is 5, it customizes the deformation for the user, opens the face-related parameters, and adds the narrow face small face parameter.
eye_enlarging: 	default is 0.5,		//0.0-1.0
cheek_thinning:	default is 0.0,  		//0.0-1.0
cheek_narrow:   default is 0.0,          //0.0-1.0
cheek_small:   default is 0.0,          //0.0-1.0
cheek_oval:    default is 0.0,          //0.0-1.0
intensity_nose: default is 0.0,        //0.0-1.0
intensity_forehead: default is 0.5,    //0.0-1.0
intensity_mouth:default is 0.5,       //0.0-1.0
intensity_chin: default is 0.5,       //0.0-1.0
```



### Attentions

The above parameters indicate the value range. If the value range is exceeded, the effect will be affected. It is not recommended.

### Filter corresponds to the key value

```
New filter
bailiang1
bailiang2
bailiang3
bailiang4
bailiang5
bailiang6
bailiang7
fennen1
fennen2
fennen3
fennen4
fennen5
fennen6
fennen7
fennen8
gexing1
gexing2
gexing3
gexing4
gexing5
gexing6
gexing7
gexing8
gexing9
gexing10
heibai1
heibai2
heibai3
heibai4
heibai5
lengsediao1
lengsediao2
lengsediao3
lengsediao4
lengsediao5
lengsediao6
lengsediao7
lengsediao8
lengsediao9
lengsediao10
lengsediao11
nuansediao1
nuansediao2
nuansediao3
xiaoqingxin1
xiaoqingxin2
xiaoqingxin3
xiaoqingxin4
xiaoqingxin5
xiaoqingxin6
```

### New and old filter correspondence

| key for new filer | key for old filter |
| ----------------- | ------------------ |
| bailiang1         |                    |
| bailiang2         | nature_old         |
| bailiang3         | delta              |
| bailiang4         | dry                |
| bailiang5         | refreshing         |
| bailiang6         | newwhite           |
| bailiang7         | ziran              |
| fennen1           |                    |
| fennen2           |                    |
| fennen3           | red                |
| fennen4           | crimson            |
| fennen5           | danya              |
| fennen6           | fennen             |
| fennen7           | qingxin            |
| fennen8           | hongrun            |
| gexing1           | electric           |
| gexing2           | tokyo              |
| gexing3           | warm               |
| gexing4           | dew                |
| gexing5           | concrete           |
| gexing6           | keylime            |
| gexing7           | cold               |
| gexing8           | lucky              |
| gexing9           | Japanese           |
| gexing10          | cloud              |
| heibai1           |                    |
| heibai2           | white level        |
| heibai3           | boardwalk          |
| heibai4           | blackwhite         |
| heibai5           | sliver             |
| lengsediao1       |                    |
| lengsediao2       |                    |
| lengsediao3       |                    |
| lengsediao4       |                    |
| lengsediao5       | girly              |
| lengsediao6       | kodak              |
| lengsediao7       | rollei             |
| lengsediao8       | autumn             |
| lengsediao9       | sunshine           |
| lengsediao10      | sakura             |
| lengsediao11      | hongkong           |
| nuansediao1       |                    |
| nuansediao2       | red tea            |
| nuansediao3       | forest             |
| xiaoqingxin1      |                    |
| xiaoqingxin2      |                    |
| xiaoqingxin3      |                    |
| xiaoqingxin4      | slowlived          |
| xiaoqingxin5      | pink               |
| xiaoqingxin6      | sweet              |
|                   | polaroid           |
|                   | cruz               |
|                   | fuji               |
|                   | cyan               |
|                   | pearl              |

There is no corresponding value in the blank.

