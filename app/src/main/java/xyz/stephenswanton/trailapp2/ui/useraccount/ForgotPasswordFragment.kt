package xyz.stephenswanton.trailapp2.ui.useraccount

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

import xyz.stephenswanton.trailapp2.main.MainApp


class ForgotPasswordFragment : Fragment() {

    private lateinit var binding: FragmentForgotPasswordBinding
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
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root


    }


    override fun onViewCreated(itemView: View, savedInstanceState: Bundle?) {
        super.onViewCreated(itemView, savedInstanceState)
        auth = FirebaseAuth.getInstance()
        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.resetPassBtn.setOnClickListener {
            var email: String = binding.emailEdtText.text.toString()
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(activity, "Please enter email id", Toast.LENGTH_LONG).show()
            } else {
                auth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(app.mainExecutor, OnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(activity, "Reset link sent to your email", Toast.LENGTH_LONG)
                                .show()
                        } else {
                            Toast.makeText(activity, "Unable to send reset mail", Toast.LENGTH_LONG)
                                .show()
                        }
                    })
            }
        }
    }
}