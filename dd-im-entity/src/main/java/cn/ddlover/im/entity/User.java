package cn.ddlover.im.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 18:07
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

  private Integer id;
  private String name;
  private String token;
}
