package com.svs.adapter;

import android.os.Environment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.svs.R;
import com.svs.downloads;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Created by chaki on 17-04-2016.
 */
public class DownloadAdapter extends RecyclerView.Adapter<DownloadAdapter.MyViewHolder>  {

    private List<downloads> dList;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView id, name, file;
        public ImageView imgDownloadFile;

        public MyViewHolder(View view) {
            super(view);
            id = (TextView) view.findViewById(R.id.lblsubNo);
            name = (TextView) view.findViewById(R.id.lblSubName);
            file = (TextView) view.findViewById(R.id.lblSubFile);
            imgDownloadFile = (ImageView) view.findViewById(R.id.imgDownloadFile);

            imgDownloadFile.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

            if (v.getId() == imgDownloadFile.getId()){

                Toast.makeText(v.getContext(), "ITEM PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();

                try {
                    String f = file.getText().toString();
                    String filename = name.getText().toString();

                    //new DownloadFileFromURL().execute(f);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(v.getContext(), "ROW PRESSED = " + String.valueOf(getAdapterPosition()), Toast.LENGTH_SHORT).show();
            }
        }
    }


    public DownloadAdapter(List<downloads> dList) {
        this.dList = dList;
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_sub_download_items, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        final downloads movie = dList.get(position);
        holder.id.setText(movie.getId());
        holder.name.setText(movie.getFile());
        holder.file.setText(movie.getFile());




    }


    @Override
    public int getItemCount() {
        return dList.size();
    }





}
