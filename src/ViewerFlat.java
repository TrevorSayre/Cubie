import java.awt.*;
import java.awt.event.*;


final class ViewerFlat extends Viewer
{
//--- Display routines ---

	// graphical representation data
	int FACE, CELL, BORD, OFFSETX, OFFSETY;
	int facecoordX[];
	int facecoordY[];
	public ViewerFlat(int x, int y, ActionListener m){
		super(x,y,m);
		CELL = x/12;
		FACE = y/9;
		if(FACE<CELL) CELL=FACE;
		FACE = CELL*3;
		BORD = 2;
		OFFSETX = (x-4*FACE)/2;
		OFFSETY = (y-3*FACE)/2;
		facecoordX = new int[6];
		facecoordY = new int[6];
		facecoordX[0] = OFFSETX + 0;
		facecoordY[0] = OFFSETY + FACE;
		facecoordX[1] = OFFSETX + FACE;
		facecoordY[1] = OFFSETY + 0;
		facecoordX[2] = OFFSETX + FACE;
		facecoordY[2] = OFFSETY + FACE;
		facecoordX[3] = OFFSETX + 2*FACE;
		facecoordY[3] = OFFSETY + FACE;
		facecoordX[4] = OFFSETX + FACE;
		facecoordY[4] = OFFSETY + 2*FACE;
		facecoordX[5] = OFFSETX + 3*FACE;
		facecoordY[5] = OFFSETY + FACE;
	}

	public void paint(Graphics g)
	{
		int i,j,k,c;
		if( offImage == null ){ initialise(); }
		offGraphics.setColor(getBackground()); // Clear drawing buffer
		offGraphics.fillRect(0,0,width,height);
		// draw cube
        Cubie.settings.cubePos.getFaceletColors();
		c=0;
		for( k=0; k<6; k++){	//each face
			offGraphics.setColor(baseColor);	// first draw cube colour
			offGraphics.fillRect(facecoordX[k],facecoordY[k],FACE,FACE);
			for( i=0; i<3; i++){	// each row
				for( j=0; j<3; j++){	// each facelet
					offGraphics.setColor(colors[Cubie.settings.cubePos.cubeletPerm[20+Cubie.settings.cubePos.faceletColor[c]]-20]); // draw facelet
					offGraphics.fillRect(facecoordX[k]+BORD+j*CELL,
										facecoordY[k]+BORD+i*CELL,
										CELL-2*BORD, CELL-2*BORD);
					if( Cubie.settings.superGroup ){
						offGraphics.setColor(baseColor);
						if( Cubie.settings.cubePos.faceletOri[c]==0 ){
							offGraphics.fillRect(facecoordX[k]+BORD+j*CELL+(CELL-BORD*2)/4,
												facecoordY[k]+BORD+i*CELL,
												CELL/2-BORD, CELL/2-BORD);
						}else if( Cubie.settings.cubePos.faceletOri[c]==3 ){
							offGraphics.fillRect(facecoordX[k]+j*CELL+CELL/2,
												facecoordY[k]+BORD+i*CELL+(CELL-BORD*2)/4,
												CELL/2-BORD, CELL/2-BORD);
						}else if( Cubie.settings.cubePos.faceletOri[c]==2 ){
							offGraphics.fillRect(facecoordX[k]+BORD+j*CELL+(CELL-BORD*2)/4,
												facecoordY[k]+i*CELL+CELL/2,
												CELL/2-BORD, CELL/2-BORD);
						}else if( Cubie.settings.cubePos.faceletOri[c]==1 ){
							offGraphics.fillRect(facecoordX[k]+j*CELL+BORD,
												facecoordY[k]+BORD+i*CELL+(CELL-BORD*2)/4,
												CELL/2-BORD, CELL/2-BORD);
						}
					}
					c++;
				}
			}
		}
		g.drawImage(offImage,0,0,this);
	}

//--- mouse routines ---
	// conversion from facelet mouse move to internal move number
	final int moves[][] = {
		{-2,-2,-2,-8,-11,-8, 5, 5, 5,   -6,-6,-6, 9, 12, 9, 3, 3, 3,   -2,-2,-2,-8,-11,-8, 5, 5, 5,   -2,-2,-2,-8,-11,-8, 5, 5, 5,    -3,-3,-3,-9,-12,-9, 6, 6, 6,    -2,-2,-2,-8,-11,-8, 5, 5, 5},
		{ 6,-9,-3, 6,-12,-3, 6,-9,-3,    1, 7,-4, 1, 10,-4, 1, 7,-4,    1, 7,-4, 1, 10,-4, 1, 7,-4,    3, 9,-6, 3, 12,-6, 3, 9,-6,     1, 7,-4, 1, 10,-4, 1, 7,-4,     4,-7,-1, 4,-10,-1, 4,-7,-1},
		{ 2, 2, 2, 8, 11, 8,-5,-5,-5,    6, 6, 6,-9,-12,-9,-3,-3,-3,    2, 2, 2, 8, 11, 8,-5,-5,-5,    2, 2, 2, 8, 11, 8,-5,-5,-5,     3, 3, 3, 9, 12, 9,-6,-6,-6,     2, 2, 2, 8, 11, 8,-5,-5,-5},
		{-6, 9, 3,-6, 12, 3,-6, 9, 3,   -1,-7, 4,-1,-10, 4,-1,-7, 4,   -1,-7, 4,-1,-10, 4,-1,-7, 4,   -3,-9, 6,-3,-12, 6,-3,-9, 6,    -1,-7, 4,-1,-10, 4,-1,-7, 4,    -4, 7, 1,-4, 10, 1,-4, 7, 1}
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
		int k,f;
		for( k=0; k<6; k++){
			if( x>=facecoordX[k] && x<facecoordX[k]+FACE &&
				y>=facecoordY[k] && y<facecoordY[k]+FACE )
			{
				f= k*9+ 3* ( (y-facecoordY[k])/CELL ) + ( (x-facecoordX[k])/CELL );
				return(f);
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
			m = moves[d][f];
			if( m<0 ){
				tryMove( -m-1, -1 );
			}else{
				tryMove( m-1, 1 );
			}
		}
	}
}
