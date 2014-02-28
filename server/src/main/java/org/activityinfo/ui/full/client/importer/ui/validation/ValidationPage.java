package org.activityinfo.ui.full.client.importer.ui.validation;

import com.google.common.base.Function;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.client.Promise;
import org.activityinfo.api2.client.PromiseMonitor;
import org.activityinfo.ui.full.client.importer.ui.ImportPage;
import org.activityinfo.ui.full.client.importer.ui.Importer;

import javax.annotation.Nullable;

/**
 * Presents the result of the matching to the user and provides
 * and opportunity to resolve conversion problems or ambiguities
 * in reference instances.
 */
public class ValidationPage extends Composite implements PromiseMonitor, ImportPage {

    private static ValidationPageUiBinder uiBinder = GWT
            .create(ValidationPageUiBinder.class);
    private Importer importer;


    interface ValidationPageUiBinder extends UiBinder<Widget, ValidationPage> {
    }

    @UiField(provided = true)
    ValidationGrid dataGrid;

    @UiField
    Element loadingElement;

    @UiField
    Element loadingErrorElement;

    public ValidationPage(Importer importer) {
        this.importer = importer;

        dataGrid = new ValidationGrid(importer);

        initWidget(uiBinder.createAndBindUi(this));
    }


    @Override
    public void start() {
        importer.updateBindings();
        importer.matchReferences()
                .withMonitor(this)
                .then(new Function<Void, Void>() {
                    @Nullable
                    @Override
                    public Void apply(@Nullable Void input) {
                        dataGrid.refreshRows();
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
