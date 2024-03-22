package com.mongmong.namo.presentation.ui.bottom.home.category

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.mongmong.namo.R
import com.mongmong.namo.data.local.NamoDatabase
import com.mongmong.namo.databinding.FragmentCategorySettingBinding
import com.mongmong.namo.presentation.ui.bottom.home.category.adapter.SetCategoryRVAdapter
import com.mongmong.namo.data.local.entity.home.Category
import com.mongmong.namo.data.remote.category.CategorySettingView
import com.mongmong.namo.domain.model.GetCategoryResponse
import com.google.gson.Gson

class CategorySettingFragment: Fragment(), CategorySettingView {

    lateinit var binding: FragmentCategorySettingBinding //플로팅 카테고리 설정 화면

    private lateinit var categoryRVAdapter: SetCategoryRVAdapter

    private lateinit var db: NamoDatabase

    private var categoryList : List<Category> = arrayListOf() // arrayListOf<Category>()

    private var gson: Gson = Gson()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?

    ): View {
        binding = FragmentCategorySettingBinding.inflate(inflater, container, false)

        db = NamoDatabase.getInstance(requireContext())

        // 카테고리가 아무것도 없으면 기본 카테고리 2개 생성 (일정, 모임)
        setInitialCategory()

        onClickEvent()

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        categoryRVAdapter = SetCategoryRVAdapter(requireContext(), categoryList)
        getCategoryList()
    }

    private fun onClickEvent() {
        // 닫힘 버튼 누르면 종료
        binding.categoryCloseTv.setOnClickListener {
            activity?.finish()
        }

        // 저장 버튼
        binding.categorySaveTv.setOnClickListener {
            activity?.finish()
        }

        //팔레트 설정
        binding.categoryCalendarPaletteSetting.setOnClickListener {

        }

        onClickCategoryAddBtn()
    }

    private fun onClickCategoryAddBtn() {
        binding.categoryAddBtn.setOnClickListener { // 새 카테고리
            (context as CategoryActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.category_frm, CategoryDetailFragment(false))
                .commitAllowingStateLoss()
        }
    }

    private fun getCategoryList() {
        val rv = binding.categoryCalendarRv

        // roomDB
        val r = Runnable {
            try {
                // 활성화 상태인 리스트만 보여줌
                categoryList = db.categoryDao.getActiveCategoryList(true)

                categoryRVAdapter.notifyItemChanged(categoryList.size)
                categoryRVAdapter = SetCategoryRVAdapter(requireContext(), categoryList)
                categoryRVAdapter.setCategoryClickListener(object: SetCategoryRVAdapter.MyItemClickListener {
                    // 아이템 클릭
                    override fun onItemClick(category: Category, position: Int) {
                        Log.d("Category-Set-FRAG", "카테고리 아이템을 클릭했음")
                        Log.e("SET-CATEGORY", "$category , $position")

                        // 데이터 저장
                        saveClickedData(category)
                        categoryRVAdapter.notifyItemChanged(position)

                        // 편집 화면으로 이동
                        startActivity(Intent(requireActivity(), CategoryEditActivity()::class.java))
                    }
                })
                requireActivity().runOnUiThread {
                    rv.adapter = categoryRVAdapter
                    rv.layoutManager = GridLayoutManager(context, 2)
                }
//                Log.d("CategorySettingFrag", "categoryDao: ${db.categoryDao.getCategoryList()}")
            } catch (e: Exception) {
                Log.d("category", "Error - $e")
            }
        }

        val thread = Thread(r)
        thread.start()

        // 서버 통신
//        CategorySettingService(this@CategorySettingFragment).tryGetAllCategory()
    }

    private fun saveClickedData(dataSet: Category) {
        // 클릭한 카테고리 데이터를 편집 화면으로 넘기기 위함
        val spf = requireActivity().getSharedPreferences(CATEGORY_KEY_PREFS, Context.MODE_PRIVATE)
        val editor = spf.edit()
        val gson = Gson()
        val json = gson.toJson(dataSet) // 카테고리 데이터 변환
//        Log.d("Category", "categoryJson: $json")

        // spf에 저장
        editor
            .putString(CATEGORY_KEY_DATA, json)
            .putLong(CATEGORY_KEY_IDX, dataSet.categoryId)
            .putLong(CATEGORY_KEY_SERVER_IDX, dataSet.serverId)
            .apply()

        Log.d("debug", "Category Data saved")
    }

    private fun setInitialCategory() {
        // 리스트에 아무런 카테고리가 없으면 기본 카테고리 설정. 근데 딜레이가 좀 있음
        Thread {
            if (db.categoryDao.getCategoryList().isEmpty()) {
                db.categoryDao.insertCategory(Category(0, "일정", R.color.schedule, true))
                db.categoryDao.insertCategory(Category(0, "그룹", R.color.schedule_group, true))
            }
        }.start()
    }

    companion object {
        const val CATEGORY_KEY_PREFS = "category"
        const val CATEGORY_KEY_DATA = "category_data"
        const val CATEGORY_KEY_IDX = "categoryId"
        const val CATEGORY_KEY_SERVER_IDX = "serverId"
    }

    override fun onGetAllCategorySuccess(response: GetCategoryResponse) {
        Log.d("CategorySettingFrag", "onGetAllCategorySuccess")
    }

    override fun onGetAllCategoryFailure(message: String) {
        Log.d("CategorySettingFrag", "onGetAllCategoryFailure")
    }
}