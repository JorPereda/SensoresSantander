package baseDeDatos;

import android.provider.BaseColumns;

public class EstadisticasContract {

    public static abstract class MedidasSensorEntry implements BaseColumns {
        public static final String TABLE_NAME ="medidas";

        public static final String ID = "id";
        public static final String ID_SENSOR = "id_sensor";
        public static final String FECHA = "fecha";
        public static final String FECHACORTADA = "fechaCortada";
        public static final String TEMP = "temp";
        public static final String RUIDO = "ruido";
        public static final String LUZ = "luz";
    }

}
