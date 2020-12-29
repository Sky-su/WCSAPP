package com.example.wcsapp.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.wcsapp.R;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LayList extends BaseAdapter {

    private List<ReplenishEntity> mData;
    private Context context;
    public LayList(List<ReplenishEntity> mData, Context context) {
        this.mData = mData;
        this.context = context;
    }

    public void clear() {
        if (mData != null){
            mData.clear();
            notifyDataSetChanged();
        }

    }
    @Override
    public int getCount() {
        return mData.size();
    }
    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_showtasklist,null);
            holder = new ViewHolder();
            holder.showtasklistgoodnames = (TextView)convertView.findViewById(R.id.showtasklistgoodnames);
            holder.showtasklistskuid = (TextView) convertView.findViewById(R.id.showtasklistskuid);
            holder.showtasklistskunumber = (TextView) convertView.findViewById(R.id.showtasklistskunumber);
            holder.showtasklistskuTime = (TextView) convertView.findViewById(R.id.showtasklistskuTime);
            holder.showtasklistscount = convertView.findViewById(R.id.showtasklistskucount);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        //赋值
        holder.showtasklistgoodnames.setText(mData.get(position).getGoodsname());
        holder.showtasklistskuid.setText(mData.get(position).getSku());
        holder.showtasklistskunumber.setText(mData.get(position).getQty());
        holder.showtasklistskuTime.setText(mData.get(position).getCreatetime());
        holder.showtasklistscount.setText(mData.get(position).getLocation());
        notifyDataSetChanged();
        return convertView;
    }
    public void showaddlist(List<ReplenishEntity>ko){
        if (mData == null) {
            mData = new LinkedList<>();
        } else {
            mData.clear();
            mData.addAll(ko);
            notifyDataSetChanged();
        }
    }
    public void additem(ReplenishEntity data) {
        if (mData == null) {
            mData = new LinkedList<>();
        } else {
            mData.add(1,data);
            notifyDataSetChanged();
        }
    }
    public void removeitem() {
        if (mData != null) {
            mData.clear();
        }
    }
    public class ViewHolder{
        TextView showtasklistgoodnames;
        TextView showtasklistskuid;
        TextView showtasklistskunumber;
        TextView showtasklistskuTime;
        TextView showtasklistscount;
    }
}
