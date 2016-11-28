package net.xinshi.discovery.search.client.util;

/**
 * Helper methods to ease implementing {@link Object#toString()}.
 */
public final class ToStringUtils {

  private ToStringUtils() {} // no instance

  /**
   * for printing boost only if not 1.0
   */
  public static String boost(float boost) {
    if (boost != 1.0f) {
      return "^" + Float.toString(boost);
    } else return "";
  }

  public static void byteArray(StringBuilder buffer, byte[] bytes) {
    for (int i = 0; i < bytes.length; i++) {
      buffer.append("b[").append(i).append("]=").append(bytes[i]);
      if (i < bytes.length - 1) {
        buffer.append(',');
      }

    }
  }

}
