package cn.ddlover.im.encode;

import cn.ddlover.im.entity.Attributes;
import cn.ddlover.im.entity.RpcMessage;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.Attribute;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import java.util.Objects;

/**
 * @author stormer.xia
 * @version 1.0
 * @date 2020/3/25 16:51
 */
public class ProtostuffEncoder extends MessageToByteEncoder<RpcMessage> {

  @Override
  protected void encode(ChannelHandlerContext channelHandlerContext, RpcMessage wrapper, ByteBuf byteBuf) throws Exception {
    Schema<RpcMessage> schema = RuntimeSchema.getSchema(RpcMessage.class);

    LinkedBuffer linkedBuffer = LinkedBuffer.allocate(512);
    final byte[] protostuff;
    try {
      protostuff = ProtobufIOUtil.toByteArray(wrapper, schema, linkedBuffer);
    } finally {
      linkedBuffer.clear();
    }
    byteBuf.writeBytes(protostuff);
  }


}
