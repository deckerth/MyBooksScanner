package com.example.thomas.mybooksscanner.ui.views;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.example.thomas.mybooksscanner.ExportFilesDirectory;
import com.example.thomas.mybooksscanner.R;

public class ExportFilesListActitity extends AppCompatActivity {

    public static ExportFilesListActitity sInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sInstance = this;
        setContentView(R.layout.activity_export_files_list_actitity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ExportFilesDirectory.getInstance().getSelectedCount() == 0) {
                    Snackbar.make(view, getText(R.string.select_export_files), Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    ExportFilesDirectory.getInstance().deleteSelectedFiles();
                }
            }
        });

        CheckBox selectAll = (CheckBox)findViewById(R.id.select_all);
        selectAll.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                   ExportFilesDirectory.getInstance().selectAll(isChecked);
                                               }
                                           }
        );        // Add list fragment if this is first creation
        if (savedInstanceState == null) {
            ExportFilesListActitityFragment fragment = new ExportFilesListActitityFragment();

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.export_files_list, fragment, ExportFilesListActitityFragment.TAG).commit();
        }

    }

}
