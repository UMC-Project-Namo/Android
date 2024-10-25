package com.mongmong.namo.presentation.ui.community.diary.adapter

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ItemMoimDiaryActivityBinding
import com.mongmong.namo.databinding.ItemMoimDiaryDiaryBinding
import com.mongmong.namo.domain.model.Activity
import com.mongmong.namo.domain.model.DiaryDetail
import com.mongmong.namo.domain.model.DiaryImage
import com.mongmong.namo.presentation.utils.DiaryDateConverter
import java.util.Calendar

class MoimDiaryVPAdapter(
    private val diaryEventListener: OnDiaryEventListener,
    private val activityEventListener: OnActivityEventListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val activities = mutableListOf<Activity>()
    private var diary = DiaryDetail("", 0L, emptyList(), 3)
    private var isEditMode: Boolean = false  // 편집 모드 상태 저장
    private var hasDiary: Boolean = false
    private var scheduleStartDate: String = ""
    private var scheduleEndDate: String = ""

    fun updateDiary(diary: DiaryDetail) {
        this.diary = diary
        notifyItemChanged(0)
    }

    fun setScheduleDate(startDate: String, endDate: String) {
        this.scheduleStartDate = startDate
        this.scheduleEndDate = endDate
    }

    fun submitActivities(newActivities: List<Activity>) {
        activities.clear()
        activities.addAll(newActivities)
        notifyDataSetChanged()
    }

    fun setEditMode(isEditMode: Boolean) {
        this.isEditMode = isEditMode
        notifyDataSetChanged()
    }

    fun setHasDiary(hasDiary: Boolean) {
        this.hasDiary = hasDiary
        notifyItemChanged(0) // 일기장
    }

    override fun getItemCount(): Int = 1 + activities.size // diary 하나 + activities

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) VIEW_TYPE_DIARY else VIEW_TYPE_ACTIVITY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DIARY -> {
                DiaryViewHolder(
                    ItemMoimDiaryDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                )
            }
            VIEW_TYPE_ACTIVITY -> {
                ActivityViewHolder(
                    ItemMoimDiaryActivityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ActivityViewHolder) {
            val activity = activities[position - 1] // 첫 번째는 diary이므로 인덱스 조정
            holder.bind(activity)
        } else if (holder is DiaryViewHolder) {
            holder.bind(diary)
        }
    }

    inner class DiaryViewHolder(private val binding: ItemMoimDiaryDiaryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(diary: DiaryDetail) {
            binding.diary = diary
            binding.isEdit = isEditMode
            binding.hasDiary = hasDiary

            binding.diaryPrivateGuideBtn.setOnClickListener{ diaryEventListener.onGuideClicked() }
            binding.diaryEditBtn.setOnClickListener { diaryEventListener.onEditModeClicked() }
            binding.diaryViewBtn.setOnClickListener { diaryEventListener.onViewModeClicked() }
            binding.diaryDeleteBtn.setOnClickListener { diaryEventListener.onDeleteDiary() }
            // Content 변경 이벤트 처리
            binding.diaryContentEt.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    diaryEventListener.onContentChanged(s.toString())
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // Enjoy 클릭 이벤트 처리
            binding.diaryEnjoy1Iv.setOnClickListener { diaryEventListener.onEnjoyClicked(1) }
            binding.diaryEnjoy2Iv.setOnClickListener { diaryEventListener.onEnjoyClicked(2) }
            binding.diaryEnjoy3Iv.setOnClickListener { diaryEventListener.onEnjoyClicked(3) }

            binding.diaryAddImageIv.setOnClickListener { diaryEventListener.onAddImageClicked() }

            // 이미지 리스트 어댑터 설정
            val adapter = MoimDiaryImagesRVAdapter(
                itemClickListener = { diaryEventListener.onImageClicked(diary.diaryImages) },
                deleteClickListener = { diaryImage -> diaryEventListener.onDeleteImage(diaryImage) },
                isEditMode
            )
            binding.diaryImagesRv.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = null
            }
            adapter.addItem(diary.diaryImages)
        }
    }

    inner class ActivityViewHolder(
        private val binding: ItemMoimDiaryActivityBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(activity: Activity) {
            binding.activity = activity
            binding.isEdit = isEditMode
            binding.hasDiary = hasDiary

            binding.activityEditBtn.setOnClickListener { activityEventListener.onEditModeClicked() }
            binding.activityViewBtn.setOnClickListener { activityEventListener.onViewModeClicked() }

            binding.activityDeleteBtn.setOnClickListener {
                activityEventListener.onDeleteActivity(bindingAdapterPosition - 1)
            }

            binding.activityTitleTv.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    activityEventListener.onActivityNameChanged(s.toString(), bindingAdapterPosition - 1)
                }
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            })

            // 시작 날짜 클릭 시
            binding.activityStartDateTv.setOnClickListener {
                handleDateViews(binding.activityStartDateLayout, binding.activityStartDateTv)
            }
            // 시작 시간 클릭 시
            binding.activityStartTimeTv.setOnClickListener {
                handleDateViews(binding.activityStartTimeLayout, binding.activityStartTimeTv)
            }
            // 종료 날짜 클릭 시
            binding.activityEndDateTv.setOnClickListener {
                handleDateViews(binding.activityEndDateLayout, binding.activityEndDateTv)
            }
            // 종료 시간 클릭 시
            binding.activityEndTimeTv.setOnClickListener {
                handleDateViews(binding.activityEndTimeLayout, binding.activityEndTimeTv)
            }

            initPickerListeners(activity)

            binding.activityPlaceTv.setOnClickListener {
                activityEventListener.onLocationClicked(bindingAdapterPosition - 1)
            }

            binding.activityAddImageIv.setOnClickListener {
                activityEventListener.onAddImageClicked(bindingAdapterPosition - 1)
            }

            // 이미지 리스트 어댑터 설정
            val adapter = MoimDiaryImagesRVAdapter(
                itemClickListener = { activityEventListener.onImageClicked(activity.images) },
                deleteClickListener = { activityImage -> activityEventListener.onDeleteImage(bindingAdapterPosition - 1, activityImage) },
                isEditMode
            )
            binding.activityImagesRv.apply {
                this.adapter = adapter
                layoutManager = LinearLayoutManager(binding.root.context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = null
            }
            adapter.addItem(activity.images)

            binding.activityParticipantsTv.setOnClickListener {
                activityEventListener.onParticipantsClicked(bindingAdapterPosition - 1)
            }

            binding.activityPaymentTv.setOnClickListener {
                activityEventListener.onPayClicked(bindingAdapterPosition - 1)
            }
        }

        private fun initPickerListeners(activity: Activity) {
            // 시작 날짜와 시간
            val startDateTime = activity.startDate.split("T")
            val startDate = startDateTime[0].split("-")
            val startTime = startDateTime[1].split(":")

            // 종료 날짜와 시간
            val endDateTime = activity.endDate.split("T")
            val endDate = endDateTime[0].split("-")
            val endTime = endDateTime[1].split(":")

            // 일정의 시작 및 종료 날짜를 파싱하여 Calendar로 변환
            val scheduleStartCalendar = Calendar.getInstance().apply {
                time = DiaryDateConverter.parseDate(scheduleStartDate)
            }
            val scheduleEndCalendar = Calendar.getInstance().apply {
                time = DiaryDateConverter.parseDate(scheduleEndDate)
            }

            // 시작 날짜 설정
            binding.activityStartDateDp.init(
                startDate[0].toInt(),
                startDate[1].toInt() - 1,
                startDate[2].toInt()
            ) { _, year, monthOfYear, dayOfMonth ->
                val updatedDate = DiaryDateConverter.formatDateToDiaryString(year, monthOfYear, dayOfMonth, startDateTime[1])
                val updatedEndDate = DiaryDateConverter.formatDateToDiaryString(
                    binding.activityEndDateDp.year,
                    binding.activityEndDateDp.month,
                    binding.activityEndDateDp.dayOfMonth,
                    endDateTime[1]
                )

                // 텍스트뷰에 업데이트된 날짜 설정
                binding.activityStartDateTv.text = DiaryDateConverter.toDate(updatedDate)

                // 시작 날짜가 종료 날짜를 넘어가면 종료 날짜를 시작 날짜로 맞춤
                if (updatedDate > updatedEndDate) {
                    binding.activityEndDateDp.updateDate(year, monthOfYear, dayOfMonth)
                    binding.activityEndDateTv.text = DiaryDateConverter.toDate(updatedDate)
                    activityEventListener.onEndDateSelected(bindingAdapterPosition - 1, updatedDate)
                }
                activityEventListener.onStartDateSelected(bindingAdapterPosition - 1, updatedDate)
            }

            // 시작 날짜 선택기에 최소 및 최대 날짜 설정
            binding.activityStartDateDp.minDate = scheduleStartCalendar.timeInMillis
            binding.activityStartDateDp.maxDate = scheduleEndCalendar.timeInMillis

            // 종료 날짜 설정
            binding.activityEndDateDp.init(
                endDate[0].toInt(),
                endDate[1].toInt() - 1,
                endDate[2].toInt()
            ) { _, year, monthOfYear, dayOfMonth ->
                val updatedEndDate = DiaryDateConverter.formatDateToDiaryString(year, monthOfYear, dayOfMonth, endDateTime[1])
                val updatedStartDate = DiaryDateConverter.formatDateToDiaryString(
                    binding.activityStartDateDp.year,
                    binding.activityStartDateDp.month,
                    binding.activityStartDateDp.dayOfMonth,
                    startDateTime[1]
                )

                // 텍스트뷰에 업데이트된 종료 날짜 설정
                binding.activityEndDateTv.text = DiaryDateConverter.toDate(updatedEndDate)

                // 종료 날짜가 시작 날짜를 넘지 않도록 설정
                if (updatedEndDate < updatedStartDate) {
                    binding.activityStartDateDp.updateDate(year, monthOfYear, dayOfMonth)
                    binding.activityStartDateTv.text = DiaryDateConverter.toDate(updatedEndDate)
                    activityEventListener.onStartDateSelected(bindingAdapterPosition - 1, updatedEndDate)
                }

                activityEventListener.onEndDateSelected(bindingAdapterPosition - 1, updatedEndDate)
            }

            // 종료 날짜 선택기에 최소 및 최대 날짜 설정
            binding.activityEndDateDp.minDate = scheduleStartCalendar.timeInMillis
            binding.activityEndDateDp.maxDate = scheduleEndCalendar.timeInMillis

            // 시작 시간 설정
            with(binding.activityStartTimeTp) {
                this.hour = startTime[0].toInt()
                this.minute = startTime[1].toInt()

                this.setOnTimeChangedListener { _, hourOfDay, minute ->
                    val updatedTime = DiaryDateConverter.formatTimeToDiaryString(hourOfDay, minute, startTime[2])
                    val updatedStartDateTime = "${binding.activityStartDateTv.text}T$updatedTime"
                    val updatedEndTime = DiaryDateConverter.formatTimeToDiaryString(
                        binding.activityEndTimeTp.hour,
                        binding.activityEndTimeTp.minute,
                        endTime[2]
                    )
                    val updatedEndDateTime = "${binding.activityEndDateTv.text}T$updatedEndTime"

                    binding.activityStartTimeTv.text = DiaryDateConverter.to12HourTime(updatedStartDateTime)

                    // 시작 시간이 종료 시간을 넘어가지 않도록 설정
                    if (updatedStartDateTime >= updatedEndDateTime) {
                        binding.activityEndTimeTp.hour = hourOfDay
                        binding.activityEndTimeTp.minute = minute
                        binding.activityEndTimeTv.text = DiaryDateConverter.to12HourTime(updatedEndDateTime)
                        activityEventListener.onEndDateSelected(bindingAdapterPosition - 1, updatedEndDateTime)
                    }

                    activityEventListener.onStartDateSelected(bindingAdapterPosition - 1, updatedStartDateTime)
                }
            }

            // 종료 시간 설정
            with(binding.activityEndTimeTp) {
                this.hour = endTime[0].toInt()
                this.minute = endTime[1].toInt()

                this.setOnTimeChangedListener { _, hourOfDay, minute ->
                    val updatedEndTime = DiaryDateConverter.formatTimeToDiaryString(hourOfDay, minute, endTime[2])
                    val updatedEndDateTime = "${binding.activityEndDateTv.text}T$updatedEndTime"
                    val updatedStartTime = DiaryDateConverter.formatTimeToDiaryString(
                        binding.activityStartTimeTp.hour,
                        binding.activityStartTimeTp.minute,
                        startTime[2]
                    )
                    val updatedStartDateTime = "${binding.activityStartDateTv.text}T$updatedStartTime"

                    binding.activityEndTimeTv.text = DiaryDateConverter.to12HourTime(updatedEndDateTime)

                    // 종료 시간이 시작 시간보다 빠르지 않도록 설정
                    if (updatedEndDateTime <= updatedStartDateTime) {
                        binding.activityStartTimeTp.hour = hourOfDay
                        binding.activityStartTimeTp.minute = minute
                        binding.activityStartTimeTv.text = DiaryDateConverter.to12HourTime(updatedStartDateTime)
                        activityEventListener.onStartDateSelected(bindingAdapterPosition - 1, updatedStartDateTime)
                    }

                    activityEventListener.onEndDateSelected(bindingAdapterPosition - 1, updatedEndDateTime)
                }
            }
        }


        private fun handleDateViews(targetMotionLayout: MotionLayout, selectedTextView: TextView) {
            // 모든 MotionLayout 리스트
            val allMotionLayouts = listOf(
                binding.activityStartDateLayout,
                binding.activityStartTimeLayout,
                binding.activityEndDateLayout,
                binding.activityEndTimeLayout
            )

            val allTextViews = listOf(
                binding.activityStartDateTv,
                binding.activityStartTimeTv,
                binding.activityEndDateTv,
                binding.activityEndTimeTv
            )

            // 다른 MotionLayout들은 닫기 및 텍스트뷰 색상 복원
            allMotionLayouts.forEach { layout ->
                if (layout != targetMotionLayout && layout.progress != 0f) {
                    layout.transitionToStart() // 닫기 애니메이션
                }
            }

            allTextViews.forEach { textView ->
                if (textView != selectedTextView) {
                    textView.setTextColor(textView.context.getColor(R.color.main_text)) // 다른 텍스트뷰는 기본 색상으로
                }
            }

            // 현재 MotionLayout이 열려있으면 닫고 텍스트 색상도 원래대로 복원
            if (targetMotionLayout.progress == 0f) {
                targetMotionLayout.transitionToEnd() // 열기 애니메이션
                selectedTextView.setTextColor(selectedTextView.context.getColor(R.color.main)) // 선택된 텍스트뷰 색상 변경
            } else {
                targetMotionLayout.transitionToStart() // 닫기 애니메이션
                selectedTextView.setTextColor(selectedTextView.context.getColor(R.color.main_text)) // 다시 클릭 시 색상 복원
            }
        }
    }

    companion object {
        private const val VIEW_TYPE_DIARY = 0
        private const val VIEW_TYPE_ACTIVITY = 1
    }

    interface OnActivityEventListener {
        fun onAddImageClicked(position: Int)
        fun onDeleteActivity(position: Int)
        fun onActivityNameChanged(name: String, position: Int)
        fun onStartDateSelected(position: Int, date: String)
        fun onEndDateSelected(position: Int, date: String)
        fun onLocationClicked(position: Int)
        fun onParticipantsClicked(position: Int)
        fun onPayClicked(position: Int)
        fun onDeleteImage(position: Int, image: DiaryImage)
        fun onEditModeClicked()
        fun onViewModeClicked()
        fun onImageClicked(images: List<DiaryImage>)
    }

    interface OnDiaryEventListener {
        fun onGuideClicked()
        fun onAddImageClicked()
        fun onImageClicked(images: List<DiaryImage>)
        fun onContentChanged(content: String)
        fun onEnjoyClicked(enjoyRating: Int)
        fun onDeleteImage(image: DiaryImage)
        fun onEditModeClicked()
        fun onViewModeClicked()
        fun onDeleteDiary()
    }
}