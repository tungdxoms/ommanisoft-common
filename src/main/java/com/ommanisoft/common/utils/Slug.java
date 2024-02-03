package com.ommanisoft.common.utils;

public class Slug {
  public static String makeSlug(String input) {
    if (input == null) {
      return "";
    }
    if (input.charAt(0) == ' ') input = input.replace(" ", "");
    // Loại bỏ dấu tiếng Việt
    String slug = input.toLowerCase().replaceAll("[àáảãạăắằẳẵặâấầẩẫậ]", "a")
      .replaceAll("[đ]", "d")
      .replaceAll("[èéẻẽẹêềếểễệ]", "e")
      .replaceAll("[ìíỉĩị]", "i")
      .replaceAll("[òóỏõọôồốổỗộơờớởỡợ]", "o")
      .replaceAll("[ùúủũụưừứửữự]", "u")
      .replaceAll("[ỳýỷỹỵ]", "y");

    // Bỏ ký tự đặc biệt, thay thế khoảng trắng bằng dấu gạch ngang
    slug = slug.replaceAll("[^a-zA-Z0-9\\s]+", "").replaceAll("\\s+", "-");

    return slug;
  }
}
