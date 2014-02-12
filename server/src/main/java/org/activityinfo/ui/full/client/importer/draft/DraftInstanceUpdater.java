package org.activityinfo.ui.full.client.importer.draft;

import com.google.common.collect.Lists;
import org.activityinfo.api2.shared.form.tree.FieldPath;
import org.activityinfo.api2.shared.form.tree.FormTree;
import org.activityinfo.ui.full.client.importer.Importer;
import org.activityinfo.ui.full.client.importer.converter.*;
import org.activityinfo.ui.full.client.importer.data.SourceRow;

import java.util.List;
import java.util.Map;

/**
 * Updates a DraftInstance from a SourceRow
 */
public class DraftInstanceUpdater {

    private List<DraftFieldUpdater> fieldUpdaters = Lists.newArrayList();

    public DraftInstanceUpdater(Importer importModel) {

        Map<Integer, FieldPath> columnBindings = importModel.getColumnBindings();
        for(Map.Entry<Integer, FieldPath> binding : columnBindings.entrySet()) {
            FormTree.Node node = importModel.getFormTree().getNodeByPath(binding.getValue());
            fieldUpdaters.add(new DraftFieldUpdater(binding.getKey(), binding.getValue(),
                    ConverterFactory.create(node.getFieldType())));
        }
    }

    public void updateDraft(SourceRow row, DraftInstance instance) {
        for(DraftFieldUpdater fieldUpdater : fieldUpdaters) {
            fieldUpdater.update(row, instance);
        }
    }

}
