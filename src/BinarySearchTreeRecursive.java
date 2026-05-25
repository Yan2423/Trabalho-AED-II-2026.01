public class BinarySearchTreeRecursive extends BaseBinaryTree implements BinarySearchTree {
    
    public Node searchNode(int id) {
        
        return searchNode(id, root);
    
    }

private Node searchNode(int id, Node node) {
  if (node == null) {
    return null;
  }

  if (id == node.data.id) {
    return node;
  } else if (id < node.data.id) {
    return searchNode(id, node.left);
  } else {
    return searchNode(id, node.right);
  }
}
@Override
    public void insertNode(PacketRule rule) {
        root = insertNode(rule, root);
    }

Node insertNode(PacketRule rule, Node node) {
  // No node at current position --> store new node at current position
  if (node == null) {
    node = new Node(rule);
  }

  // Otherwise, traverse the tree to the left or right depending on the key
  else if (rule.id < node.data.id) {
    node.left = insertNode(rule, node.left);
  } else if (rule.id > node.data.id) {
    node.right = insertNode(rule, node.right);
  } else {
    throw new IllegalArgumentException("BST already contains a node with key " + rule.id);
  }

  return node;
}

public void deleteNode(int id) {
  root = deleteNode(id, root);
}

Node deleteNode(int id, Node node) {
  // No node at current position --> go up the recursion
  if (node == null) {
    return null;
  }

  // Traverse the tree to the left or right depending on the key
  if (id < node.data.id) {
    node.left = deleteNode(id, node.left);
  } else if (id > node.data.id) {
    node.right = deleteNode(id, node.right);
  }

  // At this point, "node" is the node to be deleted

  // Node has no children --> just delete it
  else if (node.left == null && node.right == null) {
    node = null;
  }

  // Node has only one child --> replace node by its single child
  else if (node.left == null) {
    node = node.right;
  } else if (node.right == null) {
    node = node.left;
  }

  // Node has two children
  else {
    deleteNodeWithTwoChildren(node);
  }

  return node;
}

private void deleteNodeWithTwoChildren(Node node) {
  // Find minimum node of right subtree ("inorder successor" of current node)
  Node inOrderSuccessor = findMinimum(node.right);

  // Copy inorder successor's data to current node
  node.data = inOrderSuccessor.data;

  // Delete inorder successor recursively
  node.right = deleteNode(inOrderSuccessor.data.id, node.right);
}

private Node findMinimum(Node node) {
  while (node.left != null) {
    node = node.left;
  }
  return node;
}

public void printTree() {
    printTree(root, 0);
}

private void printTree(Node node, int level) {
    if (node == null) {
        return;
    }

    // Primeiro imprime o lado direito (fica "em cima")
    printTree(node.right, level + 1);

    // Espaçamento
    for (int i = 0; i < level; i++) {
        System.out.print("    "); // 4 espaços
    }

    // Imprime o valor do nó
    System.out.println(node.data.id);

    // Depois imprime o lado esquerdo (fica "embaixo")
    printTree(node.left, level + 1);
}
}
