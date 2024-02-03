package com.ommanisoft.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ommanisoft.common.exceptions.ExceptionOm;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.http.HttpStatus;

import javax.persistence.Tuple;
import java.beans.FeatureDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Stream;

public class FnCommon {
  public static boolean emailValidate(String email) {
    return true;
  }

  public static void copyProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
//    BeanUtils.copyProperties(source, target);
  }

  public static void coppyNonNullProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static List<?> listOrEmptyList(List<?> list) {
    return list == null ? new ArrayList<>() : list;
  }

  public static List<?> convertToEntity(List<Tuple> input, Class<?> dtoClass) {
    List<Object> arrayList = new ArrayList();
    input.stream()
      .forEach(
        (tuple) -> {
          Map<String, Object> temp = new HashMap();
          tuple.getElements().stream()
            .forEach(
              (tupleElement) -> {
                Object value = tuple.get(tupleElement.getAlias());
                temp.put(tupleElement.getAlias().toLowerCase(), value);
              });
          ObjectMapper map = new ObjectMapper();
          map.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
          map.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

          try {
            String mapToString = map.writeValueAsString(temp);
            arrayList.add(map.readValue(mapToString, dtoClass));
          } catch (JsonProcessingException var6) {
            throw new RuntimeException(var6.getMessage());
          }
        });
    return arrayList;
  }

  public static <T> List<Map<String, Object>> tupleToListMap(List<Tuple> input, Map<String, Class<T>> jsonColumns) {
    List<Map<String, Object>> arrayList = new ArrayList();
    input.stream()
      .forEach(
        (tuple) -> {
          Map<String, Object> temp = new HashMap();
          tuple.getElements().stream()
            .forEach(
              (tupleElement) -> {
                Object value = tuple.get(tupleElement.getAlias());
                if (jsonColumns.containsKey(tupleElement.getAlias().toLowerCase())) {
                  value = JsonParser.entity((String) value, jsonColumns.get(tupleElement.getAlias().toLowerCase()));
                }
                temp.put(tupleElement.getAlias().toLowerCase(), value);
              });
          arrayList.add(temp);
        });
    return arrayList;
  }

  public static <T> T copyProperties(Class<T> clazz, Object source) {
    try {
      Constructor<?> targetIntance = clazz.getDeclaredConstructor();
      targetIntance.setAccessible(true);

      T target = (T) targetIntance.newInstance();
      BeanUtils.copyProperties(source, target, getNullPropertyNames(source));

      return target;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static <T> T copyNonNullProperties(Class<T> clazz, Object source) {
    try {
      Constructor<?> targetIntance = clazz.getDeclaredConstructor();
      targetIntance.setAccessible(true);

      T target = (T) targetIntance.newInstance();
      BeanUtils.copyProperties(source, target, getNullPropertyNames(source));

      return target;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static void copyNonNullProperties(Object target, Object source) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  public static String[] getNullPropertyNames(Object source) {
    BeanWrapper wrappedSource = new BeanWrapperImpl(source);
    return Stream.of(wrappedSource.getPropertyDescriptors())
      .map(FeatureDescriptor::getName)
      .filter(propertyName -> wrappedSource.getPropertyValue(propertyName) == null)
      .toArray(String[]::new);
  }

  public static <T> boolean isEmpty(List<T> list) {
    return list == null || list.size() == 0;
  }

  public static boolean checkBlankString(String str) {
    return str == null || str.isEmpty();
  }

  public static Map<String, Object> parameters(Object obj) {
    Map<String, Object> map = new HashMap<>();
    for (Field field : obj.getClass().getDeclaredFields()) {
      field.setAccessible(true);
      try {
        map.put(field.getName(), field.get(obj));
      } catch (Exception e) {
      }
    }
    return map;
  }

  public static <V, K> Map<K, List<V>> convertListToMap(List<V> list, Function<V, K> keyExtractor) {
    Map<K, List<V>> result = new HashMap<>();
    for (V v : list) {
      K key = keyExtractor.apply(v);
      result.computeIfAbsent(key, k -> new ArrayList<>()).add(v);
    }
    return result;
  }

  public static String randomCode(String prefix, int randomLen) {
    Random random = new Random();
    return prefix
      + random
      .ints(97, 123)
      .limit(randomLen)
      .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
      .toString()
      .toUpperCase();
  }

  public static String randomNumberCode(String prefix, int leng) {
    String minStr = "10000000000000000";
    String maxStr = "99999999999999999";
    int min = Integer.parseInt(minStr.substring(0, leng));
    int max = Integer.parseInt(maxStr.substring(0, leng));
    return prefix + (int)((Math.random() * (max - min)) + min);
  }

  public static String gencode(String prefix, int lenght, long order) {
    String codeDefault = "000000000000000000";

    return prefix + codeDefault.substring(0, lenght - (order + "").length()) + order;
  }

  public static <T extends Enum<T>> T getEnumValueFromString(Class<T> enumType, String name) {
    Enum[] arr$ = (Enum[]) enumType.getEnumConstants();
    int len$ = arr$.length;

    for (int i$ = 0; i$ < len$; ++i$) {
      T constant = (T) arr$[i$];
      if (constant.name().compareToIgnoreCase(name) == 0) {
        return constant;
      }
    }

    throw new ExceptionOm(HttpStatus.BAD_REQUEST, "Invalid state");
  }
}
