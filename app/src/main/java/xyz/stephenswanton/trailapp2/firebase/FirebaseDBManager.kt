package firebase

import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import xyz.stephenswanton.trailapp2.models.Trail
import xyz.stephenswanton.trailapp2.models.TrailStore
import timber.log.Timber

object FirebaseDBManager : TrailStore {

    var database: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun findAll(): List<Trail> {
        var trailsList =  MutableLiveData<List<Trail>>()
        database.child("trails")
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    Timber.i("Firebase Donation error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val localList = ArrayList<Trail>()
                    val children = snapshot.children
                    children.forEach {
                        val trail = it.getValue(Trail::class.java)
                        localList.add(trail!!)
                    }
                    database.child("trails")
                        .removeEventListener(this)

                    trailsList.value = localList
                }
            })
        return trailsList.value ?: listOf<Trail>()
    }



    override fun create(trail: Trail) {
        // model needs to be updated to assign to user
        Timber.i("Firebase DB Reference : $database")
        val key = database.child("trails").push().key
        if (key == null) {
            Timber.i("Firebase Error : Key Empty")
            return
        }
        trail.uid = key
        val trailValues = trail.toMap()

        val childAdd = HashMap<String, Any>()
        childAdd["/trails/$key"] = trailValues
        childAdd["/user-trails/user/$key"] = trailValues

        database.updateChildren(childAdd)
    }

    override fun deleteById(id: Long) {

        val childDelete : MutableMap<String, Any?> = HashMap()
        childDelete["/trails/$id"] = null
        //childDelete["/user-trails/$userid/$id"] = null

        database.updateChildren(childDelete)
    }

    override fun update( trail: Trail) {

        val trailValues = trail.toMap()
        val trailId = trail.uid
        val childUpdate : MutableMap<String, Any?> = HashMap()
        childUpdate["trails/$trailId"] = trailValues
        //childUpdate["user-trails/$userid/$id"] = trailValues

        database.updateChildren(childUpdate)
    }

    override fun findById(trailId: Long): Trail? {
        TODO("Not yet implemented")
    }

    override fun deleteMarkerById(markerId: Long) {
        TODO("Not yet implemented")
    }

    override fun idContainingMarker(marker: Long): Long? {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        TODO("Not yet implemented")
    }

}