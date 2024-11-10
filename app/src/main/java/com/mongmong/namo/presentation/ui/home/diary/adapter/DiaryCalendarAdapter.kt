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
    private var diaryDates: MutableMap<String, Set<CalendarDate>> = mutableMapOf() // "yyyy-MM": [기록된 날짜들]
    private var isOpeningBottomSheet: Boolean = false
    private var selectedDateView: TextView? = null  // 선택된 날짜의 TextView
    private var selectedDate: CalendarDay? = null  // 선택된 날짜

    fun updateDiaryDates(yearMonth: String, diaryDates: Set<CalendarDate>) {
        this.diaryDates[yearMonth] = diaryDates

        items.forEachIndexed { index, calendarDay ->
            if (calendarDay.toYearMonth() == yearMonth && diaryDates.any { it.date == calendarDay.date.toString() }) {
                notifyItemChanged(index)
            }
        }
    }

    fun updateBottomSheetState(isOpened: Boolean) {
        this.isOpeningBottomSheet = isOpened

        // 화면에 보이는 아이템들의 위치를 가져옴
        val layoutManager = recyclerView.layoutManager ?: return
        val firstVisibleItemPosition = (layoutManager as GridLayoutManager).findFirstVisibleItemPosition()
        val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

        // 화면에 보이는 아이템들에 대해서는 애니메이션 적용
        for (i in firstVisibleItemPosition..lastVisibleItemPosition) {
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(i) as ViewHolder
            viewHolder.updateItemWithAnimate(isOpened)
        }

        // 화면에 보이지 않는 아이템들은 높이 변경을 직접 적용하도록 notify
        if (firstVisibleItemPosition > 0) {
            notifyItemRangeChanged(0, firstVisibleItemPosition)
        }
        if (lastVisibleItemPosition < itemCount - 1) {
            notifyItemRangeChanged(lastVisibleItemPosition + 1, itemCount - lastVisibleItemPosition - 1)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemDiaryCalendarDateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
        holder.updateItem(isOpeningBottomSheet)
    }

    fun getItemAtPosition(position: Int): CalendarDay? {
        return if (position in items.indices) items[position] else null
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemDiaryCalendarDateBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(calendarDay: CalendarDay) {
            binding.calendarDay = calendarDay

            val dateType = diaryDates[calendarDay.toYearMonth()]?.find { it.date == calendarDay.date.toString() }?.type
            binding.diaryCalendarHasDiaryIndicatorIv.visibility = if (dateType != null) View.VISIBLE else View.GONE

            val color = when (dateType) {
                ScheduleType.PERSONAL, ScheduleType.BIRTHDAY -> R.color.text_placeholder
                ScheduleType.MOIM -> R.color.main
                else -> null
            }

            color?.let {
                binding.diaryCalendarHasDiaryIndicatorIv.setColorFilter(
                    binding.root.context.getColor(it),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
            }

            binding.diaryCalendarDateTv.setTextColor(
                binding.root.context.getColor(
                    when {
                        calendarDay.isSameDate(selectedDate) -> R.color.main
                        calendarDay.isAfterToday() -> R.color.text_placeholder
                        else -> R.color.main_text
                    }
                )
            )

            binding.root.setOnClickListener {
                updateSelectedDateView(binding.diaryCalendarDateTv, calendarDay)
                listener.onCalendarDayClick(calendarDay)
            }
        }

        fun updateItem(isOpening: Boolean) {
            // 높이 조정
            val height = dpToPx(if (isOpening) OPEN_HEIGHT else CLOSE_HEIGHT, binding.root.context)
            binding.root.layoutParams = binding.root.layoutParams.apply {
                this.height = height
            }
            binding.root.requestLayout()

            val indicatorImage = if (isOpening) R.drawable.ic_archive_diary_small else R.drawable.ic_archive_diary
            binding.diaryCalendarHasDiaryIndicatorIv.setImageResource(indicatorImage)
        }

        fun updateItemWithAnimate(isOpening: Boolean) {
            // 높이 조정
            val fromHeight = binding.root.height
            val toHeight = dpToPx(if (isOpening) OPEN_HEIGHT else CLOSE_HEIGHT, binding.root.context)

            val valueAnimator = ValueAnimator.ofInt(fromHeight, toHeight)
            valueAnimator.addUpdateListener { animator ->
                val layoutParams = binding.root.layoutParams
                layoutParams.height = animator.animatedValue as Int
                binding.root.layoutParams = layoutParams
            }
            valueAnimator.duration = ANIMATION_DURATION
            valueAnimator.start()

            val indicatorImage = if (isOpening) R.drawable.ic_archive_diary_small else R.drawable.ic_archive_diary
            binding.diaryCalendarHasDiaryIndicatorIv.setImageResource(indicatorImage)
        }

        private fun updateSelectedDateView(newDateView: TextView, newDate: CalendarDay) {
            // 선택한 날짜를 다시 클릭하면 선택을 해제
            if (selectedDate?.isSameDate(newDate) == true) {
                selectedDateView?.setTextColor(
                    binding.diaryCalendarDateTv.context.getColor(
                        if (newDate.isAfterToday()) R.color.text_placeholder else R.color.main_text
                    )
                )
                selectedDate = null
                selectedDateView = null
            } else {
                // 새로운 날짜를 선택
                selectedDateView?.setTextColor(
                    binding.diaryCalendarDateTv.context.getColor(
                        if (newDate.isAfterToday()) R.color.text_placeholder else R.color.main_text
                    )
                )
                selectedDate = newDate
                selectedDateView = newDateView.apply {
                    setTextColor(context.getColor(R.color.main))
                }
            }
        }
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
