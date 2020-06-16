package com.chenyu.test.netty.group.server;

import com.chenyu.test.netty.group.server.handler.GroupChatServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * 群聊服务器
 * @author ChenYu
 * */
public class GroupChatServer {

    /**
     * 启动服务器
     * @param port 监听端口
     * */
    public void start(int port) throws Exception{
        //创建线程组
        NioEventLoopGroup boosGroup = new NioEventLoopGroup(4);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try{
            //创建启动辅助对象
            ServerBootstrap bootstrap = new ServerBootstrap();
            //绑定信息
            bootstrap.group(boosGroup,workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //获取pipeLine对象
                            ChannelPipeline pipeline = ch.pipeline();
                            ByteBuf byteBuf = Unpooled.copiedBuffer("$_".getBytes());
                            pipeline.addLast(
                                    new DelimiterBasedFrameDecoder(1024,byteBuf));
                            //添加字符串编码解码器
                            pipeline.addLast(new StringDecoder());
                            pipeline.addLast(new StringEncoder());
                            //添加自定义的handler
                            pipeline.addLast(new GroupChatServerHandler());
                        }
                    });

            //同步启动服务器
            ChannelFuture f = bootstrap.bind(port).sync();
            //判断是否启动成功
            String status = f.isSuccess()?"--------服务器启动在："+port+"端口---------"
                    : "----------服务器启动失败--------";
            System.out.println(status);
            f.channel().closeFuture().sync();
        }finally {
            //退出前关闭线程组
            boosGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
