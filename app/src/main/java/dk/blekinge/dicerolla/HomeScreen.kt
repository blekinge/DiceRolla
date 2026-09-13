package dk.blekinge.dicerolla

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(
    viewModel: DiceViewModel = viewModel(),
    navController: NavHostController // 👈 ADD THIS PARAMETER
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter Dice Pool Size", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = viewModel.dicePool.toString(),
            onValueChange = { viewModel.dicePool = it.toIntOrNull() ?: 0 },
            label = { Text("Dice Pool") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                val bucket = Bucket.create(dicepool = viewModel.dicePool)
                viewModel.rollBucket(bucket)
                navController.navigate("results") // 👈 NOW VALID
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Roll Dice")
        }
    }
}