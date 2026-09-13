// Bucket.kt
package dk.blekinge.dicerolla

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class Bucket(
    val dice: SortedMap<D6, Int>,
    val dicePoolSize: Int,
    var randomDicerollIndex: Int
) : java.io.Serializable {

    // Convert to Compose-friendly state (if needed)
    fun toState(): MutableState<Bucket> = mutableStateOf(this)

    companion object {
        fun create(dicepool: Int = 0, randomDicerollIndex: Int = 0): Bucket {
            return Bucket(
                dice = sortedMapOf(
                    D6.R1 to 0,
                    D6.R2 to 0,
                    D6.R3 to 0,
                    D6.R4 to 0,
                    D6.R5 to 0,
                    D6.R6 to 0
                ),
                dicePoolSize = dicepool,
                randomDicerollIndex = randomDicerollIndex
            )
        }

        fun roll(bucket: Bucket): Bucket {
            val newRolls = D6.randomDicerolls
                .subList(
                    bucket.randomDicerollIndex,
                    bucket.dicePoolSize.let { bucket.randomDicerollIndex += it; bucket.randomDicerollIndex }
                )
                .map { it to 1 }
                .plus(
                    sequenceOf(
                        D6.R1 to 0,
                        D6.R2 to 0,
                        D6.R3 to 0,
                        D6.R4 to 0,
                        D6.R5 to 0,
                        D6.R6 to 0
                    )
                )
                .groupingBy { it.first }
                .fold(0) { acc, item -> acc + item.second }
                .toSortedMap()

            return bucket.copy(dice = newRolls)
        }
    }
}