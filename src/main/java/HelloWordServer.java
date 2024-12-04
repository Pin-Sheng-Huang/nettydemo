import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

/**
 * @program: nettydemo
 * @description:
 * @Creator: 阿昇
 * @CreateTime: 2024-12-04 15:47
 * @LastEditTime: 2024-12-04 15:47
 */

public class HelloWordServer {

    private int port;

    public HelloWordServer(int port) {
        this.port = port;
    }
    public void start(){
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();

        ServerBootstrap server = new ServerBootstrap().group(bossGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ServerChannelInitializer());

        try {
            ChannelFuture future = server.bind(port).sync();
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workGroup.shutdownGracefully();
        }
    }
    class ServerChannelInitializer extends ChannelInitializer<SocketChannel> {
        @Override
        protected void initChannel(SocketChannel socketChannel) throws Exception {
            ChannelPipeline pipeline = socketChannel.pipeline();

            // 字符串解码 和 编码
            pipeline.addLast("decoder", new StringDecoder());
            pipeline.addLast("encoder", new StringEncoder());

            // 自己的逻辑Handler
            pipeline.addLast("handler", new HelloWordServerHandler());
        }
    }

    class HelloWordServerHandler extends ChannelInboundHandlerAdapter {

        @Override
        public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
            System.out.println(ctx.channel().remoteAddress()+"===>server: "+msg.toString());
            ctx.write("received your msg");
            ctx.flush();
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            super.exceptionCaught(ctx, cause);
            ctx.close();
        }
    }
    public static void main(String[] args) {
        HelloWordServer server = new HelloWordServer(7788);
        server.start();
    }
}
