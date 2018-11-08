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
    private ArrayList<Alimento> listaAlimentos;
    private ArrayAdapter<Alimento> adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_two, container, false);

        listView = rootView.findViewById(R.id.list_view_2);


        listaAlimentos = new ArrayList<>();
        listaAlimentos.add(new Alimento("Platano", 50));
        listaAlimentos.add(new Alimento("Pera", 40));

        adapter = new ArrayAdapter<Alimento>(getActivity(), android.R.layout.simple_list_item_1, listaAlimentos);

        listView.setAdapter(adapter);
        return rootView;
    }





    public ArrayList<Alimento> getListaProductos() {
        return listaAlimentos;
    }


    public void addItem(Alimento alimento) {
        listaAlimentos.add(alimento);
        adapter.notifyDataSetChanged();
    }


    public void fabClicked(FragmentOne f1) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.new_product_dialog, null);
        EditText editTextNombre = (EditText) viewDialog.findViewById(R.id.nuevo_alimento_nombre);
        EditText editTextKcal = (EditText) viewDialog.findViewById(R.id.nuevo_alimento_kcal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Crear producto");
        builder.setView(viewDialog);
        builder.setPositiveButton("Aceptar", (dialog, id) ->
                addItem(new Alimento(editTextNombre.getText().toString(), Integer.parseInt(editTextKcal.getText().toString())))
                );
        builder.setNegativeButton("Cancelar", null);
        Dialog dialog = builder.create();
        dialog.show();
    }


}