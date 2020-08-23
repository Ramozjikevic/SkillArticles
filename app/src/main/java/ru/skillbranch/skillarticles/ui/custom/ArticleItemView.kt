package ru.skillbranch.skillarticles.ui.custom

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.setPadding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import ru.skillbranch.skillarticles.R
import ru.skillbranch.skillarticles.data.models.ArticleItemData
import ru.skillbranch.skillarticles.extensions.*
import kotlin.math.max

class ArticleItemView@JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    private val colorGray = context.getColor(R.color.color_gray)
    private val colorTextPrimary = context.attrValue(R.attr.colorPrimary)

    private val posterSize = context.dpToIntPx(64)
    private val categorySize = context.dpToIntPx(40)
    private val space24 = context.dpToIntPx(24)
    private val space16 = context.dpToIntPx(16)
    private val space8 = context.dpToIntPx(8)
    private val cornerRadius = context.dpToIntPx(8)
    private val imageSize = context.dpToIntPx(16)

    private val posterWithCategorySize = posterSize + categorySize / 2

    private val tvDate: TextView = TextView(context).apply {
        textSize = 12f
        setTextColor(colorGray)
    }

    private val tvAuthor: TextView = TextView(context).apply {
        id = R.id.tv_author
        textSize = 12f
        setTextColor(colorTextPrimary)
    }


    private val tvTitle: TextView = TextView(context).apply {
        id = R.id.tv_title
        textSize = 18f
        typeface = Typeface.create(typeface, Typeface.BOLD)
        setTextColor(colorTextPrimary)
    }

    private val ivPoster: ImageView = ImageView(context).apply {
        id = R.id.iv_poster
        layoutParams = LayoutParams(posterSize, posterSize)
    }

    private val ivCategory: ImageView = ImageView(context).apply {
        layoutParams = LayoutParams(categorySize, categorySize)
    }

    private val tvDescription: TextView = TextView(context).apply {
        id = R.id.tv_description
        textSize = 14f
        setTextColor(colorGray)
    }

    private val ivLikes: ImageView = ImageView(context).apply {
        layoutParams = LayoutParams(imageSize, imageSize)
        imageTintList = ColorStateList.valueOf(colorGray)
        setImageResource(R.drawable.ic_favorite_black_24dp)
    }

    private val tvLikesCount: TextView = TextView(context).apply {
        textSize = 12f
        setTextColor(colorGray)
    }

    private val ivComments: ImageView = ImageView(context).apply {
        layoutParams = LayoutParams(imageSize, imageSize)
        imageTintList = ColorStateList.valueOf(colorGray)
        setImageResource(R.drawable.ic_insert_comment_black_24dp)
    }

    private val tvCommentCount: TextView = TextView(context).apply {
        textSize = 12f
        setTextColor(colorGray)
    }

    private val tvReadDuration: TextView = TextView(context).apply {
        id = R.id.tv_read_duration
        textSize = 12f
        setTextColor(colorGray)
    }

    private val ivBookmark: ImageView = ImageView(context).apply {
        layoutParams = LayoutParams(imageSize, imageSize)
        imageTintList = ColorStateList.valueOf(colorGray)
        setImageResource(R.drawable.bookmark_states)
    }

    private val views = listOf(
        tvDate,
        tvAuthor,
        tvTitle,
        ivPoster,
        ivCategory,
        tvDescription,
        ivLikes,
        tvLikesCount,
        ivComments,
        tvCommentCount,
        tvReadDuration,
        ivBookmark
    )

    init {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
        views.forEach { addView(it) }
        setPadding(space16)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        var usedHeight = paddingTop + paddingBottom
        val width = View.getDefaultSize(suggestedMinimumWidth, widthMeasureSpec)

        measureChild(tvDate, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvAuthor, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvDate.measuredHeight

        tvTitle.maxWidth = width - (posterWithCategorySize + paddingLeft + paddingRight)
        measureChild(tvTitle, widthMeasureSpec, heightMeasureSpec)
        usedHeight += max(posterWithCategorySize, tvTitle.measuredHeight) + 2 * space8

        measureChild(tvDescription, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvDescription.measuredHeight
        usedHeight += space8

        measureChild(tvLikesCount, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvCommentCount, widthMeasureSpec, heightMeasureSpec)
        measureChild(tvReadDuration, widthMeasureSpec, heightMeasureSpec)
        usedHeight += tvReadDuration.measuredHeight

        setMeasuredDimension(width, usedHeight)
    }


    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        var usedHeight = paddingTop
        val bodyWidth = r - l - paddingLeft - paddingRight
        val left = paddingLeft
        val right = paddingLeft + bodyWidth

        tvDate.layout(
            left,
            usedHeight,
            left + tvDate.measuredWidth,
            usedHeight + tvDate.measuredHeight
        )

        tvAuthor.layout(
            tvDate.right + space16,
            usedHeight,
            right,
            usedHeight + tvAuthor.measuredHeight
        )
        usedHeight += tvAuthor.measuredHeight + space8

        val tvTitleSpace = if (posterWithCategorySize > tvTitle.measuredHeight) {
            (posterWithCategorySize - tvTitle.measuredHeight) / 2
        } else 0

        tvTitle.layout(
            left,
            usedHeight + tvTitleSpace,
            left + tvTitle.measuredWidth,
            usedHeight + tvTitle.measuredHeight + tvTitleSpace
        )

        val ivPosterWithCategorySpace = if (posterWithCategorySize < tvTitle.measuredHeight) {
            (tvTitle.measuredHeight - posterWithCategorySize) / 2
        } else 0

        ivPoster.layout(
            tvTitle.right + space24,
            usedHeight + ivPosterWithCategorySpace,
            right,
            usedHeight + posterSize + ivPosterWithCategorySpace
        )

        ivCategory.layout(
            ivPoster.left - categorySize / 2,
            ivPoster.bottom - categorySize / 2,
            ivPoster.left + categorySize / 2,
            ivPoster.bottom + categorySize / 2
        )

        usedHeight += max(posterWithCategorySize, tvTitle.measuredHeight)
        usedHeight += space8

        tvDescription.layout(
            left,
            usedHeight,
            right,
            usedHeight + tvDescription.measuredHeight
        )

        usedHeight += tvDescription.measuredHeight
        usedHeight += space8

        ivLikes.layout(
            left,
            usedHeight,
            left + imageSize,
            usedHeight + imageSize
        )

        tvLikesCount.layout(
            ivLikes.right + space8,
            usedHeight,
            ivLikes.right + space8 + tvLikesCount.measuredWidth,
            usedHeight + tvLikesCount.measuredHeight
        )

        ivComments.layout(
            tvLikesCount.right + space16,
            usedHeight,
            tvLikesCount.right + space16 + imageSize,
            usedHeight + imageSize
        )

        tvCommentCount.layout(
            ivComments.right + space8,
            usedHeight,
            ivComments.right + space8 + tvCommentCount.measuredWidth,
            usedHeight + tvCommentCount.measuredHeight
        )

        tvReadDuration.layout(
            tvCommentCount.right + space16,
            usedHeight,
            right - space16 - imageSize,
            usedHeight + tvReadDuration.measuredHeight
        )

        ivBookmark.layout(
            right - space16,
            usedHeight,
            right,
            usedHeight + imageSize
        )
    }

    fun bind(data: ArticleItemData) {
        tvDate.text = data.date.format()
        tvAuthor.text = data.author
        tvTitle.text = data.title

        Glide.with(context)
            .load(data.poster)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(posterSize)
            .into(ivPoster)

        Glide.with(context)
            .load(data.categoryIcon)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            .override(categorySize)
            .into(ivCategory)

        tvDescription.text = data.description
        tvLikesCount.text = data.likeCount.toString()
        tvCommentCount.text = data.commentCount.toString()
        tvReadDuration.text = "${data.readDuration} min read"
    }
}