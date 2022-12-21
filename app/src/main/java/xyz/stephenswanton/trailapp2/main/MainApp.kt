package xyz.stephenswanton.trailapp2.main

import android.app.Application
import timber.log.Timber
import xyz.stephenswanton.trailapp2.models.*

class MainApp : Application() {
    var markersArray: MutableList<TrailMarker> = mutableListOf()
    lateinit var trails: TrailStore
    lateinit var tempTrailObject: TrailStore
    lateinit var users: UserStore
    lateinit var tempUserObject: UserStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}