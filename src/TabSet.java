import java.awt.*;
import java.awt.event.*;

// TabSet
// This is a set of tab pages.
//
//
//
//

public final class TabSet extends Canvas
    implements MouseListener
{
    final int tabEdge=9;
    Image offImage;
    Graphics offGraphics;
    ActionListener main;
    int currentTab=0;
    int width,height;
    Panel[] panelList=new Panel[0];
    String[] labelList=new String[0];
    Color tabColor, selTabColor;

    public TabSet(ActionListener m, Color tc, Color stc){
        main=m;
        tabColor=tc;
        selTabColor=stc;
        addMouseListener(this);
    }
    public void setTab(int t){currentTab=t;}
    public int getTab(){return currentTab;}
    public void addTab(String s, Panel p){
		Panel[] npl=new Panel[panelList.length+1];
		String[] nll=new String[panelList.length+1];
		for(int i=0; i<panelList.length;i++){
			npl[i]=panelList[i];
			nll[i]=labelList[i];
		}
		npl[panelList.length]=p;
		nll[panelList.length]=s;
		panelList=npl;
		labelList=nll;
	}
    void initialise()
    {
        width=getSize().width;
        height=getSize().height;
        offImage = createImage(width, height ); // Double buffer
        offGraphics = offImage.getGraphics();
    }

    public void paint( Graphics g )
    {
        if( offImage == null ){ initialise(); }

        // draw background colour
        offGraphics.setColor(getBackground());
        offGraphics.fillRect(0,0,width,height);

		//
		for(int i=0; i<panelList.length; i++){
			if(i!=currentTab) drawTab(i, false);
		}
		drawTab(currentTab, true);

        // display result
        g.drawImage(offImage,0,0,this);
    }
    void drawTab(int t, boolean sel){
		int w = (width-tabEdge)/panelList.length;
        int[]x = new int[]{t*w+w+tabEdge,t*w+w,t*w+tabEdge,t*w};
        int[]y = new int[]{height-1,0,0,height-1};

        offGraphics.setColor(sel?selTabColor:tabColor);
		offGraphics.fillPolygon(x,y,4);
        offGraphics.drawPolygon(x,y,4);
		offGraphics.setColor(getForeground());
		if(sel) offGraphics.drawPolyline(x,y,4);
		else offGraphics.drawPolygon(x,y,4);
        
		// show label
		offGraphics.drawString( labelList[t], t*w+tabEdge, height-3 );
	}

    public void update(Graphics g) { paint(g); }
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseReleased(MouseEvent e) {}
    public void mousePressed(MouseEvent e) {
		int i = (width-tabEdge)/panelList.length;
		int x = (2*e.getX()-tabEdge)/(2*i);
		if( x>=panelList.length ) x = panelList.length-1;
        if( x<0 ) x = 0;
		currentTab = x;
		for(i=0; i<panelList.length; i++){
			panelList[i].setVisible( i==currentTab );
		}
        repaint();
        e.consume();

        //Pass on an event to main applet, to signify change
        if( main!=null ){
            ActionEvent ae=new ActionEvent(this, ActionEvent.ACTION_PERFORMED,labelList[currentTab]);
            main.actionPerformed( ae );
        }
    }
}