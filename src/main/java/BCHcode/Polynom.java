package BCHcode;


import java.util.Arrays;

public class Polynom {

    private int deg;
    private int[] nums;
    private int mod = 0;

    public Polynom(String n, int m){
        mod = m;
        deg = n.length()-1;
        char[] c = n.toCharArray();
        int i = 0;
        while (c[i++] == '0' && deg > 0) deg--;
        nums = new int[deg+1];
        int k = 0;
        for (i = n.length()-1; i >= n.length()-deg-1; i--) {
            if (c[i] == '0') nums[k++] = 0;
            if (c[i] == '1') nums[k++] = 1;
        }
    }

    public Polynom(int s, Polynom p){ //????????????
        String oldPoly = p.toString();
        for (int i = 0; i < s; i++)
            oldPoly += "0";
        deg = s+p.getDeg();
        nums = new int[deg+1];
        char[] c = oldPoly.toCharArray();
        int i = 0;
        while (c[i++] == '0' && deg > 0) deg--;
        nums = new int[deg+1];
        int k = 0;
        for (i = oldPoly.length()-1; i >= oldPoly.length()-deg-1; i--) {
            if (c[i] == '0') nums[k++] = 0;
            if (c[i] == '1') nums[k++] = 1;
        }
        mod = p.getMod();
    }

    public Polynom add(Polynom b){
        int resdeg = Math.max(deg, b.deg);
        int[] resNums = new int[resdeg+1];
        StringBuilder sb = new StringBuilder();
        int i = resdeg;
        while (!(i <= b.deg && i <= deg)){
            if (i <= b.deg)
                sb.append(b.nums[i]);
            else
                sb.append(nums[i]);
            i--;
        }
        while (i >= 0){
            sb.append((b.nums[i] + nums[i]) % mod);
            i--;
        }
        return new Polynom(sb.toString(), mod);
    }

    public Polynom multiply(Polynom b){
        Polynom rez = new Polynom(b.deg, this);
        for (int i = 0; i < b.deg; i++){
            if(b.nums[i] == 1){
                rez = rez.add(new Polynom(i, this));
            }
        }
        return rez;
    }

    public Polynom concat(Polynom b){
        int [] bNums = b.getNums();
        StringBuilder sb = new StringBuilder();
        int i = deg;
        while (i > b.deg){
            sb.append(nums[i]);
            i--;
        }
        for (i = b.deg; i >= 0; i--){
            sb.append(bNums[i]);
        }
        return new Polynom(sb.toString(), mod);
    }

    public boolean isZero(){
        for (int i = 0; i<=deg; i++){
            if (nums[i] != 0) return false;
        }
        return true;
    }

    public Polynom mod(Polynom b){
        Polynom c;
        if (deg < b.deg) return this;
        Polynom a = new Polynom(0, this);
        while (true) {
            Polynom b1 = new Polynom(a.deg - b.deg, b);
            c = b1.add(a);
            if (c.deg < b.deg) break;
            a = c;
        }
        return c; /////////ATTENTION!
    }

    public int getDeg(){
        return deg;
    }

    public int[] getNums(){
        return nums;
    }

    public int getMod() {
        return mod;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        for (int i = deg; i >= 0; i--) {
            switch (nums[i]) {
                case 0:
                    sb.append("0");
                    break;
                case 1:
                    sb.append("1");
            }
        }
        return sb.toString();
    }

    public boolean equals(Polynom other){
        if (Arrays.equals(nums, other.nums))
            return true;
        return false;
    }
}