package xyz.stephenswanton.trailapp2.models

import com.google.firebase.database.*
import timber.log.Timber
import timber.log.Timber.i

class TrailFirebaseStore : TrailStore {
    val firebaseDatabase =  FirebaseDatabase.getInstance()
    var dbReference : DatabaseReference = firebaseDatabase.getReference("trails")
    var trailsList = mutableListOf<Trail>()

    fun createKey(): String {
        return dbReference.push().key.toString()
    }



    override fun findAll(): List<Trail> {
        i("initial trail list")
        dbReference
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot){
                    val localList = ArrayList<Trail>()
                    val children = snapshot.children

                    children.forEach {
                        val trail = it.getValue(Trail::class.java)
                        localList.add(trail!!)

                    }
                    dbReference.removeEventListener(this)
                    i(localList.toString())
                    trailsList.addAll(localList)
                }

            })
        return trailsList
    }

    override fun create(trail: Trail) {
        val newKey = this.createKey()
        trail.uid = newKey
        dbReference.child(newKey).setValue(trail)
    }

    override fun update(trail: Trail) {
        i(trail.toString())
        val trailId = trail.uid ?: dbReference.push().key.toString()
        dbReference.child(trailId).setValue(trail)
    }

    override fun update(trail: Map<String,Any?>) {
        i(trail.toString())
        val trailId = trail["uid"] as String ?: dbReference.push().key.toString()
        dbReference.child(trailId).setValue(trail)
    }

    override fun findById(trailId: Long): Trail? {
        var trail: Trail? = null
        dbReference.child("$trailId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    trail = dataSnapshot.getValue(Trail::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return trail
    }

    override fun findById(trailId: String): Trail? {
        var trail: Trail? = null
        dbReference.child("$trailId")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    trail = dataSnapshot.getValue(Trail::class.java)
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return trail
    }


    override fun deleteMarkerById(markerId: Long) {

        //come back to this???
        val trailId : String = this.idContainingMarker(markerId) as String

        dbReference.child("trails/$trailId/markers/$markerId").removeValue()

    }

    override fun deleteMarkerById(markerId: String) {
        TODO("Not yet implemented")
    }

    override fun idContainingMarker(marker: Long): Long? {
        var trailId : Long? = null
        dbReference.child("markers/$marker")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    trailId = dataSnapshot.value as Long
                }

                override fun onCancelled(error: DatabaseError) {
                    TODO("Not yet implemented")
                }
            })
        return trailId
    }

    override fun idContainingMarker(marker: String): String? {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        dbReference.removeValue()
    }

    override fun deleteById(trailId: String) {
        i(trailId)
        dbReference.child(trailId).removeValue()
    }
}