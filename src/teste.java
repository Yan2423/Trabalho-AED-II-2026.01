import java.util.*;

public class teste {

    private static final double PERCENTUAL_REMOCAO = 0.20;

    interface EstruturaBenchmark {
        void inserir(int chave, String valor);
        String buscar(int chave);
        void remover(int chave);
        int tamanho();
        void limpar();
    }

    // ==================== GRÁFICOS ASCII ====================
    
    static class GraficoAscii {
        
        // Gráfico de barras comparativo
        public static void gerarGraficoBarras(
            String titulo,
            String[] rotulos,
            long[] valoresRedBlack,
            long[] valoresAvl
        ) {
            System.out.println("\n" + "═".repeat(80));
            System.out.println("📊 " + titulo);
            System.out.println("═".repeat(80));
            
            long maxValor = 0;
            for (int i = 0; i < rotulos.length; i++) {
                maxValor = Math.max(maxValor, valoresRedBlack[i]);
                maxValor = Math.max(maxValor, valoresAvl[i]);
            }
            
            double maxMs = maxValor / 1_000_000.0;
            if (maxMs < 0.001) maxMs = 1.0;
            int larguraMaxBarra = 45;
            
            System.out.printf("\n┌─────────────┬──────────────────────────────────────────────────┬──────────────────────────────────────────────────┐\n");
            System.out.printf("│ %-11s │ %-48s │ %-48s │\n", "Operação", "Red-Black Tree", "AVL Tree");
            System.out.printf("├─────────────┼──────────────────────────────────────────────────┼──────────────────────────────────────────────────┤\n");
            
            for (int i = 0; i < rotulos.length; i++) {
                double rbMs = valoresRedBlack[i] / 1_000_000.0;
                double avlMs = valoresAvl[i] / 1_000_000.0;
                
                int rbBarras = (int)((rbMs / maxMs) * larguraMaxBarra);
                int avlBarras = (int)((avlMs / maxMs) * larguraMaxBarra);
                
                String rbBarra = (rbBarras > 0) ? "█".repeat(Math.min(rbBarras, larguraMaxBarra)) : "";
                String avlBarra = (avlBarras > 0) ? "█".repeat(Math.min(avlBarras, larguraMaxBarra)) : "";
                
                System.out.printf("│ %-11s │ %-30s %8.2f ms │ %-30s %8.2f ms │\n", 
                    rotulos[i], rbBarra, rbMs, avlBarra, avlMs);
            }
            
            System.out.printf("└─────────────┴──────────────────────────────────────────────────┴──────────────────────────────────────────────────┘\n");
            System.out.printf("📌 Escala: %.2f ms = %d blocos\n", maxMs, larguraMaxBarra);
        }
        
        // Gráfico de evolução com volume
        public static void gerarGraficoEvolucao(
            String titulo,
            int[] volumes,
            long[][] temposRedBlack,
            long[][] temposAvl,
            int operacaoIndex,
            String nomeOperacao
        ) {
            System.out.println("\n" + "═".repeat(80));
            System.out.println("📈 " + titulo + " - " + nomeOperacao);
            System.out.println("═".repeat(80));
            
            double maxMs = 0;
            for (int i = 0; i < volumes.length; i++) {
                maxMs = Math.max(maxMs, temposRedBlack[operacaoIndex][i] / 1_000_000.0);
                maxMs = Math.max(maxMs, temposAvl[operacaoIndex][i] / 1_000_000.0);
            }
            
            if (maxMs < 0.001) maxMs = 1.0;
            int alturaGrafico = 18;
            
            System.out.println("\nTempo (ms) ↑");
            
            for (int linha = alturaGrafico; linha >= 0; linha--) {
                double limiteMs = (linha / (double)alturaGrafico) * maxMs;
                System.out.printf("%8.1f │ ", limiteMs);
                
                for (int i = 0; i < volumes.length; i++) {
                    double rbMs = temposRedBlack[operacaoIndex][i] / 1_000_000.0;
                    double avlMs = temposAvl[operacaoIndex][i] / 1_000_000.0;
                    
                    char rbChar = (rbMs >= limiteMs) ? '●' : ' ';
                    char avlChar = (avlMs >= limiteMs) ? '○' : ' ';
                    
                    System.out.printf("%c%c ", rbChar, avlChar);
                }
                System.out.println();
            }
            
            System.out.print("         ");
            for (int v : volumes) {
                System.out.printf("%6d ", v);
            }
            System.out.println("\n         " + "→ Volume de Dados");
            System.out.println("\n📌 Legenda: ● = Red-Black Tree | ○ = AVL Tree");
        }
    }

    // ==================== AVL TREE COMPLETE IMPLEMENTATION ====================
    
    static class Node {
        int key;
        String value;
        Node left, right;
        int height;
        
        Node(int key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }
    
    static class AvlTree {
        private Node root;
        
        private int height(Node n) { return n == null ? 0 : n.height; }
        
        private int getBalance(Node n) { return n == null ? 0 : height(n.left) - height(n.right); }
        
        private Node rotateRight(Node y) {
            Node x = y.left;
            Node T2 = x.right;
            x.right = y;
            y.left = T2;
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            return x;
        }
        
        private Node rotateLeft(Node x) {
            Node y = x.right;
            Node T2 = y.left;
            y.left = x;
            x.right = T2;
            x.height = Math.max(height(x.left), height(x.right)) + 1;
            y.height = Math.max(height(y.left), height(y.right)) + 1;
            return y;
        }
        
        public void inserir(int key, String value) {
            root = inserirRec(root, key, value);
        }
        
        private Node inserirRec(Node node, int key, String value) {
            if (node == null) return new Node(key, value);
            if (key < node.key) node.left = inserirRec(node.left, key, value);
            else if (key > node.key) node.right = inserirRec(node.right, key, value);
            else { node.value = value; return node; }
            
            node.height = 1 + Math.max(height(node.left), height(node.right));
            int balance = getBalance(node);
            
            if (balance > 1 && key < node.left.key) return rotateRight(node);
            if (balance < -1 && key > node.right.key) return rotateLeft(node);
            if (balance > 1 && key > node.left.key) {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
            if (balance < -1 && key < node.right.key) {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
            return node;
        }
        
        public String buscar(int key) {
            Node node = buscarRec(root, key);
            return node == null ? null : node.value;
        }
        
        private Node buscarRec(Node node, int key) {
            if (node == null || node.key == key) return node;
            return key < node.key ? buscarRec(node.left, key) : buscarRec(node.right, key);
        }
        
        public void remover(int key) {
            root = removerRec(root, key);
        }
        
        private Node removerRec(Node node, int key) {
            if (node == null) return null;
            if (key < node.key) node.left = removerRec(node.left, key);
            else if (key > node.key) node.right = removerRec(node.right, key);
            else {
                if (node.left == null || node.right == null) {
                    node = (node.left != null) ? node.left : node.right;
                } else {
                    Node temp = minValueNode(node.right);
                    node.key = temp.key;
                    node.value = temp.value;
                    node.right = removerRec(node.right, temp.key);
                }
            }
            if (node == null) return null;
            
            node.height = 1 + Math.max(height(node.left), height(node.right));
            int balance = getBalance(node);
            
            if (balance > 1 && getBalance(node.left) >= 0) return rotateRight(node);
            if (balance > 1 && getBalance(node.left) < 0) {
                node.left = rotateLeft(node.left);
                return rotateRight(node);
            }
            if (balance < -1 && getBalance(node.right) <= 0) return rotateLeft(node);
            if (balance < -1 && getBalance(node.right) > 0) {
                node.right = rotateRight(node.right);
                return rotateLeft(node);
            }
            return node;
        }
        
        private Node minValueNode(Node node) {
            Node current = node;
            while (current.left != null) current = current.left;
            return current;
        }
        
        public int tamanho() { return contarNos(root); }
        
        private int contarNos(Node node) {
            return node == null ? 0 : 1 + contarNos(node.left) + contarNos(node.right);
        }
        
        public void limpar() { root = null; }
        
        public void exibirEmOrdem() { exibirRec(root); }
        
        private void exibirRec(Node node) {
            if (node != null) {
                exibirRec(node.left);
                System.out.printf("      [%d] -> %s%n", node.key, node.value);
                exibirRec(node.right);
            }
        }
    }

    // ==================== RED-BLACK TREE IMPLEMENTATION ====================
    
    static class RedBlackNode {
        int key;
        String value;
        RedBlackNode left, right, parent;
        boolean isRed;
        
        RedBlackNode(int key, String value) {
            this.key = key;
            this.value = value;
            this.isRed = true;
        }
    }
    
    static class RedBlackTree {
        private RedBlackNode root;
        private int size;
        
        public RedBlackTree() { root = null; size = 0; }
        
        private void rotateLeft(RedBlackNode node) {
            RedBlackNode rightChild = node.right;
            node.right = rightChild.left;
            if (rightChild.left != null) rightChild.left.parent = node;
            rightChild.parent = node.parent;
            if (node.parent == null) root = rightChild;
            else if (node == node.parent.left) node.parent.left = rightChild;
            else node.parent.right = rightChild;
            rightChild.left = node;
            node.parent = rightChild;
        }
        
        private void rotateRight(RedBlackNode node) {
            RedBlackNode leftChild = node.left;
            node.left = leftChild.right;
            if (leftChild.right != null) leftChild.right.parent = node;
            leftChild.parent = node.parent;
            if (node.parent == null) root = leftChild;
            else if (node == node.parent.left) node.parent.left = leftChild;
            else node.parent.right = leftChild;
            leftChild.right = node;
            node.parent = leftChild;
        }
        
        private void fixInsertion(RedBlackNode node) {
            while (node != root && node.parent.isRed) {
                RedBlackNode parent = node.parent;
                RedBlackNode grandparent = parent.parent;
                if (parent == grandparent.left) {
                    RedBlackNode uncle = grandparent.right;
                    if (uncle != null && uncle.isRed) {
                        parent.isRed = false;
                        uncle.isRed = false;
                        grandparent.isRed = true;
                        node = grandparent;
                    } else {
                        if (node == parent.right) {
                            node = parent;
                            rotateLeft(node);
                            parent = node.parent;
                        }
                        parent.isRed = false;
                        grandparent.isRed = true;
                        rotateRight(grandparent);
                    }
                } else {
                    RedBlackNode uncle = grandparent.left;
                    if (uncle != null && uncle.isRed) {
                        parent.isRed = false;
                        uncle.isRed = false;
                        grandparent.isRed = true;
                        node = grandparent;
                    } else {
                        if (node == parent.left) {
                            node = parent;
                            rotateRight(node);
                            parent = node.parent;
                        }
                        parent.isRed = false;
                        grandparent.isRed = true;
                        rotateLeft(grandparent);
                    }
                }
            }
            root.isRed = false;
        }
        
        public void inserir(int key, String value) {
            RedBlackNode newNode = new RedBlackNode(key, value);
            RedBlackNode parent = null;
            RedBlackNode current = root;
            while (current != null) {
                parent = current;
                if (key < current.key) current = current.left;
                else if (key > current.key) current = current.right;
                else { current.value = value; return; }
            }
            newNode.parent = parent;
            if (parent == null) root = newNode;
            else if (key < parent.key) parent.left = newNode;
            else parent.right = newNode;
            fixInsertion(newNode);
            size++;
        }
        
        public String buscar(int key) {
            RedBlackNode current = root;
            while (current != null) {
                if (key == current.key) return current.value;
                else if (key < current.key) current = current.left;
                else current = current.right;
            }
            return null;
        }
        
        private RedBlackNode findMinimum(RedBlackNode node) {
            while (node.left != null) node = node.left;
            return node;
        }
        
        private void transplant(RedBlackNode u, RedBlackNode v) {
            if (u.parent == null) root = v;
            else if (u == u.parent.left) u.parent.left = v;
            else u.parent.right = v;
            if (v != null) v.parent = u.parent;
        }
        
        private void fixDeletion(RedBlackNode node, RedBlackNode parent) {
            while (node != root && (node == null || !node.isRed)) {
                if (node == parent.left) {
                    RedBlackNode sibling = parent.right;
                    if (sibling != null && sibling.isRed) {
                        sibling.isRed = false;
                        parent.isRed = true;
                        rotateLeft(parent);
                        sibling = parent.right;
                    }
                    if ((sibling.left == null || !sibling.left.isRed) && 
                        (sibling.right == null || !sibling.right.isRed)) {
                        if (sibling != null) sibling.isRed = true;
                        node = parent;
                        parent = node.parent;
                    } else {
                        if (sibling.right == null || !sibling.right.isRed) {
                            if (sibling.left != null) sibling.left.isRed = false;
                            if (sibling != null) sibling.isRed = true;
                            rotateRight(sibling);
                            sibling = parent.right;
                        }
                        if (sibling != null) sibling.isRed = parent.isRed;
                        parent.isRed = false;
                        if (sibling.right != null) sibling.right.isRed = false;
                        rotateLeft(parent);
                        node = root;
                        break;
                    }
                } else {
                    RedBlackNode sibling = parent.left;
                    if (sibling != null && sibling.isRed) {
                        sibling.isRed = false;
                        parent.isRed = true;
                        rotateRight(parent);
                        sibling = parent.left;
                    }
                    if ((sibling.right == null || !sibling.right.isRed) && 
                        (sibling.left == null || !sibling.left.isRed)) {
                        if (sibling != null) sibling.isRed = true;
                        node = parent;
                        parent = node.parent;
                    } else {
                        if (sibling.left == null || !sibling.left.isRed) {
                            if (sibling.right != null) sibling.right.isRed = false;
                            if (sibling != null) sibling.isRed = true;
                            rotateLeft(sibling);
                            sibling = parent.left;
                        }
                        if (sibling != null) sibling.isRed = parent.isRed;
                        parent.isRed = false;
                        if (sibling.left != null) sibling.left.isRed = false;
                        rotateRight(parent);
                        node = root;
                        break;
                    }
                }
            }
            if (node != null) node.isRed = false;
        }
        
        public void remover(int key) {
            RedBlackNode node = root;
            while (node != null && node.key != key) {
                if (key < node.key) node = node.left;
                else node = node.right;
            }
            if (node == null) return;
            
            RedBlackNode y = node;
            RedBlackNode x;
            boolean yOriginalIsRed = y.isRed;
            
            if (node.left == null) {
                x = node.right;
                transplant(node, node.right);
            } else if (node.right == null) {
                x = node.left;
                transplant(node, node.left);
            } else {
                y = findMinimum(node.right);
                yOriginalIsRed = y.isRed;
                x = y.right;
                if (y.parent == node) {
                    if (x != null) x.parent = y;
                } else {
                    transplant(y, y.right);
                    y.right = node.right;
                    y.right.parent = y;
                }
                transplant(node, y);
                y.left = node.left;
                y.left.parent = y;
                y.isRed = node.isRed;
            }
            if (!yOriginalIsRed) fixDeletion(x, x != null ? x.parent : null);
            size--;
        }
        
        public int tamanho() { return size; }
        public void limpar() { root = null; size = 0; }
    }
    
    // ==================== BENCHMARK WRAPPERS ====================
    
    static class RedBlackTreeBenchmark implements EstruturaBenchmark {
        private RedBlackTree rbTree = new RedBlackTree();
        public void inserir(int chave, String valor) { rbTree.inserir(chave, valor); }
        public String buscar(int chave) { return rbTree.buscar(chave); }
        public void remover(int chave) { rbTree.remover(chave); }
        public int tamanho() { return rbTree.tamanho(); }
        public void limpar() { rbTree.limpar(); }
    }
    
    static class AvlTreeBenchmark implements EstruturaBenchmark {
        private AvlTree avlTree = new AvlTree();
        public void inserir(int chave, String valor) { avlTree.inserir(chave, valor); }
        public String buscar(int chave) { return avlTree.buscar(chave); }
        public void remover(int chave) { avlTree.remover(chave); }
        public int tamanho() { return avlTree.tamanho(); }
        public void limpar() { avlTree.limpar(); }
    }
    
    // ==================== MEDIÇÕES DE TEMPO ====================
    
    private static long medirInsercao(EstruturaBenchmark estrutura, List<Integer> chaves, List<String> valores) {
        long inicio = System.nanoTime();
        for (int i = 0; i < chaves.size(); i++) estrutura.inserir(chaves.get(i), valores.get(i));
        return System.nanoTime() - inicio;
    }
    
    private static long medirBusca(EstruturaBenchmark estrutura, List<Integer> chaves) {
        long inicio = System.nanoTime();
        for (int chave : chaves) estrutura.buscar(chave);
        return System.nanoTime() - inicio;
    }
    
    private static long medirDelecao(EstruturaBenchmark estrutura, List<Integer> chaves, double percentualRemocao) {
        Random rand = new Random(42);
        int removerTotal = (int) (chaves.size() * percentualRemocao);
        Set<Integer> indicesRemover = new HashSet<>();
        while (indicesRemover.size() < removerTotal) indicesRemover.add(rand.nextInt(chaves.size()));
        long inicio = System.nanoTime();
        for (int index : indicesRemover) estrutura.remover(chaves.get(index));
        return System.nanoTime() - inicio;
    }
    
    // ==================== BENCHMARK COM MÚLTIPLOS VOLUMES ====================
    
    private static void executarBenchmarkMultiplosVolumes() {
        System.out.println("\n╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║     BENCHMARK COM MÚLTIPLOS VOLUMES - DADOS ORDENADOS              ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        
        int[] volumes = {100, 500, 1000, 5000, 10000, 50000, 100000};
        long[][] resultadosRedBlack = new long[3][volumes.length];
        long[][] resultadosAvl = new long[3][volumes.length];
        
        System.out.println("\n⏳ Executando benchmarks... (pode levar alguns segundos)\n");
        
        for (int idx = 0; idx < volumes.length; idx++) {
            int volume = volumes[idx];
            System.out.printf("📊 Testando com %6d elementos... ", volume);
            
            List<Integer> chaves = new ArrayList<>();
            List<String> valores = new ArrayList<>();
            for (int i = 1; i <= volume; i++) {
                chaves.add(i);
                valores.add("Valor_" + i);
            }
            
            RedBlackTreeBenchmark rbTree = new RedBlackTreeBenchmark();
            AvlTreeBenchmark avlTree = new AvlTreeBenchmark();
            
            resultadosRedBlack[0][idx] = medirInsercao(rbTree, chaves, valores);
            resultadosAvl[0][idx] = medirInsercao(avlTree, chaves, valores);
            
            resultadosRedBlack[1][idx] = medirBusca(rbTree, chaves);
            resultadosAvl[1][idx] = medirBusca(avlTree, chaves);
            
            resultadosRedBlack[2][idx] = medirDelecao(rbTree, chaves, PERCENTUAL_REMOCAO);
            resultadosAvl[2][idx] = medirDelecao(avlTree, chaves, PERCENTUAL_REMOCAO);
            
            System.out.printf("OK (Inserção: RB=%.0f/AVL=%.0f ms)%n", 
                resultadosRedBlack[0][idx]/1_000_000.0, resultadosAvl[0][idx]/1_000_000.0);
            
            rbTree.limpar();
            avlTree.limpar();
        }
        
        // ========== GRÁFICOS ==========
        
        String[] rotulos = {"Inserção", "Busca", "Deleção"};
        long[] rbUltimo = {
            resultadosRedBlack[0][volumes.length-1],
            resultadosRedBlack[1][volumes.length-1],
            resultadosRedBlack[2][volumes.length-1]
        };
        long[] avlUltimo = {
            resultadosAvl[0][volumes.length-1],
            resultadosAvl[1][volumes.length-1],
            resultadosAvl[2][volumes.length-1]
        };
        
        GraficoAscii.gerarGraficoBarras(
            "Comparação para " + volumes[volumes.length-1] + " elementos",
            rotulos, rbUltimo, avlUltimo
        );
        
        GraficoAscii.gerarGraficoEvolucao(
            "Evolução do Desempenho",
            volumes, resultadosRedBlack, resultadosAvl, 0, "Inserção"
        );
        
        GraficoAscii.gerarGraficoEvolucao(
            "Evolução do Desempenho",
            volumes, resultadosRedBlack, resultadosAvl, 1, "Busca"
        );
        
        GraficoAscii.gerarGraficoEvolucao(
            "Evolução do Desempenho",
            volumes, resultadosRedBlack, resultadosAvl, 2, "Deleção"
        );
        
        // Tabela resumo
        System.out.println("\n" + "═".repeat(100));
        System.out.println("📊 TABELA RESUMO (tempo em milissegundos)");
        System.out.println("═".repeat(100));
        System.out.printf("\n%-8s | %-25s | %-25s | %-25s\n", "Volume", "Inserção (RB / AVL)", "Busca (RB / AVL)", "Deleção (RB / AVL)");
        System.out.println("─".repeat(100));
        
        for (int idx = 0; idx < volumes.length; idx++) {
            System.out.printf("%-8d | %8.2f / %-8.2f | %8.2f / %-8.2f | %8.2f / %-8.2f\n",
                volumes[idx],
                resultadosRedBlack[0][idx] / 1_000_000.0,
                resultadosAvl[0][idx] / 1_000_000.0,
                resultadosRedBlack[1][idx] / 1_000_000.0,
                resultadosAvl[1][idx] / 1_000_000.0,
                resultadosRedBlack[2][idx] / 1_000_000.0,
                resultadosAvl[2][idx] / 1_000_000.0
            );
        }
    }
    
    // ==================== MAIN ====================
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("╔════════════════════════════════════════════════════════════════════╗");
        System.out.println("║        BENCHMARK COMPARATIVO - Red-Black Tree vs AVL Tree         ║");
        System.out.println("╚════════════════════════════════════════════════════════════════════╝");
        
        System.out.println("\nEscolha o modo de teste:");
        System.out.println("1 - Teste com dados manuais (você insere os valores)");
        System.out.println("2 - Teste automático com múltiplos volumes (100 a 100.000 elementos)");
        System.out.print("\n👉 Opção: ");
        
        int opcao = scanner.nextInt();
        scanner.nextLine();
        
        if (opcao == 1) {
            // Teste manual (seu código original adaptado)
            System.out.println("\n📝 INSERÇÃO MANUAL DE DADOS");
            System.out.println("Digite 'fim' para terminar\n");
            
            List<Integer> chaves = new ArrayList<>();
            List<String> valores = new ArrayList<>();
            int contadorId = 1;
            
            while (true) {
                System.out.print("👉 Valor: ");
                String valor = scanner.nextLine();
                if (valor.equalsIgnoreCase("fim")) break;
                if (valor.trim().isEmpty()) {
                    System.out.println("   ❌ Valor inválido!");
                    continue;
                }
                chaves.add(contadorId);
                valores.add(valor);
                System.out.println("   ✅ Adicionado: ID=" + contadorId + " -> '" + valor + "'");
                contadorId++;
            }
            
            if (chaves.isEmpty()) {
                System.out.println("\n❌ Nenhuma entrada. Encerrando...");
                scanner.close();
                return;
            }
            
            RedBlackTreeBenchmark rbTree = new RedBlackTreeBenchmark();
            AvlTreeBenchmark avlTree = new AvlTreeBenchmark();
            
            long rbIns = medirInsercao(rbTree, chaves, valores);
            long avlIns = medirInsercao(avlTree, chaves, valores);
            long rbBus = medirBusca(rbTree, chaves);
            long avlBus = medirBusca(avlTree, chaves);
            long rbDel = medirDelecao(rbTree, chaves, PERCENTUAL_REMOCAO);
            long avlDel = medirDelecao(avlTree, chaves, PERCENTUAL_REMOCAO);
            
            String[] rotulos = {"Inserção", "Busca", "Deleção"};
            long[] rbTempos = {rbIns, rbBus, rbDel};
            long[] avlTempos = {avlIns, avlBus, avlDel};
            
            GraficoAscii.gerarGraficoBarras(
                "Resultados para " + chaves.size() + " elementos",
                rotulos, rbTempos, avlTempos
            );
            
            rbTree.limpar();
            avlTree.limpar();
        } else {
            executarBenchmarkMultiplosVolumes();
        }
        
        scanner.close();
    }
}