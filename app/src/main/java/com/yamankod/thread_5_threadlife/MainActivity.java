package com.yamankod.thread_5_threadlife;


        import java.io.IOException;

        import org.apache.http.HttpEntity;
        import org.apache.http.HttpResponse;
        import org.apache.http.StatusLine;
        import org.apache.http.client.HttpClient;
        import org.apache.http.client.methods.HttpGet;
        import org.apache.http.client.methods.HttpUriRequest;
        import org.apache.http.impl.client.DefaultHttpClient;
        import org.apache.http.util.EntityUtils;
        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Message;
        import android.view.View;
        import android.widget.ImageView;

public class MainActivity extends Activity {
    private static ProgressDialog dialog;
    private static Bitmap downloadBitmap;
    private static Handler handler;
    private ImageView imageView;
    private Thread downloadThread;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                imageView.setImageBitmap(downloadBitmap);
                dialog.dismiss();
            }
        };
        imageView = (ImageView) findViewById(R.id.imageView1);
        Context context = imageView.getContext();
        System.out.println(context);
        if (downloadBitmap != null) {
            imageView.setImageBitmap(downloadBitmap);
        }
        downloadThread = (Thread) getLastNonConfigurationInstance();
        if (downloadThread != null && downloadThread.isAlive()) {
            dialog = ProgressDialog.show(this, "Download", "downloading");
        }
    }
    public void resetPicture(View view) {
        if (downloadBitmap != null) {
            downloadBitmap = null;
        }
        imageView.setImageResource(R.drawable.ic_launcher);
    }
    public void downloadPicture(View view) {
        dialog = ProgressDialog.show(this, "Download", "downloading");
        downloadThread = new MyThread();
        downloadThread.start();
    }
    @Override
    public Object onRetainNonConfigurationInstance() {
        return downloadThread;
    }
    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
        super.onDestroy();
    }
    static private Bitmap downloadBitmap(String url) throws IOException {
        HttpUriRequest request = new HttpGet(url.toString());
        HttpClient httpClient = new DefaultHttpClient();
        HttpResponse response = httpClient.execute(request);
        StatusLine statusLine = response.getStatusLine();
        int statusCode = statusLine.getStatusCode();
        if (statusCode == 200) {
            HttpEntity entity = response.getEntity();
            byte[] bytes = EntityUtils.toByteArray(entity);
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0,
                    bytes.length);
            return bitmap;
        } else {
            throw new IOException("Download failed, HTTP response code "
                    + statusCode + " - " + statusLine.getReasonPhrase());
        }
    }
    static public class MyThread extends Thread {
        @Override
        public void run() {
            try {
                try {
                    new Thread().sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //Yeni bir image url si bulunmalı
                downloadBitmap = downloadBitmap("http://www.devoxx.com/download/attachments/4751369/DV11");
                handler.sendEmptyMessage(0);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {

            }
        }
    }
}







