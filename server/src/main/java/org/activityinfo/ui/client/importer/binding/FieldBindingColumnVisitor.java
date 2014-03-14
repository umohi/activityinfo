package org.activityinfo.ui.client.importer.binding;


public interface FieldBindingColumnVisitor {


    void visitMatchColumn(MappedReferenceFieldBinding binding, MatchFieldBinding field);

    void visitMappedColumn(MappedDataFieldBinding binding);

    void visitMissingColumn(MissingFieldBinding binding);


}
