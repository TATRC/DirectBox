package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.TokenData;

import java.util.Map;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TokenServiceAsync {
	void createToken(String grant, Map<String, String> resources,
			AsyncCallback<String> callback);

	void getTokenData(String tokenId, String user, AsyncCallback<TokenData> callback);
}
