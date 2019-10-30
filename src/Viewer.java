// Rubik's Cube simulator

import java.awt.*;
import java.awt.event.*;

abstract class Viewer extends Canvas
	implements MouseListener, MouseMotionListener
{
	Image offImage;
	Graphics offGraphics;
	int width, height;
	Color baseColor = new Color(0,0,0);		 //black cube
	Color colors[] = {
		new Color( 255,   0, 0   ), //red
		new Color( 0,     0, 255 ), //blue
		new Color( 255, 255, 0   ), //yellow
		new Color( 255, 160, 64   ), //orange
		new Color( 0,   192, 0   ), //green
		new Color( 255, 255, 255 )  //white
	};
	// cube group setting
	ActionListener main;

	public Viewer(int x, int y, ActionListener m){
		width=x; height=y;
		addMouseListener(this);
		main=m;
	}
	public void initialise(){
		offImage = createImage(width, height ); // Double buffer
		offGraphics = offImage.getGraphics();
	}
	public void reset(){
		repaint();
	}

//--- facelet routines ---
	// conversion data from internal cube representation to external facelet representation
	private void editMove( int f1, int f2){
		// edit mode, move facelet f1 to f2
		//find cubelet for f1
		int c1=0,o1=0,c2=0,o2=0;
		boolean f=false;
		if( Cubie.settings.lockViewer ) return;
		if( f1<0 || f2<0 || f1>=54 || f2>=54 ) return;
		for( o1=0; o1<3; o1++){
			for( c1=0; c1<26; c1++){
				if( CubePosition.cubelet2facelet[c1][o1]==f1 ){ f=true; break; }
			}
			if(f) break;
		}
		if(f==false) return;
		f=false;
		for( o2=0; o2<3; o2++){
			for( c2=0; c2<26; c2++){
				if( CubePosition.cubelet2facelet[c2][o2]==f2 ){ f=true; break; }
			}
			if(f) break;
		}
		if(f==false) return;
		Cubie.settings.cubePos.editMove(c1,o1,c2,o2);
		doEvent(true);
		repaint();
	}

	public boolean showMove(int face, int qu)
	{
		Cubie.settings.cubePos.doMove( face, qu, true );
		repaint();
		doEvent(false);
		return(true);
	}

//--- mouse routines ---
	int lastX, lastY, lastF=-1;
	int keys=0;
	boolean moved = false;
	final int sensitivityDrag = 40;
	final int sensitivityMove = 12;

    public void mousePressed(MouseEvent e) {
       	addMouseMotionListener(this);
		lastX = e.getX();
		lastY = e.getY();
		lastF = getFacelet( lastX, lastY );
		keys = e.isShiftDown()?1:0;
		keys+= e.isControlDown()?2:0;
		keys+= e.isAltDown()?4:0;
		moved=false;
		e.consume();
	}
    public void mouseReleased(MouseEvent e) {
        removeMouseMotionListener(this);
		if( Cubie.settings.edit && lastF>=0 ){
			editMove( lastF, getFacelet(e.getX(), e.getY()));
		}else if(!moved){
			checkMouseMove( e.getX(), e.getY(), sensitivityMove );
		}
		e.consume();
	}
    public void mouseDragged( MouseEvent e ) {
		if( (!Cubie.settings.edit || lastF<0 ) && !moved ) checkMouseMove( e.getX(), e.getY(), sensitivityDrag );
		e.consume();
    }
    public void mouseMoved(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

	abstract protected void checkMouseMove(int x, int y, int d);
	abstract protected int getFacelet( int x, int y );

	// m=0-5, normal turn
	// m=6-8, middle layer turn
	// m=9-11, cube turn
	// m=12-14, slice move (output only)
	// m=15-17, anti-slice move (output only)
    // check group restrictions, parse keys, and call domove
    // q=+/-1 or +-2.
    public void tryMove( int m0, int q0 )
	{
      int q = q0;
      int m = m0;
		if( m<9 ){
			if( Cubie.settings.group==1 ) keys|=1;
			if( Cubie.settings.group==2 ) { keys|=2; keys&=3; }
			if( Cubie.settings.group==3 ) { keys|=4; keys&=5; }
			if( Cubie.settings.group==3 && m>5 ) keys|=1;
		}
		if( Cubie.settings.group==4 ){
			if( m!=1 && m!=3 && m<6 ) return;
		}

		if( (keys&1)!=0 ){
			//shift pressed: half turn
			if(q>0) q=2;  //positive
			else q=-2;
		}
		if( (keys&2)!=0 && Cubie.settings.group!=4 ){
			//control pressed: Slice turn
			if(m<3) m+=12;
			else if(m<6){ m+=9; q=-q; }{}
		}
		if( (keys&4)!=0 && Cubie.settings.group!=4 ){
			//alt pressed: Anti-slice turn
			if(m<3) m+=15;
			else if(m<6) m+=12;
		}
		doMove(m,q);
	}

	void doMove(int m, int q){
		if( Cubie.settings.lockViewer ) return;
		Cubie.settings.cubePos.doMove(m,q,true);
		repaint();
		doEvent(true);
	}

	void doEvent(boolean user){
		//dispatch action event that move performed.
		// two event types: usermove and automove. First is a move done
		// by user acting on viewer via mouse. Second is move initiated from
		// a function call from Cubie.
		//I did not bother to implement an addActionListener method as there
		// will only ever be one listener to this, viz the main cubie applet.
		ActionEvent e=new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
									user ? "user": "auto");
		main.actionPerformed( e );
	}

//-- display routines ---
	public void update(Graphics g) { paint(g); }
}
