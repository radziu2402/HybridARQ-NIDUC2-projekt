package BCHcode;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class BCH {
    private final int n;
    private final int t;
    private final int m;
    private final int m_length;
    private final Polynom[] alpha;
    private static final Polynom[] prime = {
            new Polynom("1", 2),
            new Polynom("11", 2),
            new Polynom("111", 2),
            new Polynom("1011", 2),
            new Polynom("10011", 2),
            new Polynom("100101", 2),
            new Polynom("1000011", 2),
            new Polynom("10001001", 2),
            new Polynom("100011101", 2),
            new Polynom("1000010001", 2),
            new Polynom("10000001001", 2),
            new Polynom("100000000101", 2),
            new Polynom("1000001010011", 2),
            new Polynom("10000000011011", 2),
            new Polynom("100010001000011", 2),
            new Polynom("1000000000000011", 2),
    };
    private Polynom g;

    public BCH(int n, int d) {
        m = (int) (Math.log(n + 1) / Math.log(2.0));
        this.n = n;
        t = (d - 1) / 2;
        String tmp = "1";
        alpha = new Polynom[n];
        System.out.println("Do budowy użyto pola\n" +
                "następujący pierwotny wielomian: " + prime[m]);
        for (int i = 0; i < n; i++) {
            alpha[i] = new Polynom(tmp, 2).mod(prime[m]);
            tmp += "0";
            System.out.println("α^" + i + " = " + alpha[i]);
        }
        ArrayList<ArrayList<Integer>> ctc = new ArrayList<>();
        int cur = 0;
        int[] a = new int[n];
        Arrays.fill(a, -1);
        a[0] = 0;
        ctc.add(new ArrayList<>());
        ctc.get(cur).add(0);
        int k;
        for (int i = 1; i < n; i++) {
            if (a[i] == -1) {
                ctc.add(new ArrayList<>());
                cur++;
                k = i;
                do {
                    ctc.get(cur).add(k);
                    a[k] = cur;
                    k = (k * 2) % n;
                } while (k != i);
            }
        }
        System.out.println("Zajęcia z cyklotomii: " + ctc);
        //System.out.println(Arrays.toString(a));
        ArrayList<Polynom> minpol = new ArrayList<>(ctc.size());
        minpol.add(new Polynom("11", 2));
        for (int i = 1; i < ctc.size(); i++) {
            minpol.add(findMinPol(alpha, ctc.get(i)));
        }
        System.out.println("Minimalne wielomiany dla nich to: " + minpol);
        ArrayList<Polynom> usedminpol = new ArrayList<>(ctc.size());
        for (int i = 1; i <= t * 2; i++)
            usedminpol.add(minpol.get(a[i]));
        for (int i = 0; i < usedminpol.size() - 1; i++) {
            for (int j = i + 1; j < usedminpol.size(); j++) {
                if (usedminpol.get(i).equals(usedminpol.get(j)))
                    usedminpol.remove(j--);
            }
        }
        System.out.println("Zastosowano następujące minimalne wielomiany: " + usedminpol);
        g = usedminpol.get(0);
        k = 1;
        while (k < usedminpol.size())
            g = g.multiply(usedminpol.get(k++));
        System.out.println("G = " + g);
        m_length = n - g.getDeg();
    }

    Polynom findMinPol(Polynom[] alpha, ArrayList<Integer> ctc) {
        class Elem {
            public final int x;
            public final int a;
            public final int n;

            public Elem(int x, int a, int n) {
                this.x = x;
                this.a = a;
                this.n = n;
            }

            public Elem mult(Elem other) {
                return new Elem((x + other.x), ((a + other.a) % n), n);
            }

            public boolean equals(Elem other) {
                return a == other.a && x == other.x;
            }
        }
        ArrayList<Elem> first = new ArrayList<>(2);
        first.add(new Elem(1, 0, n));
        first.add(new Elem(0, ctc.get(0), n));
        int k = 1;
        while (k < ctc.size()) {
            ArrayList<Elem> second = new ArrayList<>(2);
            second.add(new Elem(1, 0, n));
            second.add(new Elem(0, ctc.get(k++), n));
            ArrayList<Elem> tmp = new ArrayList<>(first.size() * 2);
            for (Elem elem : first) {
                for (int p = 0; p < 2; p++) {
                    tmp.add(elem.mult(second.get(p)));
                }
            }
            first = tmp;
        }
        for (int i = 0; i < first.size() - 1; i++) {
            for (int j = i + 1; j < first.size(); j++) {
                if (first.get(i).equals(first.get(j))) {
                    first.remove(j);
                    first.remove(i--);
                    break;
                }
            }
        }
        for (int i = 0; i < first.size() - 1; i++) {/////////////////////////////////////////////////////////////////
            if (first.get(i).a >= m) {
                int tmpa = first.get(i).a;
                int tmpx = first.get(i).x;
                first.remove(i--);
                int[] nums = alpha[tmpa].getNums();
                for (k = 0; k < nums.length; k++) {
                    if (nums[k] == 1)
                        first.add(new Elem(tmpx, k, n));
                }
            }
        }
        for (int i = 0; i < first.size() - 1; i++) {
            for (int j = i + 1; j < first.size(); j++) {
                if (first.get(i).equals(first.get(j))) {
                    first.remove(j);
                    first.remove(i--);
                    break;
                }
            }
        }
        int[] pol = new int[first.size()];
        for (int i = 0; i < first.size(); i++) {
            pol[i] = first.get(i).x;
        }
        Arrays.sort(pol);
        StringBuilder sb = new StringBuilder();
        k = pol.length - 1;
        for (int i = pol[pol.length - 1]; i >= 0; i--) {
            if (pol[k] == i) {
                k--;
                sb.append("1");
            } else sb.append("0");
        }
        return new Polynom(sb.toString(), 2);
    }

    public String encoding(String message) {
        Polynom m = new Polynom(message, 2);
        m = new Polynom(g.getDeg(), m);
        Polynom res = m.mod(g).add(m);
        StringBuilder promrez = new StringBuilder(res.toString());
        while (promrez.length() != n)
            promrez.insert(0, "0");

        return promrez.toString();
    }

    public String decoding(String str) {
        String promrez;
        Polynom s = new Polynom(str, 2);
        int[] S = new int[t * 2];
        Arrays.fill(S, -1);
        int[] nums = s.getNums();
        ArrayList<Integer> degs = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 1)
                degs.add(i);
        }
        int[] origdeg = degs.stream().mapToInt(i -> i).toArray();
        int[] deg = degs.stream().mapToInt(i -> i).toArray();
        Polynom tmpp = new Polynom(0, s);
        for (int i = 0; i < S.length; i++) {
            tmpp = tmpp.mod(prime[m]);
            for (int j = 0; j < alpha.length; j++) {
                if (tmpp.equals(alpha[j])) {
                    S[i] = j;
                    break;
                }
            }
            for (int j = 0; j < deg.length; j++) {
                deg[j] = (origdeg[j] * (i + 2)) % n;
            }
            Arrays.sort(deg);
            StringBuilder sb = new StringBuilder();
            int k = deg.length - 1;
            for (int j = n - 1; j >= 0; j--) {
                if (k >= 0 && j == deg[k]) {
                    if (k - 1 >= 0 && j == deg[k - 1]) {
                        k -= 2;
                        j++;
                        continue;
                    }
                    sb.append("1");
                    k--;
                } else
                    sb.append("0");
            }
            tmpp = new Polynom(sb.toString(), 2);
        }
        System.out.println("Components of syndrome: " + Arrays.toString(S));

        int v = t;
        int[][] M = new int[v][v];
        int det = -1;
        for (; v > 0; v--) {
            M = new int[v][v];
            for (int i = 0; i < v; i++) {
                System.arraycopy(S, i, M[i], 0, v);
            }
            det = findDet(M);
            System.out.println("Det of matrix size " + v + ": " + (det == -1 ? "0" : ("alpha^" + det)));
            if (det != -1) break;
        }

        if (det != -1) {
            int[][] Mobr = findObr(M, det);
            int[][] Sobr = new int[v][1];
            for (int i = 0; i < v; i++)
                Sobr[i][0] = S[v + i];
            int[] el = findErLoc(Mobr, Sobr);
            System.out.println("Error locator polynomials: " + Arrays.toString(el));
            int[] errors = new int[v];
            int counter = 0;
            int[] res = new int[v + 1];
            for (int i = 0; i < n; i++) {
                res[0] = 0;
                for (int j = v; j >= 1; j--)
                    res[j] = (i * j + el[v - j]) % n;
                Arrays.sort(res);
                StringBuilder sb = new StringBuilder();
                int k = res.length - 1;
                for (int j = n - 1; j >= 0; j--) {
                    if (k >= 0 && j == res[k]) {
                        if (k - 1 >= 0 && j == res[k - 1]) {
                            k -= 2;
                            j++;
                            continue;
                        }
                        sb.append("1");
                        k--;
                    } else
                        sb.append("0");
                }
                Polynom tmppp = new Polynom(sb.toString(), 2);
                tmppp = tmppp.mod(prime[m]);
                if (tmppp.isZero()) errors[counter++] = (n - i) % n;
            }
            System.out.println("Errors on index: " + Arrays.toString(errors));
            Arrays.sort(errors);
            StringBuilder sb = new StringBuilder();
            int k = errors.length - 1;
            for (int j = n - 1; j >= 0; j--) {
                if (k >= 0 && j == errors[k]) {
                    if (k - 1 >= 0 && j == errors[k - 1]) {
                        k -= 2;
                        j++;
                        continue;
                    }
                    sb.append(j < nums.length ? ((nums[j] + 1) % 2) : 1);
                    k--;
                } else
                    sb.append(j < nums.length ? nums[j] : 0);
            }

            promrez = sb.toString();
        } else
            promrez = s.toString();

        while (promrez.length() != n)
            promrez = "0" + promrez;

        return promrez.substring(0, n - g.getDeg());
    }

    public String decodingBM(String str) {
        StringBuilder promrez;
        Polynom s = new Polynom(str, 2);
        int[] S = new int[t * 2];
        Arrays.fill(S, -1);
        int[] nums = s.getNums();
        ArrayList<Integer> degs = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            if (nums[i] == 1)
                degs.add(i);
        }
        int[] origdeg = degs.stream().mapToInt(i -> i).toArray();
        int[] deg = degs.stream().mapToInt(i -> i).toArray();
        Polynom tmpp = new Polynom(0, s);
        for (int i = 0; i < S.length; i++) {
            tmpp = tmpp.mod(prime[m]);
            for (int j = 0; j < alpha.length; j++) {
                if (tmpp.equals(alpha[j])) {
                    S[i] = j;
                    break;
                }
            }
            for (int j = 0; j < deg.length; j++) {
                deg[j] = (origdeg[j] * (i + 2)) % n;
            }
            Arrays.sort(deg);
            StringBuilder sb = new StringBuilder();
            int k = deg.length - 1;
            for (int j = n - 1; j >= 0; j--) {
                if (k >= 0 && j == deg[k]) {
                    if (k - 1 >= 0 && j == deg[k - 1]) {
                        k -= 2;
                        j++;
                        continue;
                    }
                    sb.append("1");
                    k--;
                } else
                    sb.append("0");
            }
            tmpp = new Polynom(sb.toString(), 2);
        }
        System.out.println("Składniki zespołu: " + Arrays.toString(S));

        int[] el = null;
        int v = 1;
        int alert = 0;
        for (int value : S) {
            if (value != -1) {
                alert = 1;
                el = BM(S);
                if (el == null) {
                    System.out.println("\n" +
                            "W przyjętym słowie więcej " + t + "błędów");
                    return "błąd";
                }
                v = el.length - 1;
                break;
            }
        }

        if (v > 0 && alert == 1) {
            //System.out.println("Error locator polynomials found using BM algorithm: " + Arrays.toString(el));
            int[] errors = new int[v];
            Arrays.fill(errors, -1);
            int counter = 0;
            int[] res = new int[v + 1];
            for (int i = 0; i < n; i++) {
                res[0] = 0;
                for (int j = v; j >= 1; j--) {
                    if (el[j] == -1)
                        res[j] = -1;
                    else
                        res[j] = (i * j + el[j]) % n;
                }
                Arrays.sort(res);
                StringBuilder sb = new StringBuilder();
                int k = res.length - 1;
                for (int j = n - 1; j >= 0; j--) {
                    if (k >= 0 && j == res[k]) {
                        if (k - 1 >= 0 && j == res[k - 1]) {
                            k -= 2;
                            j++;
                            continue;
                        }
                        sb.append("1");
                        k--;
                    } else
                        sb.append("0");
                }
                Polynom tmppp = new Polynom(sb.toString(), 2);
                tmppp = tmppp.mod(prime[m]);
                if (tmppp.isZero()) errors[counter++] = (n - i) % n;
            }
            for (int i = 0; i < v; i++) {
                if (errors[i] == -1) {
                    System.out.println("\n" +
                            "W przyjętym słowie więcej " + t + "błędów");
                    return "błąd";
                }
            }
            System.out.println("Wielomian lokalizatora błędów znaleziony przy użyciu algorytmu BM: " + Arrays.toString(el));
            System.out.println("Wystąpiły błędy w następujących stopniach: " + Arrays.toString(errors));
            Arrays.sort(errors);
            StringBuilder sb = new StringBuilder();
            int k = errors.length - 1;
            for (int j = n - 1; j >= 0; j--) {
                if (k >= 0 && j == errors[k]) {
                    if (k - 1 >= 0 && j == errors[k - 1]) {
                        k -= 2;
                        j++;
                        continue;
                    }
                    sb.append(j < nums.length ? ((nums[j] + 1) % 2) : 1);
                    k--;
                } else
                    sb.append(j < nums.length ? nums[j] : 0);
            }

            promrez = new StringBuilder(sb.toString());
        } else
            promrez = new StringBuilder(s.toString());

        while (promrez.length() != n)
            promrez.insert(0, "0");

        return promrez.substring(0, n - g.getDeg());
    }

    public int[] BM(int[] S) {

        class Elem {
            public int x;
            public final int a;

            public Elem(int x, int a) {
                this.x = x;
                this.a = a;
            }

            public Elem mult(Elem other) {
                return new Elem((x + other.x), ((a + other.a) % n));
            }

            public boolean equals(Elem other) {
                return a == other.a && x == other.x;
            }
        }
        Comparator<Elem> comparator = new Comparator<Elem>() {
            @Override
            public int compare(Elem elem, Elem t1) {
                return Integer.compare(elem.x, t1.x);
            }
        };

        int r = 0;
        int L = 0;
        int deltaR = -1;
        ArrayList<Elem> B = new ArrayList<>();
        ArrayList<Elem> E = new ArrayList<>();
        ArrayList<Elem> T = new ArrayList<>();
        ArrayList<Integer> delta = new ArrayList<>();
        B.add(new Elem(0, 0));
        E.add(new Elem(0, 0));
        while (r < 2 * t) {
            r++;
            delta.clear();
            int q = 0;
            for (int i = 0; i <= L; i++) {
                if (S[r - i - 1] == -1) {
                    if (E.get(q).x == i)
                        q++;
                    continue;
                }
                if (E.get(q).x != i)
                    continue;
                int tmp = (E.get(q++).a + S[r - i - 1]) % n;
                int[] degs = alpha[tmp].getNums();
                for (int j = 0; j < degs.length; j++) {
                    if (degs[j] == 1)
                        delta.add(j);
                }
            }
            for (int i = 0; i < delta.size() - 1; i++) {
                for (int j = i + 1; j < delta.size(); j++) {
                    if (delta.get(i).equals(delta.get(j))) {
                        delta.remove(j);
                        delta.remove(i--);
                        break;
                    }
                }
            }
            if (!delta.isEmpty()) {
                int[] pol = new int[delta.size()];
                for (int i = 0; i < delta.size(); i++) {
                    pol[i] = delta.get(i);
                }
                Arrays.sort(pol);
                StringBuilder sb = new StringBuilder();
                int k = pol.length - 1;
                for (int i = pol[pol.length - 1]; i >= 0; i--) {
                    if (k >= 0 && pol[k] == i) {
                        k--;
                        sb.append("1");
                    } else sb.append("0");
                }
                Polynom tmpp = new Polynom(sb.toString(), 2);
                for (int i = 0; i < n; i++) {
                    if (tmpp.equals(alpha[i]))
                        deltaR = i;
                }
            } else {
                deltaR = -1;
            }


            if (deltaR != -1) {
                T.clear();
                for (q = 0; q < B.size(); q++) {
                    T.add(new Elem(B.get(q).x + 1, (B.get(q).a + deltaR) % n));
                }
                for (q = 0; q < E.size(); q++) {
                    T.add(new Elem(E.get(q).x, E.get(q).a));
                }
                ////////Надо ли выравнивать степени по иксам в Т(х)?
                T.sort(comparator);

                if (2 * L <= r - 1) {
                    B.clear();
                    for (int i = 0; i < E.size(); i++) {
                        B.add(new Elem(E.get(i).x, (E.get(i).a - deltaR + n) % n));
                    }
                    E = (ArrayList<Elem>) T.clone();
                    for (int i = 0; i < E.size() - 1; i++) {
                        int j = i + 1;
                        int a = 0;
                        int x = E.get(i).x;
                        ArrayList<Integer> degs = new ArrayList<>();
                        degs.add(E.get(i).a);
                        while (j < E.size() && E.get(j).x == E.get(i).x) {
                            degs.add(E.get(j).a);
                            j++;
                        }
                        if (degs.size() < 2)
                            continue;
                        int[] pol = new int[degs.size()];
                        for (j = 0; j < degs.size(); j++) {
                            pol[j] = degs.get(j);
                        }
                        Arrays.sort(pol);
                        StringBuilder sb = new StringBuilder();
                        int k = pol.length - 1;
                        for (j = pol[pol.length - 1]; j >= 0; j--) {
                            if (k >= 0 && pol[k] == j) {
                                k--;
                                sb.append("1");
                            } else sb.append("0");
                        }
                        Polynom tmpp = new Polynom(sb.toString(), 2);
                        tmpp = tmpp.mod(prime[m]);
                        for (j = 0; j < n; j++) {
                            if (tmpp.equals(alpha[j]))
                                a = j;
                        }
                        for (j = 0; j < E.size(); j++) {
                            if (E.get(j).x == x)
                                E.remove(j--);
                        }
                        E.add(new Elem(x, a));
                        i--;
                    }
                    E.sort(comparator);

                    L = r - L;
                    continue;
                }

                E = (ArrayList<Elem>) T.clone();
                for (int i = 0; i < E.size() - 1; i++) {
                    int j = i + 1;
                    int a = 0;
                    int x = E.get(i).x;
                    ArrayList<Integer> degs = new ArrayList<>();
                    degs.add(E.get(i).a);
                    while (j < E.size() && E.get(j).x == E.get(i).x) {
                        degs.add(E.get(j).a);
                        j++;
                    }
                    if (degs.size() < 2)
                        continue;
                    int[] pol = new int[degs.size()];
                    for (j = 0; j < degs.size(); j++) {
                        pol[j] = degs.get(j);
                    }
                    Arrays.sort(pol);
                    StringBuilder sb = new StringBuilder();
                    int k = pol.length - 1;
                    for (j = pol[pol.length - 1]; j >= 0; j--) {
                        if (k >= 0 && pol[k] == j) {
                            k--;
                            sb.append("1");
                        } else sb.append("0");
                    }
                    Polynom tmpp = new Polynom(sb.toString(), 2);
                    tmpp = tmpp.mod(prime[m]);
                    for (j = 0; j < n; j++) {
                        if (tmpp.equals(alpha[j]))
                            a = j;
                    }
                    for (j = 0; j < E.size(); j++) {
                        if (E.get(j).x == x)
                            E.remove(j--);
                    }
                    E.add(new Elem(x, a));
                    i--;
                }
                E.sort(comparator);

            }

            for (Elem elem : B) {
                elem.x++;
            }


        }

        //dodaj alfa do e z równymi wykładnikami w x
        for (int i = 0; i < E.size() - 1; i++) {
            int j = i + 1;
            int a = 0;
            int x = E.get(i).x;
            ArrayList<Integer> degs = new ArrayList<>();
            degs.add(E.get(i).a);
            while (j < E.size() && E.get(j).x == E.get(i).x) {
                degs.add(E.get(j).a);
                j++;
            }
            if (degs.size() < 2)
                continue;
            int[] pol = new int[degs.size()];
            for (j = 0; j < degs.size(); j++) {
                pol[j] = degs.get(j);
            }
            Arrays.sort(pol);
            StringBuilder sb = new StringBuilder();
            int k = pol.length - 1;
            for (j = pol[pol.length - 1]; j >= 0; j--) {
                if (k >= 0 && pol[k] == j) {
                    k--;
                    sb.append("1");
                } else sb.append("0");
            }
            Polynom tmpp = new Polynom(sb.toString(), 2);
            tmpp = tmpp.mod(prime[m]);
            for (j = 0; j < n; j++) {
                if (tmpp.equals(alpha[j]))
                    a = j;
            }
            for (j = 0; j < E.size(); j++) {
                if (E.get(j).x == x)
                    E.remove(j--);
            }
            E.add(new Elem(x, a));
            i--;

        }
        E.sort(comparator);

        if (!(E.get(E.size() - 1).x == L)) {
            return null;
        }
        int[] res = new int[E.get(E.size() - 1).x + 1];
        int q = 0;
        for (int i = 0; i < res.length; i++) {
            if (E.get(q).x == i)
                res[i] = E.get(q++).a;
            else
                res[i] = -1;
        }
        return res;
    }

    public int[] findErLoc(int[][] Mobr, int[][] Sobr) {
        int[] el = new int[Mobr.length];
        for (int i = 0; i < Mobr.length; i++) {
            int[] res = new int[Mobr.length];
            for (int j = 0; j < Mobr.length; j++) {
                res[j] = (Mobr[i][j] + Sobr[j][0]) % n;
            }
            Arrays.sort(res);
            StringBuilder sb = new StringBuilder();
            int k = res.length - 1;
            for (int j = n - 1; j >= 0; j--) {
                if (k >= 0 && j == res[k]) {
                    if (k - 1 >= 0 && j == res[k - 1]) {
                        k -= 2;
                        j++;
                        continue;
                    }
                    sb.append("1");
                    k--;
                } else
                    sb.append("0");
            }
            Polynom tmpp = new Polynom(sb.toString(), 2);
            tmpp = tmpp.mod(prime[m]);
            el[i] = -1;
            for (int j = 0; j < alpha.length; j++) {
                if (tmpp.equals(alpha[j])) {
                    el[i] = j;
                    break;
                }
            }
        }
        return el;
    }

    public int[][] findObr(int[][] M, int det) {
        int[][] Mobr = new int[M.length][M.length];
        if (M.length == 1) {
            Mobr[0][0] = n - M[0][0];
            return Mobr;
        }
        int[][] tmp = new int[M.length - 1][M.length - 1];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M.length; j++) {
                int qq = 0;
                int pp = 0;
                for (int q = 0; q < M.length; q++) {
                    if (q == i) {
                        continue;
                    }
                    pp = 0;
                    for (int p = 0; p < M.length; p++) {
                        if (p == j) {
                            continue;
                        }
                        tmp[qq][pp++] = M[q][p];
                    }
                    qq++;
                }
                Mobr[i][j] = findDet(tmp) % n;
            }
        }
        Mobr = transp(Mobr);
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M.length; j++) {
                Mobr[i][j] = (Mobr[i][j] - det + n) % n;
            }
        }
        return Mobr;
    }

    public int[][] transp(int[][] M) {
        int[][] Mt = new int[M.length][M.length];
        for (int i = 0; i < M.length; i++) {
            for (int j = 0; j < M.length; j++) {
                Mt[i][j] = M[j][i];
            }
        }
        return Mt;
    }

    public int findDet(int[][] source) {
        if (source.length == 1)
            return source[0][0];
        int[] res = new int[source.length];
        int[][] tmp = new int[source.length - 1][source.length - 1];
        for (int i = 0; i < source.length; i++) {
            int k = 0;
            for (int q = 1; q < source.length; q++) {
                for (int p = 0; p < source.length; p++) {
                    if (p == i)
                        continue;
                    tmp[q - 1][k++ % tmp.length] = source[q][p];
                }
                //if (j == i)
                //    continue;
                //System.arraycopy(source[j], 1, tmp[k++], 0, tmp.length);
            }
            int det = findDet(tmp) % n;
            if (det != -1) {
                res[i] = (source[0][i] + det) % n;
            } else
                res[i] = -1;
        }
        Arrays.sort(res);
        StringBuilder sb = new StringBuilder();
        int k = res.length - 1;
        for (int j = n - 1; j >= 0; j--) {
            if (k >= 0 && j == res[k]) {
                if (k - 1 >= 0 && j == res[k - 1]) {
                    k -= 2;
                    j++;
                    continue;
                }
                sb.append("1");
                k--;
            } else
                sb.append("0");
        }
        Polynom tmpp = new Polynom(sb.toString(), 2);
        tmpp = tmpp.mod(prime[m]);
        int r = -1;
        for (int j = 0; j < alpha.length; j++) {
            if (tmpp.equals(alpha[j])) {
                r = j;
                break;
            }
        }
        return r;
    }

    public int getM_length() {
        return m_length;
    }
}