package com.example.thomas.mybooksscanner.ui.views;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thomas.mybooksscanner.DataRepository;
import com.example.thomas.mybooksscanner.ExportFilesDirectory;
import com.example.thomas.mybooksscanner.R;
import com.example.thomas.mybooksscanner.databinding.ListFragmentBinding;
import com.example.thomas.mybooksscanner.model.BookEntity;
import com.example.thomas.mybooksscanner.ui.adapters.BookAdapter;
import com.example.thomas.mybooksscanner.viewmodel.BookListViewModel;

import java.io.File;
import java.util.List;

/**
 * Created by Thomas on 28.07.2018.
 */

public class BookListFragment extends Fragment {

    public static final String TAG = "BookListViewModel";

    private BookAdapter mBookAdapter;

    private ListFragmentBinding mBinding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mBinding = DataBindingUtil.inflate(inflater, R.layout.list_fragment, container, false);
        mBookAdapter = new BookAdapter();
        mBinding.bookList.setAdapter(mBookAdapter);

        return mBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String filename = getActivity().getIntent().getStringExtra(MainActivity.FILENAME);

        File file = null;
        if (filename != null) file = ExportFilesDirectory.getInstance().getFile(filename);
        if (file != null) {
            // Create a new data repository with the content of the selected file.
            // This repository will later be accessed when the BookListViewModel is created
            new DataRepository(file);
        }

        final BookListViewModel viewModel =
                ViewModelProviders.of(this).get(BookListViewModel.class);

        subscribeUi(viewModel);
    }

    private void subscribeUi(BookListViewModel viewModel) {
        // Update the list when the data changes
        viewModel.getBooks().observe(this, new Observer<List<BookEntity>>() {
            @Override
            public void onChanged(@Nullable List<BookEntity> myBookings) {
                if (myBookings != null) {
                    mBookAdapter.setBookList(myBookings);
                }
                // espresso does not know how to wait for data binding's loop so we execute changes
                // sync.
                mBinding.executePendingBindings();
            }
        });
    }
}