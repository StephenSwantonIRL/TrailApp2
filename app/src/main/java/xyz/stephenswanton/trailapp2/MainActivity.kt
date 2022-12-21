package xyz.stephenswanton.trailapp2

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import timber.log.Timber
import timber.log.Timber.i
import xyz.stephenswanton.trailapp2.databinding.ActivityMainBinding
import xyz.stephenswanton.trailapp2.databinding.NavHeaderMainBinding
import xyz.stephenswanton.trailapp2.helpers.showImagePicker
import xyz.stephenswanton.trailapp2.main.MainApp
import xyz.stephenswanton.trailapp2.ui.logout.LogoutUIManager
import xyz.stephenswanton.trailapp2.ui.useraccount.LoginUIManager
import java.io.ByteArrayOutputStream
import kotlin.reflect.typeOf

class MainActivity : AppCompatActivity(), LoginUIManager, LogoutUIManager {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHeaderBinding : NavHeaderMainBinding
    private lateinit var drawerLayout: DrawerLayout

    lateinit var app : MainApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        app = application as MainApp
        i("")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        drawerLayout = binding.drawerLayout

        if ( FirebaseAuth.getInstance().currentUser == null){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        val navView: NavigationView = binding.navView

        val headerView = navView.getHeaderView(0)
        navHeaderBinding = NavHeaderMainBinding.bind(headerView)
        val navController = findNavController(R.id.nav_host_fragment_content_main)

        refreshImageHeader()

        var imageIntentLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult())
            { result ->

                if (result.data != null) {
                    i("Got Result ${result.data!!.data}")
                    updateImage(FirebaseAuth.getInstance().currentUser!!.uid,result.data!!.data!!,navHeaderBinding.ivNavHeader, false  )
                }
            }



        navHeaderBinding.ivNavHeader.setOnClickListener {
             showImagePicker(imageIntentLauncher)
        }

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home, R.id.nav_logout, R.id.nav_all_trails, R.id.nav_my_trails
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


    fun updateImage(userId: String, imageUri : Uri?, imageView: ImageView, updating : Boolean) {
        Picasso.get().load(imageUri)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?,
                                            from: Picasso.LoadedFrom?
                ) {
                    Timber.i("DX onBitmapLoaded $bitmap")
                    uploadImageToFirebase(userId, bitmap!!, updating)
                    imageView.setImageBitmap(bitmap)
                }

                override fun onBitmapFailed(e: java.lang.Exception?,
                                            errorDrawable: Drawable?) {
                    Timber.i("DX onBitmapFailed $e")
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    Timber.i("DX onPrepareLoad $placeHolderDrawable")
                    //uploadImageToFirebase(userid, defaultImageUri.value,updating)
                }
            })
    }




    fun uploadImageToFirebase(userId: String, bitmap: Bitmap, updating : Boolean) {
        // Get the data from an ImageView as bytes
        val imageRef = FirebaseStorage.getInstance().reference.child("users").child("${userId}.jpg")
        i(imageRef.toString())

        //val bitmap = (imageView as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        lateinit var uploadTask: UploadTask

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()
        uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            i(it.message.toString())
        }

    }

    override fun enableNavDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
    }

    override fun disableNavDrawer() {
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
    }

    override fun refreshImageHeader() {

        if( FirebaseAuth.getInstance().currentUser != null) {
            i("it ran")
            FirebaseStorage.getInstance().reference.child("users")
                .child("${FirebaseAuth.getInstance().currentUser!!.uid}.jpg")
                .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it)
                        .into(navHeaderBinding.ivNavHeader)
                }
        }
    }


}