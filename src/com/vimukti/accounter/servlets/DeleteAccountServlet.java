package com.vimukti.accounter.servlets;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.vimukti.accounter.core.DeleteReason;
import com.vimukti.accounter.core.FinanceDate;
import com.vimukti.accounter.utils.HibernateUtil;

public class DeleteAccountServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String DELETE_ACCOUNT_CONFORM = "/WEB-INF/deleteAccount.jsp";
	protected static final String CANCEL_FORM = "/main/cancelform";

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		HttpSession httpSession = req.getSession();
		String emailID = (String) httpSession.getAttribute(EMAIL_ID);
		StringBuffer sb = new StringBuffer();
		if (req.getParameter("tooslow") != null) {
			String toooslow = req.getParameter("tooslow");
			sb.append(toooslow);
			sb.append(". ");
		}
		if (req.getParameter("takelong") != null) {
			String takelong = req.getParameter("takelong");
			sb.append(takelong);
			sb.append(". ");
		}
		if (req.getParameter("mydata") != null) {
			String mydata = req.getParameter("mydata");
			sb.append(mydata);
			sb.append(". ");
		}
		if (req.getParameter("features") != null) {
			String features = req.getParameter("features");
			sb.append(features);
			sb.append(". ");
		}
		if (req.getParameter("personalfinance") != null) {
			String personalfinance = req.getParameter("personalfinance");
			sb.append(personalfinance);
			sb.append(". ");
		}
		if (req.getParameter("nobusinesssyet") != null) {
			String nobusinesssyet = req.getParameter("nobusinesssyet");
			sb.append(nobusinesssyet);
			sb.append(". ");
		}
		if (req.getParameter("content") != null) {
			String reason = req.getParameter("content");
			sb.append(reason);
			sb.append(". ");
		}
		Session session = HibernateUtil.openSession();
		Transaction transaction = session.beginTransaction();
		try {
			DeleteReason deleteReason = new DeleteReason();
			deleteReason.setEmailID(emailID);
			deleteReason.setReason(sb.toString());
			deleteReason.setDeletedDate(new FinanceDate());
			session.save(deleteReason);
			transaction.commit();
		} catch (Exception e) {
			e.printStackTrace();
			dispatch(req, resp, DELETE_ACCOUNT_CONFORM);
		} finally {
			session.close();
		}
		redirectExternal(req, resp, CANCEL_FORM);

	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		HttpSession session = req.getSession();
		Long companyID = (Long) session.getAttribute(COMPANY_ID);
		session.setAttribute("cancelDeleteAccountcompany", companyID);
		dispatch(req, resp, DELETE_ACCOUNT_CONFORM);
	}
}
