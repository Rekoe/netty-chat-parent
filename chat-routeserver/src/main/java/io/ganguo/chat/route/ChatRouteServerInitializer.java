package io.ganguo.chat.route;

import java.util.Map;

import io.ganguo.chat.core.codec.PacketDecoder;
import io.ganguo.chat.core.codec.PacketEncoder;
import io.ganguo.chat.core.handler.IMHandler;
import io.ganguo.chat.core.handler.IMHandlerManager;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

public class ChatRouteServerInitializer extends ChannelInitializer<SocketChannel> {

	@Override
	protected void initChannel(SocketChannel channel) throws Exception {
		ChannelPipeline pipeline = channel.pipeline();
		pipeline.addLast("decoder", new PacketDecoder(8192, 0, 4));
		pipeline.addLast("encoder", new PacketEncoder());
		pipeline.addLast("handler", new ChatRouteServerHandler());
		initIMHandler();
	}

	private void initIMHandler() {
		Map<String, IMHandler> handlers = ChatContext.getBeansOfType(IMHandler.class);
		for (String key : handlers.keySet()) {
			IMHandler handler = handlers.get(key);
			IMHandlerManager.getInstance().register(handler);
		}
	}
}
