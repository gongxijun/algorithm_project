import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Vector;

public class MyJMenuBar extends JMenuBar{
    private FilenameFilter fileFilter = new FilenameFilter() {
        @Override
        public boolean accept(File dir, String name) {
            return name.toLowerCase().endsWith(".points");
        }
    };
    
    MyJMenuBar() {
        JMenu menu = new JMenu("File");
        add(menu);

        JMenuItem item = menu.add("Save points"); menu.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    FileDialog fd = new FileDialog(new Frame(), "Save points", FileDialog.SAVE);
                    fd.setVisible(true);
                    if (fd.getFile() == null) return;
                    String directory = fd.getDirectory()+fd.getFile();
                    ObjectOutputStream out;
                    out = new ObjectOutputStream(new FileOutputStream(directory));
                    Vector<MyPoints> myPoints = OptionsAnts.getMyPointsVector();
                    out.writeObject(myPoints);
                    out.close();
                } catch (IOException e1) {
                    MyShow.Error(e1.toString());  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });


        item = menu.add("Load points"); menu.add(item);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!OptionsAnts.AllowEditing) {
                    MyShow.Warning("Please unlock options");
                    return;
                }

                FileDialog fd = new FileDialog(new Frame(), "Load points", FileDialog.LOAD);
                fd.setVisible(true);
                if (fd.getFile() == null) return;
                String directory = fd.getDirectory()+fd.getFile();

                try {
                    ObjectInputStream in;
                    in = new ObjectInputStream(new FileInputStream(directory));
                    Vector<MyPoints> myPoints = (Vector<MyPoints>) in.readObject();
                    in.close();

                    Vector<MyPoints> vec = OptionsAnts.getMyPointsVector();
                    vec.clear();
                    for (int i = 0; i < myPoints.size(); i++)
                        vec.add(myPoints.get(i));
                    MyPoints._PointCount = myPoints.size();
                    OptionsAnts.UpdateDataOptions();




                } catch (Exception e1) {
                    MyShow.Error(e1.toString());  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        });
    }
}
