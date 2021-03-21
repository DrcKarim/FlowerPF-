package com.BestofallPhotography.PhotoFrameFlowerPF.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.BestofallPhotography.PhotoFrameFlowerPF.R;
import com.squareup.picasso.Picasso;

public class FrameAdapter extends RecyclerView.Adapter<FrameAdapter.FrameViewHolder> {

    private Integer[] frameId;
    private FrameOnClickListener listener;

    public FrameAdapter(Integer[] frameId,FrameOnClickListener listener) {
        this.frameId = frameId;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FrameViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.frame_image, parent,false);
        return new FrameViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FrameViewHolder holder, int position) {

        //holder.frameImage.setImageResource(frameId[position]);

        Picasso.get()
                .load(frameId[position])
                .resize(50, 50)
                .centerCrop()
                .into(holder.frameImage);

        holder.frameImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFrameSelected(holder.getAdapterPosition());
            }
        });
    }



    @Override
    public int getItemCount() {
        return frameId.length;
    }

    class FrameViewHolder extends RecyclerView.ViewHolder {
        ImageView frameImage;
        FrameViewHolder(View itemView) {
            super(itemView);
            frameImage = itemView.findViewById(R.id.frameImage);
        }
    }

    public interface FrameOnClickListener{
        void onFrameSelected(int id);
    }
}
