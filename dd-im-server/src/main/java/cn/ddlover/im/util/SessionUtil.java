package cn.ddlover.im.util;

import cn.ddlover.im.entity.User;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/17 11:40
 */
public class SessionUtil {
  private static final Map<String, User> SESSION_MAP = new ConcurrentHashMap<>();

  public static void register(User user) {
    SESSION_MAP.put(user.getToken(), user);
  }

  public static User queryByToken(String token) {
    return SESSION_MAP.get(token);
  }

}
