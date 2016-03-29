import javax.swing.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;


class AntSearch extends Thread
{
    private static Semaphore _critical = new Semaphore(1,false);
    public static OutOfMemoryError OutOfMemoryInThread;
    public static Semaphore _semAnts = new Semaphore(0,true);
    public static int _AntsCount;
    public static CountDownLatch _iterationDone;
    private int _currentAnt;
    public static double[][] n;

    public void run()
    {
      while (true)
      {
        try
        {
            _semAnts.acquire();
            if (OutOfMemoryInThread == null)
            {
                _critical.acquire(); _currentAnt = _AntsCount++; _critical.release();
                for (int i = 0; i < n.length; i++)
                    if (!ChoiceWay(_currentAnt)) break;
                CalculateDistance(OptionsAnts.getAntWays()[_currentAnt]);
            }
        }
        catch (InterruptedException e) {if (!Main.ItIsApplet) MyShow.Error(e.toString());}
        catch (Exception e) {MyShow.Error(e.toString());}
        catch (OutOfMemoryError e) {OutOfMemoryInThread = e;}
        finally {_iterationDone.countDown();}
      }
    }

    public static void CalculateDistance(String[] Way)
    {
        if ( Way[0] == null || !Way[0].equals("Not found") ) //if not null then "Not Found"
        {
            double Distance = 0;
            for (int i = 1; i < Way.length; i++)
            {
                MyPoints p1 = MyPoints.getMyPointByNumber(Integer.parseInt(Way[i]));
                MyPoints p2 = MyPoints.getMyPointByNumber(Integer.parseInt(Way[i+1]));
                Distance += p1.distance(p2);
                if (Integer.parseInt(Way[i+1]) == MyPoints.EndPoint) break;
            }
            Way[0] = ""+Distance;
        }
    }

    private static boolean TabooList(int CurrentAnt, MyPoints toPoint, MyPoints fromPoint)
    {
        if (!MyPoints.isInVisibilityRadius(fromPoint, toPoint)) return false;
        String[][] AntWays = OptionsAnts.getAntWays();
        for (int i = 1; i < AntWays[0].length; i++)
            if (AntWays[CurrentAnt][i] != null)
                if (AntWays[CurrentAnt][i].equals("Impasse") || AntWays[CurrentAnt][i].equals(""+toPoint.getPointNumber())) return false;
        return true;
    }

    private static int getFromPointIndexInArray(int CurrentAnt)
    {
        int FromPoint = - 1;
        Vector<MyPoints> MyPointsVector = OptionsAnts.getMyPointsVector();
        String[][] antsWay = OptionsAnts.getAntWays();
        for (int i = MyPointsVector.size() ; i >= 1; i--)
            if (antsWay[CurrentAnt][i] != null)
            {
                FromPoint = Integer.parseInt(antsWay[CurrentAnt][i]);
                break;
            }
        //Now from number get index;
        int PointCount = MyPointsVector.size();
        if (FromPoint == - 1) MyShow.Error("Error in function getFromPoint()");
        for (int i = 0; i < PointCount; i++)
            if (MyPointsVector.get(i).getPointNumber() == FromPoint)
            return i;
        return - 1;
    }

    private static void MarkAsBadWay(int CurrentAnt)
    {
        String[][] antsWay = OptionsAnts.getAntWays();
        for (int i = 1; i < antsWay[0].length; i++)
            if (antsWay[CurrentAnt][i] == null)
            {
                antsWay[CurrentAnt][i] = "Impasse";
                antsWay[CurrentAnt][0] = "Not found";
                return;
            }
        MyShow.Error("Error in function MarkAsBadWay()");
    }

    private static void MarkWay(int CurrentAnt, int toPointNumber)
    {
        String[][] antsWay = OptionsAnts.getAntWays();
        for (int i = 1; i < antsWay[0].length; i++)
            if (antsWay[CurrentAnt][i] == null)
            {
                antsWay[CurrentAnt][i] = ""+toPointNumber;
                return;
            }
        MyShow.Error("Error in function MarkWay()");
    }
    
    private static boolean ChoiceWay(int CurrentAnt)
    {
        double[][] Chance = OptionsAnts.getChanceByAnt(CurrentAnt);
        double Sum = 0;
        int FromPointIndex = getFromPointIndexInArray(CurrentAnt);  //+
        //Chance to move to next point
        MyPoints fromPointMyPoint = OptionsAnts.getMyPointsVector().get(FromPointIndex);//MyPoints.getMyPointByNumber(FromPointIndex);
        for (int i = 0; i < Chance.length; i++)
            if (TabooList(CurrentAnt, OptionsAnts.getMyPointsVector().get(i), fromPointMyPoint))
                Sum = Sum + ( Math.pow(OptionsAnts.getPheromone()[FromPointIndex][i],  OptionsAnts.antsA) * Math.pow(n[FromPointIndex][i], OptionsAnts.antsB) );
        if (Sum == 0) {
            MarkAsBadWay(CurrentAnt); //-
            return false;
        }

        for (int i = 0; i < Chance.length; i++)
            if (TabooList(CurrentAnt, OptionsAnts.getMyPointsVector().get(i), fromPointMyPoint))
                Chance[FromPointIndex][i] = ( Math.pow(OptionsAnts.getPheromone()[FromPointIndex][i], OptionsAnts.antsA)
                        * Math.pow(n[FromPointIndex][i], OptionsAnts.antsB) ) / Sum;
            else Chance[FromPointIndex][i] = 0;
        //Generate Number (RULETKA)
        double Ruletka = Math.random();
        Sum = 0;
        for (int i = 0; i < Chance.length; i++)
        {
            if (Chance[FromPointIndex] [i] != 0)
            {
                Sum += Chance[FromPointIndex] [i];
                if (Sum >= Ruletka || Sum >= 1)
                {
                    MarkWay(CurrentAnt, OptionsAnts.getMyPointsVector().get(i).getPointNumber());
                    if (MyPoints.EndPoint == OptionsAnts.getMyPointsVector().get(i).getPointNumber())
                        return false;
                    return true;
                }
            }
        }

        MyShow.Error("Error in function ChoiceWay()");
        return true;
    }
}

public class SearchWays extends Thread //START<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
{
    public static final Semaphore _semCalculate = new Semaphore(0, true);
    public static int Iterations , ChanceToMove, MaxDistanceToMove;
    public static boolean Stop;
    private static int _numOfProcessors = 4;

    SearchWays() {for (int i = 0; i < _numOfProcessors; i++) new AntSearch().start();}//Constructor

    public void run()
    {
      MyArrayComparable MyComparable = new MyArrayComparable();
      while (true)
      {
        _numOfProcessors = Runtime.getRuntime().availableProcessors();
        if (_numOfProcessors < 1) _numOfProcessors = 1;
        try
        {  MainPanel.UnlockSearchButtons(); _semCalculate.acquire(); MainPanel.LockSearchButtons(); //System.gc();
            if (!MyPoints.checkStartEndPoints()) {MyShow.Warning("Please set the \"Start\" and \"End\" points"); continue;}
            Stop = false; OptionsAnts.AllowEditing = false; MainPanel.LockSearchOptions();
            //Calculating body
           // OptionsDataPanel.ComboListOfIterations.setVisible(false);
            for (int i = 0; i < Iterations && !Stop; i++)
            {
                Calculate.addIteration();  //Update Pheromone here
                Calculate.MovePoints();
                Calculate.UpdateFoundWays();
                Calculate.goAnts();
                Calculate.AddFoundWays();
                Collections.sort(OptionsAnts.getFoundWays(), MyComparable);
              //  Calculate.UpdatePheromone();
            }
        }
        catch (InterruptedException e) {if (!Main.ItIsApplet) MyShow.Error(e.toString());}
        catch (Exception e) {MyShow.Error(e.toString());}
        catch (OutOfMemoryError e)
        {
            OptionsDataPanel.ComboListOfIterations.removeItemAt(OptionsDataPanel.ComboListOfIterations.getItemCount() - 1);
            OptionsAnts.Iterations.remove(OptionsAnts.Iterations.size() - 1);
            JOptionPane.showMessageDialog(null, OptionsAnts.OutOfMemory, OptionsAnts.Message, JOptionPane.ERROR_MESSAGE);
        }

      }
    }

    public class MyArrayComparable implements Comparator<String[]>
    {
        @Override
        public int compare(String[] strArr1, String[] strArr2)
        {
            double var1 = Double.parseDouble(strArr1[0]);
            double var2 = Double.parseDouble(strArr2[0]);
            return var1>var2 ? 1 : (var1==var2 ? 0 : -1);
        }
    }
}



class Calculate
{
    private static double[][] _pheromone;

    public static void addIteration()
    {
        OptionsAnts.addIteration();
    }

    public static void AddFoundWays()
    {
        String[][] AntsWays = OptionsAnts.getAntWays();
        Vector<String[]> FoundWays = OptionsAnts.getFoundWays();
        for (int i = 0; i < AntsWays.length; i++)
        {
            if (!AntsWays[i][0].equals("Not found") && !WayIsAlreadyAdded(AntsWays[i])) FoundWays.add(AntsWays[i]);
        }
    }

    private static boolean WayIsAlreadyAdded(String[] AntWay)
    {
        Vector<String[]> FoundWays = OptionsAnts.getFoundWays();
        if (FoundWays.size() == 0) return false;
        boolean[] EqualsArray = new boolean[AntWay.length - 1];
        int FoundWaysCount = FoundWays.size();
        for (int i = 0 ; i < FoundWaysCount; i++)
        {
            String[] FoundWay = FoundWays.get(i);
            if (Arrays.equals(FoundWay, AntWay)) return true;
        }
        return false;
    }

    public static void UpdateFoundWays()
    {
        for (int i = 0; i < OptionsAnts.getFoundWays().size(); i++)
        {
            String[] FoundWay = OptionsAnts.getFoundWays().get(i);
            AntSearch.CalculateDistance(FoundWay);
            for (int j = 1; j < FoundWay.length-1; j++)
            {
                MyPoints p1 = MyPoints.getMyPointByNumber(Integer.parseInt(FoundWay[j]));
                MyPoints p2 = MyPoints.getMyPointByNumber(Integer.parseInt(FoundWay[j+1]));
                if (!MyPoints.isInVisibilityRadius(p1, p2))
                {
                    OptionsAnts.getFoundWays().remove(FoundWay);
                    i--;
                    break;
                }
                if (Integer.parseInt(FoundWay[j+1]) == MyPoints.EndPoint) break;
            }
        }
    }

    public static void goAnts()
    {
        AntSearch.OutOfMemoryInThread = null;
        AntSearch._iterationDone = new CountDownLatch(OptionsAnts.AntsAmount);
        AntSearch._AntsCount = 0;
        AntSearch._semAnts.release(OptionsAnts.AntsAmount);
        try{AntSearch._iterationDone.await();} catch (InterruptedException e) {MyShow.Error(e.toString());}
        if (AntSearch.OutOfMemoryInThread != null) {throw AntSearch.OutOfMemoryInThread;}
    }

    public static void MovePoints()
    {   //int chance = 20; int maxDistance = 100;
        int PointCount = OptionsAnts.getMyPointsVector().size();
        if (SearchWays.ChanceToMove > 0) {
            for (int i = 0; i < PointCount; i++)
            { //x = r*cos(a) + u; y = r*sin(a) + v, где r - радиус, a - угол, (u,v) - координаты центра окружности
                int GenChance = (int) (99 * Math.random());
                if (GenChance < SearchWays.ChanceToMove)
                {
                    int GenRadius = (int) ((SearchWays.MaxDistanceToMove-1) * Math.random()) + 1;
                    int GenCorner = (int) (360 * Math.random());
                    int X = (int) (GenRadius * Math.cos(GenCorner)) + OptionsAnts.getMyPointsVector().get(i).x;
                    int Y = (int) (GenRadius * Math.sin(GenCorner)) + OptionsAnts.getMyPointsVector().get(i).y;
                    OptionsAnts.getMyPointsVector().get(i).setLocation(X, Y);
                }
            }

        }
        MainPanel._mainPanel.UpdatePoints();
        //Update n - Visibility
        AntSearch.n = new double[OptionsAnts.getMyPointsVector().size()] [OptionsAnts.getMyPointsVector().size()];
        for (int i = 0; i < AntSearch.n.length; i++)
            for (int j = 0; j < AntSearch.n.length; j++)
                AntSearch.n[i][j] = 1 / OptionsAnts.getMyPointsVector().get(i).distance(OptionsAnts.getMyPointsVector().get(j));
    }

}

class MyShow
{
    public static void Warning(String str) {JOptionPane.showMessageDialog(null,str,"Message",JOptionPane.WARNING_MESSAGE);;}
    public static void Error(String str)
    {JOptionPane.showMessageDialog(null, "An error occurred\nError message: \"" + str + "\"", "Message", JOptionPane.ERROR_MESSAGE);}
}