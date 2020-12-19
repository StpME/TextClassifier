// Name: Mitchell Stapelman
// Section: DG
// Represents a TextClassifier which classifies text documents to
// determine whether comments should be flagged for spam.

public class TextClassifier {
    private Node overallRoot;
    private Vectorizer vectorizer;
    
    // Constructs a TextClassifier object with a given Vectorizer to transform
    // data points and a Splitter that determines what to split in the tree.
    public TextClassifier(Vectorizer vectorizer, Splitter splitter) {
        this.overallRoot = constructorHelper(splitter);
        this.vectorizer = vectorizer;
    }

    // Private helper for constructor to initialize overallRoot field.
    private Node constructorHelper(Splitter splitter) {
        Splitter.Result splitResult = splitter.split();
        if (splitResult == null) {
            return new Node(splitter.label());
        } else {
            return new Node(splitResult.split, splitter.label(), constructorHelper(splitResult.left),
                                                                 constructorHelper(splitResult.right));
        }
    }

    // Returns a boolean that represents the predicted label
    // using the given text by traversing through the tree.
    public boolean classify(String text) {
        double[] result = this.vectorizer.transform(text)[0];
        return classify(result, overallRoot);
    }

    // Private helper method for classify, returns a boolean representing the label
    // from the path's leaf node.
    private boolean classify(double[] result, Node root) {
        if(root.isLeaf()) {
            return root.label;
        } else {
            if (root.split.goLeft(result)) {
                return classify(result, root.left);
            } else {
                return classify(result, root.right);
            }
        }
    }

    // Prints out a Java code representation of the decision tree for the node
    // in an if/else format (without {}), adding one indentation space
    // for each level in the tree.  
    public void print() {
        print(overallRoot, "");
    }

    // Private helper method for print that traverses recursively through tree,
    // printing out the representation of the code decision tree.
    private void print(Node root, String indents) {
       
        if (root.isLeaf()) {
            System.out.println(indents + "return " + root.label + ";");
        } else {
            // Left
            System.out.println(indents + "if (" + root.split.toString() + ")");
            print(root.left, indents + " ");
            // Right
            System.out.println(indents + "else");
            print(root.right, indents + " ");
        }
    }

    // Prunes/trims the tree to the given depth, removing the nodes beyond the depth.
    public void prune(int depth) {
        overallRoot = prune(depth, overallRoot, 0);
    }

    // Private helper method for prune, while root isn't null, 
    // if depth is equal to current depth return a new Node with the roots label.
    // Otherwise, recurse through the tree, incrementing current depth until it reachs target.
    private Node prune(int depth, Node root, int currDepth) {
        if (root != null) {
            if (depth == currDepth) {
                return new Node(root.label);
            } else {
                root.left = prune(depth, root.left, currDepth + 1);
                root.right = prune(depth, root.right, currDepth + 1);
            }
        }
        return root;
    }


    // An internal node or a leaf node in the decision tree.
    private static class Node {
        public Split split;
        public boolean label;
        public Node left;
        public Node right;

        // Constructs a new leaf node with the given label.
        public Node(boolean label) {
            this(null, label, null, null);
        }

        // Constructs a new internal node with the given split, label, and left and right nodes.
        public Node(Split split, boolean label, Node left, Node right) {
            this.split = split;
            this.label = label;
            this.left = left;
            this.right = right;
        }

        // Returns true if and only if this node is a leaf node.
        public boolean isLeaf() {
            return left == null && right == null;
        }
    }
}
