import java.awt.*;
import java.awt.event.*;


final class ViewerDiag extends Viewer
{
//--- Display routines ---

	// graphical representation data
	Polygon faceletsI[] = new Polygon[54];
	Polygon faceletsO[] = new Polygon[54];
	Polygon faceOriMarks[][] = new Polygon[54][4];
	Polygon cubeBack[] = new Polygon[4];

	public ViewerDiag(int x, int y, ActionListener m){
		super(x,y,m);
		int i,c;
		int FACEH,CELLH,FACEV,CELLV,FACEL,CELLL, OFFSETX, OFFSETY;
		// vert : 8.5*CELLV+5.5*CELLL == 21.15*CELLV
		// FACEL/FACEV=2.3,  vert=2*FACEL+3*FACEV
		// FACEH/FACEV=2
		CELLV = (int)(y/21.15);
		i = x/22; if(i<CELLV) CELLV=i;
		CELLH=CELLV*2;
		CELLL=(int)(2.3*CELLV);
		FACEH=3*CELLH;
		FACEL=3*CELLL;
		FACEV=3*CELLV;

		OFFSETX = (x-11*CELLH)/2;
		OFFSETY = (y+y-11*CELLL-17*CELLV)/4;
		for( c=0; c<54; c++){
			faceletsI[c] = new Polygon();
			faceletsO[c] = new Polygon();
			for(i=0; i<4;i++) faceOriMarks[c][i] = new Polygon();
		}
		double b = 7./FACEL;

		// get the correct shape of each face
		dopoly( FACEH          , 0,                       -FACEH,FACEV,       0,FACEL,  0,b);//L
		dopoly( FACEH*2-CELLH/2, FACEL-FACEV-(CELLL-CELLV)/2,      FACEH,FACEV,  -FACEH,FACEV,  9,b);//U
		dopoly( FACEH  -CELLH/2, FACEL-(CELLL-CELLV)/2,            FACEH,FACEV,       0,FACEL, 18,b);//F
		dopoly( FACEH*2-CELLH/2, FACEL+FACEV-(CELLL-CELLV)/2,      FACEH,-FACEV,      0,FACEL, 27,b);//R
		dopoly( FACEH  -CELLH/2, FACEV*2+FACEL*2-(CELLL+CELLV)/2,    FACEH,FACEV,   FACEH,-FACEV,36,b);//D
		dopoly( FACEH*4-CELLH  , FACEV,                   -FACEH,-FACEV,      0,FACEL, 45,b);//B

		// shift into position
		for( c=0; c<54; c++){
			faceletsI[c].translate(OFFSETX,OFFSETY);
			faceletsO[c].translate(OFFSETX,OFFSETY);
			for(i=0; i<4;i++) faceOriMarks[c][i].translate(OFFSETX,OFFSETY);
		}

		// get background of the cube, the areas to be filled black.
		for( c=0; c<4; c++) cubeBack[c]=new Polygon();

		cubeBack[0].addPoint(0,0);// FUR
		cubeBack[0].addPoint(0,FACEL);
		cubeBack[0].addPoint(FACEH,FACEL+FACEV);
		cubeBack[0].addPoint(FACEH*2,FACEL);
		cubeBack[0].addPoint(FACEH*2,0);
		cubeBack[0].addPoint(FACEH,-FACEV);
		cubeBack[0].translate(OFFSETX+FACEH-CELLH/2,OFFSETY+FACEL-(CELLL-CELLV)/2);

		cubeBack[1].addPoint(FACEH,0);// L
		cubeBack[1].addPoint(0,FACEV);
		cubeBack[1].addPoint(0,FACEV+FACEL);
		cubeBack[1].addPoint(FACEH,FACEL);
		cubeBack[1].translate(OFFSETX,OFFSETY);

		cubeBack[2].addPoint(0,-FACEV);// D
		cubeBack[2].addPoint(-FACEH,0);
		cubeBack[2].addPoint(0,FACEV);
		cubeBack[2].addPoint(FACEH,0);
		cubeBack[2].translate(OFFSETX+FACEH*2-CELLH/2,OFFSETY+FACEL*2+FACEV*2-(CELLL+CELLV)/2);

		cubeBack[3].addPoint(0,FACEL);// B
		cubeBack[3].addPoint(FACEH,FACEL+FACEV);
		cubeBack[3].addPoint(FACEH,FACEV);
		cubeBack[3].addPoint(0,0);
		cubeBack[3].translate(OFFSETX+FACEH*3-CELLH,OFFSETY);


	}
	private void dopoly( int x0, int y0, int dx1, int dy1, int dx2, int dy2, int c0, double b){
	   int c = c0;
		for(int j=0; j<3; j++) for(int i=0; i<3; i++) {
			// build outer facelet
			faceletsO[c].addPoint( i   *dx1/3+ j   *dx2/3, i   *dy1/3+ j   *dy2/3);
			faceletsO[c].addPoint((i+1)*dx1/3+ j   *dx2/3,(i+1)*dy1/3+ j   *dy2/3);
			faceletsO[c].addPoint((i+1)*dx1/3+(j+1)*dx2/3,(i+1)*dy1/3+(j+1)*dy2/3);
			faceletsO[c].addPoint( i   *dx1/3+(j+1)*dx2/3, i   *dy1/3+(j+1)*dy2/3);
			faceletsO[c].translate(x0,y0);
			// build inner facelet (i.e. sticker)
			faceletsI[c].addPoint((int)(( b+i  )*dx1/3+( b+j  )*dx2/3),(int)(( b+i  )*dy1/3+( b+j  )*dy2/3));
			faceletsI[c].addPoint((int)((-b+i+1)*dx1/3+( b+j  )*dx2/3),(int)((-b+i+1)*dy1/3+( b+j  )*dy2/3));
			faceletsI[c].addPoint((int)((-b+i+1)*dx1/3+(-b+j+1)*dx2/3),(int)((-b+i+1)*dy1/3+(-b+j+1)*dy2/3));
			faceletsI[c].addPoint((int)(( b+i  )*dx1/3+(-b+j+1)*dx2/3),(int)(( b+i  )*dy1/3+(-b+j+1)*dy2/3));
			faceletsI[c].translate(x0,y0);
			// build top marker
			faceOriMarks[c][0].addPoint((int)(( i+b/2+.25 )*dx1/3+( j    )*dx2/3),(int)(( i+b/2+.25 )*dy1/3+( j    )*dy2/3));
			faceOriMarks[c][0].addPoint((int)(( i-b/2+.75 )*dx1/3+( j    )*dx2/3),(int)(( i-b/2+.75 )*dy1/3+( j    )*dy2/3));
			faceOriMarks[c][0].addPoint((int)(( i-b/2+.75 )*dx1/3+( j+.5 )*dx2/3),(int)(( i-b/2+.75 )*dy1/3+( j+.5 )*dy2/3));
			faceOriMarks[c][0].addPoint((int)(( i+b/2+.25 )*dx1/3+( j+.5 )*dx2/3),(int)(( i+b/2+.25 )*dy1/3+( j+.5 )*dy2/3));
			faceOriMarks[c][0].translate(x0,y0);
			// build right marker
			faceOriMarks[c][3].addPoint((int)(( i+.5 )*dx1/3+( j+b/2+.25 )*dx2/3),(int)(( i+.5 )*dy1/3+( j+b/2+.25 )*dy2/3));
			faceOriMarks[c][3].addPoint((int)(( i+1  )*dx1/3+( j+b/2+.25 )*dx2/3),(int)(( i+1  )*dy1/3+( j+b/2+.25 )*dy2/3));
			faceOriMarks[c][3].addPoint((int)(( i+1  )*dx1/3+( j-b/2+.75 )*dx2/3),(int)(( i+1  )*dy1/3+( j-b/2+.75 )*dy2/3));
			faceOriMarks[c][3].addPoint((int)(( i+.5 )*dx1/3+( j-b/2+.75 )*dx2/3),(int)(( i+.5 )*dy1/3+( j-b/2+.75 )*dy2/3));
			faceOriMarks[c][3].translate(x0,y0);
			// build bottom marker
			faceOriMarks[c][2].addPoint((int)(( i+b/2+.25 )*dx1/3+( j+.5 )*dx2/3),(int)(( i+b/2+.25 )*dy1/3+( j+.5 )*dy2/3));
			faceOriMarks[c][2].addPoint((int)(( i-b/2+.75 )*dx1/3+( j+.5 )*dx2/3),(int)(( i-b/2+.75 )*dy1/3+( j+.5 )*dy2/3));
			faceOriMarks[c][2].addPoint((int)(( i-b/2+.75 )*dx1/3+( j+1  )*dx2/3),(int)(( i-b/2+.75 )*dy1/3+( j+1  )*dy2/3));
			faceOriMarks[c][2].addPoint((int)(( i+b/2+.25 )*dx1/3+( j+1  )*dx2/3),(int)(( i+b/2+.25 )*dy1/3+( j+1  )*dy2/3));
			faceOriMarks[c][2].translate(x0,y0);
			// build left marker
			faceOriMarks[c][1].addPoint((int)(( i    )*dx1/3+( j+b/2+.25 )*dx2/3),(int)(( i    )*dy1/3+( j+b/2+.25 )*dy2/3));
			faceOriMarks[c][1].addPoint((int)(( i+.5 )*dx1/3+( j+b/2+.25 )*dx2/3),(int)(( i+.5 )*dy1/3+( j+b/2+.25 )*dy2/3));
			faceOriMarks[c][1].addPoint((int)(( i+.5 )*dx1/3+( j-b/2+.75 )*dx2/3),(int)(( i+.5 )*dy1/3+( j-b/2+.75 )*dy2/3));
			faceOriMarks[c][1].addPoint((int)(( i    )*dx1/3+( j-b/2+.75 )*dx2/3),(int)(( i    )*dy1/3+( j-b/2+.75 )*dy2/3));
			faceOriMarks[c][1].translate(x0,y0);

			c++;
		}
	}

	public void paint(Graphics g)
	{
		int i;
		if( offImage == null ){ initialise(); }
		offGraphics.setColor(getBackground()); // Clear drawing buffer
		offGraphics.fillRect(0,0,width,height);
		// draw cube
        Cubie.settings.cubePos.getFaceletColors();

		offGraphics.setColor(baseColor); // draw facelet base
		for( i=1; i<4; i++)
			offGraphics.fillPolygon(cubeBack[i]);

		for( i=0; i<9; i++){ // do L face
			offGraphics.setColor(colors[Cubie.settings.cubePos.cubeletPerm[20+Cubie.settings.cubePos.faceletColor[i]]-20]); // draw facelet
			offGraphics.fillPolygon(faceletsI[i]);
			if( Cubie.settings.superGroup ){
				offGraphics.setColor(baseColor);
				offGraphics.fillPolygon(faceOriMarks[i][Cubie.settings.cubePos.faceletOri[i]]);
			}
		}
		for( i=36; i<54; i++){ // do R,D faces
			offGraphics.setColor(colors[Cubie.settings.cubePos.cubeletPerm[20+Cubie.settings.cubePos.faceletColor[i]]-20]); // draw facelet
			offGraphics.fillPolygon(faceletsI[i]);
			if( Cubie.settings.superGroup ){
				offGraphics.setColor(baseColor);
				offGraphics.fillPolygon(faceOriMarks[i][Cubie.settings.cubePos.faceletOri[i]]);
			}
		}

		offGraphics.setColor(baseColor); // draw cube base
		offGraphics.fillPolygon(cubeBack[0]);

		for( i=9; i<36; i++){ // do R,D faces
			offGraphics.setColor(colors[Cubie.settings.cubePos.cubeletPerm[20+Cubie.settings.cubePos.faceletColor[i]]-20]); // draw facelet
			offGraphics.fillPolygon(faceletsI[i]);
			if( Cubie.settings.superGroup ){
				offGraphics.setColor(baseColor);
				offGraphics.fillPolygon(faceOriMarks[i][Cubie.settings.cubePos.faceletOri[i]]);
			}
		}

		g.drawImage(offImage,0,0,this);
	}

//--- mouse routines ---
	// conversion from facelet mouse move to internal move number
	final int moves[][] = {
		{-2,-2,-2,-8,-11,-8, 5, 5, 5,   -6,-6,-6, 9, 12, 9, 3, 3, 3,   -2,-2,-2,-8,-11,-8, 5, 5, 5,    2, 2, 2, 8, 11, 8,-5,-5,-5,    -3,-3,-3,-9,-12,-9, 6, 6, 6,     2, 2, 2, 8, 11, 8,-5,-5,-5},
		{ 6,-9,-3, 6,-12,-3, 6,-9,-3,   -1,-7, 4,-1,-10, 4,-1,-7, 4,    1, 7,-4, 1, 10,-4, 1, 7,-4,    3, 9,-6, 3, 12,-6, 3, 9,-6,     1, 7,-4, 1, 10,-4, 1, 7,-4,     4,-7,-1, 4,-10,-1, 4,-7,-1},
		{ 2, 2, 2, 8, 11, 8,-5,-5,-5,    6, 6, 6,-9,-12,-9,-3,-3,-3,    2, 2, 2, 8, 11, 8,-5,-5,-5,   -2,-2,-2,-8,-11,-8, 5, 5, 5,     3, 3, 3, 9, 12, 9,-6,-6,-6,    -2,-2,-2,-8,-11,-8, 5, 5, 5},
		{-6, 9, 3,-6, 12, 3,-6, 9, 3,    1, 7,-4, 1, 10,-4, 1, 7,-4,   -1,-7, 4,-1,-10, 4,-1,-7, 4,   -3,-9, 6,-3,-12, 6,-3,-9, 6,    -1,-7, 4,-1,-10, 4,-1,-7, 4,    -4, 7, 1,-4, 10, 1,-4, 7, 1}
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
		int k;
		for( k=9; k<54; k++){
			if( faceletsO[k].contains(x,y) ) return(k);
		}
		for( k=0; k<9; k++){
			if( faceletsO[k].contains(x,y) ) return(k);
		}
		return(-1);
	}
	// perform move of facelet f in direction x,y
	private void executeMouseMove( int f, int x, int y )
	{
		int d=-1,m;
		if( (f>=9 && f<18) || (f>=36 && f<45) ){	// U/D facelet
			if( x>0 && y>0 ) d=0;
			else if( x<0 && y<0 ) d=2;
			else if( x>0 && y<0 ) d=1;
			else if( x<0 && y>0 ) d=3;
		}else if( (f>=18 && f<27) || (f>=45 && f<54) ){	// F/B facelet
			if     ( x+y+y>0 && 3*x>2*y ) d=0;
			else if( x+y+y<0 && 3*x<2*y ) d=2;
			else if( x+y+y>0 && 3*x<2*y ) d=1;
			else if( x+y+y<0 && 3*x>2*y ) d=3;
		}else if( (f>=0 && f<9) || (f>=27 && f<36) ){	// L/R facelet
			if     ( y+y>x && 2*y+3*x<0 ) d=0;
			else if( y+y<x && 2*y+3*x>0 ) d=2;
			else if( y+y>x && 2*y+3*x>0 ) d=1;
			else if( y+y<x && 2*y+3*x<0 ) d=3;
		}
		if(d>=0 && f>=0){
			m = moves[d][f];
			if( m<0 ){
				tryMove( -m-1, -1 );
			}else{
				tryMove( m-1, 1 );
			}
		}
	}
}
