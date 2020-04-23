package cn.ddlover.im.entity;

import io.netty.util.AttributeKey;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/16 11:43
 */
public interface Attributes {
  AttributeKey<Boolean> LOGIN = AttributeKey.newInstance("login");
}
