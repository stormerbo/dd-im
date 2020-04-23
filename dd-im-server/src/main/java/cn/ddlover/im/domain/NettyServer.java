package cn.ddlover.im.domain;

import cn.ddlover.im.business.HeartBeatRequestHandler;
import cn.ddlover.im.business.IMIdleStateHandler;
import cn.ddlover.im.business.LoginHandler;
import cn.ddlover.im.business.ServerMessageHandler;
import cn.ddlover.im.encode.ProtostuffDecoder;
import cn.ddlover.im.encode.ProtostuffEncoder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/4/23 22:12
 */
@Slf4j
public class NettyServer {

  private ServerBootstrap serverBootstrap;
  private int port;

  public NettyServer(int port) {
    this.port = port;
    init();
  }


  private void init() {
    NioEventLoopGroup bossGroup = new NioEventLoopGroup();
    NioEventLoopGroup childGroup = new NioEventLoopGroup();
    serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(bossGroup, childGroup)
        .channel(NioServerSocketChannel.class)
        .option(ChannelOption.SO_BACKLOG, 1024)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChildChannelHandler());
  }

  public void doBind() {
    ChannelFuture channelFuture = null;
    try {
      channelFuture = serverBootstrap.bind(port).sync();
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      log.error("服务器启动失败");
    }
  }

  private static class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
      socketChannel.pipeline().addLast(new IMIdleStateHandler());
      socketChannel.pipeline().addLast("frameDecoder", new LengthFieldBasedFrameDecoder(65535, 0, 2, 0, 2));
      socketChannel.pipeline().addLast("protostuff decoder", new ProtostuffDecoder());
      socketChannel.pipeline().addLast("frameEncoder", new LengthFieldPrepender(2));
      socketChannel.pipeline().addLast("protostuff encoder", new ProtostuffEncoder());
      socketChannel.pipeline().addLast(new LoginHandler());
      socketChannel.pipeline().addLast(new ServerMessageHandler());
      socketChannel.pipeline().addLast(new HeartBeatRequestHandler());
    }
  }

}
