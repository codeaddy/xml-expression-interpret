package org.xmlevaluator.dto;

public class XPathElement extends Expression {
    private final String path;

    public XPathElement(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        XPathElement that = (XPathElement) o;
        return path.equals(that.path);
    }
}
