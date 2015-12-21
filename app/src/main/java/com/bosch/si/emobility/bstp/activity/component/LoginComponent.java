package com.bosch.si.emobility.bstp.activity.component;

import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bosch.si.emobility.bstp.R;
import com.bosch.si.emobility.bstp.activity.MapsActivity;
import com.bosch.si.emobility.bstp.model.User;

/**
 * Created by sgp0458 on 16/12/15.
 */
public class LoginComponent extends Component {

    private static LoginComponent ourInstance = new LoginComponent();

    public static LoginComponent getInstance(MapsActivity activity) {
        if (activity != null)
            ourInstance.setActivity(activity);
        return ourInstance;
    }

    private LoginComponent() {
        super();
    }

    EditText editTextUsername;
    EditText editTextPassword;
    CheckBox checkBoxSaveCredentials;

    @Override
    public void setActivity(MapsActivity activity) {
        super.setActivity(activity);
        layout = (RelativeLayout) this.activity.findViewById(R.id.loginLayout);
        editTextUsername = (EditText) this.activity.findViewById(R.id.editTextUsername);
        editTextPassword = (EditText) this.activity.findViewById(R.id.editTextPassword);
        checkBoxSaveCredentials = (CheckBox) this.activity.findViewById(R.id.checkBoxSaveCredentials);
    }

    public User getUser() {
        User user = new User();
        user.setUsername(editTextUsername.getText().toString());
        user.setPassword(editTextPassword.getText().toString());
        user.setSaveCredentials(checkBoxSaveCredentials.isChecked());
        return user;
    }

    public void setPasswordOnEditorActionListener(TextView.OnEditorActionListener onEditorActionListener) {
        editTextPassword.setOnEditorActionListener(onEditorActionListener);
    }

    @Override
    protected boolean isSlidDown() {
        return true;
    }
}
