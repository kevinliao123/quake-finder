package kevinliao.com.quakefinder;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;

public class DetailActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        WebView webView = findViewById(R.id.web_view);
        String url = getIntent().getStringExtra(getString(R.string.earthquake_url_key));
        webView.loadUrl(url);
    }
}
