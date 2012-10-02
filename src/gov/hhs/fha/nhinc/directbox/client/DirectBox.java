package gov.hhs.fha.nhinc.directbox.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DirectBox implements EntryPoint {

	@Override
	public void onModuleLoad() {
		// Pull parameters
		String page = Window.Location.getParameter("page");
		String providerId = Window.Location.getParameter("providerId");
		String patientId = Window.Location.getParameter("patientId");
		List<String> localRepositoryIds = new ArrayList<String>();
		List<String> localList = Window.Location.getParameterMap().get("local");
		if ((localList != null) && !localList.isEmpty()) {
			for (String local : localList) {
				localRepositoryIds.add(local);
			}
		}
		List<String> targets = new ArrayList<String>();
		List<String> targetList = Window.Location.getParameterMap().get("target");
		if ((targetList != null) && !targetList.isEmpty()) {
			for (String target : targetList) {
				targets.add(target);
			}
		}
		String from = Window.Location.getParameter("from");
		String to = Window.Location.getParameter("to");
		String token = Window.Location.getParameter("token");

		//Navigate
		if (page.equals("consult")) {
			ConsultWelcome welcome = new ConsultWelcome();
			RootLayoutPanel.get().add(welcome);
			welcome.init(providerId, patientId);
		}
		else if (page.equals("send")) {
			SendMail sendMail = new SendMail();
			RootLayoutPanel.get().add(sendMail);
			sendMail.init(from, to, token);
		}
		else { //if (page.equals("inbox")) {
			AhltaBox inbox = new AhltaBox();
			RootLayoutPanel.get().add(inbox);
			inbox.init(patientId);
		}
	}
}
