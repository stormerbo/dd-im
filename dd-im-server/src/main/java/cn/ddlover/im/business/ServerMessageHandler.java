package cn.ddlover.im.business;

import cn.ddlover.im.entity.RpcHeader;
import cn.ddlover.im.entity.RpcMessage;
import cn.ddlover.im.entity.RpcMessageType;
import cn.ddlover.im.util.ChannelUtil;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 18:00
 */
@Slf4j
public class ServerMessageHandler extends ChannelInboundHandlerAdapter {


  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    RpcMessage rpcMessage = (RpcMessage) msg;
    if (rpcMessage.getRpcHeader().getType().equals(RpcMessageType.MESSAGE_REQUEST.getType())) {
      String message = ((RpcMessage<String>)rpcMessage).getData();
      RpcMessage<String> resp = buildResponse(message);
      List<Channel> channelList = ChannelUtil.getAllChannel();
      channelList.forEach(channel -> {
        channel.writeAndFlush(resp);
      });
    }else {
      ctx.fireChannelRead(msg);
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

  private RpcMessage<String> buildResponse(String data) {
    RpcMessage<String> response = new RpcMessage<>();
    RpcHeader rpcHeader = new RpcHeader();
    rpcHeader.setType(RpcMessageType.MESSAGE_RESPONSE.getType());
    response.setRpcHeader(rpcHeader);
    response.setData(data);
    return response;
  }
}
