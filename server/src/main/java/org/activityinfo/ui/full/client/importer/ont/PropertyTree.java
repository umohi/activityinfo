package org.activityinfo.ui.full.client.importer.ont;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class PropertyTree {

    public static class Node {

        private String id;
        private Node parent;
        private String parentProperty;
        private Property property;

        private PropertyPath path;
        private OntClass ontClass;

        private List<Node> children = Lists.newArrayList();

        public boolean isRoot() {
            return parent == null;
        }

        public List<Node> getChildren() {
            return children;
        }

        public PropertyPath getPath() {
            return path;
        }

        public String getPropertyId() {
            return property.getId();
        }
    }

    public enum SearchOrder {
        DEPTH_FIRST,
        BREADTH_FIRST
    }

    private OntClassResolver resolver;

    private Node rootNode;
    private Map<PropertyPath, Node> nodeMap = Maps.newHashMap();

    public PropertyTree(OntClass root, OntClassResolver resolver) {
        this.resolver = resolver;

        rootNode = new Node();
        rootNode.ontClass = root;

        addChildNodes(rootNode);
    }

    public Node getNodeByPath(PropertyPath path) {
        Node node = nodeMap.get(path);
        if (node == null) {
            throw new IllegalArgumentException();
        }
        return node;
    }

    private void addChildNodes(Node parentNode) {
        for (Property property : parentNode.ontClass.getProperties()) {
            Node childNode = new Node();
            childNode.parent = parentNode;
            childNode.property = property;
            childNode.parentProperty = property.getId();
            childNode.path = new PropertyPath(parentNode.path, property);

            parentNode.children.add(childNode);
            nodeMap.put(childNode.path, childNode);

            if (property instanceof ObjectProperty) {
                ObjectProperty objectProperty = (ObjectProperty) property;
                childNode.ontClass = this.resolver.resolveOntClass(objectProperty.getRange());

                addChildNodes(childNode);
            }
        }
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
        findLeaves(leaves, rootNode);
        return leaves;
    }

    public List<PropertyPath> search(SearchOrder order, Predicate<Node> descendPredicate, Predicate<Node> matchPredicate) {
        List<PropertyPath> paths = Lists.newArrayList();
        search(paths, rootNode, order, descendPredicate, matchPredicate);
        return paths;
    }

    private void search(List<PropertyPath> paths, Node parent, SearchOrder searchOrder,
                        Predicate<Node> descendPredicate, Predicate<Node> matchPredicate) {
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
        return new Predicate<PropertyTree.Node>() {

            @Override
            public boolean apply(Node input) {
                return input.property instanceof DataTypeProperty;
            }
        };
    }

    public static Predicate<Node> isObjectProperty() {
        return Predicates.not(isDataTypeProperty());
    }

    public static Predicate<Node> pathIn(final Collection<PropertyPath> paths) {
        return new Predicate<PropertyTree.Node>() {

            @Override
            public boolean apply(Node input) {
                return paths.contains(input.path);
            }
        };
    }

    public static Predicate<Node> pathNotIn(final Collection<PropertyPath> paths) {
        return Predicates.not(pathIn(paths));
    }
}
