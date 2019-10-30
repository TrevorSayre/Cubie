
// ----- Solver for the Anti-Slice group ----
import java.awt.event.*;

final class SolverAntiSlice extends Solver
{
	int maxdepth;	// current search depth
	byte prune[][][][][];
	byte pruneFace[];
	int transFace[][];
	final int transCorn[][]={
		{4,0,6,2,5,1,7,3},	//La
		{1,2,3,0,7,4,5,6},	//Ua
		{3,5,1,7,0,6,2,4} };//Fa

	final int transEdge[][]={
		{4,1,6,3,8,0,10,2,5,9,7,11},  //La
		{1,2,3,0,4,5,6,7,11,8,9,10},  //Ua
		{0,5,2,7,3,9,1,11,8,6,10,4} };//Fa

	final int transOri[][]={
		{2,1,0,3},  //La
		{0,2,1,3},  //Ua
		{0,1,3,2} };//Fa

	public SolverAntiSlice( ActionListener m ){ super(m); }

    public MoveSequence getGenerator()
    {
        int[] m=new int[sollen];
        for( int i=0; i<sollen; i++){
            m[i]=15+solmoves[i];
            if( m[i]>17 ) m[i]-=3;            
        }
        return( new MoveSequence( sollen, m, solamount ) );
    }

	public void mix( CubePosition cubePos )
	{
		int j;
		// direct method, not random moves
		cubePos.reset();

		// edge orientation.  4 possibilities. face centres unaffected
		j=(int)(4*Math.random());
		if( j==1 || j==3 ){
			cubePos.cubeletOri[12]=
			cubePos.cubeletOri[13]=
			cubePos.cubeletOri[14]=
			cubePos.cubeletOri[15]=1;
		}
		if( j>1 ){
			cubePos.cubeletOri[8 ]=
			cubePos.cubeletOri[10]=
			cubePos.cubeletOri[16]=
			cubePos.cubeletOri[18]=1;
		}
		if( j==1 || j==2 ){
			cubePos.cubeletOri[9 ]=
			cubePos.cubeletOri[11]=
			cubePos.cubeletOri[17]=
			cubePos.cubeletOri[19]=1;
		}

		// set edges. first LR slice. 4 possibilities
		j=(int)(4*Math.random());
		if( j<2 ){
			swap( cubePos.cubeletPerm, 9,11);
			swap( cubePos.cubeletPerm, 17,19);
			swap( cubePos.cubeletPerm, 8,10);
			swap( cubePos.cubeletPerm, 16,18);
			//face centres unaffected
		}
		if( j==1 || j==3 ){
			swap( cubePos.cubeletPerm, 9,17);
			swap( cubePos.cubeletPerm, 11,19);
			swap( cubePos.cubeletPerm, 13,14);
			swap( cubePos.cubeletPerm, 12,15);
			//face centres unaffected
		}

		// now FB slice. 4 possibilities
		j=(int)(4*Math.random());
		if( j<2 ){
			swap( cubePos.cubeletPerm, 8,10);
			swap( cubePos.cubeletPerm, 16,18);
			swap( cubePos.cubeletPerm, 12,14);
			swap( cubePos.cubeletPerm, 13,15);
			//face centres unaffected by 2x2H pattern
		}
		if( j==1 || j==3 ){
			swap( cubePos.cubeletPerm, 8,18);
			swap( cubePos.cubeletPerm, 10,16);
			swap( cubePos.cubeletPerm, 13,14);
			swap( cubePos.cubeletPerm, 12,15);
			//face centres unaffected
		}

		// random position of corners.  4*3*4*2 positions
		// Maybe swap U/D corners
		j=(int)(2*Math.random());
		if( j==1 ){
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(0);cubePos.doMove(3);
		}
		// rotate around UD axis
		j=(int)(4*Math.random());
		while( j>0 ){
			cubePos.doMove(1);cubePos.doMove(4);
			j--;
		}
		// rotate corner 1 in place
		j=(int)(3*Math.random());
		while( j>0 ){
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(1);cubePos.doMove(4);
			j--;
		}

		// tetrad 1,3,4,6 has been set
		// set remaining tetrad
		j=(int)(4*Math.random());
		if( j==0 ){
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(2);cubePos.doMove(5);
		}else if(j==1){
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(0);cubePos.doMove(3);
		}else if(j==2){
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(1);cubePos.doMove(4);
		}
	}
	public boolean setPosition( CubePosition cubePos, boolean test ){
		int i,j;

		// check corner orientation
		// corners must have alternating orientation
		for( i=0; i<7; i++){
			if( i==3 ){
				j= cubePos.cubeletOri[0]+cubePos.cubeletOri[4];
			}else{
				j= cubePos.cubeletOri[i]+cubePos.cubeletOri[i+1];
			}
			if( j!=0 && j!=3 ) return(false);
		}

		// check edge orientation
		// each slice must have same orientations
		if( cubePos.cubeletOri[8]!=cubePos.cubeletOri[10] ||
			cubePos.cubeletOri[8]!=cubePos.cubeletOri[16] ||
			cubePos.cubeletOri[8]!=cubePos.cubeletOri[18] ) return(false);
		if( cubePos.cubeletOri[9]!=cubePos.cubeletOri[11] ||
			cubePos.cubeletOri[9]!=cubePos.cubeletOri[17] ||
			cubePos.cubeletOri[9]!=cubePos.cubeletOri[19] ) return(false);
		if( cubePos.cubeletOri[12]!=cubePos.cubeletOri[13] ||
			cubePos.cubeletOri[12]!=cubePos.cubeletOri[14] ||
			cubePos.cubeletOri[12]!=cubePos.cubeletOri[15] ) return(false);
		// an even number of edge slices is flipped
		if( (cubePos.cubeletOri[8]^cubePos.cubeletOri[9]^cubePos.cubeletOri[12]) != 0 ) return(false);

		// check opposite faces same
		if( Cubie.settings.superGroup && (
			cubePos.cubeletOri[20]!=cubePos.cubeletOri[23] ||
			cubePos.cubeletOri[21]!=cubePos.cubeletOri[24] ||
			cubePos.cubeletOri[22]!=cubePos.cubeletOri[25] ) ) return false;

		// first copy array, so we can do a simple solve on it
		int perm[] = new int[23];
		for( i=0; i<20; i++)
			perm[i] = cubePos.cubeletPerm[i];
		for( i=20; i<23; i++)
			perm[i] = cubePos.cubeletOri[i]&1;

		// solve corners
		// align UD axis
		if( cubePos.cubeletOri[0]==1 ){  //do Fa'
			perm[22]^=1;
			cycle( perm, 0,3,7,4 );
			cycle( perm, 1,5,6,2 );
			cycle( perm, 9,13,17,14 );
			cycle( perm, 11,15,19,12 );
		}else if( cubePos.cubeletOri[0]==2 ){ //do Ra'
			perm[20]^=1;
			cycle( perm, 0,1,5,4 );
			cycle( perm, 3,7,6,2 );
			cycle( perm, 8,13,16,12 );
			cycle( perm, 10,15,18,14 );
		}
		// flip UD axis if necessary to bring 0 up
		if( perm[4]==0 || perm[5]==0 || perm[6]==0 || perm[7]==0 ){
			// do Fa2
			swap(perm,0,7);swap(perm,4,3);
			swap(perm,1,6);swap(perm,5,2);
			swap(perm,11,19);swap(perm,12,15);
			swap(perm,9,17);swap(perm,13,14);
		}

		// turn 0 into position
		while( perm[0]!=0 ){  //do Ua'
			perm[21]^=1;
			cycle( perm, 0,1,2,3 );
			cycle( perm, 4,7,6,5 );
			cycle( perm, 8,9,10,11 );
			cycle( perm, 16,19,18,17 );
		}

		// Corner tetrad 0,2,5,7  should be solved now.
		if( perm[0]!=0 || perm[2]!=2 || perm[5]!=5 || perm[7]!=7 ) return(false);

		// solve other tetrad using 4S patterns
		if( perm[3]==1 ){
			perm[20]^=1;perm[22]^=1;
			swap( perm, 1,3);
			swap( perm, 4,6);
			swap( perm, 12,14);
			swap( perm, 13,15);
		}else if( perm[4]==1 ){
			perm[21]^=1;perm[22]^=1;
			swap( perm, 1,4);
			swap( perm, 3,6);
			swap( perm, 9,19);
			swap( perm, 11,17);
		}else if( perm[6]==1 ){
			perm[20]^=1;perm[21]^=1;
			swap( perm, 1,6);
			swap( perm, 3,4);
			swap( perm, 8,18);
			swap( perm, 10,16);
		}

		// corner tetrad 1,3,4,6 should be solved now.
		if( perm[1]!=1 || perm[3]!=3 || perm[4]!=4 || perm[6]!=6 ) return(false);

		// solve LR slice
		if( perm[11]==9 ){ // 4 vertical H on sides, no effect on face centre
			swap( perm, 9,11);
			swap( perm, 17,19);
			swap( perm, 8,10);
			swap( perm, 16,18);
		}else if( perm[17]==9 ){ //4H on udlr, no effect on face centres
			swap( perm, 9,17);
			swap( perm, 11,19);
			swap( perm, 13,14);
			swap( perm, 12,15);
		}else if( perm[19]==9 ){ // 2H (ud) 2X (fb), no effect on face centres
			swap( perm, 9,19);
			swap( perm, 11,17);
			swap( perm, 12,13);
			swap( perm, 14,15);
		}
		if( perm[9]!=9 || perm[11]!=11 || perm[17]!=17 || perm[19]!=19 ) return(false);

		// solve FB slice
		if( perm[10]==8 ){ // 2H (fb) 2X (lr) sides, no effect on face centres
			swap( perm, 8,10);
			swap( perm, 16,18);
			swap( perm, 12,14);
			swap( perm, 13,15);
		}else if( perm[18]==8 ){ //2H (ud) 2X (lr), no effect on face centres
			swap( perm, 8,18);
			swap( perm, 10,16);
			swap( perm, 13,14);
			swap( perm, 12,15);
		}else if( perm[16]==8 ){ // 4H (udfb), no effect on face centres
			swap( perm, 8,16);
			swap( perm, 10,18);
			swap( perm, 12,13);
			swap( perm, 14,15);
		}

		for( i=0; i<20; i++)
			if( perm[i]!=i ) return(false);
		if( Cubie.settings.superGroup ){
			for( i=20; i<23; i++)
				if( perm[i]!=0 ) return(false);
		}

		if( test ) return(true);
		if( !prepared ) return(false);

		// convert to numbers
		for(i=0; i<8; i++){
			if( cubePos.cubeletPerm[i]==0 ) perm[0]=i;
		}
		for(i=8; i<20; i++){
			if( cubePos.cubeletPerm[i]==8 ) perm[1]=i-8;
			else if( cubePos.cubeletPerm[i]==9 ) perm[2]=i-8;
			else if( cubePos.cubeletPerm[i]==12 ) perm[3]=i-8;
		}
		perm[4]= cubePos.cubeletOri[8]*2+cubePos.cubeletOri[9];
		if( Cubie.settings.superGroup ){
			perm[5]=(cubePos.cubeletOri[20]<<4)+(cubePos.cubeletOri[21]<<2)+cubePos.cubeletOri[22];
		}else{
			perm[5]=-1;
		}

		if( positionlist==null ){
			positionlist = new int[40][6];
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}else if( positionlist[0][0]!=perm[0] ||
				positionlist[0][1]!=perm[1] ||
				positionlist[0][2]!=perm[2] ||
				positionlist[0][3]!=perm[3] ||
				positionlist[0][4]!=perm[4] ||
				positionlist[0][5]!=perm[5] ){
			// clear out search history
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}
		// set position
		positionlist[0][0]=perm[0];
		positionlist[0][1]=perm[1];
		positionlist[0][2]=perm[2];
		positionlist[0][3]=perm[3];
		positionlist[0][4]=perm[4];
		positionlist[0][5]=perm[5];
		return(true);
	}

	// initialise tables
	protected void init()
	{
		int i1,i2,i3,i4,i5,k,m,q,j1,j2,j3,j4,j5;
		transFace = new int[3][64];
		prune = new byte[8][12][12][12][4];
		pruneFace = new byte[64];

		//calculate transition/pruning table for face centres
		k=0;
		for( i1=0; i1<4; i1++ )
		for( i2=0; i2<4; i2++ )
		for( i3=0; i3<4; i3++ ){
			transFace[0][k] = (((i1+3)&3)<<4)+(  i2      <<2)+  i3;
			transFace[1][k] = (  i1      <<4)+(((i2+3)&3)<<2)+  i3;
			transFace[2][k] = (  i1      <<4)+(  i2      <<2)+((i3+3)&3);
			pruneFace[k]=1;
			if(i1!=0)pruneFace[k]++;
			if(i2!=0)pruneFace[k]++;
			if(i3!=0)pruneFace[k]++;
			k++;
		}

		//calculate pruning tables
		int l=1;
		prune[0][0][1][4][0]=1;
		do{
			k=0;
			for(i1=0;i1<8;i1++)
			for(i2=0;i2<12;i2++)
			for(i3=0;i3<12;i3++)
			for(i4=0;i4<12;i4++)
			for(i5=0;i5<4;i5++){
				if( prune[i1][i2][i3][i4][i5]==l ){
					for(m=0;m<3;m++){
						j1=i1;j2=i2;j3=i3;j4=i4;j5=i5;
						for(q=0;q<3;q++){
							j1=transCorn[m][j1];
							j2=transEdge[m][j2];
							j3=transEdge[m][j3];
							j4=transEdge[m][j4];
							j5=transOri[m][j5];
							if( prune[j1][j2][j3][j4][j5] ==0){
								prune[j1][j2][j3][j4][j5]=(byte)(l+1);
								k++;
							}
						}
					}
				}
			}
			l++;
		}while(k!=0);
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
		if( maxdepth==0 && sollen==0 &&
				positionlist[0][0]==0 && positionlist[0][1]==0 &&
				positionlist[0][2]==1 && positionlist[0][3]==4 &&
				positionlist[0][4]==0 && positionlist[0][5]<=0 ){
			return(true);
		}

		while( sollen>=0 ){
			// at this point positionlist[0..sollen] are visited positions
			// and solmoves[0..sollen-1] are moves performed.
			// Note solmoves[sollen] is last tried (rejected) next move
			nxt=sollen+1;
			// add extra quarter turn to last move
			m = solmoves[sollen];
			if( m>=0 ){ // ignore dummy -1 move
				positionlist[nxt][0]= transCorn[m][positionlist[nxt][0]];
				positionlist[nxt][1]= transEdge[m][positionlist[nxt][1]];
				positionlist[nxt][2]= transEdge[m][positionlist[nxt][2]];
				positionlist[nxt][3]= transEdge[m][positionlist[nxt][3]];
				positionlist[nxt][4]= transOri[m][positionlist[nxt][4]];
				if( Cubie.settings.superGroup )
					positionlist[nxt][5]= transFace[m][positionlist[nxt][5]];
			}else{
				positionlist[nxt][0]= positionlist[sollen][0];
				positionlist[nxt][1]= positionlist[sollen][1];
				positionlist[nxt][2]= positionlist[sollen][2];
				positionlist[nxt][3]= positionlist[sollen][3];
				positionlist[nxt][4]= positionlist[sollen][4];
				positionlist[nxt][5]= positionlist[sollen][5];
			}
			solamount[sollen]++;
			// if done full turn, then move to next face
			if( solamount[sollen]>3 ){
				// next face, 0 turns yet
				solamount[sollen]=0;
				do{
					solmoves[sollen]++;
				}while( sollen!=0 && solmoves[sollen]==solmoves[sollen-1] );
				if( solmoves[sollen]>=3 ){
					// done all faces. backtrack
					sollen--;
					continue;
				}
				continue; // loop back to do a quarter turn of this new face
			}

			// check pruning for proposed new position
			if( sollen+prune[positionlist[nxt][0]][positionlist[nxt][1]][positionlist[nxt][2]][positionlist[nxt][3]][positionlist[nxt][4]]<maxdepth+1 &&
				( !Cubie.settings.superGroup || sollen+pruneFace[positionlist[nxt][5]]<maxdepth+1 )
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
