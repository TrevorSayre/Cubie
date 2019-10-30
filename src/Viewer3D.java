import java.awt.*;
import java.awt.event.*;


final class Viewer3D extends Viewer
	implements Runnable
{
//--- Display routines ---

	// graphical representation data
	int OFFSETX, OFFSETY;
	int radius;
	Color fgColor[][] = new Color[8][20];
	// coordinate system wrt users view
	float eye[][]=new float[3][3];
	// orthogonal axes vectors
	final float coordVec[][]={
		{1,0,0},{0,1,0},{0,0,1},
		{-1,0,0},{0,-1,0},{0,0,-1},
	};
	// rotated orthogonal axes for animation
	final int numAnimSteps=16;
	float animVec[][][][]=new float[6][numAnimSteps+numAnimSteps+1][3][3];
	// facelets (in LUFRDB order) of each cubelet.
	final int facelets[][]={
		{ 0, 9,-1,-1,-1,47 }, //ULB
		{-1,10,-1,-1,-1,46 }, //UB
		{-1,11,-1,29,-1,45 }, //UR
		{ 1,12,-1,-1,-1,-1 }, //UL
		{-1,13,-1,-1,-1,-1 }, //U
		{-1,14,-1,28,-1,-1 }, //UR
		{ 2,15,18,-1,-1,-1 }, //ULF
		{-1,16,19,-1,-1,-1 }, //UF
		{-1,17,20,27,-1,-1 }, //UFR

		{ 3,-1,-1,-1,-1,50 },
		{-1,-1,-1,-1,-1,49 },
		{-1,-1,-1,32,-1,48 },
		{ 4,-1,-1,-1,-1,-1 },
		{-1,-1,-1,-1,-1,-1 },
		{-1,-1,-1,31,-1,-1 },
		{ 5,-1,21,-1,-1,-1 },
		{-1,-1,22,-1,-1,-1 },
		{-1,-1,23,30,-1,-1 },

		{ 6,-1,-1,-1,42,53 },
		{-1,-1,-1,-1,43,52 },
		{-1,-1,-1,35,44,51 },
		{ 7,-1,-1,-1,39,-1 },
		{-1,-1,-1,-1,40,-1 },
		{-1,-1,-1,34,41,-1 },
		{ 8,-1,24,-1,36,-1 },
		{-1,-1,25,-1,37,-1 },
		{-1,-1,26,33,38,-1 }
	};
	Polygon faceletsO[] = new Polygon[54];
	float moveDirs[][] = new float[54][4];
	boolean eventType = true;

	public Viewer3D(int x, int y, ActionListener m){
		super(x,y,m);
		int i,j;
		OFFSETX = x/2;
		OFFSETY = y/2;
		radius=x*3/16;
		if( y<x ) radius=y*3/16;
		for( j=0; j<20; j++){
			i=(40-j)*j/20;
			fgColor[0][j] = new Color(50+10*i,0,0);	//Red
			fgColor[1][j] = new Color(0,0,50+10*i);//blue
			fgColor[2][j] = new Color(50+10*i,50+10*i,0);//yellow
			fgColor[3][j] = new Color(50+10*i,30+5*i,10+2*i);//orange
			fgColor[4][j] = new Color(0,50+6*i,0);//green
			fgColor[5][j] = new Color(50+10*i,50+10*i,50+10*i);//white
			fgColor[6][j] = new Color(2*i,2*i,2*i);
			fgColor[7][j] = new Color(25+2*i,25+2*i,25+2*i);
		}

		// calculate rotated vectors

		for( i=0; i<=numAnimSteps; i++){
			animVec[3][i][1][0]=0;
			animVec[3][i][1][1]=numAnimSteps-i;
			animVec[3][i][1][2]=-i;
			animVec[3][i+numAnimSteps][1][0]=0;
			animVec[3][i+numAnimSteps][1][1]=-i;
			animVec[3][i+numAnimSteps][1][2]=-numAnimSteps+i;

			animVec[4][i][0][0]=numAnimSteps-i;
			animVec[4][i][0][1]=0;
			animVec[4][i][0][2]=i;
			animVec[4][i+numAnimSteps][0][0]=-i;
			animVec[4][i+numAnimSteps][0][1]=0;
			animVec[4][i+numAnimSteps][0][2]=numAnimSteps-i;

			animVec[2][i][0][0]=numAnimSteps-i;
			animVec[2][i][0][1]=-i;
			animVec[2][i][0][2]=0;
			animVec[2][i+numAnimSteps][0][0]=-i;
			animVec[2][i+numAnimSteps][0][1]=-numAnimSteps+i;
			animVec[2][i+numAnimSteps][0][2]=0;

			animVec[0][i][1][0]=0;
			animVec[0][i][1][1]=numAnimSteps-i;
			animVec[0][i][1][2]=i;
			animVec[0][i+numAnimSteps][1][0]=0;
			animVec[0][i+numAnimSteps][1][1]=-i;
			animVec[0][i+numAnimSteps][1][2]=numAnimSteps-i;

			animVec[1][i][0][0]=numAnimSteps-i;
			animVec[1][i][0][1]=0;
			animVec[1][i][0][2]=-i;
			animVec[1][i+numAnimSteps][0][0]=-i;
			animVec[1][i+numAnimSteps][0][1]=0;
			animVec[1][i+numAnimSteps][0][2]=-numAnimSteps+i;

			animVec[5][i][0][0]=numAnimSteps-i;
			animVec[5][i][0][1]=i;
			animVec[5][i][0][2]=0;
			animVec[5][i+numAnimSteps][0][0]=-i;
			animVec[5][i+numAnimSteps][0][1]=numAnimSteps-i;
			animVec[5][i+numAnimSteps][0][2]=0;
		}
		for( i=0; i<=numAnimSteps+numAnimSteps; i++){
			normalize(animVec[0][i][1]);
			animVec[0][i][0][0]=1;
			animVec[0][i][0][1]=0;
			animVec[0][i][0][2]=0;
			vecProd( animVec[0][i][2],animVec[0][i][0],animVec[0][i][1] );

			normalize(animVec[1][i][0]);
			animVec[1][i][1][0]=0;
			animVec[1][i][1][1]=1;
			animVec[1][i][1][2]=0;
			vecProd( animVec[1][i][2],animVec[1][i][0],animVec[1][i][1] );

			normalize(animVec[2][i][0]);
			animVec[2][i][2][0]=0;
			animVec[2][i][2][1]=0;
			animVec[2][i][2][2]=1;
			vecProd( animVec[2][i][1],animVec[2][i][2],animVec[2][i][0] );

			normalize(animVec[3][i][1]);
			animVec[3][i][0][0]=1;
			animVec[3][i][0][1]=0;
			animVec[3][i][0][2]=0;
			vecProd( animVec[3][i][2],animVec[3][i][0],animVec[3][i][1] );

			normalize(animVec[4][i][0]);
			animVec[4][i][1][0]=0;
			animVec[4][i][1][1]=1;
			animVec[4][i][1][2]=0;
			vecProd( animVec[4][i][2],animVec[4][i][0],animVec[4][i][1] );

			normalize(animVec[5][i][0]);
			animVec[5][i][2][0]=0;
			animVec[5][i][2][1]=0;
			animVec[5][i][2][2]=1;
			vecProd( animVec[5][i][1],animVec[5][i][2],animVec[5][i][0] );
		}
		reset();
	}

	public void reset()
	{
		eye[0][0]= 0.9f;
		eye[0][1]= 0;
		eye[0][2]=-0.4359f;
		eye[1][0]= 0.19f;
		eye[1][1]= 0.9f;
		eye[1][2]= 0.3923f;
		vecProd(eye[2],eye[0],eye[1]);
		repaint();
	}

	boolean twisting = false;
	int twistMove, twistDir;
	public void run(){
		// run animation
		animCube(twistMove,twistDir);
		// do actual move
		Cubie.settings.cubePos.doMove(twistMove,twistDir,false);
		repaint();
		doEvent(eventType);
		eventType=true;
		twisting=false;
	}

	float animEye[][][] = new float[3][3][3];
	int anim=0;
	int axisOrder[]={0,1,2};
	public void animCube(int m,int q0)
   {
	   int q = q0;
		int i,j,k,d;
		int animDir[]={0,0,0};
		int animStep[]={0,0,0};
		int animAmount[]={0,0,0};
		d=0;
		if( q<0) {
			q=-q;
			d=3;
		}
		//initialise animation variables
		for(i=0; i<3; i++){
			for(j=0; j<3; j++)
				for(k=0; k<3; k++)
					animEye[i][j][k]=eye[j][k];
			animAmount[i]=animStep[i]=animDir[i]=0;
		}

		// m=6-8, middle layer turn
		// m=9-11, cube turn
		// m=12-14, slice move
		// m=15-17, anti-slice move

		if( m<3 ) {// face move LUF
			animStep[0]= q;
			animDir[0] = m+d;
			axisOrder[0]=m;
		}else if( m<6 ){// face move RDB
			animStep[2]= q;
			animDir[2]=  m-d;
			axisOrder[0]=m-3;
		}else if( m<9 ){// midslice move
			animStep[1]= q;
			animDir[1]=  m-6+d;
			axisOrder[0]=m-6;
		}else if( m<12 ){// whole cube move
			axisOrder[0]=m-9;
			animStep[0]=animStep[1]=animStep[2]=q;
			animDir[0]=animDir[1]=animDir[2]=m-9+d;
		}else if( m<15 ){// slice move
			axisOrder[0]=m-12;
			animStep[0]=animStep[2]=q;
			animDir[0]=m-12+d;
			animDir[2]=m-12+d;
		}else if( m<18 ){// anti-slice move
			axisOrder[0]=m-15;
			animStep[0]=animStep[2]=q;
			animDir[0]=m-15+d;
			animDir[2]=m-15+3-d;
		}

		// set order of axes - main axis first
		if( axisOrder[0]==0 ){
			axisOrder[0]=0; axisOrder[1]=1; axisOrder[2]=2;
		}else if( axisOrder[0]==1 ){
			axisOrder[0]=1; axisOrder[1]=2; axisOrder[2]=0;
		}else{
			axisOrder[0]=2; axisOrder[1]=0; axisOrder[2]=1;
			i=animStep[0];animStep[0]=animStep[2];animStep[2]=i;
			i=animDir[0];animDir[0]=animDir[2];animDir[2]=i;
		}

		for( j=1; j<numAnimSteps; j++){
			for( i=0; i<3; i++){ //for each layer
				//move a bit
				animAmount[i]+=animStep[i];
				//calculate animEye for this layer
				rotateVecs( animEye[i], animVec[animDir[i]][animAmount[i]] );
			}
			anim=j;
			repaint();
			try { Thread.sleep( 15 ); } catch ( Exception ignored ) {}
		}
		//if middle later has moved, the turn the eye to new position
		if( animStep[1]!=0 ){
			animAmount[1]+=animStep[1];
			rotateVecs( animEye[0], animVec[animDir[1]][animAmount[1]] );
			for( i=0; i<3; i++)
				for( j=0; j<3; j++)
					eye[i][j]=animEye[0][i][j];

		}
		anim=0;
	}



	float statEye[][][] = {eye,eye,eye};
	public void paint(Graphics g)
	{
		if( offImage == null ){ initialise(); }
		offGraphics.setColor(getBackground()); // Clear drawing buffer
		offGraphics.fillRect(0,0,width,height);
        Cubie.settings.cubePos.getFaceletColors();
		if( anim==0 ){
			drawCube(statEye);
		}else{
			drawCube(animEye);
		}
		// draw result
		g.drawImage(offImage,0,0,this);
	}

	// draw cube using the three views eyeVecs for three layers
	// along the axis axisOrder[0].
	private void drawCube( float eyeVecs[][][] )
	{
		int i,i0,i1,i2;
	int coord[]=new int[3];
	int axisDir[]=new int[3];
		// clear previous mouse areas
		for( i=0; i<54; i++) faceletsO[i]=null;

		// draw the 27 cubelets layer by layer

		// check axis direction of main axis
		if( eyeVecs[0][2][axisOrder[0]]>0 ){
			axisDir[axisOrder[0]]=1;
			coord[axisOrder[0]]=0;
		}else{
			axisDir[axisOrder[0]]=-1;
			coord[axisOrder[0]]=2;
		}

		// loop over all blocks to draw them
		for( i0=0; i0<3; i0++){  //for each layer
			// recalculate the axis directions for other two axes
			//   necessary since may differ by layer if in mid turn
			for( i=1; i<3; i++){
				if( eyeVecs[coord[axisOrder[0]]][2][axisOrder[i]]>0 ){
					axisDir[axisOrder[i]]=1;
					coord[axisOrder[i]]=0;
				}else{
					axisDir[axisOrder[i]]=-1;
					coord[axisOrder[i]]=2;
				}
			}

			// draw layer
			for( i1=0; i1<3; i1++){
				for( i2=0; i2<3; i2++){
					drawCubelet( coord, eyeVecs[coord[axisOrder[0]]] );
					coord[axisOrder[2]]+=axisDir[axisOrder[2]];
				}
				coord[axisOrder[2]]-=3*axisDir[axisOrder[2]];
				coord[axisOrder[1]]+=axisDir[axisOrder[1]];
			}
			coord[axisOrder[1]]-=3*axisDir[axisOrder[1]];
			coord[axisOrder[0]]+=axisDir[axisOrder[0]];
		}
	}

	// draw subcube (cx,cy,cz) under viewing direction vw[][]
	float ccorn[]=new float[3];
	private void drawCubelet( int cr[],float vw[][] )
	{
		int c=cr[0]+cr[1]*9+cr[2]*3;
		//if(c>3) return;
		ccorn[0]=cr[0]-1.5f;
		ccorn[1]=cr[1]-1.5f;
		ccorn[2]=cr[2]-1.5f;

		//draw right/left
		if( vw[2][0]>0 ){
			// draw right
			ccorn[0]++;ccorn[2]++;
			drawFacelet( ccorn, coordVec[1], coordVec[5], coordVec[0], vw, facelets[c][3] );
			ccorn[0]--;ccorn[2]--;
		}else{
			// draw left
			drawFacelet( ccorn, coordVec[1], coordVec[2], coordVec[0], vw, facelets[c][0] );
		}
		//draw top/bottom
		if( vw[2][1]>0 ){
			// draw bottom
			ccorn[1]++;ccorn[2]++;
			drawFacelet( ccorn, coordVec[5], coordVec[0], coordVec[1], vw, facelets[c][4] );
			ccorn[1]--;ccorn[2]--;
		}else{
			// draw top
			drawFacelet( ccorn, coordVec[2], coordVec[0], coordVec[1], vw, facelets[c][1] );
		}
		//draw front/back
		if( vw[2][2]>0 ){
			// draw front
			ccorn[2]++;
			drawFacelet( ccorn, coordVec[1], coordVec[0], coordVec[2], vw, facelets[c][2] );
			ccorn[2]--;
		}else{
			// draw back
			ccorn[0]++;
			drawFacelet( ccorn, coordVec[1], coordVec[3], coordVec[2], vw, facelets[c][5] );
			ccorn[0]--;
		}
	}

	float vr[]=new float[3];
	int rectx[]=new int[6];
	int recty[]=new int[6];
	private void drawFacelet( float corn[], float d1[], float d2[], float d0[],
								float vw[][], int fl )
	{
		int i;

		int br = (int)( dotProd(d0,vw[2])*20 );
		if( br<0 ) br=-br;
		if( br>19 ) br=19;

		for( i=0; i<4; i++){
			// get facelet corner coordinate
			vr[0]=corn[0];
			vr[1]=corn[1];
			vr[2]=corn[2];
			if( i==1 || i==2 ){
				vr[0]+=d1[0]; vr[1]+=d1[1]; vr[2]+=d1[2];
			}
			if( i>=2 ){
				vr[0]+=d2[0]; vr[1]+=d2[1]; vr[2]+=d2[2];
			}

			//project onto viewer plane
			rectx[i] = OFFSETX+(int)(dotProd( vr, vw[0])*radius);
			recty[i] = OFFSETY+(int)(dotProd( vr, vw[1])*radius);
		}
		offGraphics.setColor(fgColor[6][br]);
		offGraphics.fillPolygon( rectx, recty, 4 );


		int br2 = (int)( dotProd(d1,vw[2])*20 );
		if( br2<0 ) br2=-br2;
		if( br2>19 ) br2=19;
		offGraphics.setColor(fgColor[7][19-br2]);
		offGraphics.drawLine( rectx[0], recty[0], rectx[1], recty[1]);
		offGraphics.drawLine( rectx[2], recty[2], rectx[3], recty[3]);
		br2 = (int)( dotProd(d2,vw[2])*20 );
		if( br2<0 ) br2=-br2;
		if( br2>19 ) br2=19;
		offGraphics.setColor(fgColor[7][19-br2]);
		offGraphics.drawLine( rectx[3], recty[3], rectx[0], recty[0]);
		offGraphics.drawLine( rectx[1], recty[1], rectx[2], recty[2]);

		if( fl<0 ) return;	// done if was internal facelet

		// store facelet as polygon for processing mouse
		//   actions later
		faceletsO[fl] = new Polygon( rectx, recty, 4 );
		moveDirs[fl][0] = dotProd( d1, vw[0]);
		moveDirs[fl][1] = dotProd( d1, vw[1]);
		moveDirs[fl][2] = dotProd( d2, vw[0]);
		moveDirs[fl][3] = dotProd( d2, vw[1]);

		int cl = Cubie.settings.cubePos.cubeletPerm[20+Cubie.settings.cubePos.faceletColor[fl]]-20;

		for( i=0; i<4; i++){
			// get facelet corner coordinate
			vr[0]=corn[0]+0.1f*(d1[0]+d2[0]);
			vr[1]=corn[1]+0.1f*(d1[1]+d2[1]);
			vr[2]=corn[2]+0.1f*(d1[2]+d2[2]);
			if( i==1 || i==2 ){
				vr[0]+=.8f*d1[0]; vr[1]+=.8f*d1[1]; vr[2]+=.8f*d1[2];
			}
			if( i>=2 ){
				vr[0]+=.8f*d2[0]; vr[1]+=.8f*d2[1]; vr[2]+=.8f*d2[2];
			}

			//project onto viewer plane
			rectx[i] = OFFSETX+(int)(dotProd( vr, vw[0])*radius);
			recty[i] = OFFSETY+(int)(dotProd( vr, vw[1])*radius);
		}
		offGraphics.setColor(fgColor[cl][br]);
		offGraphics.fillPolygon( rectx, recty, 4 );

		if( Cubie.settings.superGroup ){
			cl = Cubie.settings.cubePos.faceletOri[fl];
			if( cl==0 ){
				addpnt( corn, d1, d2, vw, 0, 0.5f , 0.3f );
				addpnt( corn, d1, d2, vw, 1, 0.5f , 0.7f );
				addpnt( corn, d1, d2, vw, 2, 0.05f, 0.7f );
				addpnt( corn, d1, d2, vw, 3, 0.05f, 0.3f );
			}else if( cl==1 ){
				addpnt( corn, d1, d2, vw, 0, 0.3f, 0.5f  );
				addpnt( corn, d1, d2, vw, 1, 0.7f, 0.5f  );
				addpnt( corn, d1, d2, vw, 2, 0.7f, 0.05f );
				addpnt( corn, d1, d2, vw, 3, 0.3f, 0.05f );
			}else if( cl==2 ){
				addpnt( corn, d1, d2, vw, 0, 0.5f , 0.3f );
				addpnt( corn, d1, d2, vw, 1, 0.5f , 0.7f );
				addpnt( corn, d1, d2, vw, 2, 0.95f, 0.7f );
				addpnt( corn, d1, d2, vw, 3, 0.95f, 0.3f );
			}else if( cl==3 ){
				addpnt( corn, d1, d2, vw, 0, 0.3f, 0.5f  );
				addpnt( corn, d1, d2, vw, 1, 0.7f, 0.5f  );
				addpnt( corn, d1, d2, vw, 2, 0.7f, 0.95f );
				addpnt( corn, d1, d2, vw, 3, 0.3f, 0.95f );
			}
			offGraphics.setColor(fgColor[6][br]);
			offGraphics.fillPolygon( rectx, recty, 4 );
		}
	}
	private void addpnt( float corn[], float d1[], float d2[],
						float vw[][], int i, float x,float y )
	{
		// get facelet corner coordinate
		vr[0]=corn[0]+x*d1[0]+y*d2[0];
		vr[1]=corn[1]+x*d1[1]+y*d2[1];
		vr[2]=corn[2]+x*d1[2]+y*d2[2];
		//project onto viewer plane
		rectx[i] = OFFSETX+(int)(dotProd( vr, vw[0])*radius);
		recty[i] = OFFSETY+(int)(dotProd( vr, vw[1])*radius);
	}

	// normalize vetor, i.e. scale it to length 1
	private void normalize( float v[] ){
		float l=(float)Math.sqrt( v[0]*v[0]+v[1]*v[1]+v[2]*v[2] );
		v[0]/=l; v[1]/=l; v[2]/=l;
	}
	// dot (inner vector) product
	private float dotProd( float v[], float w[]){
		return( v[0]*w[0]+v[1]*w[1]+v[2]*w[2] );
	}
	// cross (outer vector) product
	private void vecProd( float u[], float v[], float w[]){
		u[0] = v[1]*w[2]-v[2]*w[1];
		u[1] = v[2]*w[0]-v[0]*w[2];
		u[2] = v[0]*w[1]-v[1]*w[0];
	}

	// Consider Eye coords along new axes v[] and return new eye in old coords
	private void rotateVecs( float res[][], float v[][] ){
		int i,j;
		for( i=0; i<3; i++){
			for( j=0; j<3; j++){
				res[i][j] = v[0][j]*eye[i][0]+v[1][j]*eye[i][1]+v[2][j]*eye[i][2];
			}
		}
	}


//--- mouse routines ---
	// conversion from facelet mouse move to internal move number
	final int moves[][] = {
		{-2,-2,-2,-8,-11,-8, 5, 5, 5,   -6,-6,-6, 9, 12, 9, 3, 3, 3,   -2,-2,-2,-8,-11,-8, 5, 5, 5,   -2,-2,-2,-8,-11,-8, 5, 5, 5,    -3,-3,-3,-9,-12,-9, 6, 6, 6,    -2,-2,-2,-8,-11,-8, 5, 5, 5},
		{-6, 9, 3,-6, 12, 3,-6, 9, 3,   -1,-7, 4,-1,-10, 4,-1,-7, 4,   -1,-7, 4,-1,-10, 4,-1,-7, 4,   -3,-9, 6,-3,-12, 6,-3,-9, 6,    -1,-7, 4,-1,-10, 4,-1,-7, 4,    -4, 7, 1,-4, 10, 1,-4, 7, 1},
		{ 2, 2, 2, 8, 11, 8,-5,-5,-5,    6, 6, 6,-9,-12,-9,-3,-3,-3,    2, 2, 2, 8, 11, 8,-5,-5,-5,    2, 2, 2, 8, 11, 8,-5,-5,-5,     3, 3, 3, 9, 12, 9,-6,-6,-6,     2, 2, 2, 8, 11, 8,-5,-5,-5},
		{ 6,-9,-3, 6,-12,-3, 6,-9,-3,    1, 7,-4, 1, 10,-4, 1, 7,-4,    1, 7,-4, 1, 10,-4, 1, 7,-4,    3, 9,-6, 3, 12,-6, 3, 9,-6,     1, 7,-4, 1, 10,-4, 1, 7,-4,     4,-7,-1, 4,-10,-1, 4,-7,-1}
	};

	// return facelet number belonging to coordinate x,y
	protected int getFacelet( int x, int y )
	{
		for( int k=0; k<54; k++){
			if( faceletsO[k]!=null && faceletsO[k].contains(x,y) ){
				return(k);
			}
		}
		return(-1);
	}
	// perform move of facelet f in direction x,y
	private void executeMouseMove( int f, int x, int y )
	{
		int d=-1,m;
		// compare x,y with the stored vectors
		if( f<0 ) return;
		float f1=x*moveDirs[f][1]-y*moveDirs[f][0];
		float f2=x*moveDirs[f][3]-y*moveDirs[f][2];
		if( f1>f2 && f1>-f2 ) d=0;
		else if( f1<f2 && f1<-f2 ) d=2;
		else if( f2>f1 && f2>-f1 ) d=1;
		else if( f2<f1 && f2<-f1 ) d=3;
		if(d>=0 && f>=0){
			m = moves[d][f];
			if( m<0 ){
				tryMove( -m-1, -1 );
			}else{
				tryMove( m-1, 1 );
			}
		}
	}

	void doMove(int m, int q){
		if( !Cubie.settings.lockViewer ) doMove2(m,q);
	}
	void doMove2(int m, int q){
		if( !twisting ){
			twisting=true;
			twistMove=m;
			twistDir=q;
			new Thread(this).start();
		}
	}
	public boolean showMove(int face, int q0)
	{
	   int qu = q0;
		if(twisting) return(false);
		if(qu>2) qu-=4;
		eventType=false;	// set flag to show this is not initiated by user
		doMove2(face,qu);
		return(true);
	}

	//Check if mouse move to coord x,y is far enough to trigger a move.
	protected void checkMouseMove(int x, int y, int d)
	{
		if( lastF<0 ){	// mousepress was not on a facelet
			// rotate whole cube
			int dx=x-lastX;
			int dy=y-lastY;
			lastX=x;
			lastY=y;
			float f = (float)dx/radius/2;
			eye[0][0]+=f*eye[2][0];
			eye[0][1]+=f*eye[2][1];
			eye[0][2]+=f*eye[2][2];
			normalize(eye[0]);
			vecProd(eye[2],eye[0],eye[1]);
			f=(float)dy/radius/2;
			eye[1][0]+=f*eye[2][0];
			eye[1][1]+=f*eye[2][1];
			eye[1][2]+=f*eye[2][2];
			normalize(eye[1]);
			vecProd(eye[2],eye[0],eye[1]);
			repaint();
		}else{ // haven't yet moved
			int dx, dy;
			dx=x-lastX; // horizontal shift
			dy=y-lastY; // vertical shift
			if( dx*dx + dy*dy > d ){
				executeMouseMove( getFacelet( lastX, lastY ), dx, dy );
				moved=true;
			}
		}
	}
}
