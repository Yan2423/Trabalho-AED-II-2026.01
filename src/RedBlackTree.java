public class RedBlackTree extends BaseBinaryTree implements BinarySearchTree  {
    
    static final boolean RED = false;
    static final boolean BLACK = true;
    
    @Override
    public Node searchNode(int id) {
      Node node = root;
     while (node != null) {
        if (id == node.data.id) {
       return node;
    }   else if (id < node.data.id) {
      node = node.left;
    }   else {
      node = node.right;
      }
  }
        return null;
    }

    @Override
    public void insertNode(PacketRule rule) {
       Node node = root;
      Node parent = null;

  // Traverse the tree to the left or right depending on the key
  while (node != null) {
    parent = node;
      if (rule.id < node.data.id) {
        node = node.left;
      } else if (rule.id > node.data.id) {
        node = node.right;
      } else {
        throw new IllegalArgumentException("BST already contains a node with key " + rule.id);
     }
    }

  // Insert new node
  Node newNode = new Node(rule);
  newNode.color = RED;
  if (parent == null) {
    root = newNode;
  } else if (rule.id < parent.data.id) {
    parent.left = newNode;
  } else {
    parent.right = newNode;
  }
  newNode.parent = parent;

    fixRedBlackPropertiesAfterInsert(newNode);
        
  }

  private void fixRedBlackPropertiesAfterInsert(Node node) {
  Node parent = node.parent;

  // Case 1
  if (parent == null) {
     node.color = BLACK;
    return;
  }

 
  if (parent.color == BLACK) {
    return;
  }


  Node grandparent = parent.parent;

  // Case 2:
  
  if (grandparent == null) {

    parent.color = BLACK;
    return;
  }


  Node uncle = getUncle(parent);

  // Case 3
  if (uncle != null && uncle.color == RED) {
    parent.color = BLACK;
    grandparent.color = RED;
    uncle.color = BLACK;


    fixRedBlackPropertiesAfterInsert(grandparent);
  }


  else if (parent == grandparent.left) {
    // Case 4a: Uncle is black and node is left->right "inner child" of its grandparent
    if (node == parent.right) {
      rotateLeft(parent);


      parent = node;
    }

    // Case 5a: Uncle is black and node is left->left "outer child" of its grandparent
    rotateRight(grandparent);

    // Recolor original parent and grandparent
    parent.color = BLACK;
    grandparent.color = RED;
  }


  else {
    // Case 4b: Uncle is black and node is right->left "inner child" of its grandparent
    if (node == parent.left) {
      rotateRight(parent);


      parent = node;
    }

    // Case 5b: Uncle is black and node is right->right "outer child" of its grandparent
    rotateLeft(grandparent);


    parent.color = BLACK;
    grandparent.color = RED;
  }
}

private Node getUncle(Node parent) {
  Node grandparent = parent.parent;
  if (grandparent.left == parent) {
    return grandparent.right;
  } else if (grandparent.right == parent) {
    return grandparent.left;
  } else {
    throw new IllegalStateException("Parent is not a child of its grandparent");
  }
}

@Override
public void deleteNode(int id) {
       Node node = root;


  while (node != null && node.data.id != id) {

    if (id < node.data.id) {
      node = node.left;
    } else {
      node = node.right;
    }
  }


  if (node == null) {
    return;
  }



  Node movedUpNode;
  boolean deletedNodeColor;


  if (node.left == null || node.right == null) {
    movedUpNode = deleteNodeWithZeroOrOneChild(node);
    deletedNodeColor = node.color;
  }


  else {

    Node inOrderSuccessor = findMinimum(node.right);


    node.data = inOrderSuccessor.data;


    movedUpNode = deleteNodeWithZeroOrOneChild(inOrderSuccessor);
    deletedNodeColor = inOrderSuccessor.color;
  }

  if (deletedNodeColor == BLACK) {
    fixRedBlackPropertiesAfterDelete(movedUpNode);


    if (movedUpNode.getClass() == NilNode.class) {
      replaceParentsChild(movedUpNode.parent, movedUpNode, null);
    }
  }
        
}

private Node deleteNodeWithZeroOrOneChild(Node node) {

  if (node.left != null) {
    replaceParentsChild(node.parent, node, node.left);
    return node.left; 
  }


  else if (node.right != null) {
    replaceParentsChild(node.parent, node, node.right);
    return node.right; 
  }

  // Node has no children -->
  // * node is red --> just remove it
  // * node is black --> replace it by a temporary NIL node (needed to fix the R-B rules)
  else {
    Node newChild = node.color == BLACK ? new NilNode() : null;
    replaceParentsChild(node.parent, node, newChild);
    return newChild;
  }
}

private Node findMinimum(Node node) {
  while (node.left != null) {
    node = node.left;
  }
  return node;
}

private void fixRedBlackPropertiesAfterDelete(Node node) {
  // Case 1: Examined node is root, end of recursion
  if (node == root) {

     node.color = BLACK;
    return;
  }

  Node sibling = getSibling(node);

  // Case 2
  if (sibling.color == RED) {
    handleRedSibling(node, sibling);
    sibling = getSibling(node); 
  }

  // Cases 3+4: Black sibling with two black children
  if (isBlack(sibling.left) && isBlack(sibling.right)) {
    sibling.color = RED;

    // Case 3: Black sibling with two black children + red parent
    if (node.parent.color == RED) {
      node.parent.color = BLACK;
    }

    // Case 4: Black sibling with two black children + black parent
    else {
      fixRedBlackPropertiesAfterDelete(node.parent);
    }
  }

  // Case 5+6: Black sibling with at least one red child
  else {
    handleBlackSiblingWithAtLeastOneRedChild(node, sibling);
  }
}

private void handleRedSibling(Node node, Node sibling) {

  sibling.color = BLACK;
  node.parent.color = RED;


  if (node == node.parent.left) {
    rotateLeft(node.parent);
  } else {
    rotateRight(node.parent);
  }
}

private void handleBlackSiblingWithAtLeastOneRedChild(Node node, Node sibling) {
  boolean nodeIsLeftChild = node == node.parent.left;

  // Case 5: Black sibling with at least one red child + "outer nephew" is black
  // --> Recolor sibling and its child, and rotate around sibling
  if (nodeIsLeftChild && isBlack(sibling.right)) {
    sibling.left.color = BLACK;
    sibling.color = RED;
    rotateRight(sibling);
    sibling = node.parent.right;
  } else if (!nodeIsLeftChild && isBlack(sibling.left)) {
    sibling.right.color = BLACK;
    sibling.color = RED;
    rotateLeft(sibling);
    sibling = node.parent.left;
  }



  // Case 6: Black sibling with at least one red child + "outer nephew" is red
  // --> Recolor sibling + parent + sibling's child, and rotate around parent
  sibling.color = node.parent.color;
  node.parent.color = BLACK;
  if (nodeIsLeftChild) {
    sibling.right.color = BLACK;
    rotateLeft(node.parent);
  } else {
    sibling.left.color = BLACK;
    rotateRight(node.parent);
  }
}

private Node getSibling(Node node) {
  Node parent = node.parent;
  if (node == parent.left) {
    return parent.right;
  } else if (node == parent.right) {
    return parent.left;
  } else {
    throw new IllegalStateException("Parent is not a child of its grandparent");
  }
}

private boolean isBlack(Node node) {
  return node == null || node.color == BLACK;
}


private static class NilNode extends Node {
    private NilNode() {
      super(null);
      this.color = BLACK;
    }
  }

private void rotateRight(Node node) {
  Node parent = node.parent;
  Node leftChild = node.left;

  node.left = leftChild.right;
  if (leftChild.right != null) {
    leftChild.right.parent = node;
  }

  leftChild.right = node;
  node.parent = leftChild;

  replaceParentsChild(parent, node, leftChild);
}

private void rotateLeft(Node node) {
  Node parent = node.parent;
  Node rightChild = node.right;

  node.right = rightChild.left;
  if (rightChild.left != null) {
    rightChild.left.parent = node;
  }

  rightChild.left = node;
  node.parent = rightChild;

  replaceParentsChild(parent, node, rightChild);
}


private void replaceParentsChild(Node parent, Node oldChild, Node newChild) {
  if (parent == null) {
    root = newChild;
  } else if (parent.left == oldChild) {
    parent.left = newChild;
  } else if (parent.right == oldChild) {
    parent.right = newChild;
  } else {
    throw new IllegalStateException("Node is not a child of its parent");
  }

  if (newChild != null) {
    newChild.parent = parent;
  }
}
}
