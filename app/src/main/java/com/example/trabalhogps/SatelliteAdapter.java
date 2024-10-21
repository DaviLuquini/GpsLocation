package com.example.trabalhogps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
//Componente 2 – Posições dos Satélites na Esfera Celeste
public class SatelliteAdapter extends RecyclerView.Adapter<SatelliteAdapter.SatelliteViewHolder> {

    private Context context;
    private List<SatelliteInfo> satelliteList;

    public SatelliteAdapter(Context context, List<SatelliteInfo> satelliteList) {
        this.context = context;
        this.satelliteList = satelliteList; // Recebe a lista de satélites na criação
    }

    @NonNull
    @Override
    public SatelliteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.satellite_list_item, parent, false);
        return new SatelliteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SatelliteViewHolder holder, int position) {
        SatelliteInfo satellite = satelliteList.get(position);
        holder.svidTextView.setText("SVID: " + satellite.getSvid());
        holder.snrTextView.setText("SNR: " + satellite.getSnr());

        // Define a cor do indicador de acordo com o SNR
        if (satellite.getSnr() >= 50) {
            holder.colorIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_green_light));
        } else {
            holder.colorIndicator.setBackgroundColor(context.getResources().getColor(android.R.color.holo_red_light));
        }
    }

    @Override
    public int getItemCount() {
        return satelliteList.size(); // Retorna o tamanho da lista
    }

    public static class SatelliteViewHolder extends RecyclerView.ViewHolder {
        View colorIndicator;
        TextView svidTextView;
        TextView snrTextView;

        public SatelliteViewHolder(@NonNull View itemView) {
            super(itemView);
            colorIndicator = itemView.findViewById(R.id.colorIndicator);
            svidTextView = itemView.findViewById(R.id.svidTextView);
            snrTextView = itemView.findViewById(R.id.snrTextView);
        }
    }
}
