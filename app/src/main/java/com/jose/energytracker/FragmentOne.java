package com.jose.energytracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.logging.Logger;


public class FragmentOne extends Fragment {

    private TextView kcalTotales;
    private ArrayList<Alimento> listaDiario;
    private ArrayAdapter<Alimento> adapter;
    private String TABLE_1 = "diario";
    private SoundPool sp1;
    private int sonidofab;
    private int sonidoadd;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        kcalTotales = rootView.findViewById(R.id.kcal_totales);
        ListView listView = rootView.findViewById(R.id.list_view_1);

        listaDiario = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaDiario);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            dialogRemoveItem(position);
            return true;
        });

        updateListFromBD();
        printKcalTotales();

        //Sonidos
        sp1= new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        sonidofab = sp1.load(getContext(), R.raw.anadir, 1);
        sonidoadd = sp1.load(getContext(), R.raw.mordisco, 1);

        return rootView;
    }




    public void fabClicked(FragmentTwo f2) {
        //Efecto de sonido del fab
        sp1.play(sonidofab,1,1,1,0,0);
        String[] arrayProductos = new String[f2.getListaProductos().size()];
        for(int i = 0; i < f2.getListaProductos().size(); i++) {
            arrayProductos[i] = f2.getListaProductos().get(i).toString();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Añadir alimento");
        builder.setItems(arrayProductos, (dialog, which) -> maybeAddItem(f2.getListaProductos().get(which)));
        builder.create().show();
    }


    private void dialogRemoveItem(int position) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Quitar alimento");
        builder.setPositiveButton("Quitar", (dialog, id) -> maybeRemoveItem(position));
        builder.setNegativeButton("Cancelar", null);
        builder.create().show();
    }





    public void maybeAddItem(Alimento alimentoF2) {
        Alimento alimento = new Alimento(alimentoF2);
        if(alreadyInList(alimento)) {
            for(Alimento a: listaDiario) {
                if(a.equals(alimento)) {
                    a.incrementUds();
                    updateItemFromDB(a);
                    break;
                }
            }
        } else {
            addItemToList(alimento);
            addItemToDB(alimento);
        }

        adapter.notifyDataSetChanged();
        printKcalTotales();
        //Efecto de sonido del add
        sp1.play(sonidoadd,1,1,1,0,0);
    }


    public void addItemToList(Alimento alimento) {
        listaDiario.add(0, alimento);
    }




    public void maybeRemoveItem(int position) {
        Alimento alimento = listaDiario.get(position);

        if(alimento.getUds() > 1) {
            alimento.decrementUds();
            updateItemFromDB(alimento);
        } else {
            removeItemFromList(position);
            removeItemFromDB(alimento);
        }

        adapter.notifyDataSetChanged();
        printKcalTotales();
    }


    public void removeItemFromList(int position) {
        listaDiario.remove(position);
    }




    public void resetDay() {
        clearDB();
        listaDiario.clear();
        adapter.notifyDataSetChanged();
        printKcalTotales();
    }


    public void printKcalTotales() {
        int total = 0;
        for(Alimento a: listaDiario) {
            total = total + a.getKcal() * a.getUds();
        }
        kcalTotales.setText(String.valueOf(total));
    }


    public boolean alreadyInList(Alimento alimento) {
        for(Alimento a: listaDiario) {
            if(a.equals(alimento)) {
                return true;
            }
        }
        return false;
    }


    public void printList() {
        System.out.println("");
        for(Alimento a: listaDiario) {
            System.out.println("--> " + a.toString());
        }
        System.out.println("");
    }





    // ----------- DATABASE ----------- //


    public SQLiteDatabase getDB() {
        AdminSQLiteOpenHelper admin = new AdminSQLiteOpenHelper(getContext(), "gestion1", null, 1);
        return admin.getWritableDatabase();
    }


    public void updateListFromBD() {
        SQLiteDatabase db = getDB();
        Cursor c = db.query(TABLE_1, new String[] {"nombre", "kcal", "uds"}, null, null, null, null, null);
        listaDiario.clear();
        while(c.moveToNext()){
            String nombre = c.getString(0);
            int kcal = Integer.parseInt(c.getString(1));
            int uds = Integer.parseInt(c.getString(2));
            listaDiario.add(0, new Alimento(nombre, kcal, uds));
        }
        c.close();
        db.close();
        adapter.notifyDataSetChanged();
    }


    public void updateDBFromList() {
        clearDB();
        for(Alimento alimento: listaDiario) {
            addItemToDB(alimento);
        }
    }


    public void addItemToDB(Alimento alimento) {
        ContentValues reg = new ContentValues();
        reg.put("nombre", alimento.getNombre());
        reg.put("kcal", alimento.getKcal());
        reg.put("uds", alimento.getUds());

        SQLiteDatabase db = getDB();
        db.insert(TABLE_1, null, reg);
        db.close();
    }


    public void removeItemFromDB(Alimento alimento) {
        SQLiteDatabase db = getDB();
        db.delete(TABLE_1, "nombre='" + alimento.getNombre() + "'", null);
        db.close();
    }


    public void updateItemFromDB(Alimento alimento) {
        ContentValues reg = new ContentValues();
        reg.put("nombre", alimento.getNombre());
        reg.put("kcal", alimento.getKcal());
        reg.put("uds", alimento.getUds());

        SQLiteDatabase db = getDB();
        db.update(TABLE_1, reg, "nombre='" + alimento.getNombre() + "'", null);
        db.close();
    }


    public void clearDB() {
        SQLiteDatabase db = getDB();
        db.delete(TABLE_1, null, null);
        db.close();
    }


    public void printDB() {
        SQLiteDatabase db = getDB();
        Cursor c  = db.rawQuery("SELECT * FROM " + TABLE_1, null);
        String tableString = String.format("Table %s\n", TABLE_1);
        c.moveToFirst();
        String[] columnNames = c.getColumnNames();
        do {
            for (String name: columnNames) {
                tableString += String.format("%s: %s  ", name, c.getString(c.getColumnIndex(name)));
            }
            tableString += "\n";
            System.out.println("");

        } while (c.moveToNext());

        System.out.println("");
        System.out.println(tableString);
        System.out.println("");
    }

}
