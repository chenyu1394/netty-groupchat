package com.chenyu.test.netty.group.client.handler;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.Scanner;

/**
 * 客户端IO事件处理类
 * @author ChenYu
 * */
public class GroupChatClientHandler extends SimpleChannelInboundHandler<String> {

    private String userName;
    public GroupChatClientHandler(String userName){
        this.userName = userName;
    }

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

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
      ctx.writeAndFlush(userName+"$_");
    }

}
