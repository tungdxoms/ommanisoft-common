package com.ommanisoft.common.utils;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CollectionHandle {
  public static <T> Set<T> findDuplicateInStream(Stream<T> stream) {

    // Set to store the duplicate elements
    Set<T> items = new HashSet<>();

    // Return the set of duplicate elements
    return stream.filter(n -> !items.add(n)).collect(Collectors.toSet());
  }
}
