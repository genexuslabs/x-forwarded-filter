/*
 * Copyright 2002-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.util;

import java.io.Serializable;
import java.util.*;

/**
 * Simple implementation of {@link MultiValueMap} that wraps a {@link LinkedHashMap},
 * storing multiple values in a {@link LinkedList}.
 * <p>
 * <p>This Map implementation is generally not thread-safe. It is primarily designed
 * for data structures exposed from request objects, for use in a single thread only.
 *
 * @author Arjen Poutsma
 * @author Juergen Hoeller
 * @since 3.0
 */

public class LinkedMultiValueMap<K, V> implements MultiValueMap<K, V>, Serializable {

	private static final long serialVersionUID = 3801124242820219131L;

	@SuppressWarnings("squid:S1948")//serializable
	private final Map<K, List<V>> targetMap;


	/**
	 * Create a new LinkedMultiValueMap that wraps a {@link LinkedHashMap}.
	 */
	public LinkedMultiValueMap() {
		this.targetMap = new LinkedHashMap<>();
	}

	/**
	 * Create a new LinkedMultiValueMap that wraps a {@link LinkedHashMap}
	 * with the given initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 */
	public LinkedMultiValueMap(int initialCapacity) {
		this.targetMap = new LinkedHashMap<>(initialCapacity);
	}

	/**
	 * Copy constructor: Create a new LinkedMultiValueMap with the same mappings as
	 * the specified Map. Note that this will be a shallow copy; its value-holding
	 * List entries will get reused and therefore cannot get modified independently.
	 *
	 * @param otherMap the Map whose mappings are to be placed in this Map
	 * @see #clone()
	 */
	public LinkedMultiValueMap(Map<K, List<V>> otherMap) {
		this.targetMap = new LinkedHashMap<>(otherMap);
	}


	// MultiValueMap implementation

	@Override
	public void add(K key, /*@Nullable*/ V value) {
		List<V> values = this.targetMap.computeIfAbsent(key, k -> new LinkedList<>());
		values.add(value);
	}


	// Map implementation

	@Override
	public int size() {
		return this.targetMap.size();
	}

	@Override
	public boolean isEmpty() {
		return this.targetMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return this.targetMap.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return this.targetMap.containsValue(value);
	}

	@Override
	/*@Nullable*/
	public List<V> get(Object key) {
		return this.targetMap.get(key);
	}

	@Override
	/*@Nullable*/
	public List<V> put(K key, List<V> value) {
		return this.targetMap.put(key, value);
	}

	@Override
	/*@Nullable*/
	public List<V> remove(Object key) {
		return this.targetMap.remove(key);
	}

	@Override
	public void putAll(Map<? extends K, ? extends List<V>> map) {
		this.targetMap.putAll(map);
	}

	@Override
	public void clear() {
		this.targetMap.clear();
	}

	@Override
	public Set<K> keySet() {
		return this.targetMap.keySet();
	}

	@Override
	public Collection<List<V>> values() {
		return this.targetMap.values();
	}

	@Override
	public Set<Entry<K, List<V>>> entrySet() {
		return this.targetMap.entrySet();
	}


	@Override
	public boolean equals(Object obj) {
		return this.targetMap.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.targetMap.hashCode();
	}

	@Override
	public String toString() {
		return this.targetMap.toString();
	}

}