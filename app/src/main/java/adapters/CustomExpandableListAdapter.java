package adapters;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.media.Image;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaSensorUnicoMapa;
import com.example.sensorsantander.VistaStats;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import datos.Parent;
import datos.SensorAmbiental;
import datos.VariablesGlobales;
import services.EstadisticasService;
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
        ImageButton botonRename = convertView.findViewById(R.id.boton_rename_fav);
        ImageButton botonShare = convertView.findViewById(R.id.boton_share_fav);
        ImageButton botonEliminar = convertView.findViewById(R.id.boton_eliminar_fav);
        ImageButton botonStats = convertView.findViewById(R.id.boton_stats);
        ImageButton botonNewStats = convertView.findViewById(R.id.boton_new_stats);

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

        botonRename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(mView.getActivityContext());
                builder.setTitle("Introduce el nuevo nombre:");

                // Set up the input
                final EditText inputRename = new EditText(mView.getActivityContext());
                // Specify the type of input expected; this
                inputRename.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(inputRename);

                // Set up the buttons
                builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String nombreNuevo = inputRename.getText().toString();
                        child.setTitulo(nombreNuevo);
                        setData(parents);
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        botonShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(Intent.ACTION_SEND);
                myIntent.setType("text/plain");
                String shareBody = "";
                if(child.getTipo().equals("WeatherObserved")){
                    shareBody = child.getTitulo() + "\n" + "Temp: " + child.getTemperatura() +
                            "\n" + "https://maps.google.com/?q=" + child.getLatitud() + "," + child.getLongitud();
                }
                if(child.getTipo().equals("NoiseLevelObserved")){
                    shareBody = child.getTitulo() + "\n" + "Noise: " + child.getRuido() +
                            "\n" + "https://maps.google.com/?q=" + child.getLatitud() + "," + child.getLongitud();
                }
                String shareSub = "Your subject";
                myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                mView.getActivityContext().startActivity(Intent.createChooser(myIntent, "Share using"));
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

        botonStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mView.getActivityContext(), VistaStats.class);
                intent.putExtra("sensor", child);
                mView.getActivityContext().startActivity(intent);
                Log.d("Estadisticas Adapter", "SensorIntent: " + child.getIdentificador() + " " + child.getTitulo());
            }
        });

        botonNewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builderGeneraStats = new AlertDialog.Builder(mView.getActivityContext());
                View mViewDialog = LayoutInflater.from(v.getContext()).inflate(R.layout.conf_stats_dialog, null);
                builderGeneraStats.setTitle("Generar estadisticas");

                final Spinner muestreo = (Spinner) mViewDialog.findViewById(R.id.spinnerMuestreo);
                String[] muestreoList = new String[] {
                        "5 minutos",
                        "1 hora",
                        "1 dia"
                };
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter(mView.getActivityContext(), android.R.layout.simple_spinner_item, muestreoList);
                dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                muestreo.setAdapter(dataAdapter1);

                final Spinner tiempoCalculo = (Spinner) mViewDialog.findViewById(R.id.spinnerCalculo);
                String[] tiempoCalcList = new String[] {
                        "1 hora",
                        "1 dia",
                        "1 semana"
                };
                ArrayAdapter<String> dataAdapter2 = new ArrayAdapter(mView.getActivityContext(), android.R.layout.simple_spinner_item, tiempoCalcList);
                dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tiempoCalculo.setAdapter(dataAdapter2);

                final Spinner tiempoVida = (Spinner) mViewDialog.findViewById(R.id.spinnerTiempoVida);
                String[] tVidaList = new String[] {
                        "1 dia",
                        "1 semana",
                        "1 mes"
                };
                ArrayAdapter<String> dataAdapter3 = new ArrayAdapter(mView.getActivityContext(), android.R.layout.simple_spinner_item, tVidaList);
                dataAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                tiempoVida.setAdapter(dataAdapter3);

                builderGeneraStats.setView(mViewDialog);



                builderGeneraStats.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        int muestreoValor = (int) muestreo.getSelectedItemId();
                        int tCalculoValor = (int) tiempoCalculo.getSelectedItemId();
                        int tVidaValor = (int) tiempoVida.getSelectedItemId();

                        child.setIntervaloStatsMuestreo(muestreoValor);
                        child.setIntervaloStatsTCalculo(tCalculoValor);
                        child.setIntervaloStatsTVida(tVidaValor);
                        setData(parents);

                        /*Intent intent = new Intent(mView.getActivityContext(), VistaStats.class);
                        intent.putExtra("sensor", child);
                        intent.putExtra("Intervalo", intervalo);
                        mView.getActivityContext().startActivity(intent);*/
                    }
                });
                AlertDialog dialog = builderGeneraStats.create();

                dialog.show();
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

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mView.getExpList().isGroupExpanded(groupPosition)){
                    mView.getExpList().collapseGroup(groupPosition);
                }else{
                    mView.getExpList().expandGroup(groupPosition);
                }
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast toast = Toast.makeText(mView.getActivityContext(), "Boton apretado", Toast.LENGTH_SHORT);
                toast.show();
                mView.actionModeEditar(groupPosition);
                return true;
            }
        });

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
