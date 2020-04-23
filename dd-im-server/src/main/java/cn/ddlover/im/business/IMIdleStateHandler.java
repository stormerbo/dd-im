package cn.ddlover.im.business;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/23 15:01
 */
public class IMIdleStateHandler extends IdleStateHandler {

  private static final int READER_IDLE_TIME = 15;

  public IMIdleStateHandler() {
    super(READER_IDLE_TIME, 0, 0, TimeUnit.SECONDS);
  }

  @Override
  protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
    System.out.println(READER_IDLE_TIME + "秒未读到数据，关闭连接");
    ctx.channel().close();
  }
}
