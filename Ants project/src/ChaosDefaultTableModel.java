//package chaos.table;

//import javax.swing.table.DefaultTableModel;
//import javax.swing.table.TableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.Vector;

/**
 * Created by IntelliJ IDEA.
 * User: Chaos
 * Date: 08.01.12
 * Time: 0:01
 * To change this template use File | Settings | File Templates.
 */
public class ChaosDefaultTableModel extends DefaultTableModel
{   protected int _columnsMinWidth = 20;
    protected TableColumnModel _tableColumnModel;


    public int getChaosColumnsMinWidth() {return _columnsMinWidth;}
    public void setChaosColumnsMinWidth(int minColumnsWidth)
    {
        try{
            _columnsMinWidth = minColumnsWidth;
            for (int i = 0; i < getColumnCount(); i++) _tableColumnModel.getColumn(i).setMinWidth(_columnsMinWidth);
        }
        catch (ArrayIndexOutOfBoundsException e) {};

      //  this.g
    }

    public ChaosDefaultTableModel()
    {super(new String[][]{{}},new String[]{}); }//_tableColumnModel = model;}

    @Override public void setColumnIdentifiers(Vector columnIdentifiers)
    {super.setColumnIdentifiers(columnIdentifiers); setChaosColumnsMinWidth(_columnsMinWidth);}
    @Override public void setColumnIdentifiers(Object[] NewIdentifiers)
    {super.setColumnIdentifiers(NewIdentifiers); setChaosColumnsMinWidth(_columnsMinWidth);}
    @Override public void setDataVector(Object[][] dataVector, Object[] columnIdentifiers)
    {super.setDataVector(dataVector, columnIdentifiers); setChaosColumnsMinWidth(_columnsMinWidth);}
    @Override public void setDataVector(Vector dataVector,Vector columnIdentifiers)
    {super.setDataVector(dataVector, columnIdentifiers); setChaosColumnsMinWidth(_columnsMinWidth);}
    @Override public void setNumRows(int rowCount)
    {super.setNumRows(rowCount); setChaosColumnsMinWidth(_columnsMinWidth);}
    @Override public void setRowCount(int rowCount)
    {super.setRowCount(rowCount); setChaosColumnsMinWidth(_columnsMinWidth);}
    @Override public void setColumnCount(int columnCount)
    {super.setColumnCount(columnCount); setChaosColumnsMinWidth(_columnsMinWidth);}



    @Override public Class getColumnClass(int col) {
        if (getValueAt(0, col) != null) return getValueAt(0, col).getClass();
        return Object.class;
    }
    //  @Override public int getRowCount() {return 4;}
    /*  @Override public int getColumnCount() {return _colNames.size();}
@Override public boolean isCellEditable(int row, int col) {return false;}

@Override public void setValueAt(Object object, int row, int col) {_rowData.get(row).set(col, object);}
@Override public Object getValueAt(int row, int col)
{
    if (_colNames.size() > col && _rowData.size() > row) return _rowData.get(row).get(col);
    return null;
}
@Override public String getColumnName(int col)
{
    if (_colNames.size() > col) return _colNames.get(col);
    return null;
}    */
}
