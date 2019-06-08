package com.howard.sleephelper.recyclerView;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.howard.sleephelper.R;
import com.howard.sleephelper.RecordDetails;

import java.util.List;

public class TraceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater inflater;
    private List<Trace> traceList;
    private Context context;
    private String date;

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView mDate, mTime;
        private TextView mTopLine;
        private ImageView mDot;

        ViewHolder(View itemView) {
            super(itemView);
            mDate = itemView.findViewById(R.id.Date);
            mTime = itemView.findViewById(R.id.Time);
            mTopLine = itemView.findViewById(R.id.TopLine);
            mDot = itemView.findViewById(R.id.Dot);
        }

        void bindHolder(Trace trace) {
            mDate.setText(trace.getDate());
            mTime.setText(trace.getTime());
        }
    }

    public TraceListAdapter(Context context, List<Trace> traceList, String date) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        this.traceList = traceList;
        this.date = date;
    }

    @Override
    @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.list_cell, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        if (position == 0) {
            // 第一行头的竖线不显示
            itemHolder.mTopLine.setVisibility(View.INVISIBLE);
            itemHolder.mDot.setBackgroundResource(R.drawable.timeline);
        } else if (position > 0) {
            itemHolder.mTopLine.setVisibility(View.VISIBLE);
            itemHolder.mDot.setBackgroundResource(R.drawable.timeline);
        }
        itemHolder.bindHolder(traceList.get(position));
        final TextView tv1 = holder.itemView.findViewById(R.id.Date);
        final TextView tv2 = holder.itemView.findViewById(R.id.Time);
        tv1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, RecordDetails.class);
                        i.putExtra("date", date);
                        i.putExtra("position", holder.getAdapterPosition());
                        context.startActivity(i);
                    }
                }
        );
        tv2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, RecordDetails.class);
                        i.putExtra("date", date);
                        i.putExtra("position", holder.getAdapterPosition());
                        context.startActivity(i);
                    }
                }
        );
    }

    @Override
    public int getItemCount() {
        return traceList.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    private OnItemClickListener mOnItemClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
