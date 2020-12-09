package adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaSensorUnicoMapa;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import datos.Parent;
import datos.SensorAmbiental;
import datos.VariablesGlobales;
import tasks.UpdateFavoritosTask;
import utilities.CardViewManage;
import utilities.Interfaces_MVP;
import utilities.TinyDB;


public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private ArrayList<Parent> parents;
    private LayoutInflater inflater;
    // View reference.
    private Interfaces_MVP.ViewFavoritosYAlarma mView;



    public CustomExpandableListAdapter(ArrayList<Parent> parents, Interfaces_MVP.ViewFavoritosYAlarma view) {
        this.parents = parents;
        inflater = LayoutInflater.from(view.getActivityContext());
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
        return childPosition;
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
        ImageButton botonEliminar = convertView.findViewById(R.id.boton_eliminar_fav);

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
        geocoder = new Geocoder(mView.getActivityContext(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(Double.valueOf(child.getLatitud()), Double.valueOf(child.getLongitud()), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String address = addresses.get(0).getThoroughfare() + ", " + addresses.get(0).getFeatureName();
        calle.setText(address);
        child.setDireccion(address);

        TinyDB tinydb = new TinyDB(mView.getActivityContext());
        tinydb.putListParent("parents", parents);

        //Parte desplegable de la card view
        final ConstraintLayout hiddenView;
        hiddenView = convertView.findViewById(R.id.hidden_view);
        final CardViewManage cardViewManage = new CardViewManage(mView.getActivityContext());


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

        final ExpandableListView expList = mView.getExpList();

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int indexChild = expList.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                int indexGroup = expList.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));
                long packedPosition = expList.getExpandableListPosition(childPosition);
                mView.checkItemList(indexChild, indexGroup);
                //Log.d("Seleccion child: ", String.valueOf(indexChild));
                //Log.d("Seleccion group: ", String.valueOf(indexGroup));

                return true;
            }
        });

        //Boton para mostrar el sensor unico en un mapa
        verEnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mView.getActivityContext(), VistaSensorUnicoMapa.class);
                intent.putExtra("sensor", child);
                mView.getActivityContext().startActivity(intent);
            }
        });

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

                final RadioGroup radioTipoAlarma = dialogView.findViewById(R.id.opciones_alarma);
                final RadioGroup radioMaxMinAlarma = dialogView.findViewById(R.id.opciones_max_min);


                final Button botonAceptar = dialogView.findViewById(R.id.aceptarAlarmaButton);
                botonAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int selectedTipo = radioTipoAlarma.getCheckedRadioButtonId();
                        String tipoAlarma = "";
                        if (selectedTipo == R.id.radio_temperatura) {
                            tipoAlarma = "temp";
                        }else if (selectedTipo == R.id.radio_luz) {
                            tipoAlarma = "luz";
                        }else if (selectedTipo == R.id.radio_ruido) {
                            tipoAlarma = "ruido";
                        }

                        int selectedMaxMin = radioMaxMinAlarma.getCheckedRadioButtonId();
                        String maxMin = "";
                        if (selectedMaxMin == R.id.radio_valor_min) {
                            maxMin = "min";
                        }else if (selectedMaxMin == R.id.radio_valor_max) {
                            maxMin = "max";
                        }

                        String nombre = editTextNombre.getText().toString();
                        if(nombre.matches("")) {
                            nombre = child.getTitulo();
                        }
                        Double valor = Double.valueOf(editTextMedida.getText().toString());
                        mView.getPresenter().onClickAddAlarma(child, valor, tipoAlarma, maxMin, nombre);
                        alertDialog.dismiss();
                    }
                });
            }
        });

        botonEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builderDeleteFav = new AlertDialog.Builder(mView.getActivityContext());
                builderDeleteFav.setTitle("¿Realmente deseas eliminar el sensor?");

                // Set up the buttons
                builderDeleteFav.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        parent.removeChild(child);
                        notifyDataSetChanged();
                        mView.updateListView(parents);
                        dialog.dismiss();
                    }
                });
                builderDeleteFav.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builderDeleteFav.show();
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

        //new UpdateFavoritosTask(parent).execute();

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
