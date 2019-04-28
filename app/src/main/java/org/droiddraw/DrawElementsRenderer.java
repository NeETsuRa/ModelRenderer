package org.droiddraw;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import edu.union.graphics.Mesh;

import android.opengl.GLSurfaceView;

public class DrawElementsRenderer implements GLSurfaceView.Renderer {
	float xrot, yrot;
	float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float lightDiffuse[] = new float[] { 0.9f, 0.9f, 0.9f, 1.0f };
	float[] lightPos = new float[] {0,0,3,10};
	
	float matAmbient[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float matDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	FloatBuffer vertices;
	ShortBuffer indices;
	int indexCount;
	
	public DrawElementsRenderer(Mesh mesh) {
		int vertexCount = mesh.getVertexCount() * 3;
    	float[] temp = new float[vertexCount];
		
    	int ix = 0;
    	for (int i = 0; i < mesh.getVertexCount(); ++i) {
    		float[] vertex = mesh.getVertexf(i);
    		temp[ix++] = vertex[0];
    		temp[ix++] = vertex[1];
    		temp[ix++] = vertex[2];
		}
		vertices = ModelRenderer.makeFloatBuffer(temp);
		
    	// Assume triangles.
    	indexCount = mesh.getFaceCount() * 3;
    	short[] buff = new short[indexCount];
    	int index = 0;
    	for (int i = 0; i < mesh.getFaceCount(); ++i) {
    		int[] face = mesh.getFace(i);
    		buff[index++] = (short)face[0];
    		buff[index++] = (short)face[1];
    		buff[index++] = (short)face[2];
    	}
    	
    	indices = ShortBuffer.wrap(buff);
	}

	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -10);

		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertices);
		
		//gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		//gl.glVertexPointer(3, GL10.GL_FLOAT, 0, normals);
		
		gl.glDrawElements(GL10.GL_TRIANGLES, indexCount, GL10.GL_UNSIGNED_SHORT, indices);

		xrot += 1.0f;
		yrot += 0.5f;
	}

	public void onSurfaceChanged(GL10 gl, int w, int h) {
        gl.glViewport(0, 0, w, h);
        
        float ratio = (float)w / h;
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        gl.glFrustumf(-ratio, ratio, -1, 1, 2, 40);
		
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		
		gl.glEnable(GL10.GL_LIGHTING);
		gl.glEnable(GL10.GL_LIGHT0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
		gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
	
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
	}
}
