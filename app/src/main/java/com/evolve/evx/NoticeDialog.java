package com.evolve.evx;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

/**
 * Created by eleazerarcilla on 27/11/2018.
 */

public class NoticeDialog extends Dialog implements android.view.View.OnClickListener{
    private Activity _activity;
    private Context _context;
    private Dialog _dialog;
    private TextView _TV_notice_title, _TV_notice_details;
    public static ImageView _IV_notice_image;
    private Button _BTN_notice_okay;
    private Helper _helper;
    private String _STR_Title, _STR_Details;
    private Boolean _BOOL_IsPersistent = false, _BOOL_IsMaintenance = false;
    public static String _STR_Image_URL = null;
    public static FrameLayout _FL_notice_image_loader;
    public static ImageButton _IB_profile_loader_circle;

    public NoticeDialog(Activity activity, String title, String details){
        super(activity);
        this._activity = activity;
        this._context = activity.getApplicationContext();
        this._STR_Title = title;
        this._STR_Details = details;
        this._helper = new Helper(_activity, _activity.getApplicationContext());
        this._dialog = new Dialog(_activity.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_notice);
            //IsPersitent|IsMaintenance|Title|Details|URL
            InitializeNoticeUI();
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }

    private void InitializeNoticeUI(){
        try{
            _TV_notice_title = (TextView) findViewById(R.id.TV_notice_Title);
            _TV_notice_details = (TextView) findViewById(R.id.TV_notice_details);
            _IV_notice_image = (ImageView) findViewById(R.id.IV_notice_image);
            _BTN_notice_okay = (Button) findViewById(R.id.BTN_notice_okay);
            _FL_notice_image_loader = (FrameLayout) findViewById(R.id.FL_notice_image_loader);
            _TV_notice_title.setTypeface(MenuActivity.FONT_RUBIK_BOLD);
            _TV_notice_details.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
            _BTN_notice_okay.setTypeface(MenuActivity.FONT_RUBIK_BOLD);
            _IB_profile_loader_circle = (ImageButton) findViewById(R.id.IB_profile_loader_circle);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            _dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            setCancelable(false);
            ExtractDetails();
            _TV_notice_title.setText(this._STR_Title);
            _TV_notice_details.setText(this._STR_Details);
            _BTN_notice_okay.setVisibility(_BOOL_IsPersistent ? View.GONE : View.VISIBLE);
            _BTN_notice_okay.setOnClickListener(this);
            if(_BOOL_IsMaintenance){
                _IV_notice_image.setVisibility(View.VISIBLE);
            }
            else if (_STR_Image_URL!=null && !_BOOL_IsMaintenance){
                _IV_notice_image.setVisibility(View.GONE);
                _FL_notice_image_loader.setVisibility(View.VISIBLE);
                Animation rotation;
                rotation = AnimationUtils.loadAnimation(_activity, R.anim.rotate);
                rotation.setFillAfter(true);
                this._IB_profile_loader_circle.startAnimation(rotation);
                new GetImageFromWeb().execute();
            }

        }catch (Exception ex){
            Helper.logger(ex);
        }
    }

    private void ExtractDetails(){
        try{
            String[] results = this._STR_Details.split("\\|");
            if(results.length>1) {
                this._BOOL_IsPersistent = Boolean.parseBoolean(results[0]);
                this._BOOL_IsMaintenance = Boolean.parseBoolean(results[1]);
                this._STR_Title = results[2];
                this._STR_Details = results[3];
                if (results.length > 4)
                    _STR_Image_URL = results[4];
            }
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.BTN_notice_okay:
                dismiss(); break;
            default:
                break;
        }
    }
    public class GetImageFromWeb extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL imageURL = null;
            try {
                imageURL = new URL(_STR_Image_URL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                _helper.logger(e);
            }
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            try {
                bitmap = BitmapFactory.decodeStream(fetch(imageURL.toString()), null, options);
            } catch (OutOfMemoryError e){
                try{
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    return bitmap;
                }catch (Exception ex){
                    _helper.logger(ex);
                }

            }catch (IOException exc){
                _helper.logger(exc);

            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                _FL_notice_image_loader.setVisibility(View.GONE);
                _IB_profile_loader_circle.setImageResource(0);
                _IV_notice_image.setImageBitmap(result);
                _IV_notice_image.setVisibility(View.VISIBLE);
            }
        }
        private InputStream fetch(String address) throws MalformedURLException,IOException {
            HttpGet httpRequest = new HttpGet(URI.create(address) );
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();
            return instream;
        }
    }
}
