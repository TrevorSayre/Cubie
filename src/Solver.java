// Rubik's Cube simulator

import java.awt.event.*;

//--- Solver class, abstract. ---

abstract class Solver implements Runnable
{
	boolean prepared=false;	// flag is set when tables are prepared
	boolean wanttostop=false;	// flag is set when solver should be stopped soon
    boolean running=false;

	int sollen = 0;	// number of moves in the move list
	int solmoves[] = new int[40];	// faces of moves performed during search
	int solamount[] = new int[40];	// exponent of moves performed during search
	int positionlist[][];	// encoded positions
	ActionListener main;

	public Solver(ActionListener m){
		main=m;
	}


	// start thread - either calc tables or solve
	public void run()
	{
		if( !prepared ){
			init();
			prepared=true;
			doEvent(0);
		}else if( !Cubie.settings.solving && !wanttostop){
		   Cubie.settings.solving=true;
            running=true;
            doEvent(4);
			boolean r = solve(); // do it!
            running=false;
            Cubie.settings.solving=false;
			if( wanttostop ){
                wanttostop = false;
				doEvent(2);
			}else if( !r ){
				doEvent(3);
			}else{
				// if found solution, and not interrupted
			   Cubie.settings.generator = getGenerator();
				doEvent(1);
			}
		}
	}
	public void stopSolving()
	{
		if(running) wanttostop = true;
	}

	void doEvent(int t){
		//dispatch action event that solver has finished.
		// four event types:
		//  0. initialisation done.
		//  1. Solution found
		//  2. Solver aborted by user
		//  3. Solver finished, no solution found.
		//I did not bother to implement an addActionListener method as there
		// will only ever be one listener to this, viz the main cubie applet.
		String cm[]={"a","b","c","d","e"};
		ActionEvent e=new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
									cm[t]);
		main.actionPerformed( e );
	}


	// access function prepared flag
    public boolean isPrepared(){ return(prepared); }
    public boolean isRunning(){ return(running); }
	// Set position to be solved - initialise search if new position
	abstract public boolean setPosition( CubePosition cubePos, boolean test );
	// Start/continue search
	abstract protected boolean solve();
	// initialise tables
	abstract protected void init();
	// mix cube within this cube group
	abstract public void mix( CubePosition cubePos );
	// convert movelist into string solution
	abstract public MoveSequence getGenerator();

	// --- General utilities ---
	protected void swap(int pr[], int i, int j){
		int c=pr[i];
		pr[i]=pr[j];
		pr[j]=c;
	}
	protected void cycle(int pr[], int i, int j, int k, int l){
		int c=pr[i];
		pr[i]=pr[j];
		pr[j]=pr[k];
		pr[k]=pr[l];
		pr[l]=c;
	}

	// return true if permutation is odd
	protected boolean parityOdd(int pieces[], int start, int len){
		int i,j;
		boolean p=false;
		for( i=0;i<len;i++ ){
			for( j=0;j<i;j++ ){
				p^=( pieces[start+i]<pieces[start+j] );
			}
		}
		return(p);
	}
	// Convert number to permutation
	protected void num2perm(int pieces[], int start, int len, int pos0)
	{
	   int pos = pos0;
		// convert number pos into permutation of 0..len-1 and put it
		// in array pieces[start..start+len-1]
		int i,r;
		int w[]={0,1,2,3,4,5,6,7,8,9,10,11};
		for( i=0; i<len; i++){
			r=pos%(len-i);
			pos=(pos-r)/(len-i);
			pieces[start+i]=w[r];	// use r'th remaining piece
			while( ++r<len ) w[r-1]=w[r];	// remove piece from list
		}
	}
	// Convert number to partial permutation
	protected void num2partperm(int pieces[], int start, int len, int np, int p0, int pos0)
	{
		// convert number pos into permutation of np pieces numbered p0..p0+np-1
		// amongst len places 0..len-1 and put it
		// in array pieces[start..start+len-1]
		int i, j, r;
		int pos = pos0;
		for( i=0; i<np; i++){
			r=pos%(len-i);
			pos=(pos-r)/(len-i);
			for(j=start; j<start+len && ( (pieces[j]>=p0 && pieces[j]<p0+np) || r>0); j++){
				if(pieces[j]<p0 || pieces[j]>=p0+np) r--;
			}
			pieces[j]=p0+i;
		}
	}
	// Convert number to permutation
	protected void num2ori(int pieces[], int start, int len, int val, int pos0)
	{
		// convert number pos into orientation of 0..val-1 and put it
		// in array pieces[start..start+len-1]
		int i,j=0,k;
		int pos = pos0;
		for( i=0;i<len-1; i++){
			k=pos%val;
			j+=val-k;
			pos=(pos-k)/val;
			pieces[start+i]=k;
		}
		pieces[start+len-1]=j%val;
	}

	// Convert orientation to number
	protected int ori2num(int pieces[], int start, int len, int val)
	{
		int i,j=0;
		for( i=len-2;i>=0; i--) j=j*val+(pieces[start+i]%val);
		return(j);
	}

	// Convert full permutation to number
	protected int perm2num(int[] pieces, int start, int len)
	{
		// convert permutation of 0..len-1 in array pieces[start..start+len-1]
		// into number
		int i,j,r;
		int p=0;
		for( i=len-1; i>=0; i-- ){
			r=0;
			for( j=i+1; j<len; j++ ){
				if( pieces[start+j]<pieces[start+i]) { r++; }
			}
			p=p*(len-i)+r;
		}
		return p;
	}
	protected int partperm2num( int perm[], int len, int start, int p0, int np )
	{
		int i,j,r,pos=0;
		for( i=np-1; i>=0; i--){
			r=0;
			for( j=0; j<len && perm[start+j]!=p0+i; j++ ){
				if( perm[start+j]<p0 || perm[start+j]>p0+i ) r++;
			}
			pos=pos*(len-i)+r;
		}
		return(pos);
	}
}


