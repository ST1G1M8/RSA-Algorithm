package rsa;


import java.math.BigInteger;
import java.util.Random;
import java.util.Scanner;

public class Main {

    private static boolean MR_Test(BigInteger num){ // Miller-Rabin Test

        BigInteger d = num.subtract(BigInteger.ONE);
        BigInteger fifty = new BigInteger("50");
        int s = 0;
        while((d.mod(BigInteger.TWO)).equals(BigInteger.ZERO)){
            d = d.divide(BigInteger.TWO);
            s++;
        }

        BigInteger a = BigInteger.valueOf(getRandomNumberInRange(BigInteger.TWO,fifty));

        if(a.modPow(d,num).equals(BigInteger.ONE)) {
            return false;
        }

        for(int r=0;r<s;r++) {
            if(a.modPow(d.multiply(BigInteger.TWO.pow(r)),num).equals(num.subtract(BigInteger.ONE))) {
                return false;
            }
        }

        return true;
    }

    private static int getRandomNumberInRange(BigInteger min, BigInteger max) {

        if (min.compareTo(max) == 1) { // min > max
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max.intValue() - min.intValue()) + 1) + min.intValue();
    }


    private static BigInteger EEA(BigInteger a, BigInteger b){ // Extended Euclidean Algorithm
        BigInteger[] r = new BigInteger[100];
        BigInteger[] q = new BigInteger[100];
        BigInteger[] x = new BigInteger[100];
        BigInteger[] y = new BigInteger[100];
        int i = 0;
        r[0] = a;
        r[1] = b;
        int n = 0;
        x[0] = BigInteger.ONE;
        x[1] = BigInteger.ZERO;
        y[0] = BigInteger.ZERO;
        y[1] = BigInteger.ONE;
        BigInteger Xm = BigInteger.ZERO;
        BigInteger Ym = BigInteger.ZERO;


        while(!(r[i].mod(r[i+1]).equals(BigInteger.ZERO))){

            q[i+1] = r[i].divide(r[i+1]);
            r[i+2] = r[i].mod(r[i+1]);
            i++;

            x[i+1] = (x[i].multiply(q[i])).add(x[i-1]);
            y[i+1] = (y[i].multiply(q[i])).add(y[i-1]);

            if((r[i].mod(r[i+1])).equals(BigInteger.ZERO)){
                n = i + 1;
                Xm = (BigInteger.ONE.negate().pow(n)).multiply(x[n]);
                Ym = (BigInteger.ONE.negate().pow(n+1)).multiply(y[n]);
            }

        }

        return Xm;
    }

    private static boolean isPowerOfTwo(BigInteger n) {

        if (n.compareTo(BigInteger.ZERO) == 0)
            return false;

        while (n.compareTo(BigInteger.ONE) != 0)
        {

            if (!(n.mod(BigInteger.TWO).equals(BigInteger.ZERO)))
                return false;

            n = n.divide(BigInteger.TWO);

        }

        return true;
    }

    private static BigInteger FME(BigInteger a, BigInteger b, BigInteger c){ // Fast Modular Exponentiation
        int i = 1;
        BigInteger m = a.mod(c);
        BigInteger d = BigInteger.ONE;
        BigInteger tmp = a.mod(c);

        if(isPowerOfTwo(b)){

            while(!(BigInteger.TWO.pow(i).equals(b))){

                m = (m.multiply(m)).mod(c);
                i = i * 2;

            }
            return m;

        } else {

            String binary = b.toString(2);
            //System.out.println(binary);

            StringBuilder input = new StringBuilder();
            input.append(binary);
            StringBuilder binary_backwards = input.reverse();
            //System.out.println(binary_backwards);

            for(int j=1;j<binary_backwards.length();j++){


                if(binary_backwards.charAt(j) == '1'){

                    d = d.multiply(BigInteger.TWO);
                    m = (m.multiply(m)).mod(c);
                    tmp = tmp.multiply(m);

                } else{

                    d = d.multiply(BigInteger.TWO);
                    m = (m.multiply(m)).mod(c);
                }
            }
            m = tmp.mod(c);
        }

        return m;

    }


    public static void main(String[] args) {

        BigInteger p = BigInteger.ZERO;
        BigInteger q = BigInteger.ZERO;
        int k = 3;

        boolean isComposite = false;
        while(!isComposite) {
            Random rand = new Random();
            BigInteger number = new BigInteger(100,rand);
            for(int i=0;i<k;i++) {
                if(MR_Test(number)) {
                    isComposite = true;
                }
            }
            if(!isComposite) {
                p = number;
                break;
            }else {
                isComposite = false;
            }
        }
        System.out.println("p = "+p);

        isComposite = false;
        while(!isComposite) {
            Random rand = new Random();
            BigInteger number = new BigInteger(100,rand);
            for(int i=0;i<k;i++) {
                if(MR_Test(number)) {
                    isComposite=true;
                }
            }
            if(!isComposite) {
                q = number;
                break;
            } else {
                isComposite = false;
            }
        }
        System.out.println("q = "+q);

        BigInteger n = p.multiply(q);
        BigInteger fi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        //System.out.println("n = "+n);
        //System.out.println("fi = "+fi);

        BigInteger e = new BigInteger("3");
        while(!(e.gcd(fi).equals(BigInteger.ONE))){
            e = e.add(BigInteger.ONE);
        }

        BigInteger d = EEA(e,fi);

        if(d.compareTo(BigInteger.ZERO) == -1){
            d = d.add(fi);
        }

        System.out.println("PK: ("+n+";"+e+")");
        System.out.println("SK: ("+d+")");

        System.out.println("Please write down your message - (number)");
        Scanner msg = new Scanner(System.in);
        BigInteger inp = msg.nextBigInteger();

        while(inp.compareTo(n) == 1) {
            System.out.println("The message is too big");
            System.out.println("Please write down your message - (number)");
            inp = msg.nextBigInteger();
        }

        System.out.println("Type 'enc' if you want to encrypt Or Type 'dec' if you want to decrypt");
        String answer = msg.next();

        if(answer.equals("enc")){
            BigInteger c = FME(inp,e,n);
            System.out.println("Encrypted Message: "+c);
            BigInteger m = FME(c,d,n);
            System.out.println("Original Message: "+m);

        } else if(answer.equals("dec")){
            BigInteger c = FME(inp,d,n);
            System.out.println("Decrypted Message: "+c);
            BigInteger m = FME(c,e,n);
            System.out.println("Original Secret Message: "+m);

        } else {
            System.out.println("Error: Unexpected Input");
            System.exit(-1);
        }

    }

}