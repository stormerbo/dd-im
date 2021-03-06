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
  private static final int CURRENT_RETRY_TIME = 1;

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    scheduleSendHeartBeat(ctx);
    super.channelActive(ctx);
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    RpcMessage rpcMessage = (RpcMessage) msg;
    if (rpcMessage.getRpcHeader().getType().equals(RpcMessageType.HEART_BEAT_RESPONSE.getType())) {
      log.info("收到心跳");
    } else {
      ctx.fireChannelRead(msg);
    }
  }

  private void scheduleSendHeartBeat(ChannelHandlerContext ctx) {
    ctx.executor().schedule(() -> {
      if (ctx.channel().isActive()) {
        log.info("开始发送心跳");
        ctx.channel().writeAndFlush(buildHeartBeatRequest());
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

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
