package xyz.stephenswanton.trailapp2.ui.logout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentLogoutBinding
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.User
import xyz.stephenswanton.trailapp2.ui.useraccount.LoginUIManager


interface LogoutUIManager {
    fun disableNavDrawer()
    fun refreshImageHeader()
}


class LogoutFragment : Fragment() {

    private var _fragBinding: FragmentLogoutBinding? = null

    lateinit var app: MainApp

    val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth.signOut()
        try {
            (activity as LoginUIManager).refreshImageHeader()
            (activity as LogoutUIManager).disableNavDrawer()
        }  catch (e: ClassCastException) {

        }
         val navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_login, true).build()
        findNavController().navigate(R.id.nav_login, null, navOptions)
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    override fun onResume() {
        super.onResume()
        auth.signOut()
        try {
            (activity as LoginUIManager).refreshImageHeader()
            (activity as LogoutUIManager).disableNavDrawer()
        }  catch (e: ClassCastException) {

        }
        findNavController().clearBackStack(R.id.nav_login)
    }


}