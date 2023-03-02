package com.example.namo.ui.bottom.diary

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.namo.R
import com.example.namo.data.NamoDatabase
import com.example.namo.data.entity.diary.Diary
import com.example.namo.databinding.FragmentDiaryAddBinding
import com.example.namo.ui.bottom.diary.adapter.GalleryListAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.File
import java.lang.Boolean.TRUE
import java.text.SimpleDateFormat

class DiaryAddFragment : Fragment() {

    private var _binding: FragmentDiaryAddBinding? = null
    private val binding get() = _binding!!

    private lateinit var db:NamoDatabase
    private lateinit var diary:Diary
    private lateinit var galleryAdapter: GalleryListAdapter

    private var imgList= arrayListOf<String>()
    private var longDate:Long = 0
    private var title:String=""
    private var place:String=""
    private var category:Int=0
    private var scheduleIdx:Int=0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDiaryAddBinding.inflate(inflater, container, false)

        hideBottomNavigation(true)

        db=NamoDatabase.getInstance(requireContext())
        galleryAdapter= GalleryListAdapter(requireContext(),imgList)
        scheduleIdx= arguments?.getInt("scheduleIdx")!!
        bind()
        charCnt()

        return binding.root
    }

    @SuppressLint("SimpleDateFormat")
    private fun bind(){
        longDate= arguments?.getLong("date")!!
        title = arguments?.getString("title").toString()
        place=arguments?.getString("place").toString()
        category= arguments?.getInt("category")!!

        binding.apply {

            val formatDate=SimpleDateFormat("yyyy.MM.dd (EE)").format(longDate)
            diaryTodayDayTv.text=SimpleDateFormat("EE").format(longDate)
            diaryTodayNumTv.text=SimpleDateFormat("dd").format(longDate)

            diaryTitleTv.text=title
            diaryInputPlaceTv.text=place
            context?.resources?.let { itemDiaryCategoryColorIv.background.setTint(ContextCompat.getColor(requireContext(),category)) }
            diaryInputDateTv.text= formatDate
            diaryBackIv.setOnClickListener {
                findNavController().popBackStack()
                hideBottomNavigation(false)
            }
            diaryEditTv.setOnClickListener {

                if (diaryEditTv.text.toString().isEmpty()) {
                    Toast.makeText(requireContext(), "메모를 입력해주세용", Toast.LENGTH_SHORT).show()
                } else {
                    insertData()
                    view?.findNavController()?.navigate(R.id.homeFragment)
                    hideBottomNavigation(false)
                }
            }

            diaryGalleryClickIv.setOnClickListener {
                getPermission()
            }
        }
    }

    private fun insertData(){
        Thread{
            diary=Diary(scheduleIdx, binding.diaryContentsEt.text.toString(),imgList)
            db.diaryDao.insertDiary(diary)
            db.diaryDao.updateHasDiary(TRUE,scheduleIdx)
        }.start()  }


    @SuppressLint("IntentReset")
    private fun getPermission(){

        val writePermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
        val readPermission = ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)

        if (writePermission == PackageManager.PERMISSION_DENIED || readPermission == PackageManager.PERMISSION_DENIED) {
            // 권한 없어서 요청
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE),200)
        } else {
            // 권한 있음
            val intent = Intent()
            //Intent(Intent.ACTION_PICK)
            //Intent(Intent.ACTION_GET_CONTENT) 실제 기기로 해보기
            intent.data = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)   //다중 이미지 가져오기
            intent.action = Intent.ACTION_GET_CONTENT

            getImage.launch(intent)
        }
    }

        private val getImage=registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){  result->

            if ( result.resultCode == RESULT_OK) {

            if (result.data?.clipData != null) { // 사진 여러개 선택한 경우
                val count = result.data?.clipData!!.itemCount
                if (count > 3) {
                    Toast.makeText(requireContext(), "사진은 3장까지 선택 가능합니다.", Toast.LENGTH_SHORT).show()
                    return@registerForActivityResult
                }
                else {
                    for (i in 0 until count) {
                        val imageUri = result.data?.clipData!!.getItemAt(i).uri
                        val file = File(absolutelyPath(imageUri, requireContext()))  // retrofit2 에 사용

                        imgList.add(imageUri.toString())
                    }
                }
            }
        } else { // 단일 선택
            result.data?.data?.let {
                val imageUri : Uri? = result.data!!.data
                if (imageUri != null) {
                    val  file = File(absolutelyPath(imageUri, requireContext()))

                    imgList.add(imageUri.toString())
                }
            }
        }
        binding.diaryGalleryClickIv.visibility=View.GONE
        binding.diaryGallerySavedRy.visibility=View.VISIBLE
        onRecyclerView()
}

    // 이미지 절대 경로 변환
    @SuppressLint("Recycle")
    private fun absolutelyPath(path: Uri, context: Context): String {
        val proj: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val c: Cursor? = context.contentResolver.query(path, proj, null, null, null)
        val index = c?.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
        c?.moveToFirst()
        val result = c?.getString(index!!)

        return result!!
    }

    private fun onRecyclerView() {

    val galleryViewRVAdapter = galleryAdapter
    binding.diaryGallerySavedRy.adapter = galleryViewRVAdapter
    binding.diaryGallerySavedRy.layoutManager =
        LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
    }

    private fun charCnt(){
        with(binding) {
            diaryContentsEt.addTextChangedListener(object : TextWatcher {
                var maxText=""
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                    maxText=s.toString()
                }

                @SuppressLint("SetTextI18n")
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(diaryContentsEt.length() > 200){
                        Toast.makeText(requireContext(),"최대 200자까지 입력 가능합니다",
                        Toast.LENGTH_SHORT).show()

                    diaryContentsEt.setText(maxText)
                    diaryContentsEt.setSelection(diaryContentsEt.length())
                        if (s != null) {
                            textNumTv.text="${s.length} / 200"
                        }
                    } else { textNumTv.text="${s.toString().length} / 200"}
                }
                override fun afterTextChanged(s: Editable?) {
                }

            })
        }
    }

        private fun hideBottomNavigation( bool : Boolean){
        val bottomNavigationView : BottomNavigationView = requireActivity().findViewById(R.id.nav_bar)
        if(bool) {
            bottomNavigationView.visibility = View.GONE
        } else {
            bottomNavigationView.visibility = View.VISIBLE
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        _binding = null
        hideBottomNavigation(false)
    }
}