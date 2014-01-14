package org.activityinfo.client.importer;

import org.activityinfo.client.dispatch.Dispatcher;
import org.activityinfo.client.importer.binding.DraftModel;
import org.activityinfo.client.importer.binding.ImportModel;
import org.activityinfo.client.importer.binding.InstanceImporter;
import org.activityinfo.client.importer.page.ChooseSourcePage;
import org.activityinfo.client.importer.page.ColumnMappingPage;
import org.activityinfo.client.importer.page.ImportSchemaView;
import org.activityinfo.client.importer.page.ValidationPage;
import org.activityinfo.client.widget.FullScreenOverlay;
import org.activityinfo.shared.command.BatchCommand;
import org.activityinfo.shared.command.result.BatchResult;
import org.apache.poi.ss.formula.functions.T;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class ImportSchemaPresenter {
	
	private ImportDialog dialogBox = new ImportDialog();
	private FullScreenOverlay overlay = new FullScreenOverlay();
	
	private ImportSchemaView view;
	
	private enum Step {
		CHOOSE_SOURCE,
		COLUMN_MATCHING,
		VALIDATION
	}
	
	private Step currentStep;

	private ImportModel<T> importModel;

	private Dispatcher dispatcher;
	
	public ImportPresenter(Dispatcher dispatcher, InstanceImporter binder) {
		this.dispatcher = dispatcher;
		this.importModel = new ImportModel<T>(binder);
		
		chooseSourcePage = new ChooseSourcePage(eventBus);
		matchingPage = new ColumnMappingPage<T>(importModel);
		validationPage = new ValidationPage<T>(importModel);

		
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
	}

	protected void submitData() {
		
		dialogBox.getFinishButton().setEnabled(false);
		dialogBox.setStatusText("Importing...");
		
		BatchCommand batch = new BatchCommand();
		for(DraftModel draftModel : importModel.getDraftModels()) {
			batch.add(importModel.getBinder().createCommand(draftModel));
		}
		
		dispatcher.execute(batch, new AsyncCallback<BatchResult>() {

			@Override
			public void onFailure(Throwable caught) {
				dialogBox.setStatusText("Import failed: " + caught.getMessage());
			}

			@Override
			public void onSuccess(BatchResult result) {
				overlay.hide();
			}
		});
	}

	public void show() {
		setStep(Step.CHOOSE_SOURCE);
		overlay.show(dialogBox);
	}

	private void setStep(Step step) {
		this.currentStep = step;
		switch(step) {
		case CHOOSE_SOURCE:
			dialogBox.setPage(chooseSourcePage);
			break;
		case COLUMN_MATCHING:
			importModel.setSource(chooseSourcePage.getImportSource());
			matchingPage.refresh();
			dialogBox.setPage(matchingPage);
			break;
		case VALIDATION:
			importModel.updateDrafts();
			validationPage.refresh();
			dialogBox.setPage(validationPage);
			break;
		}
		boolean lastPage = currentStep.ordinal() + 1 == Step.values().length;
		dialogBox.getNextButton().setVisible(!lastPage);
		dialogBox.getFinishButton().setVisible(lastPage);
	}

	private void nextPage() {
		setStep(Step.values()[currentStep.ordinal()+1]);
	}
}
