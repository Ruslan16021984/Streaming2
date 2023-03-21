package com.natife.streaming.ext

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Paint.Align
import android.graphics.Paint.FontMetrics
import android.graphics.Rect
import android.text.TextUtils
import android.util.AttributeSet
import android.view.View
import android.view.View.OnLayoutChangeListener
import androidx.annotation.*
import com.google.android.material.animation.AnimationUtils
import com.google.android.material.internal.TextDrawableHelper
import com.google.android.material.internal.TextDrawableHelper.TextDrawableDelegate
import com.google.android.material.resources.TextAppearance
import com.google.android.material.shape.EdgeTreatment
import com.google.android.material.shape.MarkerEdgeTreatment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.OffsetEdgeTreatment

@SuppressLint("RestrictedApi")
class SlideWindow private constructor(
    context: Context,
    attrs: AttributeSet?,
    @AttrRes defStyleAttr: Int,
    @StyleRes defStyleRes: Int
) :
    MaterialShapeDrawable(context, attrs, defStyleAttr, defStyleRes), TextDrawableDelegate {
    @Nullable
    private var text: CharSequence? = null
    private val context: Context

    @Nullable
    private val fontMetrics = FontMetrics()
    private val textDrawableHelper = TextDrawableHelper( /* delegate= */this)
    private val attachedViewLayoutChangeListener =
        OnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
            updateLocationOnScreen(
                v
            )
        }
    private val displayFrame = Rect()
    private var padding = 0
    private var minWidth = 0
    private var minHeight = 0
    private var layoutMargin = 0
    private var arrowSize = 0
    private var locationOnScreenX = 0
    private var tooltipScaleX = 1f
    private var tooltipScaleY = 1f
    private val tooltipPivotX = 0.5f
    private var tooltipPivotY = 0.5f
    private var labelOpacity = 1.0f


    /**
     * Return the text that TooltipDrawable is displaying.
     *
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_text
     */
    @Nullable
    fun getText(): CharSequence? {
        return text
    }

    /**
     * Sets the text to be displayed using a string resource identifier.
     *
     * @param id the resource identifier of the string resource to be displayed
     * @see .setText
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_text
     */
    fun setTextResource(@StringRes id: Int) {
        setText(context.getResources().getString(id))
    }

    /**
     * Sets the text to be displayed.
     *
     * @param text text to be displayed
     * @see .setTextResource
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_text
     */
    fun setText(@Nullable text: CharSequence?) {
        if (!TextUtils.equals(this.text, text)) {
            this.text = text
            textDrawableHelper.isTextWidthDirty = true
            invalidateSelf()
        }
    }
    /**
     * Returns the TextAppearance used by this tooltip.
     *
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_textAppearance
     */
    /**
     * Sets this tooltip's text appearance.
     *
     * @param textAppearance This tooltip's text appearance.
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_textAppearance
     */
    @get:Nullable
    var textAppearance: TextAppearance?
        get() = textDrawableHelper.textAppearance
        set(textAppearance) {
            textDrawableHelper.setTextAppearance(textAppearance, context)
        }

    /**
     * Sets this tooltip's text appearance using a resource id.
     *
     * @param id The resource id of this tooltip's text appearance.
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_textAppearance
     */
    fun setTextAppearanceResource(@StyleRes id: Int) {
        textAppearance = TextAppearance(context, id)
    }

    /**
     * Returns the minimum width of TooltipDrawable in terms of pixels.
     *
     * @see .setMinWidth
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_minWidth
     */
    fun getMinWidth(): Int {
        return minWidth
    }

    /**
     * Sets the width of the TooltipDrawable to be at least `minWidth` wide.
     *
     * @param minWidth the minimum width of TooltipDrawable in terms of pixels
     * @see .getMinWidth
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_minWidth
     */
    fun setMinWidth(@Px minWidth: Int) {
        this.minWidth = minWidth
        invalidateSelf()
    }

    /**
     * Returns the minimum height of TooltipDrawable in terms of pixels.
     *
     * @see .setMinHeight
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_minHeight
     */
    fun getMinHeight(): Int {
        return minHeight
    }

    /**
     * Sets the height of the TooltipDrawable to be at least `minHeight` wide.
     *
     * @param minHeight the minimum height of TooltipDrawable in terms of pixels
     * @see .getMinHeight
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_minHeight
     */
    fun setMinHeight(@Px minHeight: Int) {
        this.minHeight = minHeight
        invalidateSelf()
    }
    /**
     * Returns the padding between the text of TooltipDrawable and the sides in terms of pixels.
     *
     * @see .setTextPadding
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_padding
     */
    /**
     * Sets the padding between the text of the TooltipDrawable and the sides to be `padding`.
     *
     * @param padding the padding to use around the text
     * @see .getTextPadding
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_padding
     */
    var textPadding: Int
        get() = padding
        set(padding) {
            this.padding = padding
            invalidateSelf()
        }

    /**
     * Returns the margin around the TooltipDrawable.
     *
     * @see .setLayoutMargin
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_layout_margin
     */
    fun getLayoutMargin(): Int {
        return layoutMargin
    }

    /**
     * Sets the margin around the TooltipDrawable to be `margin`.
     *
     * @param layoutMargin the margin to use around the TooltipDrawable
     * @see .getLayoutMargin
     * @attr ref com.google.android.material.R.styleable#Tooltip_android_layout_margin
     */
    fun setLayoutMargin(@Px layoutMargin: Int) {
        this.layoutMargin = layoutMargin
        invalidateSelf()
    }

    /**
     * A fraction that controls the scale of the tooltip and the opacity of its text.
     *
     *
     * When fraction is 0.0, the tooltip will be completely hidden, as fraction approaches 1.0, the
     * tooltip will scale up from its pointer and animate in its text.
     *
     *
     * This method is typically called from within an animator's update callback. The animator in
     * this case is what is driving the animation while this method handles configuring the tooltip's
     * appearance at each frame in the animation.
     *
     * @param fraction A value between 0.0 and 1.0 that defines how "shown" the tooltip will be.
     */
    @SuppressLint("RestrictedApi")
    fun setRevealFraction(@FloatRange(from = 0.0, to = 1.0) fraction: Float) {
        // Set the y pivot point below the bottom of the tooltip to make it look like the
        // tooltip is translating slightly up while scaling in.
        tooltipPivotY = 1.2f
        tooltipScaleX = fraction
        tooltipScaleY = fraction
        labelOpacity = AnimationUtils.lerp(0f, 1f, 0.19f, 1f, fraction)
        invalidateSelf()
    }

    /**
     * Should be called to allow this drawable to calculate its position within the current display
     * frame. This allows it to apply to specified window padding.
     *
     * @see .detachView
     */
    fun setRelativeToView(@Nullable view: View?) {
        if (view == null) {
            return
        }
        updateLocationOnScreen(view)
        // Listen for changes that indicate the view has moved so the location can be updated
        view.addOnLayoutChangeListener(attachedViewLayoutChangeListener)
    }

    /**
     * Should be called when the view is detached from the screen.
     *
     * @see .setRelativeToView
     */
    fun detachView(@Nullable view: View?) {
        if (view == null) {
            return
        }
        view.removeOnLayoutChangeListener(attachedViewLayoutChangeListener)
    }

    override fun getIntrinsicWidth(): Int {
        return Math.max(2 * padding + textWidth, minWidth.toFloat()).toInt()
    }

    @SuppressLint("RestrictedApi")
    override fun getIntrinsicHeight(): Int {
        return Math.max(textDrawableHelper.textPaint.textSize, minHeight.toFloat())
            .toInt()
    }

    override fun draw(canvas: Canvas) {
        canvas.save()

        // Translate the canvas by the same about that the pointer is offset to keep it pointing at the
        // same place relative to the bounds.
        val translateX = calculatePointerOffset()

        // Handle the extra space created by the arrow notch at the bottom of the tooltip by moving the
        // canvas. This allows the pointing part of the tooltip to align with the bottom of the bounds.
        val translateY = (-(arrowSize * Math.sqrt(2.0) - arrowSize)).toFloat()

        // Scale the tooltip. Use the bounds to set the pivot points relative to this drawable since
        // the supplied canvas is not necessarily the same size.
        canvas.scale(
            tooltipScaleX,
            tooltipScaleY,
            bounds.left + bounds.width() * tooltipPivotX,
            bounds.top + bounds.height() * tooltipPivotY
        )
        canvas.translate(translateX, translateY)

        // Draw the background.
        super.draw(canvas)

        // Draw the text.
        drawText(canvas)
        canvas.restore()
    }

    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)

        // Update the marker edge since the location of the marker arrow can move depending on the the
        // bounds.
        shapeAppearanceModel =
            shapeAppearanceModel.toBuilder().setBottomEdge(createMarkerEdge()).build()
    }

    override fun onStateChange(state: IntArray): Boolean {
        // Exposed for TextDrawableDelegate.
        return super.onStateChange(state)
    }

    override fun onTextSizeChange() {
        invalidateSelf()
    }

    private fun updateLocationOnScreen(v: View) {
        val locationOnScreen = IntArray(2)
        v.getLocationOnScreen(locationOnScreen)
        locationOnScreenX = locationOnScreen[0]
        v.getWindowVisibleDisplayFrame(displayFrame)
    }

    private fun calculatePointerOffset(): Float {
        var pointerOffset = 0f
        if (displayFrame.right - bounds.right - locationOnScreenX - layoutMargin < 0) {
            pointerOffset =
                (displayFrame.right - bounds.right - locationOnScreenX - layoutMargin).toFloat()
        } else if (displayFrame.left - bounds.left - locationOnScreenX + layoutMargin > 0) {
            pointerOffset =
                (displayFrame.left - bounds.left - locationOnScreenX + layoutMargin).toFloat()
        }
        return pointerOffset
    }

    private fun createMarkerEdge(): EdgeTreatment {
        var offset = -calculatePointerOffset()
        // The maximum distance the arrow can be offset before extends outside the bounds.
        val maxArrowOffset = (bounds.width() - arrowSize * Math.sqrt(2.0)).toFloat() / 2.0f
        offset = Math.max(offset, -maxArrowOffset)
        offset = Math.min(offset, maxArrowOffset)
        return OffsetEdgeTreatment(MarkerEdgeTreatment(arrowSize.toFloat()), offset)
    }

    private fun drawText(canvas: Canvas) {
        if (text == null) {
            // If text is null there's nothing to draw.
            return
        }
        val bounds = bounds
        val y = calculateTextOriginAndAlignment(bounds).toInt()
        if (textDrawableHelper.textAppearance != null) {
            textDrawableHelper.textPaint.drawableState = state
            textDrawableHelper.updateTextPaintDrawState(context)
            textDrawableHelper.textPaint.alpha = (labelOpacity * 255).toInt()
        }
        canvas.drawText(
            text!!,
            0,
            text!!.length,
            bounds.centerX().toFloat(),
            y.toFloat(),
            textDrawableHelper.textPaint
        )
    }

    private val textWidth: Float
        private get() = if (text == null) {
            0f
        } else textDrawableHelper.getTextWidth(text.toString())

    /** Calculates the text origin and alignment based on the bounds.  */
    private fun calculateTextOriginAndAlignment(bounds: Rect): Float {
        return bounds.centerY() - calculateTextCenterFromBaseline()
    }

    /**
     * Calculates the offset from the visual center of the text to its baseline.
     *
     *
     * To draw the text, we provide the origin to [Canvas.drawText]. This origin always corresponds vertically to the text's baseline.
     * Because we need to vertically center the text, we need to calculate this offset.
     *
     *
     * Note that tooltips that share the same font must have consistent text baselines despite
     * having different text strings. This is why we calculate the vertical center using [ ][Paint.getFontMetrics] rather than [Paint.getTextBounds].
     */
    private fun calculateTextCenterFromBaseline(): Float {
        textDrawableHelper.textPaint.getFontMetrics(fontMetrics)
        return (fontMetrics.descent + fontMetrics.ascent) / 2f
    }

    init {
        this.context = context
        textDrawableHelper.textPaint.density = context.getResources().getDisplayMetrics().density
        textDrawableHelper.textPaint.textAlign = Align.CENTER
    }
}