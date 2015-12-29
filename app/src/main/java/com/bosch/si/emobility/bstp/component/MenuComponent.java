package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.Activity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class MenuComponent extends Component {

    private static MenuComponent ourInstance = new MenuComponent();

    public static MenuComponent getInstance(Activity activity) {
        if (activity != null)
            ourInstance.setActivity(activity);
        return ourInstance;
    }

    private MenuComponent() {
        super();
    }

    ListView listViewMenu;
    ArrayAdapter<String> listViewMenuAdapter;

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (RelativeLayout) this.activity.findViewById(R.id.menuLayout);

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
