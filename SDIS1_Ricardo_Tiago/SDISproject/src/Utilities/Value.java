package Utilities;

public class Value {
    public Integer stores;

    public Value(Integer stores) {
        this.stores = stores;
    }

    @Override
    public String toString(){
        return Integer.toString(stores);
    }

    public synchronized void increment(){
        stores++;
    }

    public synchronized void decrement(){
        stores--;
    }
}
