package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.full.client.importer.data.ImportSource;
import org.activityinfo.ui.full.client.importer.data.PastedImportSource;

/**
 * Start page for the table import process that prompts the user
 * for pasted table data
 */
public class ChooseSourcePage extends Composite implements ImportPage {


    private static ChoosePageUiBinder uiBinder = GWT
            .create(ChoosePageUiBinder.class);

    interface ChoosePageUiBinder extends UiBinder<Widget, ChooseSourcePage> {
    }

    private final EventBus eventBus;

    @UiField
    TextArea textArea;

    public ChooseSourcePage(EventBus eventBus) {
        this.eventBus = eventBus;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("textArea")
    public void onTextChanged(ChangeEvent event) {
        if (textArea.getValue().length() > 0) {
            eventBus.fireEvent(new PageChangedEvent(true));
        } else {
            eventBus.fireEvent(new PageChangedEvent(false));
        }
    }

    public ImportSource getImportSource() {
        return new PastedImportSource(textArea.getText());
    }

    @Override
    public boolean isValid() {
        return textArea.getValue().length() > 0;
    }
}
