package com.faceunity.app.data;

import androidx.annotation.NonNull;

import com.faceunity.app.data.source.FaceBeautySource;
import com.faceunity.app.data.source.MakeupSource;
import com.faceunity.core.entity.FUBundleData;
import com.faceunity.core.entity.FUColorRGBData;
import com.faceunity.core.faceunity.FURenderKit;
import com.faceunity.core.model.makeup.Makeup;
import com.faceunity.core.model.makeup.MakeupBrowWarpEnum;
import com.faceunity.core.model.makeup.MakeupLipEnum;
import com.faceunity.app.DemoConfig;
import com.faceunity.ui.entity.MakeupCombinationBean;
import com.faceunity.ui.entity.MakeupCustomBean;
import com.faceunity.ui.entity.MakeupCustomClassBean;
import com.faceunity.ui.infe.AbstractMakeupDataFactory;
import com.faceunity.ui.utils.DecimalUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import static com.faceunity.app.data.FaceBeautyDataFactory.faceBeauty;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_BLUSHER;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_EYE_BROW;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_EYE_LASH;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_EYE_LINER;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_EYE_PUPIL;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_EYE_SHADOW;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_FOUNDATION;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_HIGH_LIGHT;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_LIP_STICK;
import static com.faceunity.app.data.source.MakeupSource.FACE_MAKEUP_TYPE_SHADOW;
import static com.faceunity.app.data.source.MakeupSource.buildFUColorRGBData;

/**
 * DESC：美妆业务工厂
 * Created on 2021/3/1
 */
public class MakeupDataFactory extends AbstractMakeupDataFactory {


    /*渲染控制器*/
    private FURenderKit mFURenderKit = FURenderKit.getInstance();

    /*组合妆容列表*/
    private ArrayList<MakeupCombinationBean> makeupCombinations;
    /*组合妆容当前下标*/
    private int currentCombinationIndex;//-1：自定义
    /*美妆数据模型*/
    private Makeup currentMakeup;
    /*当前滤镜*/
    private String currentFilterName;
    /*当前滤镜*/
    private Double currentFilterIntensity;


    private LinkedHashMap<String, ArrayList<double[]>> mMakeUpColorMap;//美妆颜色表

    private HashMap<String, Integer> mCustomIndexMap = new HashMap<>();//key:美妆类别  value:当前美妆子项选中下标  默认0
    private HashMap<String, Double> mCustomIntensityMap = new HashMap<>();//key:美妆类别_子项下标    value:当前美妆选中子项的妆容强度  默认1.0
    private HashMap<String, Integer> mCustomColorIndexMap = new HashMap<>();//key:美妆类别_子项下标    value:当前美妆选中子项的颜色下标  默认3


    public MakeupDataFactory(int index) {
        makeupCombinations = MakeupSource.buildCombinations();
        mMakeUpColorMap = MakeupSource.buildMakeUpColorMap();
        currentCombinationIndex = index;
        currentFilterName = makeupCombinations.get(index).getFilterName();
        currentFilterIntensity = makeupCombinations.get(index).getFilterIntensity();
        currentMakeup = MakeupSource.getMakeupModel(makeupCombinations.get(currentCombinationIndex)); // 当前生效模型
    }

    //region 组合妆

    /**
     * 获取当前组合妆容列表
     *
     * @return
     */
    @Override
    @NonNull
    public ArrayList<MakeupCombinationBean> getMakeupCombinations() {
        return makeupCombinations;
    }

    /**
     * 获取当前组合妆容下标
     *
     * @return
     */
    @Override
    public int getCurrentCombinationIndex() {
        return currentCombinationIndex;
    }

    /**
     * 设置组合妆容下标
     *
     * @param currentCombinationIndex
     */
    @Override
    public void setCurrentCombinationIndex(int currentCombinationIndex) {
        this.currentCombinationIndex = currentCombinationIndex;
    }

    /**
     * 切换组合妆容
     *
     * @param bean
     */
    @Override
    public void onMakeupCombinationSelected(MakeupCombinationBean bean) {
        currentFilterName = bean.getFilterName();
        currentFilterIntensity = bean.getFilterIntensity();
        if (mFURenderKit.getFaceBeauty() != null) {
            mFURenderKit.getFaceBeauty().setFilterName(currentFilterName);
            mFURenderKit.getFaceBeauty().setFilterIntensity(currentFilterIntensity);
        }
        currentMakeup = MakeupSource.getMakeupModel(bean);
        mFURenderKit.setMakeup(currentMakeup);
    }

    /**
     * 切换美妆模型整体强度
     *
     * @param intensity
     */
    @Override
    public void updateCombinationIntensity(double intensity) {
        currentMakeup.setMakeupIntensity(intensity);
        currentFilterIntensity = intensity;
        if (mFURenderKit.getFaceBeauty() != null) {
            mFURenderKit.getFaceBeauty().setFilterIntensity(currentFilterIntensity);
        }
    }


    //endregion 组合妆

    //region 子美妆


    /**
     * 获取子妆类别列表
     *
     * @return
     */
    @Override
    public ArrayList<MakeupCustomClassBean> getMakeupCustomClass() {
        return MakeupSource.buildCustomClasses();
    }

    /**
     * 获取子妆列表参数
     *
     * @return
     */
    @Override
    public LinkedHashMap<String, ArrayList<MakeupCustomBean>> getMakeupCustomItemParams() {
        return MakeupSource.buildCustomItemParams(mMakeUpColorMap);
    }

    /**
     * 设置子妆强度
     *
     * @param key
     * @param current
     * @param intensity
     */
    @Override
    public void updateCustomItemIntensity(String key, int current, double intensity) {
        if (key.equals(FACE_MAKEUP_TYPE_FOUNDATION)) {
            currentMakeup.setFoundationIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_LIP_STICK)) {
            currentMakeup.setLipIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_BLUSHER)) {
            currentMakeup.setBlusherIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_BROW)) {
            currentMakeup.setEyeBrowIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_SHADOW)) {
            currentMakeup.setEyeShadowIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LINER)) {
            currentMakeup.setEyeLineIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LASH)) {
            currentMakeup.setEyeLashIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_HIGH_LIGHT)) {
            currentMakeup.setHeightLightIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_SHADOW)) {
            currentMakeup.setShadowIntensity(intensity);
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_PUPIL)) {
            currentMakeup.setPupilIntensity(intensity);
        }
        mCustomIntensityMap.put(key + "_" + current, intensity);
    }

    /**
     * 切换子妆单项
     *
     * @param key   子妆类别
     * @param index 选中下标
     */
    @Override
    public void onCustomBeanSelected(String key, int index) {
        String itemDir = DemoConfig.MAKEUP_RESOURCE_ITEM_BUNDLE_DIR;
        mCustomIndexMap.put(key, index);
        if (key.equals(FACE_MAKEUP_TYPE_FOUNDATION)) {
            if (index == 0) {
                currentMakeup.setFoundationIntensity(0.0);
            } else {
                currentMakeup.setFoundationBundle(new FUBundleData(itemDir + "mu_style_foundation_01.bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_FOUNDATION + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_FOUNDATION + "_" + index);
                }
                currentMakeup.setFoundationIntensity((intensity));
                updateCustomColor(key, index + 2);
                double[] color = mMakeUpColorMap.get("color_mu_style_foundation_01").get(index + 2);
                currentMakeup.setFoundationColor(buildFUColorRGBData(color));
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_LIP_STICK)) {
            if (index == 0) {
                currentMakeup.setLipIntensity(0.0);
            } else {
                switch (index) {
                    case 1:
                        currentMakeup.setLipType(MakeupLipEnum.FOG);
                        currentMakeup.setEnableTwoLipColor(false);
                        break;
                    case 2:
                        currentMakeup.setLipType(MakeupLipEnum.MOIST);
                        currentMakeup.setEnableTwoLipColor(false);
                        break;
                    case 3:
                        currentMakeup.setLipType(MakeupLipEnum.PEARL);
                        currentMakeup.setEnableTwoLipColor(false);
                        break;
                    case 4:
                        currentMakeup.setLipType(MakeupLipEnum.FOG);
                        currentMakeup.setEnableTwoLipColor(true);
                        currentMakeup.setLipColor2(new FUColorRGBData(0.0, 0.0, 0.0, 0.0));
                        break;
                }
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index);
                }
                currentMakeup.setLipIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_LIP_STICK + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_BLUSHER)) {
            if (index == 0) {
                currentMakeup.setBlusherIntensity(0.0);
            } else {
                currentMakeup.setBlusherBundle(new FUBundleData(itemDir + "mu_style_blush_0" + index + ".bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_BLUSHER + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_BLUSHER + "_" + index);
                }
                currentMakeup.setBlusherIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_BLUSHER + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_BLUSHER + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_BROW)) {
            if (index == 0) {
                currentMakeup.setEyeBrowIntensity(0.0);
                currentMakeup.setEnableBrowWarp(false);
            } else {
                currentMakeup.setEnableBrowWarp(true);
                currentMakeup.setEyeBrowBundle(new FUBundleData(itemDir + "mu_style_eyebrow_01.bundle"));
                switch (index) {
                    case 1:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.WILLOW);
                        break;
                    case 2:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.STANDARD);
                        break;
                    case 3:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.HILL);
                        break;
                    case 4:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.ONE_WORD);
                        break;
                    case 5:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.SHAPE);
                        break;
                    case 6:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.DAILY);
                        break;
                    case 7:
                        currentMakeup.setBrowWarpType(MakeupBrowWarpEnum.JAPAN);
                        break;
                }
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index);
                }
                currentMakeup.setEyeBrowIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_BROW + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_SHADOW)) {
            if (index == 0) {
                currentMakeup.setEyeShadowIntensity(0.0);
            } else {
                currentMakeup.setEyeShadowBundle(new FUBundleData(itemDir + "mu_style_eyeshadow_0" + index + ".bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index);
                }
                currentMakeup.setEyeShadowIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LINER)) {
            if (index == 0) {
                currentMakeup.setEyeLineIntensity(0.0);
            } else {
                currentMakeup.setEyeLinerBundle(new FUBundleData(itemDir + "mu_style_eyeliner_0" + index + ".bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index);
                }
                currentMakeup.setEyeLineIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_LINER + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LASH)) {
            if (index == 0) {
                currentMakeup.setEyeLashIntensity(0.0);
            } else {
                currentMakeup.setEyeLashBundle(new FUBundleData(itemDir + "mu_style_eyelash_0" + index + ".bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index);
                }
                currentMakeup.setEyeLashIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_LASH + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_HIGH_LIGHT)) {
            if (index == 0) {
                currentMakeup.setHeightLightIntensity(0.0);
            } else {
                currentMakeup.setHighLightBundle(new FUBundleData(itemDir + "mu_style_highlight_0" + index + ".bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index);
                }
                currentMakeup.setHeightLightIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_SHADOW)) {
            if (index == 0) {
                currentMakeup.setShadowIntensity(0.0);
            } else {
                currentMakeup.setShadowBundle(new FUBundleData(itemDir + "mu_style_contour_01.bundle"));
                double intensity = 1.0;
                if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_SHADOW + "_" + index)) {
                    intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_SHADOW + "_" + index);
                }
                currentMakeup.setShadowIntensity((intensity));
                int colorIndex = 3;
                if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_SHADOW + "_" + index)) {
                    colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_SHADOW + "_" + index);
                }
                updateCustomColor(key, colorIndex);
            }
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_PUPIL)) {
            if (index == 0) {
                currentMakeup.setPupilIntensity(0.0);
            } else {
                if (index == 1) {
                    currentMakeup.setPupilBundle(new FUBundleData(itemDir + "mu_style_eyepupil_01.bundle"));
                    double intensity = 1.0;
                    if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index)) {
                        intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index);
                    }
                    currentMakeup.setPupilIntensity((intensity));
                    int colorIndex = 3;
                    if (mCustomColorIndexMap.containsKey(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index)) {
                        colorIndex = mCustomColorIndexMap.get(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index);
                    }
                    updateCustomColor(key, colorIndex);
                } else {
                    currentMakeup.setPupilBundle(new FUBundleData(itemDir + "mu_style_eyepupil_0" + (index + 1) + ".bundle"));
                    double intensity = 1.0;
                    if (mCustomIntensityMap.containsKey(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index)) {
                        intensity = mCustomIntensityMap.get(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + index);
                    }
                    currentMakeup.setPupilIntensity((intensity));
                    currentMakeup.setPupilColor(buildFUColorRGBData(new double[]{0.0, 0.0, 0.0, 0.0}));
                }

            }
        }

    }


    /**
     * 设置子妆颜色值
     *
     * @param key   类别关键字
     * @param index 颜色下标
     */
    @Override
    public void updateCustomColor(String key, int index) {
        int current = mCustomIndexMap.containsKey(key) ? mCustomIndexMap.get(key) : 0;
        if (key.equals(FACE_MAKEUP_TYPE_LIP_STICK)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_lip_01").get(index);
            currentMakeup.setLipColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_BLUSHER)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_blush_0" + current).get(index);
            currentMakeup.setBlusherColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_BROW)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_eyebrow_01").get(index);
            currentMakeup.setEyeBrowColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_SHADOW)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_eyeshadow_0" + current).get(index);
            currentMakeup.setEyeShadowColor(new FUColorRGBData(color[0] * 255, color[1] * 255, color[2] * 255, color[3] * 255));
            currentMakeup.setEyeShadowColor2(new FUColorRGBData(color[4] * 255, color[5] * 255, color[6] * 255, color[7] * 255));
            currentMakeup.setEyeShadowColor3(new FUColorRGBData(color[8] * 255, color[9] * 255, color[10] * 255, color[11] * 255));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LINER)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_eyeliner_0" + current).get(index);
            currentMakeup.setEyeLinerColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_LASH)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_eyelash_0" + current).get(index);
            currentMakeup.setEyeLashColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_HIGH_LIGHT)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_highlight_0" + current).get(index);
            currentMakeup.setHighLightColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_SHADOW)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_contour_01").get(index);
            currentMakeup.setShadowColor(buildFUColorRGBData(color));
        } else if (key.equals(FACE_MAKEUP_TYPE_EYE_PUPIL)) {
            mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + current, index);
            double[] color = mMakeUpColorMap.get("color_mu_style_eyepupil_01").get(index);
            currentMakeup.setPupilColor(buildFUColorRGBData(color));
        }
    }


    //endregion 子美妆

    //region 其他

    /**
     * 进入自定义美妆，模型分析
     */
    @Override
    public void enterCustomMakeup() {
        mCustomIndexMap.clear();
        mCustomColorIndexMap.clear();
        mCustomIntensityMap.clear();
        double makeupIntensity = currentMakeup.getMakeupIntensity();
        /*粉底*/
        if (currentMakeup.getFoundationIntensity() != 0.0) {
            double intensity = currentMakeup.getFoundationIntensity() * makeupIntensity;
            currentMakeup.setFoundationIntensity(intensity);
            double[] array = currentMakeup.getFoundationColor().toScaleColorArray();
            ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_foundation_01");
            for (int i = 0; i < list.size(); i++) {
                if (DecimalUtils.doubleArrayEquals(array, list.get(i))) {
                    mCustomIndexMap.put(FACE_MAKEUP_TYPE_FOUNDATION, i - 2);
                    mCustomIntensityMap.put(FACE_MAKEUP_TYPE_FOUNDATION + "_" + (i - 2), intensity);
                    break;
                }
            }

        }
        /*口红*/
        if (currentMakeup.getLipIntensity() != 0.0) {
            double intensity = currentMakeup.getLipIntensity() * makeupIntensity;
            currentMakeup.setLipIntensity(intensity);
            int current = 0;
            switch (currentMakeup.getLipType()) {
                case MakeupLipEnum.FOG:
                    if (currentMakeup.getEnableTwoLipColor()) {
                        current = 4;
                    } else {
                        current = 1;
                    }
                    break;
                case MakeupLipEnum.MOIST:
                    current = 2;
                    break;
                case MakeupLipEnum.PEARL:
                    current = 3;
                    break;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getLipColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_lip_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_LIP_STICK + "_" + current, intensity);
            }
        }
        /*腮红*/
        if (currentMakeup.getBlusherIntensity() != 0.0 && currentMakeup.getBlusherBundle() != null) {
            double intensity = currentMakeup.getBlusherIntensity() * makeupIntensity;
            currentMakeup.setBlusherIntensity(intensity);
            String path = currentMakeup.getBlusherBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_blush_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_blush_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_blush_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_blush_04.bundle")) {
                current = 4;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getBlusherColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_blush_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_BLUSHER + "_" + current, intensity);
            }
        }
        /*眉毛*/
        if (currentMakeup.getEyeBrowIntensity() != 0.0) {
            double intensity = currentMakeup.getEyeBrowIntensity() * makeupIntensity;
            currentMakeup.setEyeBrowIntensity(intensity);
            int current = 0;
            switch (currentMakeup.getBrowWarpType()) {
                case MakeupBrowWarpEnum.WILLOW:
                    current = 1;
                    break;
                case MakeupBrowWarpEnum.STANDARD:
                    current = 2;
                    break;
                case MakeupBrowWarpEnum.HILL:
                    current = 3;
                    break;
                case MakeupBrowWarpEnum.ONE_WORD:
                    current = 4;
                    break;
                case MakeupBrowWarpEnum.SHAPE:
                    current = 5;
                    break;
                case MakeupBrowWarpEnum.DAILY:
                    current = 6;
                    break;
                case MakeupBrowWarpEnum.JAPAN:
                    current = 7;
                    break;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getEyeBrowColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_eyebrow_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_BROW + "_" + current, intensity);
            }
        }
        /*眼影*/
        if (currentMakeup.getEyeShadowIntensity() != 0.0 && currentMakeup.getEyeShadowBundle() != null) {
            double intensity = currentMakeup.getEyeShadowIntensity() * makeupIntensity;
            currentMakeup.setEyeShadowIntensity(intensity);
            String path = currentMakeup.getEyeShadowBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyeshadow_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyeshadow_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyeshadow_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyeshadow_04.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyeshadow_05.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyeshadow_06.bundle")) {
                current = 6;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW, current);
            if (current != 0) {
                double[] array = new double[12];
                double[] color1 = currentMakeup.getEyeShadowColor().toScaleColorArray();
                double[] color2 = currentMakeup.getEyeShadowColor2().toScaleColorArray();
                double[] color3 = currentMakeup.getEyeShadowColor3().toScaleColorArray();
                System.arraycopy(color1, 0, array, 0, color1.length);
                System.arraycopy(color2, 0, array, 4, color2.length);
                System.arraycopy(color3, 0, array, 8, color3.length);
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_eyeshadow_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(array, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_SHADOW + "_" + current, intensity);
            }
        }
        /*眼线*/
        if (currentMakeup.getEyeLineIntensity() != 0.0 && currentMakeup.getEyeLinerBundle() != null) {
            double intensity = currentMakeup.getEyeLineIntensity() * makeupIntensity;
            currentMakeup.setEyeLineIntensity(intensity);
            String path = currentMakeup.getEyeLinerBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyeliner_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyeliner_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyeliner_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyeliner_04.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyeliner_05.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyeliner_06.bundle")) {
                current = 6;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getEyeLinerColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_eyeliner_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LINER + "_" + current, intensity);
            }
        }
        /* 睫毛*/
        if (currentMakeup.getEyeLashIntensity() != 0.0 && currentMakeup.getEyeLashBundle() != null) {
            double intensity = currentMakeup.getEyeLashIntensity() * makeupIntensity;
            currentMakeup.setEyeLashIntensity(intensity);
            String path = currentMakeup.getEyeLashBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyelash_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyelash_02.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyelash_03.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyelash_04.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyelash_05.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyelash_06.bundle")) {
                current = 6;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getEyeLashColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_eyelash_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_LASH + "_" + current, intensity);
            }
        }
        /* 高光*/
        if (currentMakeup.getHeightLightIntensity() != 0.0 && currentMakeup.getHighLightBundle() != null) {
            double intensity = currentMakeup.getHeightLightIntensity() * makeupIntensity;
            currentMakeup.setHeightLightIntensity(intensity);
            String path = currentMakeup.getHighLightBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_highlight_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_highlight_02.bundle")) {
                current = 2;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getHighLightColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_highlight_0" + current);
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_HIGH_LIGHT + "_" + current, intensity);
            }
        }
        /* 阴影*/
        if (currentMakeup.getShadowIntensity() != 0.0 && currentMakeup.getShadowBundle() != null) {
            double intensity = currentMakeup.getShadowIntensity() * makeupIntensity;
            currentMakeup.setShadowIntensity(intensity);
            String path = currentMakeup.getShadowBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_contour_01.bundle")) {
                current = 1;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_SHADOW, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getShadowColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_contour_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_SHADOW + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_SHADOW + "_" + current, intensity);
            }
        }
        /* 美瞳*/
        if (currentMakeup.getPupilIntensity() != 0.0 && currentMakeup.getPupilBundle() != null) {
            double intensity = currentMakeup.getPupilIntensity() * makeupIntensity;
            currentMakeup.setPupilIntensity(intensity);
            String path = currentMakeup.getPupilBundle().getPath();
            int current = 0;
            if (path.endsWith("mu_style_eyepupil_01.bundle")) {
                current = 1;
            } else if (path.endsWith("mu_style_eyepupil_03.bundle")) {
                current = 2;
            } else if (path.endsWith("mu_style_eyepupil_04.bundle")) {
                current = 3;
            } else if (path.endsWith("mu_style_eyepupil_05.bundle")) {
                current = 4;
            } else if (path.endsWith("mu_style_eyepupil_06.bundle")) {
                current = 5;
            } else if (path.endsWith("mu_style_eyepupil_07.bundle")) {
                current = 6;
            } else if (path.endsWith("mu_style_eyepupil_08.bundle")) {
                current = 7;
            } else if (path.endsWith("mu_style_eyepupil_09.bundle")) {
                current = 8;
            }
            mCustomIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL, current);
            if (current != 0) {
                double[] colorArray = currentMakeup.getPupilColor().toScaleColorArray();
                ArrayList<double[]> list = mMakeUpColorMap.get("color_mu_style_eyepupil_01");
                for (int i = 0; i < list.size(); i++) {
                    if (DecimalUtils.doubleArrayEquals(colorArray, list.get(i))) {
                        mCustomColorIndexMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + current, i);
                        break;
                    }
                }
                mCustomIntensityMap.put(FACE_MAKEUP_TYPE_EYE_PUPIL + "_" + current, intensity);
            }
        }
        currentMakeup.setMakeupIntensity(1.0);
    }

    /**
     * 或当单项列表当前下标
     *
     * @param key
     * @return
     */
    @Override
    public int getCurrentCustomItemIndex(String key) {
        if (mCustomIndexMap.containsKey(key)) {
            return mCustomIndexMap.get(key);
        }
        return 0;
    }

    /**
     * 获取当前单项颜色下标
     *
     * @param key
     * @param current
     * @return
     */
    @Override
    public int getCurrentCustomColorIndex(String key, int current) {
        if (mCustomColorIndexMap.containsKey(key + "_" + current)) {
            return mCustomColorIndexMap.get(key + "_" + current);
        }
        return 3;
    }

    /**
     * 获取当前单项强度
     *
     * @param key
     * @param current
     * @return
     */
    @Override
    public double getCurrentCustomIntensity(String key, int current) {
        if (mCustomIntensityMap.containsKey(key + "_" + current)) {
            return mCustomIntensityMap.get(key + "_" + current);
        }
        return 1.0;
    }

    //endregion

    /**
     * FURenderKit加载当前特效
     */
    public void bindCurrentRenderer() {
        mFURenderKit.setFaceBeauty(FaceBeautySource.clone(FaceBeautyDataFactory.faceBeauty));
        mFURenderKit.getFaceBeauty().setFilterName(currentFilterName);
        mFURenderKit.getFaceBeauty().setFilterIntensity(currentFilterIntensity);
        mFURenderKit.getFUAIController().setMaxFaces(4);
        mFURenderKit.setMakeup(currentMakeup);
    }
}