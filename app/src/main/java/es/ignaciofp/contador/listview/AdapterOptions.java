package es.ignaciofp.contador.listview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Objects;

import es.ignaciofp.contador.OptionsActivity;
import es.ignaciofp.contador.R;
import es.ignaciofp.contador.models.Option;

public class AdapterOptions extends ArrayAdapter<Option> {

    public AdapterOptions(@NonNull Context context, int resource, int textViewResourceId, @NonNull List<Option> options) {
        super(context, resource, textViewResourceId, options);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        @NonNull
        Option option = Objects.requireNonNull(getItem(position));

        if (convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.option_list, parent, false);
        }

        ((TextView)convertView.findViewById(R.id.option_name)).setText(option.getName());
        ((TextView)convertView.findViewById(R.id.option_description)).setText(option.getDescription());
        convertView.findViewById(R.id.option_description).setTag(option.getTag());

        convertView.findViewById(R.id.option_switch).setTag(option.getTag());
        ((SwitchMaterial)convertView.findViewById(R.id.option_switch)).setOnCheckedChangeListener(((OptionsActivity)getContext()));
        ((SwitchMaterial)convertView.findViewById(R.id.option_switch)).setChecked(option.isChecked());

        return super.getView(position, convertView, parent);
    }
}
