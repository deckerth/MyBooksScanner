package com.example.thomas.mybooksscanner.ui.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ShareActionProvider;

import com.example.thomas.mybooksscanner.BasicApp;
import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.ExportFilesDirectory;
import com.example.thomas.mybooksscanner.R;
import com.example.thomas.mybooksscanner.model.BookEntity;
import com.example.thomas.mybooksscanner.service.ISBNQueryService;
import com.example.thomas.mybooksscanner.service.Settings;
import com.example.thomas.mybooksscanner.ui.adapters.BookAdapter;
import com.example.thomas.mybooksscanner.viewmodel.BookListViewModel;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.example.thomas.mybooksscanner.service.ISBNQueryService.QUERY_REQUEST_CODE;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "BookListViewModel";

    // constants used to pass extra data in the intent
    public static final String FILENAME = "filename";

    private static final int RC_BARCODE_CAPTURE = 9001;

    public static MainActivity mInstance;

    private Boolean mFileDisplayMode = Boolean.FALSE;
    private File mFile;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstance = this;
        setContentView(R.layout.activity_main);

        String filename = getIntent().getStringExtra(FILENAME);
        mFileDisplayMode = (filename != null);
        if (mFileDisplayMode) mFile = ExportFilesDirectory.getInstance().getFile(filename);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (mFileDisplayMode) getSupportActionBar().setTitle(filename);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(mFloatingButtonListener);
        fab.setVisibility(mFileDisplayMode ? View.INVISIBLE : View.VISIBLE);

        Button share = (Button) findViewById(R.id.share_button);
        share.setOnClickListener(mShareButtonListener);

        // Add list fragment if this is first creation
        if (savedInstanceState == null) {
            BookListFragment fragment = new BookListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, BookListFragment.TAG).commit();
        }
    }

    private void startScanner() {
        Intent intent = new Intent(mInstance,BarcodeScannerActivity.class);
        intent.putExtra(BarcodeScannerActivity.AutoFocus, Settings.getInstance().getAutoFocus());
        intent.putExtra(BarcodeScannerActivity.UseFlash, Settings.getInstance().getUseFlash());

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }


    private FloatingActionButton.OnClickListener mFloatingButtonListener = new FloatingActionButton.OnClickListener() {
        @Override
        public void onClick(View view) {
            // launch barcode activity.
        startScanner();
        }
    };

    private Button.OnClickListener mShareButtonListener = new Button.OnClickListener() {
        public void onClick(View view) {
            Uri uri = null;
            if (mFileDisplayMode) {
                uri = FileProvider.getUriForFile(BasicApp.getContext(),"com.example.thomas.mybooksscanner.fileprovider",mFile);
            } else {
                uri = DataRepository.getInstance().exportBooksToXML(view.getContext());
            }
            if (uri == null) return;

            String message = BasicApp.getContext().getResources().getString(R.string.file_saved);
            message = message.replace("&",uri.getLastPathSegment());
            Snackbar.make(getWindow().getDecorView().getRootView(),message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/xml");
            // Bundle the asset content uri as the EXTRA_STREAM uri
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(intent);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (mFileDisplayMode) return false;

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(mInstance, SettingsActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_export_files) {
            Intent intent = new Intent(mInstance, ExportFilesListActitity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_save_scanned_books) {
            Uri uri = DataRepository.getInstance().exportBooksToXML(BasicApp.getContext());
            if ( uri != null) {
                String message = BasicApp.getContext().getResources().getString(R.string.file_saved);
                message.replace("&",uri.getLastPathSegment());
                Snackbar.make(getWindow().getDecorView().getRootView(),message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeScannerActivity.BarcodeObject);
                    BookEntity scannedBook = new BookEntity(barcode.displayValue);
                    if ( DataRepository.getInstance().bookExists(scannedBook)) {
                        play(R.raw.relentless);
                        Snackbar.make(getWindow().getDecorView().getRootView(), R.string.duplicate_barcode, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        play(R.raw.stairs);
                        if (Settings.getInstance().getLookupTitles()){
                            ISBNQueryService.GetInstance().SendRequest(scannedBook);
                        } else  {
                            DataRepository.getInstance().storeBook(scannedBook);
                            if (Settings.getInstance().getPermanentScan()) {
                                startScanner();
                            }
                        }
                    }
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                } else {
                    Snackbar.make(getWindow().getDecorView().getRootView(), R.string.barcode_failure, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            }
        }
        else if (requestCode == QUERY_REQUEST_CODE) {
            ISBNQueryService.GetInstance().ProcessResult(requestCode,resultCode,data);
            if (Settings.getInstance().getPermanentScan()) {
                startScanner();
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private MediaPlayer mPlayer = new MediaPlayer();

    private void play(int resid) {

        mPlayer.release();
        mPlayer = MediaPlayer.create(getWindow().getDecorView().getRootView().getContext(), resid);
        mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPlayer.start();

    }

}