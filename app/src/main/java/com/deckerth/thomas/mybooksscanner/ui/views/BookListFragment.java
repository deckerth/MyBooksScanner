package com.deckerth.thomas.mybooksscanner.ui.views;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deckerth.thomas.mybooksscanner.ExportFileContent;
import com.deckerth.thomas.mybooksscanner.ExportFilesDirectory;
import com.deckerth.thomas.mybooksscanner.R;
import com.deckerth.thomas.mybooksscanner.databinding.ListFragmentBinding;
import com.deckerth.thomas.mybooksscanner.ui.adapters.BookAdapter;
import com.deckerth.thomas.mybooksscanner.viewmodel.BookListViewModel;
import com.deckerth.thomas.mybooksscanner.viewmodel.ExportFileContentViewModel;

import java.io.File;
import java.util.Objects;

/**
 * Created by Thomas on 28.07.2018.
 */

public class BookListFragment extends Fragment {

    public static final String TAG = "BookListViewModel";

    private BookAdapter mBookAdapter;

    private ListFragmentBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.list_fragment, container, false);
        mBookAdapter = new BookAdapter();
        mBinding.bookList.setAdapter(mBookAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String filename = Objects.requireNonNull(getActivity()).getIntent().getStringExtra(MainActivity.FILENAME);

        File file = null;
        if (filename != null) file = ExportFilesDirectory.getInstance().getFile(filename);
        if (file != null) {
            // Create a new data repository with the content of the selected file.
            // This repository will later be accessed when the BookListViewModel is created
            ExportFileContent.getInstance().LoadFile(file);
            final ExportFileContentViewModel viewModel =
                    ViewModelProviders.of(this).get(ExportFileContentViewModel.class);
            subscribeUiForExportFileContent(viewModel);
        } else {
            final BookListViewModel viewModel =
                    ViewModelProviders.of(this).get(BookListViewModel.class);

            subscribeUiForBookList(viewModel);
        }
    }

    private void subscribeUiForBookList(BookListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getBooks().observe(this, books -> {
            if (books != null) {
                mBookAdapter.setBookList(books);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

    private void subscribeUiForExportFileContent(ExportFileContentViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getBooks().observe(this, books -> {
            if (books != null) {
                mBookAdapter.setBookList(books);
            }
            // espresso does not know how to wait for data binding's loop so we execute changes
            // sync.
            mBinding.executePendingBindings();
        });
    }

}
