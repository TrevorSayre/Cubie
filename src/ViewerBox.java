import java.awt.*;
import java.awt.event.*;


final class ViewerBox extends Viewer
{
//--- Display routines ---

	// graphical representation data
	int CELL, BORD, OFFSETX, OFFSETY;
	Polygon faceletsI[];
	Polygon faceletsO[];
	Polygon faceOriMarks[][];

	public ViewerBox(int x, int y, ActionListener m){
		super(x,y,m);
		int i,j,c;
		CELL = x/16;
		BORD = y/16;
		if(BORD<CELL) CELL=BORD;
		BORD = 2;
		OFFSETX = (x-16*CELL)/2;
		OFFSETY = (y-16*CELL)/2;
		faceletsI = new Polygon[54];
		faceletsO = new Polygon[54];
		faceOriMarks = new Polygon[54][4];

		for( c=0; c<54; c++){
			faceletsI[c] = new Polygon();
			faceletsO[c] = new Polygon();
			for(i=0; i<4;i++) faceOriMarks[c][i] = new Polygon();
		}
		c=0;
		// L
		for(j=0; j<3; j++) for(i=0; i<3; i++) {
			doFacelet(	CELL*(4-i),  CELL*(4-i)+ (CELL*(8+2*i)* j   )/3,
						CELL*(5-i),  CELL*(5-i)+ (CELL*(6+2*i)* j   )/3,
						CELL*(5-i),  CELL*(5-i)+ (CELL*(6+2*i)*(j+1))/3,
						CELL*(4-i),  CELL*(4-i)+ (CELL*(8+2*i)*(j+1))/3,
						 BORD,       2*BORD*(3-j)/3,
						-BORD,       2*BORD* j   /3,
						-BORD,       2*BORD*(j-2)/3,
						 BORD,      -2*BORD*(j+1)/3,
						c);
			c++;
		}
		// U
		for(j=0; j<3; j++) for(i=0; i<3; i++) {
			doFacelet(
						CELL*(5-j)+       (CELL*(6+2*j)*        (i+1))/3,CELL*(5-j),
						CELL*(5-j)+       (CELL*(6+2*j)*         i   )/3,CELL*(5-j),
						CELL*(4-j)+       (CELL*(8+2*j)*         i   )/3,CELL*(4-j),
						CELL*(4-j)+       (CELL*(8+2*j)*        (i+1))/3,CELL*(4-j),
						 2*BORD*(i-2)/3,-BORD,
						 2*BORD* i   /3,-BORD,
						 2*BORD*(3-i)/3, BORD,
						-2*BORD*(i+1)/3, BORD,
						c);
			c++;
		}
		// F
		faceletsO[c].addPoint(0,0);
		faceletsO[c].addPoint(6*CELL,0);
		faceletsO[c].addPoint(6*CELL,2*CELL);
		faceletsO[c].addPoint(2*CELL,2*CELL);
		faceletsO[c].addPoint(2*CELL,6*CELL);
		faceletsO[c].addPoint(0,6*CELL);

		faceletsI[c].addPoint(BORD,BORD);
		faceletsI[c].addPoint(6*CELL-BORD,BORD);
		faceletsI[c].addPoint(6*CELL-BORD,2*CELL-BORD);
		faceletsI[c].addPoint(2*CELL-BORD,2*CELL-BORD);
		faceletsI[c].addPoint(2*CELL-BORD,6*CELL-BORD);
		faceletsI[c].addPoint(BORD,6*CELL-BORD);
		// top marker
		faceOriMarks[c][0].addPoint((4*CELL+BORD)/2, 0);
		faceOriMarks[c][0].addPoint((8*CELL-BORD)/2, 0);
		faceOriMarks[c][0].addPoint((8*CELL-BORD)/2, CELL);
		faceOriMarks[c][0].addPoint((4*CELL+BORD)/2, CELL);
		// right marker
		faceOriMarks[c][3].addPoint(4*CELL, (  CELL+BORD)/2);
		faceOriMarks[c][3].addPoint(6*CELL, (  CELL+BORD)/2);
		faceOriMarks[c][3].addPoint(6*CELL, (3*CELL-BORD)/2);
		faceOriMarks[c][3].addPoint(4*CELL, (3*CELL-BORD)/2);
		// bottom marker
		faceOriMarks[c][2].addPoint((  CELL+BORD)/2, 4*CELL);
		faceOriMarks[c][2].addPoint((3*CELL-BORD)/2, 4*CELL);
		faceOriMarks[c][2].addPoint((3*CELL-BORD)/2, 6*CELL);
		faceOriMarks[c][2].addPoint((  CELL+BORD)/2, 6*CELL);
		// left marker
		faceOriMarks[c][1].addPoint(0   , (4*CELL+BORD)/2);
		faceOriMarks[c][1].addPoint(CELL, (4*CELL+BORD)/2);
		faceOriMarks[c][1].addPoint(CELL, (8*CELL-BORD)/2);
		faceOriMarks[c][1].addPoint(0   , (8*CELL-BORD)/2);
		c++;
		doFacelet(
					10*CELL,0,
					 6*CELL,0,
					 6*CELL,2*CELL,
					10*CELL,2*CELL,
					-BORD, BORD,
					 BORD, BORD,
					 BORD,-BORD,
					-BORD,-BORD,
					 c);
		c++;
		faceletsO[c].addPoint(10*CELL,0);
		faceletsO[c].addPoint(16*CELL,0);
		faceletsO[c].addPoint(16*CELL,6*CELL);
		faceletsO[c].addPoint(14*CELL,6*CELL);
		faceletsO[c].addPoint(14*CELL,2*CELL);
		faceletsO[c].addPoint(10*CELL,2*CELL);
		faceletsI[c].addPoint(10*CELL+BORD,BORD);
		faceletsI[c].addPoint(16*CELL-BORD,BORD);
		faceletsI[c].addPoint(16*CELL-BORD,6*CELL-BORD);
		faceletsI[c].addPoint(14*CELL+BORD,6*CELL-BORD);
		faceletsI[c].addPoint(14*CELL+BORD,2*CELL-BORD);
		faceletsI[c].addPoint(10*CELL+BORD,2*CELL-BORD);
		// top marker
		faceOriMarks[c][0].addPoint((24*CELL+BORD)/2, 0);
		faceOriMarks[c][0].addPoint((28*CELL-BORD)/2, 0);
		faceOriMarks[c][0].addPoint((28*CELL-BORD)/2, CELL);
		faceOriMarks[c][0].addPoint((24*CELL+BORD)/2, CELL);
		// right marker
		faceOriMarks[c][3].addPoint(15*CELL, (4*CELL+BORD)/2);
		faceOriMarks[c][3].addPoint(16*CELL, (4*CELL+BORD)/2);
		faceOriMarks[c][3].addPoint(16*CELL, (8*CELL-BORD)/2);
		faceOriMarks[c][3].addPoint(15*CELL, (8*CELL-BORD)/2);
		// bottom marker
		faceOriMarks[c][2].addPoint((29*CELL+BORD)/2, 4*CELL);
		faceOriMarks[c][2].addPoint((31*CELL-BORD)/2, 4*CELL);
		faceOriMarks[c][2].addPoint((31*CELL-BORD)/2, 6*CELL);
		faceOriMarks[c][2].addPoint((29*CELL+BORD)/2, 6*CELL);
		// left marker
		faceOriMarks[c][1].addPoint(10*CELL, (  CELL+BORD)/2);
		faceOriMarks[c][1].addPoint(12*CELL, (  CELL+BORD)/2);
		faceOriMarks[c][1].addPoint(12*CELL, (3*CELL-BORD)/2);
		faceOriMarks[c][1].addPoint(10*CELL, (3*CELL-BORD)/2);
		c++;
		doFacelet(
					2*CELL,6*CELL,
					0,6*CELL,
					0,10*CELL,
					2*CELL,10*CELL,
					-BORD, BORD,
					 BORD, BORD,
					 BORD,-BORD,
					-BORD,-BORD,
					 c);
		c++;
		c++;
		doFacelet(
					16*CELL,6*CELL,
					14*CELL,6*CELL,
					14*CELL,10*CELL,
					16*CELL,10*CELL,
					-BORD, BORD,
					 BORD, BORD,
					 BORD,-BORD,
					-BORD,-BORD,
					 c);
		c++;
		faceletsO[c].addPoint(0,10*CELL);
		faceletsO[c].addPoint(2*CELL,10*CELL);
		faceletsO[c].addPoint(2*CELL,14*CELL);
		faceletsO[c].addPoint(6*CELL,14*CELL);
		faceletsO[c].addPoint(6*CELL,16*CELL);
		faceletsO[c].addPoint(0,16*CELL);
		faceletsI[c].addPoint(BORD,10*CELL+BORD);
		faceletsI[c].addPoint(2*CELL-BORD,10*CELL+BORD);
		faceletsI[c].addPoint(2*CELL-BORD,14*CELL+BORD);
		faceletsI[c].addPoint(6*CELL-BORD,14*CELL+BORD);
		faceletsI[c].addPoint(6*CELL-BORD,16*CELL-BORD);
		faceletsI[c].addPoint(BORD,16*CELL-BORD);
		// top marker
		faceOriMarks[c][0].addPoint((  CELL+BORD)/2, 10*CELL);
		faceOriMarks[c][0].addPoint((  CELL+BORD)/2, 12*CELL);
		faceOriMarks[c][0].addPoint((3*CELL-BORD)/2, 12*CELL);
		faceOriMarks[c][0].addPoint((3*CELL-BORD)/2, 10*CELL);
		// right marker
		faceOriMarks[c][3].addPoint(4*CELL, (29*CELL+BORD)/2);
		faceOriMarks[c][3].addPoint(4*CELL, (31*CELL-BORD)/2);
		faceOriMarks[c][3].addPoint(6*CELL, (31*CELL-BORD)/2);
		faceOriMarks[c][3].addPoint(6*CELL, (29*CELL+BORD)/2);
		// bottom marker
		faceOriMarks[c][2].addPoint((4*CELL+BORD)/2, 15*CELL);
		faceOriMarks[c][2].addPoint((4*CELL+BORD)/2, 16*CELL);
		faceOriMarks[c][2].addPoint((8*CELL-BORD)/2, 16*CELL);
		faceOriMarks[c][2].addPoint((8*CELL-BORD)/2, 15*CELL);
		// left marker
		faceOriMarks[c][1].addPoint(0   , (24*CELL+BORD)/2);
		faceOriMarks[c][1].addPoint(0   , (28*CELL-BORD)/2);
		faceOriMarks[c][1].addPoint(CELL, (28*CELL-BORD)/2);
		faceOriMarks[c][1].addPoint(CELL, (24*CELL+BORD)/2);
		c++;
		doFacelet(
					10*CELL,14*CELL,
					 6*CELL,14*CELL,
					 6*CELL,16*CELL,
					10*CELL,16*CELL,
					-BORD, BORD,
					 BORD, BORD,
					 BORD,-BORD,
					-BORD,-BORD,
					 c);
		c++;
		faceletsO[c].addPoint(14*CELL,10*CELL);
		faceletsO[c].addPoint(16*CELL,10*CELL);
		faceletsO[c].addPoint(16*CELL,16*CELL);
		faceletsO[c].addPoint(10*CELL,16*CELL);
		faceletsO[c].addPoint(10*CELL,14*CELL);
		faceletsO[c].addPoint(14*CELL,14*CELL);
		faceletsI[c].addPoint(14*CELL+BORD,10*CELL+BORD);
		faceletsI[c].addPoint(16*CELL-BORD,10*CELL+BORD);
		faceletsI[c].addPoint(16*CELL-BORD,16*CELL-BORD);
		faceletsI[c].addPoint(10*CELL+BORD,16*CELL-BORD);
		faceletsI[c].addPoint(10*CELL+BORD,14*CELL+BORD);
		faceletsI[c].addPoint(14*CELL+BORD,14*CELL+BORD);
		// top marker
		faceOriMarks[c][0].addPoint((29*CELL+BORD)/2, 10*CELL);
		faceOriMarks[c][0].addPoint((29*CELL+BORD)/2, 12*CELL);
		faceOriMarks[c][0].addPoint((31*CELL-BORD)/2, 12*CELL);
		faceOriMarks[c][0].addPoint((31*CELL-BORD)/2, 10*CELL);
		// right marker
		faceOriMarks[c][3].addPoint(16*CELL, (24*CELL+BORD)/2);
		faceOriMarks[c][3].addPoint(16*CELL, (28*CELL-BORD)/2);
		faceOriMarks[c][3].addPoint(15*CELL, (28*CELL-BORD)/2);
		faceOriMarks[c][3].addPoint(15*CELL, (24*CELL+BORD)/2);
		// bottom marker
		faceOriMarks[c][2].addPoint((24*CELL+BORD)/2, 15*CELL);
		faceOriMarks[c][2].addPoint((24*CELL+BORD)/2, 16*CELL);
		faceOriMarks[c][2].addPoint((28*CELL-BORD)/2, 16*CELL);
		faceOriMarks[c][2].addPoint((28*CELL-BORD)/2, 15*CELL);
		// left marker
		faceOriMarks[c][1].addPoint(10*CELL, (29*CELL+BORD)/2);
		faceOriMarks[c][1].addPoint(10*CELL, (31*CELL-BORD)/2);
		faceOriMarks[c][1].addPoint(12*CELL, (31*CELL-BORD)/2);
		faceOriMarks[c][1].addPoint(12*CELL, (29*CELL+BORD)/2);
		c++;
		// R
		for(j=0; j<3; j++) for(i=0; i<3; i++) {
			doFacelet(
						CELL*(13-i) ,CELL*(3+i)+(CELL*(10-2*i)* j   )/3,
						CELL*(14-i) ,CELL*(2+i)+(CELL*(12-2*i)* j   )/3,
						CELL*(14-i) ,CELL*(2+i)+(CELL*(12-2*i)*(j+1))/3,
						CELL*(13-i) ,CELL*(3+i)+(CELL*(10-2*i)*(j+1))/3,
						+BORD, 2*BORD* j   /3,
						-BORD, 2*BORD*(3-j)/3,
						-BORD,-2*BORD*(j+1)/3,
						+BORD, 2*BORD*(j-2)/3,
						c);
			c++;
		}
		// D
		for(j=0; j<3; j++) for(i=0; i<3; i++) {
			doFacelet(
						CELL*(2+j)+(CELL*(12-2*j)*(i+1))/3,CELL*(14-j),
						CELL*(2+j)+(CELL*(12-2*j)* i   )/3,CELL*(14-j),
						CELL*(3+j)+(CELL*(10-2*j)* i   )/3,CELL*(13-j),
						CELL*(3+j)+(CELL*(10-2*j)*(i+1))/3,CELL*(13-j),
						-2*BORD*(i+1)/3,-BORD,
						 2*BORD*(3-i)/3,-BORD,
						 2*BORD* i   /3, BORD,
						 2*BORD*(i-3)/3, BORD,
						c);
			c++;
		}
		// B
		for(j=0; j<3; j++) for(i=0; i<3; i++) {
			doFacelet(	CELL*( 9-2*i),CELL*(5+2*j),
						CELL*(11-2*i),CELL*(5+2*j),
						CELL*(11-2*i),CELL*(7+2*j),
						CELL*( 9-2*i),CELL*(7+2*j),
						 BORD, BORD,
						-BORD, BORD,
						-BORD,-BORD,
						 BORD,-BORD,
						c);
			c++;
		}

		for( c=0; c<54; c++){
			faceletsI[c].translate(OFFSETX,OFFSETY);
			faceletsO[c].translate(OFFSETX,OFFSETY);
			for(i=0; i<4; i++)
				faceOriMarks[c][i].translate(OFFSETX,OFFSETY);
		}
	}
	private void doFacelet(int x1,int y1,int x2,int y2,int x3,int y3,int x4,int y4,
							int d1,int e1,int d2,int e2,int d3,int e3,int d4,int e4,
							int c){
		faceletsO[c].addPoint(x1,y1);
		faceletsO[c].addPoint(x2,y2);
		faceletsO[c].addPoint(x3,y3);
		faceletsO[c].addPoint(x4,y4);
		faceletsI[c].addPoint(x1+d1,y1+e1);
		faceletsI[c].addPoint(x2+d2,y2+e2);
		faceletsI[c].addPoint(x3+d3,y3+e3);
		faceletsI[c].addPoint(x4+d4,y4+e4);
		//do top marker
		faceOriMarks[c][0].addPoint(((x1+d1)*3+(x2+d2)+2)/4, ((y1+e1)*3+(y2+e2)+2)/4);
		faceOriMarks[c][0].addPoint(((x1+d1)+3*(x2+d2)+2)/4, ((y1+e1)+3*(y2+e2)+2)/4);
		faceOriMarks[c][0].addPoint(((x1+d1+x4+d4)+3*(x2+d2+x3+d3)+4)/8, ((y1+e1+y4+e4)+3*(y2+e2+y3+e3)+4)/8);
		faceOriMarks[c][0].addPoint(((x1+d1+x4+d4)*3+(x2+d2+x3+d3)+4)/8, ((y1+e1+y4+e4)*3+(y2+e2+y3+e3)+4)/8);
		//do right marker
		faceOriMarks[c][1].addPoint(((x2+d2)*3+(x3+d3)+2)/4, ((y2+e2)*3+(y3+e3)+2)/4);
		faceOriMarks[c][1].addPoint(((x2+d2)+3*(x3+d3)+2)/4, ((y2+e2)+3*(y3+e3)+2)/4);
		faceOriMarks[c][1].addPoint(((x2+d2+x1+d1)+3*(x3+d3+x4+d4)+4)/8, ((y2+e2+y1+e1)+3*(y3+e3+y4+e4)+4)/8);
		faceOriMarks[c][1].addPoint(((x2+d2+x1+d1)*3+(x3+d3+x4+d4)+4)/8, ((y2+e2+y1+e1)*3+(y3+e3+y4+e4)+4)/8);
		//do bottom marker
		faceOriMarks[c][2].addPoint(((x3+d3)*3+(x4+d4)+2)/4, ((y3+e3)*3+(y4+e4)+2)/4);
		faceOriMarks[c][2].addPoint(((x3+d3)+3*(x4+d4)+2)/4, ((y3+e3)+3*(y4+e4)+2)/4);
		faceOriMarks[c][2].addPoint(((x3+d3+x2+d2)+3*(x4+d4+x1+d1)+4)/8, ((y3+e3+y2+e2)+3*(y4+e4+y1+e1)+4)/8);
		faceOriMarks[c][2].addPoint(((x3+d3+x2+d2)*3+(x4+d4+x1+d1)+4)/8, ((y3+e3+y2+e2)*3+(y4+e4+y1+e1)+4)/8);
		//do left marker
		faceOriMarks[c][3].addPoint(((x4+d4)*3+(x1+d1)+2)/4, ((y4+e4)*3+(y1+e1)+2)/4);
		faceOriMarks[c][3].addPoint(((x4+d4)+3*(x1+d1)+2)/4, ((y4+e4)+3*(y1+e1)+2)/4);
		faceOriMarks[c][3].addPoint(((x4+d4+x3+d3)+3*(x1+d1+x2+d2)+4)/8, ((y4+e4+y3+e3)+3*(y1+e1+y2+e2)+4)/8);
		faceOriMarks[c][3].addPoint(((x4+d4+x3+d3)*3+(x1+d1+x2+d2)+4)/8, ((y4+e4+y3+e3)*3+(y1+e1+y2+e2)+4)/8);
	}

	public void paint(Graphics g)
	{
		if( offImage == null ){ initialise(); }
		offGraphics.setColor(getBackground()); // Clear drawing buffer
		offGraphics.fillRect(0,0,width,height);
		offGraphics.setColor(baseColor); // draw cube base
		offGraphics.fillRect(OFFSETX,OFFSETY,16*CELL,16*CELL);
		// draw cube
        Cubie.settings.cubePos.getFaceletColors();
		for( int c=0; c<54; c++){	// each facelet
			offGraphics.setColor(colors[Cubie.settings.cubePos.cubeletPerm[20+Cubie.settings.cubePos.faceletColor[c]]-20]); // draw facelet
			offGraphics.fillPolygon(faceletsI[c]);
			if( Cubie.settings.superGroup ){
				offGraphics.setColor(baseColor);
				offGraphics.fillPolygon(faceOriMarks[c][Cubie.settings.cubePos.faceletOri[c]]);
			}
		}
		g.drawImage(offImage,0,0,this);
	}

//--- mouse routines ---
	// conversion from facelet mouse move to internal move number
	final int moves[][] = {
		{ 2, 2, 2, 8, 11, 8,-5,-5,-5,   -6,-6,-6, 9, 12, 9, 3, 3, 3,   -2,-2,-2,-8,-11,-8, 5, 5, 5,    2, 2, 2, 8, 11, 8,-5,-5,-5,    -3,-3,-3,-9,-12,-9, 6, 6, 6,     2, 2, 2, 8, 11, 8,-5,-5,-5},
		{ 6,-9,-3, 6,-12,-3, 6,-9,-3,   -1,-7, 4,-1,-10, 4,-1,-7, 4,    1, 7,-4, 1, 10,-4, 1, 7,-4,    3, 9,-6, 3, 12,-6, 3, 9,-6,    -1,-7, 4,-1,-10, 4,-1,-7, 4,     4,-7,-1, 4,-10,-1, 4,-7,-1},
		{-2,-2,-2,-8,-11,-8, 5, 5, 5,    6, 6, 6,-9,-12,-9,-3,-3,-3,    2, 2, 2, 8, 11, 8,-5,-5,-5,   -2,-2,-2,-8,-11,-8, 5, 5, 5,     3, 3, 3, 9, 12, 9,-6,-6,-6,    -2,-2,-2,-8,-11,-8, 5, 5, 5},
		{-6, 9, 3,-6, 12, 3,-6, 9, 3,    1, 7,-4, 1, 10,-4, 1, 7,-4,   -1,-7, 4,-1,-10, 4,-1,-7, 4,   -3,-9, 6,-3,-12, 6,-3,-9, 6,     1, 7,-4, 1, 10,-4, 1, 7,-4,    -4, 7, 1,-4, 10, 1,-4, 7, 1}
	};

	//Check if mouse move to coord x,y is far enough to trigger a move.
	protected void checkMouseMove(int x, int y, int d)
	{
		if( !moved ){
			int dx, dy;
			dx=x-lastX; // horizontal shift
			dy=y-lastY; // vertical shift
			if( dx*dx + dy*dy > d ){
				executeMouseMove( getFacelet( lastX, lastY ), dx, dy );
				moved=true;
			}
		}
	}
	// return facelet number belonging to coordinate x,y
	protected int getFacelet( int x, int y )
	{
		for( int k=0; k<54; k++){
			if( faceletsO[k].contains(x,y) ){
				return(k);
			}
		}
		return(-1);
	}
	// perform move of facelet f in direction x,y
	private void executeMouseMove( int f, int x, int y )
	{
		int d=-1,m;
		if( x>y && x>-y ) d=0;
		else if( x<y && x<-y ) d=2;
		else if( y>x && y>-x ) d=1;
		else if( y<x && y<-x ) d=3;
		if(d>=0 && f>=0){
			m=moves[d][f];
			if( m<0 ){
				tryMove( -m-1, -1 );
			}else{
				tryMove( m-1, 1 );
			}
		}
	}
}
