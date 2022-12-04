package xyz.stephenswanton.trailapp2.models

import com.google.firebase.database.*
import timber.log.Timber
import timber.log.Timber.i

class MarkerFirebaseStore {
    val firebaseDatabase =  FirebaseDatabase.getInstance()
    var dbReference : DatabaseReference = firebaseDatabase.getReference("markers")
    var markerList = mutableListOf<TrailMarker>()

    fun createKey(): String {
        return dbReference.push().key.toString()
    }



    fun findAll(): List<TrailMarker> {
        dbReference
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot){
                    val localList = ArrayList<TrailMarker>()
                    val children = snapshot.children

                    children.forEach {
                        val marker = it.getValue(TrailMarker::class.java)
                        localList.add(marker!!)

                    }
                    dbReference.removeEventListener(this)
                    i(localList.toString())
                    markerList.addAll(localList)
                }

            })
        return markerList
    }

    fun create(marker: TrailMarker) {
        val newKey = this.createKey()
        marker.uid = newKey
        dbReference.child(newKey).setValue(marker)
    }

    fun update(marker: TrailMarker) {
        val markerId = marker.uid ?: dbReference.push().key.toString()
        dbReference.child(markerId).setValue(marker)
    }

    fun update(marker: Map<String,Any?>) {
        val markerId = marker["uid"] as String ?: dbReference.push().key.toString()
        dbReference.child(markerId).setValue(marker)
    }

    fun findById(markerId: String): TrailMarker? {
        var marker: TrailMarker? = null
        dbReference.child("$markerId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    marker = dataSnapshot.getValue(TrailMarker::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return marker
    }

    fun deleteById(markerId: String) {
        i(markerId)
        dbReference.child(markerId).removeValue()
    }

    fun findMarkersByTrailId(trailId: String): List<TrailMarker> {
        var snapshot = dbReference.orderByChild("uid").equalTo(trailId)
        snapshot.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot){
                val localList = ArrayList<TrailMarker>()
                val children = snapshot.children

                children.forEach {
                    val marker = it.getValue(TrailMarker::class.java)
                    localList.add(marker!!)

                }
                dbReference.removeEventListener(this)
                i(localList.toString())
                markerList.addAll(localList)
            }

        })
        i(markerList.toString())
        return markerList
    }

    fun deleteMarkersInTrail(trailId: String){
        var snapshot = dbReference.orderByChild("uid").equalTo(trailId)
        snapshot.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Timber.i("Firebase error : ${error.message}")
            }

            override fun onDataChange(snapshot: DataSnapshot){
                val children = snapshot.children

                children.forEach {
                    val marker = it.getValue(TrailMarker::class.java)
                    i(marker.toString())
                    dbReference.child(marker!!.uid!!).removeValue()

                }
                dbReference.removeEventListener(this)
            }

        })
    }
}