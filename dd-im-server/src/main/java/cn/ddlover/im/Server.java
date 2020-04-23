package cn.ddlover.im;

import cn.ddlover.im.domain.NettyServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 17:44
 */
@Slf4j
public class Server {

  private static final Integer PORT = 8080;
  public static void main(String[] args) {
    NettyServer nettyServer = new NettyServer(PORT);
    nettyServer.doBind();
  }

}
