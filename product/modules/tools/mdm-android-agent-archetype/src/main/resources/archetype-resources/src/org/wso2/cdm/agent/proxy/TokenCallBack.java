package org.wso2.cdm.agent.proxy;


public interface TokenCallBack {
	public void onReceiveTokenResult(Token token,String status);
}
