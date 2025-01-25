package com.mongmong.namo.presentation.ui.home.category

import android.util.Log
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.databinding.FragmentCategoryDetailBinding
import com.mongmong.namo.domain.model.CategoryModel
import com.mongmong.namo.presentation.config.BaseFragment
import com.mongmong.namo.presentation.enums.CategoryColor
import com.mongmong.namo.presentation.enums.SuccessType
import com.mongmong.namo.presentation.ui.home.category.adapter.CategoryPaletteRVAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryDetailFragment
    : BaseFragment<FragmentCategoryDetailBinding>(R.layout.fragment_category_detail) {

    private lateinit var paletteAdapter: CategoryPaletteRVAdapter

    private val viewModel : CategoryViewModel by viewModels()

    override fun setup() {
        binding.viewModel = this@CategoryDetailFragment.viewModel

        val args = this.arguments?.let { CategoryDetailFragmentArgs.fromBundle(it).category }

        binding.isEditMode = (args != null)

        if (args != null) { // 카테고리 편집
            viewModel.setCategory(args)
        } else { // 카테고리 생성
            viewModel.setCategory(CategoryModel(isShare = true))
        }
        switchToggle()
        initClickListeners()

        initObservers()
    }

    private fun initClickListeners() {
        // 뒤로가기
        binding.categoryDetailBackIv.setOnClickListener {
            findNavController().popBackStack()
        }

        // 저장하기
        binding.categoryDetailSaveTv.setOnClickListener {
            if (!this@CategoryDetailFragment.viewModel.isValidInput()) {
                Toast.makeText(requireContext(), "카테고리를 입력해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // 수정 모드 -> 카테고리 update
            if (viewModel.isEditMode) {
                updateData()
            }
            // 생성 모드 -> 카테고리 insert
            else {
                insertData()
            }
        }
    }

    private fun initObservers() {
        viewModel.color.observe(requireActivity()) { color ->
            Log.e("CategoryDetailFrag", "initObservers - color: $color")
            if (color == null) {
                initPaletteColorRv(null)
            } else {
                initPaletteColorRv(color)
            }
        }

        viewModel.isComplete.observe(requireActivity()) { isComplete ->
            // 추가 작업이 완료된 후 뒤로가기
            if (isComplete) {
                viewModel.completeState.observe(viewLifecycleOwner) { state ->
                    when(state) {
                        SuccessType.ADD -> Toast.makeText(requireContext(), "카테고리가 생성되었습니다.", Toast.LENGTH_SHORT).show()
                        SuccessType.EDIT -> Toast.makeText(requireContext(), "카테고리가 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        else -> {}
                    }
                }
                findNavController().popBackStack() // 뒤로가기
            }
        }
    }

    /** 카테고리 추가 */
    private fun insertData() {
        // 새 카테고리 등록
        viewModel.addCategory()
    }

    private fun updateData() {
        // 카테고리 편집
        viewModel.editCategory()
    }

    private fun initPaletteColorRv(initCategory: CategoryColor?) {
        val selectedItemPosition = CategoryColor.getAllColors().indexOf(initCategory)
        // 어댑터 연결
        paletteAdapter = CategoryPaletteRVAdapter(requireContext(), CategoryColor.getAllColors(), selectedItemPosition)
        binding.categoryPaletteRv.apply {
            adapter = paletteAdapter
            layoutManager = GridLayoutManager(context, 5)
        }
        // 아이템 클릭
        paletteAdapter.setColorClickListener(object: CategoryPaletteRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, selectedColor: CategoryColor) {
                // 색상값 세팅
                viewModel.updateCategoryColor(selectedColor)
            }
        })
    }

    private fun switchToggle() {
        val isShare = viewModel.category.value!!.isShare
        binding.categoryShareToggleIv.apply {
            // 첫 진입 시 토글 이미지 세팅
            isChecked = isShare
            // 토글 클릭 시 이미지 세팅
            setOnClickListener {
                (it as SwitchCompat).isChecked = !isShare
                viewModel.updateIsShare(!isShare)
            }
        }
    }
}