package de.gandalf1783.gameserver.objects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OneToOneMap<K,V> extends HashMap<K,V> {
    public OneToOneMap() {
        super();
    }
    public OneToOneMap(int initialCapacity) {
        super(initialCapacity);
    }
    public OneToOneMap(int initialCapacity, float loadFactor) {
        super(initialCapacity, loadFactor);
    }
    public OneToOneMap(Map<? extends K, ? extends V> m) {
        super(m);
        dedup();
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m){
        super.putAll(m);
        dedup();
    }

    @Override
    public V put(K key, V value) {
        if( key == null || value == null ) return null;
        removeValue(value);
        return super.put(key,value);
    }

    public K getKey( V value ){
        if( value == null || this.size() == 0 ) return null;
        Set<K> keys = new HashSet<>();
        keys.addAll(keySet());
        for( K key : keys ){
            if( value.equals(get(key) )) return key;
        }
        return null;
    }
    public boolean hasValue( V value ){
        return getKey(value) != null;
    }
    public boolean hasKey( K key ){
        return get(key) != null;
    }

    public void removeValue( V remove ){
        V value;
        Set<K> keys = new HashSet<>();
        keys.addAll(keySet());
        for( K key : keys ){
            value = get(key);
            if( value == null || key == null || value.equals(remove)) remove(key);
        }        
    }
    //can be used when a new map is assigned to clean it up
    public void dedup(){
        V value;
        Set<V> values = new HashSet<>();
        Set<K> keys = new HashSet<>();
        keys.addAll(keySet());
        for( K key : keys ){
            value = get(key);
            if( value == null || key == null || values.contains(value) ) remove(key);
            else values.add(value);
        }
    }
}