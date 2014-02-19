package org.activityinfo.ui.full.client.importer.ui;

import com.google.common.collect.Lists;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.Widget;

import java.util.List;

public class BootstrapFormBuilder {

    public interface Templates extends SafeHtmlTemplates {
        @Template("<div class=\"radio\">"
                + "<label>"
                + "<input type=\"radio\" name=\"{0}\" id=\"{1}\">{2}</label></div>")
        SafeHtml radio(String name, String id, SafeHtml label);

        @Template("<form role=\"form\">{0}</form>")
        SafeHtml form(SafeHtml body);
    }

    private static class FormWidget {
        private String id;
        private Widget widget;

        public FormWidget(String id, Widget widget) {
            super();
            this.id = id;
            this.widget = widget;
        }
    }


    public static final Templates TEMPLATES = GWT.create(Templates.class);

    private SafeHtmlBuilder formBody = new SafeHtmlBuilder();
    private List<FormWidget> toBind = Lists.newArrayList();


    public BootstrapFormBuilder() {

    }

    public RadioButton addRadioButton(String groupName, SafeHtml label) {

        String id = HTMLPanel.createUniqueId();
        formBody.append(TEMPLATES.radio(groupName, id, label));

        RadioButton button = new RadioButton(groupName);
        toBind.add(new FormWidget(id, button));

        return button;
    }

    public HTMLPanel buildForm() {
        HTMLPanel panel = new HTMLPanel("form", formBody.toSafeHtml().asString());
        for (FormWidget formWidget : toBind) {
            panel.add(formWidget.widget, formWidget.id);
        }
        return panel;
    }
}
