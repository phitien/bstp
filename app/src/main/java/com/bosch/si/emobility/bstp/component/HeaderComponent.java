package com.bosch.si.emobility.bstp.component;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.Activity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class HeaderComponent extends Component {

    boolean disableSearch = false;

    TextView textViewTitle;
    ImageButton imageButtonSearch;
    ImageButton imageButtonMenu;

    public HeaderComponent(Activity activity) {
        super(activity);
    }

    public boolean isDisableSearch() {
        return disableSearch;
    }

    public void setDisableSearch(boolean disableSearch) {
        this.disableSearch = disableSearch;
        if (disableSearch) {
            imageButtonSearch.setVisibility(View.GONE);
        } else {
            imageButtonSearch.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (ViewGroup) this.activity.findViewById(R.id.headerLayout);
        textViewTitle = (TextView) this.activity.findViewById(R.id.textViewTitle);
        imageButtonSearch = (ImageButton) this.activity.findViewById(R.id.imageButtonSearch);
        imageButtonMenu = (ImageButton) this.activity.findViewById(R.id.imageButtonMenu);

        if (disableSearch) {
            imageButtonSearch.setVisibility(View.GONE);
        }
    }

    public void setTitle(String title) {
        textViewTitle.setText(title);
    }

}
