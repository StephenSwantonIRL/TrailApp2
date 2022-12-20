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
        users  = UserJSONStore(applicationContext)
        tempUserObject  = CurrentUserJSONStore(applicationContext)
    }

    var markers: MutableList<TrailMarker> = mutableListOf()
    var tempTrail: Trail = Trail(0,"","")
    var currentUser: User? = null


    fun logoutCurrentUser(){
        currentUser = null
    }


}