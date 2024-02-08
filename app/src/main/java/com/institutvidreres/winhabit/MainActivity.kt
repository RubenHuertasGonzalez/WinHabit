// MainActivity.kt
package com.institutvidreres.winhabit

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.ktx.storage
import com.institutvidreres.winhabit.databinding.ActivityMainBinding
import com.institutvidreres.winhabit.ui.login.AuthActivity

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var analytics: FirebaseAnalytics

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the FirebaseAnalytics instance.
        analytics = Firebase.analytics

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.mainContainer) as NavHostFragment
        this.navController = navHostFragment.navController
        val navController = navHostFragment.findNavController()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.recompensasFragment, R.id.inicioFragment, R.id.logrosFragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        setupWithNavController(bottomNavigationView, navController)

        // TODO: Optimizar parte de destinacion del menu drawer (más adelante)
        navController.addOnDestinationChangedListener { _, destination, _ ->

            when (destination.id) {
                R.id.perfilFragment -> {
                    bottomNavigationView.visibility = View.GONE
                }
                else -> {
                    bottomNavigationView.visibility = View.VISIBLE
                }
            }
        }

        // Accion para el Cerrar sesion
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.authActivity -> {
                    // Desmarcar el elemento del menú después de cerrar sesión
                    menuItem.isChecked = false
                    // Cerrar sesión solo cuando se hace clic en "Cerrar Sesión"
                    signOut()
                    true
                }
                else -> {
                    // Acciones predeterminadas para otros elementos del menú
                    bottomNavigationView.visibility = View.VISIBLE

                    // Navegar al destino correspondiente cuando se hace clic en un fragmento
                    NavigationUI.onNavDestinationSelected(menuItem, navController)

                    // Cerrar el menú después de hacer clic en un elemento
                    binding.drawerLayout.closeDrawers()
                    true
                }
            }
            // Navegar al destino correspondiente cuando se hace clic en un fragmento
            val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
            handled || super.onOptionsItemSelected(menuItem)
        }

        // Obtener el correo electrónico del intento y actualizar el perfil
        userEmail = intent.getStringExtra("user_email")
        updateNavigationDrawerEmail()

        // Listener para el clic en la imagen del perfil
        binding.navView.getHeaderView(0).findViewById<ImageView>(R.id.imageViewPersonajePerfil)
            .setOnClickListener {
                navController.navigate(R.id.perfilFragment)
                binding.drawerLayout.closeDrawers()
            }

        val headerMain = navView.getHeaderView(0)
        val imageView: ImageView = headerMain.findViewById(R.id.imageViewPersonajePerfil)

        // Obtener la URL de la imagen en Storage
        val storage = com.google.firebase.ktx.Firebase.storage
        val storageReference = storage.reference.child("arquera.png")

        storageReference.downloadUrl.addOnSuccessListener { uri ->
            // Cargar la imagen en el ImageView utilizando Glide
            Glide.with(this)
                .load(uri)
                .into(imageView)

            // Actualizar la URL de la imagen en el ViewModel
            val sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)
            sharedViewModel.selectedImageUri.value = uri.toString()
            Log.d("URL de la imagen: ", "$uri")
        }.addOnFailureListener { exception ->
            // Manejar el error si la descarga falla
            // Puedes agregar un Log, Toast o cualquier otra acción de manejo de errores aquí
            println("Error al descargar la imagen: ${exception.message}")
        }
    }

    private fun signOut() {
        // Aquí debes realizar el cierre de sesión en Firebase Auth
        FirebaseAuth.getInstance().signOut()

        // Puedes redirigir a la pantalla de inicio de sesión o realizar otras acciones según tu lógica de la aplicación
        val intent = Intent(this, AuthActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.mainContainer)
        // Abre el Drawer si está cerrado, y viceversa
        if (!binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        } else {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    fun updateNavigationDrawerEmail() {
        // Actualizar la vista con el correo almacenado
        val textViewCorreoPerfil = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.textViewCorreoPerfil)
        textViewCorreoPerfil.text = userEmail
    }
}
