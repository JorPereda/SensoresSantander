package adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.sensorsantander.R;

import baseDeDatos.EstadisticasContract;

public class MedidasCursorAdapter extends CursorAdapter {


    public MedidasCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(context);
        return inflater.inflate(R.layout.vista_stats, viewGroup, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        // Referencias UI.
        TextView nameText = (TextView) view.findViewById(R.id.tvStatsName);

        // Get valores.
        String name = cursor.getString(cursor.getColumnIndex(EstadisticasContract.MedidasSensorEntry.ID_SENSOR));

        // Setup.
        nameText.setText(name);

    }
}
