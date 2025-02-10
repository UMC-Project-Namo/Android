package com.mongmong.namo.presentation.ui.custom

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.mongmong.namo.databinding.DialogRegisterColorBinding
import com.mongmong.namo.presentation.enums.CategoryColor
import com.mongmong.namo.presentation.ui.home.category.adapter.CategoryPaletteRVAdapter
import androidx.recyclerview.widget.GridLayoutManager
import com.mongmong.namo.databinding.DialogProfileEditColorBinding


class ProfileEditColorDialog() : BottomSheetDialogFragment() {
    private val viewModel: ProfileEditViewModel by activityViewModels()
    private var _binding: DialogProfileEditColorBinding? = null
    private val binding get() = _binding!!

    private lateinit var paletteAdapter: CategoryPaletteRVAdapter
    private var selectedColor: CategoryColor? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogProfileEditColorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupButtons()
    }

    private fun setupRecyclerView() {
        val colorList = ArrayList(CategoryColor.getAllColors())
        val initialPosition = colorList.indexOf(viewModel.color.value).takeIf { it >= 0 } ?: -1  // 선택된 색이 없으면 -1

        paletteAdapter = CategoryPaletteRVAdapter(
            requireContext(),
            colorList,
            initialPosition
        )
        binding.categoryPaletteRv.apply {
            adapter = paletteAdapter
            layoutManager = GridLayoutManager(requireContext(), 5)
        }

        paletteAdapter.setColorClickListener(object : CategoryPaletteRVAdapter.MyItemClickListener {
            override fun onItemClick(position: Int, selectedColor: CategoryColor) {
                this@ProfileEditColorDialog.selectedColor = selectedColor
            }
        })
    }


    private fun setupButtons() {
        binding.profileEditColorCancelBtn.setOnClickListener {
            dismiss()
        }
        binding.profileEditColorSaveBtn.setOnClickListener {
            viewModel.clearHighlight("color")
            viewModel.setColor(selectedColor)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
