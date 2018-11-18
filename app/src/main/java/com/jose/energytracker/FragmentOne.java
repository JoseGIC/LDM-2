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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.logging.Logger;


public class FragmentOne extends Fragment {

    private TextView kcalTotales;
    private TextView goal;
    private ArrayList<Alimento> listaDiario;
    private ArrayAdapter<Alimento> adapter;
    private String TABLE_1 = "diario";
    private String TABLE_3 = "objetivo";
    private SoundPool sp1;
    private int sonidoFab;
    private int sonidoAdd;
    private int sonidoDelete;
    private int sonidoReiniciar;
    private int sonidoWin;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);

        goal = rootView.findViewById(R.id.goal);
        kcalTotales = rootView.findViewById(R.id.kcal_totales);
        ListView listView = rootView.findViewById(R.id.list_view_1);

        listaDiario = new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, listaDiario);
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            dialogRemoveItem(position);
            return true;
        });


        //Sonidos
        sp1= new SoundPool(1, AudioManager.STREAM_MUSIC,1);
        sonidoFab = sp1.load(getContext(), R.raw.fab, 1);
        sonidoAdd = sp1.load(getContext(), R.raw.mordisco, 1);
        sonidoDelete= sp1.load(getContext(),R.raw.delete, 1);
        sonidoReiniciar= sp1.load(getContext(),R.raw.reiniciar, 1);
        sonidoWin = sp1.load(getContext(), R.raw.win, 1);


        updateGoalFromDB();
        updateListFromBD();
        printKcalTotales();

        return rootView;
    }


    public void maybeSetGoal(EditText editTextGoal) {
        String newGoal = editTextGoal.getText().toString();

        if(newGoal.isEmpty()) {
            Toast.makeText(getActivity(), "Debes poner un objetivo", Toast.LENGTH_SHORT).show();
        } else {
            setGoal(newGoal);
            updateDBFromGoal();
        }
    }


    public void setGoal(String newGoal) {
        goal.setText(newGoal);
    }


    public void fabClicked(FragmentTwo f2) {
        //Efecto de sonido del fab
        sp1.play(sonidoFab, 1, 1, 1, 0, 0);
        //

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
                    updateItemInDB(a);
                    break;
                }
            }
        } else {
            addItemToList(alimento);
            addItemToDB(alimento);
        }

        //Efecto de sonido del add
        sp1.play(sonidoAdd, 1, 1, 1, 0, 0);

        adapter.notifyDataSetChanged();
        printKcalTotales();

    }


    public void addItemToList(Alimento alimento) {
        listaDiario.add(0, alimento);
    }




    public void maybeRemoveItem(int position) {
        Alimento alimento = listaDiario.get(position);

        if(alimento.getUds() > 1) {
            alimento.decrementUds();
            updateItemInDB(alimento);
        } else {
            removeItemFromList(position);
            removeItemFromDB(alimento);
        }

        //Efecto de sonido del delete
        sp1.play(sonidoDelete, 1, 1, 1, 0, 0);

        adapter.notifyDataSetChanged();
        printKcalTotales();
    }


    public void removeItemFromList(int position) {
        listaDiario.remove(position);
    }




    public void resetDay() {
        //Efecto de sonido del reset
        sp1.play(sonidoReiniciar, 1, 1, 1, 0, 0);

        clearDB(TABLE_1);
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

        int objetivo = Integer.parseInt(goal.getText().toString());
        if(total >= objetivo && objetivo != 0) {
            //Efecto de sonido del goal
            sp1.play(sonidoWin, 1, 1, 1, 0, 0);
            //

            Toast.makeText(getContext(), "¡Enhorabuena! Has alcanzado tu objetivo :)", Toast.LENGTH_LONG).show();
        }
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
        clearDB(TABLE_1);
        for(Alimento alimento: listaDiario) {
            addItemToDB(alimento);
        }
    }


    public void updateGoalFromDB() {
        SQLiteDatabase db = getDB();
        Cursor c = db.query(TABLE_3, new String[] {"goal"}, null, null, null, null, null);
        c.moveToFirst();

        if(c.getCount() != 0) {
            int kcal_goal = c.getInt(0);
            setGoal(String.valueOf(kcal_goal));
        } else {
            setGoal("0");
        }
        c.close();
        db.close();
    }


    public void updateDBFromGoal() {
        clearDB(TABLE_3);
        ContentValues reg = new ContentValues();
        reg.put("goal", goal.getText().toString());

        SQLiteDatabase db = getDB();
        db.insert(TABLE_3, null, reg);
        db.close();
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


    public void updateItemInDB(Alimento alimento) {
        ContentValues reg = new ContentValues();
        reg.put("nombre", alimento.getNombre());
        reg.put("kcal", alimento.getKcal());
        reg.put("uds", alimento.getUds());

        SQLiteDatabase db = getDB();
        db.update(TABLE_1, reg, "nombre='" + alimento.getNombre() + "'", null);
        db.close();
    }


    public void clearDB(String table) {
        SQLiteDatabase db = getDB();
        db.delete(table, null, null);
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
