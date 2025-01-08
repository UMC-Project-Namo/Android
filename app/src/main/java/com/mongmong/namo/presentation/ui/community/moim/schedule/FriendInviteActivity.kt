package com.mongmong.namo.presentation.ui.community.moim.schedule

import android.text.Html
import android.util.Log
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ActivityFriendInviteBinding
import com.mongmong.namo.presentation.config.BaseActivity
import com.mongmong.namo.presentation.ui.common.ConfirmDialog
import com.mongmong.namo.presentation.ui.community.moim.schedule.adapter.FriendInvitePreparatoryRVAdapter
import com.mongmong.namo.presentation.ui.community.moim.schedule.adapter.FriendInviteRVAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendInviteActivity : BaseActivity<ActivityFriendInviteBinding>(R.layout.activity_friend_invite),
    ConfirmDialog.ConfirmDialogInterface {

    private val viewModel: FriendInviteViewModel by viewModels()

    private lateinit var friendToInviteAdapter: FriendInvitePreparatoryRVAdapter
    private lateinit var allFriendAdapter: FriendInviteRVAdapter

    override fun setup() {
        binding.viewModel = viewModel

        intent.getLongExtra(MOIM_INVITE_KEY, 0L).let { moimScheduleId ->
            viewModel.moimScheduleId = moimScheduleId
        }

        initClickListeners()
        initObserve()
    }

    private fun initClickListeners() {
        // 뒤로가기
        binding.friendInviteBackIv.setOnClickListener {
            finish()
        }

        // 전체 선택 취소
        binding.friendInviteResetBtn.setOnClickListener {
            viewModel.resetAllSelectedFriend()
            allFriendAdapter.resetAllSelectedFriend()
        }

        // 초대하기 버튼
        binding.friendInviteBtn.setOnClickListener {
            showCustomDialog(R.string.dialog_moim_schedule_invite_complete_title, R.string.dialog_moim_schedule_invite_complete_content, 0)
        }
    }

    // 초대한 친구 현황 표시용
    private fun setFriendSelectedNum() {
        // {초대할 친구 수} / {전체 친구 수}
        binding.friendInviteSelectedNumTv.text = Html.fromHtml(String.format(resources.getString(R.string.moim_schedule_friend_invite_selected_num), viewModel.friendToInviteList.value?.size, viewModel.friendList.value?.size))
    }

    // 초대할 친구 어댑터 설정
    private fun setFriendInvitePreparatoryAdapter() {
        friendToInviteAdapter = FriendInvitePreparatoryRVAdapter()
        binding.friendInvitePreparatoryRv.apply {
            adapter = friendToInviteAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        friendToInviteAdapter.setItemClickListener(object : FriendInvitePreparatoryRVAdapter.MyItemClickListener {
            override fun onDeleteBtnClick(position: Int) { // 초대할 친구 해제
                val friendToDelete = viewModel.friendToInviteList.value!![position]
                // 초대할 친구 목록에서 삭제
                viewModel.updateSelectedFriend(false, friendToDelete)
                // 모든 친구 목록에서도 체크 해제
                allFriendAdapter.uninvitedFriend(friendToDelete)
            }
        })
    }

    // 모든 친구 어댑터 설정
    private fun setAllFriendAdapter() {
        allFriendAdapter = FriendInviteRVAdapter()
        binding.friendInviteListRv.apply {
            adapter = allFriendAdapter
            layoutManager = LinearLayoutManager(context)
        }

        allFriendAdapter.setItemClickListener(object : FriendInviteRVAdapter.MyItemClickListener {
            override fun onInviteButtonClick(isSelected: Boolean, position: Int) {
                // 초대할 친구 목록에 추가
                Log.d("FriendInviteACT", "onInviteButtonClick - isSelected: $isSelected, position: $position")
                viewModel.updateSelectedFriend(isSelected, viewModel.friendList.value!![position])
            }

            override fun onItemClick(position: Int) {
                //
            }
        })
    }

    private fun initObserve() {
        // 초대할 친구
        viewModel.friendToInviteList.observe(this) {
            Log.d("FriendInviteACT", "friendToInviteList: $it")
            if (it.isNotEmpty()) {
                setFriendInvitePreparatoryAdapter()
                friendToInviteAdapter.addFriend(it)
                setFriendSelectedNum()
            }
        }

        // 모든 친구
        viewModel.friendList.observe(this) {
            if (it.isNotEmpty()) {
                setAllFriendAdapter()
            }
            allFriendAdapter.addFriend(it)
            setFriendSelectedNum()
        }

        // API 호출 성공 여부
        viewModel.isSuccess.observe(this) { isSuccess ->
            if (isSuccess) {
                finish()
            }
        }
    }

    private fun showCustomDialog(title: Int, content: Int, id: Int) {
        val dialog = ConfirmDialog(this@FriendInviteActivity, getString(title), getString(content), null, id)
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "ConfirmDialog")
    }

    override fun onClickYesButton(id: Int) {
        viewModel.inviteMoimParticipants() // 참석자 초대 진행
    }

    companion object {
        const val MOIM_INVITE_KEY = "moim_invite_key"
    }
}