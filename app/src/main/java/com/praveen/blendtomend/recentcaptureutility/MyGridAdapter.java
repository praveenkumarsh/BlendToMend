/**
 * Created by Praveen on 16/7/2020.
 */

package com.praveen.blendtomend.recentcaptureutility;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.praveen.blendtomend.R;

import java.util.List;

class MyGridAdapter extends BaseAdapter {
    LayoutInflater inflater;
    List<GridViewItem> items;


    public MyGridAdapter(Context context, List<GridViewItem> items) {
        this.items = items;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return items.size();
    }


    @Override
    public Object getItem(int position) {
        return items.get(position);
    }


    @Override
    public long getItemId(int position) {
        return position;
    }




    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item, null);
        }

        TextView text = (TextView) convertView.findViewById(R.id.textView);
        String[] FileName = items.get(position).getPath().split("Monument/");
        text.setText(FileName[1]);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.imageView);
        Bitmap image = items.get(position).getImage();
        if(items.get(position).getPath().contains(".mp4"))
        {
            image = ThumbnailUtils.createVideoThumbnail(items.get(position).getPath(), 0); //Creation of Thumbnail of video
        }
        if (image != null){
            imageView.setImageBitmap(image);
        }
        else {
            // If no image is provided, display a folder icon.
//            imageView.setImageResource(R.drawable.your_folder_icon);
        }

        return convertView;
    }

}