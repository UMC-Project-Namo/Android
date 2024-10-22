package com.mongmong.namo.presentation.utils

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.mongmong.namo.R
import com.mongmong.namo.domain.model.ParticipantInfo
import com.mongmong.namo.presentation.config.CategoryColor

object BindingAdapters {
    @JvmStatic
    @BindingAdapter("android:imageSource")
    fun setImageSource(imageView: ImageView, resource: LiveData<Int?>?) {
        if (resource != null && resource.value != null) {
            imageView.setImageResource(resource.value!!)
        }
    }

    @JvmStatic
    @BindingAdapter("app:tintColor")
    fun setTintColor(view: View, color: Int) {
        val hexColor = when (color) {
            in 1..CategoryColor.getAllColors().size -> CategoryColor.getAllColors()[color - 1]
            else -> CategoryColor.DEFAULT_PALETTE_COLOR1.hexColor
        }
        view.backgroundTintList = ColorStateList.valueOf(Color.parseColor(hexColor))
    }

    @JvmStatic
    @BindingAdapter("drawableTintColor")
    fun setDrawableTintColor(view: AppCompatTextView, color: Int) {
        // TextView의 drawableEnd를 가져옴
        val drawables = view.compoundDrawablesRelative
        val drawableEnd = drawables[2] // drawableEnd는 배열의 세 번째 요소

        drawableEnd?.let {
            // 전달된 color 값을 사용하여 tint 적용
            it.setTint(color)
            view.setCompoundDrawablesRelativeWithIntrinsicBounds(
                drawables[0], drawables[1], it, drawables[3]
            )
        }
    }

    @JvmStatic
    @BindingAdapter("app:isGoneIfEmpty")
    fun setGoneIfEmpty(view: View, images: List<Any>?) {
        view.visibility = if (images.isNullOrEmpty()) View.GONE else View.VISIBLE
    }

    @JvmStatic
    @BindingAdapter("app:imageTint")
    fun setImageViewTint(imageView: ImageView, color: Int) {
        imageView.imageTintList =
            ColorStateList.valueOf(ContextCompat.getColor(imageView.context, color))
    }

    @JvmStatic
    @BindingAdapter("app:imageUrl", "app:placeHolder")
    fun setImage(imageView : ImageView, url : String?, placeHolder: Drawable?) {
        if (placeHolder == null) {
            Glide.with(imageView.context)
                .load(url)
                .into(imageView)
        }
        Glide.with(imageView.context)
            .load(url)
            .placeholder(placeHolder)
            .into(imageView)
    }

    @JvmStatic
    @BindingAdapter("app:tint")
    fun ImageView.setImageTint(@ColorInt color: Int) {
        setColorFilter(color)
    }

    @JvmStatic
    @BindingAdapter(value = ["participantsText", "maxCount"], requireAll = false)
    fun setParticipantsText(textView: TextView, participants: List<ParticipantInfo>?, maxCount: Int?) {
        val maxCount = maxCount ?: 3

        participants?.let {
            if (it.isEmpty()) {
                // 참가자가 없을 때
                textView.text = textView.context.getString(R.string.moim_diary_none)
            } else {
                // 참가자가 있을 때 처리
                val size = participants.size
                val displayedNames = participants.take(maxCount).joinToString(", ") { participant -> participant.nickname }

                textView.text = if (size > maxCount) {
                    "$displayedNames 외 ${size - maxCount}명"
                } else {
                    displayedNames
                }
            }
        } ?: run {
            textView.text = textView.context.getString(R.string.diary_no_place)
        }
    }


}
