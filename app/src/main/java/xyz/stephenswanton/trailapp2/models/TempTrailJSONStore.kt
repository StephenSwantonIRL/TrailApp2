package xyz.stephenswanton.trailapp2.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import xyz.stephenswanton.trailapp2.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val tJSON_FILE = "trail.json"


class TempTrailJSONStore(private val context: Context) : TrailStore {

    var trails = mutableListOf<Trail>()

    init {
        if (exists(context, tJSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<Trail> {
        logAll()
        return trails
    }

    override fun create(trail: Trail) {
        trail.id = generateRandomId()
        trails.add(trail)
        serialize()
    }


    override fun update(trail: Trail) {
        trails = mutableListOf()
        trails.add(trail);
        serialize()
    }

    override fun findById(trailId: Long): Trail? {
        TODO("Not yet implemented")
    }

    override fun deleteMarkerById(markerId: Long) {
        TODO("Not yet implemented")
    }

    override fun idContainingMarker(marker: Long): Long {
        TODO("Not yet implemented")
        var value: Long = 0
        return value
    }

    fun deleteMarkerById(id: Long, id1: Long) {
        TODO("Not yet implemented")
    }

    override fun deleteAll(){
           trails = mutableListOf()
           serialize()
        }
    override fun deleteById(trailId: Long) {
        TODO("Not yet implemented")
        }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(trails, listType)
        write(context, tJSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, tJSON_FILE)
        trails = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        trails.forEach { Timber.i("$it") }
    }
}
