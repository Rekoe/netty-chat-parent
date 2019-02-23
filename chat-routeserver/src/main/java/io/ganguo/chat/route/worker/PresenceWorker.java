package io.ganguo.chat.route.worker;

import io.ganguo.chat.core.util.TaskUtil;
import io.ganguo.chat.route.biz.bean.Presence;

/**
 * Created by Tony on 2/24/15.
 */
public class PresenceWorker extends IMWorker<Presence> {

    public static final int PROCESS_DELAYER_MILLIS = 20;

    public PresenceWorker() {
        TaskUtil.pool(this);
    }

    @Override
    public void process(Presence presence) {
        // broadcast
       /* Map<Long, ClientSession> sessions = ClientSessionManager.getInstance().sessions();
        for (long uin : sessions.keySet()) {
            if (presence.getUin() != uin) {
                ClientSession session = sessions.get(uin);

                IMResponse resp = new IMResponse();
                Header header = new Header();
                header.setHandlerId(Handlers.USER);
                header.setCommandId(Commands.USER_PRESENCE_CHANGED);
                resp.setHeader(header);
                resp.writeEntity(new PresenceDTO(presence));
                session.getConnection().sendResponse(resp);
            }
        }*/
        // 处理完当前server connection，还需要处理在其它server的用户
        // 把未发送的uin分发到route服务

        try {
            Thread.sleep(PROCESS_DELAYER_MILLIS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
