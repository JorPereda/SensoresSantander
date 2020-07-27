/*
package utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;

import java.util.ArrayList;
import java.util.HashMap;



public class CustomAdapterFavoritos extends BaseExpandableListAdapter {

    ArrayList<HashMap<String,String>> listaFavoritos;
    Context context;

    public CustomAdapterFavoritos(VistaFavoritos activity, ArrayList<HashMap<String, String>> lista) {
        context = activity;
        listaFavoritos = new ArrayList<>();
        listaFavoritos = lista;
    }

    @Override
    public int getCount() {
        return listaFavoritos.size();
    }

    @Override
    public Object getItem(int i) {
        return i;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        view = LayoutInflater.from(context).inflate(R.layout.lista_favoritos,null,false);

        TextView t1 = view.findViewById(R.id.titulo);
        TextView t2 = view.findViewById(R.id.medida);

        TextView t3 = view.findViewById(R.id.MedidaLabel);

        t1.setText((listaFavoritos.get(i).get("tipo")) + (listaFavoritos.get(i).get("id")));
        if(listaFavoritos.get(i).get("tipo").equals("WeatherObserved")){
            t2.setText(listaFavoritos.get(i).get("temperatura"));
            t3.setText("Temp:");
        }
        if(listaFavoritos.get(i).get("tipo").equals("NoiseLevelObserved")){
            t2.setText(listaFavoritos.get(i).get("ruido"));
            t3.setText("Ruido:");
        }

        return view;
    }

    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
*/
