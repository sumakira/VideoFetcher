package joyme.videofetcher.app;

import android.app.Activity;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
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

    private final int HTML_LOADED = 0;
    private final int JS_GENERATED = 1;
    private final int JS_LOADED = 2;
    private int mCurrentStage = HTML_LOADED;
    private WebView mWebview;
    private Button mButton;
//    private String mUrl = "http://hot.vrs.sohu.com/ipad1006750_4625602509181_4217071.m3u8?plat=h5";
        private String mUrl = "http://pad.tv.sohu.com/20130415/n372763498.shtml";
//    private String mLexTvUrl = "http://m.letv.com/ptv/vplay/21036600.html";
    protected String mHtmlText = "";
    protected MainActivity mSelf;
    private int count = 0;
    protected View rootview;
    protected boolean flag = false;

    private Timer mTimer;
    private TimerTask mTimertask;
    private Handler mHandler;
    private Message mMsg;

    private final static String FUNC = "var doc = document.getElementsByTagName('head')[0];var script = document.createElement('script');script.type = 'text/javascript';var funstr = \"function myFunction() {var htmstr = document.getElementsByTagName('body')[0].innerHTML; if (htmstr.substr(0,6) == 'jsonp1'){htmstr = htmstr.replace('jsonp1', '');htmstr = htmstr.replace('\"; funstr+='\"highVid\":'; funstr+=\"', '<video ><source src=');\";funstr+=\"htmstr =htmstr.replace('\";funstr+=',\"ip\":';funstr+=\"', '/></video>');\";funstr+=\"document.getElementsByTagName('body')[0].innerHTML = htmstr; }else{ var h = ''+(new Date).getTime();\";funstr+=\"var sig=h._shift_en([23, 12, 131, 1321]);\";var sohuUrl = 'http://pad.tv.sohu.com/playinfo?callback=jsonp1';funstr+=\"sohuUrl +='&sig='+ sig;\";funstr+=\"sohuUrl += '&vid=' + vid;\";funstr+=\"var vidstr = ''+vid;\";funstr+=\"sohuUrl += '&key=' + vidstr._shift_en([23, 12, 131, 1321]);\";funstr+=\"sohuUrl +='&playlistid=' + playlistId;\";funstr+=\"window.location.href=sohuUrl;}\";funstr+=\"}\"; script.text =  funstr;doc.appendChild(script);";

    private final static String JS_FUNC = "myFunction();";

    //    private final static String LEXTV_JSFUNC = "$('#j-player').trigger('click')";
//    private final static String LEXTV_JSFUNC = "javascript:$('.hv_ico_pasued').trigger('click')";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootview = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        mSelf = this;
        mMsg = new Message();
        mTimer = new Timer();

        mWebview = (WebView) findViewById(R.id.main_webview);
        mButton = (Button) findViewById(R.id.main_btn);

        mButton.setVisibility(View.GONE);
        mWebview.getSettings().setJavaScriptEnabled(true);
        mWebview.getSettings().setUserAgentString("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
//        mWebview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        mWebview.addJavascriptInterface(new HtmlLoader(), "loader");
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mMsg.what = mCurrentStage;
                mHandler.sendMessage(mMsg);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

        mWebview.loadUrl(mUrl);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {

                switch (msg.what) {
                    case HTML_LOADED:
                        mWebview.loadUrl("javascript:" + FUNC);
                        out.println("==========    " + "stage one " + "   ===========");
                        break;

                    case JS_GENERATED:
                        mWebview.loadUrl("javascript:myFunction();");
//                        mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        out.println("==========    " + " stage two" + "   ===========");
                        break;

                    case JS_LOADED:
                        mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        out.println("==========    " + "stage three " + "   ===========");
                        break;
                }

                mCurrentStage++;

                if (mTimertask == null || mTimertask.cancel()) {
                    mTimertask = new TimerTask() {
                        @Override
                        public void run() {
                            mMsg = new Message();
                            mMsg.what = mCurrentStage;
                            mHandler.sendMessage(mMsg);
                        }
                    };
                    mTimer.schedule(mTimertask, 3000);
                } else {
                    out.println("==========    " + " timertaske cancelled failed " + "   ===========");
                }

                out.println("==========    " + mCurrentStage + "   ===========");
            }
        };
    }

    class HtmlLoader {
        @JavascriptInterface
        public void loadHtml(final String str) {
            mHtmlText = str;
            out.print("str is " + mHtmlText);
        }
    }
}