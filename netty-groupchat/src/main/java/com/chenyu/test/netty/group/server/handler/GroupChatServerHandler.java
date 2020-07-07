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
    private boolean isUserName = true;
    private String userName;       //客户端用户名
    /**
     * 接收到消息事件
     * 将消息转发给其他客户端的socketChannel
     * */
    @Override
    protected void messageReceived(ChannelHandlerContext ctx, String msg) throws Exception {
        Channel channel = ctx.channel();
        if(isUserName){
            //获取用户名并且将该channel加入到群组
           this.userName = msg;
            isUserName = false;
            //该方法会将 channelGroup 中所有的channel 遍历，并发送 消息，我们不需要自己遍历
            channelGroup.add(channel);
            channelGroup.writeAndFlush("[客户端]" + userName +
                    " 加入聊天" + DATE_TIME_FORMATTER.format(LocalDateTime.now())+"$_");
            System.out.println("[客户端]" + userName +
                    " 加入聊天" + DATE_TIME_FORMATTER.format(LocalDateTime.now()));
            return;
        }
        System.out.println(userName+"："+msg
                +"   "+ DATE_TIME_FORMATTER.format(LocalDateTime.now()));
        //遍历channelGroup，若不是当前channel则转化消息
        for (Channel ch : channelGroup){
            if(ch != channel){
                ch.writeAndFlush(userName + "："+
                       msg +"   "+ DATE_TIME_FORMATTER.format(LocalDateTime.now())+"$_");
            }
        }
        //回显自己发送的消息
        channel.writeAndFlush("我："+msg+"   "
                + DATE_TIME_FORMATTER.format(LocalDateTime.now())+"$_");
    }

    /**
     * 连接建立事件
     * */
   /* @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //该方法会将 channelGroup 中所有的channel 遍历，并发送 消息，我们不需要自己遍历
        channelGroup.writeAndFlush("[客户端]" + ctx.channel().remoteAddress() +
                " 加入聊天" + DATE_TIME_FORMATTER.format(LocalDateTime.now())+"$_");
        channelGroup.add(channel);
    }*/

    /**
     * 连接断开事件
     * */
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        //遍历channelGroup，若不是当前channel则转化消息
        for (Channel ch : channelGroup){
            if(ch != channel){
                ch.writeAndFlush(userName + "："+
                        "离开了聊天室" +"   "+ DATE_TIME_FORMATTER.format(LocalDateTime.now())+"$_");
            }
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
