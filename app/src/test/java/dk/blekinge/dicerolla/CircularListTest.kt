package dk.blekinge.dicerolla

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CircularListTest {

    @Test
    fun testCirc1() {

        val normalRange = IntRange(0, 9).toMutableList()
        val circ = CircularList<Int>(normalRange)

        val sub15_20 = circ.subList(5, 10);
        val normalSublist = normalRange.subList(5, 10)
        assertThat(sub15_20).hasSize(normalSublist.size)
        assertThat(sub15_20).hasSameElementsAs(normalSublist)

    }

    @Test
    fun testCirc2() {

        val normalRange = IntRange(0, 9).toMutableList()
        val circ = CircularList<Int>(normalRange)

        val sub15_20 = circ.subList(15, 20);
        val normalSublist = normalRange.subList(5, 10)
        assertThat(sub15_20).hasSize(normalSublist.size)
        assertThat(sub15_20).hasSameElementsAs(normalSublist)

    }

    @Test
    fun testCirc3() {

        val normalRange = IntRange(0, 9).toMutableList()
        val circ = CircularList<Int>(normalRange)

        val sub15_20 = circ.subList(15, 21);
        val normalSublist = normalRange.subList(5, 10)
        assertThat(sub15_20).hasSize(6)
        assertThat(sub15_20).hasSameElementsAs(listOf(5,6,7,8,9,0))

    }

        @Test
    fun testCirc4() {

        val normalRange = IntRange(0, 9).toMutableList()
        val circ = CircularList<Int>(normalRange)

        val sub15_20 = circ.subList(0, 21);
        assertThat(sub15_20).hasSize(21)
        assertThat(sub15_20).hasSameElementsAs(listOf(0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0))

    }
}