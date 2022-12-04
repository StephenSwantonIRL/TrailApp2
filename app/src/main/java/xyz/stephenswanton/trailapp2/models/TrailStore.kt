package xyz.stephenswanton.trailapp2.models

interface TrailStore {
    fun findAll(): List<Trail>
    fun create(trail: Trail)
    fun update(trail: Trail)
    fun update(trail: Map< String, Any?>)
    fun findById(trailId: Long): Trail?
    fun deleteMarkerById(markerId: Long)
    fun idContainingMarker(marker: Long):Long?
    fun findById(trailId: String): Trail?
    fun deleteMarkerById(markerId: String)
    fun idContainingMarker(marker: String):String?
    fun deleteAll()
    fun deleteById(trailId: String)
}