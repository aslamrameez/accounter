package com.vimukti.accounter.web.client.core;

import com.vimukti.accounter.core.PayHead;

public class ClientComputationFormulaFunction implements IAccounterCore {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int FUNCTION_ADD_PAY_HEAD = 1;

	public static final int FUNCTION_SUBSTRACT_PAY_HEAD = 2;

	public static final int FUNCTION_DIVIDE_ATTENDANCE = 3;

	public static final int FUNCTION_MULTIPLY_ATTENDANCE = 4;

	private int functionType;

	private PayHead payHead;

	private long id;

	public int getFunctionType() {
		return functionType;
	}

	public void setFunctionType(int functionType) {
		this.functionType = functionType;
	}

	public PayHead getPayHead() {
		return payHead;
	}

	public void setPayHead(PayHead payHead) {
		this.payHead = payHead;
	}

	@Override
	public int getVersion() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setVersion(int version) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AccounterCoreType getObjectType() {
		return AccounterCoreType.COMPUTATION_FORMULA_FUNCTION;
	}

	@Override
	public void setID(long id) {
		this.id = id;
	}

	@Override
	public long getID() {
		return this.id;
	}

}
