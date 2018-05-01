package subscene.datnt.com.subscene.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import subscene.datnt.com.subscene.utils.Decompress;

import static subscene.datnt.com.subscene.utils.Globals.APP_FOLDER;

public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
    private ProgressDialog dialog;
    private String fileName = "";
    private PowerManager.WakeLock mWakeLock;
    private Context context;
    private ArrayList<File> extractFile;
    private DownloadFileListener listener;
    private String path;
    public DownloadFileFromURL(Context context, String path, DownloadFileListener listener) {
        this.context = context;
        this.listener = listener;
        this.path = path;
        dialog = new ProgressDialog(context);
        dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        dialog.setIndeterminate(false);
        dialog.setTitle("Subtitle");
        dialog.setMessage("Downloading...");
    }

    /**
     * Before starting background thread
     * Show Progress Bar Dialog
     */

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog.show();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                getClass().getName());
        mWakeLock.acquire();
    }

    /**
     * Downloading file in background thread
     */
    @Override
    protected String doInBackground(String... f_url) {
        InputStream input = null;
        OutputStream output = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(f_url[0]);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            long length = Long.parseLong(connection.getHeaderField("Content-Length"));
            String type = connection.getHeaderField("Content-Type");
            String content = connection.getHeaderField("Content-Disposition");
            if (content != null && content.indexOf("=") != -1) {
                fileName = content.split("=")[1]; // getting value after '='
                fileName = fileName.replaceAll("\"", "").replaceAll("]", "");
            }
            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                return "Server returned HTTP " + connection.getResponseCode()
                        + " " + connection.getResponseMessage();
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            // download the file
            input = connection.getInputStream();
            output = new FileOutputStream(new File(path , fileName));

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }
        } catch (Exception e) {
            return e.toString();
        } finally {
            try {
                if (output != null)
                    output.close();
                if (input != null)
                    input.close();
            } catch (IOException ignored) {
            }

            if (connection != null)
                connection.disconnect();
        }
        return null;
    }

    /**
     * Updating progress bar
     */
    protected void onProgressUpdate(Integer... progress) {
        // setting progress percentage
        dialog.setProgress(progress[0]);
    }

    /**
     * After completing background task
     * Dismiss the progress dialog
     **/
    @Override
    protected void onPostExecute(String result) {
        // dismiss the dialog after the file was downloaded
        mWakeLock.release();
        dialog.dismiss();
        if (result != null) {
            //Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            if (listener != null)
                listener.onDownloaded(false);
        }else {
            if (FilenameUtils.getExtension(new File(path, fileName).getAbsolutePath()).equals("rar"))
                extractFile = Decompress.extractArchive(path+ "/" + fileName, path);
            else  if (FilenameUtils.getExtension(new File(path, fileName).getAbsolutePath()).equals("zip"))
                try {
                    extractFile = Decompress.unzip(path+ "/" + fileName, path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            else  if (FilenameUtils.getExtension(new File(path, fileName).getAbsolutePath()).equals("gz"))
                try {
                    extractFile = Decompress.unGZip(path+ "/" + fileName, path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            new File(path, fileName).delete();
            if (listener != null)
                listener.onDownloaded(true);
        }

    }

    public interface DownloadFileListener{
        void onDownloaded(boolean isDownloaded);
    }
}