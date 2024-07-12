package org.xmlevaluator.dto;

public class Tree {
    private Expression root;

    public Tree(Expression root) {
        this.root = root;
    }

    public Expression getRoot() {
        return root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tree that = (Tree) o;
        return root.equals(that.root);
    }
}