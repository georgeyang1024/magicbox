package online.magicbox.desktop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import online.magicbox.desktop.R;


/**
 * Created by george.yang on 2016-3-30.
 */
public class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.NormalTextViewHolder> {
private final LayoutInflater mLayoutInflater;
private final Context mContext;
private String[] mTitles;

public NormalRecyclerViewAdapter(Context context) {
    List<String> list = new ArrayList<>();
    for (int i=0;i<50;i++) {
        list.add("#" + i);
    }
        mTitles = list.toArray(new String[50]);
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        }

@Override
public NormalTextViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new NormalTextViewHolder(mLayoutInflater.inflate(R.layout.item_text, parent, false));// ID #0x7f04001d
        }

@Override
public void onBindViewHolder(NormalTextViewHolder holder, int position) {
    try {
        holder.mTextView.setText(mTitles[position]);
    } catch (Exception e) {

    }
}

@Override
public int getItemCount() {
        return mTitles == null ? 0 : mTitles.length;
        }

public static class NormalTextViewHolder extends RecyclerView.ViewHolder {
    TextView mTextView;

    NormalTextViewHolder(View view) {
        super(view);
        mTextView = (TextView) view.findViewById(R.id.text);

    }
}
}
