package dk.blekinge.dicerolla

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.navigation.NavHostController

@Composable
fun ResultsScreen(
    viewModel: DiceViewModel, navController: NavHostController // 👈 ADD THIS PARAMETER
) {
    val bucket by viewModel.bucket.collectAsState()

    if (bucket == null) {
        Text("No results yet!")
        return
    }

    val selectedIds = rememberSaveable { mutableStateOf(emptySet<Int>()) } // NEW

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Rolled ${bucket?.dicePoolSize} Dice", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        //        https://medium.com/androiddevelopers/create-a-photo-grid-with-multiselect-behavior-using-jetpack-compose-9a8d588a9b63

        val state = rememberLazyGridState()

        LazyVerticalGrid(
            state = state,
            columns = GridCells.Fixed(6),
            verticalArrangement = Arrangement.spacedBy(3.dp),
            horizontalArrangement = Arrangement.spacedBy(3.dp),
            modifier = Modifier.photoGridDragHandler(state, selectedIds) // NEW
        ) {
            bucket?.dice?.forEach { (die, count) ->
                item(key = die.ordinal) {

                    val selected = selectedIds.value.contains(die.ordinal) // NEW
                    ImageItem(
                        die, selected, Modifier.toggleable(
                            value = selected,
                            interactionSource = remember { MutableInteractionSource() },
//                                indication = null, // do not show a ripple
                            onValueChange = {
                                if (it) {
                                    selectedIds.value += die.ordinal
                                } else {
                                    selectedIds.value -= die.ordinal
                                }
                            })
                    )
                    Text("$count", Modifier.align(Alignment.CenterHorizontally))
                }
            }
        }



        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                viewModel.reset()
                navController.popBackStack() // 👈 NAVIGATE BACK
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Roll Again")
        }
    }

}


fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState, selectedIds: MutableState<Set<Int>>
) = pointerInput(Unit) {

    // The key of the photo underneath the pointer. Null if no photo is hit by the pointer.
    fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
        layoutInfo.visibleItemsInfo.find { itemInfo ->
            itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
        }?.key as? Int

    var initialKey: Int? = null
    var currentKey: Int? = null
    detectDragGestures(
        onDragStart = { offset: Offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key -> // #1
                initialKey = key
                currentKey = key

                if (!selectedIds.value.contains(key)) { // #2
                    selectedIds.value = selectedIds.value + key // #3
                } else {
                    selectedIds.value = selectedIds.value - key // #3
                }
            }
        },
        onDragCancel = { initialKey = null },
        onDragEnd = { initialKey = null },
        onDrag = { change, _ ->
            if (initialKey != null) {
                // Add or remove photos from selection based on drag position
                lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                    if (currentKey != key) {
                        if (!selectedIds.value.contains(key)) { // #2
                            selectedIds.value = selectedIds.value + key
                        } else {
                            selectedIds.value = selectedIds.value - key
                        }
                        currentKey = key

                    }
                }
            }

        })
}


@Composable
private fun ImageItem(
    die: D6, selected: Boolean, modifier: Modifier
) {
    Surface(
        tonalElevation = 3.dp,
        contentColor = MaterialTheme.colorScheme.primary,
        modifier = modifier.aspectRatio(1f)
    ) {
        val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)

        if (selected) {
            Image(
                painter = painterResource(die.drawable),
                contentDescription = die.name,
                modifier = Modifier
                    .padding(6.dp)
                    .size(48.dp)
            )

        } else {
            Image(
                painter = painterResource(die.drawable),
                contentDescription = die.name,
                modifier = Modifier
                    .padding(6.dp)
                    .size(48.dp)
                    .border(2.dp, bgColor)

            )
        }


    }
}
