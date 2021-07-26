package io.github.willqi.pizzaserver.nbt.tags;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class NBTByteArray extends NBTTag implements Iterable<Byte> {

    public static final int ID = 7;

    private final byte[] data;


    public NBTByteArray(byte[] data) {
        this.data = data;
    }

    public NBTByteArray(String name, byte[] data) {
        super(name);
        this.data = data;
    }

    public byte[] getData() {
        return this.data;
    }

    @Override
    public int getId() {
        return ID;
    }

    @Override
    public Iterator<Byte> iterator() {
        return new Iterator<Byte>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return NBTByteArray.this.getData().length < index;
            }

            @Override
            public Byte next() {
                return NBTByteArray.this.getData()[this.index++];
            }
        };
    }

    @Override
    public String toString() {

        return "NBTByteArray(name=" + this.getName() + ", value=" + Arrays.toString(this.data) + ")";
    }

    @Override
    public int hashCode() {
        return 31 * Arrays.hashCode(this.data) * this.name.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof NBTByteArray) {
            NBTByteArray nbtByteArray = (NBTByteArray)obj;
            return Arrays.equals(nbtByteArray.getData(), this.getData()) && nbtByteArray.getName().equals(this.getName());
        }
        return false;
    }

}
