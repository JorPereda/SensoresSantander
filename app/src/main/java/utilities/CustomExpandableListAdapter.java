package utilities;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;
import com.example.sensorsantander.VistaSensorUnicoMapa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import datos.Parent;
import datos.SensorAmbiental;


public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private ArrayList<Parent> parents;
    public LayoutInflater inflater;
    public Activity activity;
    // View reference.
    private Interfaces_MVP.RequiredViewFavoritosOps mView;


    public CustomExpandableListAdapter(Activity act, ArrayList<Parent> parents, Interfaces_MVP.RequiredViewFavoritosOps view) {
        activity = act;
        this.parents = parents;
        inflater = act.getLayoutInflater();
        mView = view;
    }

    //Necesario para actualizar la lista al instante
    public void setData(ArrayList<Parent> list) {
        this.parents = list;
        notifyDataSetChanged();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return parents.get(groupPosition).getChildren().get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parentView) {

        final Parent parent = parents.get(groupPosition);
        final SensorAmbiental child = parent.getChildren().get(childPosition);

        convertView = inflater.inflate(R.layout.card_favoritos, parentView, false);

        TextView t1 = convertView.findViewById(R.id.titulo);
        TextView t2 = convertView.findViewById(R.id.medida);
        TextView t3 = convertView.findViewById(R.id.MedidaLabel);
        TextView t4 = convertView.findViewById(R.id.medida_luz);
        ImageButton verEnMapa = convertView.findViewById(R.id.boton_ver_en_mapa);
        TextView calle = convertView.findViewById(R.id.nombre_calle);
        ImageButton botonAlarma = convertView.findViewById(R.id.boton_nueva_alarma);

        t1.setText(child.getTitulo());
        if(child.getTipo().equals("WeatherObserved")){
            t2.setText(child.getTemperatura());
            t3.setText("Temp: ");
        }
        if(child.getTipo().equals("NoiseLevelObserved")){
            t2.setText(child.getRuido());
            t3.setText("Noise: ");
        }
        t4.setText(child.getLuminosidad());


        //Direccion del sensor
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(activity.getBaseContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(child.getLatitud()), Double.valueOf(child.getLongitud()), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getFeatureName();
        calle.setText(address);
        child.setDireccion(address);

        TinyDB tinydb = new TinyDB(activity.getBaseContext());
        tinydb.putListParent("parents", parents);

        //Parte desplegable de la card view
        final ConstraintLayout hiddenView;
        hiddenView = convertView.findViewById(R.id.hidden_view);
        final CardViewManage cardViewManage = new CardViewManage(activity.getBaseContext());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hiddenView.getVisibility() == View.VISIBLE) {
                    cardViewManage.collapse(hiddenView);
                }else if(hiddenView.getVisibility() == View.GONE){
                    cardViewManage.expand(hiddenView);
                }
            }
        });

        /*convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mView.actionModeEditar();
                return true;
            }
        });*/

        //Boton para mostrar el sensor unico en un mapa
        verEnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getBaseContext(), VistaSensorUnicoMapa.class);
                intent.putExtra("sensor", child);
                activity.startActivity(intent);
            }
        });


        /*// Create a generic swipe-to-dismiss touch listener.
        convertView.setOnTouchListener(new SwipeDismissTouchListener(
                convertView,
                null,
                new SwipeDismissTouchListener.DismissCallbacks() {
                    @Override
                    public boolean canDismiss(Object token) {
                        return true;
                    }

                    @Override
                    public void onDismiss(View view, Object token) {
                        Toast.makeText(activity.getBaseContext(), "Swipe funcioando", Toast.LENGTH_LONG).show();

                    }
                }));*/

        //Generar nueva alarma sobre un sensor
        final View finalConvertView = convertView;
        //finalConvertView = inflater.inflate(R.layout.new_alarm_dialog, parentView, false);

        botonAlarma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivityContext());
                ViewGroup viewGroup = finalConvertView.findViewById(android.R.id.content);
                final View dialogView = LayoutInflater.from(v.getContext()).inflate(R.layout.new_alarm_dialog, viewGroup, false);
                builder.setView(dialogView);
                final AlertDialog alertDialog = builder.create();
                alertDialog.show();
                final EditText editTextMedida = dialogView.findViewById(R.id.medida_para_alarma);
                final EditText editTextNombre = dialogView.findViewById(R.id.nombre_alarma);
                editTextNombre.setText(child.getTitulo());

                final RadioGroup radioAlarma = dialogView.findViewById(R.id.opciones_alarma);

                radioAlarma.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {

                    }
                });
                final Button botonAceptar = dialogView.findViewById(R.id.aceptarAlarmaButton);
                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedId = radioAlarma.getCheckedRadioButtonId();
                        String tipoAlarma = "";
                        if (selectedId == R.id.radio_temperatura) {
                            tipoAlarma = "Temp. max: ";
                        }else if (selectedId == R.id.radio_luz) {
                            tipoAlarma = "Luz max: ";
                        }else if (selectedId == R.id.radio_ruido) {
                            tipoAlarma = "Ruido max: ";
                        }

                        String nombre = editTextNombre.getText().toString();
                        if(nombre.matches("")) {
                            nombre = child.getTitulo();
                        }
                        Double valor = Double.valueOf(editTextMedida.getText().toString());
                        mView.getPresenter().onClickAddAlarma(child, valor, tipoAlarma, nombre);
                        alertDialog.dismiss();
                    }
                });
            }
        });


        return convertView;
    }


    @Override
    public int getChildrenCount(int groupPosition) {
        return parents.get(groupPosition).getChildren().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return parents.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return parents.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView) {

        final Parent parent = parents.get(groupPosition);

        convertView = inflater.inflate(R.layout.lista_grupos, parentView, false);

        TextView parentName = convertView.findViewById(R.id.listTitle);

        parentName.setText(parent.getNombre());


        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        super.registerDataSetObserver(observer);
    }

}
