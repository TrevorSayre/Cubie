
// ----- Solver for the Slice group ----
import java.awt.event.*;

final class SolverSlice extends Solver
{
	int maxdepth;	// current search depth
	byte prune[][][][];
	byte prune2[][][][][];
	final int transCorn[][]={
		{1,5,6,2,0,4,7,3},	//Ls
		{1,2,3,0,5,6,7,4},	//Us
		{4,5,1,0,7,6,2,3} };//Fs

	final int transEdge[][]={
		{5,1,6,3,0,8,10,2,4,9,7,11},  //Ls
		{1,2,3,0,4,5,6,7,9,10,11,8},  //Us
		{0,5,2,4,11,9,1,3,8,6,10,7} };//Fs

	int transOri[][];

	public SolverSlice( ActionListener m ){ super(m); }

    public MoveSequence getGenerator()
    {
        int[] m=new int[sollen];
        int[] a=new int[sollen];
        for( int i=0; i<sollen; i++){
            m[i]=12+solmoves[i];
            a[i]=solamount[i];
            if( m[i]>14 ) {
                m[i]-=3;
                a[i]=4-a[i];
            }
        }
        return( new MoveSequence( sollen, m, a ) );
    }

	public void mix( CubePosition cubePos )
	{
		int j;
		// direct method, not random moves
		cubePos.reset();
		// random position of corners
		// choose one of the three axes to go UD
		j=(int)(3*Math.random());
		if( j==1 ){
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(3);cubePos.doMove(3);
		}else if(j==2){
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(5);cubePos.doMove(5);
		}
		// maybe swap over UD axis
		j=(int)(2*Math.random());
		if( j!=0 ){
			cubePos.doMove(0);cubePos.doMove(0);
			cubePos.doMove(3);cubePos.doMove(3);
		}
		// rotate around UD axis
		j=(int)(4*Math.random());
		while( j>0 ){
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(4);cubePos.doMove(4);
			j--;
		}

		// now mix edges
		j=(int)(4*Math.random());
		while( j>0 ){
			// two 4-cycles of slice edges
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(3);cubePos.doMove(3);
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(4);cubePos.doMove(4);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(5);cubePos.doMove(5);
			cubePos.doMove(4);cubePos.doMove(1);
			cubePos.doMove(1);cubePos.doMove(1);
			j--;
		}
		j=(int)(4*Math.random());
		while( j>0 ){
			// two 4-cycles of slice edges
			cubePos.doMove(1);cubePos.doMove(4);
			cubePos.doMove(4);cubePos.doMove(4);
			cubePos.doMove(2);cubePos.doMove(5);
			cubePos.doMove(5);cubePos.doMove(5);
			cubePos.doMove(0);cubePos.doMove(3);
			cubePos.doMove(3);cubePos.doMove(3);
			cubePos.doMove(2);cubePos.doMove(2);
			cubePos.doMove(2);cubePos.doMove(5);
			j--;
		}
		j=(int)(2*Math.random());
		if( j!=0 ){
			// 4-H pattern
			swap( cubePos.cubeletPerm, 12,14 );
			swap( cubePos.cubeletPerm, 13,15 );
		}
		// do face centres
		j=(int)(4*Math.random());
		cubePos.cubeletOri[20]=j;
		cubePos.cubeletOri[23]=(4-j)&3;
		j=(int)(4*Math.random());
		cubePos.cubeletOri[21]=j;
		cubePos.cubeletOri[24]=(4-j)&3;
		j=(int)(2*Math.random());
		cubePos.cubeletOri[22]=j+j;
		if( cubePos.cubeletPerm[1]==0 ||
			cubePos.cubeletPerm[3]==0 ||
			cubePos.cubeletPerm[4]==0 ||
			cubePos.cubeletPerm[6]==0 ){
				cubePos.cubeletOri[22]^=1;
		}
		if( ((cubePos.cubeletOri[20]+cubePos.cubeletOri[21])&1)!=0 ){
			cubePos.cubeletOri[22]^=1;
		}
		cubePos.cubeletOri[25]=(4-cubePos.cubeletOri[22])&3;
	}

	public boolean setPosition( CubePosition cubePos, boolean test ){
		int i,j;

		// check corner orientation
		for( i=0; i<7; i++){
			if( i==3 ){
				j= cubePos.cubeletOri[0]+cubePos.cubeletOri[4];
			}else{
				j= cubePos.cubeletOri[i]+cubePos.cubeletOri[i+1];
			}
			if( j!=0 && j!=3 ) return(false);
		}

		// check edge orientation
		if( cubePos.cubeletOri[8]!=cubePos.cubeletOri[10] ||
			cubePos.cubeletOri[8]!=cubePos.cubeletOri[16] ||
			cubePos.cubeletOri[8]!=cubePos.cubeletOri[18] ) return(false);
		if( cubePos.cubeletOri[9]!=cubePos.cubeletOri[11] ||
			cubePos.cubeletOri[9]!=cubePos.cubeletOri[17] ||
			cubePos.cubeletOri[9]!=cubePos.cubeletOri[19] ) return(false);
		if( cubePos.cubeletOri[12]!=cubePos.cubeletOri[13] ||
			cubePos.cubeletOri[12]!=cubePos.cubeletOri[14] ||
			cubePos.cubeletOri[12]!=cubePos.cubeletOri[15] ) return(false);

		// check face centre orientation
		if( Cubie.settings.superGroup ){
			for( i=0; i<3; i++){
				if( ((cubePos.cubeletOri[20+i]+cubePos.cubeletOri[23+i])&3)!=0 ) return(false);
			}
			if( cubePos.cubeletPerm[1]==0 ||
				cubePos.cubeletPerm[3]==0 ||
				cubePos.cubeletPerm[4]==0 ||
				cubePos.cubeletPerm[6]==0 ){
					if( ((cubePos.cubeletOri[20]+cubePos.cubeletOri[21]+cubePos.cubeletOri[22])&1)==0 ) return(false);
			}else{
					if( ((cubePos.cubeletOri[20]+cubePos.cubeletOri[21]+cubePos.cubeletOri[22])&1)!=0 ) return(false);
			}
		}

		// first copy array
		int perm[] = new int[20];
		int ori[] = new int[3];
		for( i=0; i<20; i++){
			perm[i] = cubePos.cubeletPerm[i];
		}
		ori[0] = cubePos.cubeletOri[8];
		ori[1] = cubePos.cubeletOri[9];
		ori[2] = cubePos.cubeletOri[12];

		// solve corners
		// align UD axis
		if( cubePos.cubeletOri[0]==1 ){  //do Fs'
			cycle( perm, 0,4,7,3 );
			cycle( perm, 1,5,6,2 );
			cycle( perm, 9,13,17,14 );
			cycle( perm, 11,12,19,15 );
			swap(ori,1,2);
		}else if( cubePos.cubeletOri[0]==2 ){ //do Rs'
			cycle( perm, 0,1,5,4 );
			cycle( perm, 3,2,6,7 );
			cycle( perm, 8,13,16,12 );
			cycle( perm, 10,14,18,15 );
			i=ori[0];ori[0]=1-ori[2];ori[2]=1-i;
		}
		// flip UD axis if necessary to bring 0 up
		if( perm[4]==0 || perm[5]==0 || perm[6]==0 || perm[7]==0 ){
			// do Fs2
			swap(perm,0,7);swap(perm,4,3);
			swap(perm,1,6);swap(perm,5,2);
			swap(perm,11,19);swap(perm,12,15);
			swap(perm,9,17);swap(perm,13,14);
		}
		// turn 0 into position
		while( perm[0]!=0 ){  //do Us'
			cycle( perm, 0,1,2,3 );
			cycle( perm, 4,5,6,7 );
			cycle( perm, 8,9,10,11 );
			cycle( perm, 16,17,18,19 );
			swap( ori,0,1);
		}
		// corners should be solved now.
		for( i=0; i<8; i++)
			if( perm[i]!=i ) return(false);

		// solve LR slice
		if( perm[9]!=9 && perm[11]!=9 && perm[17]!=9 && perm[19]!=9 ) return(false);
		while( perm[9]!=9 ){
			cycle( perm, 9,17,19,11 );
			cycle( perm, 12,13,14,15 );
			ori[1]=1-ori[1];ori[2]=1-ori[2];
		}
		if( ori[1]!=0 || perm[11]!=11 || perm[17]!=17 || perm[19]!=19 ) return( false );
		// solve FB slice
		if( perm[8]!=8 && perm[10]!=8 && perm[16]!=8 && perm[18]!=8 ) return(false);
		while( perm[8]!=8 ){
			cycle( perm, 8,16,18,10 );
			cycle( perm, 12,13,14,15 );
			ori[0]=1-ori[0];ori[2]=1-ori[2];
		}
		if( ori[0]!=0 || perm[10]!=10 || perm[16]!=16 || perm[18]!=18 ) return( false );
		// solve UD slice
		if( perm[14]==12 ){
			swap( perm, 12,14);
			swap( perm, 13,15);
		}
		if( ori[2]!=0 || perm[12]!=12 || perm[13]!=13 || perm[14]!=14 || perm[15]!=15 ) return( false );

		if( test ) return(true);
		if( !prepared ) return(false);

		// convert to numbers. Use one corner and one representative from each slice
		for(i=0; i<8; i++){
			if( cubePos.cubeletPerm[i]==0 ) perm[0]=i;
		}
		for(i=8; i<20; i++){
			if( cubePos.cubeletPerm[i]==8 ) perm[1]=i-8;
			else if( cubePos.cubeletPerm[i]==9 ) perm[2]=i-8;
			else if( cubePos.cubeletPerm[i]==12 ) perm[3]=i-8;
		}
		// store face orientation
		perm[4] = (cubePos.cubeletOri[20]<<4)+(cubePos.cubeletOri[21]<<2)+cubePos.cubeletOri[22];

		if( positionlist==null ){
			positionlist = new int[40][5];
			maxdepth = sollen = 0;
			solmoves[0]=-1;
			solamount[0]=3;
		}else if( positionlist[0][0]!=perm[0] ||
				positionlist[0][1]!=perm[1] ||
				positionlist[0][2]!=perm[2] ||
				positionlist[0][3]!=perm[3] ||
				(positionlist[0][4]!=perm[4] && Cubie.settings.superGroup )){
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
		return(true);
	}

	// initialise tables
	protected void init()
	{
		int i1,i2,i3,i4,i5,k,m,q,j1,j2,j3,j4,j5;
		prune = new byte[8][12][12][12];
		prune2= new byte[8][12][12][12][64];
		transOri = new int[64][3];

		// calculate transition table for the face centres
		k=0;
		for(i1=0;i1<4;i1++)
		for(i2=0;i2<4;i2++)
		for(i3=0;i3<4;i3++){
			transOri[k][0]= (((i1+3)&3)<<4)+(  i2      <<2)+  i3;
			transOri[k][1]= (  i1      <<4)+(((i2+3)&3)<<2)+  i3;
			transOri[k][2]= (  i1      <<4)+(  i2      <<2)+((i3+3)&3);
			k++;
		}

		//calculate pruning tables
		int l=1;
		prune[0][0][1][4]=1;
		do{
			k=0;
			for(i1=0;i1<8;i1++)
			for(i2=0;i2<12;i2++)
			for(i3=0;i3<12;i3++)
			for(i4=0;i4<12;i4++){
				if( prune[i1][i2][i3][i4]==l ){
					for(m=0;m<3;m++){
						j1=i1;j2=i2;j3=i3;j4=i4;
						for(q=0;q<3;q++){
							j1=transCorn[m][j1];
							j2=transEdge[m][j2];
							j3=transEdge[m][j3];
							j4=transEdge[m][j4];
							if( prune[j1][j2][j3][j4] ==0){
								prune[j1][j2][j3][j4]=(byte)(l+1);
								k++;
							}
						}
					}
				}
			}
			l++;
		}while(k!=0);

		//calculate pruning tables
		l=1;
		prune2[0][0][1][4][0]=1;
		do{
			k=0;
			for(i1=0;i1<8;i1++)
			for(i2=0;i2<12;i2++)
			for(i3=0;i3<12;i3++)
			for(i4=0;i4<12;i4++)
			for(i5=0;i5<64;i5++){
				if( prune2[i1][i2][i3][i4][i5]==l ){
					for(m=0;m<3;m++){
						j1=i1;j2=i2;j3=i3;j4=i4;j5=i5;
						for(q=0;q<3;q++){
							j1=transCorn[m][j1];
							j2=transEdge[m][j2];
							j3=transEdge[m][j3];
							j4=transEdge[m][j4];
							j5=transOri [j5][m];
							if( prune2[j1][j2][j3][j4][j5] ==0){
								prune2[j1][j2][j3][j4][j5]=(byte)(l+1);
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
		if( maxdepth==0 && sollen==0 && positionlist[0][0]==0 && positionlist[0][1]==0 && positionlist[0][2]==1 && positionlist[0][3]==4 && ( !Cubie.settings.superGroup || positionlist[0][4]==0 ) ) return(true);

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
				if( Cubie.settings.superGroup ){
					positionlist[nxt][4]= transOri[positionlist[nxt][4]][m];
				}
			}else{
				positionlist[nxt][0]= positionlist[sollen][0];
				positionlist[nxt][1]= positionlist[sollen][1];
				positionlist[nxt][2]= positionlist[sollen][2];
				positionlist[nxt][3]= positionlist[sollen][3];
				positionlist[nxt][4]= positionlist[sollen][4];
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
			if( ( Cubie.settings.superGroup || sollen+prune[positionlist[nxt][0]][positionlist[nxt][1]][positionlist[nxt][2]][positionlist[nxt][3]]<maxdepth+1 ) &&
				(!Cubie.settings.superGroup || sollen+prune2[positionlist[nxt][0]][positionlist[nxt][1]][positionlist[nxt][2]][positionlist[nxt][3]][positionlist[nxt][4]]<maxdepth+1 )
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
