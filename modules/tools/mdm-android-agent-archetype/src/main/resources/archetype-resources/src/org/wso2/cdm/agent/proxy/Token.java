package org.wso2.cdm.agent.proxy;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.util.Log;

/**
 * Persists refresh token to obtain new access token and id token to retrieve login user claims
 */
public final class Token {
    private String refreshToken = null;
    private String idToken = null;
    private String accessToken = null;
    private Date receivedDate = null;
    private boolean expired =false;

    public Date getDate() {
        return receivedDate;
    }

    public void setDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        Date date = new Date();
        String strDate = dateFormat.format(date);
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            receivedDate = format.parse(strDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void setDate(String date) {  
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        try {
            receivedDate = format.parse(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String id_Token) {
        idToken = id_Token;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }


	public void setExpired(boolean expired) {
	    this.expired = expired;
    }
	
	@SuppressLint("SimpleDateFormat")
    public static boolean isValid(Date expirationDate) {
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date currentDate = new Date();
		String strDate = dateFormat.format(currentDate);
		SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		try {
			currentDate = format.parse(strDate);
		} catch (ParseException e1) {
			e1.printStackTrace();
		}
		boolean expired = currentDate.after(expirationDate);
		boolean equalDates = currentDate.equals(expirationDate);
		if (expired == true || equalDates == true) {
			return true;
		}

		return false;
	}
}
