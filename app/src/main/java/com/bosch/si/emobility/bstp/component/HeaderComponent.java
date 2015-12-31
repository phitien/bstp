package com.bosch.si.emobility.bstp.component;

import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.Activity;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class HeaderComponent extends Component {

    ImageButton imageButtonSearch;
    ImageButton imageButtonMenu;

    public HeaderComponent(Activity activity) {
        super(activity);
    }

    @Override
    public void setActivity(Activity activity) {
        super.setActivity(activity);
        layout = (RelativeLayout) this.activity.findViewById(R.id.headerLayout);
        imageButtonSearch = (ImageButton) this.activity.findViewById(R.id.imageButtonSearch);
        imageButtonMenu = (ImageButton) this.activity.findViewById(R.id.imageButtonMenu);
    }

}
