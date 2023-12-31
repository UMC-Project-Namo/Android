package com.mongmong.namo.ui.bottom.group

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import com.mongmong.namo.MainActivity
import com.mongmong.namo.MainActivity.Companion.ORIGIN_ACTIVITY_INTENT_KEY
import com.mongmong.namo.MainActivity.Companion.PLACE_NAME_INTENT_KEY
import com.mongmong.namo.MainActivity.Companion.PLACE_X_INTENT_KEY
import com.mongmong.namo.MainActivity.Companion.PLACE_Y_INTENT_KEY
import com.mongmong.namo.R
import com.mongmong.namo.databinding.ActivityGroupMapBinding
import com.mongmong.namo.databinding.ActivityMapBinding
import com.mongmong.namo.ui.bottom.group.GroupScheduleActivity
import com.mongmong.namo.ui.bottom.home.schedule.ScheduleActivity
import com.mongmong.namo.ui.bottom.home.schedule.map.adapter.MapRVAdapter
import com.mongmong.namo.ui.bottom.home.schedule.map.data.KakaoAPI
import com.mongmong.namo.ui.bottom.home.schedule.map.data.Place
import com.mongmong.namo.ui.bottom.home.schedule.map.data.ResultSearchPlace
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class GroupMapActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGroupMapBinding
    private lateinit var mapView : MapView
    private lateinit var mapViewContainer : ViewGroup

    private val mapRVAdapter : MapRVAdapter = MapRVAdapter()

    private val placeList : ArrayList<Place> = arrayListOf()
    private val markerList : ArrayList<MapPOIItem> = arrayListOf()
    private val defaultPlace : Place = Place()
    private var selectedPlace : Place = Place()
    private var prevPlace : Place = Place()

    private var originHeight : Int = 0

    private var uLatitude : Double = 0.0
    private var uLongitude : Double = 0.0

    companion object {
        const val BASE_URL = "https://dapi.kakao.com"
        const val API_KEY = "818de08ab952bb5274fa890b790f338d"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_group_map)
        mapView = MapView(this)
        mapViewContainer = binding.groupMapView
        mapViewContainer.addView(mapView)


        getLocationPermission()
        setCurrentLocation()

        setAdapter()
        clickListener()
    }

    private fun setAdapter() {
        binding.groupMapRv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.groupMapRv.adapter = mapRVAdapter

        setRVData()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setRVData() {
        mapRVAdapter.addPlaces(placeList)
        mapRVAdapter.notifyDataSetChanged()
    }

    private fun searchEventStart() {
        val imm: InputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)

        if (binding.groupMapSearchEt.text.isNullOrBlank()) {
            Toast.makeText(this, "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show()
        }
        else {
            searchPlace(binding.groupMapSearchEt.text.toString())
        }

    }

    private fun clickListener() {
        val targetActivityClass = when(intent.getStringExtra(ORIGIN_ACTIVITY_INTENT_KEY)) {
            "GroupSchedule" -> GroupScheduleActivity::class.java
            "Schedule" -> ScheduleActivity::class.java
            else -> ScheduleActivity::class.java
        }

        binding.groupMapSearchEt.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                searchEventStart()
                true
            } else {
                false
            }
        }

        binding.groupMapSearchBtn.setOnClickListener {
            searchEventStart()
        }

        mapRVAdapter.setItemClickListener( object :
            MapRVAdapter.OnItemClickListener {
            override fun onClick(position: Int) {

                val place : Place = placeList[position]
                Log.d("SELECTED_PLACE", place.toString())
                selectedPlace = place

                val mapPoint = MapPoint.mapPointWithGeoCoord(place.y.toDouble(), place.x.toDouble())
                mapView.setMapCenterPointAndZoomLevel(mapPoint, 1, true)

                mapView.selectPOIItem(markerList[position], true)

                binding.groupMapBtnLayout.visibility = View.VISIBLE
            }
        })

        binding.cancelBtn.setOnClickListener {
            finish()
        }

        binding.selectBtn.setOnClickListener {
            val intent = Intent(this, targetActivityClass)
            intent.putExtra(PLACE_NAME_INTENT_KEY, selectedPlace.place_name)
            intent.putExtra(PLACE_X_INTENT_KEY, selectedPlace.x)
            intent.putExtra(PLACE_Y_INTENT_KEY, selectedPlace.y)
            setResult(RESULT_OK, intent)
            finish()
        }
    }

    override fun finish() {
        binding.groupMapView.removeView(mapView)
        super.finish()
    }

    private fun searchPlace(keyword : String) {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(KakaoAPI::class.java)
        val call = api.getSearchPlace("KakaoAK $API_KEY", keyword, uLongitude.toString(), uLatitude.toString(), "distance")

        call.enqueue(object : Callback<ResultSearchPlace> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(
                call: Call<ResultSearchPlace>,
                response: Response<ResultSearchPlace>
            ) {
                Log.d("SEARCH_PLACE", " row : ${response.raw()}")
                Log.d("SEARCH_PLACE", " body : ${response.body()}")

                placeList.clear()
                placeList.addAll(response.body()!!.documents as ArrayList<Place>)
                setRVData()
                addMarkers()
            }

            override fun onFailure(call: Call<ResultSearchPlace>, t: Throwable) {
                Log.w("SEARCH_PLACE", "통신 실패 : ${t.message}")
            }
        })
    }

    private fun addMarkers() {
        mapView.removeAllPOIItems()
        if (placeList.isNullOrEmpty()) {
            Toast.makeText(this, "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show()
        }
        else {
            for (i in placeList) {
                val point = MapPOIItem()
                point.apply {
                    itemName = i.place_name
                    mapPoint = MapPoint.mapPointWithGeoCoord(i.y.toDouble(), i.x.toDouble())
                    markerType = MapPOIItem.MarkerType.BluePin
                    selectedMarkerType = MapPOIItem.MarkerType.RedPin
                }

                if (i == placeList[0]) {
                    mapView.setMapCenterPointAndZoomLevel(point.mapPoint, 1, true)
                }

                mapView.addPOIItem(point)
                markerList.add(point)
            }
        }
    }


    private fun setCurrentLocation() {
        val uNowPosition = MapPoint.mapPointWithGeoCoord(uLatitude, uLongitude)
        mapView.setMapCenterPoint(uNowPosition, true)
    }

    private fun getLocationPermission() {
        val permissionCheck = ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 권한이 없습니다.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        try {
            val userNowLocation : Location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)!!
            uLatitude = userNowLocation.latitude
            uLongitude = userNowLocation.longitude
        } catch (e : NullPointerException) {
            Log.e("LOCATION_ERROR", e.toString())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.finishAffinity(this)
            } else {
                ActivityCompat.finishAffinity(this)
            }

            finish()
        }
    }
}