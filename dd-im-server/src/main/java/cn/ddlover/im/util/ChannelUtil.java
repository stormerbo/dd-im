package cn.ddlover.im.util;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/16 14:49
 */
public class ChannelUtil {
  private static final Map<String, Channel> USER_CHANNEL = new ConcurrentHashMap<>();

  public static void register(String token, Channel channel) {
    USER_CHANNEL.put(token, channel);
  }

  public static Channel getUserChannel(String token) {
    return USER_CHANNEL.get(token);
  }

  public static List<Channel> getAllChannel() {
    return new ArrayList<>(USER_CHANNEL.values());
  }
}
