package com.deckerth.thomas.mybooksscanner.ui.views;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageButton;

import com.deckerth.thomas.mybooksscanner.BasicApp;
import com.deckerth.thomas.mybooksscanner.DataRepository;
import com.deckerth.thomas.mybooksscanner.ExportFilesDirectory;
import com.deckerth.thomas.mybooksscanner.R;
import com.deckerth.thomas.mybooksscanner.model.BookEntity;
import com.deckerth.thomas.mybooksscanner.service.ISBNQueryService;
import com.deckerth.thomas.mybooksscanner.service.Settings;
import com.deckerth.thomas.mybooksscanner.viewmodel.MainActivityViewModel;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import java.io.File;
import java.util.Objects;

import static com.deckerth.thomas.mybooksscanner.service.ISBNQueryService.QUERY_REQUEST_CODE;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // constants used to pass extra data in the intent
    public static final String FILENAME = "filename";

    private static final int RC_BARCODE_CAPTURE = 9001;

    public static MainActivity mInstance = null;

    private Boolean mFileDisplayMode = Boolean.FALSE;
    private File mFile;
    private Button mShare;
    private ImageButton mDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mInstance == null) {
            mInstance = this;
        }

        setContentView(R.layout.activity_main);

        String filename = getIntent().getStringExtra(FILENAME);
        mFileDisplayMode = (filename != null);
        if (mFileDisplayMode) mFile = ExportFilesDirectory.getInstance().getFile(filename);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(mFloatingButtonListener);
        if (mFileDisplayMode) {
            fab.hide();
        } else {
            fab.show();
        }

        mShare = findViewById(R.id.share_button);
        mShare.setOnClickListener(mShareButtonListener);

        mDelete = findViewById(R.id.delete_button);
        mDelete.setOnClickListener(mDeleteButtonListener);

        final MainActivityViewModel viewModel =
                ViewModelProviders.of(this).get(MainActivityViewModel.class);

        subscribeUi(viewModel);

        if (mFileDisplayMode) {
            Objects.requireNonNull(getSupportActionBar()).setTitle(filename);
            mDelete.setVisibility(View.GONE);
        }

        // Add list fragment if this is first creation
        if (savedInstanceState == null) {
            BookListFragment fragment = new BookListFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment, BookListFragment.TAG).commit();
        }
    }

// --Commented out by Inspection START (09.12.2019 20:00):
//    /**
//     * Sets the image button to the given state and grays-out the icon.
//     *
//     * @param ctxt The context
//     * @param enabled The state of the button
//     * @param item The button item to modify
//     * @param iconResId The button's icon ID
//     */
//    public static void setImageButtonEnabled(Context ctxt, boolean enabled,
//                                             ImageButton item, int iconResId) {
//
//        item.setEnabled(enabled);
//        Drawable originalIcon = ctxt.getResources().getDrawable(iconResId);
//        Drawable icon = enabled ? originalIcon : convertDrawableToGrayScale(originalIcon);
//        item.setImageDrawable(icon);
//    }
// --Commented out by Inspection STOP (09.12.2019 20:00)

    /**
     * Mutates and applies a filter that converts the given drawable to a Gray
     * image. This method may be used to simulate the color of disable icons in
     * Honeycomb's ActionBar.
     *
     * @return a mutated version of the given drawable with a color filter applied.
     */
    private static Drawable convertDrawableToGrayScale(Drawable drawable) {
        if (drawable == null)
            return null;

        Drawable res = drawable.mutate();
        res.setColorFilter(Color.GRAY, PorterDuff.Mode.SRC_IN);
        return res;
    }
    private void subscribeUi(MainActivityViewModel viewModel) {
        // Update the list when the data changes
        if (mFileDisplayMode)  {
            viewModel.getExportFileFilled().observe(this, booksExist -> {
                if (booksExist != null) {
                    mShare.setEnabled(booksExist);
                }
            });
        } else {
            viewModel.getBooksExist().observe(this, booksExist -> {
                if (booksExist != null) {
                    mShare.setEnabled(booksExist);
                    mDelete.setVisibility(booksExist ? View.VISIBLE : View.GONE);
                }
            });
        }
    }

    private void startScanner() {
        Intent intent = new Intent(mInstance,BarcodeScannerActivity.class);
        intent.putExtra(BarcodeScannerActivity.AutoFocus, Settings.getInstance().getAutoFocus());
        intent.putExtra(BarcodeScannerActivity.UseFlash, Settings.getInstance().getUseFlash());

        startActivityForResult(intent, RC_BARCODE_CAPTURE);
    }


    private final FloatingActionButton.OnClickListener mFloatingButtonListener = view -> {
        // launch barcode activity.
    startScanner();
    };

    private final Button.OnClickListener mShareButtonListener = new Button.OnClickListener() {
        public void onClick(View view) {
            Uri uri;
            if (mFileDisplayMode) {
                uri = FileProvider.getUriForFile(BasicApp.getContext(),"com.deckerth.thomas.mybooksscanner.fileprovider",mFile);
            } else {
                uri = DataRepository.getInstance().exportBooksToXML(view.getContext());
            }
            if (uri == null) return;

            String message = BasicApp.getContext().getResources().getString(R.string.file_saved);
            message = message.replace("&", Objects.requireNonNull(uri.getLastPathSegment()));
            Snackbar.make(getWindow().getDecorView().getRootView(),message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("application/xml");
            // Bundle the asset content uri as the EXTRA_STREAM uri
            intent.putExtra(Intent.EXTRA_STREAM, uri);
            startActivity(intent);
        }
    };

    private final Button.OnClickListener mDeleteButtonListener = v -> {
        if (Objects.requireNonNull(DataRepository.getInstance().getBooks().getValue()).size() > 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.confirm_deletion_message)
                    .setTitle(R.string.confirm_deletion_title);
            // Add the buttons
            builder.setPositiveButton(R.string.yes, (dialog, id) -> {
                // User clicked OK button
                DataRepository.getInstance().Clear();
            });
            builder.setNegativeButton(R.string.no, (dialog, id) -> {
                // User cancelled the dialog
            });
            // Create the AlertDialog
            AlertDialog dialog = builder.create();
            dialog.show();
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
            Intent intent = new Intent(mInstance, ExportFilesListActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_save_scanned_books) {
            Uri uri = DataRepository.getInstance().exportBooksToXML(BasicApp.getContext());
            if ( uri != null) {
                String message = BasicApp.getContext().getResources().getString(R.string.file_saved);
                message = message.replace("&", Objects.requireNonNull(uri.getLastPathSegment()));
                Snackbar.make(getWindow().getDecorView().getRootView(),message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            return true;
        } else if (id == R.id.action_help) {
            Intent intent = new Intent(mInstance, HelpBrowserActivity.class);
            startActivity(intent);
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
