package dk.blekinge.dicerolla

import java.io.Serializable
import java.util.Date
import java.util.Random
import java.util.function.IntFunction
import java.util.function.Supplier
import java.util.stream.Collectors

enum class D6(@JvmField val imageButtonId: Int, val labelId: Int) : Comparable<D6?>, Serializable {
    R1(R.id.D1, R.id.count1),

    R2(R.id.D2, R.id.count2),

    R3(R.id.D3, R.id.count3),

    R4(R.id.D4, R.id.count4),

    R5(R.id.D5, R.id.count5),

    R6(R.id.D6, R.id.count6);

    override fun compareTo(other: D6?): Int {
        return ordinal.compareTo(other!!.ordinal)
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
