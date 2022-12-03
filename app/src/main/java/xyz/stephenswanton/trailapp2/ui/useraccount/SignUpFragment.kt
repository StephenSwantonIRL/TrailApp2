package xyz.stephenswanton.trailapp2.ui.useraccount

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
import xyz.stephenswanton.trailapp2.MainActivity
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentSignupBinding

import xyz.stephenswanton.trailapp2.main.MainApp


class SignUpFragment : Fragment() {

    private lateinit var binding: FragmentSignupBinding
    private lateinit var auth: FirebaseAuth
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
        binding = FragmentSignupBinding.inflate(layoutInflater)
        return binding.root


    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.signupBtn.setOnClickListener{
            var email: String = binding.emailEdtText.text.toString()
            var password: String = binding.passEdtText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else{
                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(app.mainExecutor, OnCompleteListener{ task ->
                    if(task.isSuccessful){
                        Toast.makeText(activity, "Successfully Registered", Toast.LENGTH_LONG).show()
                        findNavController().navigate(R.id.nav_my_trails)
                    }else {
                        Toast.makeText(activity, "Registration Failed", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        binding.loginBtn.setOnClickListener{
            findNavController().navigate(R.id.nav_login)
        }
    }
}