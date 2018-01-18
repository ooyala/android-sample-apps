package com.ooyala.android.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

public class OrderedMap<K, V extends OrderedMapValue<K>> implements Iterable<V> {
  private List<V> _array = new ArrayList<V>();
  private Map<K, Integer> _keyToIndex = new HashMap<K, Integer>();
  private Map<K, V> _map = new HashMap<K, V>();

  private void recomputeIndicies() {
    int i = 0;
    _keyToIndex.clear();
    for (V e : _array) {
      _keyToIndex.put(e.getKey(), Integer.valueOf(i++));
    }
  }

  private int verifyIndex(int index) throws ArrayIndexOutOfBoundsException {
    if (index >= _array.size() || index < 0) { throw new ArrayIndexOutOfBoundsException(index); }
    return index;
  }

  public int indexForKey(K key) {
    Integer idx = _keyToIndex.get(key);
    return idx == null ? -1 : idx;
  }

  public int indexForValue(V val) {
    Integer idx = _keyToIndex.get(val.getKey());
    return idx == null ? -1 : idx;
  }

  public boolean add(V e) {
    _map.put(e.getKey(), e);
    _keyToIndex.put(e.getKey(), Integer.valueOf(_array.size()));
    _array.add(e);
    return true;
  }

  public void add(int index, V element) {
    _array.add(index, element);
    _map.put(element.getKey(), element);
    recomputeIndicies();
  }

  public boolean addAll(Collection<? extends V> c) {
    _array.addAll(c);
    for (V v : c) {
      _map.put(v.getKey(), v);
    }
    recomputeIndicies();
    return true;
  }

  public boolean addAll(int index, Collection<? extends V> c) {
    _array.addAll(index, c);
    for (V v : c) {
      _map.put(v.getKey(), v);
    }
    recomputeIndicies();
    return true;
  }

  public boolean contains(V v) {
    return _array.contains(v);
  }

  public boolean containsAll(Collection<V> c) {
    return _array.containsAll(c);
  }

  public V get(int index) {
    return _array.get(index);
  }

  public int indexOf(V v) {
    return _array.indexOf(v);
  }

  public Iterator<V> iterator() {
    return _array.iterator();
  }

  public int lastIndexOf(V v) {
    return _array.lastIndexOf(v);
  }

  public ListIterator<V> listIterator() {
    return _array.listIterator();
  }

  public ListIterator<V> listIterator(int index) {
    return _array.listIterator(index);
  }

  public V remove(int index) {
    int verifiedIdx = verifyIndex(index);
    V removed = _array.remove(verifiedIdx);
    _map.remove(removed.getKey());
    _keyToIndex.remove(removed.getKey());
    recomputeIndicies();
    return removed;
  }

  public V set(int index, V element) {
    V removed = remove(index);
    add(index, element);
    return removed;
  }

  public List<V> subList(int fromIndex, int toIndex) {
    return _array.subList(fromIndex, toIndex);
  }

  public Object[] toArray() {
    return _array.toArray();
  }

  public <T> T[] toArray(T[] a) {
    return _array.toArray(a);
  }

  public void clear() {
    _map.clear();
    _array.clear();
    _keyToIndex.clear();
  }

  public boolean containsKey(K key) {
    return _map.containsKey(key);
  }

  public boolean containsValue(V value) {
    return _map.containsValue(value);
  }

  public Set<java.util.Map.Entry<K, V>> entrySet() {
    return _map.entrySet();
  }

  public V get(K key) {
    return _map.get(key);
  }

  public boolean isEmpty() {
    return _array.isEmpty();
  }

  public Set<K> keySet() {
    return _map.keySet();
  }

  public V put(K key, V value) {
    _keyToIndex.put(key, _array.size());
    _array.add(value);
    return _map.put(key, value);
  }

  public V remove(K key) {
    V removed = _map.remove(key);
    _array.remove(removed);
    _keyToIndex.remove(key);
    recomputeIndicies();
    return removed;
  }

  public V remove(V value) {
    V removed = _map.remove(value.getKey());
    _array.remove(value);
    _keyToIndex.remove(value.getKey());
    return removed;
  }

  public int size() {
    return _array.size();
  }

  public Collection<V> values() {
    return _map.values();
  }
}
