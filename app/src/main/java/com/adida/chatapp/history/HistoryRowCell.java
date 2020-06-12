package com.adida.chatapp.history;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.adida.chatapp.R;
import com.adida.chatapp.entities.HistoryCell;

import java.util.ArrayList;

public class HistoryRowCell extends BaseAdapter {

    Context mContext;
    ArrayList<HistoryCell> listArr;

    public HistoryRowCell(Context context, ArrayList<HistoryCell> listArr){
        this.mContext = context;
        this.listArr = listArr;
    }

    @Override
    public int getCount() {
        return listArr.size();
    }

    @Override
    public Object getItem(int position) {
        return listArr.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View row = inflater.inflate(R.layout.history_cell, parent, false);

        HistoryCell cell = (HistoryCell) getItem(position);

        TextView listNameOnTheLeft = (TextView) row.findViewById(R.id.txtTextLeft);
        TextView listTimeOnTheRight = (TextView) row.findViewById(R.id.txtTextRight);


        listNameOnTheLeft.setText(cell.NameOnTheleft);
        listTimeOnTheRight.setText(cell.TimeOnTheRight);


        return (row);
    }
}
