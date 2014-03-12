package org.activityinfo.ui.full.client.widget.form;
/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.api.shared.adapter.CuidAdapter;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.api2.shared.validation.*;
import org.activityinfo.api2.shared.validation.widget.NotEmptyStringValidator;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.style.TransitionUtil;
import org.activityinfo.ui.full.client.util.GwtUtil;
import org.activityinfo.ui.full.client.widget.CompositeWithMirror;
import org.activityinfo.ui.full.client.widget.FormFieldTypeCombobox;
import org.activityinfo.ui.full.client.widget.dialog.ChangeFormFieldTypeDialog;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;

/**
 * @author yuriyz on 2/26/14.
 */
public class FormFieldInlineEdit extends CompositeWithMirror {

    private static FormFieldInlineEditBinder uiBinder = GWT
            .create(FormFieldInlineEditBinder.class);

    interface FormFieldInlineEditBinder extends UiBinder<Widget, FormFieldInlineEdit> {
    }

    private FormField formField;
    private boolean editMode = false;
    private FormFieldRow row;
    private FormPanel formPanel;
    private Validator validator;

    @UiField
    TextBox label;
    @UiField
    TextArea description;
    @UiField
    TextBox unit;
    @UiField
    FormFieldTypeCombobox type;
    @UiField
    CheckBox required;
    @UiField
    Button okButton;
    @UiField
    Button cancelButton;
    @UiField
    Button changeButton;
    @UiField
    DivElement unitContainer;
    @UiField
    FormFieldInlineReferenceEdit referencePanel;
    @UiField
    DivElement referenceContainer;
    @UiField
    DivElement errorContainer;

    public FormFieldInlineEdit() {
        TransitionUtil.ensureBootstrapInjected();
        initWidget(uiBinder.createAndBindUi(this));

        referencePanel.setContainer(this);
        type.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                fireState();
            }
        });
        validator = ValidatorBuilder.instance().
                addNotEmptyString(label, I18N.CONSTANTS.fieldLabel()).
                addValidator(createUnitValidator()).
                addValidator(createReferenceValidator()).
                build();
        label.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                fireState();
            }
        });
        unit.addKeyUpHandler(new KeyUpHandler() {
            public void onKeyUp(KeyUpEvent event) {
                fireState();
            }
        });

        fireState();
    }

    private Validator createReferenceValidator() {
        return new Validator() {
            @Override
            public List<ValidationFailure> validate() {
                final List<ValidationFailure> failures = Lists.newArrayList();
                if (type.getSelectedType() == FormFieldType.REFERENCE && getReferencePanel().getInstances().isEmpty()) {
                    final String message = ValidationUtils.format(I18N.CONSTANTS.values(), I18N.CONSTANTS.validationControlIsEmpty());
                    failures.add(new ValidationFailure(new ValidationMessage(message)));
                }
                return failures;
            }
        };
    }

    private Validator createUnitValidator() {
        return new Validator() {
            @Override
            public List<ValidationFailure> validate() {
                if (type.getSelectedType() == FormFieldType.QUANTITY) {
                    final NotEmptyStringValidator emptyStringValidator = new NotEmptyStringValidator(unit, I18N.CONSTANTS.fieldUnit());
                    return emptyStringValidator.validate();
                }
                return Collections.emptyList();
            }
        };
    }

    private void setUnitControlState() {
        final boolean isUnitVisible = type.getSelectedType() == FormFieldType.QUANTITY;
        GwtUtil.setVisible(unitContainer, isUnitVisible);
    }

    private void setReferencePanelState() {
        GwtUtil.setVisible(referenceContainer, type.getSelectedType() == FormFieldType.REFERENCE);
        referencePanel.apply();
    }

    @UiHandler("okButton")
    public void onOk(ClickEvent event) {
        hide();
    }

    @UiHandler("cancelButton")
    public void cancelButton(ClickEvent event) {
        hide();
    }

    @UiHandler("changeButton")
    public void changeButton(ClickEvent event) {
        final ChangeFormFieldTypeDialog dialog = new ChangeFormFieldTypeDialog(formField);
        dialog.show();
        dialog.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                FormFieldInlineEdit.this.type.setSelectedType(dialog.getType().getSelectedType());
            }
        });
    }

    public void hide() {
        setVisible(false);
    }

    public void applyNew(Element... mirrorElements) {
        final Cuid newCuid = CuidAdapter.newFormField();
        final FormField newFormField = new FormField(newCuid);
        newFormField.setType(FormFieldType.FREE_TEXT);
        apply(newFormField, mirrorElements);
    }

    public void apply(@Nonnull FormField formField, Element... mirrorElements) {
        setFormField(formField);
        setMirrorElements(mirrorElements);
        apply();
    }

    private void apply() {
        label.setValue(formField.getLabel().getValue());
        description.setValue(formField.getDescription().getValue());
        unit.setValue(formField.getUnit().getValue());
        type.setSelectedType(formField.getType());
        required.setValue(formField.isRequired());
        setChangeButtonState();
        fireState();
    }

    public void updateModel() {
        formField.setLabel(new LocalizedString(label.getValue()));
        formField.setType(type.getSelectedType());
        formField.setDescription(new LocalizedString(description.getValue()));
        formField.setUnit(new LocalizedString(unit.getValue()));
        formField.setRequired(required.getValue());

        if (type.getSelectedType() == FormFieldType.REFERENCE) {
            referencePanel.updateModel();
        }
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public Button getOkButton() {
        return okButton;
    }

    public Button getCancelButton() {
        return cancelButton;
    }

    public boolean isEditMode() {
        return editMode;
    }

    public void setEditMode(boolean editMode) {
        this.editMode = editMode;
        this.type.setEnabled(!editMode);
        GwtUtil.setVisibleInline(editMode, changeButton.getElement());
        setChangeButtonState();
    }

    private void setChangeButtonState() {
        changeButton.setEnabled(formField != null && !formField.getType().getAllowedConvertTo().isEmpty());
    }

    public void fireState() {
        final List<ValidationFailure> failureList = validator.validate();
        ValidationUtils.show(failureList, errorContainer);

        setUnitControlState();
        setReferencePanelState();

        okButton.setEnabled(failureList.isEmpty() && referencePanel.isInValidState());
    }

    public FormFieldInlineReferenceEdit getReferencePanel() {
        return referencePanel;
    }

    public FormFieldRow getRow() {
        return row;
    }

    public void setRow(FormFieldRow row) {
        this.row = row;
        if (formPanel == null && row != null) {
            formPanel = row.getFormPanel();
        }
    }

    public void showError(String errorMessage) {
        errorContainer.setInnerSafeHtml(SafeHtmlUtils.fromSafeConstant(errorMessage));
    }

    public void clearError() {
        errorContainer.setInnerHTML("");
    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    public void setFormPanel(FormPanel formPanel) {
        this.formPanel = formPanel;
    }
}
