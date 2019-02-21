package io.ganguo.chat.route.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.ganguo.chat.core.connetion.ConnectionManager;
import io.ganguo.chat.core.connetion.IMConnection;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.handler.IMHandlerManager;
import io.ganguo.chat.core.protocol.Handlers;
import io.ganguo.chat.core.transport.Header;
import io.ganguo.chat.core.transport.IMRequest;
import io.ganguo.chat.route.server.service.UpdateUserStatusService;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ChatServerHandler extends SimpleChannelInboundHandler<IMRequest> {

	private Logger logger = LoggerFactory.getLogger(ChatServerHandler.class);

	private final ConnectionManager mConnectionManager = ConnectionManager.getInstance();

	private UpdateUserStatusService updateUserStatusService;

	public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
		super.handlerAdded(ctx);
		mConnectionManager.create(ctx);
		logger.warn("handlerAdded " + mConnectionManager.count());
	}

	public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
		super.handlerRemoved(ctx);
		mConnectionManager.remove(ctx);
		logger.warn("handlerRemoved " + mConnectionManager.count());
	}

	protected void messageReceived(ChannelHandlerContext ctx, IMRequest request) throws Exception {
		logger.warn("messageReceived header:" + request.getHeader());
		// TODO 集群环境下 通过总路由和子路由的方式来查找 conn
		IMConnection conn = mConnectionManager.find(ctx);
		Header header = request.getHeader();
		IMHandler handler = IMHandlerManager.getInstance().find(header.getHandlerId());
		if (handler != null) {
			handler.dispatch(conn, request);
		} else {
			logger.warn("Not found handlerId: " + header.getHandlerId());
		}
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		IMHandler handler = IMHandlerManager.getInstance().find(Handlers.EXCEPTION);
		IMConnection connection = mConnectionManager.find(ctx.pipeline().lastContext());
		if (handler != null) {
			handler.dispatch(connection, null);
		}
		if (this == ctx.pipeline().last()) {
			logger.warn("EXCEPTION, please implement " + getClass().getName() + ".exceptionCaught() for proper handling.", cause.getCause());
		}
	}
}
