import javax.swing.*;
import javax.swing.border.*;

import javax.swing.table.*;

import java.awt.*;
import javax.swing.event.*;

import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;

import java.awt.geom.Ellipse2D;

import java.beans.*;

import java.awt.event.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.text.*;
import java.util.Vector;


public class Main extends JApplet
{
    public static boolean ItIsApplet = false;

    public void init()
    {   ItIsApplet = true;
       // Thread.currentThread().setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        Object[] options = {"Run in browser",
                "Cut from browser",};
        int choosed = JOptionPane.showOptionDialog(null,
                "Please choose the running variant of program",
                "Message",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        while (true)
        {
            try
            {
                if (choosed == 0) getContentPane().add(new MainPanel());
                else new AntsMainFrame();
            }
            catch (Exception e)
            {
                continue;
            }
                break;
        }
    }


    public static void main(String[] args)
    {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
        try
        {
            AntsMainFrame MainFrame = new AntsMainFrame();
        }
        catch (Exception e)
        {
            JOptionPane.showMessageDialog(null,"An error occurred when program starting\nError message: \"" +
                e.getMessage() + "\"\nProgram will close","Message",JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }
    }
}

class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    @Override public void uncaughtException(Thread t, Throwable e) {
        MyShow.Error(e.toString());
    }
}

class TabbedPanel extends JTabbedPane
{
    public static ChaosTable _coordinatesTable;
    public static ChaosTable _distanceTable;
    public static ChaosTable _pheromoneTable = new ChaosTable(new ChaosDefaultTableModel(), false);
    public static ChaosTable _chanceTable = new ChaosTable(new ChaosDefaultTableModel(), false);
    public static ChaosTable _foundRoute = new ChaosTable(new ChaosDefaultTableModel(), true);
    public static ChaosTable _antsRoute = new ChaosTable(new ChaosDefaultTableModel(), false);
    public static String FoundRoutes = "Found routes";
    public static String AntRoutes = "Route of ants";
    public static String ChanceTitle = "Chance";
    
    public static void UpdateTables()
    {
        OptionsDataPanel.UpdateColors();
        String[] NullHeaderIteration = {"Point №"};
        //Tables Header Point
        String[] VectorNames = new String[OptionsAnts.getMyPointsVector().size() + 1];
        VectorNames[0] = "Point №";
        for (int i = 1; i < VectorNames.length; i++)
        {
            VectorNames[i] = ""+OptionsAnts.getMyPointsVector().get(i-1).getPointNumber();
        }
        //Distance table
        Vector vector = new Vector();
        vector.add(OptionsAnts.getMyPointsVector());
        _distanceTable.getChaosModel().setColumnIdentifiers(vector);
        _distanceTable.getChaosModel().setColumnCount(OptionsAnts.getMyPointsVector().size() + 1);
        if (OptionsAnts.AllowEditing)
        {
            _pheromoneTable.getChaosModel().setDataVector(null,NullHeaderIteration);
            _chanceTable.getChaosModel().setDataVector(null,NullHeaderIteration);
            //
            //antsRoute
            OptionsDataPanel.AllowSelect = false;
            _antsRoute.getChaosModel().setDataVector(null, new String[]{"Ant №", "Distance"});
            OptionsDataPanel.AllowSelect = true;
            //^^^^
            _foundRoute.getChaosModel().setDataVector(null, new String[] {"№", "Distance"} );
          //  _coordinatesTable.getChaosModel().setDataVector(null, new String[] {"Point №", "X", "Y"} );
        }
        else
        {
            _foundRoute.getChaosModel().setDataVector(getFoundWaysForTable(), getNamesForRouteTable("Way №"));
            if (OptionsAnts.getFoundWays().size() > 0) _foundRoute.setRowSelectionInterval(0,0);
            _antsRoute.getChaosModel().setDataVector(getFormatForAntsRoute(), getNamesForRouteTable("Ant №"));

            if (OptionsDataPanel.ComboListOfAnts.getSelectedIndex() >= 0)
                _antsRoute.setRowSelectionInterval(0,OptionsDataPanel.ComboListOfAnts.getSelectedIndex());
        //    _antsRoute.setRowSelectionInterval(0,0);

            _pheromoneTable.getChaosModel().setDataVector(getPheromoneForTable(),VectorNames); //PheromoneTable
            _chanceTable.getChaosModel().setDataVector(getFormatForTable(OptionsAnts.getChanceByAnt(OptionsDataPanel.ComboListOfAnts.getSelectedIndex())),VectorNames);
        }
        //Coordinate
        _coordinatesTable.setValueAt(OptionsAnts.getMyPointsVector(),1,1);
        //UpdateSize
        _foundRoute.UpdateMySize();
        _antsRoute.UpdateMySize();
        _chanceTable.UpdateMySize();
        _coordinatesTable.UpdateMySize();
        _distanceTable.UpdateMySize();
        _pheromoneTable.UpdateMySize();

    }

    private static String[][] getFoundWaysForTable()
    {
        String[][] data = new String[OptionsAnts.getFoundWays().size()] [OptionsAnts.getMyPointsVector().size() + 2];
        for (int i = 0; i < data.length; i++)
        {
            data[i][0] = ""+(i+1);
            for (int j = 1; j < data[0].length; j++)
            {
                data[i][j] = OptionsAnts.getFoundWays().get(i)[j-1];
            }
        }
        return data;  
    }
    
    private static String[] getNamesForRouteTable(String firstCol)
    {
        String[] VectorNames = new String[OptionsAnts.getMyPointsVector().size() + 2];
        VectorNames[0] = firstCol;
        VectorNames[1] = "Distance";
        for (int i = 2; i < VectorNames.length; i++)
            VectorNames[i] = "Point";
        return VectorNames;
    }

    private static String[][] getFormatForAntsRoute()
    {
        String[][] dataVector = new String[OptionsAnts.AntsAmount][OptionsAnts.getMyPointsVector().size() + 2];
        for (int i = 0; i < dataVector.length; i++)
        {
            dataVector[i][0] = ""+(i+1);
            for (int j = 1; j < dataVector[0].length; j++)
            {
                dataVector[i][j] = OptionsAnts.getAntWays()[i][j-1];
            }
        }
        return dataVector;
    }

    private static Object[][] getFormatForTable(double [][] data)
    {
        Object[][] pheromoneObj = new Object[data.length]  [data.length + 1];
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data.length; j++)
            {
                pheromoneObj[i][j+1] = data[i][j];
                pheromoneObj[i][0] = OptionsAnts.getMyPointsVector().get(i).getPointNumber();
            }
        return pheromoneObj;
    }

    private static Object[][] getPheromoneForTable()
    {
        double[][] pheromone = OptionsAnts.getPheromone();
        Object[][] pheromoneObj = new Object[pheromone.length]  [pheromone.length + 1];
        for (int i = 0; i < pheromone.length; i++)
            for (int j = 0; j < pheromone.length; j++)
            {
                pheromoneObj[i][j+1] = pheromone[i][j];
                pheromoneObj[i][0] = OptionsAnts.getMyPointsVector().get(i).getPointNumber();
            }
        return pheromoneObj;
    }

    TabbedPanel()
    {
        _distanceTable = new ChaosTable(new DistancesChaosTableModel(), false);
        _coordinatesTable = new ChaosTable(new CoordinateChaosTableModel(), false);
        setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setPreferredSize(new Dimension(250,200));
        //Found routes


        addTab(FoundRoutes, _foundRoute.getScrollPane());
        _foundRoute.setRowSorter(null);
        _foundRoute.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                MainPanel._mainPanel._workSpace.repaint();
            }});
        //Route of ants
        addTab(AntRoutes, _antsRoute.getScrollPane());
        _antsRoute.setRowSorter(null);
        _antsRoute.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (OptionsDataPanel.AllowSelect)
                {
                    OptionsDataPanel.AllowSelect = false;
                    if (_antsRoute.getSelectedRow() >= 0) OptionsDataPanel.ComboListOfAnts.setSelectedIndex(_antsRoute.getSelectedRow());
                    String[] VectorNames = new String[OptionsAnts.getMyPointsVector().size() + 1];
                    VectorNames[0] = "Point №";
                    for (int i = 1; i < VectorNames.length; i++)
                    {
                        VectorNames[i] = ""+OptionsAnts.getMyPointsVector().get(i-1).getPointNumber();
                    }
                    _chanceTable.getChaosModel().setDataVector(getFormatForTable(OptionsAnts.getChanceByAnt(OptionsDataPanel.ComboListOfAnts.getSelectedIndex())),VectorNames);
                  //  _antsRoute.getChaosModel().setDataVector(getFormatForAntsRoute(), getNamesForRouteTable("Ant №"));
                  //  _antsRoute.setRowSelectionInterval(0,OptionsDataPanel.ComboListOfAnts.getSelectedIndex());

                    OptionsDataPanel.AllowSelect = true;
                }
                MainPanel._mainPanel._workSpace.repaint();
            }
        });
        //Chance to go
        addTab(ChanceTitle, _chanceTable.getScrollPane());
        _chanceTable.getTableHeader().setReorderingAllowed(true);
        //Pheromone
        addTab("Pheromone", _pheromoneTable.getScrollPane());
        _pheromoneTable.getTableHeader().setReorderingAllowed(true);
        //Distances
        addTab("Distance", _distanceTable.getScrollPane());
     //   _distanceTable.getChaosModel().setColumnCount(1);
        Vector vector = new Vector();
        vector.add(OptionsAnts.getMyPointsVector());
        _distanceTable.getChaosModel().setColumnIdentifiers(vector);
        _distanceTable.getTableHeader().setReorderingAllowed(true);
        //Coordinates
      //  String[] str =  {"Point №", "X", "Y"};
    //    _coordinatesTable.getChaosModel().setColumnIdentifiers( str);
        vector = new Vector();
        vector.add(OptionsAnts.getMyPointsVector());
        _coordinatesTable.getChaosModel().setColumnIdentifiers(vector);
        _coordinatesTable.getChaosModel().setColumnCount(3);
        _coordinatesTable.getTableHeader().setReorderingAllowed(true);
        addTab("Coordinate", _coordinatesTable.getScrollPane());
        this.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent e) {
                MainPanel._mainPanel._workSpace.repaint();
                String SelectedTitle = MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex());
                if (!OptionsAnts.AllowEditing && !OptionsAnts.IsCalculating && (SelectedTitle == ChanceTitle || SelectedTitle == AntRoutes))
                {
                    OptionsDataPanel.AntNumberStrut.setVisible(true);
                    OptionsDataPanel.AntNumber.setVisible(true);
                }
                else
                {
                    OptionsDataPanel.AntNumberStrut.setVisible(false);
                    OptionsDataPanel.AntNumber.setVisible(false);
                }
            }
        });
        
    }

    class CoordinateChaosTableModel extends ChaosDefaultTableModel
    {
        CoordinateChaosTableModel() {super();}

        @Override public int getRowCount()
        {
            if (columnIdentifiers.size() != 0) return ((Vector<MyPoints>)(columnIdentifiers.get(0))).size();
            return 0;
        }
        @Override public Object getValueAt(int row, int col)
        {
         //   if (columnIdentifiers.size() == 0) return null;
            if (((Vector<MyPoints>)columnIdentifiers.get(0)).size() > 0)
            {
                switch (col)
                {
                    case 0: return ((Vector<MyPoints>)columnIdentifiers.get(0)).get(row).getPointNumber();
                    case 1: return ((Vector<MyPoints>)columnIdentifiers.get(0)).get(row).x;
                    case 2: return ((Vector<MyPoints>)columnIdentifiers.get(0)).get(row).y;
                }
            }
            return null;
        }
        @Override public String getColumnName(int col)
        {
            switch (col)
            {
                case 0: return "Point №";
                case 1: return "X";
                case 2: return "Y";
            }
            return null;
        }
        @Override public void setValueAt(Object object, int row, int col)
        {
            columnIdentifiers.set(0,object);
        }

    }

    class DistancesChaosTableModel extends ChaosDefaultTableModel
    {
        DistancesChaosTableModel() {super(); /*_tableColumnModel = model*/}

        @Override public int getRowCount()
        {
            if (columnIdentifiers.size() != 0) return columnIdentifiers.size() - 1;
            return 0;
        }
        @Override public Object getValueAt(int row, int col)
        {
           if (((Vector<MyPoints>)columnIdentifiers.get(0)).size() > 0)
           {
               if (col == 0) return ((Vector<MyPoints>)columnIdentifiers.get(0)).get(row).getPointNumber();
               return ((Vector<MyPoints>)columnIdentifiers.get(0)).get(row).distance(
                    ((Vector<MyPoints>)columnIdentifiers.get(0)).get(col - 1));
           }
           return null;
        }
        @Override public String getColumnName(int col)
        {
            if (col == 0) return "Point №";
            return ""+((Vector<MyPoints>)columnIdentifiers.get(0)).get(col - 1).getPointNumber();//""+((MyPoints)(columnIdentifiers.get(col-1))).getPointNumber();
        }
    }

    class CoordinatesTable extends JTable
    {
        private ArrayList<String> _colNames;
        private ArrayList<String> _data;

        CoordinatesTable()
        {
            _colNames = new ArrayList<String>();
            _data = new ArrayList<String>();
            _colNames.add("Hello");
            _colNames.add("GG");
            _data.add("check");
            _data.add("check");

            this.setModel(new MyTable3xModel());
            setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        }

        class MyTable3xModel extends AbstractTableModel
        {
            @Override public int getRowCount() {return _data.size();}
            @Override public int getColumnCount() {return _colNames.size(); }
            @Override public Object getValueAt(int rowIndex, int columnIndex) {return _data.size();}
        }
    }
}

class ChaosJToolBar extends JToolBar //Resizable toolbar
{
   // private int test = 0;

    ChaosJToolBar(String Title, boolean Horizontal, boolean Resizable)
    {
        super(Title, Horizontal ? 0 : 1);
      //  this.
        if (Resizable)
        {
            Dimension Size = getSize();
            this.addHierarchyListener(new HierarchyListener()
            {   @Override
                public void hierarchyChanged(HierarchyEvent e)
                {
                    Window window = SwingUtilities.getWindowAncestor(e.getComponent());
                    if (window instanceof Dialog && !((Dialog) window).isResizable())
                    {
                        final Dialog dialog = (Dialog) window;
                        dialog.setResizable(true);
                        //set size like in drop in one time
                      //  getRootPane().setWindowDecorationStyle(JRootPane.FILE_CHOOSER_DIALOG);
                        dialog.setPreferredSize(new Dimension(getSize().width+dialog.getSize().width,
                                getSize().height+dialog.getSize().height));


                        dialog.addComponentListener(new ComponentAdapter() {
                            public void componentResized(ComponentEvent e) {
                                if (dialog != null)
                                dialog.setPreferredSize(dialog.getSize());
                                //       setPreferredSize(dialog.getComponent(0).getSize());
                                // setPreferredSize(dialog.getComponent(0).getMinimumSize());
                            }
                        });
                        removeHierarchyListener(this); //<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
                    }
                    else
                    {
                     /*   if (test > 1 )
                        {
                            //;
                    //       removeHierarchyListener(this);
                          //  setPreferredSize(getMaximumSize());
                        }
                        else test++;     */
                    }
                }
            });
        }
    }
}

class ChaosJPanelWithScroll extends JPanel   //tables
{
    private int _scrollIncrement;
    private JScrollPane _scrollPane;

    ChaosJPanelWithScroll(int ScrollsIncrement)
    {
      //  _scrollPane = new JScrollPane(this);
        _scrollPane = new JScrollPane();
        _scrollPane.setViewportView(this);
        setScrollsIncrement(ScrollsIncrement);
    }

    public JScrollPane getScrollPane() {return _scrollPane;}
    private void setScrollsIncrement(int Increment)
    {

        _scrollIncrement = Increment;
        _scrollPane.getHorizontalScrollBar().setUnitIncrement(_scrollIncrement);
        _scrollPane.getVerticalScrollBar().setUnitIncrement(_scrollIncrement);
    }
}


class AntsMainFrame extends JFrame
{
  //  public static AntsMainFrame MainFrame;
    AntsMainFrame()
    {
     //   MainFrame = this;
        if (!Main.ItIsApplet) setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      //  Dimension ScreenSize=Toolkit.getDefaultToolkit().getScreenSize();
        setTitle("Ants v1.1");

     //   setBounds((int) (ScreenSize.width * 0.10), (int) (ScreenSize.height * 0.10), (int) (ScreenSize.width * 0.80), (int) (ScreenSize.height * 0.80));
        add(new MainPanel());
        setJMenuBar(new MyJMenuBar());

        setVisible(true);
        pack();
     //   setSize(getSize().width+20, getSize().height+100);
        setLocationRelativeTo(null);
        MainPanel._setFillWorkSpace.doClick();
    }
}







class MainPanel extends JPanel
{
    public static SearchOptions MySearchOptions;
    public static MainPanel _mainPanel;
    public final static int ScrollIncrement = 30;
    public static int Strut = 4;
    public int _currentX;
    public int _currentY;
    public static JFormattedTextField _antsA;
    public static JFormattedTextField _antsB;
    public static JFormattedTextField _iterations;
    public static JFormattedTextField _antsRadiusVisibility; //= new JFormattedTextField(NumberFormat.INTEGER_FIELD);
    public static JFormattedTextField _ants;
    public static JFormattedTextField _eliteAnts;
    public static JFormattedTextField _pointChanceToMove;
    public static JFormattedTextField _pointMaxDistanceToMove;

    private static JButton _setWorkSpace;
    public static JButton _setFillWorkSpace;
    private static JButton _newSearch;
    private static JButton _unlockSearch;
   // private static JButton _continueSearch;
    private static JButton _nextStepSearch;
    private static JButton _deletePoints;
    private static JButton _stopSearch;

    public static MyWorkJPanel _workSpace;
    private String _message;
    // int getPointCount() {return _PointCount;}/**/void setPointCount(int PointCount) {_PointCount = PointCount;}
    private boolean[] KeyIsDown;
   // public static CurrentDataPanel _currentSettingPanel;
    public TabbedPanel _tables;

    public static void LockSearchOptions()
    {
        _antsA.setEnabled(false);
        _antsB.setEnabled(false);
        _antsRadiusVisibility.setEnabled(false);
        _ants.setEnabled(false);
        _eliteAnts.setEnabled(false);
        _pointChanceToMove.setEnabled(false);
        _pointMaxDistanceToMove.setEnabled(false);


        
        System.gc();
    }

    public static void UnlockSearchOptions()
    {
        try
        {
            _antsA.setEnabled(true);
            _antsB.setEnabled(true);
          //  _iterations.setEnabled(true);
            _antsRadiusVisibility.setEnabled(true);
            _ants.setEnabled(true);
            _eliteAnts.setEnabled(true);
            _pointChanceToMove.setEnabled(true);
            _pointMaxDistanceToMove.setEnabled(true);
            OptionsAnts.AllowEditing = true;
            System.gc();
        }
        catch (NullPointerException e) {}
    }

    public static void LockSearchButtons()
    {
        _stopSearch.setEnabled(true);
        _setWorkSpace.setEnabled(false);
        _newSearch.setEnabled(false);
        _unlockSearch.setEnabled(false);
        _nextStepSearch.setEnabled(false);
        _deletePoints.setEnabled(false);
        _setFillWorkSpace.setEnabled(false);

        OptionsDataPanel.IterationNumber.setVisible(false);
        OptionsDataPanel.AntNumberStrut.setVisible(false);
        OptionsDataPanel.AntNumber.setVisible(false);
        OptionsAnts.IsCalculating = true;
        System.gc();
    }
    public static void UnlockSearchButtons()
    {
        try
        {
            _stopSearch.setEnabled(false);
            _setWorkSpace.setEnabled(true);
            _newSearch.setEnabled(true);
            _unlockSearch.setEnabled(true);
            _nextStepSearch.setEnabled(true);
            _deletePoints.setEnabled(true);
            _setFillWorkSpace.setEnabled(true);

            OptionsDataPanel.IterationNumber.setVisible(true);

           // OptionsDataPanel.ComboListOfAnts.setVisible(true);

            String SelectedTitle = MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex());
            
            if (SelectedTitle == MainPanel._mainPanel._tables.ChanceTitle || SelectedTitle == MainPanel._mainPanel._tables.AntRoutes)
            {
                OptionsDataPanel.AntNumberStrut.setVisible(true);
                OptionsDataPanel.AntNumber.setVisible(true);
            }
            else
            {
                OptionsDataPanel.AntNumberStrut.setVisible(false);
                OptionsDataPanel.AntNumber.setVisible(false);
            }

        //   try{//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<< Problem
            int Index = OptionsDataPanel.ComboListOfIterations.getItemCount() - 1;
                if (Index >= 0 )
                    OptionsDataPanel.ComboListOfIterations.setSelectedIndex(Index);
            OptionsAnts.IsCalculating = false;
            MainPanel._mainPanel._workSpace.repaint();
         //   } catch (ArrayIndexOutOfBoundsException e) {}
            System.gc();
        }
        catch (NullPointerException e) {}

    }

    MainPanel()
    {
        _mainPanel=this;
     //   _currentOptionsData = new MyAntsOptions();
        OptionsAnts.ClearResults();


        KeyIsDown = new boolean[255];
            for (int i = 0; i < KeyIsDown.length; i++)
                KeyIsDown[i] = false;
   //     _myPointsArray = new Vector<MyPoints>();
        setLayout(new BorderLayout()); _message = "Message";
        _workSpace = new MyWorkJPanel();
        _antsB = new JFormattedTextField(DecimalFormat.getNumberInstance()); _antsB.setValue(0);
        CheckFormatedField_Listener Check = new CheckFormatedField_Listener(0);
        _antsB.addPropertyChangeListener(Check);
        _pointChanceToMove = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _pointChanceToMove.setValue(20);
        _pointChanceToMove.addPropertyChangeListener(Check);

        _antsA = new JFormattedTextField(DecimalFormat.getNumberInstance()); _antsA.setValue(1);
        _antsA.addPropertyChangeListener(Check);


        _antsRadiusVisibility = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _antsRadiusVisibility.setValue(100);
        Check = new CheckFormatedField_Listener(50);
        _antsRadiusVisibility.addPropertyChangeListener(Check);
        _iterations = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _iterations.setValue(100);
        Check = new CheckFormatedField_Listener(1);
        _iterations.addPropertyChangeListener(Check);
        _ants = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _ants.setValue(30);
        _ants.addPropertyChangeListener(Check);
        _eliteAnts = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _eliteAnts.setValue(4);
        _eliteAnts.addPropertyChangeListener(Check);
        _pointMaxDistanceToMove = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _pointMaxDistanceToMove.setValue(30);
        _pointMaxDistanceToMove.addPropertyChangeListener(Check);


        _mainPanel.add(new MySettingFlow(),BorderLayout.WEST);



        _mainPanel.add(new MyWorkPanel());
        _mainPanel.add(new MyToolBar(),BorderLayout.NORTH);
        //Data tables
        ChaosJToolBar bar = new ChaosJToolBar("Data tables", true, true);
        bar.add(_tables = new TabbedPanel());
        _mainPanel.add(bar,BorderLayout.SOUTH);
       // Integer.parseInt("" + _antsRadiusVisibility.getValue());
    }



    class CheckFormatedField_Listener implements PropertyChangeListener
    {
        private int _minValue;
        private int _currentValue;
        CheckFormatedField_Listener(int MinValue)
        {
            _currentValue = _minValue = MinValue;
        }
        public void propertyChange(PropertyChangeEvent e)
        {
            JFormattedTextField fTextField = ((JFormattedTextField)e.getSource());
            //AntsA and AntsB
            if (e.getSource() == _antsA || e.getSource() == _antsB)
            {
                OptionsAnts.UpdateDataOptions();
                return;
            }
            //Other

            try
            {
                if (Integer.parseInt(""+fTextField.getValue()) < _minValue)
                    fTextField.setValue(_minValue);
                else _currentValue = Integer.parseInt("" + fTextField.getValue());
            }
            catch (NumberFormatException exception)
            {
                fTextField.setValue(_currentValue);
            }
            //set value
            JFormattedTextField field = (JFormattedTextField) e.getSource();
            MainPanel panel = MainPanel._mainPanel;
         //   if (field == panel._antsA) ;//_currentOptionsData.antsA =Integer.parseInt (""+field.getValue());
            OptionsAnts.UpdateDataOptions();
            if (e.getSource() == _ants)
            {
                OptionsDataPanel.ComboListOfAnts.removeAllItems();
                for (int i = 0; i < OptionsAnts.AntsAmount; i++)
                    OptionsDataPanel.ComboListOfAnts.addItem(""+(i+1));
            }
        }
    }

    class MyToolBar extends JToolBar
    {
        private ArrayList<JButton> _buttons;
        private Dimension _separatorSize;
        private Border _separatorBorder;

        void AddMySeparator()
        {
            addSeparator(_separatorSize);
            JButton Button = new JButton();
            Button.setEnabled(false);
            Button.setBorder(_separatorBorder);
            Button.setBackground(Color.GRAY);
            add(Button);
            addSeparator(_separatorSize);
            _buttons.add(Button);
        }

        private void StartCalculate(int Iterations)
        {
            SearchWays.Iterations = Iterations;
            SearchWays.ChanceToMove = OptionsAnts.PointChanceToMove;
            SearchWays.MaxDistanceToMove = OptionsAnts.PointMaxDistanceToMove;
            SearchWays._semCalculate.release();
        }

        MyToolBar()
        {
            new SearchWays().start();
           // JOptionPane.showMessageDialog(null,getBorder().toString());
            setBorder(new CompoundBorder(getBorder(), new BevelBorder(BevelBorder.RAISED)));
            _separatorSize = new Dimension(3,3); _separatorBorder = new EmptyBorder(0,0,0,0); _buttons = new ArrayList<JButton>();
            setName("The control panel");

            _newSearch = addButton("images/start_search.png","Start search based on previous results",null); add(_newSearch);
            _newSearch.addActionListener(new ActionListener()
            {
                @Override public void actionPerformed(ActionEvent e)
                {
                 //   OptionsAnts.ClearResults();
                    StartCalculate(Integer.parseInt(_mainPanel._iterations.getValue().toString()));
                }
            });
           /* _continueSearch = addButton("images/continue_search.png","Continue search based on previous results",null); add(_continueSearch);
            _continueSearch.addActionListener(new ActionListener() {
                @Override public void actionPerformed(ActionEvent e)
                {
                    StartCalculate(Integer.parseInt(_mainPanel._iterations.getValue().toString()));
                }});   */
            _nextStepSearch = addButton("images/next_step.png","Next step (One iteration) based on previous results",null); add(_nextStepSearch);
            _nextStepSearch.addActionListener(new ActionListener()
            {   @Override
                public void actionPerformed(ActionEvent e)
                {
                    StartCalculate(1);
                }
            });
            _stopSearch = addButton("images/stop.png","Stop current searching",null); add(_stopSearch);
            _stopSearch.setEnabled(false);
            _stopSearch.addActionListener(new ActionListener()
            {
                @Override public void actionPerformed(ActionEvent e) {SearchWays.Stop = true;}
            });
            AddMySeparator();

            _deletePoints = addButton("images/delete_points.png","Delete all points. Current results will be lost",null); add(_deletePoints);
            _deletePoints.addActionListener(new ActionListener()
            {   @Override
                public void actionPerformed(ActionEvent e)
                {
                    OptionsAnts.NullIterationMyPoints.clear();
                    OptionsAnts.ClearResults();
                    MyPoints._PointCount = 0;

                }
            });
            _unlockSearch = addButton("images/unlock.png", "Unlock options (without delete points). Current results will be lost", null); add(_unlockSearch);
            _unlockSearch.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    OptionsAnts.NullIterationMyPoints = OptionsAnts.getMyPointsVector();
                    OptionsAnts.ClearResults();

                }
            });

            addPropertyChangeListener(new PropertyChangeListener() {
                private Dimension _horizontal;
                private Dimension _vertical;

                {
                    _horizontal = new Dimension(2, 32);
                    _vertical = new Dimension(32, 2);
                }

                public void propertyChange(PropertyChangeEvent event) {
                    if (getOrientation() == 0)
                        for (int i = 0; i < _buttons.size(); i++) {
                            _buttons.get(i).setMaximumSize(_horizontal);
                            _buttons.get(i).setMinimumSize(_horizontal);
                            _buttons.get(i).setPreferredSize(_horizontal);
                        }
                    else
                        for (int i = 0; i < _buttons.size(); i++) {
                            _buttons.get(i).setMaximumSize(_vertical);
                            _buttons.get(i).setMinimumSize(_vertical);
                            _buttons.get(i).setPreferredSize(_vertical);
                        }
                }
            });
        }

        //Method to add Buttons on MyToolBar
        JButton addButton(String Path, String Hint, Action action)
        {
            JButton Button = new JButton(new ImageIcon(AntsMainFrame.class.getResource(Path)));
            Button.setToolTipText(Hint);
            Button.setBorder(new CompoundBorder(new EmptyBorder(3,3,3,3), new BevelBorder(BevelBorder.RAISED)));
            Button.setMaximumSize(new Dimension(34,34));
            return Button;
        }



    }

    class MySettingFlow extends JToolBar
    {
        MySettingFlow()
        {
            setName("Setting");
            setBorder(new CompoundBorder(getBorder(), new EmptyBorder(0,-1,-3,-2)));
         //   setBorder(new CompoundBorder(getBorder(), new BevelBorder(BevelBorder.RAISED)));
            setOrientation(1);
            add(new MyScroll(new MySettingJPanel()));
        }
    }

    class MyScroll extends JScrollPane //Controll panel
    {
        MyScroll(JPanel panel)  //Scroll
        {

           //   setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
            //MyShow.Warning(""+this.getVerticalScrollBar().getWidth());

            getVerticalScrollBar().setUnitIncrement(ScrollIncrement);
            getHorizontalScrollBar().setUnitIncrement(ScrollIncrement);
            setViewportView(panel);

            Dimension size = MainPanel.MySearchOptions.getPreferredSize();
            size.width += 20;
            size.height = getPreferredSize().height;
            setPreferredSize(size);
        }
    }


        class MySettingJPanel extends JPanel
        {

            MySettingJPanel() //Panel
            {
                setLayout(new BorderLayout());
                Box vBox = Box.createVerticalBox(); add(vBox,BorderLayout.NORTH);
                vBox.add(MySearchOptions = new SearchOptions());
                vBox.add(new WorkSize());

                vBox.add(new OptionsDataPanel());
                OptionsAnts.UpdateDataOptions();
            }
        }
    


        class WorkSize extends JPanel
        {
            private JFormattedTextField _xTextField, _yTextField;
            private JLabel _currentFieldSize = new JLabel();
            WorkSize()
            {
                setLayout(new BorderLayout());
                setBorder(new CompoundBorder(new TitledBorder("The workspace size"), new BevelBorder(BevelBorder.RAISED)));
              //  setBorder(new TitledBorder("The workspace size"));

                Box vBox = Box.createVerticalBox(); add(vBox);
              //  Box hBox = Box.createHorizontalBox();// hBox.add(_currentFieldSize);
                vBox.add(_currentFieldSize);_currentFieldSize.setAlignmentX(CENTER_ALIGNMENT);
                vBox.add(Box.createVerticalStrut(Strut));
                Box hBox = Box.createHorizontalBox(); vBox.add(hBox);
                // X and Y
                hBox.add(new JLabel("X")); hBox.add(Box.createHorizontalStrut(Strut));
                _xTextField = new JFormattedTextField(NumberFormat.INTEGER_FIELD); _xTextField.setValue(800); hBox.add(_xTextField);
                CheckFormatedField_Listener Check= new CheckFormatedField_Listener(100);
                _xTextField.addPropertyChangeListener(Check);
                hBox.add(Box.createHorizontalStrut(Strut));
                hBox.add(new JLabel("Y")); hBox.add(Box.createHorizontalStrut(Strut));
                _yTextField = new JFormattedTextField(NumberFormat.INTEGER_FIELD);_yTextField.setValue(400); hBox.add(_yTextField);
                _yTextField.addPropertyChangeListener(Check);
                //Button Set
                _setWorkSpace = new JButton("Set");
                _setWorkSpace.setToolTipText("Sets new workspace size. Current results will be lost!");
                _setWorkSpace.addActionListener(new ActionListener()
                {   @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        if (!_workSpace.getPreferredSize().equals(new Dimension(Integer.parseInt(""+_xTextField.getValue()),Integer.parseInt(""+_yTextField.getValue()))))
                        {
                            _workSpace.setPreferredSize(new Dimension(Integer.parseInt(""+_xTextField.getValue()),Integer.parseInt(""+_yTextField.getValue())));
                          //  Vector<Vector> iteration
                            _currentX = Integer.parseInt(""+_xTextField.getValue());
                            _currentY = Integer.parseInt(""+_yTextField.getValue());
                            _currentFieldSize.setText("Current: X = "+_currentX+" Y = "+_currentY);
                            OptionsAnts.ClearResults();
                              //  _myPointsArray.clear();
                            OptionsAnts.getMyPointsVector().clear();
                            UpdatePoints();
                            _workSpace.updateUI();
                            OptionsAnts.ClearResults();
                            MyPoints._PointCount = 0;
                         //   OptionsAnts.UpdateDataOptions();
                        }
                    }
                });
                hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(_setWorkSpace);
                vBox.add(Box.createVerticalStrut(Strut));
                JPanel panel = new JPanel(new BorderLayout()); panel.add(_setFillWorkSpace = new JButton("Fill the free space"));
                vBox.add(panel);
                _setFillWorkSpace.addActionListener(new ActionListener()
                {
                    @Override public void actionPerformed(ActionEvent e)
                    {
                     //   _workSpace.getParent().getParent().
                        Dimension size = _workSpace.getParent().getParent().getParent().getSize();
                        size.width += -3;
                        size.height += -3;
                        if (size.width < 100) size.width = 100;
                        if (size.height < 100) size.height = 100;
                        _workSpace.setPreferredSize(new Dimension(size.width, size.height)); _workSpace.updateUI();

                        _currentX = Integer.parseInt(""+size.width);
                        _currentY = Integer.parseInt(""+size.height);
                        _currentFieldSize.setText("Current: X = "+_currentX+" Y = "+_currentY);
                        OptionsAnts.ClearResults();
                        OptionsAnts.getMyPointsVector().clear();
                        UpdatePoints();
                        _workSpace.updateUI();
                        OptionsAnts.ClearResults();
                        MyPoints._PointCount = 0;
                     //   OptionsAnts.UpdateDataOptions();
                    }
                });

                _setFillWorkSpace.setToolTipText("Sets the new workspace size, by filling the free space. Current results will be lost!");

                _currentX = Integer.parseInt(""+_xTextField.getValue());
                _currentY = Integer.parseInt(""+_yTextField.getValue());
                _currentFieldSize.setText("Current: X = "+_currentX+" Y = "+_currentY);

            }
        }

        class SearchOptions extends JPanel
        {
            SearchOptions()
            {
                setLayout(new BorderLayout());
                Box hBox; JLabel Label;
                String str = "When a = 0 is chosen nearest point that correspond to the greedy algorithm in classical optimization theory";
               // setBorder(new TitledBorder("Search options"));
                setBorder(new CompoundBorder(new TitledBorder("Search options"), new BevelBorder(BevelBorder.RAISED)));
                Box vBox = Box.createVerticalBox(); add(vBox);
                hBox = Box.createHorizontalBox(); vBox.add(hBox);
                hBox.add(new JLabel("Radius of ants visibility"));
                hBox.add(Box.createHorizontalStrut(Strut));
                hBox.add(_antsRadiusVisibility);
                vBox.add(hBox);
                vBox.add(Box.createVerticalStrut(Strut));
               // vBox.add(_antsRadiusVisibility);
                _antsRadiusVisibility.setPreferredSize(new Dimension(50,_antsRadiusVisibility.getPreferredSize().height));
                // a, b
                vBox.add(Box.createVerticalStrut(Strut));
                hBox = Box.createHorizontalBox(); vBox.add(hBox);
                Label = new JLabel("a");
                Label.setToolTipText(str); _antsA.setToolTipText(str);
                hBox.add(Label); hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(_antsA);
                Label = new JLabel("b");
                str = "When b = 0 choice is based only on pheromone that leading to suboptimal solutions";
                Label.setToolTipText(str); hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(Label);
                _antsB.setToolTipText(str); hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(_antsB);

                vBox.add(Box.createVerticalStrut(Strut));
                hBox = Box.createHorizontalBox(); vBox.add(hBox);
                vBox.add(Box.createVerticalStrut(Strut));
                // iterations
                hBox.add(new JLabel("Number of iterations")); hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(_iterations);
                //Ants+EliteAnts
                hBox = Box.createHorizontalBox(); vBox.add(hBox);
                Label = new JLabel("Ants"); str="Number of ants"; Label.setToolTipText(str); _ants.setToolTipText(str);
                hBox.add(Label); hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(_ants);
                //EliteAnts
                hBox.add(Box.createHorizontalStrut(Strut)); str = "Weight of the optimal path";
                Label = new JLabel("Elite ants"); Label.setToolTipText(str); _eliteAnts.setToolTipText(str);
                hBox.add(Label);  hBox.add(Box.createHorizontalStrut(Strut)); hBox.add(_eliteAnts);
                //Chance to move
                vBox.add(Box.createVerticalStrut(Strut));
                hBox = Box.createHorizontalBox(); vBox.add(hBox);
                str = "Chance that a point will change its location (for next iterations and for each point)";
                Label = new JLabel("Chance to move"); Label.setToolTipText(str);
                hBox.add(Label); hBox.add(Box.createHorizontalStrut(Strut));
                hBox.add(_pointChanceToMove); _pointChanceToMove.setToolTipText(str);
                //Maximum distance to move
                vBox.add(Box.createVerticalStrut(Strut));
                hBox = Box.createHorizontalBox(); vBox.add(hBox);
                str = "The maximum distance on which, point will move in the range from 1 to this value (the distance is chosen randomly from this range)";
                Label = new JLabel("Max distance to move"); Label.setToolTipText(str);
                hBox.add(Label); hBox.add(Box.createHorizontalStrut(Strut));
                hBox.add(_pointMaxDistanceToMove); _pointMaxDistanceToMove.setToolTipText(str);
            }
        }
        


    //Робоча панель
    class MyWorkPanel extends JScrollPane //Work panel
    {
        MyWorkPanel()  //Scroll
        {
            getVerticalScrollBar().setUnitIncrement(ScrollIncrement);
            getHorizontalScrollBar().setUnitIncrement(ScrollIncrement);
            JPanel space = new JPanel(new FlowLayout(FlowLayout.LEFT));
            space.setBorder(new EmptyBorder(-5,-5,-5,-5));
            _workSpace.setBorder(new BevelBorder(BevelBorder.RAISED));
            setViewportView(space);
            space.add(_workSpace);
            _workSpace.setPreferredSize(new Dimension(800,400));
        }
    }
    //workspace
    class MyWorkJPanel extends JPanel
    {
        private Graphics2D _g2;
        private Color _startPointColor = Color.ORANGE;
        private Color _endPointColor = Color.GREEN.darker();
        private Color _visibilityRadiusColor;
        private Color _contourPointsColor;
        private Color _pointsColor;
        private Color _fontColor;
        public boolean[] ParrallelWays;
        public Color _linePointToPointColor = Color.GREEN;
        public Color _lineNotParallelPointToPoint = Color.YELLOW;
        private Font _font;

        private int _pointsVisualRadius;
        private Font _fontChance;
        private double _pointsVisualInside;
        private Ellipse2D _ellipse;
        private MyPoints _currentDragedPoint;
        private MyPoints _currentPointUnderCursor;
        private Stroke _stroke;
        private MyPoints _popUpPoint;
        private JPopupMenu _popUp = new JPopupMenu();

        MyWorkJPanel()
        {
            //initialize variables

            _visibilityRadiusColor = Color.BLUE;
            _contourPointsColor = Color.BLACK;
            _pointsColor = Color.RED;
            _pointsVisualRadius = 18;
            _pointsVisualInside = _pointsVisualRadius * 0.75;
            _fontColor = Color.WHITE;
            final String str = "Current results will be lost";
            setBackground(Color.GRAY);
            _popUp.add(new AbstractAction() {
                {putValue(Action.NAME,"Set as start point"); putValue(Action.SHORT_DESCRIPTION,str);}
                @Override public void actionPerformed(ActionEvent e)
                {MyPoints.StartPoint = _popUpPoint.getPointNumber();}
            });
            _popUp.addSeparator();
            _popUp.add(new AbstractAction() {
                {putValue(Action.NAME,"Set as end point"); putValue(Action.SHORT_DESCRIPTION,str);}
                @Override public void actionPerformed(ActionEvent e)
                {MyPoints.EndPoint = _popUpPoint.getPointNumber();}
            });
            _fontChance = new Font(null, 0, _pointsVisualRadius );
           _font = new Font(null, 0, (int)(_pointsVisualRadius * 1.2));
            _ellipse = new Ellipse2D.Double();
            this.addMouseMotionListener(new MouseMotionListener()
            {
                @Override
                public void mouseDragged(MouseEvent e)
                {
                    if (!OptionsAnts.AllowEditing) return;
                    if (_currentDragedPoint != null)
                    {
                        _currentDragedPoint.setLocation(e.getPoint());
                        if (_currentPointUnderCursor == null)
                        {
                            _currentPointUnderCursor = _currentDragedPoint;
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                        UpdatePoints();
                        return;
                    }
                    _currentDragedPoint = getPointUnderCursor(e.getPoint());
                    if (_currentDragedPoint != null)
                    {
                        _currentDragedPoint.setLocation(e.getPoint());
                        if (_currentPointUnderCursor == null)
                        {
                            _currentPointUnderCursor = _currentDragedPoint;
                            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                        UpdatePoints();
                    }
                }
                @Override public void mouseMoved(MouseEvent e)
                {   if(OptionsAnts.IsCalculating) return;
                    MyPoints point = getPointUnderCursor(e.getPoint());
                    if (point != null)
                    {
                        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                        _currentPointUnderCursor = point; //paint radius visibility

                        UpdatePoints();
                        return;
                    }
                    setCursor(Cursor.getDefaultCursor());
                    _currentPointUnderCursor = null; //stop paint visibility radius
                    repaint();
                   // UpdatePoints();
                }
            } );
            this.addMouseListener(new MouseAdapter()
            {
                public void mousePressed(MouseEvent e)
                {   if (!OptionsAnts.AllowEditing) return;
                    requestFocus();
                  //  if (OptionsDataPanel.ComboListOfIterations.getSelectedIndex() != 0) return;
                    // add point
                    if (KeyIsDown[KeyEvent.VK_CONTROL] && e.getButton() == MouseEvent.BUTTON1)
                    {
                        OptionsAnts.getMyPointsVector().add(new MyPoints(e.getPoint()));
                      //  OptionsAnts.Iteration.get(1).add(new MyPoints(e.getPoint()));
                    }
                    // delete point
                    if (KeyIsDown[KeyEvent.VK_CONTROL] && e.getButton() == MouseEvent.BUTTON3)
                    {
                        MyPoints point = getPointUnderCursor(e.getPoint());
                        if (_currentPointUnderCursor == point)//disable handcursor and radius visibility
                        {
                            _currentPointUnderCursor = null;
                            setCursor(Cursor.getDefaultCursor());
                        }
                        if (point != null)
                        {
                            OptionsAnts.getMyPointsVector().remove(point);
                            if (point.getPointNumber() == MyPoints.EndPoint) MyPoints.EndPoint = 0;
                            if (point.getPointNumber() == MyPoints.StartPoint) MyPoints.StartPoint = 0;
                        }
                                
                    }
                    OptionsDataPanel.setPointsCount(OptionsAnts.getMyPointsVector().size());
                    _tables._distanceTable.getChaosModel().setColumnCount(OptionsAnts.getMyPointsVector().size() + 1);
                    UpdatePoints();
                }

                public void mouseReleased(MouseEvent e)
                {
                    if (!OptionsAnts.AllowEditing) return;
                    _currentDragedPoint = null;//drag untill release
                    if (e.getX() > _currentX || e.getY() > _currentY || e.getY() < 0 || e.getX() < 0)
                    {
                        _currentPointUnderCursor = null;
                        UpdatePoints();
                    }
                    if ((_popUpPoint = getPointUnderCursor(e.getPoint())) != null && e.isPopupTrigger())
                    {
                        _popUp.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
            //keyboard listener
            this.addKeyListener(new KeyAdapter()
            {
                @Override
                public void keyPressed(KeyEvent e)
                {
                    KeyIsDown[e.getKeyCode()] = true;
                }
                @Override
                public void keyReleased(KeyEvent e)
                {
                    KeyIsDown[e.getKeyCode()] = false;
                }
            });
        }

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            _g2 = (Graphics2D) g;
            _g2.setFont(_font);

            if (OptionsAnts.getMyPointsVector().size() == 0)
            {
                _g2.setColor(Color.WHITE);
                int x, y; String str;
                Point p = new Point(_currentX / 2, _currentY / 2);
                str = "Please, press";
                _g2.drawString(str,p.x - _g2.getFontMetrics().stringWidth(str) / 2, (p.y + _g2.getFontMetrics().getHeight() / 4) - _g2.getFontMetrics().getHeight());
                str = "\"Ctrl+Left button\" on the mouse to add point";
                _g2.drawString(str,p.x - _g2.getFontMetrics().stringWidth(str) / 2, p.y + _g2.getFontMetrics().getHeight() / 4);
                str = "or \"Ctrl+Right button\" to delete point under cursor";
                _g2.drawString(str,p.x - _g2.getFontMetrics().stringWidth(str) / 2, (p.y + _g2.getFontMetrics().getHeight() / 4) + _g2.getFontMetrics().getHeight());
            }

            _stroke = _g2.getStroke();
            PaintPoints();
        }

        private void PaintWay(String[] FoundRoute, Color color)
        {
            _g2.setColor(color);
            int[] ArrayX = new int[FoundRoute.length-1];
            int[] ArrayY = new int[ArrayX.length];

            int i = 0;
            for (; i < ArrayX.length; i++)
            {
                if (FoundRoute[i+1] == null || FoundRoute[i+1].equals("Impasse")) break;
                MyPoints point = MyPoints.getMyPointByNumber(Integer.parseInt(FoundRoute[i+1]));
                ArrayX[i] = point.x;
                ArrayY[i] = point.y;

            }
            _g2.drawPolyline(ArrayX, ArrayY, i);
        }

        public void PaintPoints()
        {

            //paint way
            if (!OptionsAnts.IsCalculating) 
            {
                _g2.setStroke(new BasicStroke(3));

                try{//<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<

                    if (MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex()) == TabbedPanel.FoundRoutes
                        && TabbedPanel._foundRoute.getRowCount() > 0 && TabbedPanel._foundRoute.getSelectedRow() >= 0)
                    {  // Found Ways
                        // Parallel ways
                        if (OptionsDataPanel.ShowParalell.isSelected() && !OptionsAnts.IsCalculating && !OptionsAnts.AllowEditing)
                        {
                            // printing
                            Vector<String[]> Ways = OptionsAnts.getFoundWays();
                            int WaysSize = Ways.size();
                            for (int i = 0; i < WaysSize; i++) 
                                if (ParrallelWays[i]) PaintWay(Ways.get(i),_linePointToPointColor.darker());
                            int index = TabbedPanel._foundRoute.getSelectedRow();
                            if (index >= 0)
                                if (ParrallelWays[index]) PaintWay(Ways.get(index),_linePointToPointColor);
                                else PaintWay(Ways.get(index),_lineNotParallelPointToPoint);
                        }
                        // selected                                                                             
                        else PaintWay(OptionsAnts.getFoundWays().get(TabbedPanel._foundRoute.getSelectedRow()), _linePointToPointColor);
                    }
                    else if ((MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex()) == TabbedPanel.AntRoutes
                            || MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex()) == TabbedPanel.ChanceTitle)
                            && TabbedPanel._antsRoute.getRowCount() > 0 && TabbedPanel._antsRoute.getSelectedRow() >= 0)
                    {   // Ant route
                        PaintWay(OptionsAnts.getAntWays()[TabbedPanel._antsRoute.getSelectedRow()],_linePointToPointColor);
                    }

                }
                catch (ArrayIndexOutOfBoundsException e) {};
                _g2.setStroke(_stroke);
            }
            Ellipse2D ellipse = new Ellipse2D.Double();

            //paint Points (courner, inside, number)
            for (int i = 0; i < OptionsAnts.getMyPointsVector().size(); i++)
            {
                MyPoints p = OptionsAnts.getMyPointsVector().get(i);

              //  java.awt.geom.Ellipse2D ellipse = new Ellipse2D.Double();
                //courner color
                ellipse.setFrameFromDiagonal(p.x - _pointsVisualRadius, p.y - _pointsVisualRadius, p.x + _pointsVisualRadius,p.y + _pointsVisualRadius);
                _g2.setColor(_contourPointsColor);
                _g2.fill(ellipse);
                //inside color
                _g2.setColor(_pointsColor); // point inside color
                if (MyPoints.EndPoint == p.getPointNumber()) _g2.setColor(_endPointColor);
                if (MyPoints.StartPoint == p.getPointNumber()) _g2.setColor(_startPointColor);

                if (_currentPointUnderCursor != null) //inside color that in visibility radius
                {
                    if (MyPoints.isInVisibilityRadius(_currentPointUnderCursor,p))
                        _g2.setColor(_visibilityRadiusColor); //visibility radius color
                }
                ellipse.setFrameFromDiagonal(p.x - _pointsVisualInside, p.y - _pointsVisualInside, p.x + _pointsVisualInside,p.y + _pointsVisualInside);
                _g2.fill(ellipse);
                //write the number of point;
                _g2.setColor(_fontColor);
                String Number = ""+p.getPointNumber();
                _g2.drawString(Number,p.x - _g2.getFontMetrics().stringWidth(Number) / 2, p.y + _g2.getFontMetrics().getHeight() / 4);


            }
            //Current iteration
            if (OptionsAnts.IsCalculating)_g2.drawString(""+(OptionsDataPanel.ComboListOfIterations.getItemCount()-1),5,20);
            //radius visibility
            if (_currentPointUnderCursor != null)
            {
                _g2.setStroke(new BasicStroke(3));
                _g2.setColor(_visibilityRadiusColor);
                int vRadius = OptionsAnts.VisibilityRadius;
                ellipse.setFrameFromDiagonal(_currentPointUnderCursor.x - vRadius, _currentPointUnderCursor.y - vRadius,
                        _currentPointUnderCursor.x + vRadius, _currentPointUnderCursor.y + vRadius);
                _g2.draw(ellipse);
                //Paint chances
                _g2.setStroke(_stroke);
                if (OptionsDataPanel.ComboListOfIterations.getSelectedIndex() >= 0 && OptionsDataPanel.ComboListOfAnts.getSelectedIndex() >=0 &&
                        (MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex()) == TabbedPanel.AntRoutes
                        || MainPanel._mainPanel._tables.getTitleAt(MainPanel._mainPanel._tables.getSelectedIndex()) == TabbedPanel.ChanceTitle))
                {
                    double[][] Chances = OptionsAnts.getChanceByAnt(OptionsDataPanel.ComboListOfAnts.getSelectedIndex());
                    _g2.setStroke(_stroke);
                    _g2.setColor(_fontColor);
                    _g2.setFont(_fontChance);
                    int FromIndex = OptionsAnts.getMyPointsVector().indexOf(_currentPointUnderCursor);
                    for (int i = 0; i < Chances.length ; i++)
                    //    for (int j = 0; j < Chances.length; j++)
                    {
                        MyPoints point = OptionsAnts.getMyPointsVector().get(i);
                        //  _g2.drawString(""+(Chances[FromIndex][i] * 100), point.x, point.y);
                        String str = new BigDecimal(Chances[FromIndex][i] * 100).setScale(0, BigDecimal.ROUND_UP)+"%";
                        _g2.drawString(str,
                                point.x - _g2.getFontMetrics().stringWidth(str) / 2, point.y - _pointsVisualRadius);
                    }
                }
                _g2.setStroke(_stroke);
            }

        }

        public MyPoints getPointUnderCursor(Point point)
        {
            if (point == null) return null;
            for (int i = 0; i < OptionsAnts.getMyPointsVector().size(); i++)
            {
                Point p = OptionsAnts.getMyPointsVector().get(i).getLocation();
                _ellipse.setFrameFromDiagonal(p.x - _pointsVisualRadius, p.y - _pointsVisualRadius, p.x + _pointsVisualRadius,p.y + _pointsVisualRadius);
                if (_ellipse.contains(point))
                    return OptionsAnts.getMyPointsVector().get(i);
            }
            return null;
        }
    }

    public void UpdatePoints()
    {
        _workSpace.repaint();
        if (!OptionsAnts.IsCalculating)
        {
            _tables._distanceTable.updateUI();
            _tables._coordinatesTable.updateUI();
        }
//        _tables._coordinatesTable.updateUI();
      //  MyPoints.UpdateDistances();
    }
    
}

class MyPoints extends Point implements Cloneable, Serializable
{
    private int _pointNumber;
    public static int StartPoint;
    public static int EndPoint;
    public static int _PointCount = 0;

    public static int getMyPointIndexInArrayByNumber(int number)
    {
        for (int i = 0; i < OptionsAnts.getMyPointsVector().size(); i++)
            if (OptionsAnts.getMyPointsVector().get(i).getPointNumber() == number) return i;
        return 0;
    }
    
    public static MyPoints getMyPointByNumber(int number)  //-
    {
        for (int i = 0; i < OptionsAnts.getMyPointsVector().size(); i++)
        {
            MyPoints myPoint = OptionsAnts.getMyPointsVector().get(i);
            if (myPoint.getPointNumber() == number) return myPoint;
        }
        MyShow.Error("Error in function MyPoints getMyPointByNumber");
        return null;
    }
    //PHEROMONE

    public static boolean isInVisibilityRadius(Point p1, Point p2)
    {
        Ellipse2D ellipse = new Ellipse2D.Double();
        ellipse.setFrameFromDiagonal(p1.x - OptionsAnts.VisibilityRadius, p1.y - OptionsAnts.VisibilityRadius,
                p1.x + OptionsAnts.VisibilityRadius, p1.y + OptionsAnts.VisibilityRadius);
        if (ellipse.contains(p2)) return true;
        return false;
    }

    //^^^^^^^^^^^^^^^^^^^^^^^^^^^
    public int getPointNumber(){return _pointNumber;}

    public MyPoints clone()
    {
        try{MyPoints cloned = (MyPoints) super.clone(); return cloned;}
        catch (Exception e) {MyShow.Error(e.toString());}
        return null;
    }

    public static boolean checkStartEndPoints()
    {
        boolean start = false, end = false;
        for (int i = 0; i < OptionsAnts.getMyPointsVector().size(); i++)
        {
           MyPoints point = OptionsAnts.getMyPointsVector().get(i);
           if (point.getPointNumber() == StartPoint) start = true;
           if (point.getPointNumber() == EndPoint) end = true;
        }
        if (StartPoint != EndPoint && start && end) return true;
        return false;
    }

    MyPoints(int x, int y)
    {
        super(x, y);
        this._pointNumber = ++_PointCount;
    }
    MyPoints(Point p) {this(p.x, p.y);}

    public void setLocation(int X, int Y) {this.setLocation(new Point(X,Y));}
    public void setLocation(Point p)
    {
        if (p.x > MainPanel._mainPanel._currentX) p.x = MainPanel._mainPanel._currentX;
        if (p.y > MainPanel._mainPanel._currentY) p.y = MainPanel._mainPanel._currentY;
        if (p.x < 0) p.x = 0;
        if (p.y < 0) p.y = 0;
        x = p.x;
        y = p.y;
    }
}



class OptionsAnts
{
    public static final String OutOfMemory = "An error occurred\nError message: java.lang.OutOfMemoryError: Java heap space\nNow program will run very slowly\nTo free memory clean the current results";
    public static final String Message = "Message";
    public static boolean AllowEditing = true;
    public static boolean IsCalculating = false;

    public static int AntsAmount;
    public static int EliteAnts;
    public static int VisibilityRadius;
    public static double antsA;
    public static double antsB;
    public static int PointMaxDistanceToMove;
    public static int PointChanceToMove;
    public static Vector<MyPoints> NullIterationMyPoints = new Vector<MyPoints>();
    public static Vector<Vector> Iterations = new Vector<Vector>(); //public Vector<MyPoints> _myPointsArray;

    private Vector<Vector<Double>> _pheromone = new Vector<Vector<Double>>();

    public static Vector<MyPoints> getMyPointsVector()
    {
        if (AllowEditing || OptionsDataPanel.ComboListOfIterations.getItemCount() == 0) return NullIterationMyPoints;
        if (!IsCalculating) return (Vector<MyPoints>) Iterations.get(OptionsDataPanel.ComboListOfIterations.getSelectedIndex()).get(0);
        else return (Vector<MyPoints>) Iterations.get(OptionsDataPanel.ComboListOfIterations.getItemCount() - 1).get(0);
    }

    public static double[][]getPheromone()
    {
      //  if (AllowEditing || OptionsDataPanel.ComboListOfIterations.getItemCount() == 0) return null;
        if (!IsCalculating) return (double[][]) Iterations.get(OptionsDataPanel.ComboListOfIterations.getSelectedIndex()).get(1);
        else return (double[][]) Iterations.get(OptionsDataPanel.ComboListOfIterations.getItemCount() - 1).get(1);
    }
    
    public static double[][] getChanceByAnt(int AntNumber)
    {
        if (!IsCalculating) return (double[][])
                ((Vector) (Iterations.get(OptionsDataPanel.ComboListOfIterations.getSelectedIndex()).get(2))).get(AntNumber);

        else return (double[][])
                ((Vector) (Iterations.get(OptionsDataPanel.ComboListOfIterations.getItemCount() - 1).get(2))).get(AntNumber);
    }

    public static String[][] getAntWays()
    {
        if (!IsCalculating) return (String[][]) Iterations.get(OptionsDataPanel.ComboListOfIterations.getSelectedIndex()).get(3);
        else return (String[][]) Iterations.get(OptionsDataPanel.ComboListOfIterations.getItemCount() - 1).get(3);
    }

    public static Vector<String[]> getFoundWays()
    {
        if (!IsCalculating) return (Vector<String[]>) Iterations.get(OptionsDataPanel.ComboListOfIterations.getSelectedIndex()).get(4);
        else return (Vector<String[]>) Iterations.get(OptionsDataPanel.ComboListOfIterations.getItemCount() - 1).get(4);
    }
  //---------------------------------------------------------------------------------------------------
    private static double[][] FirstIterationPheromone()
    {
        double[][] Pheromone = new double[getMyPointsVector().size()]  [getMyPointsVector().size()];
        for (int i = 0; i < Pheromone.length; i++)
            for (int j = 0; j < Pheromone.length; j++)
                Pheromone[i][j] = 1;
        return Pheromone;
    }

    private static double[][] NextIterationsPheromone()
    {
      //  int FoundWaysCount = OptionsAnts.getFoundWays();
        Vector<String[]> FoundWays = OptionsAnts.getFoundWays();
        if (FoundWays.size() == 0) return FirstIterationPheromone();
        int Q = 10;

        double[][] Pheromone = new double[getMyPointsVector().size()]  [getMyPointsVector().size()];
        for (int row = FoundWays.size() - 1; row >= 0; row--)
        {
            for (int i = 1; i < Pheromone.length; i++)
            {
                if (OptionsAnts.getFoundWays().get(row)[i+1] == null) break;
                double Distance = Double.parseDouble( FoundWays.get(row)[0] );
                int FromPointIndex = MyPoints.getMyPointIndexInArrayByNumber(Integer.parseInt( OptionsAnts.getFoundWays().get(row)[i] ));
                int ToPointIndex = MyPoints.getMyPointIndexInArrayByNumber(Integer.parseInt( OptionsAnts.getFoundWays().get(row)[i+1] ));
                if (row == 0)
                {
                    Pheromone[FromPointIndex][ToPointIndex] = OptionsAnts.EliteAnts * Q / Distance;
                    Pheromone[ToPointIndex][FromPointIndex] = Pheromone[FromPointIndex][ToPointIndex];
                }
                else
                {
                    Pheromone[FromPointIndex][ToPointIndex] = Q / Distance;
                    Pheromone[ToPointIndex][FromPointIndex] = Pheromone[FromPointIndex][ToPointIndex];
                }
            }
        }
        double Distance = Double.parseDouble( FoundWays.get(FoundWays.size() - 1)[0] );
        for (int i = 0; i < Pheromone.length; i++)
            for (int j = 0; j < Pheromone.length; j ++)
            {
                if (Pheromone[i][j] == 0 && i != j &&
                        MyPoints.isInVisibilityRadius(OptionsAnts.getMyPointsVector().get(i), OptionsAnts.getMyPointsVector().get(j)) )
                {
                    Pheromone[i][j] = (Q / Distance) / 1.5;
                    Pheromone[j][i] = Pheromone[i][j];
                }
            }

        return Pheromone;
    }
    //^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^

    public static void addIteration()
    {
        //Points
        Vector vector = new Vector();
        vector.add(ClonePreviousPoints());   // ClonePreviousPoints()
        //Pheromone
        if (Iterations.size() == 0) vector.add(FirstIterationPheromone());
        else vector.add(NextIterationsPheromone());
        //Chance
        Vector vec2 = new Vector();
        for (int i = 0; i < OptionsAnts.AntsAmount; i++)
            vec2.add(new double[OptionsAnts.getMyPointsVector().size()] [OptionsAnts.getMyPointsVector().size()]);
        vector.add(vec2);
        //Ways
        String[][] AntWays = new String[OptionsAnts.AntsAmount] [OptionsAnts.getMyPointsVector().size() + 1];
        for (int i = 0; i < OptionsAnts.AntsAmount; i++)
        {
            //AntWays[i][0] = ""+(i+1);
            AntWays[i][1] = "" + MyPoints.StartPoint;
        }
        vector.add(AntWays);
        //Found Ways
     //   if
        if (Iterations.size() == 0) vector.add(new Vector<String[]>());
        else
        {
            Vector<String[]> ClonedVector = new Vector<String[]>();
            Vector<String[]> PreviousVector = getFoundWays();
            int PreviousVectorCount = PreviousVector.size();
            for (int i = 0; i < PreviousVectorCount; i++)
            {
                ClonedVector.add(PreviousVector.get(i).clone());
            }
          //  vector.add(getFoundWays().clone());
            vector.add(ClonedVector);
        }


    /*-----------------------------------------------------------------------------------------------------------------*/
    /**/  Iterations.add(vector);                                                                     /**/
    /**/  OptionsDataPanel.ComboListOfIterations.addItem(""+(OptionsDataPanel.ComboListOfIterations.getItemCount() + 1));  /**/
    /*-----------------------------------------------------------------------------------------------------------------*/
    }

      private static Vector<MyPoints> ClonePreviousPoints()//clone for next iteration
      {
          int PointCount = getMyPointsVector().size();
          Vector<MyPoints> clonedVector = new Vector<MyPoints>();
          for (int i = 0; i < PointCount; i++)
              clonedVector.add(getMyPointsVector().get(i).clone());
          return clonedVector;
      }

    public static void ClearResults()
    {
       // OptionsDataPanel.AllowSelect = false;
        OptionsDataPanel.ComboListOfIterations.removeAllItems();
        Iterations.clear();
        MainPanel.UnlockSearchOptions();
        //Distance Table
        if (TabbedPanel._distanceTable != null)
        {
            Vector vector = new Vector();
            vector.add(OptionsAnts.NullIterationMyPoints);
            TabbedPanel._distanceTable.getChaosModel().setColumnIdentifiers(vector);
            OptionsAnts.UpdateDataOptions();
        }
        try{
            OptionsDataPanel.IterationNumber.setVisible(false);
            OptionsDataPanel.AntNumberStrut.setVisible(false);
            OptionsDataPanel.AntNumber.setVisible(false);
        } catch (NullPointerException e) {}
        System.gc();
    }



    public static void UpdateDataOptions()
    {
        try
        {   //int item = OptionsDataPanel.ComboListOfIterations.getSelectedIndex();
            OptionsAnts.antsA = Double.parseDouble(""+MainPanel._antsA.getValue());
            OptionsAnts.antsB = Double.parseDouble(""+MainPanel._antsB.getValue());
            OptionsAnts.AntsAmount = Integer.parseInt(""+MainPanel._ants.getValue());
            OptionsAnts.EliteAnts = Integer.parseInt(""+MainPanel._eliteAnts.getValue());
            OptionsAnts.VisibilityRadius = Integer.parseInt(""+MainPanel._antsRadiusVisibility.getValue());
            OptionsAnts.PointChanceToMove = Integer.parseInt(""+MainPanel._pointChanceToMove.getValue());
            OptionsAnts.PointMaxDistanceToMove = Integer.parseInt(""+MainPanel._pointMaxDistanceToMove.getValue());

            //Update Panel
            OptionsDataPanel.setPointsCount(OptionsAnts.getMyPointsVector().size());

            MainPanel._mainPanel.updateUI();
            TabbedPanel.UpdateTables();
        }
        catch (Exception e) {}
    }
}

class OptionsDataPanel extends JPanel
{
    public static Box IterationNumber;
    public static Component AntNumberStrut;
    public static Box AntNumber;
    public static JCheckBox ShowParalell = new JCheckBox("Show parallel ways");
    private static JLabel LabelPointCount_;
    private static JLabel LabelAntNumber = new JLabel("Ant №");
    private static JLabel LabelIterationNumber = new JLabel("Iteration №");

    public static JComboBox<String> ComboListOfIterations = new JComboBox<String>();
    public static JComboBox<String> ComboListOfAnts = new JComboBox<String>();
    public static boolean AllowSelect = true;

    public static void UpdateColors() {
        Vector<String[]> Ways = OptionsAnts.getFoundWays();
        int WaysSize = Ways.size();
        boolean[] list = new boolean[OptionsAnts.getMyPointsVector().size()];
        boolean[] ParrallelWays = new boolean[WaysSize]; for (int i=0;i<ParrallelWays.length;i++) ParrallelWays[i] = true;
        MainPanel._workSpace.ParrallelWays = ParrallelWays;
        for (int i = 0; i < list.length; i++) list[i] = false;

        breakL:
        for (int j = 0; j < WaysSize; j++) {
            String[] Way = Ways.get(j);
            for (int i = 2; i < list.length; i++) {
                if (Way[i] == null || Integer.parseInt(Way[i]) == MyPoints.EndPoint) break;
                int PointNumber = Integer.parseInt(Way[i]);
                int PointIndex = MyPoints.getMyPointIndexInArrayByNumber(PointNumber);
                if (list[PointIndex]) {
                    ParrallelWays[j] = false;
                    continue breakL;
                }
                list[PointIndex] = true;
            }

        }
    }

    OptionsDataPanel()
    {
        ComboListOfIterations.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (OptionsAnts.IsCalculating || ComboListOfIterations.getItemCount() < 1) return;

                TabbedPanel.UpdateTables();
                MainPanel._mainPanel.UpdatePoints();
            }
        });
        ComboListOfAnts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (OptionsAnts.IsCalculating || ComboListOfIterations.getItemCount() < 1) return;
                if (AllowSelect)
                {
                    AllowSelect = false;
                    TabbedPanel.UpdateTables();


                    TabbedPanel._antsRoute.getSelectionModel().setSelectionInterval(0, ComboListOfAnts.getSelectedIndex());
                    AllowSelect = true;
                }
            }
        });
        setLayout(new BorderLayout());
        setBorder(new CompoundBorder(new TitledBorder("Results"), new BevelBorder(BevelBorder.RAISED)));
        Box vBox = Box.createVerticalBox(); add(vBox);
        //Iteration №
        Box hBox = Box.createHorizontalBox(); vBox.add(hBox);
        hBox.setAlignmentX(Container.LEFT_ALIGNMENT);
        hBox.add(LabelIterationNumber);
        hBox.add(Box.createHorizontalStrut(MainPanel.Strut));
        hBox.add(ComboListOfIterations); //ComboListOfIterations.setAlignmentX(Container.LEFT_ALIGNMENT);
        IterationNumber = hBox;
        IterationNumber.setVisible(false);
        AntNumberStrut = Box.createVerticalStrut(MainPanel.Strut);
        vBox.add(AntNumberStrut);
        AntNumberStrut.setVisible(false);
        //Ant №

        hBox = Box.createHorizontalBox(); vBox.add(hBox);
        hBox.setAlignmentX(Container.LEFT_ALIGNMENT);
        hBox.add(LabelAntNumber);
        hBox.add(Box.createHorizontalStrut(MainPanel.Strut));
        hBox.add(ComboListOfAnts);// ComboListOfAnts.setAlignmentX(Container.LEFT_ALIGNMENT);
        AntNumber = hBox;
        AntNumber.setVisible(false);

        vBox.add(ShowParalell);
        ShowParalell.setToolTipText("Shows the shortest parallel ways from table \"Found routes\"");
        ShowParalell.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                MainPanel._mainPanel.UpdatePoints();
                TabbedPanel._foundRoute.updateUI();
            }
        });

        vBox.add(LabelPointCount_ = new JLabel("Number of points = 0"));
    }

    public static void setPointsCount(int Points) {LabelPointCount_.setText("Number of points = "+Points);}
}
