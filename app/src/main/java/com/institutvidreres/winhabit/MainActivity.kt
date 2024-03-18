// MainActivity.kt
package com.institutvidreres.winhabit

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
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
    private lateinit var sharedViewModel: SharedViewModel

    private var userEmail: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedViewModel = ViewModelProvider(this).get(SharedViewModel::class.java)

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
                    sharedViewModel.signOut()
                    // Puedes redirigir a la pantalla de inicio de sesión o realizar otras acciones según tu lógica de la aplicación
                    val intent = Intent(this, AuthActivity::class.java)
                    startActivity(intent)
                    finish()
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

        // Obtén el valor del personaje seleccionado del Intent
        val sharedPreferences = getSharedPreferences("MyPrefs", MODE_PRIVATE)
        val character = sharedPreferences.getInt("user_character", -1)
        if (character != -1) {
            updateFirebaseIdCharacter(character)
            Log.d("MainActivity", "Personaje seleccionado: $character")
        } else {
            Log.e("MainActivity", "No se pudo obtener el personaje seleccionado")
        }

        val banner = sharedPreferences.getInt("user_banner", -1)
        if (banner != -1) {
            updateFirebaseIdBanner(banner)
            Log.d("MainActivity", "Personaje seleccionado: $banner")
        } else {
            Log.e("MainActivity", "No se pudo obtener el personaje seleccionado")
        }

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

    fun updateFirebaseIdCharacter(firebaseId: Int) {
        val imageName = when (firebaseId) {
            0 -> "vaquero.png"
            1 -> "mago.png"
            2 -> "arquero.png"
            3 -> "vaquera.png"
            4 -> "bruja.png"
            5 -> "arquera.png"
            6 -> "payaso.png"
            7 -> "dracula.png"
            8 -> "genio.png"
            9 -> "momia.png"
            10 -> "orco.png"
            11 -> "zombi.png"
            12 -> "caballero.png"
            13 -> "rey.png"
            14 -> "princesa.png"
            else -> throw IllegalArgumentException("Valor de personaje no válido")
        }

        val headerMain = binding.navView.getHeaderView(0)
        val imageView: ImageView = headerMain.findViewById(R.id.imageViewPersonajePerfil)

        // Obtener la URL de la imagen en Storage
        val storage = com.google.firebase.ktx.Firebase.storage
        val storageReference = storage.reference.child(imageName)

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

    // TODO: Mirar si en un futuro se puede implementar los banners en firebase igual que los personajes
    //TODO: Al Cerrar session y volver a entrar el personaje o banner que el usuario tennia se reiniciar, se puede dejar asi o canviarlo en un futuro
    fun updateFirebaseIdBanner(firebaseId: Int) {
        val backgroundDrawable = when (firebaseId) {
            19 -> R.drawable.side_nav_bar
            20 -> R.drawable.gradiante_azul
            21 -> R.drawable.gradiante_purpura
            22-> R.drawable.gradiante_rojo
            23 -> R.drawable.gradiante_verde_blanco
            24 -> R.drawable.gradiante_azul_purpura
            25 -> R.drawable.gradiante_azul_rojo
            26 -> R.drawable.gradiante_azul_purpura_rojo
            27 -> R.drawable.gradiante_dorado_negro

            else -> throw IllegalArgumentException("Valor de banner no válido")
        }

        val headerMain = binding.navView.getHeaderView(0)
        val navHeaderLayout = headerMain.findViewById<LinearLayout>(R.id.navHeader)

        // Setear el fondo XML al LinearLayout
        navHeaderLayout.setBackgroundResource(backgroundDrawable)
    }
}
