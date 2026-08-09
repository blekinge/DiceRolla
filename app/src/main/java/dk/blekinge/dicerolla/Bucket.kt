package dk.blekinge.dicerolla

import java.io.Serializable
import java.util.SortedMap

data class Bucket(
    val dice: SortedMap<D6, Int>,
    val dicePoolSize: Int,
    var randomDicerollIndex: Int
) : Serializable {

    constructor(dicepool: Int, randomDicerollIndex: Int) : this(
        dice = sortedMapOf(
            Pair(D6.R1, 0),
            Pair(D6.R2, 0),
            Pair(D6.R3, 0),
            Pair(D6.R4, 0),
            Pair(D6.R5, 0),
            Pair(D6.R6, 0)
        ),
        dicePoolSize = dicepool,
        randomDicerollIndex = randomDicerollIndex
    )

    constructor() : this(
        dicepool = 0,
        randomDicerollIndex = 0
    )

    fun rollDice(): SortedMap<D6, Int> {
        return D6.randomDicerolls.subList(
            fromIndex = randomDicerollIndex,
            toIndex = dicePoolSize.let { randomDicerollIndex += it; randomDicerollIndex })
            .map { Pair(it, 1) }
            .asSequence()
            .plus(
                sequenceOf(
                    Pair(D6.R1, 0),
                    Pair(D6.R2, 0),
                    Pair(D6.R3, 0),
                    Pair(D6.R4, 0),
                    Pair(D6.R5, 0),
                    Pair(D6.R6, 0)
                )
            )
            .groupingBy { it.first }
            .fold(
                0,
                { accumulatedValue, elementBeingAdded -> accumulatedValue + elementBeingAdded.second })
            .toSortedMap()
    }
}