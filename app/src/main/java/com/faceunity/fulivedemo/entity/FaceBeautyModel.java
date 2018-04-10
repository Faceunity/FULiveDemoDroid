package com.faceunity.fulivedemo.entity;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

/**
 * 美颜参数SharedPreferences记录
 * Created by tujh on 2018/3/7.
 */

public class FaceBeautyModel {
    private static final String TAG = FaceBeautyModel.class.getSimpleName();

    private static final String FaceBeautyFilterLevel = "FaceBeautyFilterLevel_";

    private static final String FaceBeautyALLBlurLevel = "FaceBeautyALLBlurLevel";
    private float mFaceBeautyALLBlurLevel = 1.0f;//精准磨皮
    private static final String FaceBeautyType = "FaceBeautyType";
    private float mFaceBeautyType = 0.0f;//美肤类型
    private static final String FaceBeautyBlurLevel = "FaceBeautyBlurLevel";
    private float mFaceBeautyBlurLevel = 0.7f;//磨皮
    private static final String FaceBeautyColorLevel = "FaceBeautyColorLevel";
    private float mFaceBeautyColorLevel = 0.5f;//美白
    private static final String FaceBeautyRedLevel = "FaceBeautyRedLevel";
    private float mFaceBeautyRedLevel = 0.5f;//红润
    private static final String BrightEyesLevel = "BrightEyesLevel";
    private float mBrightEyesLevel = 1000.7f;//亮眼
    private static final String BeautyTeethLevel = "BeautyTeethLevel";
    private float mBeautyTeethLevel = 1000.7f;//美牙

    private static final String OpenFaceShape = "OpenFaceShape";
    private float mOpenFaceShape = 1.0f;
    private static final String FaceBeautyFaceShape = "FaceBeautyFaceShape";
    private float mFaceBeautyFaceShape = 3.0f;//脸型
    private static final String FaceShapeLevel = "FaceShapeLevel";
    private float mFaceShapeLevel = 1.0f;//程度
    private static final String FaceBeautyEnlargeEye_old = "FaceBeautyEnlargeEye_old";
    private float mFaceBeautyEnlargeEye_old = 0.4f;//大眼
    private static final String FaceBeautyCheekThin_old = "FaceBeautyCheekThin_old";
    private float mFaceBeautyCheekThin_old = 0.4f;//瘦脸
    private static final String FaceBeautyEnlargeEye = "FaceBeautyEnlargeEye";
    private float mFaceBeautyEnlargeEye = 0.4f;//大眼
    private static final String FaceBeautyCheekThin = "FaceBeautyCheekThin";
    private float mFaceBeautyCheekThin = 0.4f;//瘦脸
    private static final String ChinLevel = "ChinLevel";
    private float mChinLevel = 0.3f;//下巴
    private static final String ForeheadLevel = "ForeheadLevel";
    private float mForeheadLevel = 0.3f;//额头
    private static final String ThinNoseLevel = "ThinNoseLevel";
    private float mThinNoseLevel = 0.5f;//瘦鼻
    private static final String MouthShape = "MouthShape";
    private float mMouthShape = 0.4f;//嘴形

    private Map<String, Float> mFilterLevelIntegerMap = new HashMap<>();

    private SharedPreferences mSharedPreferences;

    private static FaceBeautyModel sMFaceBeautyModel;

    public static FaceBeautyModel getInstance(Context context) {
        if (sMFaceBeautyModel == null) {
            sMFaceBeautyModel = new FaceBeautyModel(context);
        }
        return sMFaceBeautyModel;
    }

    private FaceBeautyModel(Context context) {
        mSharedPreferences = context.getSharedPreferences(TAG, Context.MODE_PRIVATE);
//        mFaceBeautyALLBlurLevel = mSharedPreferences.getFloat(FaceBeautyALLBlurLevel, mFaceBeautyALLBlurLevel);
//        mFaceBeautyType = mSharedPreferences.getFloat(FaceBeautyType, mFaceBeautyType);
//        mFaceBeautyBlurLevel = mSharedPreferences.getFloat(FaceBeautyBlurLevel, mFaceBeautyBlurLevel);
//        mFaceBeautyColorLevel = mSharedPreferences.getFloat(FaceBeautyColorLevel, mFaceBeautyColorLevel);
//        mFaceBeautyRedLevel = mSharedPreferences.getFloat(FaceBeautyRedLevel, mFaceBeautyRedLevel);
//        mBrightEyesLevel = mSharedPreferences.getFloat(BrightEyesLevel, mBrightEyesLevel);
//        mBeautyTeethLevel = mSharedPreferences.getFloat(BeautyTeethLevel, mBeautyTeethLevel);
//
//        mOpenFaceShape = mSharedPreferences.getFloat(OpenFaceShape, mOpenFaceShape);
//        mFaceBeautyFaceShape = mSharedPreferences.getFloat(FaceBeautyFaceShape, mFaceBeautyFaceShape);
//        mFaceShapeLevel = mSharedPreferences.getFloat(FaceShapeLevel, mFaceShapeLevel);
//        mFaceBeautyEnlargeEye = mSharedPreferences.getFloat(FaceBeautyEnlargeEye, mFaceBeautyEnlargeEye);
//        mFaceBeautyCheekThin = mSharedPreferences.getFloat(FaceBeautyCheekThin, mFaceBeautyCheekThin);
//        mFaceBeautyEnlargeEye_old = mSharedPreferences.getFloat(FaceBeautyEnlargeEye_old, mFaceBeautyEnlargeEye_old);
//        mFaceBeautyCheekThin_old = mSharedPreferences.getFloat(FaceBeautyCheekThin_old, mFaceBeautyCheekThin_old);
//        mChinLevel = mSharedPreferences.getFloat(ChinLevel, mChinLevel);
//        mForeheadLevel = mSharedPreferences.getFloat(ForeheadLevel, mForeheadLevel);
//        mThinNoseLevel = mSharedPreferences.getFloat(ThinNoseLevel, mThinNoseLevel);
//        mMouthShape = mSharedPreferences.getFloat(MouthShape, mMouthShape);
    }

    private void putFloat(String name, float value) {
        mSharedPreferences.edit().putFloat(name, value).apply();
    }

    public float getFaceBeautyFilterLevel(String filterName) {
        Float level = mFilterLevelIntegerMap.get(FaceBeautyFilterLevel + filterName);
        return level == null ? 1.0f : level;
    }

    public void setFaceBeautyFilterLevel(String filterName, float faceBeautyFilterLevel) {
        mFilterLevelIntegerMap.put(FaceBeautyFilterLevel + filterName, faceBeautyFilterLevel);
        putFloat(FaceBeautyFilterLevel + filterName, faceBeautyFilterLevel);
    }

    public float getFaceBeautyALLBlurLevel() {
        return mFaceBeautyALLBlurLevel;
    }

    public void setFaceBeautyALLBlurLevel(float faceBeautyALLBlurLevel) {
        mFaceBeautyALLBlurLevel = faceBeautyALLBlurLevel;
        putFloat(FaceBeautyALLBlurLevel, mFaceBeautyALLBlurLevel);
    }

    public float getFaceBeautyBlurLevel() {
        return mFaceBeautyBlurLevel;
    }

    public void setFaceBeautyBlurLevel(float faceBeautyBlurLevel) {
        mFaceBeautyBlurLevel = faceBeautyBlurLevel;
        putFloat(FaceBeautyBlurLevel, mFaceBeautyBlurLevel);
    }

    public float getFaceBeautyType() {
        return mFaceBeautyType;
    }

    public void setFaceBeautyType(float faceBeautyType) {
        mFaceBeautyType = faceBeautyType;
        putFloat(FaceBeautyType, mFaceBeautyType);
    }

    public float getFaceBeautyColorLevel() {
        return mFaceBeautyColorLevel;
    }

    public void setFaceBeautyColorLevel(float faceBeautyColorLevel) {
        mFaceBeautyColorLevel = faceBeautyColorLevel;
        putFloat(FaceBeautyColorLevel, mFaceBeautyColorLevel);
    }

    public float getFaceBeautyRedLevel() {
        return mFaceBeautyRedLevel;
    }

    public void setFaceBeautyRedLevel(float faceBeautyRedLevel) {
        mFaceBeautyRedLevel = faceBeautyRedLevel;
        putFloat(FaceBeautyRedLevel, mFaceBeautyRedLevel);
    }

    public float getBrightEyesLevel() {
        return mBrightEyesLevel;
    }

    public void setBrightEyesLevel(float brightEyesLevel) {
        mBrightEyesLevel = brightEyesLevel;
        putFloat(BrightEyesLevel, mBrightEyesLevel);
    }

    public float getBeautyTeethLevel() {
        return mBeautyTeethLevel;
    }

    public void setBeautyTeethLevel(float beautyTeethLevel) {
        mBeautyTeethLevel = beautyTeethLevel;
        putFloat(BeautyTeethLevel, mBeautyTeethLevel);
    }

    public float getOpenFaceShape() {
        return mOpenFaceShape;
    }

    public void setOpenFaceShape(float openFaceShape) {
        mOpenFaceShape = openFaceShape;
        putFloat(OpenFaceShape, mOpenFaceShape);
    }

    public float getFaceBeautyFaceShape() {
        return mFaceBeautyFaceShape;
    }

    public void setFaceBeautyFaceShape(float faceBeautyFaceShape) {
        mFaceBeautyFaceShape = faceBeautyFaceShape;
        putFloat(FaceBeautyFaceShape, mFaceBeautyFaceShape);
    }

    public float getFaceShapeLevel() {
        return mFaceShapeLevel;
    }

    public void setFaceShapeLevel(float faceShapeLevel) {
        mFaceShapeLevel = faceShapeLevel;
        putFloat(FaceShapeLevel, mFaceShapeLevel);
    }

    public float getFaceBeautyEnlargeEye_old() {
        return mFaceBeautyEnlargeEye_old;
    }

    public void setFaceBeautyEnlargeEye_old(float faceBeautyEnlargeEye_old) {
        mFaceBeautyEnlargeEye_old = faceBeautyEnlargeEye_old;
        putFloat(FaceBeautyEnlargeEye_old, mFaceBeautyEnlargeEye_old);

    }

    public float getFaceBeautyCheekThin_old() {
        return mFaceBeautyCheekThin_old;
    }

    public void setFaceBeautyCheekThin_old(float faceBeautyCheekThin_old) {
        mFaceBeautyCheekThin_old = faceBeautyCheekThin_old;
        putFloat(FaceBeautyCheekThin_old, mFaceBeautyCheekThin_old);

    }

    public float getFaceBeautyEnlargeEye() {
        return mFaceBeautyEnlargeEye;
    }

    public void setFaceBeautyEnlargeEye(float faceBeautyEnlargeEye) {
        mFaceBeautyEnlargeEye = faceBeautyEnlargeEye;
        putFloat(FaceBeautyEnlargeEye, mFaceBeautyEnlargeEye);
    }

    public float getFaceBeautyCheekThin() {
        return mFaceBeautyCheekThin;
    }

    public void setFaceBeautyCheekThin(float faceBeautyCheekThin) {
        mFaceBeautyCheekThin = faceBeautyCheekThin;
        putFloat(FaceBeautyCheekThin, mFaceBeautyCheekThin);
    }

    public float getChinLevel() {
        return mChinLevel;
    }

    public void setChinLevel(float chinLevel) {
        mChinLevel = chinLevel;
        putFloat(ChinLevel, mChinLevel);
    }

    public float getForeheadLevel() {
        return mForeheadLevel;
    }

    public void setForeheadLevel(float foreheadLevel) {
        mForeheadLevel = foreheadLevel;
        putFloat(ForeheadLevel, mForeheadLevel);
    }

    public float getThinNoseLevel() {
        return mThinNoseLevel;
    }

    public void setThinNoseLevel(float thinNoseLevel) {
        mThinNoseLevel = thinNoseLevel;
        putFloat(ThinNoseLevel, mThinNoseLevel);
    }

    public float getMouthShape() {
        return mMouthShape;
    }

    public void setMouthShape(float mouthShape) {
        mMouthShape = mouthShape;
        putFloat(MouthShape, mMouthShape);
    }

    /*
        //FURenderer获取记录的美颜参数
        FaceBeautyModel mFaceBeautyModel = FaceBeautyModel.getInstance(context);
        mFaceBeautyALLBlurLevel = mFaceBeautyModel.getFaceBeautyALLBlurLevel();
        mFaceBeautyALLBlurLevel = mFaceBeautyALLBlurLevel < 1000 ? mFaceBeautyALLBlurLevel : 0;
        mFaceBeautyType = mFaceBeautyModel.getFaceBeautyType();
        mFaceBeautyType = mFaceBeautyType < 1000 ? mFaceBeautyType : 0;
        mFaceBeautyBlurLevel = mFaceBeautyModel.getFaceBeautyBlurLevel();
        mFaceBeautyBlurLevel = mFaceBeautyBlurLevel < 1000 ? mFaceBeautyBlurLevel : 0;
        mFaceBeautyColorLevel = mFaceBeautyModel.getFaceBeautyColorLevel();
        mFaceBeautyColorLevel = mFaceBeautyColorLevel < 1000 ? mFaceBeautyColorLevel : 0;
        mFaceBeautyRedLevel = mFaceBeautyModel.getFaceBeautyRedLevel();
        mFaceBeautyRedLevel = mFaceBeautyRedLevel < 1000 ? mFaceBeautyRedLevel : 0;
        mBrightEyesLevel = mFaceBeautyModel.getBrightEyesLevel();
        mBrightEyesLevel = mBrightEyesLevel < 1000 ? mBrightEyesLevel : 0;
        mBeautyTeethLevel = mFaceBeautyModel.getBeautyTeethLevel();
        mBeautyTeethLevel = mBeautyTeethLevel < 1000 ? mBeautyTeethLevel : 0;

        mOpenFaceShape = mFaceBeautyModel.getOpenFaceShape();
        mOpenFaceShape = mOpenFaceShape < 1000 ? mOpenFaceShape : 0;
        mFaceBeautyFaceShape = mFaceBeautyModel.getFaceBeautyFaceShape();
        mFaceBeautyFaceShape = mFaceBeautyFaceShape < 1000 ? mFaceBeautyFaceShape : 0;
        mFaceShapeLevel = mFaceBeautyModel.getFaceShapeLevel();
        mFaceShapeLevel = mFaceShapeLevel < 1000 ? mFaceShapeLevel : 0;
        mFaceBeautyEnlargeEye = mFaceBeautyModel.getFaceBeautyEnlargeEye();
        mFaceBeautyEnlargeEye = mFaceBeautyEnlargeEye < 1000 ? mFaceBeautyEnlargeEye : 0;
        mFaceBeautyCheekThin = mFaceBeautyModel.getFaceBeautyCheekThin();
        mFaceBeautyCheekThin = mFaceBeautyCheekThin < 1000 ? mFaceBeautyCheekThin : 0;
        mFaceBeautyEnlargeEye_old = mFaceBeautyModel.getFaceBeautyEnlargeEye_old();
        mFaceBeautyEnlargeEye_old = mFaceBeautyEnlargeEye_old < 1000 ? mFaceBeautyEnlargeEye_old : 0;
        mFaceBeautyCheekThin_old = mFaceBeautyModel.getFaceBeautyCheekThin_old();
        mFaceBeautyCheekThin_old = mFaceBeautyCheekThin_old < 1000 ? mFaceBeautyCheekThin_old : 0;
        mChinLevel = mFaceBeautyModel.getChinLevel();
        mChinLevel = mChinLevel < 1000 ? mChinLevel : 0.5f;
        mForeheadLevel = mFaceBeautyModel.getForeheadLevel();
        mForeheadLevel = mForeheadLevel < 1000 ? mForeheadLevel : 0.5f;
        mThinNoseLevel = mFaceBeautyModel.getThinNoseLevel();
        mThinNoseLevel = mThinNoseLevel < 1000 ? mThinNoseLevel : 0;
        mMouthShape = mFaceBeautyModel.getMouthShape();
        mMouthShape = mMouthShape < 1000 ? mMouthShape : 0.5f;
     */

    /*
        //BeautyControlView获取记录的美颜参数
        mFaceBeautyModel = mFaceBeautyModel.getInstance(context);
        mFaceBeautyALLBlurLevel = mFaceBeautyModel.getFaceBeautyALLBlurLevel();
        mFaceBeautyType = mFaceBeautyModel.getFaceBeautyType();
        mFaceBeautyBlurLevel = mFaceBeautyModel.getFaceBeautyBlurLevel();
        mFaceBeautyColorLevel = mFaceBeautyModel.getFaceBeautyColorLevel();
        mFaceBeautyRedLevel = mFaceBeautyModel.getFaceBeautyRedLevel();
        mBrightEyesLevel = mFaceBeautyModel.getBrightEyesLevel();
        mBeautyTeethLevel = mFaceBeautyModel.getBeautyTeethLevel();

        mOpenFaceShape = mFaceBeautyModel.getOpenFaceShape();
        mFaceBeautyFaceShape = mFaceBeautyModel.getFaceBeautyFaceShape();
        mFaceBeautyEnlargeEye = mFaceBeautyModel.getFaceBeautyEnlargeEye();
        mFaceBeautyCheekThin = mFaceBeautyModel.getFaceBeautyCheekThin();
        mFaceBeautyEnlargeEye_old = mFaceBeautyModel.getFaceBeautyEnlargeEye_old();
        mFaceBeautyCheekThin_old = mFaceBeautyModel.getFaceBeautyCheekThin_old();
        mChinLevel = mFaceBeautyModel.getChinLevel();
        mForeheadLevel = mFaceBeautyModel.getForeheadLevel();
        mThinNoseLevel = mFaceBeautyModel.getThinNoseLevel();
        mMouthShape = mFaceBeautyModel.getMouthShape();
     */
}
