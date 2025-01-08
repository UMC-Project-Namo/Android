package com.mongmong.namo.presentation.ui.community.moim

import android.app.Activity
import android.content.Intent
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.databinding.FragmentMoimBinding
import com.mongmong.namo.domain.model.Moim
import com.mongmong.namo.presentation.config.BaseFragment
import com.mongmong.namo.presentation.ui.common.ConfirmDialog
import com.mongmong.namo.presentation.ui.common.ConfirmDialog.ConfirmDialogInterface
import com.mongmong.namo.presentation.ui.community.moim.diary.MoimDiaryDetailActivity
import com.mongmong.namo.presentation.ui.community.moim.adapter.MoimRVAdapter
import com.mongmong.namo.presentation.ui.community.moim.schedule.FriendInviteActivity
import com.mongmong.namo.presentation.ui.community.moim.schedule.MoimScheduleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MoimFragment : BaseFragment<FragmentMoimBinding>(R.layout.fragment_moim),
    ConfirmDialogInterface {

    private val viewModel: MoimViewModel by viewModels()

    private var moimAdapter = MoimRVAdapter()

    private val getResultText = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d("MoimFragment", "ActivityResult received")
        if (result.resultCode == Activity.RESULT_OK) {
            Log.d("MoimFragment", "Result OK")
            val isEdited = result.data?.getBooleanExtra(MOIM_EDIT_KEY, false)
            if (isEdited == true) viewModel.getMoim() // 변경 사항이 있다면 업데이트

            val createdMoimTitle = result.data?.getStringExtra(MOIM_CREATE_KEY)
            Log.d("MoimFragment", "isEdited: $isEdited, createdMoimTitle: $createdMoimTitle")
            if (createdMoimTitle != null) showCustomDialog(getString(R.string.dialog_moim_friend_invite_title, createdMoimTitle), R.string.dialog_moim_friend_invite_content, R.string.continuously, 0)
        } else {
            Log.d("MoimFragment", "Result not OK: ${result.resultCode}")
        }
    }

    override fun setup() {
        binding.viewModel = this@MoimFragment.viewModel
        setAdapter()
        initClickListeners()
        initObserve()
    }

    private fun initClickListeners() {
        // + 버튼
        binding.moimCreateFloatingBtn.setOnClickListener {
            // 모임 일정 생성 화면으로 이동
            requireActivity().startActivity(Intent(context, MoimScheduleActivity::class.java)
                .putExtra("moim", Moim())
            )
        }
    }

    private fun setAdapter() {
        binding.moimRv.apply {
            adapter = moimAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        }
        moimAdapter.setItemClickListener(object : MoimRVAdapter.MyItemClickListener {
            override fun onRecordButtonClick(position: Int) {
                startActivity(
                    Intent(context, MoimDiaryDetailActivity::class.java)
                        .putExtra("scheduleId", viewModel.moimPreviewList.value!![position].moimId)
                )
            }

            override fun onItemClick(position: Int) {
                // 모임 일정 편집 화면으로 이동
                val intent = Intent(context, MoimScheduleActivity::class.java)
                    .putExtra("moimScheduleId", viewModel.moimPreviewList.value!![position].moimId)
                getResultText.launch(intent)
            }
        })
    }

    private fun showCustomDialog(title: String, content: Int, buttonText: Int, id: Int) {
        Log.d("MoimFragment", "showCustomDialog()")
        val dialog = ConfirmDialog(this, title, getString(content), getString(buttonText), id)
        dialog.isCancelable = false
        activity?.let { dialog.show(it.supportFragmentManager, "ConfirmDialog") }
    }

    private fun initObserve() {
        viewModel.moimPreviewList.observe(viewLifecycleOwner) {
            if (it.isNotEmpty()) {
                Log.d("MoimFragment", "moimPreviewListObserve\n$it")
                moimAdapter.addMoim(it)
            }
        }
    }

    override fun onClickYesButton(id: Int) {
        // 친구 초대 화면으로 이동
        startActivity(Intent(requireActivity(), FriendInviteActivity::class.java))
    }

    companion object {
        const val MOIM_EDIT_KEY = "moim_edit_key"
        const val MOIM_CREATE_KEY = "moim_create_key"
    }
}