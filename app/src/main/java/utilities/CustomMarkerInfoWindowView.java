package utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.sensorsantander.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {

    private View markerItemView;
    private Context context;

    public CustomMarkerInfoWindowView(Context context) {
        this.context=context;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        markerItemView = inflater.inflate(R.layout.marker_info_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView itemTitleTextView = markerItemView.findViewById(R.id.titulo);
        itemTitleTextView.setText(marker.getTitle());
        return markerItemView;
    }
}