package org.activityinfo.ui.client.component.table.action;
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

import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import org.activityinfo.core.shared.Cuid;
import org.activityinfo.core.shared.form.FormClass;
import org.activityinfo.core.shared.form.FormInstance;
import org.activityinfo.i18n.shared.I18N;
import org.activityinfo.legacy.shared.adapter.CuidAdapter;
import org.activityinfo.ui.client.component.form.FormDialog;
import org.activityinfo.ui.client.component.form.FormDialogCallback;
import org.activityinfo.ui.client.component.table.InstanceTable;
import org.activityinfo.ui.client.style.Icons;

/**
 * @author yuriyz on 4/8/14.
 */
public class NewInstanceButton extends ButtonCell {

    public interface Template extends SafeHtmlTemplates {
        @SafeHtmlTemplates.Template("<div class='pull-right'>" +
                "<button class='btn btn-default btn-xs' type='button' tabindex='-1'><span class='{0}'>{1}</span></button>" +
                "</div>")
        SafeHtml button(String icon, String text);
    }

    public static final Template BUTTON_TEMPLATE = GWT.create(Template.class);

    private final InstanceTable table;

    public NewInstanceButton(InstanceTable table) {
        super();
        this.table = table;
    }

    @Override
    public void render(Context context, String value, SafeHtmlBuilder sb) {
        sb.append(BUTTON_TEMPLATE.button(Icons.INSTANCE.add(), I18N.CONSTANTS.newText()));
    }

    @Override
    protected void onEnterKeyDown(Context context, Element parent, String value,
                                  NativeEvent event, ValueUpdater<String> valueUpdater) {
        final FormClass formClass = table.getRootFormClass();
        final Cuid instanceId = CuidAdapter.newFormInstance(formClass.getId());
        FormDialog dialog = new FormDialog(table.getResourceLocator());
        dialog.setDialogTitle(I18N.CONSTANTS.addInstance());
        dialog.show(formClass.getId(), instanceId, new FormDialogCallback() {
            @Override
            public void onPersisted(FormInstance instance) {
                table.reload();
            }
        });
    }
}
