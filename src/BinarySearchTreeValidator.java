public final class BinarySearchTreeValidator{

    public static boolean isBstWithoutDuplicates(BinaryTree tree) {
  return isBstWithoutDuplicates(tree.getRoot(), Integer.MIN_VALUE, Integer.MAX_VALUE);
}

private static boolean isBstWithoutDuplicates(
    Node node, int minAllowedKey, int maxAllowedKey) {
  if (node == null) {
    return true;
  }

  if (node.data.id < minAllowedKey || node.data.id > maxAllowedKey) {
    return false;
  }

  return isBstWithoutDuplicates(node.left, minAllowedKey, node.data.id - 1)
      && isBstWithoutDuplicates(node.right, node.data.id + 1, maxAllowedKey);
}

}