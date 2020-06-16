package com.chenyu.test.netty.group.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 监听客户端channel的IO事件
 * @author ChenYu
 * */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
    /**
     * 定义一个channel 组，管理所有的channel
     * GlobalEventExecutor.INSTANCE) 是全局的事件执行器，是一个单例
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
    /**
     * 接收到消息事件
     * 将消息转发给其他客户端的socketChannel
     * */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        System.out.println(channel.remoteAddress()+"："+msg);
        //遍历channelGroup，若不是当前channel则转化消息
        for (Channel ch : channelGroup){
            if(ch != channel){
                ch.writeAndFlush(channel.remoteAddress() + "："+
                       msg +"   "+ DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            }
        }
        //回显自己发送的消息
        channel.writeAndFlush("我："+msg+"   "
                + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
    }

    /**
     * 连接建立事件
     * */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //该方法会将 channelGroup 中所有的channel 遍历，并发送 消息，我们不需要自己遍历
        channelGroup.writeAndFlush("[客户端]" + channel.remoteAddress() +
                " 加入聊天" + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        channelGroup.add(channel);
    }

    /**
     * 连接断开事件
     * */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //遍历channelGroup，若不是当前channel则转化消息
        for (Channel ch : channelGroup){
            if(ch != channel){
                ch.writeAndFlush(channel.remoteAddress() + "："+
                        "离开了聊天室" +"   "+ DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
