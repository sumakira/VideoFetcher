package joyme.videofetcher.app;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

import static java.lang.System.out;

public class MainActivity extends Activity {

    private WebView mWebview;
    private Button mButton;
    //    private String mUrl = "http://hot.vrs.sohu.com/ipad1006750_4625602509181_4217071.m3u8?plat=h5";
    private String mUrl = "http://pad.tv.sohu.com/20130415/n372763498.shtml";
    protected String mHtmlText = "";
    protected MainActivity mSelf;
    private static int count = 0;
    protected View rootview;
    protected boolean flag = false;

    private Timer mTimer;
    private TimerTask mTimertask;
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootview = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        mSelf = this;
        mWebview = (WebView) findViewById(R.id.main_webview);
        mButton = (Button) findViewById(R.id.main_btn);

//        rootview.setVisibility(View.INVISIBLE);
//        mWebview.setVisibility(View.INVISIBLE);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setUserAgentString("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        mWebview.addJavascriptInterface(new HtmlLoader(), "loader");

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//                super.onPageFinished(view, url);
                Toast.makeText(mSelf, "Finished", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
//                mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }
        });

//        mWebview.loadUrl(mUrl);
        new UrlCatcher().execute();

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
//                mWebview.reload();
                new UrlCatcher().execute();
            }
        });

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 1:
                        out.println("checking html determined by timer");

                        mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        break;
                }
            }
        };

        mTimertask = new TimerTask() {
            @Override
            public void run() {
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        };
    }

    private void changeWebviewOrientation() {
        out.println("================== " + Thread.currentThread().getStackTrace()[2].getClassName() + " ===============");
        out.println("Changing orientation " + this.getRequestedOrientation());


        if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            out.println("=============Change to portrait==========");
        } else if (this.getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            out.println("=============Change to landscapte==========");
        }

        count++;
    }

    class HtmlLoader {
        @JavascriptInterface
        public void loadHtml(final String str) {
            mHtmlText = str;
//            out.print("str is " + mHtmlText);

//            System.out.println("Calling loadhtml");

//            if (str.contains("sig")) {
//                out.println("================== " + Thread.currentThread().getStackTrace()[2].getClassName() + " ===============");
//                out.println("^^^^^^^^^^^^^ got it! " + count);
////                mSelf.getWindow().getDecorView().setVisibility(View.VISIBLE);
////                rootview.setVisibility(View.VISIBLE);
////                rootview.invalidate();
//
//            } else {
////                out.println("================== " + Thread.currentThread().getStackTrace()[2].getClassName() + " ===============");
////                out.println("changing orientation");
////
////                mSelf.changeWebviewOrientation();
////                mButton.callOnClick();
////                new AsyncTask<Void, Void, Void>() {
////
////                    @Override
////                    protected Void doInBackground(Void... voids) {
////                        mSelf.changeWebviewOrientation();
////                        return null;
////                    }
////
////                    @Override
////                    protected void onPostExecute(Void aVoid) {
////                        if (str.contains("sid")) {
////                            return;
////                        }
////
////
////                    }
////                }.execute();
//                out.println("loading async task ");
////                mWebview.loadUrl(mUrl);
////                mWebview.reload();
//                count++;
////                new UrlCatcher().execute();
//            }

            if (mHtmlText.contains("key=")) {
                flag = true;
                out.println("-------------- count is  " + count);
            } else {
                count++;
                mWebview.loadUrl(mUrl);
                mTimer.schedule(mTimertask, 500);
                out.println("timer start");

            }

            if (flag) {
                return;
            }
//            new UrlCatcher().execute();
        }
    }

    class UrlCatcher extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            out.println("loading html ");

//            mSelf.changeWebviewOrientation();
//            mWebview.reload();
//            reloadWebview();
            mWebview.loadUrl(mUrl);
//            mButton.callOnClick();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
//            mButton.callOnClick();
            out.println("Task finished");
            Toast.makeText(mSelf, "Task finished", Toast.LENGTH_SHORT).show();

            if (mHtmlText.contains("sig")) {
                out.println("================== " + Thread.currentThread().getStackTrace()[2].getClassName() + " ===============");
                out.println("^^^^^^^^^^^^^ got it! " + count);
                flag = true;
            } else {
                count++;
//                new UrlCatcher().execute();
            }
        }
    }

    void reloadWebview() {
        mWebview.reload();
    }


}
