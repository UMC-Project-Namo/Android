package com.mongmong.namo.presentation.ui.community.alert

import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.databinding.FragmentFriendAlertBinding
import com.mongmong.namo.presentation.config.BaseFragment
import com.mongmong.namo.presentation.ui.community.alert.adapter.FriendAlertRVAdapter
import com.mongmong.namo.presentation.ui.community.friend.FriendInfoDialog
import com.mongmong.namo.presentation.ui.community.friend.OnFriendInfoChangedListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FriendAlertFragment : BaseFragment<FragmentFriendAlertBinding>(R.layout.fragment_friend_alert), OnFriendInfoChangedListener {

    private val viewModel: AlertViewModel by activityViewModels()

    private lateinit var friendAdapter: FriendAlertRVAdapter

    override fun setup() {
        binding.viewModel = this@FriendAlertFragment.viewModel

        setAdapter()
        initObserve()
    }

    private fun setAdapter() {
        friendAdapter = FriendAlertRVAdapter()
        binding.friendAlertListRv.apply {
            adapter = friendAdapter
            layoutManager = LinearLayoutManager(context)
        }
        friendAdapter.setItemClickListener(object : FriendAlertRVAdapter.MyItemClickListener {
            override fun onFriendInfoClick(position: Int) {
                // 친구 정보 화면으로 이동
                FriendInfoDialog(null, viewModel.friendRequestList.value!![position], true, this@FriendAlertFragment).show(parentFragmentManager, "FriendInfoDialog")
            }

            override fun onAcceptBtnClick(position: Int) {
                // 친구 요청 수락
                viewModel.acceptFriendRequest(viewModel.friendRequestList.value!![position].friendRequestId)
            }

            override fun onDenyBtnClick(position: Int) {
                // 친구 요청 거절
                viewModel.denyFriendRequest(viewModel.friendRequestList.value!![position].friendRequestId)
            }
        })
    }

    private fun initObserve() {
        viewModel.friendRequestList.observe(viewLifecycleOwner) { friendRequestList ->
            if (friendRequestList.isNotEmpty()) {
                friendAdapter.addRequest(friendRequestList)
            }
        }
    }

    override fun onFriendInfoChanged() {
        viewModel.getFriendRequests() // 친구 정보 업데이트
    }
}