package com.mongmong.namo.presentation.ui.home.diary.adapter

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ItemDiaryCalendarDateBinding
import com.mongmong.namo.domain.model.CalendarDate
import com.mongmong.namo.domain.model.CalendarDay
import com.mongmong.namo.domain.model.ScheduleType
import com.mongmong.namo.presentation.utils.converter.DiaryDateConverter.toYearMonth

class DiaryCalendarAdapter(
    private val recyclerView: RecyclerView,
    private val items: List<CalendarDay>,
    private val listener: OnCalendarListener
) : RecyclerView.Adapter<DiaryCalendarAdapter.ViewHolder>() {

    private var diaryDates: MutableMap<String, Set<CalendarDate>> = mutableMapOf()
    private var isBottomSheetOpen: Boolean = false
    private var selectedDate: CalendarDay? = null // 선택된 날짜
    private var visibleMonth: String? = null

    fun updateDiaryDates(yearMonth: String, diaryDates: Set<CalendarDate>) {
        this.diaryDates[yearMonth] = diaryDates
        items.forEachIndexed { index, calendarDay ->
            if (calendarDay.toYearMonth() == yearMonth) notifyItemChanged(index)
        }
    }

    fun updateVisibleMonth(visibleMonth: String) {
        if (this.visibleMonth != visibleMonth) {
            this.visibleMonth = visibleMonth
            notifyDataSetChanged()
        }
    }

    fun updateBottomSheetState(isOpened: Boolean) {
        if (isBottomSheetOpen != isOpened) {
            isBottomSheetOpen = isOpened
            val layoutManager = recyclerView.layoutManager as? GridLayoutManager ?: return
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

            for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
                (recyclerView.findViewHolderForAdapterPosition(i) as? ViewHolder)
                    ?.updateItemWithAnimate(isOpened)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryCalendarDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val isSelected = selectedDate?.isSameDate(item) == true
        holder.bind(item, isSelected)
        holder.updateItem(isBottomSheetOpen)
    }

    fun getItemAtPosition(position: Int): CalendarDay? {
        return if (position in items.indices) items[position] else null
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemDiaryCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendarDay: CalendarDay, isSelected: Boolean) {
            binding.calendarDay = calendarDay

            val dateType = diaryDates[calendarDay.toYearMonth()]
                ?.find { it.date == calendarDay.date.toString() }?.type
            binding.diaryCalendarHasDiaryIndicatorIv.visibility = if (dateType != null) View.VISIBLE else View.GONE

            val color = when (dateType) {
                ScheduleType.PERSONAL, ScheduleType.BIRTHDAY -> R.color.text_placeholder
                ScheduleType.MOIM -> R.color.main
                else -> null
            }
            color?.let {
                binding.diaryCalendarHasDiaryIndicatorIv.setColorFilter(
                    binding.root.context.getColor(it), android.graphics.PorterDuff.Mode.SRC_IN
                )
            }

            binding.diaryCalendarDateTv.setTextColor(
                binding.root.context.getColor(
                    when {
                        isSelected -> R.color.main
                        calendarDay.toYearMonth() == visibleMonth -> R.color.main_text
                        else -> R.color.text_placeholder
                    }
                )
            )
            binding.diaryCalendarDateTv.invalidate()

            binding.root.setOnClickListener {
                updateSelectedDate(calendarDay)
                listener.onCalendarDayClick(calendarDay)
            }
        }

        fun updateItem(isOpening: Boolean) {
            val height = dpToPx(if (isOpening) OPEN_HEIGHT else CLOSE_HEIGHT, binding.root.context)
            binding.root.layoutParams = binding.root.layoutParams.apply { this.height = height }
            binding.root.requestLayout()
        }

        fun updateItemWithAnimate(isOpening: Boolean) {
            val fromHeight = binding.root.height
            val toHeight = dpToPx(if (isOpening) OPEN_HEIGHT else CLOSE_HEIGHT, binding.root.context)
            ValueAnimator.ofInt(fromHeight, toHeight).apply {
                addUpdateListener { animator ->
                    binding.root.layoutParams = binding.root.layoutParams.apply {
                        height = animator.animatedValue as Int
                    }
                }
                duration = ANIMATION_DURATION
                start()
            }
        }
    }

    private fun updateSelectedDate(newDate: CalendarDay) {
        val oldSelectedPosition = items.indexOfFirst { it.isSameDate(selectedDate) }
        val newSelectedPosition = items.indexOfFirst { it.isSameDate(newDate) }

        selectedDate = if (selectedDate?.isSameDate(newDate) == true) null else newDate

        if (oldSelectedPosition != -1) notifyItemChanged(oldSelectedPosition)
        if (newSelectedPosition != -1) notifyItemChanged(newSelectedPosition)
    }

    interface OnCalendarListener {
        fun onCalendarDayClick(calendarDay: CalendarDay)
    }

    private fun dpToPx(dp: Int, context: android.content.Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }

    companion object {
        const val ANIMATION_DURATION = 170L
        const val OPEN_HEIGHT = 56
        const val CLOSE_HEIGHT = 84
    }
}
