package org.activityinfo.ui.client.component.form;
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

import com.google.common.collect.Sets;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.LocalizedString;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldCardinality;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.core.shared.type.converter.Converter;
import org.activityinfo.core.shared.type.converter.ConverterFactory;
import org.activityinfo.core.shared.validation.HasValidator;
import org.activityinfo.core.shared.validation.Validator;
import org.activityinfo.core.shared.validation.ValidatorBuilder;
import org.activityinfo.core.shared.validation.widget.NotEmptyValidator;
import org.activityinfo.legacy.shared.Log;
import org.activityinfo.ui.client.util.GwtUtil;
import org.activityinfo.ui.client.widget.HasReadOnly;
import org.activityinfo.ui.client.widget.undo.IsUndoable;

import java.util.List;
import java.util.Set;

/**
 * @author yuriyz on 1/28/14.
 */
public class FormFieldRow extends Composite implements HasValidator {

    private static FormFieldWidgetUiBinder uiBinder = GWT
            .create(FormFieldWidgetUiBinder.class);

    interface FormFieldWidgetUiBinder extends UiBinder<Widget, FormFieldRow> {
    }

    @UiField
    DivElement label;
    @UiField
    DivElement description;
    @UiField
    DivElement unit;
    @UiField
    FlowPanel control;
    @UiField
    RowToolbar toolbar;
    @UiField
    DivElement rowContainer;
    @UiField
    FormSectionInlineEdit addSectionPanel;
    @UiField
    FormFieldInlineEdit editFieldPanel;
    @UiField
    FormFieldInlineEdit addFieldPanel;
    @UiField
    DivElement requiredMarker;

    private FormField formField;
    private IsWidget formFieldWidget;
    private final ElementNode node;
    private final FormPanel formPanel;
    private final Validator validator = createValidator();

    public FormFieldRow(FormField formField, FormPanel formPanel, final ElementNode node) {
        this(formField, formPanel, node, FormFieldWidgetFactory.create(formField, formPanel));
    }

    public FormFieldRow(FormField formField, FormPanel formPanel, final ElementNode node, IsWidget formFieldWidget) {
        initWidget(uiBinder.createAndBindUi(this));

        this.formField = formField;
        this.node = node;
        this.formPanel = formPanel;
        this.formFieldWidget = formFieldWidget;
        this.toolbar.attach(this);
        this.toolbar.setFormPanel(formPanel);
        this.addSectionPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSectionPanel.updateModel();
                final int rowIndexOnPanel = node.getContentPanel().getWidgetIndex(FormFieldRow.this);
                node.addSection(addSectionPanel.getFormSection(), rowIndexOnPanel);
            }
        });

        initPanels();
        addHandlers();
        updateUI();
        control.add(formFieldWidget);
    }

    private Validator createValidator() {
        final ValidatorBuilder validatorBuilder = ValidatorBuilder.instance();
        if (formFieldWidget instanceof HasValue) {
            validatorBuilder.addValidator(new NotEmptyValidator((HasValue) formFieldWidget, formField.getLabel().getValue()));
        }
        return validatorBuilder.build();
    }

    private void initPanels() {
        editFieldPanel.setRow(this);
        addFieldPanel.setRow(this);

        editFieldPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                final LocalizedString oldLabel = formField.getLabel();
                final LocalizedString oldDescription = formField.getDescription();
                final FormFieldType oldType = formField.getType();
                final LocalizedString oldUnit = formField.getUnit();
                final boolean oldRequired = formField.isRequired();
                final FormFieldCardinality oldCardinality = formField.getCardinality();
                final Set<Cuid> oldRange = formField.getRange();

                editFieldPanel.updateModel();

                final LocalizedString newLabel = formField.getLabel();
                final LocalizedString newDescription = formField.getDescription();
                final FormFieldType newType = formField.getType();
                final LocalizedString newUnit = formField.getUnit();
                final boolean newRequired = formField.isRequired();
                final FormFieldCardinality newCardinality = formField.getCardinality();
                final Set<Cuid> newRange = formField.getRange();

                final FlowPanel widgetContainer = FormFieldRow.this.control;
                final boolean isRangeEqual = oldRange != null && newRange != null &&
                        oldRange.containsAll(newRange) && newRange.containsAll(oldRange);
                final boolean isReferenceChange = (newType == FormFieldType.REFERENCE) &&
                        (oldCardinality != newCardinality || !isRangeEqual);
                final boolean isTypeChanged = oldType != newType;
                final IsWidget oldWidget = FormFieldRow.this.formFieldWidget;

                if (isTypeChanged || isReferenceChange) {
                    widgetContainer.remove(oldWidget);

                    if (isReferenceChange) {
                        final List<FormInstance> currentInstances = editFieldPanel.getReferencePanel().getInstances();
                        FormFieldRow.this.formFieldWidget = new FormFieldWidgetReference(formField, currentInstances);
                    } else {
                        FormFieldRow.this.formFieldWidget = FormFieldWidgetFactory.create(formField, formPanel);
                    }

                    widgetContainer.add(FormFieldRow.this.formFieldWidget);
                }
                final IsWidget newWidget = FormFieldRow.this.formFieldWidget;

                updateUI();
                updateValue(oldType, newType, newWidget);

                formPanel.getUndoManager().addUndoable(new IsUndoable() {
                    @Override
                    public void undo() {
                        formField.setLabel(oldLabel);
                        formField.setDescription(oldDescription);
                        formField.setType(oldType);
                        formField.setUnit(oldUnit);
                        formField.setRequired(oldRequired);
                        formField.setCardinality(oldCardinality);
                        formField.setRange(oldRange);

                        if (isTypeChanged || isReferenceChange) {
                            widgetContainer.remove(newWidget);
                            widgetContainer.add(oldWidget);
                            FormFieldRow.this.formFieldWidget = oldWidget;
                        }

                        updateUI();
                        updateValue(newType, oldType, oldWidget);
                    }

                    @Override
                    public void redo() {
                        formField.setLabel(newLabel);
                        formField.setDescription(newDescription);
                        formField.setType(newType);
                        formField.setUnit(newUnit);
                        formField.setRequired(newRequired);
                        formField.setCardinality(newCardinality);
                        formField.setRange(newRange);

                        if (isTypeChanged || isReferenceChange) {
                            widgetContainer.remove(oldWidget);
                            widgetContainer.add(newWidget);
                            FormFieldRow.this.formFieldWidget = newWidget;
                        }

                        updateUI();
                        updateValue(oldType, newType, newWidget);
                    }
                });
            }
        });
        addFieldPanel.getOkButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addFieldPanel.updateModel();
                int rowIndexOnPanel = node.getContentPanel().getWidgetIndex(FormFieldRow.this);
                node.addField(addFieldPanel.createNewRow(node), rowIndexOnPanel);
            }
        });
    }

    private void updateValue(FormFieldType oldType, FormFieldType newType, IsWidget widgetToUpdate) {
        final Object value = getFormPanel().getValue().get(formField.getId());
        if (widgetToUpdate instanceof HasValue && value != null) {
            final HasValue widget = (HasValue) widgetToUpdate;

            // reference type -> value Set<Cuid>
            if (newType == FormFieldType.REFERENCE) {
                if (oldType == newType && (value instanceof Set || value instanceof Cuid)) {
                    widget.setValue(value);
                }
            } else { // newType != REFERENCE
                Object convertedValue = null;
                final Converter converter = ConverterFactory.createSilently(oldType, newType);
                if (converter != null) {
                    try {
                        convertedValue = converter.convert(value);
                        if (convertedValue != null) {
                            widget.setValue(convertedValue);
                        }
                    } catch (Exception e) {
                        Log.error(e.getMessage(), e);
                    }
                }
                getFormPanel().getValue().set(formField.getId(), convertedValue);
            }
        }
    }

    private void updateUI() {
        label.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getLabel().getValue()));
        description.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getDescription().getValue()));
        unit.setInnerSafeHtml(SafeHtmlUtils.fromString(formField.getUnit().getValue()));
        GwtUtil.setVisibleInline(formField.isRequired(), requiredMarker);
    }

    private void addHandlers() {
        toolbar.getEditButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                editFieldPanel.apply(formField, rowContainer);
                editFieldPanel.setVisible(true);
            }
        });
        toolbar.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addFieldPanel.applyNew();
                addFieldPanel.setVisible(true);
            }
        });
        toolbar.getAddSectionButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                addSectionPanel.applyNew();
                addSectionPanel.setVisible(true);
            }
        });
        toolbar.getRemoveButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.remove(FormFieldRow.this);
            }
        });
        toolbar.getUpButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.moveUpWidget(FormFieldRow.this, formField, true);
            }
        });
        toolbar.getDownButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                node.moveDownWidget(FormFieldRow.this, formField, true);
            }
        });
    }


    public void setValue(Object value) {
        if (value instanceof Cuid && formFieldWidget instanceof FormFieldWidgetReference) { // autofix of wrong data in form instance
            ((FormFieldWidgetReference) formFieldWidget).setValue(Sets.newHashSet((Cuid) value));
        } else if (formFieldWidget instanceof HasValue) { // run here is data in form instance is correct
            ((HasValue) formFieldWidget).setValue(value);
        }
    }

    public Object setValue() {
        if (formFieldWidget instanceof HasValue) {
            return ((HasValue) formFieldWidget).getValue();
        }
        return null;
    }

    public void setReadOnly(boolean readOnly) {
        if (formFieldWidget instanceof ValueBoxBase) {
            ((ValueBoxBase) formFieldWidget).setReadOnly(readOnly);
        } else if (formFieldWidget instanceof HasReadOnly) {
            ((HasReadOnly) formFieldWidget).setReadOnly(readOnly);
        } else {
            Log.error("Widget doesn't support read-only flag");
            assert true;
        }
    }

    public boolean isReadOnly() {
        if (formFieldWidget instanceof ValueBoxBase) {
            return ((ValueBoxBase) formFieldWidget).isReadOnly();
        } else if (formFieldWidget instanceof HasReadOnly) {
            return ((HasReadOnly) formFieldWidget).isReadOnly();
        } else {
            Log.error("Widget doesn't support read-only flag");
            assert true;
            return false;
        }
    }

    @Override
    public Validator getValidator() {
        return validator;
    }

    public IsWidget getFormFieldWidget() {
        return formFieldWidget;
    }

    public void clear() {
        setValue(null);
    }

    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }

    public FormPanel getFormPanel() {
        return formPanel;
    }

    public ElementNode getNode() {
        return node;
    }

    @Override
    public String toString() {
        return "FormFieldRow{" +
                "formField=" + formField +
                '}';
    }
}
