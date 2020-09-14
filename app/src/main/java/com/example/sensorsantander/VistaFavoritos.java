package com.example.sensorsantander;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;




import java.util.ArrayList;

import datos.SensorAmbiental;
import datos.VariablesGlobales;
import presenters.SensorAppPresenter;
import utilities.CustomExpandableListAdapter;
import utilities.Interfaces_MVP;
import utilities.TinyDB;

import static android.content.ContentValues.TAG;


public class VistaFavoritos extends AppCompatActivity implements View.OnClickListener, Interfaces_MVP.RequiredViewOps {

    private static Interfaces_MVP.ProvidedPresenterOps mPresenter;

    private ArrayList<CustomExpandableListAdapter.Parent> parents = new ArrayList<>();

    private CustomExpandableListAdapter mAdapter;

    private ExpandableListView expList;

    private ArrayList<CustomExpandableListAdapter.Child> eliminables = new ArrayList<>();

    private ActionMode mActionMode;
    private CustomExpandableListAdapter.Child sensorSelected;
    private CustomExpandableListAdapter.Parent grupoSelected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vista_favoritos);

        mPresenter = new SensorAppPresenter(this);

        TinyDB tinydb = new TinyDB(this);
        parents = tinydb.getListParent("parents");
        expList = findViewById(R.id.list_view_favoritos);

        expList.setSelector(R.drawable.selector_list_item);
        expList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                long packedPosition = expList.getExpandableListPosition(position);
                if (ExpandableListView.getPackedPositionType(packedPosition) ==
                        ExpandableListView.PACKED_POSITION_TYPE_CHILD) {
                    // get item ID's
                    int groupPosition = ExpandableListView.getPackedPositionGroup(packedPosition);
                    int childPosition = ExpandableListView.getPackedPositionChild(packedPosition);

                    int index = expList.getFlatListPosition(packedPosition);

                    // handle data
                    expList.setItemChecked(index, true);
                    grupoSelected = parents.get(groupPosition);
                    sensorSelected = parents.get(groupPosition).getChild(childPosition);
                    actionModeEditar();


                    // return true as we are handling the event.
                    return true;
                }
                return false;
            }
        });

        expList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getActivityContext(), "Item Click", Toast.LENGTH_LONG).show();
                return true;
            }
        });

        mAdapter = new CustomExpandableListAdapter(this, parents);
        expList.setAdapter(mAdapter);

    }

    @Override
    public void actionModeEditar(){
        mActionMode = startSupportActionMode(mActionModeCallback);
    }

    @Override
    protected void onStop() {
        super.onStop();

        TinyDB tinydb = new TinyDB(this);

        tinydb.putListString("nombreGrupos", VariablesGlobales.nombreGrupos);
        tinydb.putListParent("parents", parents);
    }


    @Override
    protected void onStart() {
        super.onStart();
        TinyDB tinydb = new TinyDB(this);
        VariablesGlobales.nombreGrupos = tinydb.getListString("nombreGrupos");
        parents = tinydb.getListParent("parents");
    }

    @Override
    protected void onResume() {
        super.onResume();
        TinyDB tinydb = new TinyDB(this);
        VariablesGlobales.nombreGrupos = tinydb.getListString("nombreGrupos");
        parents = tinydb.getListParent("parents");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_favoritos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mPresenter.menuFavoritos(item, this);
    }

    @Override
    public void onClick(View v) {
    }

    @Override
    public Context getActivityContext() {
        return this;
    }

    @Override
    public Context getAppContext() {
        return getApplicationContext();
    }

    @Override
    public void addToGroup(CustomExpandableListAdapter.Parent grupo) {
        parents.add(grupo);
        mAdapter.setData(parents);
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){


        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_actionmode, menu);
            mode.setTitle("Choose your option");

            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.delete:
                    for(CustomExpandableListAdapter.Parent p : parents){
                        p.removeChild(sensorSelected);
                    }
                    mAdapter.setData(parents);
                    mode.finish();
                    return true;
                case R.id.share:
                    Intent myIntent = new Intent(Intent.ACTION_SEND);
                    myIntent.setType("text/plain");
                    String shareBody = sensorSelected.getTitulo() + "\n" + sensorSelected.getMedidaLabel() + sensorSelected.getMedida();
                    String shareSub = "Your subject";
                    myIntent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                    myIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(myIntent, "Share using"));
                    mode.finish();
                    return true;
                case R.id.rename:
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivityContext());
                    builder.setTitle("Introduce el nuevo nombre:");

                    // Set up the input
                    final EditText inputGrupo = new EditText(getActivityContext());
                    // Specify the type of input expected; this
                    inputGrupo.setInputType(InputType.TYPE_CLASS_TEXT);
                    builder.setView(inputGrupo);

                    // Set up the buttons
                    builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String m_Text = inputGrupo.getText().toString();
                            sensorSelected.setTitulo(m_Text);
                            mAdapter.setData(parents);
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();
                    return true;
                default:
                    return false;
            }        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
        }
    };

}