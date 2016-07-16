package net.shadowinlife.spydroid.ui;

import net.shadowinlife.http.TinyHttpServer;
import net.shadowinlife.spydroid.R;
import net.shadowinlife.spydroid.api.CustomHttpServer;
import net.shadowinlife.spydroid.api.CustomRtspServer;
import net.shadowinlife.streaming.SessionBuilder;
import net.shadowinlife.streaming.gl.SurfaceView;
import net.shadowinlife.streaming.rtsp.RtspServer;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class PreviewFragment extends Fragment {

	public final static String TAG = "PreviewFragment";

	private SurfaceView mSurfaceView;
	private TextView mTextView;
    private CustomHttpServer mHttpServer;
    private RtspServer mRtspServer;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public void onPause() {
		super.onPause();
    	getActivity().unbindService(mHttpServiceConnection);
    	getActivity().unbindService(mRtspServiceConnection);
	}
	
	@Override
    public void onResume() {
    	super.onResume();
		getActivity().bindService(new Intent(getActivity(),CustomHttpServer.class), mHttpServiceConnection, Context.BIND_AUTO_CREATE);
		getActivity().bindService(new Intent(getActivity(),CustomRtspServer.class), mRtspServiceConnection, Context.BIND_AUTO_CREATE);
    }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.preview,container,false);

		mTextView = (TextView)rootView.findViewById(R.id.tooltip);
		
		if (((LivecastActivity)getActivity()).device == ((LivecastActivity)getActivity()).TABLET) {

			mSurfaceView = (SurfaceView)rootView.findViewById(R.id.tablet_camera_view);
			SessionBuilder.getInstance().setSurfaceView(mSurfaceView);

		} 
		
		return rootView;
	}
	
	public void update() {
		getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if (mTextView != null) {
					if ((mRtspServer != null && mRtspServer.isStreaming()) || (mHttpServer != null && mHttpServer.isStreaming()))
						mTextView.setVisibility(View.INVISIBLE);
					else 
						mTextView.setVisibility(View.VISIBLE);
				}
			}
		});
	}
	
    private final ServiceConnection mRtspServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mRtspServer = (RtspServer) ((RtspServer.LocalBinder)service).getService();
			update();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
    
    private final ServiceConnection mHttpServiceConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			mHttpServer = (CustomHttpServer) ((TinyHttpServer.LocalBinder)service).getService();
			update();
		}
		@Override
		public void onServiceDisconnected(ComponentName name) {}
	};
	
}
