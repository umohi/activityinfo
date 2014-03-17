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
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
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
    }

    public void initOracle(List<FormClass> formClassess) {
        for (FormClass formClass : formClassess) {
            final String labelValue = formClass.getLabel().getValue();
            oracle.add(labelValue);
            labelToCuidBiMap.forcePut(labelValue, formClass.getId());
        }
    }
}
