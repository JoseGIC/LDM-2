package com.jose.energytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaDiario);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            maybeRemoveItem(position);
            return true;
        });

        createListFromBD();
        printKcalTotales();

        return rootView;
    }


    public SQLiteDatabase getDB() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "gestion1", null, 1);
        return admin.getWritableDatabase();
    }


    public void createListFromBD() {
        SQLiteDatabase db = getDB();
        Cursor c = db.query("diario", new String[] {"nombre", "kcal"}, null, null, null, null, null);
        while(c.moveToNext()){
            String nombre = c.getString(0);
            int kcal = Integer.parseInt(c.getString(1));
            //int cantidad = Integer.parseInt(c.getString(2));
            listaDiario.add(0, new Alimento(nombre, kcal));
        }
        c.close();
        db.close();
        adapter.notifyDataSetChanged();
    }


    public int getCantidad(Alimento alimento) {
        SQLiteDatabase db = getDB();
        Cursor c = db.rawQuery(("select * from diario where nombre='" + alimento.getNombre() + "'"), null);
        int cantidad = c.getCount();
        c.close();
        return cantidad;
    }


    public void selectItem(FragmentTwo f2, int posicion) {
        Alimento alimento = f2.getListaProductos().get(posicion);
        ContentValues reg = new ContentValues();
        reg.put("nombre", alimento.getNombre());
        reg.put("kcal", alimento.getKcal());

        SQLiteDatabase db = getDB();
        db.insert("diario", null, reg);
        db.close();


        listaDiario.add(0, alimento);
        adapter.notifyDataSetChanged();
        printKcalTotales();
    }


    private void maybeRemoveItem(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Quitar alimento");
        builder.setPositiveButton("Aceptar", (dialog, id) -> removeItem(position));
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }


    public void removeItem(int pos) {
        SQLiteDatabase db = getDB();
        db.delete("diario", "nombre='" + listaDiario.get(pos).getNombre() + "'", null);
        db.close();

        listaDiario.remove(pos);
        adapter.notifyDataSetChanged();
        printKcalTotales();
    }


    public void fabClicked(FragmentTwo f2) {
        String[] arrayProductos = new String[f2.getListaProductos().size()];
        for(int i = 0; i < f2.getListaProductos().size(); i++) {
            arrayProductos[i] = f2.getListaProductos().get(i).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir alimento");
        builder.setItems(arrayProductos, (dialog, which) -> selectItem(f2, which));
        builder.create().show();
    }


    public void printKcalTotales() {
        int total = 0;
        for(Alimento a: listaDiario) {
            total = total + a.getKcal();
        }
        kcalTotales.setText(String.valueOf(total));
    }


    public void resetKcal() {
        SQLiteDatabase db = getDB();
        db.delete("diario", null, null);
        db.close();

        listaDiario.clear();
        adapter.notifyDataSetChanged();
        printKcalTotales();
    }



}
