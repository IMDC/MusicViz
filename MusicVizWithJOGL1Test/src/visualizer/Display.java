package visualizer;

import java.util.LinkedList;

import javax.media.opengl.GL;
import javax.media.opengl.glu.GLU;

public class Display {
	
	private GL gl;
	private int limit;
	private LinkedList<Integer> lists = new LinkedList<Integer>();
	
	public Display(GL gl, GLU glu, int size)
	{	
		this.gl = gl;
		limit = size;
	}
	
	protected void add(int displayList)
	{
		lists.addLast(displayList);
		if (lists.size() > limit)
		{
			gl.glDeleteLists(lists.removeFirst(), 5);
		}
	}
	
	protected void draw(float x,float y,float z)
	{
		for (int i=0;i<lists.size()-1;i++)
		{
			gl.glTranslatef(x,y,z);
			gl.glCallList(lists.get(i));
		}
	}
	
	public LinkedList<Integer> getLists()
	{
		return lists;
	}
}