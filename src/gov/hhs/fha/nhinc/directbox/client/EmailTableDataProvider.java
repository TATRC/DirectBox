package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.EmailData;

import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;

/**
 * Handle fetching the data through an RPC call.
 * 
 * @author cmatser
 */
public class EmailTableDataProvider extends AsyncDataProvider<EmailData> {

	/** Holds the data options */
	private AhltaBox options;

	/** Email service */
	private EmailServiceAsync emailService = GWT.create(EmailService.class);

	/** Last time refresh */
	private long lastRefreshTime = 0;
	
	/** 3 seconds between refreshes */
	public static long REFRESH_WINDOW = 3000;
	
	public EmailTableDataProvider(AhltaBox options) {
		this.options = options;
	}

	@Override
	protected void onRangeChanged(HasData<EmailData> display) {
		//final int start = display.getVisibleRange().getStart();
		//int length = display.getVisibleRange().getLength();
		options.showWorking(true);
		try {
			emailService.getMail(options.getServerFolderName(), 0, 0,
					options.getPatientId(),
					new AsyncCallback<List<EmailData>>() {
						@Override
						public void onFailure(Throwable caught) {
							ErrorHandler.showError(
									"Error retrieving documents.", caught);
							options.showWorking(false);
						}

						@Override
						public void onSuccess(List<EmailData> result) {
							//We are always getting the whole list
							updateRowData(0, result);
							updateRowCount(result.size(), true);
							options.showWorking(false);
						}
					});
		} catch (Exception e) {
			ErrorHandler.showError("Error retrieving documents.", e);
		}
	}

	public void refresh() {
		Date now = new Date();
		
		if ((now.getTime() - lastRefreshTime) < REFRESH_WINDOW)
			return;

		lastRefreshTime = now.getTime();
		
		for (HasData<EmailData> display : this.getDataDisplays()) {
			onRangeChanged(display);
		}
	}
}
