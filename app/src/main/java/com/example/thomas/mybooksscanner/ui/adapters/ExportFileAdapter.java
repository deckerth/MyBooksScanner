package com.example.thomas.mybooksscanner.ui.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.thomas.mybooksscanner.R;
import com.example.thomas.mybooksscanner.databinding.FileItemBinding;
import com.example.thomas.mybooksscanner.model.ExportFile;

import java.util.List;
import java.util.Objects;

public class ExportFileAdapter extends RecyclerView.Adapter<ExportFileAdapter.ExportFileViewHolder>{

    private List<? extends ExportFile> mFileList;
    private ItemClickListener mClickListener;


    public void setFileList(final List<? extends ExportFile> fileList) {
        if (mFileList == null) {
            mFileList = fileList;
            notifyItemRangeInserted(0, fileList.size());
        } else {
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
                @Override
                public int getOldListSize() {
                    return mFileList.size();
                }

                @Override
                public int getNewListSize() {
                    return fileList.size();
                }

                @Override
                public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                    return mFileList.get(oldItemPosition).getFile().getName().equals(fileList.get(newItemPosition).getFile().getName()) &&
                           mFileList.get(oldItemPosition).getSelected() == fileList.get(newItemPosition).getSelected();
                }

                @Override
                public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                    ExportFile newFile = fileList.get(newItemPosition);
                    ExportFile oldFile = mFileList.get(oldItemPosition);
                    return newFile.getFile().getName().equals(oldFile.getFile().getName()) &&
                           newFile.getSelected() == oldFile.getSelected();
                }
            });
            mFileList = fileList;
            result.dispatchUpdatesTo(this);
        }
    }

    @NonNull
    @Override
    public ExportFileAdapter.ExportFileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        FileItemBinding binding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.getContext()), R.layout.file_item,
                        parent, false);
        return new ExportFileAdapter.ExportFileViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ExportFileAdapter.ExportFileViewHolder holder, int position) {
        holder.binding.setFile((ExportFile)mFileList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }

    public class ExportFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final FileItemBinding binding;

        public ExportFileViewHolder(FileItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
        }
    }

    // convenience method for getting data at click position
    public ExportFile getItem(int id) {
        return mFileList.get(id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

}
