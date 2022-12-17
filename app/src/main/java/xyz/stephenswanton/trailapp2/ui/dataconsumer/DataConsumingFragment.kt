package xyz.stephenswanton.trailapp2.ui.dataconsumer

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import xyz.stephenswanton.trailapp2.databinding.FragmentForgotPasswordBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.*
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.MainActivity
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentSignupBinding
import xyz.stephenswanton.trailapp2.databinding.FragmentUpdatePasswordBinding
import xyz.stephenswanton.trailapp2.databinding.FragmentUserAccountBinding

import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.UserInfo


class DataConsumingFragment : Fragment() {

    private lateinit var binding: FragmentUserAccountBinding

    private lateinit var dbReference: DatabaseReference
    private lateinit var firebaseDatabase: FirebaseDatabase
    private var userId: String = ""

    lateinit var app: MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState)
        binding = FragmentUserAccountBinding.inflate(layoutInflater)
        return binding.root


    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        firebaseDatabase = FirebaseDatabase.getInstance()
        dbReference = firebaseDatabase.getReference("users")
        var edit = false

        if(edit){
            // userId equals user id passed in in arguments
        } else{
            userId = dbReference.push().key.toString()
        }

        i(userId)
        binding.updateUserBtn.setOnClickListener {
            var name: String = binding.nameEdtText.text.toString()
            var mobile: String = binding.mobileEdtText.text.toString()

            if (TextUtils.isEmpty(userId)) {
                createUser(name, mobile, userId)
            } else {
                updateUser(name, mobile)
            }
        }

    }

    private fun updateUser(name: String, mobile: String) {
        var user = UserInfo(name,mobile, userId)
        dbReference.child(userId).setValue(user)
        addUserChangeListener()

    }

    private fun createUser(name: String, mobile: String, uid: String) {
        val user = UserInfo(name, mobile, uid)
        dbReference.child(userId).setValue(user)
    }

    private fun addUserChangeListener() {
        // User data change listener
        dbReference.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val user = dataSnapshot.getValue(UserInfo::class.java)

                // Check for null
                if (user == null) {
                    return
                }


                // Display newly updated name and email
                binding.userName.setText(user?.name).toString()
                binding.userMobile.setText(user?.mobile).toString()

                // clear edit text
                binding.nameEdtText.setText("")
                binding.mobileEdtText.setText("")

                changeButtonText()
            }

            override fun onCancelled(error: DatabaseError) {
                // Failed to read value
            }
        })
    }
    private fun changeButtonText(){
        if (TextUtils.isEmpty(userId)) {
            binding.updateUserBtn.text = "Save";
        } else {
            binding.updateUserBtn.text = "Update";
        }
    }
}