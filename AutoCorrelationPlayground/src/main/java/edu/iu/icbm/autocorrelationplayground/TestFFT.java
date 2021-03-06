/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.iu.icbm.autocorrelationplayground;

/**
 *
 * @author vinfrais
 * 
 * from http://stackoverflow.com/questions/12239096/computing-autocorrelation-with-fft-using-jtransforms-library
 */
//

import edu.emory.mathcs.jtransforms.fft.DoubleFFT_1D;
import java.util.Arrays;
import javax.swing.JFrame;
import java.util.Random;

public class TestFFT extends JFrame {

    void print(String msg, double [] x) {
        System.out.println(msg);
        for (double d : x) System.out.println(d);
    }

    /**
     * This is a "wrapped" signal processing-style autocorrelation. 
     * For "true" autocorrelation, the data must be zero padded.  
     */
    public void bruteForceAutoCorrelation(double [] x, double [] ac) {
        Arrays.fill(ac, 0);
        int n = x.length;
        for (int j = 0; j < n; j++) {
            for (int i = 0; i < n; i++) {
                ac[j] += x[i] * x[(n + i - j) % n];
            }
        }
    }

    private double sqr(double x) {
        return x * x;
    }

    public void fftAutoCorrelation(double [] x, double [] ac) {
        int n = x.length;
        // Assumes n is even.
        DoubleFFT_1D fft = new DoubleFFT_1D(n);
        fft.realForward(x); //real forwards fft of data
        ac[0] = sqr(x[0]); //square of fft of data is the first result
        //square is an auto-correlation
        //ac[0] = 0;  // For statistical convention, zero out the mean 
        ac[1] = sqr(x[1]);  //square of fft of data is the second result
        for (int i = 2; i < n; i += 2) {// step through n from 3rd every second (correlation)
            ac[i] = sqr(x[i]) + sqr(x[i+1]); // square of current fft plus the square of next fft
            ac[i+1] = 0; // next result is 0 (skipped over by n += 2
        }
        DoubleFFT_1D ifft = new DoubleFFT_1D(n); 
        ifft.realInverse(ac, true);//inverse the result ac 
        // For statistical convention, normalize by dividing through with variance
//        for (int i = 1; i < n; i++)
//            ac[i] /= ac[0];
//        ac[0] = 1;
    }

    void test() {
        System.out.println("Testing AutoCorrelations");
        double [] data = sineData(10000);
        double [] ac1 = new double [data.length];
        double [] ac2 = new double [data.length];
        long stop;
        long start; 
        start = System.currentTimeMillis();
        bruteForceAutoCorrelation(data, ac1);
        stop = System.currentTimeMillis();
        System.out.println("Brute force time: " + (stop-start) + " ms.");
        start = System.currentTimeMillis();
        fftAutoCorrelation(data, ac2);
        stop = System.currentTimeMillis();
        System.out.println("FFT time: " + (stop-start) + " ms.");
        //print("data", data);
        //print("bf", ac1);
        //print("fft", ac2);
        
        double err = 0;
        for (int i = 0; i < ac1.length; i++)
            err += sqr(ac1[i] - ac2[i]);
        System.out.println("err = " + err);
    }
    

    
    

    public static void main(String[] args) {
        new TestFFT().test();
        
        
    }
    
    public double [] sineData(int n) {
        
        double theta = 0;
        //double pi = 3.14159265359;
        double [] sine = new double [n*2];
        
         System.out.println("Generated data: ");
        for(int i = 0; i < n*2; i++){   
            Random r = new Random(System.nanoTime());
            double radian = (Math.PI/200)*i;
            sine[i] =poissonValue(Math.sin(radian)+5)+Math.sin(radian)+5;
            System.out.println(sine[i]);
    }       
    return sine;    
    }
    
    public class Diagram extends JFrame{
        
    }
    
    //http://rsb.info.nih.gov/ij/plugins/download/Poisson_Noise.java
    
    private double poissonValue(double pixVal) {
      Random random = new Random();
      double L = Math.exp(-(pixVal));
      int k = 0;
      double p = 1;
      do {
         k++;
         // Generate uniform random number u in [0,1] and let p ← p × u.
         p *= random.nextDouble();
      } while (p >= L);
      return (k - 1);
   }
    
}
