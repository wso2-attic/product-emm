package org.wso2.cdm.agent.proxy;

/**
 * Created with IntelliJ IDEA.
 * User: gayan
 * Date: 3/25/14
 * Time: 8:40 AM
 * To change this template use File | Settings | File Templates.
 */
public interface CallBack {

    /**
     * @param tokens
     * @param status
     */
    void receiveAccessToken(String status, String message, Token token);

    /**
     * @param status
     * @param message
     */
    void receiveNewAccessToken(String status, String message, Token token);
    
    
    
}
