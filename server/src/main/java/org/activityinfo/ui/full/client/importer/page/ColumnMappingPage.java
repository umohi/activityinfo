package org.activityinfo.ui.full.client.importer.page;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.ResizeComposite;
import com.google.gwt.user.client.ui.Widget;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.ui.full.client.importer.Importer;
import org.activityinfo.ui.full.client.importer.page.ColumnSelectionChangedEvent.Handler;

/**
 * Page that allows the user to match columns in an imported table to
 * an existing structure.
 */
public class ColumnMappingPage<T> extends ResizeComposite {

    public static int nextUniqueGroupNum = 1;

    private static ColumnMatchingPanelUiBinder uiBinder = GWT
            .create(ColumnMatchingPanelUiBinder.class);

    interface ColumnMatchingPanelUiBinder extends
            UiBinder<Widget, ColumnMappingPage<?>> {
    }

    interface Style extends CssResource {
        String selectedColumn();
    }

    @UiField
    Style style;

    @UiField(provided = true)
    ColumnMappingGrid<T> dataGrid;

    @UiField
    HeadingElement columnChooserHeader;
    @UiField(provided = true)
    PropertyChooser propertyChooser;

    private final Importer<T> importer;

    private int selectedColumnIndex;

    public ColumnMappingPage(Importer<T> importer) {
        this.importer = importer;

        dataGrid = new ColumnMappingGrid<T>(importer);
        propertyChooser = new PropertyChooser(importer.getFieldsToMatch());

        initWidget(uiBinder.createAndBindUi(this));

        dataGrid.addColumnSelectionChangedHandler(new Handler() {

            @Override
            public void onColumnSelectionChanged(ColumnSelectionChangedEvent e) {
                onColumnChanged(e);
            }
        });

        propertyChooser.addValueChangeHandler(new ValueChangeHandler<FieldPath>() {

            @Override
            public void onValueChange(ValueChangeEvent<FieldPath> event) {
                updateColumnMapping(event.getValue());
            }
        });
    }

    public void refresh() {
        dataGrid.refresh();
    }

    private void onColumnChanged(ColumnSelectionChangedEvent e) {
        selectedColumnIndex = e.getSelectedColumnIndex();
        propertyChooser.setValue(importer.getColumnBindings().get(selectedColumnIndex), false);
        columnChooserHeader.setInnerText(importer.getSource().getColumnHeader(selectedColumnIndex));
    }

    private void updateColumnMapping(FieldPath property) {

        if (property == null) {
            importer.clearColumnBinding(selectedColumnIndex);
        } else {
            importer.setColumnBinding(property, selectedColumnIndex);
        }

        dataGrid.refreshMappings();
    }
}