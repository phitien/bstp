package com.bosch.si.emobility.bstp.component;

import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.core.Activity;
import com.bosch.si.emobility.bstp.core.Component;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class HeaderComponent extends Component {

    boolean disableSearch = false;
    boolean disableMenu = false;

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

    public boolean isDisableMenu() {
        return disableMenu;
    }

    public void setDisableMenu(boolean disableMenu) {
        this.disableMenu = disableMenu;
        if (disableMenu) {
            imageButtonMenu.setVisibility(View.GONE);
        } else {
            imageButtonMenu.setVisibility(View.VISIBLE);
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

        imageButtonSearch.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction()==MotionEvent.ACTION_DOWN) { //esto es necesario para evitar que salte el evento muchas veces con una sola presión del dedo.
                    imageButtonSearch.setPressed(!imageButtonSearch.isPressed()); //invertimos el estado del botón
                    return v.performClick();
                }
                return true;
            }

        });
    }

    public void setTitle(String title) {
        textViewTitle.setText(title);
    }

}
