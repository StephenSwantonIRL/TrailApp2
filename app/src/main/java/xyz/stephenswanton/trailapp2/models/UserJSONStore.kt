package xyz.stephenswanton.trailapp2.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import xyz.stephenswanton.trailapp2.helpers.*
import timber.log.Timber
import timber.log.Timber.i
import java.lang.reflect.Type
import java.util.*

const val uJSON_FILE = "user.json"

val userlistType: Type = object : TypeToken<ArrayList<User>>() {}.type


class UserJSONStore(private val context: Context) : UserStore {

    var users = mutableListOf<User>()

    init {
        if (exists(context, uJSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): List<User> {
        logAll()
        return users
    }

    override fun create(user: User) {
        users.add(user)
        serialize()
    }


    override fun update(user: User) {
        users = users!!.filter{it.username != user.username} as MutableList<User>
        users.add(user)
        serialize()
    }

    override fun findByUsername(username: String): User? {
        i(username)
        var user: User? = null
        users.forEach{ item -> if(item.username == username){
            user = item
        } }
        return user
    }

    override fun deleteAll() {
            users = mutableListOf()
            serialize()
        }

    override fun deleteByUsername(username: String) {
            users = users!!.filter{it.username != username} as MutableList<User>
            serialize()
        }


    private fun serialize() {
        val jsonString = gsonBuilder.toJson(users, userlistType)
        write(context, uJSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, uJSON_FILE)
        users = gsonBuilder.fromJson(jsonString, userlistType)
    }

    fun logAll() {
        users.forEach { Timber.i("$it") }
    }
}
