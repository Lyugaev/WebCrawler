package net.lyugaev.webcrawler;

/**
 * Created by dmitry on 20.09.16.
 */
class Node {
    private String url;
    private int linkDepth;
    Node parent, child, sibling;

    public Node(String url, int linkDepth) {
        this.url = url;
        this.linkDepth = linkDepth;
        this.parent = null;
        this.child = null;
        this.sibling = null;
    }

    public Node(Link link) {
        this.url = link.url;
        this.linkDepth = link.linkDepth;
        this.parent = parent;
        this.child = child;
        this.sibling = sibling;
    }

    public String getUrl() {
        return url;
    }

    public int getLinkDepth() {
        return linkDepth;
    }
}

class LinkTree {
    Node root;

    public LinkTree(Node root) {
        this.root = root;
    }

    Node find(Node curNode, String link) {
        if (curNode == null)
            return null;

        if (curNode.getUrl().equals(link))
            return curNode;

        Node result = find(curNode.child, link);
        if (result != null)
            return result;

        result = find(curNode.sibling, link);
        if (result != null)
            return result;

        return null;
    }

    void add(String parentLink, String link, int linkDepth) {
        Node parentNode = find(root, parentLink);
        if (parentNode != null) {
            Node node = new Node(link, linkDepth);
            node.parent = parentNode;
            node.sibling = parentNode.child;
            parentNode.child = node;
        }
    }

    void printNodeConsole(Node node) {
        for (int i = 0; i < node.getLinkDepth(); i++) {
            System.out.print("     ");
        }
        System.out.println(node.getUrl());
    }

    void printNode(Node node) {
        if (node == null)
            return;

        printNodeConsole(node);
        printNode(node.child);
        printNode(node.sibling);
    }

    void print() {
        printNode(root);
    }
}
