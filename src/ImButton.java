import java.awt.*;

// ImButton
// This is a button with cassette player ideogram on it.
//
// Create with: new ImButton(type)
// where type is optional integer denoting ideogram:
//   0= forward play (left triangle)  Default
//   1= reverse play (right triangle)
//   2= fast forward (two left triangles)
//   3= fast reverse (two right triangles)
// 4-7= ... to end   (as 0-3 but with bar at triangle tip)
//   8= pause        (two vertical bars )
//   9= stop         (square)
//
// Type can be changed by setType(newtype);
// After a resize, call clear() to clear old images.
// When disabled, image will be grey with white edge highlight

public final class ImButton
	extends java.awt.Button
{
	int type=0;
	Polygon shapes[];

	public ImButton(){}
	public ImButton(int t){ type=t; }
	public void setType(int t){type=t;}
	public void clear(){shapes=null;}

	void initShapes()
	{
		shapes=new Polygon[40];
		int w=getSize().width;
		int h=getSize().height;
		int ox=w/2;
		int oy=h/2;
		int rad= (w<h)? ox-3 : oy-3;

		//play
		int shpx[]={ox+rad,ox+rad,ox-rad};
		int shpy[]={oy-rad,oy+rad,oy    };
		shapes[0]=new Polygon(shpx,shpy,3);
		shapes[1]=new Polygon(shpx,shpy,3); shapes[1].translate(1,1);

		//reverse
		shpx=new int[]{ox-rad,ox-rad,ox+rad};
		shpy=new int[]{oy-rad,oy+rad,oy    };
		shapes[2]=new Polygon(shpx,shpy,3);
		shapes[3]=new Polygon(shpx,shpy,3); shapes[3].translate(1,1);

		// fast forward
		shpx=new int[]{ox+rad,ox+rad,ox,ox    ,ox-rad,ox    ,ox};
		shpy=new int[]{oy+rad,oy-rad,oy,oy-rad,oy    ,oy+rad,oy};
		shapes[4]=new Polygon(shpx,shpy,7);
		shapes[5]=new Polygon(shpx,shpy,7); shapes[5].translate(1,1);

		// rewind
		shpx=new int[]{ox-rad,ox-rad,ox,ox    ,ox+rad,ox    ,ox};
		shpy=new int[]{oy+rad,oy-rad,oy,oy-rad,oy    ,oy+rad,oy};
		shapes[6]=new Polygon(shpx,shpy,7);
		shapes[7]=new Polygon(shpx,shpy,7); shapes[7].translate(1,1);

		//step left
		shpx=new int[]{ox-rad,ox-rad*3/4,ox-rad*3/4,ox-rad};
		shpy=new int[]{oy-rad,oy-rad    ,oy+rad    ,oy+rad};
		shapes[8]=new Polygon(shpx,shpy,4);
		shapes[9]=new Polygon(shpx,shpy,4); shapes[9].translate(1,1);

		//step right
		shpx=new int[]{ox+rad,ox+rad*3/4,ox+rad*3/4,ox+rad};
		shpy=new int[]{oy-rad,oy-rad    ,oy+rad    ,oy+rad};
		shapes[10]=new Polygon(shpx,shpy,4);
		shapes[11]=new Polygon(shpx,shpy,4); shapes[11].translate(1,1);

		//Pause, left bar
		shpx=new int[]{ox-rad,ox-rad,ox-rad/3,ox-rad/3};
		shpy=new int[]{oy-rad,oy+rad,oy+rad  ,oy-rad  };
		shapes[12]=new Polygon(shpx,shpy,4);
		shapes[13]=new Polygon(shpx,shpy,4); shapes[13].translate(1,1);
		//Pause, right bar
		shpx=new int[]{ox+rad,ox+rad,ox+rad/3,ox+rad/3};
		shpy=new int[]{oy-rad,oy+rad,oy+rad  ,oy-rad  };
		shapes[14]=new Polygon(shpx,shpy,4);
		shapes[15]=new Polygon(shpx,shpy,4); shapes[15].translate(1,1);

		//Stop
		shpx=new int[]{ox-rad*2/3,ox-rad*2/3,ox+rad*2/3,ox+rad*2/3};
		shpy=new int[]{oy-rad*2/3,oy+rad*2/3,oy+rad*2/3,oy-rad*2/3};
		shapes[16]=new Polygon(shpx,shpy,4);
		shapes[17]=new Polygon(shpx,shpy,4); shapes[17].translate(1,1);

	}
	public void update( Graphics g ){ paint(g); }
	public void paint( Graphics g )
	{
		if( shapes==null ) initShapes();

		if( !isEnabled() ){
			g.setColor(new Color(255,255,255));
			drawShape(g,1);
			g.setColor(new Color(128,128,128));
		}else{
			g.setColor(getForeground());
		}
		drawShape(g,0);
	}

	public void drawShape( Graphics g, int offset )
	{
		switch(type){
		case 0:
			g.fillPolygon( shapes[  offset] ); break;
		case 1:
			g.fillPolygon( shapes[2+offset] ); break;
		case 2:
			g.fillPolygon( shapes[4+offset] ); break;
		case 3:
			g.fillPolygon( shapes[6+offset] ); break;
		case 4:
			g.fillPolygon( shapes[  offset] );
			g.fillPolygon( shapes[8+offset] ); break;
		case 5:
			g.fillPolygon( shapes[2+offset] );
			g.fillPolygon( shapes[10+offset] ); break;
		case 6:
			g.fillPolygon( shapes[4+offset] );
			g.fillPolygon( shapes[8+offset] ); break;
		case 7:
			g.fillPolygon( shapes[6+offset] );
			g.fillPolygon( shapes[10+offset] ); break;
		case 8:
			g.fillPolygon( shapes[12+offset] );
			g.fillPolygon( shapes[14+offset] ); break;
		case 9:
			g.fillPolygon( shapes[16+offset] ); break;
		}
	}
}