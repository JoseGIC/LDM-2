package com.jose.energytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class FragmentTwo extends Fragment {

    private ListView listView;
    private ArrayList<String> listaProductos;
    private ArrayAdapter<String> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        listView = rootView.findViewById(R.id.list_view_2);


        listaProductos = new ArrayList<>();
        listaProductos.add("Platano");
        listaProductos.add("Pera");

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listaProductos);

        listView.setAdapter(adapter);
        return rootView;
    }


    public ArrayList<String> getListaProductos() {
        return listaProductos;
    }


    public void addItem(String newItem) {
        listaProductos.add(newItem);
        adapter.notifyDataSetChanged();
    }


    public void fabClicked(FragmentOne f1) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.new_product_dialog, null);
        EditText editText = (EditText) viewDialog.findViewById(R.id.text_nuevo_producto);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir producto");
        builder.setView(viewDialog);
        builder.setPositiveButton("Aceptar", (dialog, id) -> addItem(editText.getText().toString()));
        builder.setNegativeButton("Cancelar", null);
        Dialog dialog = builder.create();
        dialog.show();
    }


}
