package cn.ddlover.im;

import cn.ddlover.im.business.ClientMessageHandler;
import cn.ddlover.im.business.HeartBeatTimerHandler;
import cn.ddlover.im.business.LoginHandler;
import cn.ddlover.im.encode.ProtostuffDecoder;
import cn.ddlover.im.encode.ProtostuffEncoder;
import cn.ddlover.im.entity.RpcHeader;
import cn.ddlover.im.entity.RpcMessage;
import cn.ddlover.im.entity.RpcMessageType;
import cn.ddlover.im.util.SessionUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.util.internal.StringUtil;
import java.util.Scanner;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/15 17:45
 */
public class Client {

  private static final String HOST = "192.168.32.120";
  private static final int PORT = 8080;

  private static boolean doBind(String host, int port) {
    EventLoopGroup group = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(group).channel(NioSocketChannel.class)
          .option(ChannelOption.TCP_NODELAY, true)
          .handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel socketChannel) throws Exception {
              socketChannel.pipeline().addLast(new HeartBeatTimerHandler());
              socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
              socketChannel.pipeline().addLast("protostuff decoder", new ProtostuffDecoder());
              socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
              socketChannel.pipeline().addLast("protostuff encoder", new ProtostuffEncoder());
              socketChannel.pipeline().addLast(new LoginHandler());
              socketChannel.pipeline().addLast(new ClientMessageHandler());
            }
          });

      connect(bootstrap, host, port, 3);
      return true;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return false;
  }

  public static void main(String[] args) {
    doBind(HOST, PORT);
  }

  private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
    bootstrap.connect(host, port).addListener(future -> {
      if (future.isSuccess()) {
        Channel channel = ((ChannelFuture) future).channel();
        // 连接成功之后，启动控制台线程
        startConsoleThread(channel);
      }
    });
  }

  private static void startConsoleThread(Channel channel) {
    new Thread(() -> {
      while (!Thread.interrupted()) {
        String token = SessionUtil.getCurrentToken();
        if (!StringUtil.isNullOrEmpty(token)) {
          System.out.println("输入消息发送至服务端: ");
          Scanner sc = new Scanner(System.in);
          String line = sc.nextLine();
          RpcMessage<String> rpcMessage = new RpcMessage<>();
          RpcHeader rpcHeader = new RpcHeader();
          rpcHeader.setType(RpcMessageType.MESSAGE_REQUEST.getType());
          rpcMessage.setRpcHeader(rpcHeader);
          rpcMessage.setData(line);
          channel.writeAndFlush(rpcMessage);
        }
      }
    }).start();
  }

}
