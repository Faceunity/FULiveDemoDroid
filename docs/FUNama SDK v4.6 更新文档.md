# FUNama SDK v4.6 更新文档

本次更新主要包含以下改动：
- 增强表情优化功能，在人脸快速转动时提高表情稳定性

## 提高转动时表情稳定性

在最新的v4.6中，针对人头快速转动的情况（如摇头），增强了表情的稳定性。同时，该优化不会影响头部保持不动时的表情灵活性。该功能集成在表情优化功能中（v4.4加入），该功能的详细说明可以参考v4.4更新文档。

启用该功能时，通过 ```fuLoadAnimModel``` 加载动画模型数据，加载成功即可启动。该功能会影响通过```fuGetFaceInfo```获取的```expression```表情系数，以及通过表情驱动的avatar模型。

动画数据文件为 ```anim_model.bundle```，随SDK包提供，文件大小为214KB。

```C
/**
\brief Load facial animation model data, to enable expression optimization
\param data - the pointer to facial animation model data 'anim_model.bundle', 
	which is along beside lib files in SDK package
\param sz - the data size, we use plain int to avoid cross-language compilation issues
\return zero for failure, one for success
*/
int fuLoadAnimModel(void* dat, int dat_sz);
```
| 函数参数   | 含义                        |
| ------ | ------------------------- |
| dat    | 动画数据的内存指针                 |
| dat_sz | 动画数据的内存大小                 |
| 返回值    | 1为成功加载，0为失败，失败原因打印至各平台控制台 |

如开启该功能后，发现特定表情做不出来，或不到位，请提供特定表情的照片或视频，将信息反馈给我司技术支持，以安排该特定表情的优化。

