package org.activityinfo.ui.client.page.config.design.importer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.ui.client.importer.data.PastedTable;
import org.activityinfo.ui.client.page.config.design.importer.SchemaImporter.ProgressListener;
import org.activityinfo.ui.client.page.config.design.importer.SchemaImporter.Warning;
import org.activityinfo.ui.client.style.BaseStylesheet;

public class ImportSchemaDialog {

    private static ImportSchemaUiBinder uiBinder = GWT
            .create(ImportSchemaUiBinder.class);

    interface ImportSchemaUiBinder extends UiBinder<Widget, ImportSchemaDialog> {
    }

    interface Templates extends SafeHtmlTemplates {

        @Template("<p class=\"{0}\">{1}: {2}</p>")
        SafeHtml warning(String styleName, String severity, String message);

        @Template("<div class=\"alert alert-danger\">{0}</div>")
        SafeHtml alertDanger(String message);

        @Template("<div class=\"progress\">" +
                "<div class=\"progress-bar progress-bar-info\" role=\"progressbar\" aria-valuenow=\"{0}\" "
                + "aria-valuemin=\"{0}\" aria-valuemax=\"100\" style=\"width: {0}%\">" +
                "<span class=\"sr-only\">{0}% Complete</span>" +
                "</div>" +
                "</div>")
        SafeHtml progressBar(int percentComplete);
    }

    private static final Templates TEMPLATES = GWT.create(Templates.class);

    private SchemaImporter importer;

    @UiField
    DeckPanel deckPanel;

    // first page
    @UiField
    TextArea textArea;
    @UiField
    HTML warnings;

    // second page
    @UiField
    DivElement submitStatus;
    @UiField
    HTML validationPanel;

    // button bar

    @UiField
    Button closeButton;
    @UiField
    Button cancelButton;
    @UiField
    Button backButton;
    @UiField
    Button continueButton;

    private PopupPanel popupPanel;

    private int currentPage = 0;

    public ImportSchemaDialog(SchemaImporter importer) {
        this.importer = importer;

        BaseStylesheet.INSTANCE.ensureInjected();

        Widget content = uiBinder.createAndBindUi(this);
    
        showPage(currentPage);

        popupPanel = new PopupPanel(true);
        popupPanel.add(content);
    }

    public void show() {
        popupPanel.center();
    }

    private void showPage(int index) {
        switch (index) {
            case 0:
                backButton.setVisible(false);
                continueButton.setText("Continue &raquo;");
                break;
            case 1:
                importer.processRows();
                backButton.setVisible(false);
                continueButton.setText("Submit");
                submitStatus.setInnerSafeHtml(SafeHtmlUtils.EMPTY_SAFE_HTML);
                break;
        }
        currentPage = index;
        deckPanel.showWidget(index);
    }


    @UiHandler("textArea")
    public void onTextAreaChanged(ChangeEvent changeEvent) {
        importer.clearWarnings();
        boolean succesful = importer.parseColumns(new PastedTable(textArea.getText()));
        continueButton.setEnabled(succesful);

        // show any warnings about the import
        warnings.setHTML(warningsHtml());
    }

    private SafeHtml warningsHtml() {
        SafeHtmlBuilder html = new SafeHtmlBuilder();
        for (Warning warning : importer.getWarnings()) {
            if (warning.isFatal()) {
                html.append(TEMPLATES.warning("text-danger", "Fatal Error", warning.getMessage()));
            } else {
                html.append(TEMPLATES.warning("text-warning", "Warning", warning.getMessage()));
            }
        }
        return html.toSafeHtml();
    }

    @UiHandler("continueButton")
    public void onContinue(ClickEvent event) {
        if (currentPage == 0) {
            importer.clearWarnings();
            showPage(1);
        } else {
            submit();
        }
    }

    @UiHandler("cancelButton")
    public void onCancel(ClickEvent event) {
        popupPanel.hide();
    }

    @UiHandler("closeButton")
    public void onClose(ClickEvent event) {
        popupPanel.hide();
    }

    private void submit() {
        importer.setProgressListener(new ProgressListener() {

            @Override
            public void submittingBatch(int batchNumber, int batchCount) {
                int percent = (int) (((double) batchNumber) / ((double) batchCount) * 100d);
                submitStatus.setInnerSafeHtml(TEMPLATES.progressBar(percent));
            }
        });
        importer.persist(new AsyncCallback<Void>() {

            @Override
            public void onSuccess(Void result) {
                deckPanel.showWidget(2);
                cancelButton.setText("Close");
                backButton.setVisible(false);
                continueButton.setVisible(false);
            }

            @Override
            public void onFailure(Throwable caught) {
                submitStatus.setInnerSafeHtml(TEMPLATES.alertDanger("Update failed: " + caught.getMessage()));
            }
        });
    }
}
