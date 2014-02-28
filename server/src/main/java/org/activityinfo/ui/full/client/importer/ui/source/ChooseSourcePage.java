package org.activityinfo.ui.full.client.importer.ui.source;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.DataGrid;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.full.client.importer.data.PastedTable;
import org.activityinfo.ui.full.client.importer.data.SourceTable;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.ImportPage;
import org.activityinfo.ui.full.client.importer.ui.PageChangedEvent;

/**
 * Start page for the table import process that prompts the user
 * for pasted table data
 */
public class ChooseSourcePage extends Composite implements ImportPage {


    private static ChoosePageUiBinder uiBinder = GWT
            .create(ChoosePageUiBinder.class);

    interface ChoosePageUiBinder extends UiBinder<Widget, ChooseSourcePage> {
    }

    private ImportModel model;
    private final EventBus eventBus;

    @UiField
    TextArea textArea;

    public ChooseSourcePage(ImportModel model, EventBus eventBus) {
        this.model = model;
        this.eventBus = eventBus;
        initWidget(uiBinder.createAndBindUi(this));
    }

    @UiHandler("textArea")
    public void onTextChanged(ChangeEvent event) {
        if (textArea.getValue().length() > 0) {
            model.setSource(new PastedTable(textArea.getText()));
            eventBus.fireEvent(new PageChangedEvent(true));
        } else {
            eventBus.fireEvent(new PageChangedEvent(false));
        }
    }

    @Override
    public boolean isValid() {
        return textArea.getValue().length() > 0;
    }

    @Override
    public boolean hasNextStep() {
        return false;
    }

    @Override
    public boolean hasPreviousStep() {
        return false;
    }

    @Override
    public void start() {

    }

    @Override
    public void nextStep() {
    }

    @Override
    public void previousStep() {
    }
}
