package Utilities;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.sensorsantander.MainActivity;
import com.example.sensorsantander.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomMarkerInfoWindowView implements GoogleMap.InfoWindowAdapter {
    private final View markerItemView;
    private LayoutInflater layoutInflater;

    public CustomMarkerInfoWindowView() {
        markerItemView = layoutInflater.inflate(R.layout.popup_window, null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        TextView textViewVentana = markerItemView.findViewById(R.id.titleText);
        Button botonVentana = markerItemView.findViewById(R.id.messageButton);
        textViewVentana.setText(marker.getTitle());
        return markerItemView;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
