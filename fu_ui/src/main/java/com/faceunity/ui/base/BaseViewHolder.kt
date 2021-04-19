package com.faceunity.ui.base

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.SparseArray
import android.view.View
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.annotation.IdRes
import androidx.annotation.StringRes
import androidx.recyclerview.widget.RecyclerView
import com.faceunity.ui.listener.OnMultiClickListener


/**
 *
 * DESC：控件布局绑定
 * Created on 2020/11/17
 *
 */
class BaseViewHolder(view: View) :
    RecyclerView.ViewHolder(view) {

    private val views = SparseArray<View>()

    /**
     * Will set the text of a TextView.
     *
     * @param viewId The view id.
     * @param value  The text to put in the text view.
     * @return The BaseViewHolder for chaining.
     */
    fun setText(@IdRes viewId: Int, value: String?): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view?.text = value
        return this
    }

    fun setText(@IdRes viewId: Int, @StringRes strId: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view?.setText(strId)
        return this
    }

    /**
     * Will set the image of an ImageView from a resource id.
     *
     * @param viewId     The view id.
     * @param imageResId The image resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageResource(@IdRes viewId: Int, @DrawableRes imageResId: Int): BaseViewHolder {
        val view = getView<ImageView>(viewId)
        view?.setImageResource(imageResId)
        return this
    }

    /**
     * Will set background color of a view.
     *
     * @param viewId The view id.
     * @param color  A color, not a resource id.
     * @return The BaseViewHolder for chaining.
     */
    fun setBackgroundColor(@IdRes viewId: Int, @ColorInt color: Int): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.setBackgroundColor(color)
        return this
    }

    /**
     * Will set background of a view.
     *
     * @param viewId        The view id.
     * @param backgroundRes A resource to use as a background.
     * @return The BaseViewHolder for chaining.
     */
    fun setBackgroundRes(@IdRes viewId: Int, @DrawableRes backgroundRes: Int): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.setBackgroundResource(backgroundRes)
        return this
    }

    /**
     * Will set text color of a TextView.
     *
     * @param viewId    The view id.
     * @param textColor The text color (not a resource id).
     * @return The BaseViewHolder for chaining.
     */
    fun setTextColor(@IdRes viewId: Int, @ColorInt textColor: Int): BaseViewHolder {
        val view = getView<TextView>(viewId)
        view?.setTextColor(textColor)
        return this
    }


    /**
     * Will set the image of an ImageView from a drawable.
     *
     * @param viewId   The view id.
     * @param drawable The image drawable.
     * @return The BaseViewHolder for chaining.
     */
    fun setImageDrawable(@IdRes viewId: Int, drawable: Drawable): BaseViewHolder {
        val view = getView<ImageView>(viewId)
        view?.setImageDrawable(drawable)
        return this
    }

    /**
     * Add an action to set the image of an image view. Can be called multiple times.
     */
    fun setImageBitmap(@IdRes viewId: Int, bitmap: Bitmap): BaseViewHolder {
        val view = getView<ImageView>(viewId)
        view?.setImageBitmap(bitmap)
        return this
    }


    /**
     * Set a view visibility to VISIBLE (true) or GONE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for GONE.
     * @return The BaseViewHolder for chaining.
     */
    fun setGone(@IdRes viewId: Int, visible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.visibility = if (visible) View.VISIBLE else View.GONE
        return this
    }

    /**
     * Set a view visibility to VISIBLE (true) or INVISIBLE (false).
     *
     * @param viewId  The view id.
     * @param visible True for VISIBLE, false for INVISIBLE.
     * @return The BaseViewHolder for chaining.
     */
    fun setVisible(@IdRes viewId: Int, visible: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.visibility = if (visible) View.VISIBLE else View.INVISIBLE
        return this
    }


    /**
     * Sets the progress of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @return The BaseViewHolder for chaining.
     */
    fun setProgress(@IdRes viewId: Int, progress: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view?.progress = progress
        return this
    }

    /**
     * Sets the progress and max of a ProgressBar.
     *
     * @param viewId   The view id.
     * @param progress The progress.
     * @param max      The max value of a ProgressBar.
     * @return The BaseViewHolder for chaining.
     */
    fun setProgress(@IdRes viewId: Int, progress: Int, max: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view?.max = max
        view?.progress = progress
        return this
    }

    /**
     * Sets the range of a ProgressBar to 0...max.
     *
     * @param viewId The view id.
     * @param max    The max value of a ProgressBar.
     * @return The BaseViewHolder for chaining.
     */
    fun setMax(@IdRes viewId: Int, max: Int): BaseViewHolder {
        val view = getView<ProgressBar>(viewId)
        view?.max = max
        return this
    }

    /**
     * Sets the rating (the number of stars filled) of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @return The BaseViewHolder for chaining.
     */
    fun setRating(@IdRes viewId: Int, rating: Float): BaseViewHolder {
        val view = getView<RatingBar>(viewId)
        view?.rating = rating
        return this
    }

    /**
     * Sets the rating (the number of stars filled) and max of a RatingBar.
     *
     * @param viewId The view id.
     * @param rating The rating.
     * @param max    The range of the RatingBar to 0...max.
     * @return The BaseViewHolder for chaining.
     */
    fun setRating(@IdRes viewId: Int, rating: Float, max: Int): BaseViewHolder {
        val view = getView<RatingBar>(viewId)
        view?.max = max
        view?.rating = rating
        return this
    }

    /**
     * Sets the on click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on click listener;
     * @return The BaseViewHolder for chaining.
     */
    fun setOnClickListener(@IdRes viewId: Int, listener: OnMultiClickListener): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.setOnClickListener(listener)
        return this
    }

    /**
     * Sets the on long click listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The on long click listener;
     * @return The BaseViewHolder for chaining.
     * Please use [.addOnLongClickListener] (adapter.setOnItemChildLongClickListener(listener))}
     */
    fun setOnLongClickListener(@IdRes viewId: Int, listener: View.OnLongClickListener): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.setOnLongClickListener(listener)
        return this
    }


    /**
     * Sets the on checked change listener of the view.
     *
     * @param viewId   The view id.
     * @param listener The checked change listener of compound button.
     * @return The BaseViewHolder for chaining.
     */
    fun setOnCheckedChangeListener(@IdRes viewId: Int, listener: CompoundButton.OnCheckedChangeListener): BaseViewHolder {
        val view = getView<CompoundButton>(viewId)
        view?.setOnCheckedChangeListener(listener)
        return this
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param tag    The tag;
     * @return The BaseViewHolder for chaining.
     */
    fun setTag(@IdRes viewId: Int, tag: Any): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.tag = tag
        return this
    }

    /**
     * Sets the tag of the view.
     *
     * @param viewId The view id.
     * @param key    The key of tag;
     * @param tag    The tag;
     * @return The BaseViewHolder for chaining.
     */
    fun setTag(@IdRes viewId: Int, key: Int, tag: Any): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.setTag(key, tag)
        return this
    }

    /**
     * Sets the checked status of a checkable.
     *
     * @param viewId  The view id.
     * @param checked The checked status;
     * @return The BaseViewHolder for chaining.
     */
    fun setChecked(@IdRes viewId: Int, checked: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        // View unable cast to Checkable
        if (view is Checkable) {
            (view as Checkable).isChecked = checked
        }
        return this
    }

    /**
     * Set the enabled state of this view.
     *
     * @param viewId  The view id.
     * @param enable The checked status;
     * @return The BaseViewHolder for chaining.
     */
    fun setEnabled(@IdRes viewId: Int, enable: Boolean): BaseViewHolder {
        val view = getView<View>(viewId)
        view?.isEnabled = enable
        return this
    }

    fun <T : View> getView(viewId: Int): T? {
        var view: View? = views.get(viewId)
        if (view == null) {
            view = itemView.findViewById(viewId)
            views.put(viewId, view)
        }
        return view as? T
    }


}