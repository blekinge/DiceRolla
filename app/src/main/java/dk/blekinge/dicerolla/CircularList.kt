package dk.blekinge.dicerolla;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class CircularList<Type> extends ArrayList<Type> {
    public Type get(int index) {
        if (this.isEmpty()) {
            throw new IndexOutOfBoundsException("List is empty");
        }
        return super.get(index % this.size());
    }

    @NonNull
    @Override
    public List<Type> subList(int fromIndex, int toIndex) {
        return StreamU
        return super.subList(fromIndex, toIndex);
    }
}