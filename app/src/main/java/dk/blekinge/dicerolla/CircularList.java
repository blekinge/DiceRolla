package dk.blekinge.dicerolla;

import java.util.ArrayList;
import java.util.List;

public class CircularList<Type> extends ArrayList<Type> {
    public Type get(int index) {
        if (this.isEmpty()) {
            throw new IndexOutOfBoundsException("List is empty");
        }
        return super.get(index % this.size());
    }
}