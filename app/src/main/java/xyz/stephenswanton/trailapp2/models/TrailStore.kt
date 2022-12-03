package xyz.stephenswanton.trailapp2.models

interface TrailStore {
    fun findAll(): List<Trail>
    fun create(trail: Trail)
    fun update(trail: Trail)
    fun findById(trailId: Long): Trail?
    fun deleteMarkerById(markerId: Long)
    fun idContainingMarker(marker: Long):Long?
    fun deleteAll()
    fun deleteById(trailId: Long)
}