package uqac.dim.pallll;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import uqac.dim.paldle.R;

public class CustomAdapter extends ArrayAdapter<String>  {

    private final LayoutInflater inflater;
    private List<Pal> palList;


    public CustomAdapter(Context context, List<Pal> palList, List<String>palName) {
        super(context, 0, palName);
        inflater = LayoutInflater.from(context);
        this.palList = palList;
    }


    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.custom_dropdown_item, parent, false);
        }

        ImageView imageView = view.findViewById(R.id.imageView);
        TextView textView = view.findViewById(R.id.textView);

        String name = getItem(position);
        for (Pal pal : palList) {
            if (pal.getName().equals(name)) {
                textView.setText(pal.getName());
                Picasso.get().load(pal.getImage()).into(imageView);
                break;
            }
        }

        return view;
    }
}

