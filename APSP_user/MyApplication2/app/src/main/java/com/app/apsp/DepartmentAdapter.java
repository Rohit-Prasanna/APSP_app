
package com.app.apsp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import com.app.apsp.R;


public class DepartmentAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] values;
    private final String semname;

    public DepartmentAdapter(Context context, String[] values,String semname) {
        super(context, R.layout.custom_grid_item, values);
        this.context = context;
        this.values = values;
        this.semname =semname;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView;
        if (convertView == null) {
            gridView = inflater.inflate(R.layout.custom_grid_item, null);
            Button button = gridView.findViewById(R.id.gridItemButton);
            button.setOnClickListener(view -> {
                Intent intent = new Intent(context,YearActivity.class);
                intent.putExtra("Department_name", values[position]);
                intent.putExtra("SemName", semname);
                context.startActivity(intent);

            });
            button.setText(values[position]);
        } else {
            gridView = convertView;
        }
        return gridView;
    }
}