package project;

public class CorridorMonitor {
    public static boolean[] selectCheckpoints(int nodeCount, int[][] edges, int edgeCount) {
        boolean[] selected = new boolean[nodeCount];
        boolean[] edgeUsed = new boolean[edgeCount];

        for (int i = 0; i < edgeCount; i++) {
            if (!edgeUsed[i]) {
                int u = edges[i][0];
                int v = edges[i][1];
                if (!selected[u] && !selected[v]) {
                    selected[u] = true;
                    selected[v] = true;
                    for (int j = 0; j < edgeCount; j++) {
                        if (edges[j][0] == u || edges[j][0] == v || edges[j][1] == u || edges[j][1] == v) {
                            edgeUsed[j] = true;
                        }
                    }
                }
            }
        }
        return selected;
    }
}
