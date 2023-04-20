package com.example.google

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.google.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*
import java.io.File
import java.net.MalformedURLException
import java.net.URL
import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mMoonTiles: TileOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        val db = DatabaseHelper(this).readableDatabase
        val tileProvider: TileProvider = object : UrlTileProvider(256, 256) {
            @Synchronized
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {

                val query = "SELECT image FROM tiles WHERE x=? AND y=?"
                val cursor = db.rawQuery(query, arrayOf(x.toString(),	y.toString()))
                if (cursor.moveToFirst()) {
                    val image = cursor.getBlob(cursor.getColumnIndex("image"))
                    val file = File.createTempFile("images", null, this@MapsActivity.cacheDir)
                    file.writeBytes(image)
                    cursor.close()
                    return file.toURI().toURL()
                }
                val reversedY = (1 shl zoom) - y - 1
                val s = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoom, x, reversedY)
                var url: URL? = null
                url = try {
                    URL(s)
                } catch (e: MalformedURLException) {
                    throw AssertionError(e)
                }
                return url
            }
        }
        mMoonTiles = mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))!!
        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    companion object {
        private const val TRANSPARENCY_MAX = 100

        /** This returns moon tiles.  */
        private const val MOON_MAP_URL_FORMAT = "https://mw1.google.com/mw-planetary/lunar/lunarmaps_v1/clem_bw/%d/%d/%d.jpg"
    }
}