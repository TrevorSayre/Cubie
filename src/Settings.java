public final class Settings
{
	public int group = 0;	// current cube group
	public boolean superGroup = false; // set if centre orientation visible
	public boolean solving = false; // set while some solver is busy
	public MoveSequence generator = null;	// movesequence returned from solver
	public boolean edit = false;	// set when edit mode, else play mode.

	public boolean lockViewer = false;	// set by cubie to disable user interaction on viewer
	// current cube position
	public CubePosition cubePos = new CubePosition();
}