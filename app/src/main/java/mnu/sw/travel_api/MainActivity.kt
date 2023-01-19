package mnu.sw.travel_api
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_kakao_map.*
import net.daum.mf.map.api.MapView

class KakaoMapActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kakao_map)

        val mapView = MapView(this)
        kakaoMapView.addView(mapView)
    }
}