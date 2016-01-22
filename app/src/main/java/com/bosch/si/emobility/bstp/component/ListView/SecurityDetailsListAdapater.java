package com.bosch.si.emobility.bstp.component.ListView;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.model.SecurityDetails;

import java.util.List;

/**
 * Created by SSY1SGP on 22/1/16.
 */
public class SecurityDetailsListAdapater extends ArrayAdapter<SecurityDetails> {

    private final Context context;
    private final List<SecurityDetails> securityDetails;

    public SecurityDetailsListAdapater(Context context, int resource, List<SecurityDetails> details) {
        super(context, resource);
        this.context = context;
        this.securityDetails = details;
    }

    @Override
    public int getCount() {
        return securityDetails.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.security_item, parent, false);

        SecurityDetails itemData = this.securityDetails.get(position);
        TextView textView = (TextView) rowView.findViewById(R.id.security_item_text);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.security_item_image);

        if (itemData != null){
            textView.setText(itemData.getSecurityDetailName());
            if (itemData.getIsAvailable()){
                imageView.setImageResource(R.drawable.icon_shield_hi);
                textView.setTextColor(this.context.getResources().getColor(R.color.primary));
            }
            else {
                imageView.setImageResource(R.drawable.icon_shield);
                textView.setTextColor(Color.GRAY);
            }
        }
        else {
            textView.setText("--");
            textView.setTextColor(Color.GRAY);
            imageView.setImageResource(R.drawable.icon_shield);
        }

        return rowView;
    }
}
