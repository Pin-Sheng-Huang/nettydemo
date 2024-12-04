import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @program: nettydemo
 * @description:
 * @Creator: 阿昇
 * @CreateTime: 2024-12-04 15:47
 * @LastEditTime: 2024-12-04 15:47
 */

public class HelloWorldClient {
    private  int port;
    private  String address;

    public HelloWorldClient(int port,String address) {
        this.port = port;
        this.address = address;
    }

    public void start(){
        EventLoopGroup group = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class).handler(new ClientChannelInitializer());

        try {
            Channel channel = bootstrap.connect(address,port).sync().channel();
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            for(;;){
                String msg = reader.readLine();
                if(msg == null){
                    continue;
                }
                channel.writeAndFlush(msg + "\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            group.shutdownGracefully();
        }

    }


    public static void main(String[] args) {
        HelloWorldClient client = new HelloWorldClient(7788,"127.0.0.1");
        client.start();
    }
}

class ClientChannelInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();

//        /*
//         * 这个地方的 必须和服务端对应上。否则无法正常解码和编码
//         *
//         * 解码和编码 我将会在下一节为大家详细的讲解。暂时不做详细的描述
//         *
//         * /
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());

        // 我们自己的handler
        pipeline.addLast("handler", new ClientHandler());
    }
}

class ClientHandler extends io.netty.channel.ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println("server say : "+msg.toString());
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client is active");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Client is close");
    }
}
