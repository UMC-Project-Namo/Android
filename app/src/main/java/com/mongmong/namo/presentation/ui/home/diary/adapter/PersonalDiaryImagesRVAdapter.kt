package com.mongmong.namo.presentation.ui.home.diary.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.mongmong.namo.databinding.ItemDiaryImageBinding
import com.mongmong.namo.domain.model.DiaryImage

class PersonalDiaryImagesRVAdapter(
    val deleteClickListener: (diaryImage: DiaryImage) -> Unit,
    val imageClickListener: (List<DiaryImage>) -> Unit
) : RecyclerView.Adapter<PersonalDiaryImagesRVAdapter.ViewHolder>() {

    private val items = ArrayList<DiaryImage>()

    fun addImages(imgs: List<DiaryImage>) {
        this.items.clear()
        this.items.addAll(imgs)
        notifyDataSetChanged()
    }

    private fun removeImage(position: Int) {
        val removedImage = items.removeAt(position)
        notifyItemRemoved(position)
        deleteClickListener(removedImage)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemDiaryImageBinding = ItemDiaryImageBinding.inflate(
            LayoutInflater.from(viewGroup.context), viewGroup, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemDiaryImageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(image: DiaryImage) {
            val requestOptions = RequestOptions().diskCacheStrategy(DiskCacheStrategy.DATA)
            Glide.with(binding.imageIv.context)
                .load(image.imageUrl)
                .apply(requestOptions)
                .into(binding.imageIv)

            binding.imageDeleteBtn.setOnClickListener {
                removeImage(bindingAdapterPosition)
            }

            binding.imageIv.setOnClickListener {
                imageClickListener(items)
            }
        }
    }
}
