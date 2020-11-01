package com.deckerth.thomas.mybooksscanner.ui.views;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deckerth.thomas.mybooksscanner.BasicApp;
import com.deckerth.thomas.mybooksscanner.R;
import com.deckerth.thomas.mybooksscanner.databinding.ExportFilesFragmentBinding;
import com.deckerth.thomas.mybooksscanner.ui.adapters.ExportFileAdapter;
import com.deckerth.thomas.mybooksscanner.viewmodel.FilesListViewModel;

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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
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
        viewModel.getFiles().observe(this, myFiles -> {
            if (myFiles != null) {
                mExportFileAdapter.setFileList(myFiles);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        Intent intent = new Intent(BasicApp.getContext(),MainActivity.class);
        intent.putExtra(MainActivity.FILENAME,mExportFileAdapter.getItem(position).getFile().getName());
        startActivity(intent);
    }
}

