package org.activityinfo.ui.client.component.importDialog.validation;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.validation.ValidatedResult;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.fp.client.PromiseMonitor;
import org.activityinfo.ui.client.component.importDialog.ImportDialog;
import org.activityinfo.ui.client.component.importDialog.ImportPage;
import org.activityinfo.ui.client.component.importDialog.Importer;

/**
 * Presents the result of the matching to the user and provides
 * and opportunity to resolve conversion problems or ambiguities
 * in reference instances.
 */
public class ValidationPage extends Composite implements PromiseMonitor, ImportPage {

    private static ValidationPageUiBinder uiBinder = GWT
            .create(ValidationPageUiBinder.class);

    interface ValidationPageUiBinder extends UiBinder<Widget, ValidationPage> {
    }

    private ImportModel model;
    private Importer importer;
    private ImportDialog dialogBox;

    @UiField(provided = true)
    ValidationGrid dataGrid;
    @UiField(provided = true)
    ValidationMappingGrid mappingGrid;

    @UiField
    Element loadingElement;
    @UiField
    Element loadingErrorElement;

    public ValidationPage(ImportModel model, Importer importer, ImportDialog dialogBox) {
        this.model = model;
        this.importer = importer;
        this.dialogBox = dialogBox;

        mappingGrid = new ValidationMappingGrid();
        dataGrid = new ValidationGrid();

        initWidget(uiBinder.createAndBindUi(this));
    }

    @Override
    public void start() {
        importer.validate(model)
                .withMonitor(this)
                .then(new Function<ValidatedResult, Void>() {
                    @Override
                    public Void apply(ValidatedResult input) {
                        dataGrid.refresh(input.getRowTable());
                        mappingGrid.refresh(input.getClassValidation());
                        dialogBox.getFinishButton().setEnabled(input.getClassValidation().isEmpty());
                        return null;
                    }
                });
    }


    @Override
    public void onPromiseStateChanged(Promise.State state) {
        this.loadingElement.getStyle().setDisplay(  state == Promise.State.PENDING ?
                Style.Display.BLOCK : Style.Display.NONE );
        this.loadingErrorElement.getStyle().setDisplay(  state == Promise.State.REJECTED ?
                Style.Display.BLOCK : Style.Display.NONE );
    }

    @Override
    public boolean isValid() {
        return false;
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
    public void nextStep() {

    }

    @Override
    public void previousStep() {

    }

}
