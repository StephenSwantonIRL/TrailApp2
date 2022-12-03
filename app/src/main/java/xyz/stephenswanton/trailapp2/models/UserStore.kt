package xyz.stephenswanton.trailapp2.models

interface UserStore {
    fun findAll(): List<User>
    fun create(user: User)
    fun update(user: User)
    fun findByUsername(username: String): User?
    fun deleteAll()
    fun deleteByUsername(username: String)
}