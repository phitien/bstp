package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Component;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MenuComponent extends Component {

    ListView listViewMenu;
    ArrayAdapter<String> listViewMenuAdapter;

    public MenuComponent(Activity activity) {
        super(activity);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (ViewGroup) this.activity.findViewById(R.id.menuLayout);

        layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEnabled(false, false);
            }
        });

        setEnabled(false, true);
        String[] values = new String[]{
                this.activity.getString(R.string.home),
                this.activity.getString(R.string.upcoming),
                this.activity.getString(R.string.about),
                this.activity.getString(R.string.logout),
        };

        listViewMenuAdapter = new ArrayAdapter<>(this.activity, R.layout.menu_item, R.id.textViewMenu, values);
        listViewMenu = (ListView) this.activity.findViewById(R.id.listViewMenu);
        listViewMenu.setAdapter(listViewMenuAdapter);
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        listViewMenu.setOnItemClickListener(listener);
    }

    @Override
    protected boolean isSlideLeft() {
        return true;
    }
}
