package cn.ddlover.im.business;

import cn.ddlover.im.entity.RpcHeader;
import cn.ddlover.im.entity.RpcMessage;
import cn.ddlover.im.entity.RpcMessageType;
import cn.ddlover.im.entity.RpcResponse;
import cn.ddlover.im.entity.login.LoginRequest;
import cn.ddlover.im.entity.User;
import cn.ddlover.im.util.SessionUtil;
import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.internal.StringUtil;
import java.util.Scanner;
import lombok.extern.slf4j.Slf4j;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/16 10:33
 */
@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    if (StringUtil.isNullOrEmpty(SessionUtil.getCurrentToken())) {
      Scanner scanner = new Scanner(System.in);
      System.out.println("需要登录， 清输入用户名和密码");
      String userName = scanner.nextLine();
      String password = scanner.nextLine();
      RpcMessage<LoginRequest> loginRequest = buildLoginRequest(userName, password);
      ctx.writeAndFlush(loginRequest);
    }else {
      ctx.fireChannelActive();
    }
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    RpcMessage<RpcResponse> rpcMessage = (RpcMessage<RpcResponse>) msg;
    if (rpcMessage.getRpcHeader().getType().equals(RpcMessageType.LOGIN_RESPONSE.getType())) {
      RpcResponse<User> rpcResponse = rpcMessage.getData();
      if(rpcResponse.getCode() == 0) {
        log.info("登录成功！当前信息:["+ JSON.toJSONString(rpcResponse.getData())+"]");
        User user = rpcResponse.getData();
        SessionUtil.setToken(user.getToken());
      }else {
        log.error("登陆失败！"+rpcResponse.getMessage());
      }
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

  private RpcMessage<LoginRequest> buildLoginRequest(String userName, String password) {
    RpcMessage<LoginRequest> response = new RpcMessage<>();
    RpcHeader rpcHeader = new RpcHeader();
    rpcHeader.setType(RpcMessageType.LOGIN_REQUEST.getType());
    response.setRpcHeader(rpcHeader);
    LoginRequest loginRequest = new LoginRequest(userName, password);
    response.setData(loginRequest);
    return response;
  }
}
