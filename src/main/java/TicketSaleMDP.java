import java.util.Arrays;

import arrays.AH;
import distributions.DiscreteIntDistribution;
import util.MyMath;

/**
 * Basic MDP model to determine an optimal pricing strategy.
 * 
 * @author Christian Ruf
 *
 */
class TicketSaleMDP {

    /* Parameter */

    int                       T   = 60;               // 2 month
    int                       S   = 50;
    int                       K   = 4;
    int[]                     pks = { 9, 12, 15, 19 };

    double[]                  prShow;
    DiscreteIntDistribution[] wp;

    /* Algorithm requesites */

    double[][]                vs;
    double[][]                ps;

    /** Init params */
    TicketSaleMDP() {
        prShow = new double[T];
        for (int t = 0; t < T; t++)
            prShow[t] = (1.0 / T) * t;

        int[] events = { 9, 12, 15, 19 };
        wp = new DiscreteIntDistribution[T];
        int k = 0;
        for (int t = 0; t < T; t++) {
            double[] drifter = new double[K];
            drifter[0] = MyMath.max0(100 - t * 2);
            drifter[1] = MyMath.max0(50 - t * 0.95);
            drifter[2] = MyMath.max0(-5 + t * 1.0);
            drifter[3] = MyMath.max0(-40 + t * 1.5);
            if (drifter[k] > 0) {
                drifter[k]--;
                drifter[k + 1]++;
                k = (k + 1) % (K - 1);
            }
            double[] probs = AH.mult(1.0 / (AH.sum(drifter)), drifter);
            wp[t] = new DiscreteIntDistribution(events, probs);
            System.out.println(Arrays.toString(probs));
        }
    }

    /**
     * System model.
     * 
     * @param s
     *            # seats
     * @param p
     *            price set
     * @param w
     *            WP of the one new customer.
     * @return next s
     */
    static int sm(int s, double p, double w) {
        if (s == 0) return 0;
        if (w >= p) return s - 1;
        return s;
    }

    /**
     * Reward function.
     * 
     * @param p
     *            price set
     * @param n
     *            # tickets sold for p
     * @return reward
     */
    double r(double p, int n) {
        return p * n;
    }

    /** Run the backwards DP algorithm. */
    void runDP() {
        System.out.println("backwards DP mit T = " + T);
        vs = new double[T + 1][S + 1];
        ps = new double[T][S + 1];
        for (int t = T - 1; t >= 0; t--) { // go backwards
            System.out.println("t=" + t);
            for (int s = 0; s <= S; s++) {
                double max = -Double.MAX_VALUE;
                int argmax = 0;
                for (int p : pks) {
                    double val = 0;

                    // iterate over events
                    {
                        // no customer shows up
                        val += (1 - prShow[t]) * (vs[t + 1][s]);
                    }
                    // 1 customer shows up
                    for (int ind = 0; ind < wp[t].getEvents().length; ind++) {
                        int w = wp[t].getEvents()[ind];
                        double pr = wp[t].getPdf()[ind];
                        int sNext = sm(s, p, w);
                        int nSales = s - sNext;
                        val += prShow[t] * pr * (r(p, nSales) + vs[t + 1][sNext]);
                    }

                    if (val > max) {
                        max = val;
                        argmax = p;
                    }
                }
                vs[t][s] = max;
                ps[t][s] = argmax;
            }
        }
        System.out.println("backwards DP finished, print csv");
        for (int s = 0; s <= S; s++) {
            for (int t = 0; t < T; t++)
                System.out.print(ps[t][s] + "," + MyMath.round(vs[t][s], 1) + ";");
            System.out.println();
        }
    }

    public static void main(String[] a) {
        TicketSaleMDP m = new TicketSaleMDP();
        m.runDP();
    }
}
