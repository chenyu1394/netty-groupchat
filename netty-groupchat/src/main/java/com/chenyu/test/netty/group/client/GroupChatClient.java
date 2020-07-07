package com.chenyu.test.netty.group.client;

import com.chenyu.test.netty.group.client.handler.GroupChatClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;

/**
 * 群聊客户端
 * @author ChenYu
 * */
public class GroupChatClient {

    /**
     * 聊天
     * @param host 服务器地址
     * @param port 服务端监听端口
     * @param userName 客户端名字
     * */
    public void run(String userName,String host,int port) throws Exception{
        //创建工作线程组
        NioEventLoopGroup group = new NioEventLoopGroup();
        try{
            //创建启动辅助对象
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
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
                            pipeline.addLast(new GroupChatClientHandler(userName));
                        }
                    });
            //连接到服务器,同步
            ChannelFuture f = bootstrap.connect(host, port).sync();
            if(f.isSuccess()){
                Channel channel = f.channel();
                Scanner scanner = new Scanner(System.in);
                String message;
                while (scanner.hasNextLine()){
                    message = scanner.nextLine();
                    channel.writeAndFlush(message+"$_");
                }
            }
            f.channel().closeFuture().sync();
        }finally {
            group.shutdownGracefully();
        }

    }

    public static void main(String[] args) throws Exception {
        new GroupChatClient().run("陈宇","127.0.0.1",8090);
    }
}
