package music;

import graphics.G;
import reaction.Mass;

import java.awt.*;
import java.util.ArrayList;


// sys is a list of staff
public class Sys extends Mass {
    public Page page;
    public int iSys;
    public Staff.List staffs;

    public Sys(Page page, G.HC sysTop) {
        super("BACK");
        this.page = page;
        iSys = page.sysList.size();
        staffs = new Staff.List(sysTop);
        if (iSys == 0) {
            staffs.add(new Staff(this, 0, new G.HC(sysTop, 0)));
        } else {
            Sys oldSys = page.sysList.get(0);
            for (Staff oldStaff : oldSys.staffs) {
                Staff ns = oldStaff.copy(this);
                this.staffs.add(ns);
            }

        }
    }

    public int yTop() {return staffs.sysTop();}

    public int yBot() {return staffs.getLast().yBot();}

    public int height() {return yBot() - yTop();}

    public void addNewStaff(int y) {
        int off = y - staffs.sysTop(); // offset
        G.HC staffTop = new G.HC(staffs.sysTop, off);
        staffs.add(new Staff(this, staffs.size(), staffTop));

    }

    public void show(Graphics g) {
        int x = page.margins.left;
        g.drawLine(x, yTop(), x, yBot());

        page.show(g);

    }

    //-----------------  -List-------------------
    public static class List extends ArrayList<Sys> {

    }


}
