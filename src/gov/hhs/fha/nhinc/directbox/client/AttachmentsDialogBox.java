package gov.hhs.fha.nhinc.directbox.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AttachmentsDialogBox extends DialogBox {

    private ListBox multiBox;
    private CloseListener closeListener;
    
	public AttachmentsDialogBox(CloseListener eventListener) {
	    this.closeListener = eventListener;
	    
		setText("Attachments");

	    // Create a table to layout the content
	    VerticalPanel dialogContents = new VerticalPanel();
	    dialogContents.setSpacing(4);
	    setWidget(dialogContents);

	    // Add some text to the top of the dialog
	    Label details = new Label("Select files:");
	    dialogContents.add(details);

	    // Add a list box with multiple selection enabled
	    multiBox = new ListBox(true);
	    multiBox.setVisibleItemCount(10);
	    dialogContents.add(multiBox);
	    
	    // Add a close button at the bottom of the dialog
	    Button closeButton = new Button("Close",
	    	new ClickHandler() {
	            public void onClick(ClickEvent event) {
	            	if (closeListener != null) {
	            		closeListener.onClose();
	            	}
	            	
	                AttachmentsDialogBox.this.hide();
	            }
	        });
	    dialogContents.add(closeButton);
	    dialogContents.setCellHorizontalAlignment(
	    	closeButton, HasHorizontalAlignment.ALIGN_RIGHT);
	}
	
	public void setItems(String items[]) {
		multiBox.clear();
		
		if (items == null) {
			return;
		}
		
		for (int i = 0; i < items.length; i++) {
			multiBox.addItem(items[i]);
		}

	}
	
	/**
	 * Return selected items.
	 * 
	 * @return
	 */
	public String [] getSelected() {
		List<String> selectedValues = new ArrayList<String>();
		
		for (int i = 0, l = multiBox.getItemCount(); i < l; i++) {
			if (multiBox.isItemSelected(i)) {
				selectedValues.add(multiBox.getValue(i));
			}
		}
		
		return selectedValues.toArray(new String[selectedValues.size()]);
	}
	
	interface CloseListener {
		public void onClose();
	}
}
