package xyz.stephenswanton.trailapp2.ui.useraccount


import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.tasks.OnCompleteListener
import xyz.stephenswanton.trailapp2.MainActivity
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentLoginBinding


import xyz.stephenswanton.trailapp2.main.MainApp


interface LoginUIManager {
    fun enableNavDrawer()
    fun refreshImageHeader()
}


class LoginFragment : Fragment() {

    private lateinit var binding: FragmentLoginBinding
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
        binding = FragmentLoginBinding.inflate(layoutInflater)
        return binding.root


    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.loginBtn.setOnClickListener {
            var email: String = binding.emailEdtText.text.toString()
            var password: String = binding.passEdtText.text.toString()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(activity, "Please fill all the fields", Toast.LENGTH_LONG).show()
            } else{
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(app.mainExecutor, OnCompleteListener { task ->
                    if(task.isSuccessful) {
                        try {
                            (activity as LoginUIManager).refreshImageHeader()
                            (activity as LoginUIManager).enableNavDrawer()
                        }  catch (e: ClassCastException) {

                        }
                        Toast.makeText(activity, "Successfully Logged In", Toast.LENGTH_LONG).show()

                        findNavController().navigate(R.id.nav_all_trails)
                    }else {
                        Toast.makeText(activity, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                })
            }
        }

        binding.signupBtn.setOnClickListener{
            findNavController().navigate(R.id.nav_signup)
        }

        binding.resetPassTv.setOnClickListener{
            findNavController().navigate(R.id.nav_forgot_password)
        }
    }
}