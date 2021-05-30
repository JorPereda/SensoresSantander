package baseDeDatos;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class EstadisticasDbHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Estadisticas.db";

    private static final String SQL_CREATE_ENTRIES_MEDIDAS = "CREATE TABLE " + EstadisticasContract.MedidasSensorEntry.TABLE_NAME + " ("
            + EstadisticasContract.MedidasSensorEntry.ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + EstadisticasContract.MedidasSensorEntry.ID_SENSOR + " INTEGER NOT NULL,"
            + EstadisticasContract.MedidasSensorEntry.FECHA + " TEXT,"
            + EstadisticasContract.MedidasSensorEntry.FECHACORTADA + " TEXT,"
            + EstadisticasContract.MedidasSensorEntry.TEMP + " TEXT,"
            + EstadisticasContract.MedidasSensorEntry.RUIDO + " TEXT,"
            + EstadisticasContract.MedidasSensorEntry.LUZ + " TEXT)";

    private static final String SQL_DELETE_ENTRIES_MEDIDAS =
            "DROP TABLE IF EXISTS " + EstadisticasContract.MedidasSensorEntry.TABLE_NAME;

    public EstadisticasDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES_MEDIDAS);
        //mockData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES_MEDIDAS);
        onCreate(db);
    }

    //Mocks para introducir datos de prueba
    private void mockData(SQLiteDatabase sqLiteDatabase) {
        mockMedidas(sqLiteDatabase, new Medidas(1, 1, "10/02/2021","10/02/2021","20","", "0"));
        mockMedidas(sqLiteDatabase, new Medidas(2, 1, "10/02/2021","10/02/2021","25","", "0"));
        mockMedidas(sqLiteDatabase, new Medidas(3, 1, "10/02/2021","10/02/2021","28","", "0"));
        mockMedidas(sqLiteDatabase, new Medidas(4, 2, "10/02/2021","10/02/2021","","70", ""));
        mockMedidas(sqLiteDatabase, new Medidas(5, 2, "10/02/2021","10/02/2021","","80", ""));
        mockMedidas(sqLiteDatabase, new Medidas(6, 2, "10/02/2021","10/02/2021","","90", ""));
    }


    public long mockMedidas(SQLiteDatabase db, Medidas medidas) {
        return db.insert(
                EstadisticasContract.MedidasSensorEntry.TABLE_NAME,
                null,
                medidas.toContentValues());
    }

}
