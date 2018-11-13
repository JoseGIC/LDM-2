package com.jose.energytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.Toast;

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


        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaAlimentos);

        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            maybeRemoveItem(position);
            return true;
        });

        createListFromBD();

        return rootView;
    }


    public ArrayList<Alimento> getListaProductos() {
        return listaAlimentos;
    }


    public SQLiteDatabase getDB() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "gestion", null, 1);
        return admin.getWritableDatabase();
    }


    public void createListFromBD() {
        SQLiteDatabase db = getDB();
        Cursor c = db.query("alimentos", new String[] {"nombre", "kcal"}, null, null, null, null, null);
        while(c.moveToNext()){
            listaAlimentos.add(0, new Alimento(c.getString(0), Integer.parseInt(c.getString(1))));
        }
        c.close();
        db.close();
        adapter.notifyDataSetChanged();
    }


    public void maybeAddItem(EditText editTextNombre, EditText editTextKcal) {
        String nombre = editTextNombre.getText().toString();
        String kcal = editTextKcal.getText().toString();

        if(nombre.isEmpty() || kcal.isEmpty()) {
            Toast.makeText(getActivity(), "Debes poner nombre y calorías", Toast.LENGTH_SHORT).show();
        } else {
            if(alreadyInList(nombre, Integer.parseInt(kcal))) {
                Toast.makeText(getActivity(), "El alimento ya existe", Toast.LENGTH_SHORT).show();
            } else {
                addItem(new Alimento(nombre, Integer.parseInt(kcal)));
            }
        }
    }


    public void addItem(Alimento alimento) {
        ContentValues reg = new ContentValues();
        reg.put("nombre", alimento.getNombre());
        reg.put("kcal", alimento.getKcal());

        SQLiteDatabase db = getDB();
        db.insert("alimentos", null, reg);
        db.close();

        listaAlimentos.add(0, alimento);
        adapter.notifyDataSetChanged();
    }


    private void maybeRemoveItem(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar alimento");
        builder.setPositiveButton("Aceptar", (dialog, id) -> removeItem(position));
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }


    public void removeItem(int pos) {
        SQLiteDatabase db = getDB();
        db.delete("alimentos", "nombre='" + listaAlimentos.get(pos).getNombre() + "'", null);
        db.close();

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
        builder.setPositiveButton("Aceptar", (dialog, id) -> maybeAddItem(editTextNombre, editTextKcal));
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }


    public boolean alreadyInList(String nombre, int kcal) {
        for(Alimento a: listaAlimentos) {
            if(a.getNombre().equals(nombre) && a.getKcal() == kcal) {
                return true;
            }
        }
        return false;
    }


}
