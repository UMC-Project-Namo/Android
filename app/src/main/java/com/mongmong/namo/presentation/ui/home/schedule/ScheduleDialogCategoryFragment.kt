package com.mongmong.namo.presentation.ui.home.schedule

import android.content.Intent
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.presentation.ui.home.schedule.adapter.DialogCategoryRVAdapter
import com.mongmong.namo.domain.model.Category
import com.mongmong.namo.data.local.entity.home.Schedule
import com.mongmong.namo.databinding.FragmentScheduleDialogCategoryBinding
import com.mongmong.namo.presentation.config.BaseFragment
import com.mongmong.namo.presentation.ui.category.CategoryActivity
import com.mongmong.namo.presentation.ui.category.CategoryViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ScheduleDialogCategoryFragment
    : BaseFragment<FragmentScheduleDialogCategoryBinding>(R.layout.fragment_schedule_dialog_category) {
    private val args: ScheduleDialogCategoryFragmentArgs by navArgs()
    private var schedule: Schedule = Schedule()

    private lateinit var categoryRVAdapter: DialogCategoryRVAdapter

    private val viewModel: CategoryViewModel by viewModels()

    override fun setup() {
        binding.viewModel = viewModel

        schedule = args.schedule

        onClickCategoryEdit()
        initObserve()
    }

    override fun onResume() {
        super.onResume()
        getCategoryList()
    }

    private fun onClickCategoryEdit()  {
        binding.dialogScheduleCategoryEditCv.setOnClickListener {
            Log.d("DialogCategoryFrag", "categoryEditCV 클릭")
            startActivity(Intent(activity, CategoryActivity::class.java))
        }
    }

    private fun setAdapter(categoryList: List<Category>) {
        categoryRVAdapter = DialogCategoryRVAdapter(categoryList)
        categoryRVAdapter.setSelectedId(schedule.categoryInfo.categoryId)
        categoryRVAdapter.setMyItemClickListener(object: DialogCategoryRVAdapter.MyItemClickListener {
            // 아이템 클릭
            override fun onSendId(category: Category) {
                // 카테고리 세팅
                schedule.categoryInfo.categoryId = category.categoryId
                Log.d("TEST_CATEGORY", "In category : ${schedule.categoryInfo.categoryId}")
                Log.d("TEST_CATEGORY", "In category Result: ${schedule}")
                val action = ScheduleDialogCategoryFragmentDirections.actionScheduleDialogCategoryFragmentToScheduleDialogBasicFragment(schedule)
                findNavController().navigate(action)
            }
        })
        binding.dialogScheduleCategoryRv.apply {
            adapter = categoryRVAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun initObserve() {
        viewModel.categoryList.observe(viewLifecycleOwner) {
            if (!it.isNullOrEmpty()) {
                setAdapter(it)
            }
        }
    }

    /** 카테고리 조회 */
    private fun getCategoryList() {
        lifecycleScope.launch{
            viewModel.getCategories()
        }
    }
}