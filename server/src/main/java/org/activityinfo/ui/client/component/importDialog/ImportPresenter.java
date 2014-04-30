package org.activityinfo.ui.client.component.importDialog;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.activityinfo.core.client.ResourceLocator;
import org.activityinfo.core.client.form.tree.AsyncFormTreeBuilder;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.importing.model.ImportModel;
import org.activityinfo.core.shared.importing.model.MapExistingAction;
import org.activityinfo.core.shared.importing.strategy.ImportTarget;
import org.activityinfo.fp.client.Promise;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.ui.client.component.importDialog.mapping.ColumnMappingPage;
import org.activityinfo.ui.client.component.importDialog.source.ChooseSourcePage;
import org.activityinfo.ui.client.component.importDialog.validation.ValidationPage;
import org.activityinfo.ui.client.widget.FullScreenOverlay;

import java.util.List;
import java.util.ListIterator;

public class ImportPresenter {

    private final EventBus eventBus = GWT.create(SimpleEventBus.class);

    private final ImportModel importModel;
    private final Importer importer;

    private ImportDialog dialogBox = new ImportDialog();
    private FullScreenOverlay overlay = new FullScreenOverlay();

    private ListIterator<ImportPage> pages;
    private ImportPage currentPage;

    public ImportPresenter(ResourceLocator resourceLocator, FormTree formTree) {
        this.importModel = new ImportModel(formTree);
        this.importer = new Importer(resourceLocator, formTree);

        ChooseSourcePage chooseSourcePage = new ChooseSourcePage(importModel, eventBus);

        ColumnMappingPage matchingPage = new ColumnMappingPage(importModel, createMatchingColumnActions());
        ValidationPage validationPage = new ValidationPage(importModel, importer);

        pages = Lists.<ImportPage>newArrayList(chooseSourcePage, matchingPage, validationPage).listIterator();

        dialogBox.getPreviousButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                previousPage();
            }
        });

        dialogBox.getNextButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                nextPage();
            }
        });

        dialogBox.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                overlay.hide();
            }
        });

        dialogBox.getFinishButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                persistData();
            }
        });

        dialogBox.getTitleWidget().setText(I18N.CONSTANTS.importDialogTitle());
    }

    private List<MapExistingAction> createMatchingColumnActions() {
        final List<MapExistingAction> columnActions = Lists.newArrayList();
        final List<ImportTarget> importTargets = importer.getImportTargets();
        for (ImportTarget target : importTargets) {
            columnActions.add(new MapExistingAction(target));
        }
        return columnActions;
    }

    protected void persistData() {

        dialogBox.getFinishButton().setEnabled(false);
        dialogBox.setStatusText(I18N.CONSTANTS.importing());

        importer.persist(importModel).then(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                showDelayedFailure(caught);
            }

            @Override
            public void onSuccess(Void result) {
                overlay.hide();
                eventBus.fireEvent(new ImportResultEvent(true));
            }
        });
    }

    private void showDelayedFailure(final Throwable caught) {
        // Show failure message only after a short fixed delay to ensure that
        // the progress stage is displayed. Otherwise if we have a synchronous error, clicking
        // the retry button will look like it's not working.
        Scheduler.get().scheduleFixedDelay(new Scheduler.RepeatingCommand() {
            @Override
            public boolean execute() {
                dialogBox.setStatusText(I18N.CONSTANTS.importFailed());
                dialogBox.getFinishButton().setText(I18N.CONSTANTS.retry());
                dialogBox.getFinishButton().setEnabled(true);
                eventBus.fireEvent(new ImportResultEvent(false));
                return false;
            }
        }, 500);
    }

    public void show() {
        gotoPage(pages.next());
        overlay.show(dialogBox);
    }

    private void gotoPage(ImportPage page) {
        currentPage = page;
        currentPage.start();
        dialogBox.setPage(currentPage);

        boolean hasNext = currentPage.hasNextStep() || pages.hasNext();
        boolean hasPrev = currentPage.hasPreviousStep() || pages.hasPrevious();

        dialogBox.getNextButton().setVisible(hasNext);
        dialogBox.getFinishButton().setVisible(!hasNext);
        dialogBox.getFinishButton().setEnabled(currentPage.isValid());
        dialogBox.getPreviousButton().setEnabled(hasPrev);
    }

    private void nextPage() {
        if (currentPage.hasNextStep()) {
            currentPage.nextStep();
        } else if (pages.hasNext()) {
            gotoPage(pages.next());
        }
    }

    private void previousPage() {
        if (currentPage.hasPreviousStep()) {
            currentPage.previousStep();
        } else if (pages.previousIndex() > 0) {
            pages.previous();
            gotoPage(pages.previous());
        }
        dialogBox.setStatusText(""); // clear status text
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public static Promise<ImportPresenter> showPresenter(Cuid activityId, final ResourceLocator resourceLocator) {
        AsyncFormTreeBuilder treeBuilder = new AsyncFormTreeBuilder(resourceLocator);
        return treeBuilder.apply(activityId).then(new Function<FormTree, ImportPresenter>() {
            @Override
            public ImportPresenter apply(FormTree input) {
                return new ImportPresenter(resourceLocator, input);
            }
        });
    }
}
