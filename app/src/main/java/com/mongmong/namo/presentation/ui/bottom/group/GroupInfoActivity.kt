package com.mongmong.namo.presentation.ui.bottom.group

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.presentation.config.BaseResponse
import com.mongmong.namo.data.remote.group.DeleteMoimMemberView
import com.mongmong.namo.domain.model.Group
import com.mongmong.namo.data.remote.group.MoimService
import com.mongmong.namo.domain.model.ParticipateMoimResponse
import com.mongmong.namo.domain.model.UpdateMoimNameBody
import com.mongmong.namo.databinding.ActivityGroupInfoBinding
import com.mongmong.namo.presentation.ui.bottom.group.adapter.GroupInfoMemberRVAdapter
import com.mongmong.namo.presentation.utils.ConfirmDialog
import com.mongmong.namo.presentation.utils.ConfirmDialogInterface

class GroupInfoActivity : AppCompatActivity(), ConfirmDialogInterface, DeleteMoimMemberView {

    private lateinit var binding : ActivityGroupInfoBinding
    private lateinit var group : Group

    private lateinit var groupInfoMemberRVAdapter : GroupInfoMemberRVAdapter

    private var groupName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityGroupInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d("TEST_COLOR", R.color.palette1.toString())
        Log.d("TEST_COLOR", R.color.palette2.toString())
        Log.d("TEST_COLOR", R.color.palette3.toString())

        group = intent.getSerializableExtra("group") as Group
        setAdapter()
        clickListener()

        setGroupInfo()
        setListenerToEditText()
    }

    private fun setAdapter() {

        binding.groupInfoMemberRv.layoutManager = GridLayoutManager(this, 2)
        groupInfoMemberRVAdapter = GroupInfoMemberRVAdapter(group.moimUsers)
        binding.groupInfoMemberRv.adapter = groupInfoMemberRVAdapter
    }

    private fun clickListener() {
        binding.groupInfoCloseBtn.setOnClickListener {
            finish()
        }

        binding.groupInfoSaveBtn.setOnClickListener {
            val editGroupName = binding.groupInfoGroupNameContentEt.text.toString()
            // 그룹명이 변경되었으면
            if (groupName != editGroupName) {
                val moimService = MoimService()
                moimService.setDeleteMoimMemberView(this)

                moimService.updateMoimName(UpdateMoimNameBody(group.groupId, editGroupName))
            }
        }

        binding.groupInfoCodeCopyIv.setOnClickListener {
            copyTextToClipboard(binding.groupInfoCodeTv.text.toString())
            Toast.makeText(this, "그룹 코드가 복사되었습니다.", Toast.LENGTH_SHORT).show()
        }

        binding.groupInfoLeaveBtn.setOnClickListener {
            showLeaveDialog()
        }
    }

    private fun showLeaveDialog() {
        // 탈퇴 확인 다이얼로그
        val title = "정말 모임에서 탈퇴하시겠어요?"
        val content = "탈퇴하더라도\n이전 모임 일정은 사라지지 않습니다."

        val dialog = ConfirmDialog(this@GroupInfoActivity, title, content, "삭제", 0)
        dialog.isCancelable = false
        dialog.show(this.supportFragmentManager, "ConfirmDialog")
    }

    private fun setGroupInfo() {
        groupName = group.groupName
        binding.groupInfoGroupNameContentEt.setText(group.groupName)
        binding.groupInfoMemberHeaderContentTv.text = group.moimUsers.size.toString()

        binding.groupInfoCodeTv.isSelected = true
        binding.groupInfoCodeTv.text = group.groupCode
    }

    private fun setListenerToEditText() {
        binding.groupInfoGroupNameContentEt.setOnKeyListener { view, keyCode, event ->
            // Enter Key Action
            if (event.action == KeyEvent.ACTION_DOWN
                && keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                // 키보드 내리기
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.groupInfoGroupNameContentEt.windowToken, 0)
                true
            }

            false
        }
    }

    private fun copyTextToClipboard(text : String) {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText("text", text)

        clipboardManager.setPrimaryClip(clipData)
    }

    override fun onUpdateMoimNameSuccess(response: ParticipateMoimResponse) {
        Log.d("GroupInfoAct", "onUpdateMoimNameSuccess")
        Toast.makeText(this, "모임 이름이 변경되었습니다.", Toast.LENGTH_SHORT).show()
        val resultIntent = Intent(this, GroupCalendarActivity::class.java).apply {
            putExtra("groupName", binding.groupInfoGroupNameContentEt.text.toString()) // 사용자가 이름을 바꿨음을 알려줌
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onUpdateMoimNameFailure(message: String) {
        Log.d("GroupInfoAct", "onUpdateMoimNameFailure, $message")
        finish()
    }

    override fun onDeleteMoimMemberSuccess(response: BaseResponse) {
        Log.d("GroupInfoAct", "onDeleteMoimMemberSuccess")
        Toast.makeText(this, "${group.groupName} 모임에서 탈퇴하였습니다.", Toast.LENGTH_SHORT).show()

        val resultIntent = Intent(this, GroupCalendarActivity::class.java).apply {
            putExtra("leave", true) // 사용자가 탈퇴했음을 알려줌
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun onDeleteMoimMemberFailure(message: String) {
        Log.d("GroupInfoAct", "onDeleteMoimMemberFailure")
    }

    override fun onClickYesButton(id: Int) {
        // 탈퇴 진행
        val moimService = MoimService()
        moimService.setDeleteMoimMemberView(this)

        moimService.deleteMoimMember(group.groupId)
    }
}