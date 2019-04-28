package org.droiddraw;

import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.graphics.Bitmap;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.util.Log;
import edu.union.graphics.Mesh;
import edu.union.graphics.Model;

public class VBORenderer implements GLSurfaceView.Renderer {
	Bitmap bitmap;
	int texId;
	int currentFrame, nextFrame;
	int ix = 0;
	int frameCount;
	float xrot, yrot;
	float lightAmbient[] = new float[] { 0.2f, 0.3f, 0.6f, 1.0f };
	float lightDiffuse[] = new float[] { 0.9f, 0.9f, 0.9f, 1.0f };
	float[] lightPos = new float[] {0,5,10};
	
	float matAmbient[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float matDiffuse[] = new float[] { 0.6f, 0.6f, 0.6f, 1.0f };
	float[][] vertices;
	float[] drawVertices;
	float[][] normals;
	float[] drawNormals;
	FloatBuffer vertexBuffer;
	FloatBuffer normalBuffer;
	FloatBuffer textures;
	
	int vertexCount;
	int textureBuffer;
	
	public VBORenderer(Model model, Bitmap bitmap) {
		this.texId = -1;
		this.bitmap = bitmap;	
		this.frameCount = model.getAnimation(0).getEndFrame();
		this.frameCount = 16;
		this.vertices = new float[frameCount][];
		this.normals = new float[frameCount][];
		for (int i = 0; i < frameCount; ++i) {
			loadMesh(model.getFrame(i).getMesh(), i);
		}
		this.drawNormals = new float[vertexCount * 3];
		this.normalBuffer = FloatBuffer.wrap(drawNormals);
		this.drawVertices = new float[vertexCount * 3];
		this.vertexBuffer = FloatBuffer.wrap(drawVertices);
	}
	
	protected void loadMesh(Mesh mesh, int frame_ix) {
		vertexCount = mesh.getFaceCount() * 3;
		vertices[frame_ix] = new float[vertexCount * 3];
		normals[frame_ix] = new float[vertexCount * 3];
		float[] temp = vertices[frame_ix];
		float[] normals_temp = normals[frame_ix];
		float[] tex_temp = new float[vertexCount *2];
    	int ix2 = 0, ix3 = 0;
    	for (int i = 0; i < mesh.getFaceCount(); ++i) {
    		int[] face_ix = mesh.getFace(i);
    		int[] normal_ix = mesh.getFaceNormals(i);
    		int[] texture_ix = mesh.getFaceTextures(i);
    		for (int j = 0; j < 3; ++j) {
    			float[] vertex = mesh.getVertexf(face_ix[j]);
    			float[] normal = mesh.getNormalf(normal_ix[j]);
    			float[] tex_coord = mesh.getTextureCoordinatef(texture_ix[j]);
    			tex_temp[ix2++] = tex_coord[0];
    			tex_temp[ix2++] = tex_coord[1];
    			
    			normals_temp[ix3] = normal[0];
    			temp[ix3++] = vertex[0];
    			normals_temp[ix3] = normal[1];
    			temp[ix3++] = vertex[1];
    			normals_temp[ix3] = normal[2];
    			temp[ix3++] = vertex[2];
    		}
		}
		if (textures == null) {
			textures = ModelRenderer.makeFloatBuffer(tex_temp);
		}
		if (vertexBuffer == null) {
			vertexBuffer = ModelRenderer.makeFloatBuffer(temp);
		}
		if (normalBuffer == null) {
			normalBuffer = ModelRenderer.makeFloatBuffer(normals_temp);
		}
	}

	protected void interpolate(float fraction, float[] a, float[] b, float[] out) {
		for (int i = 0; i < a.length; ++i) {
			out[i] = (a[i] * (1 - fraction)) + b[i] * fraction;
		}
	}
	
	public void onDrawFrame(GL10 gl10) {
		float fraction = ((float)ix) / 10;
		interpolate(fraction, vertices[currentFrame], vertices[nextFrame], drawVertices);
		interpolate(fraction, normals[currentFrame], normals[nextFrame], drawNormals);

		GL11 gl = (GL11)gl10;
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPos, 0);
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glTranslatef(0, 0, -10);

		gl.glRotatef(xrot, 1, 0, 0);
		gl.glRotatef(yrot, 0, 1, 0);
		
		gl.glBindTexture(GL10.GL_TEXTURE_2D, texId);
		
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

		gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, textureBuffer);
        gl.glTexCoordPointer(2, GL11.GL_FLOAT, 0, 0);
		gl.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
		
		gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		gl.glNormalPointer(GL10.GL_FLOAT, 0, normalBuffer);
		
		gl.glDrawArrays(GL10.GL_TRIANGLES, 0, vertexCount);

		ix++;
		if (ix % 10 == 0) {
			currentFrame = (currentFrame + 1) % frameCount;
			nextFrame = (nextFrame + 1) % frameCount;
		}
		//xrot += 1.0f;
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
		
		if (bitmap != null) {
			gl.glEnable(GL10.GL_TEXTURE_2D);
			texId = loadTexture(bitmap, gl);
		}
		
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient,	0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse,	0);
		
		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDepthFunc(GL10.GL_LEQUAL);
	
		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		
		GL11 gl11 = (GL11)gl;
		int[] temp = new int[1];
		gl11.glGenBuffers(1, temp, 0);
		textureBuffer = temp[0];
		
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, textureBuffer);
		gl11.glBufferData(GL11.GL_ARRAY_BUFFER, vertexCount * 2 * 4, textures, GL11.GL_STATIC_DRAW);
		gl11.glBindBuffer(GL11.GL_ARRAY_BUFFER, 0);
        gl11.glBindBuffer(GL11.GL_ELEMENT_ARRAY_BUFFER, 0);
	}
	
	protected int loadTexture(Bitmap texture, GL10 gl) {
        int textureName = -1;
        if (gl != null) {
        	int[] tex_out = new int[1];
            gl.glGenTextures(1, tex_out, 0);

            textureName = tex_out[0];
            gl.glBindTexture(GL10.GL_TEXTURE_2D, textureName);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
            gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

            gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);

            

            GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, texture, 0);

            int error = gl.glGetError();
            if (error != GL10.GL_NO_ERROR) {
                Log.e("VBORenderer", "Texture Load GLError: " + error);
            }

        }

        return textureName;
    }

}
