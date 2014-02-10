package org.activityinfo.api2.shared.form.tree;

import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.activityinfo.api2.shared.Cuid;
import org.activityinfo.api2.shared.Iri;
import org.activityinfo.api2.shared.LocalizedString;
import org.activityinfo.api2.shared.form.FormClass;
import org.activityinfo.api2.shared.form.FormField;
import org.activityinfo.api2.shared.form.FormFieldType;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Contains a tree of fields based on references to other {@code FormClasses}
 *
 *
 */
public class FormTree {


    public class Node {

        private String id;
        private Node parent;
        private Cuid parentField;
        private FormField field;

        private FieldPath path;
        private FormClass formClass;

        private List<Node> children = Lists.newArrayList();

        public boolean isRoot() {
            return parent == null;
        }

        public boolean isReference() {
            return field.getType() == FormFieldType.REFERENCE;
        }

        public void addChildren(FormClass formClass) {
            Preconditions.checkState(this.formClass == null);
            this.formClass = formClass;
            for (FormField property : formClass.getFields()) {
                Preconditions.checkNotNull(property);

                FormTree.Node childNode = new FormTree.Node();
                childNode.parent = this;
                childNode.field = property;
                childNode.parentField = property.getId();
                childNode.path = new FieldPath(this.path, property);
                children.add(childNode);
                nodeMap.put(childNode.path, childNode);
            }
        }

        public List<Node> getChildren() {
            return children;
        }

        public FieldPath getPath() {
            return path;
        }

        public Cuid getFieldId() {
            return field.getId();
        }

        public FormClass getFormClass() {
            return formClass;
        }

        public Set<Iri> getRange() {
            return field.getRange();
        }

        public FormFieldType getFieldType() {
            return field.getType();
        }

        public String debugPath() {
            StringBuilder path = new StringBuilder();
            path.append(this);
            Node parent = this.parent;
            while(parent != null) {
                path.insert(0, parent + " -> ");
                parent = parent.parent;
            }
            return path.toString();
        }

        @Override
        public String toString() {
            if(isRoot()) {
                return "ROOT";
            } else if(formClass != null) {
                return toString(field.getLabel()) + ":" + toString(formClass.getLabel());
            } else {
                return toString(field.getLabel()) + ":" + field.getType().name();
            }
        }

        private String toString(LocalizedString label) {
            return "[" + label.getValue() + "]";
        }
    }

    public enum SearchOrder {
        DEPTH_FIRST,
        BREADTH_FIRST
    }

    private Node root;
    private Map<FieldPath, Node> nodeMap = Maps.newHashMap();

    public FormTree() {
        root = new Node();
    }

    public Node getRoot() {
        return root;
    }

    public Node getNodeByPath(FieldPath path) {
        Node node = nodeMap.get(path);
        if (node == null) {
            throw new IllegalArgumentException();
        }
        return node;
    }

    private void findLeaves(List<Node> leaves, Node node) {
        if (node.children.isEmpty()) {
            leaves.add(node);
        } else {
            for (Node child : node.children) {
                findLeaves(leaves, child);
            }
        }
    }

    public List<Node> getLeaves() {
        List<Node> leaves = Lists.newArrayList();
        findLeaves(leaves, root);
        return leaves;
    }

    public List<FieldPath> search(SearchOrder order, Predicate<? super Node> descendPredicate, Predicate<Node> matchPredicate) {
        List<FieldPath> paths = Lists.newArrayList();
        search(paths, root, order, descendPredicate, matchPredicate);
        return paths;
    }

    private void search(List<FieldPath> paths, Node parent, SearchOrder searchOrder,
                        Predicate<? super Node> descendPredicate, Predicate<Node> matchPredicate) {
        if (searchOrder == SearchOrder.BREADTH_FIRST && !parent.isRoot() && matchPredicate.apply(parent)) {
            paths.add(parent.path);
        }
        if (parent.isRoot() || descendPredicate.apply(parent)) {
            for (Node child : parent.children) {
                search(paths, child, searchOrder, descendPredicate, matchPredicate);
            }
        }
        if (searchOrder == SearchOrder.DEPTH_FIRST && !parent.isRoot() && matchPredicate.apply(parent)) {
            paths.add(parent.path);
        }
    }

    public static Predicate<Node> isDataTypeProperty() {
        return new Predicate<FormTree.Node>() {

            @Override
            public boolean apply(Node input) {
                return input.field.getType() != FormFieldType.REFERENCE;
            }
        };
    }

    public static Predicate<Node> isReference() {
        return Predicates.not(isDataTypeProperty());
    }

    public static Predicate<Node> pathIn(final Collection<FieldPath> paths) {
        return new Predicate<FormTree.Node>() {

            @Override
            public boolean apply(Node input) {
                return paths.contains(input.path);
            }
        };
    }

    public static Predicate<Node> pathNotIn(final Collection<FieldPath> paths) {
        return Predicates.not(pathIn(paths));
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for(Node node : getLeaves()) {
            s.append(node.debugPath()).append("\n");
        }
        return s.toString();
    }
}
