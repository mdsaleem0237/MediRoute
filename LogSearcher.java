package project;

public class LogSearcher {
    public static CustomIntList searchKMP(String text, String pattern) {
        CustomIntList matches = new CustomIntList();
        int n = text.length(), m = pattern.length();
        if (m == 0 || n < m) return matches;

        int[] pi = new int[m];
        for (int i = 1, j = 0; i < m; i++) {
            while (j > 0 && pattern.charAt(i) != pattern.charAt(j)) j = pi[j - 1];
            if (pattern.charAt(i) == pattern.charAt(j)) j++;
            pi[i] = j;
        }

        for (int i = 0, j = 0; i < n; i++) {
            while (j > 0 && text.charAt(i) != pattern.charAt(j)) j = pi[j - 1];
            if (text.charAt(i) == pattern.charAt(j)) j++;
            if (j == m) {
                matches.add(i - m + 1);
                j = pi[j - 1];
            }
        }
        return matches;
    }

    public static CustomIntList searchRabinKarp(String text, String pattern) {
        CustomIntList matches = new CustomIntList();
        int n = text.length(), m = pattern.length();
        if (m == 0 || n < m) return matches;

        long base = 256, mod1 = 1_000_000_007L, mod2 = 1_000_000_009L;
        long p1 = 0, p2 = 0, t1 = 0, t2 = 0, pow1 = 1, pow2 = 1;

        for (int i = 0; i < m - 1; i++) {
            pow1 = (pow1 * base) % mod1;
            pow2 = (pow2 * base) % mod2;
        }
        for (int i = 0; i < m; i++) {
            p1 = (p1 * base + pattern.charAt(i)) % mod1;
            p2 = (p2 * base + pattern.charAt(i)) % mod2;
            t1 = (t1 * base + text.charAt(i)) % mod1;
            t2 = (t2 * base + text.charAt(i)) % mod2;
        }

        for (int i = 0; i <= n - m; i++) {
            if (p1 == t1 && p2 == t2) {
                boolean exact = true;
                for (int j = 0; j < m; j++) {
                    if (text.charAt(i + j) != pattern.charAt(j)) {
                        exact = false;
                        break;
                    }
                }
                if (exact) matches.add(i);
            }
            if (i < n - m) {
                t1 = (base * (t1 - text.charAt(i) * pow1) + text.charAt(i + m)) % mod1;
                if (t1 < 0) t1 += mod1;
                t2 = (base * (t2 - text.charAt(i) * pow2) + text.charAt(i + m)) % mod2;
                if (t2 < 0) t2 += mod2;
            }
        }
        return matches;
    }
}
