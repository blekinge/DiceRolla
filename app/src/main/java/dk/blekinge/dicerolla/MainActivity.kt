// MainActivity.kt
package dk.blekinge.dicerolla

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiceApp() {
    val navController = rememberNavController()
    Scaffold(
        topBar = { TopAppBar(title = { Text("Dice Roller") }) }
    ) { padding ->
        Navigation(
            navController = navController,
            modifier = Modifier.padding(padding)
        )
    }
}

@Composable
fun Navigation(
    navController: NavHostController, // 👈 CORRECT TYPE
    modifier: Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") {
            HomeScreen(
                navController = navController // 👈 PASS NAV CONTROLLER
            )
        }
        composable("results") {
            ResultsScreen(
                navController = navController // 👈 PASS NAV CONTROLLER
            )
        }
    }
}