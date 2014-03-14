package org.activityinfo.core.shared.importing.binding;


public interface FieldBindingColumnVisitor {


    void visitMatchColumn(MappedReferenceFieldBinding binding, MatchFieldBinding field);

    void visitMappedColumn(MappedDataFieldBinding binding);

    void visitMissingColumn(MissingFieldBinding binding);


}
