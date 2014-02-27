package org.activityinfo.ui.full.client.importer.ui.mapping;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.form.FormFieldType;
import org.activityinfo.api2.shared.form.tree.FieldComponent;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.api2.shared.model.CoordinateAxis;
import org.activityinfo.ui.full.client.i18n.I18N;
import org.activityinfo.ui.full.client.importer.data.SourceColumn;
import org.activityinfo.ui.full.client.importer.model.ColumnTarget;
import org.activityinfo.ui.full.client.importer.model.ImportModel;

import java.util.List;

/**
 * Creates the list of options for each column
 */
public class ColumnOptionsFactory {

    private final ImportModel model;

    public ColumnOptionsFactory(ImportModel model) {
        this.model = model;
    }


    public List<ColumnOption> getOptions() {

        List<ColumnOption> options = Lists.newArrayList();

        // ignore is always an option...
        options.add(new ColumnOption(I18N.CONSTANTS.ignoreColumnAction(), ColumnTarget.ignored()));

        // add existing fields
        collectFields(model.getFormTree().getRoot(), options);

        return options;

    }

    private void collectFields(FormTree.Node parent, List<ColumnOption> options) {
        for(FormTree.Node node : parent.getChildren()) {
            switch(node.getFieldType()) {
                case REFERENCE:
                    collectFields(node, options);
                    break;
                case GEOGRAPHIC_POINT:
                    options.add(new ColumnOption(I18N.CONSTANTS.latitude(),
                            node.getPath().component(FieldComponent.LATITUDE)));
                    options.add(new ColumnOption(I18N.CONSTANTS.longitude(),
                            node.getPath().component(FieldComponent.LONGITUDE)));
                    break;
                default:
                    options.add(new ColumnOption(label(node), node.getPath()));
                    break;
            }
        }
    }

    private String label(FormTree.Node node) {
        if(node.getPath().isNested()) {
            return node.getParent().getField().getLabel() + " " +
                    node.getField().getLabel().getValue();
        } else {
            return node.getField().getLabel().getValue();
        }
    }
}
