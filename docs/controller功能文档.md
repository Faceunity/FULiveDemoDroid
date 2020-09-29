# Contorller 参数文档

## 特别说明
```C
//文档中假设：通过fuCreateItemFromPackage创建的controller.bundle的句柄为1
//创建controller后，要先创建controller_config.bundle并绑定到controller。这样controller才算正确初始化。
```
------

## 多人模式
```C
//使用fuBindItems绑定道具, fuUnbindItems解绑道具，以及对controller设置的参数，作用的都是当前角色，默认情况下，当前角色的ID是0号。
//使用多人模式，需要通过设置参数current_instance_id，切换当前角色，例如切换到1号角色：
fuItemSetParamd(1, "current_instance_id", 1.0);

//获取当前角色的id
fuItemGetParamd(1, "current_instance_id");
```

------

## 设置角色位置
```C
//NAMA中使用右手坐标系，X轴水平向右，Y轴竖直向上，Z轴垂直屏幕向外
```
------
##### 设置角色的旋转角度
```C
//第三个参数是归一化的旋转角度，范围[0.0, 1.0]，0.0代表0度，1.0代表360度
fuItemSetParamd(1, "target_angle", 0.5);
```
##### 设置角色的大小
```C
//第三个参数是角色在三维空间中Z方向的坐标，范围[-3000, 600]，数值越大，显示的角色越大
fuItemSetParamd(1, "target_scale", -300.0);
```
##### 设置角色在竖直方向上的位置
```C
//第三个参数是角色在三维空间中Y方向的位置，范围[-600, 800]
fuItemSetParamd(1, "target_trans", 30.0);
```
##### 设置角色在三维空间的位置
```C
//第三个参数是角色在三维空间中的坐标[x, y, z]，x范围[-200, 200]，y范围[-600, 800]，z范围[-3000, 600]
fuItemSetParamdv(1, "target_position", [30.0, 0.0, -300]);
```
##### 调用重置命令，使上述对位置的设置命令生效
```C
//第三个参数为过渡帧数，范围[1.0, 60.0]，表示经过多少帧从当前位置过渡到目标位置
fuItemSetParamd(1, "reset_all", 1.0);
```
------
##### 旋转角色
```C
//第三个参数是旋转增量，取值范围[-1.0, 1.0]
fuItemSetParamd(1, "rot_delta", 1.0);
```
##### 缩放角色
```C
//第三个参数缩放增量，取值范围[-1.0, 1.0]
fuItemSetParamd(1, "scale_delta", 1.0);
```
##### 上下移动角色
```C
//第三个参数是上下增量，取值范围[-1.0, 1.0]
fuItemSetParamd(1, "translate_delta", 1.0);
```

##### 获取角色在三维空间的位置
```C
fuItemGetParamdv(1, "current_position");
```
------

## 动画控制
```C
//假设：通过fuCreateItemFromPackage创建的动画道具anim.bundle的句柄为2
//以下控制接口只对当前角色有效
```
------
```C
//个别设备支持的骨骼数量有限，无法在默认情况下运行骨骼动画，这时候开启这个选项
fuItemSetParamd(1, "enable_vtf", 1.0); 

//从头播放句柄为2的动画（循环）
fuItemSetParamd(1, "play_animation", 2);

//从头播放句柄为2的动画（单次）
fuItemSetParamd(1, "play_animation_once", 2);

//继续播放当前动画，参数无意义
fuItemSetParamd(1, "start_animation", 1);

//暂停播放当前动画，参数无意义
fuItemSetParamd(1, "pause_animation", 1);

//结束播放动画，参数无意义
fuItemSetParamd(1, "stop_animation", 1);

//重置动画，参数无意义，效果相当于先调用stop_animation再调用start_animation
fuItemSetParamd(1, "reset_animation", 1);

//设置动画的过渡时间，单位为秒
fuItemSetParamd(1, "animation_transition_time", 4.0); 

//1为开启，0为关闭，开启时会把25帧的动画插值到实际渲染帧数（如60帧）从而使得动画更流畅，但是某些情况下不适合插值，如有闪现操作等不希望插值的动画，可以主动关闭。
//这个参数默认开启
//这个开关并不会对已加载的动画产生效果，已加载的动画无法实时改变帧间插值
//开启或关闭这个开关后再加载的动画，就会产生上述效果
fuItemSetParamd(1, "animation_internal_lerp", 1.0); 

//获取句柄为2的动画的当前进度
//进度0~0.9999为第一次循环，1.0~1.9999为第二次循环，以此类推
//即使play_animation_once，进度也会突破1.0，照常运行
fuItemGetParamd(1, "{\"name\":\"get_animation_progress\", \"anim_id\":2}"); 

//获取句柄为2的动画的当前过渡进度
//进度小于0时，这个动画没有在过渡状态，不论作为source还是target
//进度大于等于0时，这个动画在过渡中，范围为0~1.0，0为开始，1.0为结束
fuItemGetParamd(1, "{\"name\":\"get_animation_transition_progress\", \"anim_id\":2}"); 

//获取句柄为2的动画的总帧数
fuItemGetParamd(1, "{\"name\":\"get_animation_frame_num\", \"anim_id\":2}"); 

//获取句柄为2的动画的LayerID
fuItemGetParamd(1, "{\"name\":\"get_animation_layerid\", \"anim_id\":2}"); 
```

------

## DynamicBone控制
```C
//假设：通过fuCreateItemFromPackage创建的动画道具anim.bundle的句柄为2
//以下控制接口只对当前角色有效
```
------
```C
//1为开启，0为关闭，开启的时候移动角色的值会被设进骨骼系统，这时候带DynamicBone的模型会有相关效果
//如果添加了没有骨骼的模型，请关闭这个值，否则无法移动模型
//默认关闭
//每个角色的这个值都是独立的
fuItemSetParamd(1, "modelmat_to_bone", 1.0); 

//1为开启，0为关闭，开启的时候已加载的物理会生效，同时加载新的带物理的bundle也会生效，关闭的时候已加载的物理会停止生效，但不会清除缓存（这时候再次开启物理会在此生效），这时加载带物理的bundle不会生效，且不会产生缓存，即关闭后加载的带物理的bundle，即时再次开启，物理也不会生效，需要重新加载
fuItemSetParamd(1, "enable_dynamicbone", 1.0); 

//1为开启，0为关闭，开启的时候人物移动或者动画都不会使被DynamicBone控制的骨骼产生位移，关闭的时候再开始计算DynamicBone的效果。人物需要快速移动/旋转的时候强烈建议开启这个，移动/旋转结束后再关闭，可以防止穿模和闪烁。
fuItemSetParamd(1, "dynamicBone_TeleportMode", 1.0); 

//1为开启，0为关闭，根骨骼（美术编辑DynamicBone时指定的根骨骼）限速开关，开启时根骨骼移动速度超过一定值就会自动复位刚体
//默认值和限速值由美术编辑并保存在相应bundle中
fuItemSetParamd(1, "dynamicBone_RootTranslateSpeedLimitMode", 1.0); 
//1为开启，0为关闭，根骨骼（美术编辑DynamicBone时指定的根骨骼）限速开关，开启时根骨骼旋转速度超过一定值就会自动复位刚体
//默认值和限速值由美术编辑并保存在相应bundle中
fuItemSetParamd(1, "dynamicBone_RootRotateSpeedLimitMode", 1.0); 

//有些时候快速移动/旋转或者大幅度的动画都会导致某些刚体被卡住，这时候可以设置以下两个参数来恢复刚体默认位置，这两个接口的参数没有意义，只是占位
//Refresh是会重建整个DynamixBone，消耗巨大，除非不得已否则慎用
fuItemSetParamd(1, "dynamicBone_Refresh", 1.0); 
//Reset会重置刚体位置，消耗较小，推荐用这个，这个解决不了再用Refresh
fuItemSetParamd(1, "dynamicBone_Reset", 1.0); 
```

------

## 相机控制
//通过以下接口控制相机的镜头参数

```C
//默认值为0
//0为透视投影，这时候fov起效，有近大远小的效果
//1为平行投影，这时候render_orth_size起效，没有近大远小，物体远近不影响在屏幕上的大小，使用render_orth_size控制物体在屏幕上的大小
fuItemSetParamd(1,"project_mode",0);

//控制相机镜头的fov，默认值为8.6，取值范围0~90，单位为度（角度）
fuItemSetParamd(1,"render_fov",8.6);

//控制相机镜头的渲染大小，默认值为100，单位为厘米，和模型在一个坐标系下
fuItemSetParamd(1,"render_orth_size",100);

//相机近平面，默认值为30，单位为厘米，和模型在一个坐标系下
//离相机距离小于这个值的模型不会被显示
fuItemSetParamd(1,"znear",30);

//相机远平面，默认值为6000，单位为厘米，和模型在一个坐标系下
//离相机距离大于这个值的模型不会被显示
fuItemSetParamd(1,"zfar",6000);
```

## 相机动画控制

```C
//相机动画控制类似人物动画控制，逻辑都是一样的

//1为开启，0为关闭，开启或关闭相机动画
fuItemSetParamd(1,"active_camera_animation",1);

//从头播放句柄为2的相机动画（循环）
fuItemSetParamd(1, "play_camera_animation", 2);

//从头播放句柄为2的相机动画（单次）
fuItemSetParamd(1, "play_camera_animation_once", 2);

//继续播放当前相机动画，参数无意义
fuItemSetParamd(1, "start_camera_animation", 1);

//暂停播放当前相机动画，参数无意义
fuItemSetParamd(1, "pause_camera_animation", 1);

//结束播放相机动画，参数无意义
fuItemSetParamd(1, "stop_camera_animation", 1);

//重置相机动画，参数无意义，效果相当于先调用stop_camera_animation再调用start_camera_animation
fuItemSetParamd(1, "reset_camera_animation", 1);

//设置相机动画的过渡时间，单位为秒
fuItemSetParamd(1, "camera_animation_transition_time", 4.0); 

//1为开启，0为关闭，开启时会把25帧的相机动画插值到实际渲染帧数（如60帧）从而使得相机动画更流畅，但是某些情况下不适合插值，如有闪现操作等不希望插值的相机动画，可以主动关闭。
//这个参数默认开启
//这个开关并不会对已加载的相机动画产生效果，已加载的相机动画无法实时改变帧间插值
//开启或关闭这个开关后再加载的相机动画，就会产生上述效果
fuItemSetParamd(1, "camera_animation_internal_lerp", 1.0); 

//获取句柄为2的相机动画的当前进度
//进度0~0.9999为第一次循环，1.0~1.9999为第二次循环，以此类推
//即使play_animation_once，进度也会突破1.0，照常运行
fuItemGetParamd(1, "{\"name\":\"get_camera_animation_progress\", \"anim_id\":2}"); 

//获取句柄为2的相机动画的当前过渡进度
//进度小于0时，这个相机动画没有在过渡状态，不论作为source还是target
//进度大于等于0时，这个相机动画在过渡中，范围为0~1.0，0为开始，1.0为结束
fuItemGetParamd(1, "{\"name\":\"get_camera_animation_transition_progress\", \"anim_id\":2}"); 

//获取句柄为2的相机动画的总帧数
fuItemGetParamd(1, "{\"name\":\"get_camera_animation_frame_num\", \"anim_id\":2}"); 
```
------
## 使用自定义的动画系统时间轴，必须按以下步骤
```C
//1.重置一下当前的动画系统，准备切换时间轴
//每个角色调用
fuItemSetParamd(1, "reset_animation", 1); 
fuItemSetParamd(1, "dynamicBone_Reset", 1); //或者dynamicBone_Refresh
//调用一次
fuItemSetParamd(1,"reset_camera_animation",1);
fuItemSetParamd(1, "enable_set_time", 1); 

//2.之后，每次渲染前设置动画系统的当前时间，单位为秒
fuItemSetParamd(1, "animation_time_current", 0.1); 

//3.如果要切换回系统时间
//每个角色调用
fuItemSetParamd(1, "reset_animation", 1);  
fuItemSetParamd(1, "dynamicBone_Reset", 1); //或者dynamicBone_Refresh
//调用一次
fuItemSetParamd(1,"reset_camera_animation",1);
fuItemSetParamd(1, "enable_set_time", 0); 
```

##2D动画控制

```C
//参数范围为0~uv_anim_frame_num-1，这个参数用来控制播放哪帧动画
//只支持default模板打的bundle，打包时需要设置：
//enable_uv_anim：开启UV动画
//uv_anim_column：动画列数
//uv_anim_row：动画行数
//uv_anim_frame_num：动画帧数
fuItemSetParamd(1,'uv_anim_frame_id',1);

//UV动画每秒播放帧率，默认值25
fuItemSetParamd(1,'uv_anim_fps',25);
```

## 颜色设置

```C
//所有输入的颜色值都为RGB，范围0-255
```
------
##### 肤色
```C
//设置角色头和身体的肤色
fuItemSetParamdv(1, "skin_color", [255, 0, 0]);
//获取当前肤色在肤色表的索引，从0开始
int skin_color_index = fuItemGetParamd(1, "skin_color_index");
```
------
##### 唇色
```C
//设置唇色
fuItemSetParamdv(1, "lip_color", [255, 0, 0]);
//获取当前唇色在唇色表的索引，从0开始
int lip_color_index = fuItemGetParamd(1, "lip_color_index");
```
------
##### 瞳孔颜色
```C
//设置瞳孔颜色
fuItemSetParamdv(1, "iris_color", [255,0,0]);
```
------
##### 眼镜颜色
```C
//设置眼镜片颜色
fuItemSetParamdv(1, "glass_color", [255,0,0]);
//设置眼镜框颜色
fuItemSetParamdv(1, "glass_frame_color", [255,0,0]);
```
------
##### 头发颜色
```C
//设置头发颜色
fuItemSetParamdv(1, "hair_color", [255, 0, 0]);
//设置颜色强度，参数大于0.0，一般取值为1.0
fuItemSetParamd(1, "hair_color_intensity", 1.0);
```
------
##### 胡子颜色
```C
//设置胡子颜色
fuItemSetParamdv(1, "beard_color", [255,0,0]);
```
------
##### 美妆颜色
```C
//设置美妆的颜色
//美妆参数名为json结构，
{
    "name":"global",      //固定
    "type":"face_detail", //固定
    "param":"blend_color",//固定
    "UUID":5              //目标的美妆道具bundle handle id
}
//美妆参数值为0-1之间的RGB设置，美妆颜色原始为RGB色值(sRGB空间)，RGB/255得到传给controller的值
//例如要替换的美妆颜色为[255,0,0], 传给controller的值为[1,0,0]
fuItemSetParamdv(1, "{\"name\":\"global\",\"type\":\"face_detail\",\"param\":\"blend_color\",\"UUID\":5}", [1,0,0]);

//获取美妆的颜色
//如果返回buf的是[1.0, 0.0, 0.0]，表示 RGB = [255, 0, 0]
//PC/IOS，如果size = -1，表示获取失败
double* buf;
int size = fuItemGetParamdv(1, "{\"name\":\"global\",\"type\":\"face_detail\",\"param\":\"blend_color\",\"UUID\":5}", buf, 0);
buf = new double[size];
fuItemGetParamdv(1, "{\"name\":\"global\",\"type\":\"face_detail\",\"param\":\"blend_color\",\"UUID\":5}", buf, size);
// Android，如果buf = null，表示获取失败
double[] buf = fuItemGetParamdv(1, "{\"name\":\"global\",\"type\":\"face_detail\",\"param\":\"blend_color\",\"UUID\":5}");
```
------
##### 帽子颜色
```C
//设置帽子颜色
fuItemSetParamdv(1, "hat_color", [255,0,0]);
```
------
##### 设置背景颜色
```C
//开启enable_background_color，只有开启后，才能通过set_background_color，设置纯色背景
fuItemSetParamd(1, "enable_background_color", 1.0);
fuItemSetParamdv(1, "set_background_color", [255, 255, 255, 255]);
//开启enable_background_color后背景道具失效，所以如果要使用背景道具，注意关闭enable_background_color
fuItemSetParamd(1, "enable_background_color", 0.0);
```
------

## 阴影设置

```C
// 开启阴影，value = 1.0 代表开启，value = 0.0 代表关闭
fuItemSetParamd(1, "enable_shadow", 1.0);

// 设置shadow map的分辨率
fuItemSetParamd(1, "shadow_map_size", 1024.0);
// 设置shadow PCF level， value = 2.0 代表 Height， value = 1.0 代表 medium, value = 0.0 代表 low
fuItemSetParamd(1, "shadow_pcf_level", 2.0);
// 设置shadow bias， value[0] 代表 uniform bias， value[1] 代表 normal bias
fuItemSetParamdv(1, "shadow_bias", [0.01, 0.1]);
```
------

## 特殊模式

### 动画混合的头部跟踪模式

```C
//这个模式开启的时候会取代原有的头旋转跟踪模式，原有的头跟踪时只旋转1根骨骼，在大幅度转头时脖子很不自然，开启这个模式后，将由6个动画混合出头部跟踪骨骼数据，使得脖子部分更自然

//开启这个模式并加载6个头部旋转动画bundle后，才会真正起效
//(new_pta/bundle_db/head_rotate/0_ditou.bundle)
//(new_pta/bundle_db/head_rotate/1_yangtou.bundle)
//(new_pta/bundle_db/head_rotate/2_zuokan.bundle)
//(new_pta/bundle_db/head_rotate/3_youkan.bundle)
//(new_pta/bundle_db/head_rotate/4_zuowaitou.bundle)
//(new_pta/bundle_db/head_rotate/5_youwaitou.bundle)

//开启动画混合的头部跟踪模式
fuItemSetParamd(1, "enable_animation_track", 1.0);
//关闭动画混合的头部跟踪模式
fuItemSetParamd(1, "enable_animation_track", 0.0);
```

### AR模式
```C
//开启AR模式
fuItemSetParamd(1, "enter_ar_mode", 1.0);
//关闭AR模式
fuItemSetParamd(1, "quit_ar_mode", 1.0);
//AR模式下，为了支持旋转屏幕时，同时旋转头发遮罩，需要由移动端设置当前屏幕旋转方向
//0表示设备未旋转，1表示逆时针旋转90度，2表示逆时针旋转180度，3表示逆时针旋转270度
fuItemSetParamd(1, "screen_orientation", 0);
```
------

### Blendshape混合
```C
//开启或关闭Blendshape混合：value = 1.0表示开启，value = 0.0表示不开启
fuItemSetParamd(1, "enable_expression_blend", value);
//设置Blendshape混合参数：blend_expression、expression_weight0、expression_weight1，只在enable_expression_blend设置为1时有效
//blend_expression是用户输入的bs系数数组，取值为0~1，序号0-45代表基表情bs，46-56代表口腔bs，57-66代表舌头bs
var d = [];
for(var i = 0; i<57; i++){
	d[i] = 0;
}
fuItemSetParamdv(1, "blend_expression", d);
//expression_weight0是blend_expression的权重，expression_weight1是算法检测返回的表情或者加载的动画表情系数数组的权重，取值为0~1
var d = [];
for(var i = 0; i<57; i++){
	d[i] = 0;
}
fuItemSetParamdv(1, "expression_weight0", d);
```
------

### 眼睛注视相机
```C
//开启眼镜注释功能，value = 1.0表示开启，value = 0.0表示不开启
fuItemSetParamd(1, "enable_fouce_eye_to_camera", value);
//设置眼睛注视相机参数：fouce_eye_to_camera_height_adjust、fouce_eye_to_camera_distance_adjust、fouce_eye_to_camera_weight
fuItemSetParamd(1, "fouce_eye_to_camera_height_adjust", 30.0); //调整虚拟相机相对高度
fuItemSetParamd(1, "fouce_eye_to_camera_distance_adjust", 30.0); //调整虚拟相机相对距离
fuItemSetParamd(1, "fouce_eye_to_camera_weight", 1.0); //调整注视的影响权重，1.0表示完全启用，0.0表示无影响
```
------

### Face Processor 面部追踪
```C
//1.使用Face Processor 面部追踪前，需要加载ai_face_processor.bundle
vector<uint8_t> u8_ai_face_processor;
loadbinary("ai_face_processor.bundle", u8_ai_face_processor);
fuLoadAIModelFromPackage(u8_ai_face_processor.data(), (int)u8_ai_face_processor.size(), FUAITYPE::FUAITYPE_FACEPROCESSOR);

//2.开启或关闭面部追踪, value = 1.0表示开启，value = 0.0表示关闭
fuItemSetParamd(1, "enable_face_processor", 1.0);

//3.为当前角色，并分配面部追踪检测到的人脸索引，默认为0
fuItemSetParamd(1, "set_face_processor_face_id", 0.0);

//4.退出程序前，需要销毁Face Processor相关资源
fuReleaseAIModel(FUAITYPE::FUAITYPE_FACEPROCESSOR);
```
------

### Human Processor 身体追踪
##### 开启或关闭身体追踪
```C
//1.使用Human Processor身体追踪前，需要加载ai_human_processor.bundle
vector<uint8_t> u8_ai_human_processor;
loadbinary("ai_human_processor.bundle", u8_ai_human_processor);
fuLoadAIModelFromPackage(u8_ai_human_processor.data(), (int)u8_ai_human_processor.size(), FUAITYPE::FUAITYPE_HUMAN_PROCESSOR);

//2.开启或关闭身体追踪，value = 1.0表示开启，value = 0.0表示关闭
//  enter_human_pose_track_mode 和 quit_human_pose_track_mode 参数接口已废弃
fuItemSetParamd(1, "enable_human_processor", 1.0);

//3.退出程序前，需要销毁Human Processor相关资源
fuReleaseAIModel(FUAITYPE::FUAITYPE_HUMAN_PROCESSOR);

```
##### 身体追踪参数设置
```C
//开启身体追踪前需要设置target_angle，target_scale，target_trans，reset_all参数，来决定角色在追踪失败时的默认位置

//设置是否开启跟随模式：value = 1.0表示跟随，value = 0.0表示不跟随
fuItemSetParamd(1, "human_3d_track_is_follow", 1.0);
//如果使用跟随模式，可以通过参数human_3d_track_render_fov设置渲染的fov大小，单位是度
fuItemSetParamd(1, "human_3d_track_render_fov", 30.0);
//设置是全身驱动，还是半身驱动， 1为全身驱动，0为半身驱动
fuItemSetParamd(1, "human_3d_track_set_scene", 0);
//设置全身驱动跟随模式下模型缩放
fuItemSetParamd(1, "human_3d_track_set_fullbody_avatar_scale", 1.2);
//设置半身驱动跟随模式下模型缩放
fuItemSetParamd(1, "human_3d_track_set_halfbody_avatar_scale", 1.2);
//设置半身驱动跟随模式下，X轴，Y轴方向上的偏移
fuItemSetParamdv(1, "human_3d_track_set_halfbody_global_offset", [0.0, 30.0]);
//设置手势追踪的动画过渡时间，默认值为0.1（秒）
fuItemSetParamd(1, "anim_transition_max_time_gesture_track", 0.1);
//设置在身体动画和身体追踪数据之间过渡的时间，默认值为0.5（秒）
fuItemSetParamd(1, "anim_transition_max_time_human_3d_track", 0.5);
//设置在面部追踪数据和身体追踪数据内的面部数据之间过渡的时间，默认值为1.0（秒）
fuItemSetParamd(1, "anim_transition_max_time_face_track", 1.0);
```
##### 获取身体追踪的状态
```C
//FUAI_HUMAN_NO_BODY = 0,
//FUAI_HUMAN_HALF_LESS_BODY = 1,
//FUAI_HUMAN_HALF_BODY = 2,
//FUAI_HUMAN_HALF_MORE_BODY = 3,
//FUAI_HUMAN_FULL_BODY = 4,
fuItemGetParam(1, "human_status");
```
##### 获取身体追踪时双手手势ID
```C
// 返回[x, y], x表示左手手势，y表示右手手势
fuItemGetParamdv(1, "human_track_gesture_id");
```
------

### CNN 面部追踪（已废弃）
```C
//1.使用CNN 面部追踪前，用户需要通过fuFaceCaptureCreate创建面部追踪模型
var face_capture = fuFaceCaptureCreate(__pointer data, int sz);
//2.将这个模型注册到controller的当前角色上，并分配人脸索引，索引从0开始
fuItemSetParamu64(1, "register_face_capture_manager", face_capture);
fuItemSetParamd(1, "register_face_capture_face_id", 0.0);
//3.设置close_face_capture，说明启用或者关闭CNN面部追踪，value = 0.0表示开启，value = 1.0表示关闭
fuItemSetParamd(1, "close_face_capture", 1.0);

//4.如果开启CNN 面部追踪，每帧都需要调用fuFaceCaptureProcessFrame处理输入图像
fuFaceCaptureProcessFrame(face_capture, __pointer img_data, int image_w, int image_h, int fu_image_format, int rotate_mode)

//5.最后，退出程序前，需要销毁面部追踪模型
fuFaceCaptureDestory(face_capture)
```
------

### 捏脸
##### 进入或者退出捏脸模式 
```C
//进入捏脸模式  
fuItemSetParamd(1, "enter_facepup_mode", 1);
//退出捏脸模式
fuItemSetParamd(1, "quit_facepup_mode", 1);
```
##### 细分部位调整 
```C
//设置捏脸参数，最后保存，保存会将当前参数保存进bundle中
//参数名为json结构，
{
    "name":"facepup", //固定
    "param":"Head_Fat" //具体动作, 见下表
}
//数值范围[0, 1]
fuItemSetParamd(1, "{\"name\":\"facepup\",\"param\":\"Head_Fat\"}", 1.0);
```
##### 获取保存在bundle中的捏脸参数
```C
//参数名为json结构，
{
    "name":"facepup", //固定
    "param":"Head_Fat" //具体动作 ,见下表
}
//数值范围[0,1]
fuItemGetParamd(1,"{\"name\":\"facepup\",\"param\":\"Head_Fat\"}");

//获取保存在bundle中的全部捏脸参数
fuItemGetParamfv(1, "facepup_expression", (float*)buf, (int)sz);
```

##### 参数表：
__脸型__：  

| param              | 含义         |
| ------------------ | ------------ |
| "HeadBone_wide " |头型变宽  |
| "Head_narrow " |头型变窄  |
| "head_shrink " |头部缩短  |
| "head_stretch " |头部拉长  |
| "head_fat " |胖  |
| "head_thin " |瘦  |
| "cheek_wide " |颊变宽  |
| "cheekbone_narrow " |颊变短  |
| "jawbone_Wide " |下颌角向下  |
| "jawbone_Narrow " |下颌角向下  |
| "jaw_m_wide " |下颌变宽  |
| "jaw_M_narrow " |下颌变窄  |
| "jaw_wide " |下巴变宽  |
| "jaw_narrow " |下巴变窄  |
| "jaw_up " |下巴变短  |
| "jaw_lower " |下巴变长  |
| "jawTip_forward " |下巴向前  |
| "jawTip_backward " |下巴向后  |
| "jawBone_m_up " |下颌中间变窄  |
| "jawBone_m_down " |下颌中间变宽  |

__眼睛__：  

| param                | 含义       |
| -------------------- | ---------- |
| "Eye_wide " |眼睛放大  |
| "Eye_shrink " |眼睛缩小  |
| "Eye_up " |眼睛向上  |
| "Eye_down " |眼睛向下  |
| "Eye_in " |眼睛向里  |
| "Eye_out " |眼睛向外  |
| "Eye_close_L " |左眼闭  |
| "Eye_close_R " |右眼闭  |
| "Eye_open_L " |左眼睁  |
| "Eye_open_R " |右眼睁  |
| "Eye_upper_up_L " |左上眼皮向上  |
| "Eye_upper_up_R " |右上眼皮向上  |
| "Eye_upper_down_L " |左上眼皮向下  |
| "Eye_upper_down_R " |右上眼皮向下  |
| "Eye_upperBend_in_L " |左上眼皮向里  |
| "Eye_upperBend_in_R " |右上眼皮向里  |
| "Eye_upperBend_out_L " |左上眼皮向外  |
| "Eye_upperBend_out_R " |右上眼皮向外  |
| "Eye_downer_up_L " |左下眼皮向上  |
| "Eye_downer_up_R " |右下眼皮向上  |
| "Eye_downer_dn_L " |左下眼皮向下  |
| "Eye_downer_dn_R " |右下眼皮向下  |
| "Eye_downerBend_in_L " |左下眼皮向里  |
| "Eye_downerBend_in_R " |右下眼皮向里  |
| "Eye_downerBend_out_L " |左下眼皮向外  |
| "Eye_downerBend_out_R " |右下眼皮向外  |
| "Eye_outter_in " |外眼角向里  |
| "Eye_outter_out " |外眼角向外  |
| "Eye_outter_up " |外眼角向上  |
| "Eye_outter_down " |外眼角向下  |
| "Eye_inner_in " |内眼角向里  |
| "Eye_inner_out " |内眼角向外  |
| "Eye_inner_up " |内眼角向上  |
| "Eye_inner_down " |内眼角向下  |
| "Eye_forward " |眼睛向前  |

__嘴巴__：

| param                | 含义         |
| -------------------- | ------------ |
| "upperLip_Thick " |上唇变厚  |
| "upperLipSide_Thick " |上唇两侧变厚  |
| "lowerLip_Thick " |下唇变厚  |
| "lowerLipSide_Thin " |下唇两侧变薄  |
| "lowerLipSide_Thick " |下唇两侧变厚  |
| "upperLip_Thin " |上唇变薄  |
| "lowerLip_Thin " |下唇变薄  |
| "mouth_magnify " |嘴巴放大  |
| "mouth_shrink " |嘴巴缩小  |
| "lipCorner_Out " |嘴角向外  |
| "lipCorner_In " |嘴角向里  |
| "lipCorner_up " |嘴角向上  |
| "lipCorner_down " |嘴角向下  |
| "mouth_m_down " |唇尖向下  |
| "mouth_m_up " |唇尖向上  |
| "mouth_Up " |嘴向上  |
| "mouth_Down " |嘴向下  |
| "mouth_side_up " |唇线两侧向上  |
| "mouth_side_down " |唇线两侧向下  |
| "mouth_forward " |嘴向前  |
| "mouth_backward " |嘴向后  |
| "upperLipSide_thin " |上唇两侧变薄  |

__鼻子__：

| param          | 含义       |
| -------------- | ---------- |
| "nostril_Out " |鼻翼变宽  |
| "nostril_In " |鼻翼变窄  |
| "noseTip_Up " |鼻尖向上  |
| "noseTip_Down " |鼻尖向下  |
| "nose_Up " |鼻子向上  |
| "nose_tall " |鼻子变高  |
| "nose_low " |鼻子变矮  |
| "nose_Down " |鼻子向下  |
| "noseTip_forward " |鼻尖向前  |
| "noseTip_backward " |鼻尖向后  |
| "noseTip_magnify " |鼻尖放大  |
| "noseTip_shrink " |鼻尖缩小  |
| "nostril_up " |鼻翼向上  |
| "nostril_down " |鼻翼向下  |
| "noseBone_tall " |鼻梁变高  |
| "noseBone_low " |鼻梁变低  |
| "nose_wide " |鼻子变宽  |
| "nose_shrink " |鼻子变窄  |

------
### 骨骼捏形

```C
//参数名为json结构，
{
    "name":"deformation", //固定
    "param":"tall" //具体动作 ,见下表
}

//设置某个捏形维度（channel）的系数，原则上范围是0~1，但是如果效果能接受，小于0或者大于1都是可以运行的
fuItemSetParamd(1,"{\"name\":\"deformation\",\"param\":\"channelName\"}",0);

//获取某个捏形维度（channel）的系数
fuItemGetParamd(1, "{\"name\":\"deformation\",\"param\":\"channelName\"}");

//获取当前全部捏形参数
fuItemGetParamfv(1, "deformationData", (float*)buf, (int)sz);
```

__身材__：

| param          | 含义       |
| -------------- | ---------- |
| "tall" |高  |
| "short" |矮  |
| "fat" |胖  |
| "thin" |瘦  |

__脸型__：

| param          | 含义       |
| -------------- | ---------- |
| "eye_narrow" | 眼睛窄  |
| "eye_wide" |  眼睛宽 |
| "eye_down" | 眼睛下  |
| "eye_up" |  眼睛上 |
| "eye_inner" |  眼睛内 |
| "eye_backward" | 眼睛小  |
| "eye_forward" | 眼睛大  |
| "upperLidOut_narrow" | 外上眼皮右  |
| "upperLidOut_wide" |  外上眼皮左 |
| "upperLidOut_down" | 外上眼皮左下  |
| "upperLidOut_up" | 外上眼皮左上  |
| "upperLidMid_narrow" | 中上眼皮右 |
| "upperLidMid_wide" |  中上眼皮左 |
| "upperLidMid_down" |  中上眼皮下 |
| "upperLidMid_up" |  中上眼皮上 |
| "upperLidIn_narrow" | 内上眼皮右  |
| "upperLidIn_wide" |  内上眼皮左 |
| "upperLidIn_down" | 内上眼皮下 |
| "upperLidIn_up" |  内上眼皮上下 |
| "lidInner_narrow" | 内眼角内  |
| "lidInner_wide" | 内眼角外  |
| "lidInner_down" | 内眼角下  |
| "lidInner_up" |  内眼角上 |
| "lowerLidIn_narrow" | 内下眼皮右 |
| "lowerLidIn_wide" | 内下眼皮左  |
| "lowerLidIn_down" |  内下眼皮下 |
| "lowerLidIn_up" | 内下眼皮上  |
| "lowerLidMid_narrow" | 中下眼皮右  |
| "lowerLidMid_wide" | 中下眼皮左  |
| "lowerLidMid_down" | 中下眼皮下  |
| "lowerLidMid_up" |  中下眼皮上 |
| "lowerLidOut_narrow" |  外下眼皮右 |
| "lowerLidOut_wide" | 外下眼皮左  |
| "lowerLidOut_down" |  外下眼皮下 |
| "lowerLidOut_up" | 外下眼皮上  |
| "lidOuter_narrow" | 外眼角内  |
| "lidOuter_wide" |  外眼角外 |
| "lidOuter_down" | 外眼角下  |
| "lidOuter_up" | 外眼角上  |
| "nose_down" | 鼻子下  |
| "nose_up" | 鼻子上  |
| "nose_backward" | 鼻子后  |
| "nose_forward" | 鼻子前  |
| "noseTip_down" |  鼻尖下 |
| "noseTip_up" | 鼻尖上  |
| "noseTip_backward" |  鼻尖后 |
| "noseTip_forward" |  鼻尖前 |
| "upperHead_narrow" |  脸窄 |
| "upperHead_wide" |  脸宽 |
| "upperHead_down" |  上脸短 |
| "upperHead_up" | 上脸长  |
| "upperHead_backward" | 上脸后  |
| "upperHead_forward" | 上脸前  |
| "lowerHead_down" | 下脸长  |
| "lowerHead_up" | 下脸短 |
| "lowerHead_backward" |  下脸后 |
| "lowerHead_forward" | 下脸前  |
| "upperJaw_narrow" | 脸颊瘦  |
| "upperJaw_wide" | 脸颊胖  |
| "midJaw_narrow" | 下颚瘦 |
| "midJaw_wide" | 下颚胖 |
| "midJaw_down" | 下颚下 |
| "midJaw_up" | 下颚上  |
| "lowerJaw_narrow" | 腮帮瘦  |
| "lowerJaw_wide" | 腮帮胖  |
| "lowerJaw_down" | 腮帮下  |
| "lowerJaw_up" | 腮帮上  |
| "jawLine_narrow" |  下颌角窄 |
| "jawLine_wide" |  下颌角宽 |
| "jawLine_down" |  下颌角下 |
| "jawLine_up" |  下颌角上 |
| "jawTip_narrow" |  下巴瘦 |
| "jawTip_wide" | 下巴胖  |
| "jawTip_down" | 下巴下  |
| "jawTip_up" |  下巴上 |
| "jawTip_backward" | 下巴后  |
| "jawTip_forward" | 下巴前  |
| "jawTip_peak_narrow" | 下巴尖窄  |
| "jawTip_peak_wide" |  下巴尖宽 |
| "jawTip_peak_down" |  下巴尖长 |
| "jawTip_peak_up" |  下巴尖短 |
| "jawTip_peak_backward" |  下巴尖后 |
| "jawTip_peak_forward" | 下巴尖前  |
| "lowerChin_down" |  下巴内侧下 |
| "lowerChin_up" | 下巴内侧上  |
| "mouth_narrow" |  嘴巴小 |
| "mouth_wide" | 嘴巴大  |
| "mouth_down" | 嘴巴下  |
| "mouth_up" |  嘴巴上 |
| "mouth_backward" |  嘴巴后 |
| "mouth_forward" |  嘴巴前 |
| "globalBrow_down" |  眉毛下 |
| "globalBrow_up" |  眉毛上 |
| "InnerBrow_down" | 内眉毛下  |
| "InnerBrow_up" | 内眉毛上  |
| "middleBrow_down" |  中眉毛下 |
| "middleBrow_up" |  中眉毛上 |
| "outerBrow_down" |  外眉毛下 |
| "outerBrow_up" | 外眉毛上  |
| "ear_narrow" | 耳朵小  |
| "ear_wide" | 耳朵大  |
| "ear_down" | 耳朵下  |
| "ear_up" | 耳朵上  |
| "upperEar_down" |  上耳朵下 |
| "upperEar_up" |  上耳朵上 |

------

## 其他

### 更新背景道具贴图
```C
//背景道具包含背景贴图和画中画贴图
//更新背景贴图
fuCreateTexForItem(1, "background_bg_tex", __pointer data, int width, int height)
//更新画中画贴图
fuCreateTexForItem(1, "background_live_tex", __pointer data, int width, int height)
```
------
### 隐藏脖子
```C
fuItemSetParam(1, "hide_neck", 1.0);
```
------
### 输入脸部mesh顶点序号获取其在屏幕空间的坐标

```C
//计算序号为1顶点在屏幕空间的坐标
fuItemSetParamd(1, "query_vert", 1);
//获取坐标x 
fuItemGetParamd(1, "query_vert_x");
//获取坐标y
fuItemGetParamd(1, "query_vert_y");
```
------
### 获取serverinfo信息
```C
//参数名是json格式，name固定是serverinfo，param是参数名。
```
##### 获取头发分类类别
```C  
var ret = fuItemGetParamd(1, "{\"name\":\"serverinfo\", \"param\":\"hair_label\"}");
```
##### 设置道具是否可见(对美妆道具和背景道具不可用)
```C 
//参数名为json结构，
{
    "name":"is_visible",  //固定
    "UUID":5              //目标的道具bundle handle id
}
// value = 0.0 表示不可见，value = 1.0 表示可见
fuItemSetParamdv(1, "{\"name\":\"is_visible\",\"UUID\":5}", 1.0);
```
##### 设置美妆道具的合成顺序（只对使用正常混合模式的美妆道具起效）
```C
// 是否使用自定义的美妆合成顺序，value = 1.0表示使用自定义的美妆合成顺序，value = 0.0表示使用绑定顺序作为合成顺序。
fuItemSetParamd(1, "use_facebeauty_order", 1.0);
// 设置合成顺序的数组，数组中的元素为美妆道具的bundle handle id，数组中越靠后的美妆渲染层级越高，视觉上看起来越在上方。
// 例如，有两个handle_id分别为6和7美妆道具，使用下面的合成顺序，视觉上看起来7在6的上方。
fuItemSetParamdv(1, "facebeauty_order", [6, 7]);
```
##### 设置身体道具各部分的显示列表
```C
// 是否使用显示列表显示身体道具，value = 1.0表示使用显示列表，value = 0.0表示使用默认方式
fuItemSetParamd(1, "use_body_visible_list", 1.0);
// 设置身体道具各部分的显示列表，例如，使用下面的显示列表，则只显示身体道具中6和7两个部分。
fuItemSetParamdv(1, "body_visible_list", [6, 7]);
```
##### 返回当前角色在模型空间的包围盒的左下角和右上角的坐标
```C
// 返回数组[x0, y0, z0, x1, y1, z1]，[x0, y0, x0]表示左下角坐标，[x1, y1, z1]表示右上角坐标
fuItemGetParamdv(1, "boundingbox");
```
##### 返回当前角色的中心在屏幕空间的二维坐标
```C
//假设屏幕空间的坐标原点在左下角
fuItemGetParamdv(1, "target_position_in_screen_space");
```
##### 返回当前角色骨骼的屏幕坐标，屏幕坐标的原点在左下角
```C
//参数名为json结构
{
    "name":"get_bone_coordinate_screen", //固定
    "param":"Head_M" //骨骼名字
}
// PTA指尖骨骼列表如下
{
    "ThumbFinger3_L", "IndexFinger3_L", "MiddleFinger3_L", "RingFinger3_L", "PinkyFinger3_L",
    "ThumbFinger3_R", "IndexFinger3_R", "MiddleFinger3_R", "RingFinger3_R", "PinkyFinger3_R"
}
//返回屏幕空间的二维坐标
fuItemGetParamdv(1, "{\"name\":\"get_bone_coordinate_screen\",\"param\":\"Head_M\"}");
```
##### 返回controller的版本号
```C
//返回controller版本号的字符串
fuItemGetParams(1, "version");
```
##### 返回绑定到controller上的道具的类型
```C
//返回道具类型的字符串
fuItemGetParams(1, "{\"name\":\"get_bundle_type\", \"bundle_id\":3}"); 
```
##### 设置低质量灯光
```C
//设置是否开启低质量灯光的渲染，value = 1.0代表开启，value=0.0代表不开启
fuItemSetParamd(1, "low_quality_lighting", 1.0);
```
##### 更新贴图（从RGBA Buffer创建贴图）
```C
//参数名为json结构
{
    "name":"update_tex_from_data", //字符串， 固定
    "UUID":0, //整数，目标道具的handle id，如果设置UUID = 0，则表示目标道具是头
    "dc_name":"eyeL", //字符串，目标mesh的名字
}

fuCreateTexForItem(1, "{\"name\":\"update_tex_from_data\", \"UUID\":0, \"dc_name\":\"eyel\"}", __pointer data, int width, int height);
```