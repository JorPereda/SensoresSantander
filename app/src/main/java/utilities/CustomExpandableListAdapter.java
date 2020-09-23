package utilities;

import android.app.Activity;
import android.database.DataSetObserver;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.example.sensorsantander.R;

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


    public CustomExpandableListAdapter(Activity act, ArrayList<Parent> parents) {
        activity = act;
        this.parents = parents;
        inflater = act.getLayoutInflater();
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

        verEnMapa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity.getBaseContext(), "Mostrar sensor en el mapa", Toast.LENGTH_SHORT).show();
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
