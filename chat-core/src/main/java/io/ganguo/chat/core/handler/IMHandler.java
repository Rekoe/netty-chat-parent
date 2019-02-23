package io.ganguo.chat.core.handler;

import io.ganguo.chat.core.connetion.IMConnection;

/**
 * Handler
 * 
 * @author Tony
 * @createAt Feb 17, 2015
 *
 */
public abstract class IMHandler<T> {
	public abstract short getId();

	public abstract void dispatch(IMConnection connection, T data);
}
