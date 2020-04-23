package cn.ddlover.im.util;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/16 10:34
 */
public class SessionUtil {

  private static String TOKEN;


  public static String getCurrentToken() {
    return TOKEN;
  }

  public static void setToken(String token) {
    TOKEN = token;
  }
}
