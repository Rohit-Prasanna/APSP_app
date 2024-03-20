
package com.app.apsp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.app.apsp.R;


public class SemAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;


    public SemAdapter(Context context, String[] values) {
        super(context, R.layout.custom_grid_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.custom_grid_item, null);
            Button button = gridView.findViewById(R.id.gridItemButton);

            button.setOnClickListener(view -> {
                Intent intent = new Intent(context,DepartmentActivity.class);
                intent.putExtra("SemName", values[position]);
                context.startActivity(intent);

            });
            button.setText(values[position]);
        } else {
            gridView = convertView;
        }
        return gridView;
    }
}