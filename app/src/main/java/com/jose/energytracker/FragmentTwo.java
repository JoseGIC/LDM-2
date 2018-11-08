package com.jose.energytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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

        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaAlimentos);

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            maybeRemoveItem(position);
            return true;
        });

        return rootView;
    }


    public ArrayList<Alimento> getListaProductos() {
        return listaAlimentos;
    }


    public void addItem(Alimento alimento) {
        listaAlimentos.add(alimento);
        adapter.notifyDataSetChanged();
    }


    private void maybeRemoveItem(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar alimento");
        builder.setPositiveButton("Aceptar", (dialog, id) -> removeItem(position));
        builder.setNegativeButton("Cancelar", null);
        Dialog dialog = builder.create();
        dialog.show();
    }


    public void removeItem(int pos) {
        listaAlimentos.remove(pos);
        adapter.notifyDataSetChanged();
    }


    public void fabClicked(FragmentOne f1) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View viewDialog = inflater.inflate(R.layout.new_product_dialog, null);
        EditText editTextNombre = (EditText) viewDialog.findViewById(R.id.nuevo_alimento_nombre);
        EditText editTextKcal = (EditText) viewDialog.findViewById(R.id.nuevo_alimento_kcal);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Crear alimento");
        builder.setView(viewDialog);
        builder.setPositiveButton("Aceptar", (dialog, id) ->
                addItem(new Alimento(editTextNombre.getText().toString(),
                        Integer.parseInt(editTextKcal.getText().toString())))
                );
        builder.setNegativeButton("Cancelar", null);
        Dialog dialog = builder.create();
        dialog.show();
    }


}
