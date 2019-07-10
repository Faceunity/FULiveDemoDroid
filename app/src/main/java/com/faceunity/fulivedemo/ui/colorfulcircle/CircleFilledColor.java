package com.faceunity.fulivedemo.ui.colorfulcircle;

/**
 * @author Richie on 2019.05.26
 */
public class CircleFilledColor {
    private static final int DEFAULT_COLOR = 0xFFFFFF;
    private int mFillColor1 = DEFAULT_COLOR;
    private int mFillColor2 = DEFAULT_COLOR;
    private int mFillColor3 = DEFAULT_COLOR;
    private int mFillColor4 = DEFAULT_COLOR;
    private FillMode mFillMode = FillMode.SINGLE;

    public CircleFilledColor(int fillColor1, FillMode fillMode) {
        this(fillColor1, 0, 0, 0, fillMode);
    }

    public CircleFilledColor(int fillColor1, int fillColor2, FillMode fillMode) {
        this(fillColor1, fillColor2, 0, 0, fillMode);
    }

    public CircleFilledColor(int fillColor1, int fillColor2, int fillColor3, FillMode fillMode) {
        this(fillColor1, fillColor2, fillColor3, 0, fillMode);
    }

    public CircleFilledColor(int fillColor1, int fillColor2, int fillColor3, int fillColor4, FillMode fillMode) {
        mFillColor1 = fillColor1;
        mFillColor2 = fillColor2;
        mFillColor3 = fillColor3;
        mFillColor4 = fillColor4;
        mFillMode = fillMode;
    }

    public int getFillColor1() {
        return mFillColor1;
    }

    public void setFillColor1(int fillColor1) {
        mFillColor1 = fillColor1;
    }

    public int getFillColor2() {
        return mFillColor2;
    }

    public void setFillColor2(int fillColor2) {
        mFillColor2 = fillColor2;
    }

    public int getFillColor3() {
        return mFillColor3;
    }

    public void setFillColor3(int fillColor3) {
        mFillColor3 = fillColor3;
    }

    public int getFillColor4() {
        return mFillColor4;
    }

    public void setFillColor4(int fillColor4) {
        mFillColor4 = fillColor4;
    }

    public FillMode getFillMode() {
        return mFillMode;
    }

    public void setFillMode(FillMode fillMode) {
        mFillMode = fillMode;
    }

    public enum FillMode {
        /**
         * 单色
         */
        SINGLE(0),
        /**
         * 双色
         */
        DOUBLE(1),
        /**
         * 三色
         */
        TRIPLE(2),
        /**
         * 四色
         */
        QUADRUPLE(3);

        private final int value;

        FillMode(int value) {
            this.value = value;
        }

        public static FillMode ofValue(int value) {
            switch (value) {
                case 0:
                    return SINGLE;
                case 1:
                    return DOUBLE;
                case 2:
                    return TRIPLE;
                case 3:
                    return QUADRUPLE;
                default:
                    return SINGLE;
            }
        }

        public int getValue() {
            return value;
        }
    }
}
