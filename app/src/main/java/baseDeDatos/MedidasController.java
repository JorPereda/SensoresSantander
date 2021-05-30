package baseDeDatos;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MedidasController {

    private EstadisticasDbHelper ayudanteBD;
    private String NOMBRE_TABLA = "medidas";

    public MedidasController(Context contexto) {
        ayudanteBD = new EstadisticasDbHelper(contexto);
    }


    public long nuevaMedida(Medidas medida){
        // writable porque vamos a insertar
        SQLiteDatabase baseDeDatos = ayudanteBD.getWritableDatabase();
        ContentValues valoresParaInsertar = new ContentValues();
        valoresParaInsertar.put("ID_SENSOR", medida.getIdSensor());
        valoresParaInsertar.put("FECHA", medida.getFecha());
        valoresParaInsertar.put("FECHACORTADA", medida.getFechaCortada());
        valoresParaInsertar.put("TEMP", medida.getTemperatura());
        valoresParaInsertar.put("RUIDO", medida.getRuido());
        valoresParaInsertar.put("LUZ", medida.getLuz());
        return baseDeDatos.insert(NOMBRE_TABLA, null, valoresParaInsertar);
    }

    public ArrayList<Medidas> obtenerMedidasTotales() {
        ArrayList<Medidas> medidas = new ArrayList<>();
        // readable porque no vamos a modificar, solamente leer
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        // SELECT
        String[] columnasAConsultar = {"ID", "ID_SENSOR", "FECHA", "FECHACORTADA", "TEMP", "RUIDO", "LUZ"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,//from medidas
                columnasAConsultar,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor == null) {
            /*
                Salimos aquí porque hubo un error, regresar
                lista vacía
             */
            return medidas;

        }
        // Si no hay datos, igualmente regresamos la lista vacía
        if (!cursor.moveToFirst()) return medidas;

        // En caso de que sí haya, iteramos y vamos agregando los
        // datos a la lista de medidas
        do {
            // El 0 es el número de la columna, como seleccionamos
            // el id es 0, idsensor 1 y fecha es 2
            long id = cursor.getLong(0);
            int idSensor = cursor.getInt(1);
            String fecha = cursor.getString(2);
            String fechaCortada = cursor.getString(3);
            String temp = cursor.getString(4);
            String ruido = cursor.getString(5);
            String luz = cursor.getString(6);
            Medidas medidaObtenidaDeBD = new Medidas(id, idSensor, fecha, fechaCortada, temp, ruido, luz);
            medidas.add(medidaObtenidaDeBD);
        } while (cursor.moveToNext());

        // Fin del ciclo. Cerramos cursor y regresamos la lista
        cursor.close();
        return medidas;
    }

    public ArrayList<String> obtenerTemperaturasSensor(String sensorId) {
        ArrayList<String> temperaturas = new ArrayList<>();
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"TEMP"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                "ID_SENSOR = ?",
                new String[]{sensorId},
                null,
                null,
                null
        );
        if (cursor == null) {
            return temperaturas;
        }
        if (!cursor.moveToFirst()) return temperaturas;
        do {
            String temp = cursor.getString(0);
            temperaturas.add(temp);
        } while (cursor.moveToNext());
        cursor.close();
        return temperaturas;
    }

    public ArrayList<String> obtenerRuidoSensor(String sensorId) {
        ArrayList<String> ruidos = new ArrayList<>();
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"RUIDO"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                "ID_SENSOR = ?",
                new String[]{sensorId},
                null,
                null,
                null
        );
        if (cursor == null) {
            return ruidos;
        }
        if (!cursor.moveToFirst()) return ruidos;
        do {
            String ruido = cursor.getString(0);
            ruidos.add(ruido);
        } while (cursor.moveToNext());
        cursor.close();
        return ruidos;
    }

    public ArrayList<String> obtenerLuzSensor(String sensorId) {
        ArrayList<String> luminosidades = new ArrayList<>();
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"LUZ"};
        Cursor cursor = baseDeDatos.query(
                NOMBRE_TABLA,
                columnasAConsultar,
                "ID_SENSOR = ?",
                new String[]{sensorId},
                null,
                null,
                null
        );
        if (cursor == null) {
            return luminosidades;
        }
        if (!cursor.moveToFirst()) return luminosidades;
        do {
            String luz = cursor.getString(0);
            luminosidades.add(luz);
        } while (cursor.moveToNext());
        cursor.close();
        return luminosidades;
    }

    public ArrayList<String> obtenerTemperaturasSensorFecha(String sensorId, String fecha) {
        ArrayList<String> temperaturas = new ArrayList<>();
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"TEMP", "FECHACORTADA"};
        String[] selectionArgs = new String[]{sensorId, fecha};
        Cursor cursor = baseDeDatos.rawQuery("SELECT TEMP, FECHACORTADA FROM " + NOMBRE_TABLA +
                " WHERE ID_SENSOR" + " = ? and instr(FECHACORTADA, ?)", selectionArgs);
        if (cursor == null) {
            return temperaturas;
        }
        if (!cursor.moveToFirst()) return temperaturas;
        do {
            String temp = cursor.getString(0);
            temperaturas.add(temp);
        } while (cursor.moveToNext());
        cursor.close();
        return temperaturas;
    }

    public ArrayList<String> obtenerRuidoSensorFecha(String sensorId, String fecha) {
        ArrayList<String> ruidos = new ArrayList<>();
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"RUIDO", "FECHACORTADA"};
        String[] selectionArgs = new String[]{sensorId, fecha};
        Cursor cursor = baseDeDatos.rawQuery("SELECT RUIDO, FECHACORTADA FROM " + NOMBRE_TABLA +
                " WHERE ID_SENSOR" + " = ? and instr(FECHACORTADA, ?)", selectionArgs);
        if (cursor == null) {
            return ruidos;
        }
        if (!cursor.moveToFirst()) return ruidos;
        do {
            String ruido = cursor.getString(0);
            ruidos.add(ruido);
        } while (cursor.moveToNext());
        cursor.close();
        return ruidos;
    }

    public ArrayList<String> obtenerLuzSensorFecha(String sensorId, String fecha) {
        ArrayList<String> luminosidades = new ArrayList<>();
        SQLiteDatabase baseDeDatos = ayudanteBD.getReadableDatabase();
        String[] columnasAConsultar = {"LUZ", "FECHACORTADA"};
        String[] selectionArgs = new String[]{sensorId, fecha};
        Cursor cursor = baseDeDatos.rawQuery("SELECT LUZ, FECHACORTADA FROM " + NOMBRE_TABLA +
                " WHERE ID_SENSOR" + " = ? and instr(FECHACORTADA, ?)", selectionArgs);
        if (cursor == null) {
            return luminosidades;
        }
        if (!cursor.moveToFirst()) return luminosidades;
        do {
            String luz = cursor.getString(0);
            luminosidades.add(luz);
        } while (cursor.moveToNext());
        cursor.close();
        return luminosidades;
    }

    public int eliminarMedida(Medidas medida) {
        SQLiteDatabase baseDeDatos = ayudanteBD.getWritableDatabase();
        String[] argumentos = {String.valueOf(medida.getId())};
        return baseDeDatos.delete(NOMBRE_TABLA, "id = ?", argumentos);
    }

}
