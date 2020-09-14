package utilities;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.sensorsantander.R;

import java.util.ArrayList;

import datos.SensorAmbiental;
import datos.VariablesGlobales;

import static android.content.Context.MODE_PRIVATE;


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
        final Child child = parent.getChildren().get(childPosition);

        convertView = inflater.inflate(R.layout.lista_favoritos, parentView, false);

        TextView t1 = convertView.findViewById(R.id.titulo);
        TextView t2 = convertView.findViewById(R.id.medida);
        TextView t3 = convertView.findViewById(R.id.MedidaLabel);

        t1.setText(child.getTitulo());
        t2.setText(child.getMedida());
        t3.setText(child.getMedidaLabel());

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
        return 0;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parentView) {

        final Parent parent = parents.get(groupPosition);

        convertView = inflater.inflate(R.layout.lista_grupos, parentView, false);

        TextView parentName = convertView.findViewById(R.id.listTitle);
        ImageButton botonEliminarGrupo = convertView.findViewById(R.id.eliminargrupo_button);

        parentName.setText(parent.getNombre());

        //Boton eliminar grupo
        botonEliminarGrupo.setOnClickListener(new View.OnClickListener()   {
            public void onClick(View v)  {

                Context context = activity;
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("¿Deseas eliminar el grupo de la lista?");

                // Set up the buttons
                builder.setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for(int i=0; i<parent.getChildren().size(); i++){
                            parent.removeChild(i);
                        }
                        parents.remove(parent);
                        VariablesGlobales.nombreGrupos.remove(parent.getNombre());
                        TinyDB tinydb = new TinyDB(activity);
                        tinydb.putListParent("parents", parents);
                        notifyDataSetChanged();
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

        botonEliminarGrupo.setFocusable(false);

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

    /*public void removeGroup(int groupPos) {
        Parent p = parents.get(groupPos);

        for(int i=0; i<VariablesGlobales.nombreGrupos.size(); i++){
            if(VariablesGlobales.nombreGrupos.get(i).equals(p.getNombre())){
                VariablesGlobales.nombreGrupos.remove(i);
            }
        }
        parents.remove(p);
        notifyDataSetChanged();
    }*/


    //Clase para elemento padre en lista desplegable
    public static class Parent{

        private String nombre;
        // ArrayList to store child objects
        private ArrayList<Child> children = new ArrayList<Child>();

        public Parent(String nombre) {
            this.nombre = nombre;
        }

        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public ArrayList<Child> getChildren() {
            return children;
        }

        public void setChildren(ArrayList<Child> children) {
            this.children.addAll(children);
        }

        public Child getChild(int childPosition) {
            return children.get(childPosition);
        }

        public void addChild(Child childItem) {
            children.add(childItem);
        }

        public void removeChild(int childPosition) {
            children.remove(childPosition);
        }

        public void removeChild(Child child) {
            children.remove(child);
        }


    }

    //Clase para elemento hijo en lista desplegable
    public static class Child{

        private String titulo;
        private String tipo;
        private String medida;
        private String medidaLabel;
        private String grupo;

        public Child(){
            this.grupo = "default";
        }

        public String getTitulo() {
            return titulo;
        }

        public void setTitulo(String titulo) {
            this.titulo = titulo;
        }

        public String getTipo() {
            return tipo;
        }

        public void setTipo(String tipo) {
            this.tipo = tipo;
        }

        public String getMedida() {
            return medida;
        }

        public void setMedida(String medida) {
            this.medida = medida;
        }

        public String getMedidaLabel() {
            return medidaLabel;
        }

        public void setMedidaLabel(String medidaLabel) {
            this.medidaLabel = medidaLabel;
        }

        public String getGrupo() {
            return grupo;
        }

        public void setGrupo(String grupo) {
            this.grupo = grupo;
        }

    }


}
