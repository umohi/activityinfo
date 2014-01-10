package org.activityinfo.client.importer;

import org.activityinfo.client.importer.binding.Binder;
import org.activityinfo.client.importer.binding.ImportModel;
import org.activityinfo.client.importer.page.ChooseSourcePage;
import org.activityinfo.client.importer.page.ColumnMappingPage;
import org.activityinfo.client.importer.page.ValidationGrid;
import org.activityinfo.client.importer.page.ValidationPage;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.Window;

public class ImportPresenter<T> {

	private EventBus eventBus = GWT.create(SimpleEventBus.class);
	
	private ImportDialog dialogBox = new ImportDialog();
	
	private ChooseSourcePage chooseSourcePage;
	private ColumnMappingPage<T> matchingPage;
	private ValidationPage<T> validationPage;
	
	private enum Step {
		CHOOSE_SOURCE,
		COLUMN_MATCHING,
		VALIDATION
	}
	
	private Step currentStep;

	private ImportModel<T> model;
	
	public ImportPresenter(Binder<T> binder) {
		this.model = new ImportModel<T>(binder);
		
		chooseSourcePage = new ChooseSourcePage(eventBus);
		matchingPage = new ColumnMappingPage<T>(model);
		validationPage = new ValidationPage<T>(model);
		
		dialogBox.setWidth( frac(Window.getClientWidth(), 0.90));
		dialogBox.setHeight( frac(Window.getClientHeight(), 0.80));
		
		dialogBox.getNextButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				nextPage();
			}
		});
		
		dialogBox.getCancelButton().addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				dialogBox.hide();
			}
		});		
	}
	
	private String frac(double size, double proportion) {
		int fracSize = (int) (size * proportion);
		return fracSize + "px";
	}

	public void show() {
		setStep(Step.CHOOSE_SOURCE);
		dialogBox.center();
	}

	private void setStep(Step step) {
		this.currentStep = step;
		switch(step) {
		case CHOOSE_SOURCE:
			dialogBox.setPage(chooseSourcePage);
			break;
		case COLUMN_MATCHING:
			model.setSource(chooseSourcePage.getImportSource());
			matchingPage.refresh();
			dialogBox.setPage(matchingPage);
			break;
		case VALIDATION:
			validationPage.refresh();
			dialogBox.setPage(validationPage);
			break;
		}
		dialogBox.getNextButton().setEnabled(currentStep.ordinal() + 1 < Step.values().length);
	}

	private void nextPage() {
		setStep(Step.values()[currentStep.ordinal()+1]);
	}
}
