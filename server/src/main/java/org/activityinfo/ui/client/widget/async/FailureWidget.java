package org.activityinfo.ui.client.widget.async;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.style.Icons;
import org.activityinfo.ui.client.widget.Button;

/**
 * Displays a failure to the user along with the option to retry
 */
public class FailureWidget implements IsWidget {

    private final HTMLPanel rootElement;

    @UiField
    Element icon;

    @UiField
    Element heading;

    @UiField
    Element explanation;

    @UiField
    Button retryButton;

    interface FailureWidgetUiBinder extends UiBinder<HTMLPanel, FailureWidget> {
    }

    private static FailureWidgetUiBinder ourUiBinder = GWT.create(FailureWidgetUiBinder.class);

    public FailureWidget() {
        rootElement = ourUiBinder.createAndBindUi(this);

        Icons.INSTANCE.ensureInjected();
        AsyncStylesheet.INSTANCE.ensureInjected();
    }

    public void setException(Throwable caught) {
        if(isConnectionFailure(caught)) {
            icon.setClassName(Icons.INSTANCE.connectionProblem());
            heading.setInnerText(I18N.CONSTANTS.connectionProblem());
            explanation.setInnerText(I18N.CONSTANTS.connectionProblemText());
        } else {
            icon.setClassName(Icons.INSTANCE.exception());
            heading.setInnerText(I18N.CONSTANTS.unexpectedException());
            explanation.setInnerText(I18N.CONSTANTS.unexpectedExceptionExplanation());
        }

        retryButton.setFocus(true);
    }

    private boolean isConnectionFailure(Throwable caught) {
        return false;
    }

    public HasClickHandlers getRetryButton() {
        return retryButton;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}