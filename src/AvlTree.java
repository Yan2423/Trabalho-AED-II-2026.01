import static java.lang.Math.max;

public class AvlTree extends BinarySearchTreeRecursive {
    
  Node root;
    private int height(Node node) {
    return node != null ? node.height : -1;
  }

  private void updateHeight(Node node) {
    int leftChildHeight = height(node.left);
    int rightChildHeight = height(node.right);
    node.height = max(leftChildHeight, rightChildHeight) + 1;
  }

  private int balanceFactor(Node node) {
    return height(node.right) - height(node.left);
  }

  private Node rotateRight(Node node) {
  Node leftChild = node.left;

  node.left = leftChild.right;
  leftChild.right = node;

  updateHeight(node);
  updateHeight(leftChild);

  return leftChild;
}


private Node rotateLeft(Node node) {
  Node rightChild = node.right;

  node.right = rightChild.left;
  rightChild.left = node;

  updateHeight(node);
  updateHeight(rightChild);

  return rightChild;
}

private Node rebalance(Node node) {
  int balanceFactor = balanceFactor(node);

  // Left-heavy?
  if (balanceFactor < -1) {
    if (balanceFactor(node.left) <= 0) {    // Case 1
      // Rotate right
      node = rotateRight(node);
    } else {                                // Case 2
      // Rotate left-right
      node.left = rotateLeft(node.left);
      node = rotateRight(node);
    }
  }

  // Right-heavy?
  if (balanceFactor > 1) {
    if (balanceFactor(node.right) >= 0) {    // Case 3
      // Rotate left
      node = rotateLeft(node);
    } else {                                 // Case 4
      // Rotate right-left
      node.right = rotateRight(node.right);
      node = rotateLeft(node);
    }
  }

  return node;
} 

@Override
Node insertNode(PacketRule rule, Node node) {
  node = super.insertNode(rule, node);

  updateHeight(node);

  return rebalance(node);
}

@Override
Node deleteNode(int id, Node node) {
  node = super.deleteNode(id, node);

  // Node is null if the tree doesn't contain the key
  if (node == null) {
    return null;
  }

  updateHeight(node);

  return rebalance(node);
}
}
