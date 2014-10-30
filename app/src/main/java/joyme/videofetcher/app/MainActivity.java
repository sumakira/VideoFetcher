package joyme.videofetcher.app;

import android.app.Activity;
import android.content.Intent;
import android.net.http.SslError;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class MainActivity extends Activity implements PatternMatch.PlayVideoInterface {

    private final int SOHU = 0x01;
    private final int YOUKU = 0x02;
    private final int LETV = 0x03;

    private WebView mWebview;
    private Button mButton;
//    private VideoView mVideoView;
//    private ProgressBar mProgressbar;
//    private TextView mTvDownloadRate, mTvLoadRate;
//    private MediaController mMediaController;
//    private AudioManager mAudioManager;

    private String mUrl = "http://pad.tv.sohu.com/20130415/n372764577.shtml";
    private String mLexTvUrl = "http://m.letv.com/ptv/vplay/21036600.html";
    private String mYoukuUrl = "http://v.youku.com/v_show/id_XODEzNzgyMTY0.html?f=22987453&ev=1&from=y8.3-idx-grid-1519-9909.86808-86807.1-1";

    protected String mHtmlText = "";
    protected MainActivity mSelf;
    protected View rootview;
    private final static String FUNC = "var doc = document.getElementsByTagName('head')[0];var script = document.createElement('script');script.type = 'text/javascript';var funstr = \"function myFunction() {var htmstr = document.getElementsByTagName('body')[0].innerHTML; if (htmstr.substr(0,6) == 'jsonp1'){htmstr = htmstr.replace('jsonp1', '');htmstr = htmstr.replace('\"; funstr+='\"highVid\":'; funstr+=\"', '<video ><source src=');\";funstr+=\"htmstr =htmstr.replace('\";funstr+=',\"ip\":';funstr+=\"', '/></video>');\";funstr+=\"document.getElementsByTagName('body')[0].innerHTML = htmstr; }else{ var h = ''+(new Date).getTime();\";funstr+=\"var sig=h._shift_en([23, 12, 131, 1321]);\";var sohuUrl = 'http://pad.tv.sohu.com/playinfo?callback=jsonp1';funstr+=\"sohuUrl +='&sig='+ sig;\";funstr+=\"sohuUrl += '&vid=' + vid;\";funstr+=\"var vidstr = ''+vid;\";funstr+=\"sohuUrl += '&key=' + vidstr._shift_en([23, 12, 131, 1321]);\";funstr+=\"sohuUrl +='&playlistid=' + playlistId;\";funstr+=\"window.location.href=sohuUrl;}\";funstr+=\"}\"; script.text =  funstr;doc.appendChild(script);";

    private final static String JS_FUNC = "myFunction();";
    private final static String JS_YOUKU = "\"var _m3u8 = document.getElementById(\\\"youku-html5player-video\\\").getAttribute('src');\\nif(_m3u8)\\n{\\n\\tvar _newM3u8 =_m3u8.replace(/vid\\\\/([\\\\d]+)/ , \\\"vid/\\\"+ videoId);\\n\\tdocument.getElementById(\\\"youku-html5player-video\\\").setAttribute('src', _newM3u8);\\n}\"";
    private final static String LEXTV_JSFUNC = "$('#j-player').trigger('click')";
//    private final static String LEXTV_JSFUNC = "javascript:$('.hv_ico_pasued').trigger('click')";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

//        if (!LibsChecker.checkVitamioLibs(this)) {
//            return;
//        }

        setContentView(R.layout.activity_main);
        rootview = ((ViewGroup) this.findViewById(android.R.id.content)).getChildAt(0);

        //switch video sources between youku and sohu
//        mUrl = mYoukuUrl;
//        mUrl = mLexTvUrl;

        mSelf = this;

        mWebview = (WebView) findViewById(R.id.main_webview);
        mButton = (Button) findViewById(R.id.main_btn);
//        mVideoView = (VideoView) findViewById(R.id.main_videoview);
//        mMediaController = new MediaController(this);
//        mProgressbar = (ProgressBar) findViewById(R.id.main_pb);
//        mTvDownloadRate = (TextView) findViewById(R.id.main_tv_downloadRate);
//        mTvLoadRate = (TextView) findViewById(R.id.main_tv_loadRate);
        mButton.setVisibility(View.GONE);

//        mVideoView.setMediaController(mMediaController);
//        mVideoView.requestFocus();
//        mVideoView.setOnInfoListener(this);
//        mVideoView.setOnBufferingUpdateListener(this);
//        mVideoView.setOnPreparedListener(this);

        mWebview.getSettings().setJavaScriptEnabled(true);

        if (videoUrlIsFrom(mUrl) == SOHU) {
            System.out.println("sohu video ");
            mWebview.getSettings().setUserAgentString("Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        } else if (videoUrlIsFrom(mUrl) == YOUKU) {
            System.out.println("youku video");
            mWebview.getSettings().setUserAgentString("Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53");
        }

        mWebview.addJavascriptInterface(new HtmlLoader(), "loader");
        mWebview.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                switch (videoUrlIsFrom(mUrl)) {
                    case SOHU:
                        mWebview.loadUrl("javascript:" + FUNC);
                        mWebview.loadUrl("javascript:myFunction();");
                        mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].outerHTML+'</html>');");
                        break;

                    case YOUKU:
                        mWebview.loadUrl("javascript:" + JS_YOUKU);
                        mWebview.loadUrl("javascript:window.loader.loadHtml('<html>'+document.getElementsByTagName('html')[0].innerHTML+'</html>');");
                        break;

                    case LETV:
                        break;
                }
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });

        mWebview.loadUrl(mUrl);

        PatternMatch.setDelegate(this);
    }

//    @Override
//    public void onBufferingUpdate(MediaPlayer mp, int percent) {
//        mTvLoadRate.setText(percent + "%");
//    }

//    @Override
//    public void onClick(View view) {
//
//    }

//    @Override
//    public boolean onInfo(MediaPlayer mp, int what, int extra) {
//        switch (what) {
//            case MediaPlayer.MEDIA_INFO_BUFFERING_START:
//                if (mVideoView.isPlaying()) {
//                    mVideoView.pause();
//                    mProgressbar.setVisibility(View.VISIBLE);
//                    mTvDownloadRate.setVisibility(View.VISIBLE);
//                    mTvDownloadRate.setText("");
//                    mTvLoadRate.setVisibility(View.VISIBLE);
//                    mTvLoadRate.setText("");
//                }
//                break;
//
//            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED:
//                mTvDownloadRate.setText("" + extra + "kb/s" + " ");
//                break;
//
//            case MediaPlayer.MEDIA_INFO_BUFFERING_END:
//                mVideoView.start();
//                mProgressbar.setVisibility(View.GONE);
//                mTvDownloadRate.setVisibility(View.GONE);
//                mTvLoadRate.setVisibility(View.GONE);
////                mMaxVideoLength = mVideoView.getDuration();
//                break;
//        }
//        return false;
//    }

//    @Override
//    public void onPrepared(MediaPlayer mp) {
//        mp.setPlaybackSpeed(1.0f);
//    }

    @Override
    public void playVideo(String url) {
        System.out.println("start to load the video");
        Intent intent = new Intent(this, VideoPlayActivity.class);
        intent.putExtra("url",url);
        this.startActivity(intent);
    }

    class HtmlLoader {
        @JavascriptInterface
        public void loadHtml(final String str) {
            mHtmlText = str;
            PatternMatch.getStringsByPattern(mHtmlText);
        }
    }

    private int videoUrlIsFrom(String url) {
        if (url.contains("sohu")) {
            return SOHU;
        } else if (url.contains("youku")) {
            return YOUKU;
        } else if (url.contains("letv")) {
            return LETV;
        }
        return 0;
    }


}