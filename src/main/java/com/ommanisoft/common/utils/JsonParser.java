package com.ommanisoft.common.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class JsonParser {
  private static ObjectMapper mObjectMapper;

  /**
   * Creates an {@link ObjectMapper} for mapping json objects. Mapper can be configured here
   *
   * @return created {@link ObjectMapper}
   */
  private static ObjectMapper getMapper() {

    if (mObjectMapper == null) {
      mObjectMapper = new ObjectMapper();
      mObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
    return mObjectMapper;
  }

  /**
   * Maps json string to specified class
   *
   * @param json   string to parse
   * @param tClass class of object in which json will be parsed
   * @param <T>    generic parameter for tClass
   * @return mapped T class instance
   */
  public static <T> T entity(String json, Class<T> tClass) {
    try {
      return getMapper().readValue(json, tClass);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static List setToList(Set param) {
    List list = new ArrayList<>();
    list.addAll(param);

    return list;
  }

  /**
   * Maps json string to {@link ArrayList} of specified class object instances
   *
   * @param json   string to parse
   * @param tClass class of object in which json will be parsed
   * @param <T>    generic parameter for tClass
   * @return mapped T class instance
   */
  public static <T> ArrayList<T> arrayList(String json, Class<T> tClass) {
    try {
      TypeFactory typeFactory = getMapper().getTypeFactory();
      JavaType type = typeFactory.constructCollectionType(ArrayList.class, tClass);
      return getMapper().readValue(json, type);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Writes specified object as string
   *
   * @param object object to write
   * @return result json
   */
  public static String toJson(Object object) {
    try {
      return getMapper().writeValueAsString(object);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Convert int[] to ArrayList<Integer></>
   *
   * @param ints
   * @return
   */
  public static ArrayList<Integer> intArrayList(int[] ints) {
    return IntStream.of(ints).boxed().collect(Collectors.toCollection(ArrayList::new));
  }

  /**
   * Convert ArrayList<Integer></> to int[]
   *
   * @param list
   * @return
   */
  public static int[] listToIntArray(List<Integer> list) {
    return list.stream().mapToInt(i -> i).toArray();
  }

  /**
   * @param object
   * @return
   */
  public static Map<String, Object> objectToMap(Object object) {
    MultiValueMap parameters = new LinkedMultiValueMap();
    Map<String, String> maps =
      getMapper().convertValue(object, new TypeReference<Map<String, String>>() {
      });
    parameters.setAll(maps);
    return parameters;
  }

  /*  public static MultiValueMap<String, String> objectToMapSnackCase(Object object) {
      MultiValueMap parameters = new LinkedMultiValueMap();
      Map<String, String> maps =
          getMapper().convertValue(object, new TypeReference<Map<String, String>>() {});

      Map<String, String> mapSnack = new HashMap<>();
      for (Map.Entry<String, String> entry : maps.entrySet()) {
        mapSnack.put(CaseUtil.toSnack(entry.getKey()), entry.getValue());
      }
      parameters.setAll(mapSnack);
      return parameters;
    }

    public static <T> T xmlToJson(String xmlData, Class<T> clazz) {
      try {
        XmlMapper xmlMapper = new XmlMapper();
        return xmlMapper.readValue(xmlData, clazz);
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

  private static String toSnack(String camelCase) {
      return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, camelCase);
  }*/

  public static String mapToQueryStringUTF8(Map<String, Object> map) {

    try {
      List fieldNames = new ArrayList(map.keySet());
      Collections.sort(fieldNames);
      StringBuilder stringBuilder = new StringBuilder();
      Iterator itr = fieldNames.iterator();
      while (itr.hasNext()) {
        String fieldName = (String) itr.next();
        Object fieldValue = map.get(fieldName);
        if ((fieldValue != null)) {
          stringBuilder.append(fieldName);
          stringBuilder.append('=');
          stringBuilder.append(URLDecoder.decode(String.valueOf(fieldValue), "UTF-8"));
          if (itr.hasNext()) stringBuilder.append('&');
        }
      }

      return stringBuilder.toString();
    } catch (Exception e) {
      return null;
    }
  }

  public static String mapToQueryString(Map<String, String> map) {
    try {
      List fieldNames = new ArrayList(map.keySet());
      Collections.sort(fieldNames);
      StringBuilder stringBuilder = new StringBuilder();
      Iterator itr = fieldNames.iterator();
      while (itr.hasNext()) {
        String fieldName = (String) itr.next();
        String fieldValue = map.get(fieldName);
        if ((fieldValue != null) && (fieldValue.length() > 0)) {
          stringBuilder.append(fieldName);
          stringBuilder.append('=');
          stringBuilder.append(fieldValue);
          if (itr.hasNext()) stringBuilder.append('&');
        }
      }

      return stringBuilder.toString();
    } catch (Exception e) {
      return null;
    }
  }
}
