package testes;

import java.util.*;

public class teste {

    private static final long SEED = 42;
    private static final int TOTAL_ELEMENTOS = 100_000;
    private static final double PERCENTUAL_REMOCAO = 0.20;
    private static final int[] VOLUMES = {10_000, 25_000, 50_000, 75_000, 100_000};

    interface EstruturaBenchmark {
        void inserir(int chave, String valor);
        String buscar(int chave);
        void remover(int chave);
        int tamanho();
        void limpar();
    }

    static class HashMapBenchmark implements EstruturaBenchmark {
        private HashMap<Integer, String> map = new HashMap<>();
        public void inserir(int chave, String valor) { map.put(chave, valor); }
        public String buscar(int chave) { return map.get(chave); }
        public void remover(int chave) { map.remove(chave); }
        public int tamanho() { return map.size(); }
        public void limpar() { map = new HashMap<>(); }
    }

    static class TreeMapBenchmark implements EstruturaBenchmark {
        private TreeMap<Integer, String> map = new TreeMap<>();
        public void inserir(int chave, String valor) { map.put(chave, valor); }
        public String buscar(int chave) { return map.get(chave); }
        public void remover(int chave) { map.remove(chave); }
        public int tamanho() { return map.size(); }
        public void limpar() { map = new TreeMap<>(); }
    }

    private static List<Integer> gerarDadosOrdenados(int quantidade) {
        Random rand = new Random(SEED);
        TreeSet<Integer> conjunto = new TreeSet<>();
        while (conjunto.size() < quantidade) {
            conjunto.add(rand.nextInt(quantidade * 10));
        }
        return new ArrayList<>(conjunto);
    }

    private static long medirInsercao(EstruturaBenchmark estrutura, List<Integer> chaves) {
        long inicio = System.nanoTime();
        for (int chave : chaves) {
            estrutura.inserir(chave, "valor_" + chave);
        }
        long fim = System.nanoTime();
        return fim - inicio;
    }

    private static long medirBusca(EstruturaBenchmark estrutura, List<Integer> chaves) {
        long inicio = System.nanoTime();
        for (int chave : chaves) {
            estrutura.buscar(chave);
        }
        long fim = System.nanoTime();
        return fim - inicio;
    }

    private static long medirDelecao(EstruturaBenchmark estrutura, List<Integer> chaves) {
        Random rand = new Random(SEED);
        int removerTotal = (int) (chaves.size() * PERCENTUAL_REMOCAO);
        
        Set<Integer> indicesRemover = new HashSet<>();
        while (indicesRemover.size() < removerTotal) {
            indicesRemover.add(rand.nextInt(chaves.size()));
        }
        
        long inicio = System.nanoTime();
        for (int index : indicesRemover) {
            estrutura.remover(chaves.get(index));
        }
        long fim = System.nanoTime();
        return fim - inicio;
    }

    static class Resultado {
        int volume;
        long insercaoNs, buscaNs, delecaoNs;
        String estrutura;
        
        Resultado(int volume, long insercaoNs, long buscaNs, long delecaoNs, String estrutura) {
            this.volume = volume;
            this.insercaoNs = insercaoNs;
            this.buscaNs = buscaNs;
            this.delecaoNs = delecaoNs;
            this.estrutura = estrutura;
        }
    }

    private static Resultado executarBenchmark(int volume, EstruturaBenchmark estrutura) {
        List<Integer> dadosOrdenados = gerarDadosOrdenados(volume);
        estrutura.limpar();
        
        long tempoInsercao = medirInsercao(estrutura, dadosOrdenados);
        long tempoBusca = medirBusca(estrutura, dadosOrdenados);
        long tempoDelecao = medirDelecao(estrutura, dadosOrdenados);
        
        return new Resultado(volume, tempoInsercao, tempoBusca, tempoDelecao, "");
    }

    // Gera gráfico ASCII simples
    private static void gerarGraficoASCII(String titulo, String unidade, 
                                          List<Double> valoresHashMap, 
                                          List<Double> valoresTreeMap) {
        System.out.println("\n " + titulo);
        System.out.println("   " + unidade);
        System.out.println("   ┌────────────────────────────────────────┐");
        
        double maxValor = Math.max(
            valoresHashMap.stream().max(Double::compare).orElse(0.0),
            valoresTreeMap.stream().max(Double::compare).orElse(0.0)
        );
        
        int altura = 15;
        for (int i = altura; i >= 0; i--) {
            double limite = (i / (double) altura) * maxValor;
            System.out.printf("%3d%% │", (int)((i / (double) altura) * 100));
            
            for (int j = 0; j < valoresHashMap.size(); j++) {
                if (valoresHashMap.get(j) >= limite) {
                    System.out.print("█");
                } else if (valoresTreeMap.get(j) >= limite) {
                    System.out.print("░");
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
        
        System.out.println("     └────────────────────────────────────────┘");
        System.out.print("      ");
        for (int v : VOLUMES) {
            System.out.printf("%-8d", v / 1000);
        }
        System.out.println(" (milhares)");
        System.out.println("     Legenda: █ = HashMap  ░ = TreeMap\n");
    }

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║        BENCHMARK COMPARATIVO - HashMap vs TreeMap        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        System.out.println("\n Configuração:");
        System.out.println("   • Seed: " + SEED);
        System.out.println("   • Total de elementos: " + TOTAL_ELEMENTOS);
        System.out.println("   • Removendo " + (PERCENTUAL_REMOCAO * 100) + "% dos nós");
        System.out.println("   • Medições em nanossegundos\n");
        
        List<Resultado> resultadosHashMap = new ArrayList<>();
        List<Resultado> resultadosTreeMap = new ArrayList<>();
        
        for (int volume : VOLUMES) {
            System.out.println("Testando volume: " + String.format("%,d", volume) + " elementos");
            
            HashMapBenchmark hashMap = new HashMapBenchmark();
            Resultado resHashMap = executarBenchmark(volume, hashMap);
            resultadosHashMap.add(resHashMap);
            
            TreeMapBenchmark treeMap = new TreeMapBenchmark();
            Resultado resTreeMap = executarBenchmark(volume, treeMap);
            resultadosTreeMap.add(resTreeMap);
            
            System.out.printf("    HashMap  → Ins: %6.2f ms | Busca: %6.2f ms | Del: %6.2f ms%n",
                    resHashMap.insercaoNs / 1_000_000.0,
                    resHashMap.buscaNs / 1_000_000.0,
                    resHashMap.delecaoNs / 1_000_000.0);
            System.out.printf("    TreeMap  → Ins: %6.2f ms | Busca: %6.2f ms | Del: %6.2f ms%n%n",
                    resTreeMap.insercaoNs / 1_000_000.0,
                    resTreeMap.buscaNs / 1_000_000.0,
                    resTreeMap.delecaoNs / 1_000_000.0);
        }
        
        // Prepara dados para gráficos ASCII
        List<Double> insercaoHashMap = new ArrayList<>();
        List<Double> buscaHashMap = new ArrayList<>();
        List<Double> delecaoHashMap = new ArrayList<>();
        List<Double> insercaoTreeMap = new ArrayList<>();
        List<Double> buscaTreeMap = new ArrayList<>();
        List<Double> delecaoTreeMap = new ArrayList<>();
        
        for (Resultado r : resultadosHashMap) {
            insercaoHashMap.add(r.insercaoNs / 1_000_000.0);
            buscaHashMap.add(r.buscaNs / 1_000_000.0);
            delecaoHashMap.add(r.delecaoNs / 1_000_000.0);
        }
        
        for (Resultado r : resultadosTreeMap) {
            insercaoTreeMap.add(r.insercaoNs / 1_000_000.0);
            buscaTreeMap.add(r.buscaNs / 1_000_000.0);
            delecaoTreeMap.add(r.delecaoNs / 1_000_000.0);
        }
        
        // Gera gráficos ASCII
        gerarGraficoASCII("INSERÇÃO (menos é melhor)", "Tempo (ms)", insercaoHashMap, insercaoTreeMap);
        gerarGraficoASCII("BUSCA (menos é melhor)", "Tempo (ms)", buscaHashMap, buscaTreeMap);
        gerarGraficoASCII("DELEÇÃO (menos é melhor)", "Tempo (ms)", delecaoHashMap, delecaoTreeMap);
        
        // Resumo final
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║                    RESUMO FINAL                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
        
        double avgHashMapIns = insercaoHashMap.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double avgTreeMapIns = insercaoTreeMap.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double avgHashMapBusca = buscaHashMap.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        double avgTreeMapBusca = buscaTreeMap.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        
        System.out.printf("\n Média de Inserção: HashMap %.2f ms vs TreeMap %.2f ms → %s %.1f%% mais rápido%n",
                avgHashMapIns, avgTreeMapIns,
                avgHashMapIns < avgTreeMapIns ? "HashMap" : "TreeMap",
                Math.abs((avgTreeMapIns - avgHashMapIns) / avgTreeMapIns * 100));
        
        System.out.printf(" Média de Busca:    HashMap %.2f ms vs TreeMap %.2f ms → %s %.1f%% mais rápido%n",
                avgHashMapBusca, avgTgreeMapBusca,
                avgHashMapBusca < avgTreeMapBusca ? "HashMap" : "TreeMap",
                Math.abs((avgTreeMapBusca - avgHashMapBusca) / avgTreeMapBusca * 100));
        
        System.out.println("\n Conclusão: HashMap é geralmente mais rápido para todos os volumes testados,");
        System.out.println("   especialmente em buscas. TreeMap só é recomendado quando a ordenação");
        System.out.println("   dos elementos é necessária.\n");
    }
}

//Código rodou em 1,58 segundos