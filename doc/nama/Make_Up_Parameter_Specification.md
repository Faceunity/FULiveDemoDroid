# Makeup bundle specification

## landmark modification

The landmark modification is mainly used to edit the feature points used by the beauty makeup, and the optimization effect is used.

The modification of the landmark is mainly controlled by two parameters. The client needs to set these two parameters to modify and use the landmark:

1. is_use_fix：this parameter controls whether the modified landmark point is used. If it is set to 1 for use, 0 is not used.

2. fix_makeup_data：this parameter is an array, the client needs to pass an array into it, the length of the passed array is 150 * face number, that is, all the points information stored in the array is passed in.

   ### Attentions：

   The reason why the length of fix_makeup_data is 150 is: we return a total of 75 landmark points. Each landmark has x and y data, so it is 75 * 2 = 150.

## Other Parameters

		is_makeup_on:	1,     //swtich
		makeup_intensity:1.0,       //make up level 
		//The following are separate parameters for each makeup. Setting the intensity to 0 turns off this makeup.
		makeup_intensity_lip:1.0,		//kouhong
		makeup_intensity_pupil:1.0,		//meitong
		makeup_intensity_eye:1.0,  		//yanying
		makeup_intensity_eyeLiner:1.0,  		//yanxian
		makeup_intensity_eyelash:1.0,  		//jiemao
		makeup_intensity_eyeBrow:1.0,  		//meimao
		makeup_intensity_blusher：1.0,     //saihong
		makeup_lip_color：[0,0,0,0]   //Array of length 4, rgba color value
		makeup_lip_mask：0.0        //1.0 is on, 0 is off

## replace the texture of the current makeup via loading face_makeup.bundle

**Note that the following interfaces are used after 5.8**

Use the **fuCreateTexForItem** interface to pass in the image data directly in the interface.

```
fuCreateTexForItem(int obj_handle,__pointer name,__pointer value,int width,int height)
```

**obj_handle**:the bundleID for makeup bundle

**name** :you can pick up name from the following :

```
tex_brow 

tex_eye 

tex_pupil 

tex_eyeLash 

tex_highlight 

tex_eyeLiner 

tex_blusher

```

**value**: corresponding to an array of u8 type, corresponding to the image rgba data

**width** and **height** : width and height correspond to the width and height of the image

##  foundation

The foundation is made using blusher, and the corresponding parameter is **tex_blusher**

Foundation resources are produced as follows:

![](img/fendi.png)

## the recommended makeup material combination

| makeup | para | lipstick  | para | blusher     | para | eyebrow       | para | eyeshadow       | para | filter name &para | para |
| :----- | :--- | :-------- | :--- | :---------- | :--- | :------------ | :--- | :-------------- | :--- | :---------------- | :--- |
| 桃花妆 | 90   | mu_lip_01 | 90   | mu_blush_01 | 90   | mu_eyebrow_01 | 50   | mu_eyeshadow_01 | 90   | fennen3           | 100  |
| 复古妆 | 100  | mu_lip_11 | 100  | mu_blush_14 | 100  | mu_eyebrow_11 | 50   | mu_eyeshadow_11 | 100  | lengsediao11      | 85   |
| 朋克妆 | 85   | mu_lip_03 | 85   |             | 0    | mu_eyebrow_03 | 50   | mu_eyeshadow_03 | 85   | bailiang4         | 50   |
| 枫叶妆 | 100  | mu_lip_10 | 100  | mu_blush_13 | 100  | mu_eyebrow_10 | 50   | mu_eyeshadow_10 | 100  | bailiang3         | 80   |
| 锦鲤妆 | 90   | mu_lip_12 | 90   | mu_blush_15 | 90   | mu_eyebrow_12 | 50   | mu_eyeshadow_12 | 90   | fennen2           | 70   |
| 梅子妆 | 85   | mu_lip_13 | 85   | mu_blush_16 | 85   | mu_eyebrow_13 | 50   | mu_eyeshadow_13 | 85   | nuansediao2       | 80   |
| 宿醉妆 | 100  | mu_lip_14 | 100  | mu_blush_17 | 100  | mu_eyebrow_14 | 50   | mu_eyeshadow_14 | 100  | fennen8           | 55   |
| 赤茶妆 | 100  | mu_lip_16 | 90   | mu_blush_18 | 100  | mu_eyebrow_10 | 60   | mu_eyeshadow_16 | 100  | xiaoqingxin2      | 75   |
| 冬日妆 | 90   | mu_lip_17 | 90   | mu_blush_19 | 80   | mu_eyebrow_12 | 60   | mu_eyeshadow_17 | 80   | nuansediao1       | 80   |
| 男友妆 | 100  | mu_lip_18 | 100  | mu_blush_20 | 80   | mu_eyebrow_16 | 65   | mu_eyeshadow_18 | 90   | xiaoqingxin3      | 90   |
| 奶油妆 | 100  | mu_lip_19 | 75   | mu_blush_21 | 100  | mu_eyebrow_17 | 50   | mu_eyeshadow_19 | 95   | bailiang1         | 75   |
| 清透妆 | 90   | mu_lip_20 | 80   | mu_blush_22 | 90   | mu_eyebrow_18 | 45   | mu_eyeshadow_20 | 65   | xiaoqingxin1      | 80   |
| 西柚妆 | 100  | mu_lip_21 | 80   | mu_blush_23 | 100  | mu_eyebrow_19 | 60   | mu_eyeshadow_21 | 75   | lengsediao4       | 70   |
| 厌世妆 | 100  | mu_lip_22 | 80   | mu_blush_24 | 100  | mu_eyebrow_13 | 60   | mu_eyeshadow_22 | 100  | bailiang2         | 85   |
| 黑白妆 | 100  | mu_lip_15 | 100  |             | 0    | mu_eyebrow_15 | 60   | mu_eyeshadow_15 | 100  | heibai1           | 100  |

