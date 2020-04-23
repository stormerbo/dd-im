package cn.ddlover.im.business;

import cn.ddlover.im.entity.RpcHeader;
import cn.ddlover.im.entity.RpcMessage;
import cn.ddlover.im.entity.RpcMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/23 15:12
 */
@Slf4j
public class HeartBeatTimerHandler extends ChannelInboundHandlerAdapter {

  private static final int HEARTBEAT_INTERVAL = 5;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    scheduleSendHeartBeat(ctx);
    super.channelActive(ctx);
  }

  private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
    ctx.executor().schedule(() -> {
      if (ctx.channel().isActive()) {
        log.info("开始发送心跳");
        ctx.writeAndFlush(buildHeartBeatRequest());
        scheduleSendHeartBeat(ctx);
      }

    }, HEARTBEAT_INTERVAL, TimeUnit.SECONDS);
  }

  private RpcMessage<Void> buildHeartBeatRequest() {
    RpcMessage<Void> response = new RpcMessage<>();
    RpcHeader rpcHeader = new RpcHeader();
    rpcHeader.setType(RpcMessageType.HEART_BEAT_REQUEST.getType());
    response.setRpcHeader(rpcHeader);
    return response;
  }
}
