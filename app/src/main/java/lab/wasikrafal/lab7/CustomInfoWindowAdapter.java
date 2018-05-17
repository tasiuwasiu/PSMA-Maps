package lab.wasikrafal.lab7;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter
{
    private Activity context;

    public CustomInfoWindowAdapter(Activity context)
    {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        View view = context.getLayoutInflater().inflate(R.layout.info_window, null);
        MarkerInfoHolder info = (MarkerInfoHolder) marker.getTag();
        if (info != null) {
            TextView title = (TextView) view.findViewById(R.id.markerTitle);
            title.setText(info.getTitle(context));
            TextView text = (TextView) view.findViewById(R.id.markerText);
            text.setText(info.getText(context));
            ImageView photo = (ImageView) view.findViewById(R.id.photo);
            photo.setImageDrawable(context.getDrawable(info.getImage()));
        }
        return view;

    }
}
