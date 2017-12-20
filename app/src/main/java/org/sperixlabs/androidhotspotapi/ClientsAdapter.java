package org.sperixlabs.androidhotspotapi;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Jay Lux Ferro on 12/19/17.
 */

public class ClientsAdapter extends RecyclerView.Adapter<ClientsAdapter.MyViewHolder>{
    private List<Client> clientList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView hostname, ip, mac;

        public MyViewHolder(View view) {
            super(view);
            hostname = view.findViewById(R.id.hostname);
            ip =  view.findViewById(R.id.ip);
            mac = view.findViewById(R.id.mac);
        }
    }

    public ClientsAdapter(List<Client> clientList) {
        this.clientList = clientList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.clients_list_layout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Client client = clientList.get(position);
        holder.hostname.setText(client.getHostname());
        holder.ip.setText(client.getIp());
        holder.mac.setText(client.getMac());
    }

    @Override
    public int getItemCount() {
        return clientList.size();
    }
}
