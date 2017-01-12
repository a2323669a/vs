package ni;

public class Util {
	public static double getSolution(double a,double b,double c){
        double x1,x2,t;

        t = b * b - 4 * a * c;

        System.out.println("方程" + a + "x*x+" + b + "x+" + c + "=0的解：");

        if(t < 0){
        	t = 0;
            System.out.println("此方程无解");
            return 0;

        }

            x1 = ((-b)+Math.pow(t,0.5))/(2*a);

            x2 = ((-b)-Math.pow(t,0.5))/(2*a);
            
            return x1>x2?x1:x2;
	}
}
