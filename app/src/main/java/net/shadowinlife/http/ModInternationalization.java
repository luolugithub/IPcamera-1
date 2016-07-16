package net.shadowinlife.http;

import static net.shadowinlife.http.TinyHttpServer.TAG;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.util.Locale;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.MethodNotSupportedException;
import org.apache.http.entity.ContentProducer;
import org.apache.http.entity.EntityTemplate;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;

import android.util.Log;

public class ModInternationalization implements HttpRequestHandler {

	/** Client side file name. */
	public static final String PATTERN = "/strings.json";

	/** Prefix for strings. */
	public static final String PREFIX  = "web_";  
	
	private String mJSON = "{}"; 
	
	public ModInternationalization(TinyHttpServer server) {
		super();
		
		StringBuilder builder = new StringBuilder();
		
		try {

			// Retrieves R.string
			Class<?> String = Class.forName(server.mContext.getPackageName()+".R$string");
			Field[] fields = String.getFields();
			
			// Constructs a JSON with all members starting with PREFIX
			builder.append("{\"");
			for(int i=0; i < fields.length; i++) {
				if (fields[i].getName().startsWith(PREFIX)) {
					builder.append(fields[i].getName());
					builder.append("\":\"");
					builder.append((String) server.getString(fields[i].getInt(null)));
					builder.append("\",\"");
				}
			}
			
			if (builder.length()>2) {
				builder.setLength(builder.length()-2);
			} else {
				builder.setLength(builder.length()-1);
			}
			builder.append("}");
			
			mJSON = builder.toString();
			
		} catch (Exception e) {
			Log.e(TAG,"Little problem with ModInternationalization !");
			e.printStackTrace();
		} 

	}

	public void handle(
			final HttpRequest request, 
			final HttpResponse response,
			final HttpContext context) throws HttpException, IOException {

		final String method = request.getRequestLine().getMethod().toUpperCase(Locale.ENGLISH);
		if (!method.equals("GET") && !method.equals("HEAD")) {
			throw new MethodNotSupportedException(method + " method not supported"); 
		}

		final EntityTemplate body = new EntityTemplate(new ContentProducer() {
			public void writeTo(final OutputStream outstream) throws IOException {
				OutputStreamWriter writer = new OutputStreamWriter(outstream, "UTF-8"); 
				writer.write(mJSON);
				writer.flush();
			}
		});

		response.setStatusCode(HttpStatus.SC_OK);
		body.setContentType("text/json; charset=UTF-8");
		response.setEntity(body);

	}

}
