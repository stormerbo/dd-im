package cn.ddlover.im.domain;

import cn.ddlover.im.business.ClientMessageHandler;
import cn.ddlover.im.business.HeartBeatTimerHandler;
import cn.ddlover.im.business.LoginHandler;
import cn.ddlover.im.business.ReconnectHandler;
import cn.ddlover.im.business.ReconnectPolicy;
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
 * @date 2020/4/23 21:55
 */
public class NettyClient {

  private String host;
  private int port;
  private Bootstrap bootstrap;
  private Channel channel;
  private ReconnectPolicy reconnectPolicy;

  public ReconnectPolicy getReconnectPolicy() {
    return reconnectPolicy;
  }

  public NettyClient(String host, int port, ReconnectPolicy reconnectPolicy) {
    this.host = host;
    this.port = port;
    this.reconnectPolicy = reconnectPolicy;
    init();
  }

  private void init() {
    EventLoopGroup group = new NioEventLoopGroup();
    // bootstrap 可重用, 只需在TcpClient实例化的时候初始化即可.
    bootstrap = new Bootstrap();
    bootstrap.group(group)
        .channel(NioSocketChannel.class)
        .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 60*1000)
        .handler(new ChildChannelHandler(NettyClient.this));
  }

  /**
   * 向远程TCP服务器请求连接
   */
  public void connect() {
    synchronized (bootstrap) {
      bootstrap.connect(host, port).addListener(future -> {
        if (future.isSuccess()) {
          this.channel = ((ChannelFuture) future).channel();
          // 连接成功之后，启动控制台线程
          startConsoleThread(this.channel);
        }else {
          ((ChannelFuture) future).channel().pipeline().fireChannelInactive();
        }
      });
    }
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

  private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    private NettyClient nettyClient;
    private ReconnectHandler reconnectHandler;

    public ChildChannelHandler(NettyClient nettyClient) {
      this.nettyClient = nettyClient;
      this.reconnectHandler = new ReconnectHandler(nettyClient);
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
      socketChannel.pipeline().addLast(this.reconnectHandler);
      socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
      socketChannel.pipeline().addLast("protostuff decoder", new ProtostuffDecoder());
      socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
      socketChannel.pipeline().addLast("protostuff encoder", new ProtostuffEncoder());
      socketChannel.pipeline().addLast(new HeartBeatTimerHandler());
      socketChannel.pipeline().addLast(new LoginHandler());
      socketChannel.pipeline().addLast(new ClientMessageHandler());
    }
  }
}
