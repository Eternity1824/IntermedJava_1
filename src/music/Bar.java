package music;

import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

import java.awt.*;

public class Bar extends Mass {
    /*
    0 : single
    1 : double
    2 : fine
    if either right or left, it repeats
    */
    private static final int FAT = 0x2, RIGHT = 0x4, LEFT = 0x8;
    public Sys sys;
    public int x, barType = 0;

    public Bar(Sys sys, int x) {
        super("BACK");
        int RIGHt = sys.page.margins.right;

        this.sys = sys;
        this.x = x;
        if (Math.abs(RIGHt - x) < UC.barToMarginSnap) {
            this.x = RIGHt;
        }

        addReaction(new Reaction("S-S") { // cycle this bar
            @Override
            public int bid(Gesture g) {
                int x = g.vs.xM();
                if (Math.abs(x - Bar.this.x) > UC.barToMarginSnap) {return UC.noBid;}

                int y1 = g.vs.yL(), y2 = g.vs.yH();
                if (y1 < Bar.this.sys.yTop() - 20 || y2 > Bar.this.sys.yBot() + 20) {return UC.noBid;}
                return Math.abs(x - Bar.this.x);
            }
            @Override
            public void act(Gesture g) {
                Bar.this.cycleType();
            }
        });

        addReaction(new Reaction("DOT") { // toggle repeat
            public int bid(Gesture g) {
                int x = g.vs.xM(), y = g.vs.yM();
                if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) {return UC.noBid;}
                int dist = Math.abs(x - Bar.this.x);
                if (dist > 3*sys.page.maxH) {return UC.noBid;}
                return dist;
            }

            public void act(Gesture g) {
                if (g.vs.xM() < Bar.this.x) {Bar.this.toggleLeft();} else {Bar.this.toggleRight();}
            }
        });
    }

    public void cycleType() {barType++; if (barType > 2) {barType = 0;}}

    public void toggleLeft() {barType = barType^LEFT;}

    public void toggleRight() {barType = barType^RIGHT;}

    public void show(Graphics g) {
//        g.setColor(barType == 1 ? Color.red : Color.black);
//        for (Staff staff : sys.staffs) {
//            g.drawLine(x, staff.yTop(), x, staff.yBot());
//        }
        int sysTop = sys.yTop(), y1 = 0, y2 = 0;
        boolean justSawBreak = true;
        for (int i = 0; i < sys.staffs.size(); i++) {
            Staff staff = sys.staffs.get(i);
            int staffTop = staff.yTop();
            if (justSawBreak) {y1 = staffTop;}
            y2 = staff.yBot();
            justSawBreak = !staff.fmt.barContinues;
            if (justSawBreak) {drawLines(g, x, y1, y2);}
            if (barType > 3) {drawDots(g, x, staffTop);}
        }
    }

    public void drawLines(Graphics g, int x, int y1, int y2) {
        int H = sys.page.maxH;
        if (barType == 0) {thinBar(g, x, y1, y2);}
        if (barType == 1) {thinBar(g, x, y1, y2); thinBar(g, x - H, y1, y2);}
        if (barType == 2) {fatBar(g, x - H, y1, y2, H); thinBar(g, x - 2*H, y1, y2);}
        if (barType >= 4) {
            fatBar(g, x - H, y1, y2, H);
            if ((barType & LEFT) != 0) {thinBar(g, x - 2*H, y1, y2); wings(g, x - 2*H, y1, y2, -H, H);}
            if ((barType & RIGHT) != 0) {thinBar(g, x + H, y1, y2); wings(g, x + H, y1, y2, H, H);}
        }
    }

    public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
        g.drawLine(x, y1, x + dx, y1 - dy);
        g.drawLine(x, y2, x + dx, y2 + dy);
    }

    public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {g.fillRect(x, y1, dx, y2 - y1);}

    public static void thinBar(Graphics g, int x, int y1, int y2) {g.drawLine(x, y1, x, y2);}

    public void drawDots(Graphics g, int x, int top) {
        int H = sys.page.maxH;
        if ((barType & LEFT) != 0) {
            g.fillOval(x - 3*H, top + 11*H/4, H/2, H/2);
            g.fillOval(x - 3*H, top + 19*H/4, H/2, H/2);
        }
        if ((barType & RIGHT) != 0) {
            g.fillOval(x + 3*H/2, top + 11*H/4, H/2, H/2);
            g.fillOval(x + 3*H/2, top + 19*H/4, H/2, H/2);
        }
    }
}
