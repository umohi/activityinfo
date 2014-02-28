package org.activityinfo.ui.full.client.importer.ui;

import com.google.common.collect.Lists;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import org.activityinfo.api2.client.ResourceLocator;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.model.ImportModel;
import org.activityinfo.ui.full.client.importer.ui.mapping.FieldChoicePresenter;
import org.activityinfo.ui.full.client.importer.ui.source.ChooseSourcePage;
import org.activityinfo.ui.full.client.importer.ui.mapping.ColumnMappingPage;
import org.activityinfo.ui.full.client.importer.ui.validation.ValidationPage;
import org.activityinfo.ui.full.client.widget.FullScreenOverlay;

import java.util.ListIterator;

public class ImportPresenter {

    private EventBus eventBus = GWT.create(SimpleEventBus.class);


    private final ImportModel importModel;
    private final Importer importer;


    private ImportDialog dialogBox = new ImportDialog();
    private FullScreenOverlay overlay = new FullScreenOverlay();

    private ListIterator<ImportPage> pages;
    private ImportPage currentPage;


    public ImportPresenter(ResourceLocator dispatcher, FormTree formTree) {
        this.importModel = new ImportModel(formTree);
        this.importer = new Importer(Scheduler.get(), dispatcher, importModel);

        ChooseSourcePage chooseSourcePage = new ChooseSourcePage(importModel, eventBus);
        ColumnMappingPage matchingPage = new ColumnMappingPage(importModel, new FieldChoicePresenter(importModel));
        ValidationPage validationPage = new ValidationPage(importer);

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
                submitData();
            }
        });

        dialogBox.getTitleWidget().setText("Import data from a spreadsheet");
    }

    protected void submitData() {

        dialogBox.getFinishButton().setEnabled(false);
        dialogBox.setStatusText("Importing...");



//        BatchCommand batch = new BatchCommand();
//        for (DraftModel draftModel : importer.getDraftModels()) {
//            //batch.add(importer.getBinder().createCommand(draftModel));
//        }
//
//        dispatcher.execute(batch, new AsyncCallback<BatchResult>() {
//
//            @Override
//            public void onFailure(Throwable caught) {
//                dialogBox.setStatusText("Import failed: " + caught.getMessage());
//            }
//
//            @Override
//            public void onSuccess(BatchResult result) {
//                overlay.hide();
//            }
//        });
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
        dialogBox.getPreviousButton().setEnabled(hasPrev);
    }

    private void nextPage() {
        if(currentPage.hasNextStep()) {
            currentPage.nextStep();
        } else if(pages.hasNext()) {
            gotoPage(pages.next());
        }
    }

    private void previousPage() {
        if(currentPage.hasPreviousStep()) {
            currentPage.previousStep();
        } else if(pages.previousIndex() > 0) {
            pages.previous();
            gotoPage(pages.previous());
        }
    }

}
