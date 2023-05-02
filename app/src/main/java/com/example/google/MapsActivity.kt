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

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        val tileProvider: TileProvider = object : UrlTileProvider(256, 256){
            @Synchronized
            override fun getTileUrl(x: Int, y: Int, zoom: Int): URL? {
                val db = DatabaseHelper(this@MapsActivity).readableDatabase
                val query = "SELECT image FROM tiles WHERE x=? AND y=?"
                val cursor = db.rawQuery(query, arrayOf(x.toString(),	y.toString()))
                if (cursor.moveToFirst()) {
                    val image = cursor.getBlob(cursor.getColumnIndex("image"))
                    print(image)
                    val file = File.createTempFile("images", null, this@MapsActivity.cacheDir)
                    file.writeBytes(image)
                    cursor.close()
                    db.close()
                    return file.toURI().toURL()
                }
                return URL("")
            }
        }
        mMoonTiles = mMap.addTileOverlay(TileOverlayOptions().tileProvider(tileProvider))!!
    }

}