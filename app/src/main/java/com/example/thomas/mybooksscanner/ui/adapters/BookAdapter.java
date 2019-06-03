package com.example.thomas.mybooksscanner.ui.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.example.thomas.mybooksscanner.databinding.BookItemBinding;
import com.example.thomas.mybooksscanner.R;
import com.example.thomas.mybooksscanner.model.Book;

import java.util.List;
import java.util.Objects;

/**
 * Created by Thomas on 20.07.2018.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder> {

    private List<? extends Book> mBookList;

    public void setBookList(final List<? extends Book> bookList) {
        if (mBookList == null) {
            mBookList = bookList;
            notifyItemRangeInserted(0, bookList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mBookList.size();
                }

                @Override
                public int getNewListSize() {
                    return bookList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mBookList.get(oldItemPosition).getISBN() ==
                            bookList.get(newItemPosition).getISBN();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    Book newBook = bookList.get(newItemPosition);
                    Book oldBook = mBookList.get(oldItemPosition);
                    return newBook.getISBN() == oldBook.getISBN()
                            && Objects.equals(newBook.getTitle(), oldBook.getTitle());
                }
            });
            mBookList = bookList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public BookAdapter.BookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BookItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.book_item,
                        parent, false);
        return new BookViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BookAdapter.BookViewHolder holder, int position) {
        holder.binding.setBook(mBookList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mBookList == null ? 0 : mBookList.size();
    }

    static class BookViewHolder extends RecyclerView.ViewHolder {

        final BookItemBinding binding;

        public BookViewHolder(BookItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

}
