package utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.sensorsantander.R;
import com.example.sensorsantander.VistaFavoritos;

import java.util.ArrayList;
import java.util.HashMap;



public class CustomAdapterFavoritos extends BaseAdapter {

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
}
