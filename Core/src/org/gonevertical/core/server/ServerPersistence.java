package org.gonevertical.core.server;

import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServletRequest;

import org.gonevertical.core.client.oauth.OAuthTokenData;
import org.gonevertical.core.client.ui.admin.thing.ThingData;
import org.gonevertical.core.server.db.Db_User;
import org.gonevertical.core.server.jdo.PMF;
import org.gonevertical.core.server.jdo.data.SessionAccessTokenJdo;

/**
 * 
 * 
 * TODO - set the ThingId after the login, so to tell the inserts and updates whos doing what
 *
 * 
 * @author branflake2267
 *
 */
public class ServerPersistence {

	private static final Logger log = Logger.getLogger(ServerPersistence.class.getName());

	// keep track of the http request coming in
	private HttpServletRequest request = null;

	private OAuthTokenData accessToken;
	
	// whos controlling this session
	private long thingId = 0;

	/**
	 * constructor - init
	 */
	public ServerPersistence() {
	}

	/**
	 * set up what we need for the session
	 * 
	 * @param request
	 * @param accessToken
	 */
	public void start(HttpServletRequest request, OAuthTokenData accessToken) {
		this.accessToken = accessToken;
		this.request = request;
		
		// set the user that is logged in
		setSession_ThingId();
	}

	private void setSession_ThingId() {
		Db_User dbU = new Db_User(this);
		ThingData td = dbU.getUser(accessToken);
		if (td != null && td.getThingId() > 0) {
			thingId = td.getThingId();
		}
  }

	public void end() {
	}

	public void getUrl() {
	}

	/**
	 * get request url
	 * 
	 * @return
	 */
	public String getRequestUrl() {
		String host = request.getRemoteHost();
		String path = request.getPathInfo();
		int port = request.getRemotePort();

		String url = "";
		if (port == 80) {
			url = "http://" + host + path;
		} else if (port == 443) {
			url = "https://" + host + path;
		} else {
			url = "http://" + host + ":" + port + path;
		}

		return url;
	}

	/**
	 * get request url path for oauth - minus port
	 * 
	 * ajax requests come in on different ports, as far as I can tell
	 * maybe its only in hosted mode
	 * 
	 * @return
	 */
	public String getRequestUrlOAuth() {
		String host = getHost();
		String path = request.getRequestURI();
		// take off the servlet context path
		String newPath = "";
		if (path != null) {
			String re = "(.*/)";
			Pattern p = Pattern.compile(re);
			Matcher m = p.matcher(path);
			boolean found = m.find();
			if (found == true) {
				newPath = m.group(1);
			}
		}
		String url = host + newPath;
		return url;   
	}

	/**
	 * get hostname
	 * 
	 * @param request
	 * @return
	 */
	public String getHost() {
		String s = "";    
		StringBuffer url = request.getRequestURL();
		int sep = 0;
		int col = 0;
		for (int i=0; i < url.length(); i++) {
			String c = Character.toString(url.charAt(i));
			if (c.equals(":")) {
				col++;
				if (col == 2) {
					break;
				}
			}
			if (c.equals("/")) {
				sep++;
				if (sep == 3) {
					break;
				}
			}
			s += c;
		}
		return s;
	}

	private long getThingId(OAuthTokenData accessToken) {
		SessionAccessTokenJdo satj = new SessionAccessTokenJdo(this);
		satj.getThingId();
		return thingId;
	}
	
	/**
	 * get thing controlling session
	 * 
	 * @return
	 */
	public long getUserThingId() {
		return thingId;
	}

	/**
	 * get persistence manager - TODO wondering if there is a different way to speed recurring connections?
	 * TODO Connection Pooling???
	 * 
	 * @return
	 */
	public PersistenceManager getPmf() {
		return PMF.get().getPersistenceManager();
	}

	public PersistenceManager getPersistenceManager() {
		return PMF.get().getPersistenceManager();
	}
	
	public Logger getLogger() {
		return log;
	}

}
