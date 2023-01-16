package mnu.sw.travel_api

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.connple.weat.R
import kotlinx.android.synthetic.main.activity_map.*
import kotlinx.android.synthetic.main.activity_map_info.view.*
import kotlinx.android.synthetic.main.activity_main.*
import net.daum.mf.map.api.CalloutBalloonAdapter
import net.daum.mf.map.api.MapPOIItem
import net.daum.mf.map.api.MapPoint
import net.daum.mf.map.api.MapView


class MapActivity : AppCompatActivity(), MapView.POIItemEventListener,
    MapView.MapViewEventListener {
    val currentLocationMarker: MapPOIItem = MapPOIItem()
    var centerPoint: MapPoint? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // 레이아웃 뷰 onClick 설정
        activity_map_button_back.setOnClickListener {
            finish()
        }

        // 카카오 지도 캐시 저장 옵션 활성화
        if (MapView.isMapTilePersistentCacheEnabled()) {
            MapView.setMapTilePersistentCacheEnabled(true)
        }

        // 내 현재 위치 불러오기
        activity_map_find_location.setOnClickListener {
            val locationRequestCode = 1002
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            )
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        locationRequestCode
                    )
                }
            // 뷰의 트래킹 모드 설정
            activity_kakaomap_map.currentLocationTrackingMode =
                MapView.CurrentLocationTrackingMode.TrackingModeOnWithoutHeading

            println("eventPoint: $centerPoint")

            // centerPoint null 아니면 좌표값 설정
            if (centerPoint != null) {
                val point = MapPoint.mapPointWithGeoCoord(
                    centerPoint!!.mapPointGeoCoord.latitude,
                    centerPoint!!.mapPointGeoCoord.longitude
                )
                // 좌표값 setter
                activity_kakaomap_map.setMapCenterPoint(point, false)
                createCustomMaker(activity_kakaomap_map)
            }
        }

        // POI = point of interest : 주변 마커들을 의미함
        // 맵뷰 리스너, POI
        activity_kakaomap_map.setMapViewEventListener(this)
        activity_kakaomap_map.setPOIItemEventListener(this)


        // 현재 위치 리스너
        // 현재 위치 업데이트
        class CurrentLocationListener :
            MapView.CurrentLocationEventListener {
            override fun onCurrentLocationUpdateFailed(p0: MapView?) {
                Toast.makeText(applicationContext, "위치 정보를 불러오는데 실패했습니다.", Toast.LENGTH_LONG).show()
            }

            override fun onCurrentLocationUpdate(
                mapView: MapView?,
                point: MapPoint?,
                accuracy: Float
            ) {
                println("point: $point")
                if (point != null) {
                    // 현재 위치 업데이트
                    centerPoint = point
                    currentLocationMarker.moveWithAnimation(point, false)
                    currentLocationMarker.alpha = 1f
                    if (mapView != null) {
                        activity_kakaomap_map.setMapCenterPoint(
                            MapPoint.mapPointWithGeoCoord(
                                point.mapPointGeoCoord.latitude,
                                point.mapPointGeoCoord.longitude
                            ), false
                        )
                    }
                }
            }

            override fun onCurrentLocationUpdateCancelled(p0: MapView?) {
                Toast.makeText(applicationContext, "위치 요청이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }

            override fun onCurrentLocationDeviceHeadingUpdate(p0: MapView?, p1: Float) {
                Toast.makeText(applicationContext, "단말의 각도 값 요청", Toast.LENGTH_SHORT).show()
            }
        }

        // 현재 위치 리스너 설정
        class CustomCalloutBalloonAdapter : CalloutBalloonAdapter {
            private var mCalloutBalloon: View =
                layoutInflater.inflate(R.layout.activity_map_info, null)

            // 마커 선택시 값 변경
            override fun getCalloutBalloon(poiItem: MapPOIItem): View {
                mCalloutBalloon.activity_map_store_name.text = "가게 이름"
                return mCalloutBalloon
            }

            override fun getPressedCalloutBalloon(poiItem: MapPOIItem?): View? {
                return null
            }
        }

        // 현재 위치 리스너
        activity_kakaomap_map.setCurrentLocationEventListener(CurrentLocationListener())

        // 구현한 CalloutBalloonAdapter 등록
        activity_kakaomap_map.setCalloutBalloonAdapter(CustomCalloutBalloonAdapter())
    }

    private fun createCustomMaker(mapView: MapView) {
        val customMaker = MapPOIItem()
        customMaker.itemName = "Custom Marker"
        customMaker.tag = 1
        println("centerPoint: $centerPoint")
        customMaker.mapPoint = centerPoint
        customMaker.markerType = MapPOIItem.MarkerType.CustomImage
        customMaker.customImageResourceId = R.drawable.ic_location_mark_01
        customMaker.isCustomImageAutoscale = false
        currentLocationMarker.setCustomImageAnchor(0.5f, 0.5f)
        activity_kakaomap_map.addPOIItem(currentLocationMarker)
        activity_kakaomap_map.selectPOIItem(currentLocationMarker, true)
        activity_kakaomap_map.setMapCenterPoint(centerPoint, false)
    }

    private fun createStoreMaker(mapView: MapView, storePoint: MapPoint) {
        val customMaker = MapPOIItem()
        customMaker.itemName = "Custom Marker"
        customMaker.tag = 1
        println("centerPoint: $storePoint")
        customMaker.mapPoint = storePoint
        customMaker.markerType = MapPOIItem.MarkerType.CustomImage
        customMaker.customImageResourceId = R.drawable.ic_location_mark_01
        customMaker.isCustomImageAutoscale = false
        currentLocationMarker.setCustomImageAnchor(0.5f, 0.5f)
        activity_kakaomap_map.addPOIItem(currentLocationMarker)
        activity_kakaomap_map.selectPOIItem(currentLocationMarker, true)
        activity_kakaomap_map.setMapCenterPoint(centerPoint, false)
    }

    override fun onCalloutBalloonOfPOIItemTouched(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onCalloutBalloonOfPOIItemTouched(
        p0: MapView?,
        p1: MapPOIItem?,
        p2: MapPOIItem.CalloutBalloonButtonType?
    ) {

    }

    override fun onDraggablePOIItemMoved(p0: MapView?, p1: MapPOIItem?, p2: MapPoint?) {

    }

    override fun onPOIItemSelected(p0: MapView?, p1: MapPOIItem?) {

    }

    override fun onMapViewDoubleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewInitialized(p0: MapView?) {
    }

    override fun onMapViewDragStarted(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewMoveFinished(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewCenterPointMoved(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewDragEnded(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewSingleTapped(p0: MapView?, p1: MapPoint?) {

    }

    override fun onMapViewZoomLevelChanged(p0: MapView?, p1: Int) {

    }

    override fun onMapViewLongPressed(p0: MapView?, p1: MapPoint?) {

    }
}
