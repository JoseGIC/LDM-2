package com.jose.energytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class FragmentOne extends Fragment {

    private TextView kcalTotales;
    private ListView listView;
    private ArrayList<String> listaDiario;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        kcalTotales = rootView.findViewById(R.id.kcal_totales);
        kcalTotales.setText(String.valueOf(1850));

        listView = rootView.findViewById(R.id.list_view_1);


        listaDiario = new ArrayList<>();
        listaDiario.add("Patatas");
        listaDiario.add("Filete de pavo");

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listaDiario);

        listView.setAdapter(adapter);
        return rootView;
    }


    public void selectItem(FragmentTwo f2, int posicion) {
        listaDiario.add(f2.getListaProductos().get(posicion));
        adapter.notifyDataSetChanged();
    }


    public void fabClicked(FragmentTwo f2) {
        String[] arrayProductos = f2.getListaProductos().toArray(new String[0]);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir producto");
        builder.setItems(arrayProductos, (dialog, which) -> selectItem(f2, which));
        Dialog dialog = builder.create();
        dialog.show();
    }


}
