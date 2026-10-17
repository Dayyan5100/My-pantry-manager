package com.example.mypantrymanage;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
public class PantryAdapter extends BaseAdapter   {


    private final Context context;
    private final LayoutInflater inflater;
    private final ArrayList<PantryItem> items =
            new ArrayList<>(); // ingredients currently displayed

    public PantryAdapter(Context context)  {
        this.context= context;
        inflater =  LayoutInflater.from(context);
    }



    public void setItems(List<PantryItem> newItems)   {

        items.clear(); // removes the previous displayed records

        items.addAll( newItems ); // copies the latest database records

        notifyDataSetChanged(); // tells the listview to refresh
    }

    @Override
    public int getCount() {
        return items.size(); // number of rows needed
    }



    @Override
    public PantryItem getItem(int position)   {

        return items.get( position ); // ingredient for this row
    }
    @Override
    public long getItemId(int position) {
        return getItem(position).getId(); // uses the database id

    }



    @Override
    public boolean hasStableIds()  {
        return true; // an ingredient keeps its database id
    }



    @Override
    public View getView(
            int position,
            View convertView,
            ViewGroup parent
    )   {

        ViewHolder holder;


        if(convertView == null)  {
            convertView = inflater.inflate(
                    R.layout.row_pantry_item,
                    parent,
                    false
            ); // creates a row when no reusable row is available


            holder= new ViewHolder();
            holder.nameView = convertView.findViewById(
                    R.id.text_item_name
            );
            holder.quantityView= convertView.findViewById(
                    R.id.text_item_quantity
            );
            convertView.setTag( holder ); // keeps references for reuse
        } else  {
            holder = (ViewHolder) convertView.getTag();
        }


        PantryItem item= getItem(position);
        holder.nameView.setText( item.getName() );
        String quantityText = BigDecimal
                .valueOf( item.getQuantity() )
                .stripTrailingZeros()
                .toPlainString(); // displays 500 instead of 500.0
        holder.quantityView.setText(
                context.getString(
                        R.string.pantry_quantity_display,
                        quantityText,
                        item.getUnit()
                )

        );


        return convertView; // returns the completed row
    }



    private static class ViewHolder   {

        TextView nameView;
        TextView quantityView;
    }

}
