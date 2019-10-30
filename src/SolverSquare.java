// ----- Solver for the Square group ----
import java.awt.event.*;


final class SolverSquare extends Solver
{
	int transEdge[][][];
	int transCorn[][];
	int transCorn2[][];
	byte pruneEdge[][][];
	byte pruneCorn[];
	byte pruneCorn2[];
	int maxdepth;	// current search depth
	final int orbits[][]={
		// UR,DR,DL,UL  RB,RF,LF,LB  UF,DF,DB,UB
		{8,16,18,10},{12,13,14,15},{9,17,19,11},
		// UBR,UFL,DFR,DBL,  URF,ULB,DRB,DLF
		{0,2,5,7},{1,3,4,6}
	};

	public SolverSquare( ActionListener m ){ super(m); }

    public MoveSequence getGenerator()
    {
        int[] a=new int[sollen];
        for( int i=0; i<sollen; i++){
            a[i]=2;
        }
        return( new MoveSequence( sollen, solmoves, a ) );
    }

	public void mix( CubePosition cubePos )
	{
		int i,j;
		// direct method, not random moves
		cubePos.reset();
		// put random corner at position 2
		j=(int)(4*Math.random());
		if( j!=3 ) { cubePos.doMove(j);cubePos.doMove(j); }
		// put random corner at 5
		j=(int)(3+3*Math.random());
		if( j!=5 ) { cubePos.doMove(j);cubePos.doMove(j); }
		// put random corner at 0/7
		j=(int)(2*Math.random());
		if( j!=0 ) { cubePos.doMove(5);cubePos.doMove(5); }
		// randomly twist other tetrad
		j=(int)(4*Math.random());
		if( j==0 ) {
			cubePos.doMove(2);cubePos.doMove(2);
			cubePos.doMove(3);cubePos.doMove(3);
			cubePos.doMove(1);cubePos.doMove(1);
			cubePos.doMove(3);cubePos.doMove(3);
		}else if( j==1 ) {
			cubePos.doMove(3);cubePos.doMove(3);
			cubePos.doMove(1);cubePos.doMove(1);
			cubePos.doMove(2);cubePos.doMove(2);
			cubePos.doMove(1);cubePos.doMove(1);
		}else if( j==2 ) {
			cubePos.doMove(1);cubePos.doMove(1);
			cubePos.doMove(2);cubePos.doMove(2);
			cubePos.doMove(3);cubePos.doMove(3);
			cubePos.doMove(2);cubePos.doMove(2);
		}
		// corners are now in place.
		// randomly place edges
		int order[] = new int[4];
		for( i=0; i<3; i++){
			num2perm( order, 0,4, (int)(4*Math.random()) );
			for( j=0; j<4; j++){
				cubePos.cubeletPerm[orbits[i][j]]=orbits[i][order[j]];
			}
		}
		//ensure edges have even permutation
		if( parityOdd( cubePos.cubeletPerm, 8,12 ) ){
			// swap two edges to fix it.
			swap(cubePos.cubeletPerm, 8, 10);
		}

		//convert orbits
		int perm[]=new int[20];
		int k, c=0;
		for( i=0;i<5;i++ ){
			for( j=0; j<4; j++){
				for( k=0; k<4; k++){
					if( cubePos.cubeletPerm[orbits[i][j]] == orbits[i][k] ) break;
				}
				perm[c++]=k;
			}
		}

		// do face centres. First ensure solvable
		j=0;// 210:LUF
		for( i=0; i<6; i++) cubePos.cubeletOri[20+i]=0;
		if( parityOdd(perm,12,4) ) j^=7;
		if( parityOdd(perm,0,4) )  j^=6;
		if( parityOdd(perm,4,4) )  j^=5;
		if( parityOdd(perm,8,4) )  j^=3;
		if( (j&4)!=0 ) cubePos.cubeletOri[20]=2;
		if( (j&2)!=0 ) cubePos.cubeletOri[21]=2;
		if( (j&1)!=0 ) cubePos.cubeletOri[22]=2;

		// randomize face centres
		j=(int)(8*Math.random());
		if( (j&4)!=0 ) {
			cubePos.cubeletOri[20]^=2;
			cubePos.cubeletOri[23]^=2;
		}
		if( (j&2)!=0 ) {
			cubePos.cubeletOri[21]^=2;
			cubePos.cubeletOri[24]^=2;
		}
		if( (j&1)!=0 ) {
			cubePos.cubeletOri[22]^=2;
			cubePos.cubeletOri[25]^=2;
		}
	}

	public boolean setPosition( CubePosition cubePos, boolean test ){
		int i,j,k,c;
		int perm[]=new int[20];

		if( parityOdd( cubePos.cubeletPerm,0,20 ))return(false);

		// check orientations
		for(i=0;i<20;i++){
			if( cubePos.cubeletOri[i]!=0 ) return(false);
		}
		//check and convert orbits
		c=0;
		for( i=0;i<5;i++ ){
			for( j=0; j<4; j++){
				for( k=0; k<4; k++){
					if( cubePos.cubeletPerm[orbits[i][j]] == orbits[i][k] ) break;
				}
				if( k>=4 ) return(false);
				perm[c++]=k;
			}
		}
		// corner (and total edge) perm must be even
		if( parityOdd(perm,12,4)!=parityOdd(perm,16,4) ) return(false);

		// check face centres
		if( Cubie.settings.superGroup ){
			// must be half turns
			for(i=0; i<6; i++){
				if( (cubePos.cubeletOri[20+i]&1)!=0 ) return(false);
			}
		// UR,DR,DL,UL  RB,RF,LF,LB  UF,DF,DB,UB
			j=0;// 210:LUF
			if( parityOdd(perm,12,4) ) j^=7;
			if( parityOdd(perm,0,4) )  j^=6;
			if( parityOdd(perm,4,4) )  j^=5;
			if( parityOdd(perm,8,4) )  j^=3;
			if( cubePos.cubeletOri[20]!=cubePos.cubeletOri[23] ) j^=4;
			if( cubePos.cubeletOri[21]!=cubePos.cubeletOri[24] ) j^=2;
			if( cubePos.cubeletOri[22]!=cubePos.cubeletOri[25] ) j^=1;
			if( j!=0 ) return(false);
		}

		// convert to numbers
		int newpos[] = new int[4];
		newpos[0] = perm2num( perm, 0,4 );
		newpos[1] = perm2num( perm, 4,4 );
		newpos[2] = perm2num( perm, 8,4 );
		newpos[3] = perm2num( perm,12,4 );
		for( k=0; k<4; k++){
			if( cubePos.cubeletPerm[orbits[4][k]] == orbits[4][0] ) break;
		}
		newpos[3] = newpos[3]*4+k;
		if( Cubie.settings.superGroup ){
			j=0;
			if( cubePos.cubeletOri[20]!=0 ) j^=4;
			if( cubePos.cubeletOri[21]!=0 ) j^=2;
			if( cubePos.cubeletOri[22]!=0 ) j^=1;
			newpos[3]=(newpos[3]<<3)+j;
		}

		// do permutations to solve corners.

		//solve corner 0 of first orbit
		if( perm[13]==0 ){
			swap( perm, 12,13 );
			swap( perm, 14,15 );

		}else if( perm[14]==0 ){
			swap( perm, 12,14 );
			swap( perm, 13,15 );
		}else if( perm[15]==0 ){
			swap( perm, 12,15 );
			swap( perm, 13,14 );
		}
		//solve corner 1 of first orbit
		if( perm[14]==1 ){ //F
			swap( perm, 13,14 );
			swap( perm, 16,19 );
		}else if( perm[15]==1 ){ //L
			swap( perm, 13,15 );
			swap( perm, 17,19 );
		}
		//solve corners 2,3 of first orbit
		// UBR,UFL,DFR,DBL,  URF,ULB,DRB,DLF
		if( perm[15]==2 ){ //D
			swap( perm, 14,15 );
			swap( perm, 18,19 );
		}
		//solve corners 0 of second orbit
		// UBR,UFL,DFR,DBL,  URF,ULB,DRB,DLF
		if( perm[17]==0 ){
			swap( perm, 16,17 );
			swap( perm, 18,19 );
		}else if( perm[18]==0 ){
			swap( perm, 16,18 );
			swap( perm, 17,19 );
		}else if( perm[19]==0 ){
			swap( perm, 16,19 );
			swap( perm, 17,18 );
		}
		if( perm[17]!=1 || perm[18]!=2 || perm[19]!=3 ) return(false);

		if( test ) return(true);
		if( !prepared ) return(false);

		if( positionlist==null ){
			positionlist = new int[40][4];
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}else if( positionlist[0][0]!=newpos[0] ||
				positionlist[0][1]!=newpos[1] ||
				positionlist[0][2]!=newpos[2] ||
				positionlist[0][3]!=newpos[3] ){
			// clear out search history
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}
		// set position
		positionlist[0][0]=newpos[0];
		positionlist[0][1]=newpos[1];
		positionlist[0][2]=newpos[2];
		positionlist[0][3]=newpos[3];
		return(true);
	}
	protected boolean solve()
	{
		// do IDA* - search depth first for each maxdepth.
		while( !search() ){
			if( wanttostop ) return(false);
			maxdepth++;
		}
		return(true);
	}
	private boolean search()
	{
		//do a depth search through all positions at depth maxdepth.
		// return false if no solution, or break out with true if solved.
		int m,nxt;

		// do check for solved position with null solution.
		if( maxdepth==0 && sollen==0 && positionlist[0][0]==0 && positionlist[0][1]==0 && positionlist[0][2]==0 && positionlist[0][3]==0 ) return(true);

		while( sollen>=0 ){
			// at this point positionlist[0..sollen] are visited positions
			// and solmoves[0..sollen-1] are moves performed.
			// Note solmoves[sollen] is last tried (rejected) next move
			nxt=sollen+1;
			// add extra half turn to last move
			m = solmoves[sollen];
			if( m>=0 ){ // ignore dummy -1 move
				positionlist[nxt][0]= transEdge[0][positionlist[nxt][0]][m];
				positionlist[nxt][1]= transEdge[1][positionlist[nxt][1]][m];
				positionlist[nxt][2]= transEdge[2][positionlist[nxt][2]][m];
				if( Cubie.settings.superGroup ){
					positionlist[nxt][3]= transCorn2[positionlist[nxt][3]][m];
				}else{
					positionlist[nxt][3]= transCorn[positionlist[nxt][3]][m];
				}
			}else{
				positionlist[nxt][0]= positionlist[sollen][0];
				positionlist[nxt][1]= positionlist[sollen][1];
				positionlist[nxt][2]= positionlist[sollen][2];
				positionlist[nxt][3]= positionlist[sollen][3];
			}
			solamount[sollen]+=2;
			// if done full turn, then move to next face
			if( solamount[sollen]>3 ){
				// next face, 0 turns yet
				solamount[sollen]=0;
				do{
					solmoves[sollen]++;
				}while( sollen!=0 && ( solmoves[sollen]==solmoves[sollen-1] || solmoves[sollen]==solmoves[sollen-1]+3 ));
				if( solmoves[sollen]>=6 ){
					// done all faces. backtrack
					sollen--;
					continue;
				}
				continue; // loop back to do a quarter turn of this new face
			}

			// check pruning for proposed new position
			if( sollen+pruneEdge[positionlist[nxt][0]][positionlist[nxt][1]][positionlist[nxt][2]]<maxdepth+1 &&
				(  Cubie.settings.superGroup || sollen+pruneCorn[positionlist[nxt][3]]<maxdepth+1 ) &&
				( !Cubie.settings.superGroup || sollen+pruneCorn2[positionlist[nxt][3]]<maxdepth+1 )
				){
				//its ok, officially add to movelist
				// append dummy move to list for later extension.
				solmoves[nxt]=-1;
				solamount[nxt]=3;
				sollen=nxt;
				// check if have found solution of required length;
				if( sollen >= maxdepth ) return(true);
			}

			if( wanttostop ) return(false);
			//loop back to shift to next move sequence
		}
		// reset for next search
		solmoves[0]=-1;
		solamount[0]=3;
		sollen=0;
		return(false);
	}

	// initialise tables
	protected void init()
	{
		int i,k,m;
		transEdge = new int[3][24][6];
		transCorn = new int[96][6];
		transCorn2 = new int[768][6];
		pruneEdge = new byte[24][24][24];
		pruneCorn = new byte[96];
		pruneCorn2 = new byte[768];
		for( k=0; k<3; k++){	// each slice
			for( i=0; i<24; i++){	// each edge position
				for( m=0;m<6;m++ ){	//each move
					transEdge[k][i][m]=gettransEdge(k,i,m);
				}
			}
		}
		for( i=0; i<96; i++){	// each corner position
			for( m=0;m<6;m++ ){	//each move
				transCorn[i][m]=gettransCorn(i,m);
			}
		}
		for( i=0; i<768; i++){	// each corner position
			for( m=0;m<6;m++ ){	//each move
				transCorn2[i][m]=gettransCorn2(i,m);
			}
		}

		//calculate pruning tables. First corners
		int l=1;
		pruneCorn[0]=1;
		do{
			k=0;
			for(i=0;i<96;i++){
				if( pruneCorn[i]==l){
					for(m=0;m<6;m++){
						if( pruneCorn[transCorn[i][m]]==0){
							pruneCorn[transCorn[i][m]]=(byte)(l+1);
							k++;
						}
					}
				}
			}
			l++;
		}while(k!=0);
		//calculate pruning tables. First corners
		l=1;
		pruneCorn2[0]=1;
		do{
			k=0;
			for(i=0;i<768;i++){
				if( pruneCorn2[i]==l){
					for(m=0;m<6;m++){
						if( pruneCorn2[transCorn2[i][m]]==0){
							pruneCorn2[transCorn2[i][m]]=(byte)(l+1);
							k++;
						}
					}
				}
			}
			l++;
		}while(k!=0);

		//Now edge pruning table
		l=1;
		int i2,i3;
		pruneEdge[0][0][0]=1;
		do{
			k=0;
			for(i=0;i<24;i++){
			for(i2=0;i2<24;i2++){
			for(i3=0;i3<24;i3++){
				if( pruneEdge[i][i2][i3]==l){
					for(m=0;m<6;m++){
						if( pruneEdge[transEdge[0][i][m]][transEdge[1][i2][m]][transEdge[2][i3][m]]==0){
							pruneEdge[transEdge[0][i][m]][transEdge[1][i2][m]][transEdge[2][i3][m]]=(byte)(l+1);
							k++;
						}
					}
				}
			}}}
			l++;
		}while(k!=0);
	}

	private int gettransEdge(int slice, int pos, int m)
	{
		//convert pos number into permutation
		// UR,DR,DL,UL  RB,RF,LF,LB  UF,DF,DB,UB
		int edges[]={0,1,2,3, 0,1,2,3, 0,1,2,3};
		num2perm(edges,slice*4,4,pos);
		//do move  	  L U F R D B Lc Uc Fc
		if(m==0){
			swap( edges, 2,3 );
			swap( edges, 6,7 );
		}else if(m==1){
			swap( edges, 0,3 );
			swap( edges, 8,11 );
		}else if(m==2){
			swap( edges, 5,6 );
			swap( edges, 8,9 );
		}else if(m==3){
			swap( edges, 0,1 );
			swap( edges, 4,5 );
		}else if(m==4){
			swap( edges, 1,2 );
			swap( edges, 9,10 );
		}else if(m==5){
			swap( edges, 4,7 );
			swap( edges, 10,11 );
		}
		//convert back to number
		return(perm2num(edges,slice*4,4));
	}

	private int gettransCorn(int pos, int m)
	{
		//convert pos number into permutation
		// UBR,UFL,DFR,DBL,  URF,ULB,DRB,DLF
		int corners[]={0,1,2,3, 0,0,0,0};
		num2perm(corners,0,4,pos>>2);
		corners[4+ (pos&3) ]=1;
		//do move  	  L U F R D B Lc Uc Fc
		if(m==0){
			swap( corners, 1,3 );
			swap( corners, 5,7 );
		}else if(m==1){
			swap( corners, 0,1 );
			swap( corners, 4,5 );
		}else if(m==2){
			swap( corners, 1,2 );
			swap( corners, 4,7 );
		}else if(m==3){
			swap( corners, 0,2 );
			swap( corners, 4,6 );
		}else if(m==4){
			swap( corners, 2,3 );
			swap( corners, 6,7 );
		}else if(m==5){
			swap( corners, 0,3 );
			swap( corners, 5,6 );
		}
		//convert back to number
		return(perm2num(corners,0,4)*4 + corners[5]+corners[6]*2+corners[7]*3);
	}
	private int gettransCorn2(int pos, int m)
	{
		int c;
		//convert pos number into permutation
		// UBR,UFL,DFR,DBL,  URF,ULB,DRB,DLF
		int corners[]={0,1,2,3, 0,0,0,0};
		num2perm(corners,0,4,pos>>5);
		corners[4+ ((pos>>3)&3) ]=1;
		c=pos&7;
		//do move  	  L U F R D B Lc Uc Fc
		if(m==0){
			swap( corners, 1,3 );
			swap( corners, 5,7 );
			c^=4;
		}else if(m==1){
			swap( corners, 0,1 );
			swap( corners, 4,5 );
			c^=2;
		}else if(m==2){
			swap( corners, 1,2 );
			swap( corners, 4,7 );
			c^=1;
		}else if(m==3){
			swap( corners, 0,2 );
			swap( corners, 4,6 );
		}else if(m==4){
			swap( corners, 2,3 );
			swap( corners, 6,7 );
		}else if(m==5){
			swap( corners, 0,3 );
			swap( corners, 5,6 );
		}
		//convert back to number
		return( (perm2num(corners,0,4)<<5) + ((corners[5]+corners[6]*2+corners[7]*3)<<3) + c );
	}
}
