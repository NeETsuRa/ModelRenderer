package org.droiddraw;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import edu.union.graphics.Mesh;

public class DrawArraysRenderer implements GLSurfaceView.Renderer {
	float xrot, yrot;
	float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float lightDiffuse[] = new float[] { 0.9f, 0.9f, 0.9f, 1.0f };
	float[] lightPos = new float[] {0,5,10};
	
	float matAmbient[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float matDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	FloatBuffer vertices;
	FloatBuffer normals;
	int vertexCount;
	
	public DrawArraysRenderer(Mesh mesh) {
		vertexCount = mesh.getFaceCount() * 3;
    	float[] temp = new float[vertexCount * 3];
		float[] normals_temp = new float[vertexCount * 3];
    	int ix = 0;
    	for (int i = 0; i < mesh.getFaceCount(); ++i) {
    		int[] face_ix = mesh.getFace(i);
    		int[] normal_ix = mesh.getFaceNormals(i);
    		for (int j = 0; j < 3; ++j) {
    			float[] vertex = mesh.getVertexf(face_ix[j]);
    			float[] normal = mesh.getNormalf(normal_ix[j]);
    			normals_temp[ix] = normal[0];
    			temp[ix++] = vertex[0];
    			normals_temp[ix] = normal[1];
    			temp[ix++] = vertex[1];
    			normals_temp[ix] = normal[2];
    			temp[ix++] = vertex[2];
    		}
		}
		vertices = ModelRenderer.makeFloatBuffer(temp);
		normals = ModelRenderer.makeFloatBuffer(normals_temp);
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
		
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normals);
		
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertexCount);

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
