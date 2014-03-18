package org.activityinfo.ui.client.widget.loading;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.cellview.client.LoadingStateChangeEvent;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.ui.client.widget.Button;
import org.activityinfo.ui.client.widget.ButtonWithSize;

/**
 * Displays the loading state of a table
 */
public class TableLoadingIndicator implements IsWidget, LoadingView {

    private final HTMLPanel rootElement;
    private Button retryButton;

    interface LoadingIndicatorUiBinder extends UiBinder<HTMLPanel, TableLoadingIndicator> {
    }

    private static LoadingIndicatorUiBinder ourUiBinder = GWT.create(LoadingIndicatorUiBinder.class);

    public TableLoadingIndicator() {
        rootElement = ourUiBinder.createAndBindUi(this);
        retryButton = new Button(ButtonWithSize.ButtonStyle.DEFAULT);
    }

    @Override
    public void onLoadingStateChanged(LoadingState state, Throwable caught) {
        ExceptionOracle.setLoadingStyle(rootElement, LoadingState.LOADING);
    }

    @Override
    public HasClickHandlers getRetryButton() {
        return retryButton;
    }

    @Override
    public Widget asWidget() {
        return rootElement;
    }
}