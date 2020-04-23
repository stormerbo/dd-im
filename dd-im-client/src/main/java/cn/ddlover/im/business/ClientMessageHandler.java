package cn.ddlover.im.business;

import cn.ddlover.im.entity.RpcMessage;
import cn.ddlover.im.entity.RpcMessageType;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/17 14:10
 */
public class ClientMessageHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    RpcMessage rpcMessage = (RpcMessage) msg;
    if (rpcMessage.getRpcHeader().getType().equals(RpcMessageType.MESSAGE_RESPONSE.getType())) {
      RpcMessage<String> resp = (RpcMessage<String>) rpcMessage;
      System.out.println("收到信息: " + resp.getData());
    }
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}
