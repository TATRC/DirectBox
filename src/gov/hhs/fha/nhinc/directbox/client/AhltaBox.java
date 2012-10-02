package gov.hhs.fha.nhinc.directbox.client;

import gov.hhs.fha.nhinc.directbox.shared.EmailAttachment;
import gov.hhs.fha.nhinc.directbox.shared.EmailData;

import java.util.Date;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.uibinder.client.UiTemplate;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

public class AhltaBox extends Composite {

	/**
	 * Create a remote service proxy to talk to the server-side email service.
	 */
	private final EmailServiceAsync emailService = GWT
			.create(EmailService.class);

	/** UiBinder declaration */
	private static AhltaBoxUiBinder uiBinder = GWT
			.create(AhltaBoxUiBinder.class);

	@UiTemplate("AhltaBox.ui.xml")
	interface AhltaBoxUiBinder extends UiBinder<Panel, AhltaBox> {
	}

	@UiField
	Label statusLabel;
	
	@UiField
	ListBox folderListBox;

	@UiField
	Button refreshButton;

	@UiField
	Button createConsultButton;

	@UiField
	Button deleteButton;

	/** Item table */
	@UiField
	DataGrid<EmailData> itemTable;

	/** Item attachment display */
	@UiField
	HorizontalPanel attachmentPanel;

	/** Item display */
	@UiField
	HTML itemHTML;

	/** Item table data provider. */
	private EmailTableDataProvider provider;

	/** Item table selction model. */
	private SingleSelectionModel<EmailData> selectionModel;

	/** Patient id */
	private String patientId;

	public static final String MAIL_CLIENT_INBOX_FOLDER = "Inbox";
	public static final String MAIL_CLIENT_SENT_FOLDER = "Sent";

	public static final String DOCDISPLAY_ERROR = "";
	public static final String DOCDISPLAY_NOTHTML = "<style class='docNotHtml'>Document is not displayable html.</style>";

    public static final String ANCHOR_TARGET_BLANK  = "_blank";
	   
	public AhltaBox() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void init(String patientId) {
		this.patientId = patientId;

		// Populate folder names
		folderListBox.addItem(MAIL_CLIENT_INBOX_FOLDER);
		folderListBox.addItem(MAIL_CLIENT_SENT_FOLDER);

		// Hide buttons until needed
		deleteButton.setVisible(false);

		// Height is set in the ui xml
		itemTable.setWidth("100%");

		// Add a selection model to handle user selection.
		selectionModel = new SingleSelectionModel<EmailData>();
		itemTable.setSelectionModel(selectionModel);
		selectionModel
			.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
				public void onSelectionChange(SelectionChangeEvent event) {
					EmailData selected = selectionModel.getSelectedObject();
					if (selected != null) {
						attachmentPanel.clear();
						if (selected.getAttachments().length > 0) {
							attachmentPanel.setVisible(true);
							attachmentPanel.add(new HTML("<strong>Attachments(" 
								+ selected.getAttachments().length + "): </strong>"));

							for (int i = 0; i < selected.getAttachments().length; i++) {
								EmailAttachment attachment = selected.getAttachments()[i];
								Anchor a = new Anchor(attachment.getName(),
									GWT.getHostPageBaseURL() + attachment.getLocation());
								a.setTarget(ANCHOR_TARGET_BLANK);
								attachmentPanel.add(a);
							}
						}
						else {
							attachmentPanel.setVisible(false);
						}
						itemHTML.setHTML(selected.getContent());
						deleteButton.setVisible(true);
					}
				}
			});

		// Add a text column to show the sender.
		TextColumn<EmailData> senderColumn = new TextColumn<EmailData>() {
			@Override
			public String getValue(EmailData object) {
				return object.getSender();
			}
		};
		itemTable.addColumn(senderColumn, "From");
		itemTable.setColumnWidth(senderColumn, 40.0, Unit.PCT);

		// Add a text column to show the subject.
		TextColumn<EmailData> subjColumn = new TextColumn<EmailData>() {
			@Override
			public String getValue(EmailData object) {
				return object.getSubject();
			}
		};
		itemTable.addColumn(subjColumn, "Subject");
		itemTable.setColumnWidth(subjColumn, 60.0, Unit.PCT);

		// Add a date column to show the email date.
		DateCell dateCell = new DateCell(
			DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_MEDIUM));
		Column<EmailData, Date> dateColumn = new Column<EmailData, Date>(dateCell) {
			@Override
			public Date getValue(EmailData object) {
				return object.getDate();
			}
		};
		itemTable.addColumn(dateColumn, "Date");
		itemTable.setColumnWidth(dateColumn, 200.0, Unit.PX);

		// Associate an async data provider to the table
		provider = new EmailTableDataProvider(this);
		provider.addDataDisplay(itemTable);
	}

	public String getPatientId() {
		return patientId;
	}

	public String getServerFolderName() {
		String 	serverFolderName = EmailData.MAIL_SERVER_INBOX_FOLDER;
		String clientFolderName = folderListBox.getItemText(folderListBox.getSelectedIndex());

		if (MAIL_CLIENT_SENT_FOLDER.equals(clientFolderName)) {
			serverFolderName = EmailData.MAIL_SERVER_SENT_FOLDER;
		}

		return serverFolderName;
	}

	@UiHandler("refreshButton")
	void onRefreshClick(ClickEvent e) {
		refreshTable();
	}

	@UiHandler("createConsultButton")
	void onCreateConsultClick(ClickEvent e) {
		Window.Location.assign(GWT.getHostPageBaseURL() + "?page=consult");
	}

	@UiHandler("deleteButton")
	void onDeleteClick(ClickEvent e) {
		showWorking(true);
		itemHTML.setHTML("");
		EmailData selected = selectionModel.getSelectedObject();
		emailService.deleteMsg(getServerFolderName(), selected.getEmailId(),
			new AsyncCallback<Void> () {
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
					Window.alert("Mail error: " + caught.getMessage());
				}

				public void onSuccess(Void v) {
					refreshTable();
				}
			}
		);

	}

	@UiHandler("folderListBox")
	void onFolderChange(ChangeEvent event) {
		int selectedIndex = folderListBox.getSelectedIndex();
		if (selectedIndex > 0) {
			refreshTable();
		}
	}

	/**
	 * Refresh item table
	 */
	public void refreshTable() {
		provider.refresh();
	}
	
	/**
	 * Show working status
	 */
	public void showWorking(boolean working) {
		statusLabel.setVisible(working);
	}
}
