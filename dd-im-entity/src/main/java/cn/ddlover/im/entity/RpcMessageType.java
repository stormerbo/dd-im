package cn.ddlover.im.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 18:11
 */
@Getter
@AllArgsConstructor
public enum RpcMessageType {
  HEART_BEAT_REQUEST(0),
  HEART_BEAT_RESPONSE(1),
  LOGIN_REQUEST(2),
  LOGIN_RESPONSE(3),
  MESSAGE_REQUEST(4),
  MESSAGE_RESPONSE(5);

  private Integer type;
}
