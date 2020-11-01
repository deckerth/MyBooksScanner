package com.deckerth.thomas.mybooksscanner.ui.views;

import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.CheckBox;

import com.deckerth.thomas.mybooksscanner.ExportFilesDirectory;
import com.deckerth.thomas.mybooksscanner.R;

public class ExportFilesListActivity extends AppCompatActivity {

    // --Commented out by Inspection (09.12.2019 19:59):private static ExportFilesListActivity sInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export_files_list_actitity);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            if (ExportFilesDirectory.getInstance().getSelectedCount() == 0) {
                Snackbar.make(view, getText(R.string.select_export_files), Snackbar.LENGTH_LONG)
                        .show();
            } else {
                ExportFilesDirectory.getInstance().deleteSelectedFiles();
            }
        });

        CheckBox selectAll = findViewById(R.id.select_all);
        selectAll.setOnCheckedChangeListener((buttonView, isChecked) -> ExportFilesDirectory.getInstance().selectAll(isChecked)
        );        // Add list fragment if this is first creation
        if (savedInstanceState == null) {
            ExportFilesListActitityFragment fragment = new ExportFilesListActitityFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.export_files_list, fragment, ExportFilesListActitityFragment.TAG).commit();
        }

    }

}
