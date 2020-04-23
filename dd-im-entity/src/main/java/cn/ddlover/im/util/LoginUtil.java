package cn.ddlover.im.util;

import cn.ddlover.im.entity.Attributes;
import io.netty.channel.Channel;
import io.netty.util.Attribute;
import java.util.Objects;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/16 11:44
 */
public class LoginUtil {

  public static void markAsLogin(Channel channel) {
    channel.attr(Attributes.LOGIN);
  }

  public static boolean hasLogin(Channel channel) {
    Attribute<Boolean> loginAttr = channel.attr(Attributes.LOGIN);
    return Objects.nonNull(loginAttr.get());
  }
}