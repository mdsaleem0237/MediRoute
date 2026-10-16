package project;

public class RouteOptimizer {
    private final int totalNodes = 16;
    private int[][] capacity = new int[totalNodes][totalNodes];
    private int[][] residual = new int[totalNodes][totalNodes];

    public void setCapacity(int u, int v, int cap) {
        capacity[u][v] = cap;
        residual[u][v] = cap;
    }

    public int calculateMaxFlow(int src, int dst, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) residual[i][j] = capacity[i][j];
        }
        int maxFlow = 0;
        int[] parent = new int[n];
        SimpleQueue q = new SimpleQueue();

        while (true) {
            for (int i = 0; i < n; i++) parent[i] = -1;
            q.reset();
            q.push(src);
            parent[src] = src;

            while (!q.isEmpty()) {
                int u = q.pop();
                if (u == dst) break;
                for (int v = 0; v < n; v++) {
                    if (parent[v] == -1 && residual[u][v] > 0) {
                        parent[v] = u;
                        q.push(v);
                    }
                }
            }
            if (parent[dst] == -1) break;

            int bottleneck = Integer.MAX_VALUE;
            for (int v = dst; v != src; v = parent[v]) {
                int u = parent[v];
                bottleneck = Math.min(bottleneck, residual[u][v]);
            }
            for (int v = dst; v != src; v = parent[v]) {
                int u = parent[v];
                residual[u][v] -= bottleneck;
                residual[v][u] += bottleneck;
            }
            maxFlow += bottleneck;
        }
        return maxFlow;
    }

    public void printFlowBreakdown(int n, String[] labels) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int sent = capacity[i][j] - residual[i][j];
                if (capacity[i][j] > 0 && sent > 0) {
                    System.out.printf("   -> Pipeline: %-26s -> %-26s : %d / %d units/hr\n",
                            labels[i], labels[j], sent, capacity[i][j]);
                }
            }
        }
    }
}
