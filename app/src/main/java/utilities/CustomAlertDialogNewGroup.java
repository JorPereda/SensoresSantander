package utilities;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.example.sensorsantander.R;

public class CustomAlertDialogNewGroup extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button ok, cancel;
    public EditText newGroup;
    public String textoGrupo;

    public CustomAlertDialogNewGroup(Activity a) {
        super(a);
        this.c = a;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_alert_dialog_new_group);
        ok = findViewById(R.id.btn_ok);
        cancel = findViewById(R.id.btn_cancel);
        newGroup = findViewById(R.id.edit_text_new_group);
        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                textoGrupo = String.valueOf(newGroup.getText());
                Log.e("tag", "(CustomAlert)Nombre del grupo: " + textoGrupo);
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }

    public String getTextoGrupo() {
        return textoGrupo;
    }
}


