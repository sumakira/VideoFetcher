package joyme.videofetcher.app;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.http.SslError;
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
        mTimer = new Timer();
        mWebview = (WebView) findViewById(R.id.main_webview);
        mButton = (Button) findViewById(R.id.main_btn);

        mButton.setVisibility(View.GONE);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setUserAgentString("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        mWebview.addJavascriptInterface(new HtmlLoader(), "loader");

        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }
        });

        mWebview.loadUrl(mUrl);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                System.out.println("====== sending msg======");
                mHandler.sendMessage(msg);
            }
        };
    }

    class HtmlLoader {
        @JavascriptInterface
        public void loadHtml(final String str) {
            mHtmlText = str;
            out.print("str is " + mHtmlText);

            if (mHtmlText.contains("sig")) {
                Toast.makeText(mSelf, "Params captured in " + count + " loading times", Toast.LENGTH_LONG).show();
                mTimer.cancel();
            } else {
                mTimer.schedule(mTimertask, 3000);
                count++;
                mWebview.loadUrl(mUrl);
            }

            if (flag) {
                return;
            }
        }
    }

    void reloadWebview() {
        mWebview.reload();
    }


}
