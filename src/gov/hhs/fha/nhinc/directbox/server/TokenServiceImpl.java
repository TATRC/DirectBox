package gov.hhs.fha.nhinc.directbox.server;

import gov.hhs.fha.nhinc.directbox.client.TokenService;
import gov.hhs.fha.nhinc.directbox.shared.TokenData;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.tatrc.act.tokenaccess.AccessTokenAccess;
import org.tatrc.act.tokenaccess.beans.AccessToken;
import org.tatrc.act.tokenaccess.impl.AccessTokenCreationImpl;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class TokenServiceImpl extends RemoteServiceServlet implements
		TokenService {

	/** Logging. */
	private static Log log = LogFactory.getLog(TokenServiceImpl.class);

	public static final String DIRECT_DEMO_SUBJECT = "RHex Demo";

	@Override
	public String createToken(String grant, Map<String, String> resources)
			throws Exception {
        ApplicationContext ctx = new ClassPathXmlApplicationContext("org/tatrc/act/tokenaccess/tokenaccess.xml");
        AccessTokenCreationImpl impl = (AccessTokenCreationImpl) ctx.getBean("tokenFactory");
        
        //Create token
        AccessToken tok = impl.createToken(DIRECT_DEMO_SUBJECT);
        
        //Add grant
        tok.getGrants().add(grant);
        
        //Add resources
        Iterator<String> iterator = resources.keySet().iterator();
        while (iterator.hasNext()) {
        	String key = iterator.next();
            tok.getResources().put(key, resources.get(key));
        }

        //Update token in db
        impl.updateToken(tok);
        
        if (log.isDebugEnabled()) {
            log.debug("Created token: " + tok.getId() + ", granted to: " + grant);
        }

        return tok.getId();
	}

	@Override
	public TokenData getTokenData(String tokenId, String user) throws Exception {
		TokenData retVal = null;
		
        ApplicationContext ctx = new ClassPathXmlApplicationContext("org/tatrc/act/tokenaccess/tokenaccess.xml");
		AccessTokenAccess impl = (AccessTokenAccess) ctx.getBean("tokenAccess");
       
        //Get token
		AccessToken token = impl.getAccessToken(tokenId, user);
		//Convert to transport object
		if (token != null) {
			retVal = new TokenData();
			retVal.setId(tokenId);
			retVal.getResources().putAll(token.getResources());
		}
		else {
            log.debug("Token not found id: " + tokenId + ", granted to: " + user);
        }

        return retVal;
	}

}
