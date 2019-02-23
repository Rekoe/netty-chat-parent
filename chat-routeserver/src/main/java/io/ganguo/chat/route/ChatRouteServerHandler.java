package io.ganguo.chat.route;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ganguo.chat.core.connetion.ConnectionManager;
import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.handler.IMHandlerManager;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatRouteServerHandler extends SimpleChannelInboundHandler<IMRequest> {

    private Logger logger = LoggerFactory.getLogger(ChatRouteServerHandler.class);
    private final ConnectionManager mConnectionManager = ConnectionManager.getInstance();

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        super.handlerAdded(ctx);
        mConnectionManager.create(ctx);
        logger.info("handlerAdded " + mConnectionManager.count());
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        super.handlerRemoved(ctx);
        mConnectionManager.remove(ctx);
        logger.info("handlerRemoved " + mConnectionManager.count());
    }

    @Override
    protected void messageReceived(ChannelHandlerContext ctx, IMRequest request) throws Exception {
        logger.info("messageReceived header:" + request.getHeader().toString());
        //这里只能收到需要转发的消息 根据ip 和端口来寻找服务器
        IMConnection conn = mConnectionManager.find(ctx);
        Header header = request.getHeader();
        IMHandler<IMRequest> handler = IMHandlerManager.getInstance().find(header.getHandlerId());
        if (handler != null) {
            handler.dispatch(conn, request);
        } else {
            logger.warn("Not found handlerId: " + request.toString());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        logger.info("........."+cause.getMessage());
    }
}
