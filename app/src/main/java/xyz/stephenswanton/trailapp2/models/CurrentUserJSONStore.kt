package xyz.stephenswanton.trailapp2.models

import android.content.Context
import xyz.stephenswanton.trailapp2.helpers.*
import timber.log.Timber

const val cuJSON_FILE = "currentuser.json"

class CurrentUserJSONStore(private val context: Context) : UserStore {

    var users = mutableListOf<User>()

    init {
        if (exists(context, cuJSON_FILE)) {
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
        write(context, cuJSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, cuJSON_FILE)
        users = gsonBuilder.fromJson(jsonString, userlistType)
    }

    private fun logAll() {
        users.forEach { Timber.i("$it") }
    }
}
