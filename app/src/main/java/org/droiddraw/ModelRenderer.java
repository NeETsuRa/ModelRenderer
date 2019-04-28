package org.droiddraw;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import edu.union.graphics.FloatMesh;
import edu.union.graphics.MD2Loader;
import edu.union.graphics.Mesh;
import edu.union.graphics.Model;
import edu.union.graphics.ModelLoader;

public class ModelRenderer extends Activity {
	private GLSurfaceView mGLView;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ModelLoader loader = new MD2Loader();
        loader.setFactory(FloatMesh.factory());
        try {
        	Model m = loader.load(this.getResources().openRawResource(R.raw.tris));
        	Log.d("ModelRenderer", "Frames: " + m.getFrameCount());
        	
        	for (int i = 0; i < m.getFrameCount(); ++i) {
        		Mesh mesh = m.getFrame(i).getMesh();
        		mesh.scale(0.05f);
        	}
        	
        	BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.RGB_565;

        	InputStream is = getResources().openRawResource(R.raw.skin);
            Bitmap bitmap;
            try {
                bitmap = BitmapFactory.decodeStream(is, null, options);
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                	Log.e("ModelRenderer", "Error loading skin: ", e);
                }
            }
        	
        	mGLView = new GLSurfaceView(getApplication());
        	mGLView.setRenderer(new VBORenderer(m, bitmap));
        	setContentView(mGLView);
             
        } catch (IOException ex) {
        	Log.e("ModelRenderer", "Loading cube", ex);
        }
    }
	
	protected static FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer result = bb.asFloatBuffer();
		result.put(arr);
		result.position(0);
		return result;
	}

}