package com.faceunity.fulivedemo.ui;

import android.content.Context;
import android.support.annotation.IdRes;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.faceunity.fulivedemo.R;

/**
 * Created by tujh on 2018/4/17.
 */
public class BeautyBoxGroup extends LinearLayout {
    private static final String LOG_TAG = BeautyBoxGroup.class.getSimpleName();

    // holds the checked id; the selection is empty by default
    private int mCheckedId = View.NO_ID;
    // tracks children radio buttons checked state
    private BeautyBox.OnCheckedChangeListener mChildOnCheckedChangeListener;
    // when true, mOnCheckedChangeListener discards events
    private boolean mProtectFromCheckedChange = false;
    private OnCheckedChangeListener mOnCheckedChangeListener;
    private PassThroughHierarchyChangeListener mPassThroughListener;

    /**
     * {@inheritDoc}
     */
    public BeautyBoxGroup(Context context) {
        super(context);
        setOrientation(VERTICAL);
        init();
    }

    /**
     * {@inheritDoc}
     */
    public BeautyBoxGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mChildOnCheckedChangeListener = new CheckedStateTracker();
        mPassThroughListener = new PassThroughHierarchyChangeListener();
        super.setOnHierarchyChangeListener(mPassThroughListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setOnHierarchyChangeListener(OnHierarchyChangeListener listener) {
        // the user listener is delegated to our pass-through listener
        mPassThroughListener.mOnHierarchyChangeListener = listener;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        // checks the appropriate radio button as requested in the XML file
        if (mCheckedId != View.NO_ID) {
            mProtectFromCheckedChange = true;
            setCheckedStateForView(mCheckedId, true);
            mProtectFromCheckedChange = false;
            setCheckedId(mCheckedId, true);
        }
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        if (child instanceof BeautyBox) {
            final BeautyBox button = (BeautyBox) child;
            if (button.isChecked()) {
                mProtectFromCheckedChange = true;
                if (mCheckedId != View.NO_ID) {
                    setCheckedStateForView(mCheckedId, false);
                }
                mProtectFromCheckedChange = false;
                setCheckedId(button.getId(), false);
            }
        }

        super.addView(child, index, params);
    }

    /**
     * <p>Sets the selection to the radio button whose identifier is passed in
     * parameter. Using View.NO_ID as the selection identifier clears the selection;
     * such an operation is equivalent to invoking {@link #clearCheck()}.</p>
     *
     * @param id the unique id of the radio button to select in this group
     * @see #getCheckedBeautyBoxId()
     * @see #clearCheck()
     */
    public void check(@IdRes int id) {
        // don't even bother
        if (id != View.NO_ID && (id == mCheckedId)) {
            return;
        }

        if (mCheckedId != View.NO_ID) {
            setCheckedStateForView(mCheckedId, false);
        }

        if (id != View.NO_ID) {
            setCheckedStateForView(id, true);
        }

        setCheckedId(id, true);
    }

    private void setCheckedId(@IdRes int id, boolean isChecked) {
        mCheckedId = id;
        setCheckedStateForView(mCheckedId, true);
        if (mOnCheckedChangeListener != null) {
            mOnCheckedChangeListener.onCheckedChanged(this, mCheckedId, isChecked);
        }
    }

    private void setCheckedStateForView(int viewId, boolean checked) {
        View checkedView = findViewById(viewId);
        if (checkedView != null && checkedView instanceof BeautyBox) {
            BeautyBox box = (BeautyBox) checkedView;
            if (checked) {
                box.setBackgroundImg(R.drawable.control_beauty_select);
                box.setSelect(true);
            } else {
                box.clearBackgroundImg();
                box.setSelect(false);
            }
        }
    }

    /**
     * <p>Returns the identifier of the selected radio button in this group.
     * Upon empty selection, the returned value is View.NO_ID.</p>
     *
     * @return the unique id of the selected radio button in this group
     * @attr ref android.R.styleable#CheckGroup_checkedButton
     * @see #check(int)
     * @see #clearCheck()
     */
    @IdRes
    public int getCheckedBeautyBoxId() {
        return mCheckedId;
    }

    /**
     * <p>Clears the selection. When the selection is cleared, no radio button
     * in this group is selected and {@link #getCheckedBeautyBoxId()} returns
     * null.</p>
     *
     * @see #check(int)
     * @see #getCheckedBeautyBoxId()
     */
    public void clearCheck() {
        check(View.NO_ID);
    }

    /**
     * <p>Register a callback to be invoked when the checked radio button
     * changes in this group.</p>
     *
     * @param listener the callback to call on checked state change
     */
    public void setOnCheckedChangeListener(BeautyBoxGroup.OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    @Override
    public CharSequence getAccessibilityClassName() {
        return BeautyBoxGroup.class.getName();
    }


    /**
     * <p>Interface definition for a callback to be invoked when the checked
     * radio button changed in this group.</p>
     */
    public interface OnCheckedChangeListener {
        /**
         * <p>Called when the checked radio button has changed. When the
         * selection is cleared, checkedId is View.NO_ID.</p>
         *
         * @param group     the group in which the checked radio button has changed
         * @param checkedId the unique identifier of the newly checked radio button
         */
        public void onCheckedChanged(BeautyBoxGroup group, @IdRes int checkedId, boolean isChecked);
    }

    private class CheckedStateTracker implements BeautyBox.OnCheckedChangeListener {
        @Override
        public void onCheckedChanged(BeautyBox buttonView, boolean isChecked) {
            // prevents from infinite recursion
            if (mProtectFromCheckedChange) {
                return;
            }

            int id = buttonView.getId();
            mProtectFromCheckedChange = true;
            if (mCheckedId != View.NO_ID && mCheckedId != id) {
                setCheckedStateForView(mCheckedId, false);
            }
            mProtectFromCheckedChange = false;

            setCheckedId(id, isChecked);
        }
    }

    /**
     * <p>A pass-through listener acts upon the events and dispatches them
     * to another listener. This allows the table layout to set its own internal
     * hierarchy change listener without preventing the user to setup his.</p>
     */
    private class PassThroughHierarchyChangeListener implements
            OnHierarchyChangeListener {
        private OnHierarchyChangeListener mOnHierarchyChangeListener;

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewAdded(View parent, View child) {
            if (parent == BeautyBoxGroup.this && child instanceof BeautyBox) {
                int id = child.getId();
                // generates an id if it's missing
                if (id == View.NO_ID) {
                    id = View.generateViewId();
                    child.setId(id);
                }
                BeautyBox box = (BeautyBox) child;
                box.setOnCheckedChangeListener(mChildOnCheckedChangeListener);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewAdded(parent, child);
            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void onChildViewRemoved(View parent, View child) {
            if (parent == BeautyBoxGroup.this && child instanceof BeautyBox) {
                ((BeautyBox) child).setOnCheckedChangeListener(null);
            }

            if (mOnHierarchyChangeListener != null) {
                mOnHierarchyChangeListener.onChildViewRemoved(parent, child);
            }
        }
    }
}