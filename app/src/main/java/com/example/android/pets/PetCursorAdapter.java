package com.example.android.pets;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;

import org.w3c.dom.Text;

public class PetCursorAdapter extends CursorAdapter {
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c,0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        TextView t1= (TextView) view.findViewById(R.id.t1);
        TextView t2= (TextView) view.findViewById(R.id.t2);

        String s1=cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetsEntry.COLUMN_PET_NAME));
        String s2=cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetsEntry.COLUMN_PET_BREED));

        t1.setText(s1);
        t2.setText(s2);


    }
}
