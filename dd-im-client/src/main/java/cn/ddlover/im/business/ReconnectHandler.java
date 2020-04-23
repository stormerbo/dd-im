package cn.ddlover.im.business;

import cn.ddlover.im.domain.NettyClient;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.EventLoop;
import java.util.concurrent.TimeUnit;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/23 22:27
 */
@Sharable
public class ReconnectHandler extends ChannelInboundHandlerAdapter {

  private int retries = 0;
  private ReconnectPolicy reconnectPolicy;

  private NettyClient nettyClient;

  public ReconnectHandler(NettyClient nettyClient) {
    this.nettyClient = nettyClient;
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("Successfully established a connection to the server.");
    retries = 0;
    ctx.fireChannelActive();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    if (retries == 0) {
      System.err.println("Lost the TCP connection with the server.");
      ctx.close();
    }

    boolean allowRetry = getRetryPolicy().allowRetry(retries);
    if (allowRetry) {

      long sleepTimeMs = getRetryPolicy().getSleepTimeMs(retries);

      System.out.println(String.format("Try to reconnect to the server after %dms. Retry count: %d.", sleepTimeMs, ++retries));

      final EventLoop eventLoop = ctx.channel().eventLoop();
      eventLoop.schedule(() -> {
        System.out.println("Reconnecting ...");
        nettyClient.connect();
      }, sleepTimeMs, TimeUnit.MILLISECONDS);
      ctx.fireChannelInactive();
    } else {
      System.err.println("reconnect failed after " + retries + "times");
      ctx.close();
    }
  }


  private ReconnectPolicy getRetryPolicy() {
    if (this.reconnectPolicy == null) {
      this.reconnectPolicy = nettyClient.getReconnectPolicy();
    }
    return this.reconnectPolicy;
  }
}
