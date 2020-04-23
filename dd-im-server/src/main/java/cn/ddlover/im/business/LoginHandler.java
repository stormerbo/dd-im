package cn.ddlover.im.business;

import cn.ddlover.im.entity.RpcHeader;
import cn.ddlover.im.entity.RpcMessage;
import cn.ddlover.im.entity.RpcMessageType;
import cn.ddlover.im.entity.RpcResponse;
import cn.ddlover.im.entity.login.LoginRequest;
import cn.ddlover.im.entity.User;
import cn.ddlover.im.util.ChannelUtil;
import cn.ddlover.im.util.LoginUtil;
import cn.ddlover.im.util.SessionUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Objects;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 18:03
 */
@Slf4j
public class LoginHandler extends ChannelInboundHandlerAdapter {

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    RpcMessage rpcMessage = (RpcMessage) msg;
    if (rpcMessage.getRpcHeader().getType().equals(RpcMessageType.LOGIN_REQUEST.getType())) {
      RpcMessage<LoginRequest> loginMessage = (RpcMessage<LoginRequest>) msg;
      User user = doLogin(loginMessage.getData());
      if(Objects.nonNull(user)) {
        LoginUtil.markAsLogin(ctx.channel());
        ChannelUtil.register(user.getToken(), ctx.channel());
        SessionUtil.register(user);
      }
      ctx.writeAndFlush(buildResponse(user));
    } else {
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

  public static User doLogin(LoginRequest loginRequest) {
    String userName = loginRequest.getUserName();
    String password = loginRequest.getPassword();
    if (userName.equals("user") && password.equals("password")) {
      String token = UUID.randomUUID().toString();
      token = token.replaceAll("-", "");
      User user = new User(1, "user", token);
      log.info("用户[" + user.getName() + "]登录成功！");
      return user;
    } else {
      return null;
    }
  }

  private RpcMessage<RpcResponse<User>> buildResponse(User user) {
    RpcMessage<RpcResponse<User>> response = new RpcMessage<>();
    RpcHeader rpcHeader = new RpcHeader();
    rpcHeader.setType(RpcMessageType.LOGIN_RESPONSE.getType());
    response.setRpcHeader(rpcHeader);
    RpcResponse<User> rpcResponse = new RpcResponse<>();
    if (Objects.isNull(user)) {
      rpcResponse.setCode(-1);
      rpcResponse.setMessage("用户名密码错误");
    } else {
      rpcResponse.setCode(0);
      rpcResponse.setMessage("success");
      rpcResponse.setData(user);
    }
    response.setData(rpcResponse);
    return response;
  }


}
