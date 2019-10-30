// ----- Solver for the Two-Generator group ----
import java.awt.event.*;

final class SolverTwoGen extends Solver
{
	int maxdepth;	// current search depth
	int transCorn[][];
	int transEdge[][];
	int transOri[][];
	int transOri2[][];
	byte pruneCorn[];
	byte pruneEdge[];
	byte pruneOri[];
	byte pruneOri2[];

	public SolverTwoGen( ActionListener m ){ super(m); }

    public MoveSequence getGenerator()
    {
        int[] m=new int[sollen];
        for( int i=0; i<sollen; i++){
            m[i]=solmoves[i]*2+1;
        }
        return( new MoveSequence( sollen, m, solamount ) );
    }

	public void mix( CubePosition cubePos )
	{
		int i,j;
        // direct method, not random moves
		cubePos.reset();
		// first mix corners
		//corner 5
		j=(int)(6*Math.random());
		if( j==4 ) cubePos.doMove(3);
		else if( j<4 ){
			while(j>0) {
				cubePos.doMove(1);
				j--;
			}
			cubePos.doMove(3);cubePos.doMove(3);
		}
		//corner 4
		j=(int)(5*Math.random());
		if( j<4 ){
			while(j>0) {
				cubePos.doMove(1);
				j--;
			}
			cubePos.doMove(3);cubePos.doMove(3);cubePos.doMove(3);
			cubePos.doMove(1);cubePos.doMove(3);
		}
		//corners 0-3
		j=(int)(4*Math.random());
		while(j>0) {
			cubePos.doMove(1);
			j--;
		}

		//corner orientation
		num2ori( cubePos.cubeletOri, 0,6,3, (int)(243*Math.random()) );

		//permute edges
		int edges[]={8,9,10,11, 12,13, 16};
		int pr[]=new int[7];
		do{
			num2perm( pr,  0,7, (int)(5040*Math.random()) );
		}while( parityOdd( pr,0,7 )!=parityOdd(cubePos.cubeletPerm,0,6) );
		for( i=0; i<7; i++){
			cubePos.cubeletPerm[edges[i]] = edges[pr[i]];
			j= ( i==4 || i==5 )? 1:0;
			if( pr[i]==4 || pr[i]==5 ) j=1-j;
			cubePos.cubeletOri[edges[i]]=j;
		}

		// centre orientation
		i=(int)(8*Math.random());
		cubePos.cubeletOri[21]=i&3;
		cubePos.cubeletOri[23]=((i&4)>>1);
		if( parityOdd(cubePos.cubeletPerm,0,6) ){
			cubePos.cubeletOri[23]+=1-(i&1);
		}else{
			cubePos.cubeletOri[23]+=(i&1);
		}
	}

	public boolean setPosition( CubePosition cubePos, boolean test ){
		int i,j;
		int block[]={6,7,14,15,17,18,19};
		// check 2x2x3 block
		for( i=0; i<7; i++)
			if( cubePos.cubeletOri[block[i]]!=0 || cubePos.cubeletPerm[block[i]]!=block[i] ) return(false);

		// check corner ori
		j=0;
		for( i=0;i<8;i++ ){
			j+=cubePos.cubeletOri[i]; if( j>2 )j-=3;
		}
		if( j!=0 ) return(false);

		// check edge ori
		// unfortunately flip is defined wrt rl faces!
		for( i=8; i<20; i++){
			j=cubePos.cubeletOri[i];
			if( i>=12 && i<16 ) j=1-j;
			if( cubePos.cubeletPerm[i]>=12 && cubePos.cubeletPerm[i]<16 ) j=1-j;
			if( j!=0 ) return(false);
		}

		// check permutation parity
		if( parityOdd( cubePos.cubeletPerm, 0, 20 ) ) return(false);

		// check centre ori
		if( Cubie.settings.superGroup ){
			if( cubePos.cubeletOri[20]!=0 || cubePos.cubeletOri[22]!=0 || cubePos.cubeletOri[24]!=0 || cubePos.cubeletOri[25]!=0 ) return(false);
			if( parityOdd( cubePos.cubeletPerm, 0, 8 ) ) {
				if( (cubePos.cubeletOri[21]&1) == (cubePos.cubeletOri[23]&1) ) return(false);
			}else{
				if( (cubePos.cubeletOri[21]&1) != (cubePos.cubeletOri[23]&1) ) return(false);
			}
		}

		// copy corner permutation
		int corn[]=new int[7];
		for( i=0; i<6; i++) corn[i]=cubePos.cubeletPerm[i];
		//solve corner 0
		if( corn[1]==0 ){
			cycle( corn, 1,2,3,0 );
		}else if( corn[2]==0 ){
			cycle( corn, 2,1,5,0 );
		}else if( corn[3]==0 ){
			cycle( corn, 3,2,1,0 );
		}else if( corn[4]==0 ){
			cycle( corn, 4,5,1,0 );
		}else if( corn[5]==0 ){
			cycle( corn, 5,1,2,0 );
		}

		//solve corner 1
		if( corn[2]==1 ){
			cycle( corn, 2,5,4,1 );
		}else if( corn[3]==1 ){
			cycle( corn, 3,2,5,1 );
		}else if( corn[4]==1 ){
			cycle( corn, 4,5,2,1 );
		}else if( corn[5]==1 ){
			cycle( corn, 5,2,3,1 );
		}

		//solve corner 2
		while( corn[3]==2 || corn[4]==2 || corn[5]==2 ){
			cycle( corn, 2,3,5,4 );
		}

		if( corn[3]!=3 || corn[4]!=4 || corn[5]!=5 ) return(false);

		if( test ) return(true);
		if( !prepared ) return(false);

		int newpos[]={0,0,0};
		// corner position
		for( i=0; i<6; i++) corn[i]=cubePos.cubeletPerm[i];
		newpos[0] = perm2num(corn,0,6);
		// edge position
		final int edg[] = {8,9,10,11,12,13,16};
		for( i=0; i<7; i++) corn[i]=cubePos.cubeletPerm[edg[i]];
		newpos[1] = perm2num(corn,0,7);
		// corner orientation
		newpos[2] = 0;
		for( i=4; i>=0; i--) newpos[2]=newpos[2]*3+cubePos.cubeletOri[i];
		if( Cubie.settings.superGroup ){
			newpos[2]=(newpos[2]<<4)+(cubePos.cubeletOri[23]<<2)+cubePos.cubeletOri[21];
		}

		if( positionlist==null ){
			positionlist = new int[40][3];
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}else if( positionlist[0][0]!=newpos[0] ||
				positionlist[0][1]!=newpos[1] ||
				positionlist[0][2]!=newpos[2] ){
			// clear out search history
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}
		// set position
		positionlist[0][0]=newpos[0];
		positionlist[0][1]=newpos[1];
		positionlist[0][2]=newpos[2];
		return(true);
	}

	// initialise tables
	protected void init()
	{
		int i,k,m,p,q;
		transEdge = new int[5040][2];
		transCorn = new int[720][2];
		transOri = new int[243][2];
		transOri2 = new int[3888][2];
		pruneEdge = new byte[5040];
		pruneCorn = new byte[720];
		pruneOri = new byte[243];
		pruneOri2 = new byte[3888];

		for( i=0; i<5040; i++){	// each edge position
			for( m=0;m<2;m++ ){	//each move
				transEdge[i][m]=gettransEdge(i,m);
			}
		}
		for( i=0; i<720; i++){	// each corner position
			for( m=0;m<2;m++ ){	//each move
				transCorn[i][m]=gettransCorn(i,m);
			}
		}
		for( i=0; i<243; i++){	// each corner position
			for( m=0;m<2;m++ ){	//each move
				transOri[i][m]=gettransOri(i,m);
			}
		}
		for( i=0; i<3888; i++){	// each corner position
			for( m=0;m<2;m++ ){	//each move
				transOri2[i][m]=gettransOri2(i,m);
			}
		}

		//calculate pruning tables. First edges
		int l=1;
		pruneEdge[0]=1;
		do{
			k=0;
			for(i=0;i<5040;i++){
				if( pruneEdge[i]==l){
					for(m=0;m<2;m++){
						p=i;
						for(q=0;q<3;q++){
							p=transEdge[p][m];
							if( pruneEdge[p]==0){
								pruneEdge[p]=(byte)(l+1);
								k++;
							}
						}
					}
				}
			}
			l++;
		}while(k!=0);

		//Now corner pruning table
		l=1;
		pruneCorn[0]=1;
		do{
			k=0;
			for(i=0;i<720;i++){
				if( pruneCorn[i]==l){
					for(m=0;m<2;m++){
						p=i;
						for(q=0;q<3;q++){
							p=transCorn[p][m];
							if( pruneCorn[p]==0){
								pruneCorn[p]=(byte)(l+1);
								k++;
							}
						}
					}
				}
			}
			l++;
		}while(k!=0);

		//Now corner orientation pruning table
		l=1;
		pruneOri[0]=1;
		do{
			k=0;
			for(i=0;i<243;i++){
				if( pruneOri[i]==l){
					for(m=0;m<2;m++){
						p=i;
						for(q=0;q<3;q++){
							p=transOri[p][m];
							if( pruneOri[p]==0){
								pruneOri[p]=(byte)(l+1);
								k++;
							}
						}
					}
				}
			}
			l++;
		}while(k!=0);
		//Now corner+centre orientation pruning table
		l=1;
		pruneOri2[0]=1;
		do{
			k=0;
			for(i=0;i<3888;i++){
				if( pruneOri2[i]==l){
					for(m=0;m<2;m++){
						p=i;
						for(q=0;q<3;q++){
							p=transOri2[p][m];
							if( pruneOri2[p]==0){
								pruneOri2[p]=(byte)(l+1);
								k++;
							}
						}
					}
				}
			}
			l++;
		}while(k!=0);
	}

	private int gettransEdge(int pos, int m)
	{
		//convert pos number into edge permutation
		int edges[]=new int[7];
		num2perm(edges,0,7,pos);
		//do move  U,R
		if(m==0){//U
			cycle( edges, 0,3,2,1 );
		}else if(m==1){ //R
			cycle( edges, 0,5,6,4 );
		}
		//convert back to number
		return(perm2num(edges,0,7));
	}
	private int gettransCorn(int pos, int m)
	{
		//convert pos number into corner permutation
		int corn[]=new int[6];
		num2perm(corn,0,6,pos);
		//do move  U,R
		if(m==0){//U
			cycle( corn, 0,3,2,1 );
		}else if(m==1){ //R
			cycle( corn, 0,1,5,4 );
		}
		//convert back to number
		return(perm2num(corn,0,6));
	}
	private int gettransOri(int pos, int m)
	{
		//convert pos number into permutation
		int corn[]=new int[6];
		num2ori( corn, 0,6, 3, pos);

		//do move  	  L U F R D B
		if(m==0){//U
			cycle( corn, 0,3,2,1 );
		}else if(m==1){ //R
			cycle( corn, 0,1,5,4 );
			corn[0]+=2;corn[1]++;corn[5]+=2;corn[4]++;
		}
		//convert back to number
		return(ori2num(corn,0,6,3));
	}
	private int gettransOri2(int pos, int m)
	{
		//convert pos number into permutation
		int corn[]=new int[8];
		num2ori( corn, 0,6, 3, pos>>4);
		corn[6]=pos&3;
		corn[7]=(pos>>2)&3;

		//do move  	  L U F R D B
		if(m==0){//U
			cycle( corn, 0,3,2,1 );
			corn[6]=(corn[6]+3)&3;
		}else if(m==1){ //R
			cycle( corn, 0,1,5,4 );
			corn[0]+=2;corn[1]++;corn[5]+=2;corn[4]++;
			corn[7]=(corn[7]+3)&3;
		}
		//convert back to number
		return((ori2num(corn,0,6,3)<<4)+(corn[7]<<2)+corn[6]);
	}

	//solve a position
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
		if( maxdepth==0 && sollen==0 && positionlist[0][0]==0 && positionlist[0][1]==0 && positionlist[0][2]==0 ) return(true);

		while( sollen>=0 ){
			// at this point positionlist[0..sollen] are visited positions
			// and solmoves[0..sollen-1] are moves performed.
			// Note solmoves[sollen] is last tried (rejected) next move
			nxt=sollen+1;
			// add extra half turn to last move
			m = solmoves[sollen];
			if( m>=0 ){ // ignore dummy -1 move
				positionlist[nxt][0]= transCorn[positionlist[nxt][0]][m];
				positionlist[nxt][1]= transEdge[positionlist[nxt][1]][m];
				if( Cubie.settings.superGroup ){
					positionlist[nxt][2]= transOri2[positionlist[nxt][2]][m];
				}else{
					positionlist[nxt][2]= transOri[positionlist[nxt][2]][m];
				}
			}else{
				positionlist[nxt][0]= positionlist[sollen][0];
				positionlist[nxt][1]= positionlist[sollen][1];
				positionlist[nxt][2]= positionlist[sollen][2];
			}
			solamount[sollen]++;
			// if done full turn, then move to next face
			if( solamount[sollen]>3 ){
				// next face, 0 turns yet
				solamount[sollen]=0;
				do{
					solmoves[sollen]++;
				}while( sollen!=0 && solmoves[sollen]==solmoves[sollen-1] );
				if( solmoves[sollen]>=2 ){
					// done all faces. backtrack
					sollen--;
					continue;
				}
				continue; // loop back to do a quarter turn of this new face
			}

			// check pruning for proposed new position
			if( sollen+pruneCorn[positionlist[nxt][0]]<maxdepth+1 &&
				sollen+pruneEdge[positionlist[nxt][1]]<maxdepth+1 &&
				(  Cubie.settings.superGroup ||  sollen+pruneOri[positionlist[nxt][2]]<maxdepth+1 ) &&
				( !Cubie.settings.superGroup || sollen+pruneOri2[positionlist[nxt][2]]<maxdepth+1 )
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
}
