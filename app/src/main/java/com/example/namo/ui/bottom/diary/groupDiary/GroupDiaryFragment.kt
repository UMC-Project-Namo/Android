package com.example.namo.ui.bottom.diary.groupDiary


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.namo.R
import com.example.namo.data.entity.diary.DiaryGroupEvent
import com.example.namo.data.entity.diary.GroupDiaryMember
import com.example.namo.databinding.FragmentDiaryGroupAddBinding
import com.example.namo.ui.bottom.diary.groupDiary.adapter.GroupMemberRVAdapter
import com.example.namo.ui.bottom.diary.groupDiary.adapter.GroupPlaceGalleryAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView


class GroupDiaryFragment : Fragment() {  // 그룹 다이어리 추가 화면

    private var _binding: FragmentDiaryGroupAddBinding? = null
    private val binding get() = _binding!!

    private var memberNames = ArrayList<GroupDiaryMember>()  // 그룹 다이어리 구성원
    private var placeEvent: ArrayList<DiaryGroupEvent> = arrayListOf()  // 장소, 정산 금액, 이미지
    private var imgList = mutableListOf<String>() // 장소별 이미지

    private lateinit var memberadapter: GroupMemberRVAdapter
    private lateinit var imgAdapter: GroupPlaceGalleryAdapter

    private lateinit var groupLayout: LinearLayout
    private lateinit var eventLayout: View
    private lateinit var placeText: EditText
    private lateinit var moneyText: TextView
    private lateinit var gallery: LinearLayout
    private lateinit var imgRv: RecyclerView

    private var layoutCount: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDiaryGroupAddBinding.inflate(inflater, container, false)

        hideBottomNavigation(true)

        addEventView()
        onClickListener()
        onRecyclerView()
        dummy()


        return binding.root
    }


    private fun addEventView() {
        binding.groudPlaceAddBtn.setOnClickListener {

            groupLayout = binding.groupLayout   // addView를 하기 위한 LinearLayout
            eventLayout = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_diary_group_event, null)
            groupLayout.addView(eventLayout)
            setupEventLayout(eventLayout)
            layoutCount++  // 장소 추가 버튼 클릭 수

            if (layoutCount == 0) {
                binding.groudPlaceAddBtn.visibility = View.VISIBLE
                binding.groupPlaceSaveBtn.visibility = View.GONE
            } else {
                binding.groudPlaceAddBtn.visibility = View.GONE
                binding.groupPlaceSaveBtn.visibility = View.VISIBLE
            }
        }

        binding.groupPlaceSaveBtn.setOnClickListener {
            saveEvent()
            binding.groudPlaceAddBtn.visibility = View.VISIBLE
            binding.groupPlaceSaveBtn.visibility = View.GONE
        }


    }


    private fun setupEventLayout(layout: View) {
        imgList.clear()
        placeText = layout.findViewById(R.id.item_place_name_tv)
        placeText.isSingleLine = true
        moneyText = layout.findViewById(R.id.item_place_money_tv)
        gallery = layout.findViewById(R.id.group_gallery_lv)
        imgRv = layout.findViewById(R.id.group_add_gallery_rv)
        val moneyImg = layout.findViewById<LinearLayout>(R.id.click_money_ly)

        gallery.setOnClickListener {
            getGallery()

            imgAdapter = GroupPlaceGalleryAdapter(requireContext())
            imgRv.adapter = imgAdapter
            imgRv.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )

        }

        moneyImg.setOnClickListener {
            GroupPayDialog(memberNames) { moneyValue ->
                moneyText.text = moneyValue.toString()
            }.show(parentFragmentManager, "show")
        }


    }


    private fun hasImagePermission(): Boolean { // 갤러리 권한 여부
        val writePermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val readPermission = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_EXTERNAL_STORAGE
        )

        return writePermission == PackageManager.PERMISSION_GRANTED && readPermission == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("IntentReset")
    private fun getGallery() {

        val gallery = eventLayout.findViewById<LinearLayout>(R.id.group_gallery_lv)
        val imgRv = eventLayout.findViewById<RecyclerView>(R.id.group_add_gallery_rv)

        if (hasImagePermission()) {  // 권한 있으면 갤러리 불러오기

            val intent = Intent()
            intent.type = "image/*"
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)   //다중 이미지 가져오기
            intent.action = Intent.ACTION_GET_CONTENT

            getImage.launch(intent)

            gallery.visibility = View.GONE
            imgRv.visibility = View.VISIBLE

        } else {  // 없으면 권한 받기
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                200
            )

            gallery.visibility = View.VISIBLE
        }
    }


    private val getImage = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->

        if (result.resultCode == Activity.RESULT_OK) {
            imgList.clear()
            if (result.data?.clipData != null) { // 사진 여러개 선택한 경우
                val count = result.data?.clipData!!.itemCount
                if (count > 3) {
                    Toast.makeText(requireContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    for (i in 0 until count) {
                        val imageUri = result.data?.clipData!!.getItemAt(i).uri
                        imgList.add(imageUri.toString())

                    }
                }
            }
        } else { // 단일 선택
            result.data?.data?.let {
                val imageUri: Uri? = result.data!!.data
                if (imageUri != null) {
                    imgList.add(imageUri.toString())

                }
            }
        }

        if (imgList.isEmpty()) gallery.visibility = View.VISIBLE
        else gallery.visibility = View.GONE

        imgAdapter.addItem(imgList)

    }

    private fun saveEvent() {

        val newImage = ArrayList(imgList)

        placeEvent.add(
            DiaryGroupEvent(
                placeText.text.toString(),
                moneyText.text.toString().toInt(),
                newImage
            )
        )
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun onClickListener() {

        binding.upArrow.setOnClickListener {// 화살표가 위쪽 방향일 때 리사이클러뷰 숨기기
            setMember(true)
        }

        binding.bottomArrow.setOnClickListener {
            setMember(false)
        }

        binding.groupAddBackIv.setOnClickListener { // 뒤로가기
            findNavController().popBackStack()
        }

        binding.groupPlaceSaveTv.setOnClickListener {// 저장하기
            findNavController().popBackStack()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun onRecyclerView() {

        binding.apply {

            // 멤버 이름 리사이클러뷰
            memberadapter = GroupMemberRVAdapter(memberNames)
            groupAddPeopleRv.adapter = memberadapter
            groupAddPeopleRv.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }
    }

    private fun setMember(isVisible: Boolean) {
        if (isVisible) {
            binding.groupAddPeopleRv.visibility = View.GONE
            binding.bottomArrow.visibility = View.VISIBLE
            binding.upArrow.visibility = View.GONE

        } else {
            binding.groupAddPeopleRv.visibility = View.VISIBLE
            binding.bottomArrow.visibility = View.GONE
            binding.upArrow.visibility = View.VISIBLE
        }
    }


    private fun hideBottomNavigation(bool: Boolean) {
        val bottomNavigationView: BottomNavigationView =
            requireActivity().findViewById(R.id.nav_bar)
        if (bool) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    private fun dummy() {
        memberNames.apply {
            add(GroupDiaryMember("코코아"))
            add(GroupDiaryMember("지니"))
            add(GroupDiaryMember("앨리"))
        }
    }

    override fun onResume() {
        super.onResume()
        hideBottomNavigation(true)
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        hideBottomNavigation(false)
    }
}