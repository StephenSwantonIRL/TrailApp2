package xyz.stephenswanton.trailapp2.ui.logout

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import at.favre.lib.crypto.bcrypt.BCrypt
import com.google.android.material.snackbar.Snackbar
import xyz.stephenswanton.trailapp2.R
import xyz.stephenswanton.trailapp2.databinding.FragmentLogoutBinding
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.models.User

class LogoutFragment : Fragment() {

    private var _fragBinding: FragmentLogoutBinding? = null

    lateinit var app: MainApp

    val emailRegex: Regex =
        """^[a-zA-Z0-9.!#${'$'}%&’*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*${'$'}""".toRegex()
    val passwordRegex: Regex = """^.{5}.*$""".toRegex()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as MainApp

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _fragBinding = FragmentLogoutBinding.inflate(layoutInflater)
        return _fragBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Snackbar.make(view, R.string.signout_successful, Snackbar.LENGTH_LONG)
            .show()
        var user: User = User("", "")
        _fragBinding!!.btnLogin.setOnClickListener {
            user.username = _fragBinding!!.username.text.toString()
            user.password = _fragBinding!!.password.text.toString()
            onSubmitForm(user, it)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance() =
            LogoutFragment().apply {
                arguments = Bundle().apply {}
            }
    }


    private fun onSubmitForm(user: User, view: View) {


        if (user.username != "" || user.username.isNotEmpty()) {
            if (emailRegex.matches(user.username)) {
                var userExists = app!!.users.findByUsername(user.username) as User?
                if (userExists != null) {
                    var passwordCheck = BCrypt.verifyer()
                        .verify(user.password.toCharArray(), userExists.password);
                    if (passwordCheck.verified) {

                    } else {
                        Snackbar.make(view, R.string.invalid_password, Snackbar.LENGTH_LONG)
                            .show()
                    }
                } else {
                    if (user.password != "" || user.password.isNotEmpty()) {
                        if (passwordRegex.matches(user.password)) {
                            user.password =
                                BCrypt.withDefaults()
                                    .hashToString(12, user.password.toCharArray());
                            app!!.users.create(user.copy())
                            Snackbar.make(view, R.string.account_created, Snackbar.LENGTH_LONG)
                                .show()
                        } else {
                            Snackbar.make(view, R.string.invalid_password, Snackbar.LENGTH_LONG)
                                .show()
                        }
                    } else {
                        Snackbar.make(view, R.string.enter_password, Snackbar.LENGTH_LONG)
                            .show()
                    }
                }
            } else {
                Snackbar.make(view, R.string.enter_username_as_email, Snackbar.LENGTH_LONG)
                    .show()
            }
        } else {
            Snackbar.make(view, R.string.enter_username, Snackbar.LENGTH_LONG)
                .show()
        }
        if (user.password == "" || user.password.isEmpty()) {
            Snackbar.make(view, R.string.enter_password, Snackbar.LENGTH_LONG)
                .show()
        }
    }


}