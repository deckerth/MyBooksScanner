package com.deckerth.thomas.mybooksscanner.ui.adapters;

import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.DiffUtil;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.deckerth.thomas.mybooksscanner.R;
import com.deckerth.thomas.mybooksscanner.databinding.FileItemBinding;
import com.deckerth.thomas.mybooksscanner.model.ExportFile;

import java.util.List;

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
        holder.binding.setFile(mFileList.get(position));
        holder.binding.executePendingBindings();
    }

    @Override
    public int getItemCount() {
        return mFileList == null ? 0 : mFileList.size();
    }

    public class ExportFileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final FileItemBinding binding;

        ExportFileViewHolder(FileItemBinding binding) {
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
