package org.activityinfo.ui.full.client.component.form;
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

import com.google.common.base.Predicate;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gwt.event.logical.shared.HasValueChangeHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.*;
import org.activityinfo.ui.full.client.widget.undo.IsUndoable;
import org.activityinfo.ui.full.client.widget.undo.UndoManager;
import org.activityinfo.ui.full.client.widget.undo.UndoableCreator;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author yuriyz on 2/19/14.
 */
public class ElementNode {

    private final BiMap<Cuid, FormFieldRow> fieldMap = HashBiMap.create();
    private final BiMap<Cuid, FormSectionRow> sectionMap = HashBiMap.create();

    private final FormPanel formPanel;
    private final FlowPanel contentPanel;
    private final ElementNode parentNode;
    private final UndoManager undoManager;
    private final FormElementContainer formElementContainer;

    public ElementNode(FormPanel formPanel, FlowPanel contentPanel, ElementNode parentNode, FormElementContainer formElementContainer) {
        this.formPanel = formPanel;
        this.contentPanel = contentPanel;
        this.parentNode = parentNode;
        this.undoManager = formPanel.getUndoManager();
        this.formElementContainer = formElementContainer;
    }

    public void clear() {
        contentPanel.clear();
        fieldMap.clear();
        sectionMap.clear();
    }

    /**
     * Renders form element recursively.
     *
     * @param elements elements to render
     */
    public void renderElements(List<FormElement> elements) {
        if (elements != null && !elements.isEmpty()) {
            for (FormElement element : elements) {
                if (element instanceof FormField) {
                    final FormFieldRow fieldRow = new FormFieldRow((FormField) element, formPanel, this);
                    contentPanel.add(fieldRow);
                    fieldMap.put(element.getId(), fieldRow);
                    addValueChangeHandler(fieldRow);
                } else if (element instanceof FormSection) {
                    final FormSection section = (FormSection) element;
                    final FormSectionRow sectionWidget = new FormSectionRow(section, formPanel, this);
                    contentPanel.add(sectionWidget);
                    sectionMap.put(element.getId(), sectionWidget);
                }
            }
        }
    }


    private void addValueChangeHandler(final FormFieldRow fieldRow) {
        final IsWidget widget = fieldRow.getFormFieldWidget();
        if (widget instanceof HasValueChangeHandlers) {
            final HasValueChangeHandlers hasValueChangeHandlers = (HasValueChangeHandlers) widget;
            hasValueChangeHandlers.addValueChangeHandler(new ValueChangeHandler() {
                @Override
                public void onValueChange(ValueChangeEvent event) {
                    final FormInstance formInstance = formPanel.getValue();
                    final Object oldValue = formInstance.get(fieldRow.getFormField().getId());
                    formPanel.getUndoManager().addUndoable(UndoableCreator.create(event, oldValue)); // push undoable
                    formInstance.set(fieldRow.getFormField().getId(), event.getValue());
                }
            });
        }
    }

    public void clearFields(@Nonnull List<FormField> fields) {
        for (FormField field : fields) {
            final FormFieldRow formFieldRow = fieldMap.get(field.getId());
            if (formFieldRow != null) {
                formFieldRow.clear();
            }
        }
    }

    public void remove(final FormFieldRow fieldRow) {
        final int widgetIndex = contentPanel.getWidgetIndex(fieldRow);
        if (widgetIndex != -1) {
            contentPanel.remove(widgetIndex);
            fieldMap.remove(fieldMap.inverse().get(fieldRow));

            final FormField formField = fieldRow.getFormField();
            final int formFieldIndexInClass = formElementContainer.getElements().indexOf(formField);
            formElementContainer.getElements().remove(formFieldIndexInClass);

            final FormInstance formInstance = formPanel.getValue();
            final Object fieldValue = formInstance.getValueMap().get(formField.getId());
            formInstance.getValueMap().remove(formField.getId());

            undoManager.addUndoable(new IsUndoable() {
                @Override
                public void undo() {
                    contentPanel.insert(fieldRow, widgetIndex);
                    fieldMap.put(formField.getId(), fieldRow);
                    formElementContainer.getElements().add(formFieldIndexInClass, formField);
                    formInstance.getValueMap().put(formField.getId(), fieldValue);
                }

                @Override
                public void redo() {
                    contentPanel.remove(widgetIndex);
                    fieldMap.remove(fieldMap.inverse().get(fieldRow));
                    formElementContainer.getElements().remove(formFieldIndexInClass);
                    formInstance.getValueMap().remove(formField.getId());
                }
            });
        }
    }

    public void addField(final FormFieldRow row, int rowIndexOnPanel) {
        final int index = rowIndexOnPanel > 0 ? rowIndexOnPanel : 0;
        final FormField formField = row.getFormField();

        contentPanel.insert(row, index);
        fieldMap.put(formField.getId(), row);
        formElementContainer.getElements().add(index, formField);

        formPanel.getUndoManager().addUndoable(new IsUndoable() {
            @Override
            public void undo() {
                contentPanel.remove(index);
                fieldMap.remove(formField.getId());
                formElementContainer.getElements().remove(index);
            }

            @Override
            public void redo() {
                contentPanel.insert(row, index);
                fieldMap.put(formField.getId(), row);
                formElementContainer.getElements().add(index, formField);
            }
        });
    }

    public void addSection(final FormSection section, int rowIndexOnPanel) {
        final int index = rowIndexOnPanel > 0 ? rowIndexOnPanel : 0;
        final FormSectionRow row = new FormSectionRow(section, formPanel, this);

        contentPanel.insert(row, index);
        sectionMap.put(section.getId(), row);
        formElementContainer.getElements().add(index, section);

        undoManager.addUndoable(new IsUndoable() {
            @Override
            public void undo() {
                contentPanel.remove(index);
                sectionMap.remove(section.getId());
                formElementContainer.getElements().remove(section);
            }

            @Override
            public void redo() {
                contentPanel.insert(row, index);
                sectionMap.put(section.getId(), row);
                formElementContainer.getElements().add(index, section);
            }
        });
    }

    public void remove(final FormSectionRow formSectionRow) {
        final int widgetIndex = contentPanel.getWidgetIndex(formSectionRow);
        if (widgetIndex != -1) {
            contentPanel.remove(widgetIndex);
            sectionMap.remove(sectionMap.inverse().get(formSectionRow));

            final FormSection formSection = formSectionRow.getFormSection();
            final int formSectionIndexInClass = formElementContainer.getElements().indexOf(formSection);
            formElementContainer.getElements().remove(formSectionIndexInClass);

            final Set<Cuid> allFieldIds = formSectionRow.getNode().getOwnAndChildFieldMap().keySet();

            final FormInstance formInstance = formPanel.getValue();
            final Map<Cuid, Object> removedValues = Maps.filterKeys(formInstance.getValueMap(), new Predicate<Cuid>() {
                @Override
                public boolean apply(@Nullable Cuid input) {
                    return allFieldIds.contains(input);
                }
            });

            // order is important : first filter removedValues and only than remove
            formInstance.removeAll(allFieldIds);

            undoManager.addUndoable(new IsUndoable() {
                @Override
                public void undo() {
                    contentPanel.insert(formSectionRow, widgetIndex);
                    sectionMap.put(formSection.getId(), formSectionRow);
                    formElementContainer.getElements().add(formSectionIndexInClass, formSection);
                    formInstance.getValueMap().putAll(removedValues);
                }

                @Override
                public void redo() {
                    contentPanel.remove(widgetIndex);
                    sectionMap.remove(sectionMap.inverse().get(formSectionRow));
                    formElementContainer.getElements().remove(formSectionIndexInClass);
                    formInstance.removeAll(allFieldIds);
                }
            });
        }
    }

    protected void moveUpWidget(final Widget widget, final FormElement formElement, boolean addUndo) {
        final int widgetIndex = contentPanel.getWidgetIndex(widget);
        final FormElementContainer parent = getFormClass().getParent(formElement);
        final int indexInClass = parent.getElements().indexOf(formElement);
        if (widgetIndex > 0 && indexInClass > 0) { // widget is not first and != -1
            contentPanel.remove(widgetIndex);
            contentPanel.insert(widget, (widgetIndex - 1));
            Collections.swap(parent.getElements(), indexInClass, (indexInClass - 1));

            if (addUndo) {
                undoManager.addUndoable(new IsUndoable() {
                    @Override
                    public void undo() {
                        moveDownWidget(widget, formElement, false);
                    }

                    @Override
                    public void redo() {
                        moveUpWidget(widget, formElement, false);
                    }
                });
            }
        }
    }

    public void moveDownWidget(final Widget widget, final FormElement formElement, boolean addUndo) {
        final int widgetIndex = contentPanel.getWidgetIndex(widget);
        final int widgetCount = contentPanel.getWidgetCount();
        final FormElementContainer parent = getFormClass().getParent(formElement);
        final int indexInClass = parent.getElements().indexOf(formElement);

        if (widgetIndex != -1 && (widgetIndex + 1) < widgetCount &&  // widget is found and has "room" to move down
                indexInClass != -1 && (indexInClass + 1) < parent.getElements().size()) { // form field bounds is container elements
            contentPanel.remove(widgetIndex);
            contentPanel.insert(widget, (widgetIndex + 1));
            Collections.swap(parent.getElements(), indexInClass, (indexInClass + 1));

            if (addUndo) {
                undoManager.addUndoable(new IsUndoable() {
                    @Override
                    public void undo() {
                        moveUpWidget(widget, formElement, false);
                    }

                    @Override
                    public void redo() {
                        moveDownWidget(widget, formElement, false);
                    }
                });
            }
        }
    }

    public BiMap<Cuid, FormFieldRow> getFieldMap() {
        return fieldMap;
    }

    public BiMap<Cuid, FormSectionRow> getSectionMap() {
        return sectionMap;
    }

    public BiMap<Cuid, FormFieldRow> getOwnAndChildFieldMap() {
        final BiMap<Cuid, FormFieldRow> ownAndChildFieldMap = HashBiMap.create();
        fillOwnAndChildFieldMap(ownAndChildFieldMap);
        return ownAndChildFieldMap;
    }

    public void fillOwnAndChildFieldMap(BiMap<Cuid, FormFieldRow> ownAndChildFieldMap) {
        ownAndChildFieldMap.putAll(fieldMap);
        for (FormSectionRow sectionRow : sectionMap.values()) {
            sectionRow.fillOwnAndChildFieldMap(ownAndChildFieldMap);
        }
    }

    public ElementNode getParentNode() {
        return parentNode;
    }

    public FlowPanel getContentPanel() {
        return contentPanel;
    }

    public FormClass getFormClass() {
        return formPanel.getFormClass();
    }

    public FormElementContainer getFormElementContainer() {
        return formElementContainer;
    }
}
