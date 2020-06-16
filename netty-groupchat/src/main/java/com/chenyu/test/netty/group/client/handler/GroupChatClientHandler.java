package com.chenyu.test.netty.group.client.handler;

import com.chenyu.test.netty.group.client.GroupChatClient;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 客户端IO事件处理类
 * @author ChenYu
 * */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

    /**
     * 客户端接收到消息
     * */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        //接收到消息，将消息打印输出
        System.out.println(msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
