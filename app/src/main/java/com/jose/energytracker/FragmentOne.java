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
    private ArrayList<Alimento> listaDiario;
    private ArrayAdapter<Alimento> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        kcalTotales = rootView.findViewById(R.id.kcal_totales);
        listView = rootView.findViewById(R.id.list_view_1);

        listaDiario = new ArrayList<>();
        //Habría que conectarse con la base de datos y actualizar la lista si se está en el mismo dia
        printKcalTotales();

        adapter = new ArrayAdapter<Alimento>(getActivity(), android.R.layout.simple_list_item_1, listaDiario);

        listView.setAdapter(adapter);
        return rootView;
    }


    public void selectItem(FragmentTwo f2, int posicion) {
        listaDiario.add(f2.getListaProductos().get(posicion));
        adapter.notifyDataSetChanged();
        printKcalTotales();
    }


    public void fabClicked(FragmentTwo f2) {
        String[] arrayProductos = new String[f2.getListaProductos().size()];
        for(int i = 0; i < f2.getListaProductos().size(); i++) {
            arrayProductos[i] = f2.getListaProductos().get(i).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir producto");
        builder.setItems(arrayProductos, (dialog, which) -> selectItem(f2, which));
        Dialog dialog = builder.create();
        dialog.show();
    }


    public void printKcalTotales() {
        int total = 0;
        for(Alimento a: listaDiario) {
            total = total + a.getKcal();
        }
        kcalTotales.setText(String.valueOf(total));
    }


}
