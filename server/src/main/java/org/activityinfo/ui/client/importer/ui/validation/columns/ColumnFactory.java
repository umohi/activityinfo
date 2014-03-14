package org.activityinfo.ui.client.importer.ui.validation.columns;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;
import org.activityinfo.core.shared.form.FormField;
import org.activityinfo.core.shared.form.FormFieldType;
import org.activityinfo.core.shared.form.tree.FieldPath;
import org.activityinfo.core.shared.form.tree.FormTree;
import org.activityinfo.core.shared.formatter.QuantityFormatterFactory;
import org.activityinfo.ui.client.importer.binding.*;
import org.activityinfo.ui.client.importer.ui.validation.cells.LocalDateRenderer;
import org.activityinfo.ui.client.importer.ui.validation.cells.QuantityRenderer;
import org.activityinfo.ui.client.importer.ui.validation.cells.ValidationCellTemplates;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Predicates.alwaysTrue;

/**
 * Creates a set of validation columns from the {@code FieldBindings}
 */
public class ColumnFactory implements FieldBindingColumnVisitor {

    private final QuantityFormatterFactory formatterFactory;
    private final List<FieldPath> formOrder;
    private ValidationCellTemplates templates;
    private FormTree tree;

    private LinkedList<ImportColumn<?>> columns = Lists.newLinkedList();

    public ColumnFactory(
            QuantityFormatterFactory formatterFactory,
            ValidationCellTemplates templates,
            FormTree tree) {

        this.formatterFactory = formatterFactory;
        this.templates = templates;
        this.tree = tree;
        this.formOrder = tree.search(FormTree.SearchOrder.BREADTH_FIRST, alwaysTrue(), alwaysTrue());
    }

    @Override
    public void visitMatchColumn(MappedReferenceFieldBinding binding, MatchFieldBinding field) {
        FieldPath path = new FieldPath(binding.getFieldId(), field.getRelativeFieldPath());
        FormTree.Node node = tree.getNodeByPath(path);
        Function<Object, String> renderer = createRenderer(node.getField());
        add(new MatchedFieldColumn(templates, binding, field, renderer));
    }

    @Override
    public void visitMappedColumn(MappedDataFieldBinding binding) {
        Function<Object, String> renderer = createRenderer(binding.getField());
        add(new MappedDataColumn(templates, binding, renderer));
    }

    @Override
    public void visitMissingColumn(MissingFieldBinding binding) {
        FormFieldType type = binding.getField().getType();
        if(type == FormFieldType.REFERENCE) {
            add(new MissingReferenceColumn(binding));
        } else {
            add(new MissingDataColumn(binding, createRenderer(binding.getField())));
        }
    }

    private void add(ImportColumn newColumn) {

        int insertPos = -1;

        if(newColumn.getSourceColumn() == -1) {
            // add based on the original field order
            int newIndex = formOrder.indexOf(newColumn.getFieldPath());
            for(int i=0;i!=columns.size();++i) {
                int columnIndex = formOrder.indexOf(columns.get(i).getFieldPath());
                if(columnIndex > newIndex) {
                    insertPos = i;
                    break;
                }
            }
        } else {
            // add based on the SourceTable order
            for(int i=0;i!=columns.size();++i) {
                if(columns.get(i).getSourceColumn() > newColumn.getSourceColumn()) {
                    insertPos = i;
                    break;
                }
            }
        }

        if(insertPos == -1) {
            columns.add(newColumn);
        } else {
            columns.add(insertPos, newColumn);
        }
    }

    private Function<Object, String> createRenderer(FormField field) {
        switch(field.getType()) {
            case FREE_TEXT:
            case NARRATIVE:
                return Functions.toStringFunction();
            case QUANTITY:
                return new QuantityRenderer(formatterFactory.create());
            case LOCAL_DATE:
                return new LocalDateRenderer();
        }
        throw new IllegalArgumentException(field.getType().name());
    }

    public List<ImportColumn<?>> create(List<FieldBinding> bindings) {
        for(FieldBinding binding : bindings) {
            binding.accept(this);
        }
        return columns;
    }
}
