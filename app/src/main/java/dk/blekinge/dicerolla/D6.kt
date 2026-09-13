package dk.blekinge.dicerolla

import java.io.Serializable
import java.util.Date
import java.util.Random
import java.util.function.IntFunction
import java.util.function.Supplier
import java.util.stream.Collectors

enum class D6(val drawable: Int) : Comparable<D6?>, Serializable {
    R1(R.drawable.d1),

    R2(R.drawable.d2),

    R3(R.drawable.d3),

    R4(R.drawable.d4),

    R5(R.drawable.d5),

    R6(R.drawable.d6);

    override fun compareTo(other: D6?): Int {
        return this.ordinal - other!!.ordinal
    }

    companion object {
        private val random: Random = Random(Date().getTime())

        val randomDicerolls: MutableList<D6> = random
            .ints(1000000, 0, entries.size)
            .mapToObj<D6?>(IntFunction { i: Int -> entries[i] })
            .collect(Collectors.toCollection(Supplier { CircularList() }))

        fun sequence(): Sequence<D6> {
            return entries.asSequence()
        }

        val randomOffset: Int
            get() = random.nextInt(randomDicerolls.size)

    }
}
