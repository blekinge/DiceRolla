package dk.blekinge.dicerolla

import kotlin.collections.ArrayList

class CircularList<Type> : ArrayList<Type> {

    constructor(c: Collection<Type?>) : super(c)
    constructor() : super()

    override fun get(index: Int): Type {
        if (this.isEmpty()) {
            throw IndexOutOfBoundsException("List is empty")
        }
        return super.get(index % this.size)
    }

    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Type> {
        return if (fromIndex >= size) {
            subList(fromIndex - size, toIndex - size)
        } else if (toIndex > size) {
            super.subList(fromIndex, size).plus(subList(0, toIndex - size)).toMutableList()
        } else {
            super.subList(fromIndex, toIndex)
        }
    }
}