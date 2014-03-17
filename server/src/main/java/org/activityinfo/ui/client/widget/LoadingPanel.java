package org.activityinfo.ui.client.widget;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.gwt.core.client.Duration;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.ui.client.widget.async.FailureWidget;
import org.activityinfo.ui.client.widget.async.LoadingWidget;

import javax.annotation.Nullable;
import javax.inject.Provider;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SimpleLayoutPanel for widgets that need to be asynchronously
 * created
 */
public class LoadingPanel<V> implements IsWidget {

    private static final Logger LOGGER = Logger.getLogger(LoadingPanel.class.getName());

    /**
     * We get confused when things fail to quickly. I mean really, it's like you're not
     * even trying...
     */
    public static final int FAILURE_MIN_DELAY = 1000;

    private final SimplePanel panel = new SimplePanel();

    /**
     * A function which provides the widget based on the resolved value
     */
    private Function<? super V, ? extends DisplayWidget<? super V>> widgetProvider;

    /**
     * Provides the value to be displayed by the display widget. We use a provider so that
     * can retry if it fails at first.
     */
    private Provider<Promise<V>> valueProvider;

    private FailureWidget failureWidget;
    private LoadingWidget loadingWidget;

    private int currentRequestNumber = 0;

    public void setDisplayWidget(DisplayWidget<? super V> widget) {
        this.widgetProvider = Functions.constant(widget);
    }

    public void setDisplayWidgetProvider(Function<V, ? extends DisplayWidget<? super V>> function) {
        this.widgetProvider = function;
    }

    public Promise<Void> show(Provider<Promise<V>> provider) {
        this.valueProvider = provider;
        return tryLoad(provider);
    }

    public <T> Promise<Void> show(final Function<T, Promise<V>> function, final T argument) {
        return show(new Provider<Promise<V>>() {

            @Override
            public Promise<V> get() {
                return function.apply(argument);
            }
        });
    }

    private Promise<Void> tryLoad(Provider<Promise<V>> provider) {
        Promise<V> promisedValue = provider.get();

        // make sure we only react to the last request submitted...
        final int requestNumber = currentRequestNumber+1;
        this.currentRequestNumber = requestNumber;

        if(promisedValue.getState() != Promise.State.FULFILLED) {
            showLoadingIndicator();
        }

        Promise<Void> loadResult = promisedValue.then(new Function<V, Void>() {
            @Override
            public Void apply(@Nullable V result) {
                if (requestNumber == currentRequestNumber) {
                    try {
                        showWidget(result);
                    } catch (Throwable e) {
                        showLoadFailure(requestNumber, e);
                    }
                }
                return null;
            }
        });

        // handle failure (including the failure of the show() method of our
        // display widget)

        loadResult.then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                if (requestNumber == currentRequestNumber) {
                    showLoadFailure(requestNumber, caught);
                }
            }

            @Override
            public void onSuccess(Void result) {
                // already handled above
            }
        });

        return loadResult;
    }

    private void showLoadFailure(final int requestNumber, Throwable caught) {

        LOGGER.log(Level.SEVERE, "Load failed", caught);

        // the failure may have been caught upstream
        if(!panel.isAttached()) {
            return;
        }

        if(failureWidget == null) {
            failureWidget = new FailureWidget();
            failureWidget.getRetryButton().addClickHandler(new ClickHandler() {
                @Override
                public void onClick(ClickEvent event) {
                    tryLoad(valueProvider);
                }
            });
        }
        failureWidget.setException(caught);

        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                if(requestNumber == currentRequestNumber) {
                    panel.setWidget(failureWidget);
                }
                return false;
            }
        }, FAILURE_MIN_DELAY);
    }

    private void showLoadingIndicator() {
        if(loadingWidget == null) {
            loadingWidget = new LoadingWidget();
        }
        panel.setWidget(loadingWidget);
    }

    private void showWidget(V result) {
        assert widgetProvider != null : "No widget/provider has been set!";
        DisplayWidget<? super V> displayWidget = widgetProvider.apply(result);
        displayWidget.show(result);
        panel.setWidget(displayWidget);
    }

    @Override
    public Widget asWidget() {
        return panel;
    }

}
