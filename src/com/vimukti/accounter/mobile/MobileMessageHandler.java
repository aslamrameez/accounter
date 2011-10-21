/**
 * 
 */
package com.vimukti.accounter.mobile;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import com.vimukti.accounter.core.IMUser;
import com.vimukti.accounter.main.ServerLocal;
import com.vimukti.accounter.mobile.MobileAdaptor.AdaptorType;
import com.vimukti.accounter.utils.HibernateUtil;

/**
 * @author Prasanna Kumar G
 * 
 */
public class MobileMessageHandler {

	private Map<String, MobileSession> sessions = new HashMap<String, MobileSession>();

	/**
	 * @param message
	 * @param message2
	 * @return
	 * @throws AccounterMobileException
	 */
	public String messageReceived(String networkId, String userId,
			String message, AdaptorType adaptorType, int networkType)
			throws AccounterMobileException {
		Session openSession = HibernateUtil.openSession();
		try {

			MobileSession session = sessions.get(networkId);

			if (session == null || session.isExpired()) {
				session = new MobileSession();
				sessions.put(networkId, session);
				ServerLocal.set(Locale.ENGLISH);
			}
			IMUser imUser = getIMUser(networkId, networkType);
			if (imUser != null) {
				session.setClient(imUser.getClient());
			}
			session.sethibernateSession(openSession);
			session.reloadObjects();

			MobileAdaptor adoptor = getAdaptor(adaptorType);

			UserMessage preProcess = adoptor.preProcess(session, message,
					userId, networkId, networkType);
			Result result = getCommandProcessor().handleMessage(session,
					preProcess);
			String reply = adoptor.postProcess(result);
			session.await();

			return reply;
		} catch (Exception e) {
			e.printStackTrace();
			throw new AccounterMobileException(e);
		} finally {
			if (openSession.isOpen()) {
				openSession.close();
			}
		}
	}

	private IMUser getIMUser(String networkId, int networkType) {
		Session session = HibernateUtil.getCurrentSession();
		IMUser user = (IMUser) session.getNamedQuery("imuser.by.networkId")
				.setString("networkId", networkId)
				.setInteger("networkType", networkType).uniqueResult();
		return user;
	}

	/**
	 * Returns the Adaptor of given Type
	 * 
	 * @param chat
	 * @return
	 */
	private MobileAdaptor getAdaptor(AdaptorType type) {
		if (type.equals(AdaptorType.CHAT)) {
			return MobileChatAdaptor.INSTANCE;
		} else {
			return MobileApplicationAdaptor.INSTANCE;
		}
	}

	/**
	 * Returns the Command Processor
	 */
	private CommandProcessor getCommandProcessor() {
		return CommandProcessor.INSTANCE;
	}
}
