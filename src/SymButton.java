import java.awt.*;
import java.awt.event.*;

// SymButton
// This is a kind button with cube symmetry picture on it.
// Button can remain depressed or not, depending on setPressed(boolean) state
// When clicked, an ActionEvent is passed to single ActionListener, with
//    name 's' if shift key pressed during click or '' otherwise.
// If disabled then no longer a button, just the picture.

public final class SymButton extends Canvas
    implements MouseListener
{
    Image offImage;
    Graphics offGraphics;
    ActionListener main;
    boolean pressed;
    int centx,centy,width,height;
    int vec[][];
    int type;
    Color colors[]={
        new Color(  0,  0,  0), //drawing colour
        new Color( 64, 64, 64), //shadowed edge outside
        new Color(128,128,128), //shadowed edge inside
        new Color(224,224,224), //highlighted edge inside
        new Color(255,255,255), //highlighted edge outside
        new Color(255,  0,  0), //m colour
        new Color(255,128,128), //mc colour
        new Color(255,  0,255), //r2 colour
        new Color(  0,255,255), //r3 colour
        new Color(  0,  0,255), //r4 colour
    };

    public SymButton(ActionListener m,int t){
        main=m;
        pressed=false;
        type=t;
        addMouseListener(this);
    }
    public boolean isPressed(){return(pressed);}
    public void setPressed(boolean p){
        pressed=p;
        repaint();
    }
    void setType( int t){
        type=checkSym(t);
        repaint();
    }
    void initialise()
    {
        width=getSize().width;
        height=getSize().height;

        offImage = createImage(width, height ); // Double buffer
        offGraphics = offImage.getGraphics();

        centx=(width-1)/2;
        centy=(height-1)/2;
        int rad = (int)((centx-1)/.866);
        if( rad>centy-1 ) rad=centy-1;

        vec=new int[][]{
            {               0, rad  },
            {(int)(-rad*.866),-rad/2},
            {(int)( rad*.866),-rad/2},
            {               0, rad  },
            {(int)(-rad*.866),-rad/2},
            {(int)( rad*.866),-rad/2}
        };
    }

    public void paint( Graphics g )
    {
        int i;
        if( offImage == null ){ initialise(); }

        if( this.isEnabled()){
           // draw button
           // top left edges
           offGraphics.setColor(colors[pressed?1:4]);
           offGraphics.fillRect(0,0,width,height);
           // bot right edges
           offGraphics.setColor(colors[pressed?4:1]);
           offGraphics.fillRect(1,1,width-1,height-1);
           // top left edges
           offGraphics.setColor(colors[pressed?2:3]);
           offGraphics.fillRect(1,1,width-2,height-2);
           // bot right edges
           offGraphics.setColor(colors[pressed?3:2]);
           offGraphics.fillRect(2,2,width-3,height-3);
           // background
           offGraphics.setColor(getBackground());
           offGraphics.fillRect(2,2,width-4,height-4);
       }else{
           // background
           offGraphics.setColor(getBackground());
           offGraphics.fillRect(0,0,width,height);
        }

        int m=1;

        //centre symmetry
        if( (type&m) !=0 ){
            offGraphics.setColor(colors[6]);
            int r=vec[0][1]/3;
            offGraphics.fillOval(centx-r,centy-r,r+r,r+r);
        }
        m<<=1;

        //me symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[5]);
                offGraphics.drawPolyline(
                    new int[]{centx+vec[i+1][0]+vec[i+2][0],
                              centx,
                              centx+vec[i][0]},
                    new int[]{centy+vec[i+1][1]+vec[i+2][1],
                              centy,
                              centy+vec[i][1]},
                    3);
            }
            m<<=1;
        }

        //me symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[5]);
                offGraphics.drawPolyline(
                    new int[]{centx+vec[i  ][0]+vec[i+1][0],
                              centx+vec[i+1][0],
                              centx+vec[i+2][0],
                              centx+vec[i+2][0]+vec[i  ][0]},
                    new int[]{centy+vec[i  ][1]+vec[i+1][1],
                              centy+vec[i+1][1],
                              centy+vec[i+2][1],
                              centy+vec[i+2][1]+vec[i  ][1]},
                    4);
            }
            m<<=1;
        }

        //mf symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[5]);
                offGraphics.drawPolyline(
                    new int[]{centx+vec[i+1][0]+vec[i  ][0]/2,
                              centx+vec[i  ][0]/2,
                              centx+vec[i+2][0]+vec[i  ][0]/2},
                    new int[]{centy+vec[i+1][1]+vec[i  ][1]/2,
                              centy+vec[i  ][1]/2,
                              centy+vec[i+2][1]+vec[i  ][1]/2},
                    3);
            }
            m<<=1;
        }

        //r2f symmetries
        m<<=3;  // display before doing m4
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[7]);
                offGraphics.fillPolygon(
                    new int[]{centx+(4*vec[i+1][0]+4*vec[i+2][0]+3)/6,
                              centx+(4*vec[i+1][0]+2*vec[i+2][0]+3)/6,
                              centx+(2*vec[i+1][0]+2*vec[i+2][0]+3)/6,
                              centx+(2*vec[i+1][0]+4*vec[i+2][0]+3)/6},
                    new int[]{centy+(4*vec[i+1][1]+4*vec[i+2][1]+3)/6,
                              centy+(4*vec[i+1][1]+2*vec[i+2][1]+3)/6,
                              centy+(2*vec[i+1][1]+2*vec[i+2][1]+3)/6,
                              centy+(2*vec[i+1][1]+4*vec[i+2][1]+3)/6},
                    4);
            }
            m<<=1;
        }
        m>>=6;

        //m4 symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[5]);
                offGraphics.fillPolygon(
                    new int[]{centx+(4*vec[i+1][0]+4*vec[i+2][0]+3)/6,
                              centx+(4*vec[i+1][0]+2*vec[i+2][0]+3)/6,
                              centx+(2*vec[i+1][0]+2*vec[i+2][0]+3)/6,
                              centx+(2*vec[i+1][0]+4*vec[i+2][0]+3)/6},
                    new int[]{centy+(4*vec[i+1][1]+4*vec[i+2][1]+3)/6,
                              centy+(4*vec[i+1][1]+2*vec[i+2][1]+3)/6,
                              centy+(2*vec[i+1][1]+2*vec[i+2][1]+3)/6,
                              centy+(2*vec[i+1][1]+4*vec[i+2][1]+3)/6},
                    4);
            }
            m<<=1;
        }
        m<<=3;

        //r4 symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[9]);
                offGraphics.fillPolygon(
                    new int[]{centx+(4*vec[i+1][0]+4*vec[i+2][0]+3)/6,
                              centx+(4*vec[i+1][0]+2*vec[i+2][0]+3)/6,
                              centx+(2*vec[i+1][0]+2*vec[i+2][0]+3)/6,
                              centx+(2*vec[i+1][0]+4*vec[i+2][0]+3)/6},
                    new int[]{centy+(4*vec[i+1][1]+4*vec[i+2][1]+3)/6,
                              centy+(4*vec[i+1][1]+2*vec[i+2][1]+3)/6,
                              centy+(2*vec[i+1][1]+2*vec[i+2][1]+3)/6,
                              centy+(2*vec[i+1][1]+4*vec[i+2][1]+3)/6},
                    4);
            }
            m<<=1;
        }

        //r3 symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[8]);
                offGraphics.fillPolygon(
                    new int[]{centx+vec[i][0],
                              centx+vec[i][0]+(vec[i+1][0]+1)/3,
                              centx+2*vec[i][0]/3,
                              centx+vec[i][0]+(vec[i+2][0]+1)/3},
                    new int[]{centy+vec[i][1],
                              centy+vec[i][1]+(vec[i+1][1]+1)/3,
                              centy+2*vec[i][1]/3,
                              centy+vec[i][1]+(vec[i+2][1]+1)/3},
                    4);
                offGraphics.fillPolygon(
                    new int[]{centx+vec[i+1][0]+vec[i+2][0],
                              centx+(2*vec[i+1][0]+3*vec[i+2][0])/3,
                              centx+(3*vec[i+1][0]+2*vec[i+2][0])/3,},
                    new int[]{centy+vec[i+1][1]+vec[i+2][1],
                              centy+(2*vec[i+1][1]+3*vec[i+2][1])/3,
                              centy+(3*vec[i+1][1]+2*vec[i+2][1])/3,},
                    3);
            }
            m<<=1;
        }

        if( (type&m)!=0 ){
            offGraphics.setColor(colors[8]);
            offGraphics.fillPolygon(
                new int[]{centx+(vec[i  ][0]+1)/3,
                          centx+(vec[i+1][0]+1)/3,
                          centx+(vec[i+2][0]+1)/3,},
                new int[]{centy+(vec[i  ][1]+1)/3,
                          centy+(vec[i+1][1]+1)/3,
                          centy+(vec[i+2][1]+1)/3,},
                3);
        }
        m<<=1;

        // cube itself
        offGraphics.setColor(colors[0]);
        for(i=0; i<3; i++){
            offGraphics.drawLine(centx,centy,centx+vec[i][0],centy+vec[i][1]);
            offGraphics.drawLine(centx+vec[i][0],centy+vec[i][1],centx+vec[i][0]+vec[i+1][0],centy+vec[i][1]+vec[i+1][1]);
            offGraphics.drawLine(centx+vec[i][0]+vec[i+1][0],centy+vec[i][1]+vec[i+1][1],centx+vec[i+1][0],centy+vec[i+1][1]);

            offGraphics.drawPolyline(
                 new int[]{centx,
                           centx+vec[i][0],
                           centx+vec[i][0]+vec[i+1][0],
                           centx+vec[i+1][0]},
                 new int[]{centy,
                           centy+vec[i][1],
                           centy+vec[i][1]+vec[i+1][1],
                           centy+vec[i+1][1]},
                 4);
        }

        //r2e symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[7]);
                offGraphics.drawLine(
                    centx+vec[i][0]/3, centy+vec[i][1]/3,
                    centx+2*vec[i][0]/3, centy+2*vec[i][1]/3);
            }
            m<<=1;
        }

        //r2e symmetries
        for(i=0; i<3; i++){
            if( (type&m)!=0 ){
                offGraphics.setColor(colors[7]);
                offGraphics.drawLine(
                        centx+vec[i+1][0]+  vec[i][0]/3, centy+vec[i+1][1]+  vec[i][1]/3,
                        centx+vec[i+1][0]+2*vec[i][0]/3, centy+vec[i+1][1]+2*vec[i][1]/3);
                offGraphics.drawLine(
                        centx+vec[i+2][0]+  vec[i][0]/3, centy+vec[i+2][1]+  vec[i][1]/3,
                        centx+vec[i+2][0]+2*vec[i][0]/3, centy+vec[i+2][1]+2*vec[i][1]/3);
            }
            m<<=1;
        }

        // display result
        g.drawImage(offImage,0,0,this);
    }

    int checkSym(int t){
        // add all deduced symmetries to bitmask t
        // list of all possible subgroups of Symm_cube,
        //  from small to large
        final int s[]={
            //order 2
            0x00000001,//m_c
            0x00000002,0x00000004,0x00000008,//m_e
            0x00000010,0x00000020,0x00000040,
            0x00000080,0x00000100,0x00000200,//m_f
            0x00002000,0x00004000,0x00008000,//r2_f
            0x00800000,0x01000000,0x02000000,//r2_e
            0x04000000,0x08000000,0x10000000,
            //order 3
            0x00080000,0x00100000,0x00200000,0x00400000,//r3
            //order 4
            0x00002400,0x00004800,0x00009000,//m4
            0x00012000,0x00024000,0x00048000,//r4
            0x0000E000,//r2f r2f
            0x04802000,0x09004000,0x12008000,//r2e r2e
            0x00800082,0x01000104,0x02000208,//mf r2e
            0x04000090,0x08000120,0x10000240,
            0x04000003,0x08000005,0x10000009,//me r2e
            0x00800011,0x01000021,0x02000041,
            0x00002081,0x00004101,0x00008201,//mc r2f
            0x00002012,0x00004024,0x00008048,//me r2f
            0x00002300,0x00004280,0x00008180,//mf r2f
            //order 6
            0x07080000,0x0A900000,0x11A00000,0x1C400000,//r3r2
            0x00080001,0x00100001,0x00200001,0x00400001,//mc r3
            0x00080062,0x00100054,0x00200038,0x0040000E,//me r3
            //order 8
            0x0481E000,0x0902E000,0x1204E000,//r4r2
            0x04802093,0x09004125,0x12008249,//mc r2e r2e
            0x04802700,0x09004A80,0x12009180,//mf r2e r2e
            0x0000E381,//mf r2f r2f
            0x0000E412,0x0000E824,0x0000F048,//me r2f r2f
            0x00012481,0x00024901,0x00049201,//mc r4
            0x00012312,0x000242A4,0x000481C8,//me r4
            //order 12
            0x0078E000,//r3r2r2
            0x07080063,0x0A900055,0x11A00039,0x1C40000F,//mr3r2
            //order 16
            0x0481E793,0x0902EBA5,0x1204F3C9,//mr4r2
            //order 24
            0x1FFFE000,//r4r3r2
            0x0078E381,//mfr3r2r2
            0x0078FC7E,//mer3r2r2
            //order 48
            0x1FFFFFFF,//mr4r3r2
        };

        if( t==0 ) return 0;
        int i=0;
        while( (t&s[i])!=t ) i++;
        return s[i];
    }

    public void update(Graphics g) { paint(g); }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
		boolean shift = e.isShiftDown() || e.isAltDown();
        boolean ctrl = e.isControlDown();
        repaint();
        e.consume();

        //Pass on an event to main applet, to signify change
        if(main!=null){
            ActionEvent ae=new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
        					shift? "s": ctrl?"c":"");
            main.actionPerformed( ae );
        }
    }
}