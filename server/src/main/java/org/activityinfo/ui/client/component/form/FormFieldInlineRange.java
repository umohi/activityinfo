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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.criteria.ClassCriteria;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.legacy.client.callback.SuccessCallback;
import org.activityinfo.ui.client.widget.SuggestBox;

import java.util.List;

/**
 * @author yuriyz on 3/17/14.
 */
public class FormFieldInlineRange extends Composite {

    private static FormFieldInlineRangeUiBinder uiBinder = GWT
            .create(FormFieldInlineRangeUiBinder.class);

    interface FormFieldInlineRangeUiBinder extends UiBinder<Widget, FormFieldInlineRange> {
    }

    private final BiMap<String, Cuid> labelToCuidBiMap = HashBiMap.create();
    private final MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
    private FormPanel formPanel;

    @UiField
    ListBox selectedList;
    @UiField(provided = true)
    SuggestBox suggestBox;
    @UiField
    Button addButton;
    @UiField
    Button removeButton;
    @UiField
    Button editButton;

    public FormFieldInlineRange() {
        suggestBox = new SuggestBox(oracle);
        initWidget(uiBinder.createAndBindUi(this));
        setRemoveButtonState();
        selectedList.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                setRemoveButtonState();
            }
        });
    }

    private void setRemoveButtonState() {
        removeButton.setEnabled(selectedList.getSelectedIndex() != -1);
    }

    public void init(FormPanel formPanel) {
        this.formPanel = formPanel;
        if (formPanel != null) {
            this.formPanel.getResourceLocator().queryInstances(new ClassCriteria(FormClass.CLASS_ID)).then(new SuccessCallback<List<FormInstance>>() {
                @Override
                public void onSuccess(List<FormInstance> result) {
                    initOracle(result);
                }
            });
        }
    }

    public void initOracle(List<FormInstance> formClassess) {
        for (FormInstance formClass : formClassess) {
            final String labelValue = formClass.getString(FormClass.LABEL_FIELD_ID);
            oracle.add(labelValue);
            labelToCuidBiMap.forcePut(labelValue, formClass.getId());
        }
    }

    @UiHandler("addButton")
    public void onAddButton(ClickEvent event) {
        final String label = suggestBox.getValue();
        final Cuid cuid = labelToCuidBiMap.get(label);
        if (cuid != null && !hasListValue(cuid)) {
            selectedList.addItem(label, cuid.asString());
            suggestBox.setValue(""); // clear box
            fireState();
        }
    }

    @UiHandler("removeButton")
    public void onRemove(ClickEvent event) {
        for (int i = 0; i < selectedList.getItemCount(); i++) {
            if (selectedList.isItemSelected(i)) {
                selectedList.removeItem(i);
            }
        }
        fireState();
    }

    public List<Cuid> getSelectedFormClasses() {
        final List<Cuid> selectedCuids = Lists.newArrayList();
        for (int i = 0; i < selectedList.getItemCount(); i++) {
            selectedCuids.add(new Cuid(selectedList.getValue(i)));
        }
        return selectedCuids;
    }

    public void fireState() {

    }

    private boolean hasListValue(Cuid cuid) {
        final int itemCount = selectedList.getItemCount();
        for (int i = 0; i < itemCount; i++) {
            final String value = selectedList.getValue(i);
            if (value.equals(cuid.asString())) {
                return true;
            }
        }
        return false;
    }
}
