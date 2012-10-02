package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.TokenData;

import java.util.Map;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("tokenservice")
public interface TokenService extends RemoteService {
	String createToken(String grant, Map<String,String> resources) throws Exception;
	TokenData getTokenData(String tokenId, String user) throws Exception;
}
