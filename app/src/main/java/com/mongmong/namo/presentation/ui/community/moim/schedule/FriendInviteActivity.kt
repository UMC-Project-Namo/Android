package com.mongmong.namo.presentation.ui.community.moim.schedule

import android.text.Html
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ActivityFriendInviteBinding
import com.mongmong.namo.presentation.config.BaseActivity
import com.mongmong.namo.presentation.ui.community.moim.schedule.adapter.FriendInviteRVAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendInviteActivity : BaseActivity<ActivityFriendInviteBinding>(R.layout.activity_friend_invite) {

    private val viewModel: FriendInviteViewModel by viewModels()

    private lateinit var friendAdapter: FriendInviteRVAdapter

    override fun setup() {
        binding.viewModel = viewModel

        initClickListeners()
        initObserve()
    }

    private fun initClickListeners() {
        // 뒤로가기
        binding.friendInviteBackIv.setOnClickListener {
            finish()
        }
    }

    // 초대한 친구 현황 표시용
    private fun setFriendSelectedNum() {
        // {초대할 친구 수} / {전체 친구 수}
        binding.friendInviteSelectedNumTv.text = Html.fromHtml(String.format(resources.getString(R.string.moim_schedule_friend_invite_selected_num), viewModel.friendToInviteList.value?.size, viewModel.friendList.value?.size))
    }

    private fun setAdapter() {
        friendAdapter = FriendInviteRVAdapter()
        binding.friendInviteListRv.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(context)
        }

        friendAdapter.setItemClickListener(object : FriendInviteRVAdapter.MyItemClickListener {
            override fun onInviteButtonClick(position: Int) {
                //TODO: 친구 초대 진행
            }

            override fun onItemClick(position: Int) {
                //TODO: 친구 정보 화면으로 이동?
            }
        })
    }

    private fun initObserve() {
        viewModel.friendList.observe(this) {
            if (it.isNotEmpty()) {
                setAdapter()
                friendAdapter.addFriend(it)
                setFriendSelectedNum()
            }
        }
    }
}