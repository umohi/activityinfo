package org.activityinfo.ui.client.component.form;

import com.google.common.base.Strings;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.ui.client.component.form.field.FormFieldWidget;

/**
 * Simple field container which displays the label, input, and help text in a vertical
 * line.
 */
public class VerticalFieldContainer implements FieldContainer {

    public static class Factory implements FieldContainerFactory {
        @Override
        public FieldContainer createContainer(FormField field, FormFieldWidget widget) {
            return new VerticalFieldContainer(field, widget);
        }
    }

    interface VerticalFieldContainerUiBinder extends UiBinder<HTMLPanel, VerticalFieldContainer> {
    }

    private static VerticalFieldContainerUiBinder ourUiBinder = GWT.create(VerticalFieldContainerUiBinder.class);

    private final HTMLPanel formGroup;

    @UiField(provided = true)
    FormFieldWidget fieldWidget;

    @UiField
    LabelElement label;

    @UiField
    SpanElement helpText;

    @UiField
    SpanElement validationMessage;

    public VerticalFieldContainer(FormField formField, FormFieldWidget fieldWidget) {
        this.fieldWidget = fieldWidget;
        formGroup = ourUiBinder.createAndBindUi(this);

        label.setInnerText(formField.getLabel().getValue());

        if(!Strings.isNullOrEmpty(formField.getDescription().getValue())) {
            helpText.removeClassName("hide");
            helpText.setInnerText(formField.getDescription().getValue());
        }
    }


    @Override
    public FormFieldWidget getFieldWidget() {
        return fieldWidget;
    }

    @Override
    public void setValid() {
        formGroup.removeStyleName("has-error");
        validationMessage.addClassName("hide");
    }

    @Override
    public void setInvalid(String message) {
        formGroup.addStyleName("has-error");
        validationMessage.removeClassName("hide");
        validationMessage.setInnerText(message);
    }

    @Override
    public Widget asWidget() {
        return formGroup;
    }

}