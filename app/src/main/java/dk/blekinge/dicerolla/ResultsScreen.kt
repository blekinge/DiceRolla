package dk.blekinge.dicerolla

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController

@Composable
fun ResultsScreen(
    viewModel: DiceViewModel,
    navController: NavHostController // 👈 ADD THIS PARAMETER
) {
    val bucket by viewModel.bucket.collectAsState()


    if (bucket == null) {
        Text("No results yet!")
        return
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Rolled ${bucket?.dicePoolSize} Dice", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        bucket?.dice?.forEach { (die, count) ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("D${die.ordinal+1}: $count", modifier = Modifier.weight(1f))
                if (count > 0) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.reset()
                navController.popBackStack() // 👈 NAVIGATE BACK
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Roll Again")
        }
    }
}