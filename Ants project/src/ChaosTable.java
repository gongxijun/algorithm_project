//package chaos.table;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chaos
 * Date: 06.01.12
 * Time: 18:27
 * To change this template use File | Settings | File Templates.
 */


public class ChaosTable extends JTable        //http://forum.vingrad.ru/faq/topic-158024.html
{
    private static Color BackgroundColor = Color.WHITE;
    private boolean isFoundTable;
    protected JScrollPane _scrollPane = new JScrollPane(this);
    protected TableRowSorter _sorter;
    protected ChaosDefaultTableModel _model;// = new ChaosDefaultTableModel(getColumnModel());

    //methods
    public ChaosDefaultTableModel getChaosModel() {return _model;}
    public JScrollPane getScrollPane() {return _scrollPane;}

    //CONSTRUCTOR
    public ChaosTable(ChaosDefaultTableModel model, boolean isFoundTable)
    {    //init varibles
        //
        this.isFoundTable = isFoundTable;
        model._tableColumnModel = getColumnModel();
        this.setModel(_model = model);
        getTableHeader().setReorderingAllowed(false);


     //   _sorter = new TableRowSorter(_model);
      //  this.setRowSorter(_sorter);
        setUpdateSelectionOnSort(true);
    //    _sorter.setSortsOnUpdates(true);


        setSelectionMode(0); //disable range select
        //Horizontal scroll
        this.getParent().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                UpdateMySize();
            }
        });
    }

    public void UpdateMySize()
    {
        if (getColumnCount() * _model._columnsMinWidth > getParent().getSize().width)
        {
            setAutoResizeMode(AUTO_RESIZE_OFF);
            for (int i = 0; i < getColumnCount(); i++) getColumnModel().getColumn(i).setPreferredWidth(_model._columnsMinWidth);
        }
        else {setAutoResizeMode(AUTO_RESIZE_ALL_COLUMNS);}
    }

    @Override public boolean isCellEditable(int rowIndex, int colIndex) {return false;}

    @Override
    public void updateUI()
    {
        if (_sorter != null) _sorter.sort();
        super.updateUI();
    }

   // @Override public void updateUI()
  //  {
      //  if (_sorter != null && _rowData.size() != 0 ) _sorter.sort();
       // get
   //     super.updateUI();
  //  }
   @Override public Component prepareRenderer(TableCellRenderer renderer,int rowIndex, int vColIndex) {
       boolean[] ParrallelWays = MainPanel._workSpace.ParrallelWays;
       Component rComp = super.prepareRenderer(renderer, rowIndex, vColIndex);
       if (!isFoundTable || ParrallelWays == null || !OptionsDataPanel.ShowParalell.isSelected() || OptionsAnts.IsCalculating || OptionsAnts.AllowEditing ||
               ParrallelWays.length != OptionsAnts.getFoundWays().size()) { // звичайний колір
           if (rowIndex == this.getSelectedRow()) rComp.setBackground(Color.WHITE.darker());
           else rComp.setBackground(BackgroundColor);
           return rComp;
       }

       if (ParrallelWays[rowIndex]) {   //paralel
           if (this.getSelectedRow() == rowIndex) {// selected
               rComp.setBackground(MainPanel._workSpace._linePointToPointColor);
           }
           else rComp.setBackground(MainPanel._workSpace._linePointToPointColor.darker());
       }
       else { // not paralel
           if (this.getSelectedRow() == rowIndex) {// selected
               rComp.setBackground(MainPanel._workSpace._lineNotParallelPointToPoint);
           }
           else rComp.setBackground(MainPanel._workSpace._lineNotParallelPointToPoint.darker());
       }
       return rComp;
   }             

}




