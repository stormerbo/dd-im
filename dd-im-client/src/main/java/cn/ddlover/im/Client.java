package cn.ddlover.im;

import cn.ddlover.im.business.ExponentialBackOffRetry;
import cn.ddlover.im.domain.NettyClient;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 17:45
 */
public class Client {

  private static final String HOST = "192.168.1.156";
  private static final int PORT = 8080;


  public static void main(String[] args) {
    NettyClient nettyClient = new NettyClient(HOST, PORT, new ExponentialBackOffRetry(5, 5));
    nettyClient.connect();
  }

}
