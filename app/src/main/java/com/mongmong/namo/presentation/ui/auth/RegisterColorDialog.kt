package com.mongmong.namo.presentation.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mongmong.namo.databinding.DialogRegisterColorBinding
import com.mongmong.namo.presentation.enums.CategoryColor
import com.mongmong.namo.presentation.ui.home.category.adapter.CategoryPaletteRVAdapter
import androidx.recyclerview.widget.GridLayoutManager


class RegisterColorDialog(
    private val initialColor: CategoryColor?,
    private val onColorSelected: (CategoryColor?) -> Unit // nullable 허용
) : BottomSheetDialogFragment() {

    private var _binding: DialogRegisterColorBinding? = null
    private val binding get() = _binding!!

    private lateinit var paletteAdapter: CategoryPaletteRVAdapter
    private var selectedColor: CategoryColor? = initialColor // 초기값을 null 허용

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRegisterColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        val colorList = ArrayList(CategoryColor.getAllColors())
        val initialPosition = colorList.indexOf(initialColor).takeIf { it >= 0 } ?: 0

        paletteAdapter = CategoryPaletteRVAdapter(
            requireContext(),
            colorList,
            initialColor ?: CategoryColor.NAMO_ORANGE, // 기본값이 필요하면 사용
            initialPosition
        )
        binding.categoryPaletteRv.apply {
            adapter = paletteAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
        }

        paletteAdapter.setColorClickListener(object : CategoryPaletteRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, selectedColor: CategoryColor) {
                this@RegisterColorDialog.selectedColor = selectedColor
            }
        })
    }

    private fun setupButtons() {
        binding.registerColorCancelBtn.setOnClickListener {
            dismiss()
        }
        binding.registerColorSaveBtn.setOnClickListener {
            onColorSelected(selectedColor) // 선택하지 않았다면 null 전달
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
