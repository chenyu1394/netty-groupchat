package com.chenyu.test;

import static org.junit.Assert.assertTrue;

import com.chenyu.test.netty.group.client.GroupChatClient;
import com.chenyu.test.netty.group.server.GroupChatServer;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * 开启服务器端
     */
    @Test
    public void serverTest() throws Exception {
        new GroupChatServer().start(8090);
    }


}
