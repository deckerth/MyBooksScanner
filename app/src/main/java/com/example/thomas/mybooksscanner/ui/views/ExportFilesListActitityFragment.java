package com.example.thomas.mybooksscanner.ui.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.example.thomas.mybooksscanner.BasicApp;
import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.R;
import com.example.thomas.mybooksscanner.databinding.ExportFilesFragmentBinding;
import com.example.thomas.mybooksscanner.model.BookEntity;
import com.example.thomas.mybooksscanner.model.ExportFileEntity;
import com.example.thomas.mybooksscanner.service.Settings;
import com.example.thomas.mybooksscanner.ui.adapters.ExportFileAdapter;
import com.example.thomas.mybooksscanner.viewmodel.BookListViewModel;
import com.example.thomas.mybooksscanner.viewmodel.FilesListViewModel;

import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class ExportFilesListActitityFragment extends Fragment implements  ExportFileAdapter.ItemClickListener {

    public static final String TAG = "ExportFilesViewModel";

    private ExportFileAdapter mExportFileAdapter;

    private ExportFilesFragmentBinding mBinding;

    public ExportFilesListActitityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.export_files_fragment, container, false);
        mExportFileAdapter = new ExportFileAdapter();
        mExportFileAdapter.setClickListener(this);
        mBinding.exportFilesList.setAdapter(mExportFileAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final FilesListViewModel viewModel =
                ViewModelProviders.of(this).get(FilesListViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(FilesListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getFiles().observe(this, new Observer<List<ExportFileEntity>>() {
            @Override
            public void onChanged(@Nullable List<ExportFileEntity> myFiles) {
                if (myFiles != null) {
                    mExportFileAdapter.setFileList(myFiles);
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                mBinding.executePendingBindings();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(BasicApp.getContext(),MainActivity.class);
        intent.putExtra(MainActivity.FILENAME,mExportFileAdapter.getItem(position).getFile().getName());
        startActivity(intent);
    }
}

