import java.awt.Point;
import java.util.ArrayList;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleUnaryOperator;


public class main {
    public static void main(String[] args){
        testAlgIntervalUn();
    }

    public static void testAlgIntervalUn(){
        DoubleUnaryOperator func = x -> -Math.pow(x, 2) + 5;
        Double radius = 1.0;
        Double steps = 0.1;
        Pair<Double, Double> intervalX = new Pair<>(-5d, 5d);
        Double start = 3d;
        DoubleBinaryOperator distanceFunc = ((p1, p2) ->
                Math.abs(p1 - p2)
        );
        Double result = bergsteigerWithIntervalForUnaFun(intervalX,  start, func, distanceFunc, steps, radius);
        System.out.println("Calculated local maximum with start Point(" + start + "): (" + result + ") -> " + func.applyAsDouble(result) +"\n\n");
    }

    public static void testAlgIntervalBin(){
        DoubleBinaryOperator func = (x, y) -> Math.sin(x);
        Double radius = 1.0;
        Double steps = 0.1;
        Pair<Double, Double> intervalX = new Pair<>(-5d, 5d);
        Pair<Double, Double> intervalY = new Pair<>(0d, 0d);
        Pair<Double, Double> start =  new Pair<>(3d, 0d);
        PairToDoubleOperator<Double, Double> distanceFunc = ((p1, p2) ->
                Math.max(Math.abs(p1.val1 - p2.val1), Math.abs(p1.val2 - p2.val2))
        );
        Pair<Double, Double> result = bergsteigerWithIntervalForBinFun(intervalX, intervalY, start, func, distanceFunc, steps, radius);
        System.out.println("Calculated local maximum with start Point(" + start.val1 + ", " + start.val2 + "): (" + result.val1 + ", " + result.val2 + ") -> " + func.applyAsDouble(result.val1, result.val2) +"\n\n");
    }

    public static void testAlgSet(){
        DoubleBinaryOperator func = (x, y) -> Math.pow(2, -x * x - y * y + 1) + 3 * Math.pow(2, -x * x - y * y + 2 * x + 4 * y - 5);
        int[] L = {-4, -3, -2, -1, 0, 1, 2, 3, 4};
        Double radius = 1d;
        PointBinaryToDoubleOperator distanceFunc = ((p1, p2) ->
                Math.max((double)Math.abs(p1.x - p2.x), (double)Math.abs(p1.y - p2.y))
        );
        for(int i = 0; i < L.length; i++){
            for( int j = 0; j < L.length; j++){
                double res = func.applyAsDouble(L[j], L[i]);
                System.out.println("(" + L[j] + ", " + L[i] + "): " + res);
                for(int c = 0; c < res * 10; c++){
                    System.out.print('#');
                }
                System.out.println("");
            }
        }
        Point start = new Point(-2, -4);
        Point max = bergsteigerWithSet(L, start, func, distanceFunc, radius);
        System.out.println("Calculated local maximum with start Point(" + start.x + ", " + start.y + "): (" + max.x + ", " + max.y + ") -> " + func.applyAsDouble(max.x, max.y) +"\n\n");
        System.out.print("\n\n\n");
        start = new Point(4, 4);
        max = bergsteigerWithSet(L, start, func,distanceFunc, radius);
        System.out.println("Calculated local maximum with start Point(" + start.x + ", " + start.y + "): (" + max.x + ", " + max.y + ") -> " + func.applyAsDouble(max.x, max.y) +"\n\n");
        ArrayList<Point> adjPs = adjacentValuesSet(new Point(2, 2), L, distanceFunc, radius);
        for(Point point : adjPs){
            System.out.println("(" + point.x + ", " + point.y + ")");
        }
    }

    public static Point bergsteigerWithSet(int[] werte, Point start, DoubleBinaryOperator func, PointBinaryToDoubleOperator distanceFunc, double radius){
        Point current = start;
        Double currentVal = func.applyAsDouble(current.x, current.y);
        System.out.print("\n (" + current.x + ", " + current.y + ")");
        wh:
        while(true){
            ArrayList<Point> adjPoints = adjacentValuesSet(current, werte, distanceFunc, radius);
            boolean newMax = false;
            for(Point test : adjPoints){
                if(func.applyAsDouble(test.x, test.y) > currentVal){
                    currentVal = func.applyAsDouble(test.x, test.y);
                    current = test;
                    System.out.print(" -> (" + current.x + ", " + current.y + ")");
                    newMax = true;
                }
            }
            if(newMax)
                continue wh;
            break;
        }
        System.out.print("\n");
        return current;
    }

    public static Pair<Double, Double> bergsteigerWithIntervalForBinFun(Pair<Double, Double> intervalX, Pair<Double, Double> intervalY, Pair<Double, Double> start, DoubleBinaryOperator func, PairToDoubleOperator<Double, Double> distanceFunc, double steps, double radius){
        Pair<Double, Double> current = start;
        Double currentVal = func.applyAsDouble(current.val1, current.val2);
        System.out.print("\n (" + current.val1 + ", " + current.val2 + ")");
        wh:
        while(true){
            ArrayList<Pair<Double, Double>> adjPoints = adjacentValuesIntervalR2(current, intervalX, intervalY, steps, radius, distanceFunc);
            boolean newMax = false;
            for(Pair<Double, Double> test : adjPoints){
                if(func.applyAsDouble(test.val1, test.val2) > currentVal){
                    currentVal = func.applyAsDouble(test.val1, test.val2);
                    current = test;
                    System.out.print(" -> (" + current.val1 + ", " + current.val2 + ")");
                    newMax = true;
                }
            }
            if(newMax)
                continue wh;
            break;
        }
        System.out.print("\n");
        return current;
    }

    public static Double bergsteigerWithIntervalForUnaFun(Pair<Double, Double> intervalX, Double start, DoubleUnaryOperator func, DoubleBinaryOperator distanceFunc, double steps, double radius){
        Double current = start;
        Double currentVal = func.applyAsDouble(current);
        System.out.print("\n " + current + "");
        wh:
        while(true){
            ArrayList<Double> adjPoints = adjacentValuesIntervalR1(current, intervalX, steps, radius, distanceFunc);
            boolean newMax = false;
            for(Double test : adjPoints){
                if(func.applyAsDouble(test) > currentVal){
                    currentVal = func.applyAsDouble(test);
                    current = test;
                    System.out.print(" -> " + current);
                    newMax = true;
                }
            }
            if(newMax)
                continue wh;
            break;
        }
        System.out.print("\n");
        return current;
    }

    public static ArrayList<Point> adjacentValuesSet(Point current, int[] werte, PointBinaryToDoubleOperator distanceFunc, double radius){
        ArrayList<Point> points = new ArrayList<>();
        for(int i = 0; i < werte.length; i++){
            for(int j = 0; j < werte.length; j++) {
                Point test = new Point(werte[i], werte[j]);
                if (distanceFunc.applyAsPoint(current, test) <= radius) {
                    points.add(test);
                }
            }
        }
        return points;
    }

    public static ArrayList<Double> adjacentValuesIntervalR1(Double current, Pair<Double, Double> intervalX, double steps, double radius, DoubleBinaryOperator func){
        ArrayList<Double> points = new ArrayList<>();
        Double x = current;
        for(double xo = -radius; xo < radius; xo += steps){
            if(x + xo > intervalX.val2 || x + xo < intervalX.val1)
                continue;
                Double test = x + xo;
                if(func.applyAsDouble(test, x) <= radius)
                    points.add(test);
        }
        return points;
    }

    public static ArrayList<Pair<Double, Double>> adjacentValuesIntervalR2(Pair<Double, Double> current, Pair<Double, Double> intervalX, Pair<Double, Double> intervalY, double steps, double radius, PairToDoubleOperator<Double, Double> func){
        ArrayList<Pair<Double, Double>> points = new ArrayList<>();
        Double x = current.val1;
        Double y = current.val2;
        for(double xo = -radius; xo < radius; xo += steps){
            if(x + xo > intervalX.val2 || x + xo < intervalX.val1)
                continue;
            for(double yo = -radius; yo < radius; yo += steps){
                if(y + yo > intervalY.val2 || y + yo < intervalY.val1)
                    continue;
                Pair<Double, Double> test = new Pair<>(current.val1 + xo, current.val2 + yo);
                if(func.applyAsPair(current, test) <= radius)
                    points.add(test);
            }
        }
        return points;
    }

    public interface PointBinaryToDoubleOperator{
        public Double applyAsPoint(Point p1, Point p2);
    }

    public interface PairToDoubleOperator<S, T>{
        public Double applyAsPair(Pair<S,T> p1, Pair<S,T> p2);
    }

    private static class Pair<T, S>{
        T val1;
        S val2;

        public Pair(T v1, S v2){
            val1 = v1;
            val2 = v2;
        }
    }
}
