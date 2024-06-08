package com.example.contener;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class BDData extends SQLiteOpenHelper {
    final String CREATE_USERS_TABLE = "CREATE TABLE USERS (" +
            "NAMES TEXT, " +
            "UID TEXT, " +
            "BIRTHDATE TEXT, " +
            "CHILDRENQUANTITY TEXT, " +
            "STATE TEXT, PREGNANT TEXT, " +
            "HAVECHILDRENS TEXT, " +
            "CITY TEXT, " +
            "OHTERCITY TEXT, " +
            "SEX TEXT, " +
            "DATEBORNLASTSON TEXT," +
            "ACTIVE BOOLEAN)";
    //TODO: colocar correctamente los tipos de datos

    public BDData(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USERS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Verifica si existe una version mas antigua de la base de datos
        db.execSQL("DROP TABLE IF EXISTS USERS");
        onCreate(db);
    }

    public void setDataUser(ContentValues dataUser){
        SQLiteDatabase db = this.getWritableDatabase();
        db.insert("USERS", null, dataUser);
        db.close();
    }

    public void updateDataUser(ContentValues dataUser){
        SQLiteDatabase db = this.getWritableDatabase();
        //db.insert("USERS", null, dataUser);
        String[] args = new String[]{dataUser.get("UID").toString()};
        db.update("USERS", dataUser, "UID=?", args);
        db.close();
    }

    public HashMap<String, String> getDataUser(String uid){
        HashMap<String,String> value = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM USERS WHERE UID='"+uid+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();

        for (int j = 0; j < registros.getColumnCount(); j++) {
            value.put(registros.getColumnName(j), registros.getString(j));
        }

        return value;
    }

    public boolean existsUser(String uid){
        Boolean value = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM USERS WHERE UID='"+uid+"'";
        Cursor registros = db.rawQuery(search, null);

        if (registros.getCount()==1){
            value = true;
        }

        return value;
    }

}

    /*//----------------------------------------------------------------------------------------------
    //AEDES
    public void CreateColumnsAedes(Context oldcontext){
        archivos = new Archivos(oldcontext);
        ArrayList<String> cabeceras = new ArrayList<>();
        cabeceras = archivos.getCodeCabecerasAedes(oldcontext);

        SQLiteDatabase dbread = this.getReadableDatabase();
        String search = "SELECT * FROM AEDES";
        @SuppressLint("Recycle") Cursor registros = dbread.rawQuery(search, null);
        List<String> columnNamesAedes = Arrays.asList(registros.getColumnNames());
        dbread.close();

        SQLiteDatabase db = this.getWritableDatabase();
        for (Object o : cabeceras) {
            if(!columnNamesAedes.contains(o.toString())){
                String addColumn = "ALTER TABLE AEDES ADD COLUMN "+o.toString()+" TEXT";
                db.execSQL(addColumn);
            }
        }
        db.close();
    }

    public void insertValuesAedes(HashMap<String, String> valuesAedes){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        Object[] columnsNameFamiliaToInsert = valuesAedes.keySet().toArray();
        for (Object o : columnsNameFamiliaToInsert) {
            //Log.e("msg", o.toString());
            valores.put(o.toString(), valuesAedes.get(o.toString()));
        }

        db.insert("AEDES", null, valores);
        db.close();
    }

    public ArrayList<HashMap<String, String>> searchValuesAedesForDate(String date){
        ArrayList<HashMap<String, String>> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM AEDES";// WHERE FECHA='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount();i++) {
            HashMap<String, String> value = new HashMap<>();
            for (int j = 0; j < registros.getColumnCount(); j++) {
                value.put(registros.getColumnName(j), registros.getString(j));
            }
            registros.moveToNext();
            values.add(value);
        }
        db.close();

        return values;
    }

    //----------------------------------------------------------------------------------------------
    //DOMICILIO
    public void CreateColumnsDomicilio(Context oldcontext){
        archivos = new Archivos(oldcontext);
        ArrayList<String> cabeceras = new ArrayList<>();
        cabeceras = archivos.getCodeCabecerasDomicilio(oldcontext);

        SQLiteDatabase dbread = this.getReadableDatabase();
        String search = "SELECT * FROM DOMICILIO";
        @SuppressLint("Recycle") Cursor registros = dbread.rawQuery(search, null);
        List<String> columnNamesDomicilio = Arrays.asList(registros.getColumnNames());
        dbread.close();

        SQLiteDatabase db = this.getWritableDatabase();
        for (Object o : cabeceras) {
            if(!columnNamesDomicilio.contains(o.toString())){
                String addColumn = "ALTER TABLE DOMICILIO ADD COLUMN "+o.toString()+" TEXT";
                db.execSQL(addColumn);
            }
        }
        db.close();
    }

    // Para cache de UD de la version 2.2
    public void CreateCategoriesCacheUD(Context oldcontext){
        archivos = new Archivos(oldcontext);
        ArrayList<String> cabeceras = new ArrayList<>();
        cabeceras = archivos.getCodeCabecerasDomicilio(oldcontext);
        cabeceras.addAll(archivos.getCodeCabecerasPersonas(oldcontext));

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        for (String code : cabeceras){
            if (!checkExistsCode(code)) {
                //Log.e(code, "codigo a cache ud");
                valores.put("CODE", code);
                valores.put("VALUE", "");
                db.insert("CACHEUD", null, valores);
            }
        }

        db.close();
    }

    private boolean checkExistsCode(String code){
        Boolean value = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM CACHEUD WHERE CODE='"+code+"'";
        Cursor registros = db.rawQuery(search, null);

        if (registros.getCount()==1){
            value = true;
        }

        return value;
    }

    public void updateCacheUD(HashMap<String, String> values){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();

        Object[] codes = values.keySet().toArray();
        for (Object code : codes) {

            if(!checkExistsCode(code.toString()) && code.toString()!=null){
                ContentValues new_code = new ContentValues();
                new_code.put("CODE", (String) code);
                new_code.put("VALUE", values.get(code.toString()));
                //Log.e("code", "v"+code.toString()+values.get(code.toString()));
                db.insert("CACHEUD", null, new_code);
            }else {
                valores.put("CODE", (String) code);
                if (values.get(code.toString()) == null) {
                    valores.put("VALUE", "");
                } else {
                    valores.put("VALUE", values.get(code.toString()));
                }
                //Log.e(code.toString() + "update", String.valueOf(values.get(code.toString())));
                String[] args = new String[]{code.toString()};
                db.update("CACHEUD", valores, "CODE=?", args);
            }
        }

        db.close();
    }

    public HashMap<String, String> getValuesCacheUD(){
        HashMap<String, String> values = new HashMap<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM CACHEUD";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            values.put(registros.getString(0), registros.getString(1));
            //Log.e("values save cache", registros.getString(0) + registros.getString(1)+" values");
            registros.moveToNext();
        }

        db.close();
        return values;
    }

    public String getValue4CodeCacheUD(String code){
        String value = "";

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT VALUE FROM CACHEUD WHERE CODE='"+code+"'";
        Cursor registros = db.rawQuery(search, null);

        if (registros.getCount()!=0){
        registros.moveToFirst();
        value = registros.getString(0);}

        db.close();
        return value;
    }

    public String getValue4CodePerson(String code, String ultimafecha, String latitud, String longitud, String DNI){
        SQLiteDatabase db = this.getReadableDatabase();

        String value = "";

        String search = "SELECT "+code+" FROM PERSONS WHERE FECHA='"+ultimafecha+"' AND LATITUD='"+latitud+"' AND LONGITUD='"+longitud+"' AND DNI='"+DNI+"'";
        Cursor registros = db.rawQuery(search, null);

        if (registros.getCount()!=0){
            registros.moveToFirst();
            value = registros.getString(0);}

        db.close();
        return value;
    }

    public String getValues(){
        SQLiteDatabase db = this.getReadableDatabase();

        String value = "";

        String search = "SELECT * FROM PERSONS";
        Cursor registros = db.rawQuery(search, null);

        if (registros.getCount()!=0){
            registros.moveToFirst();
            value = registros.getString(0);}

        db.close();
        return value;
    }

    public void deleteCacheUD(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CACHEUD");
    }

    //----------------------------------------------------------------------------------------------
    // insertar una familia en la base de datos
    public void insert_family(FamiliarUnityClass family){
        //Log.e("marca", "insert family funcion");
        CreateColumnsFamily(family);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        Object[] columnsNameFamiliaToInsert = family.Data.keySet().toArray();

        for (Object o : columnsNameFamiliaToInsert) {
            if (!(!o.toString().contains("REDO") && o.toString().contains("RE"))){
                    String key = o.toString();
                    valores.put(key, family.Data.get(key));
                //Log.e("marca "+key, family.Data.get(key) );
            }
        }

        db.insert("FAMILIES", null, valores);
        db.close();

    }

    private void CreateColumnsFamily(FamiliarUnityClass family){
        Object[] columnsNameFamiliaToInsert = family.Data.keySet().toArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        @SuppressLint("Recycle") Cursor registros = db.rawQuery(search, null);
        List<String> columnNamesFamilies = Arrays.asList(registros.getColumnNames());
        db.close();

        for (Object o : columnsNameFamiliaToInsert) {

                if (!(!o.toString().contains("REDO") && o.toString().contains("RE"))){

                    if (!columnNamesFamilies.contains(o.toString())) {
                        db = this.getWritableDatabase();
                        String addColumn = "ALTER TABLE FAMILIES ADD COLUMN " + o.toString() + " TEXT";
                        db.execSQL(addColumn);
                        db.close();
                    }

                }

        }
    }

    public ArrayList<FamiliarUnityClass> searchAllFamilies(Context context){
        ArrayList<FamiliarUnityClass> families = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            families.add(familyaux);
            registros.moveToNext();
        }
        db.close();

        return families;
    }

    public ArrayList<FamiliarUnityClass> SearchFamilyDate(Context context, String date){
        ArrayList<FamiliarUnityClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES WHERE FECHA='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(familyaux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public ArrayList<FamiliarUnityClass> SearchFamilyRenuenteDeshabitadaDate(Context context, String date){
        ArrayList<FamiliarUnityClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES WHERE SITUACION_HABITACIONAL='RENUENTE' AND FECHA='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(familyaux);
            registros.moveToNext();
        }

        search = "SELECT * FROM FAMILIES WHERE SITUACION_HABITACIONAL='DESHABITADA' AND FECHA='"+date+"'";
        registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(familyaux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public FamiliarUnityClass SearchFamilyCoordinate(Context context, String latitude, String longitude){
        FamiliarUnityClass values = new FamiliarUnityClass(context);

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES WHERE LATITUD='"+latitude+"' AND LONGITUD='"+longitude+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int j=0; j<registros.getColumnCount();j++){
            values.Data.put(registros.getColumnName(j),
                        registros.getString(j));
        }
        db.close();

        return values;
    }

    public ArrayList<FamiliarUnityClass> MinnusThirtyMeters(Context context, double latitude, double longitude){
        ArrayList<FamiliarUnityClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            //familyaux.LoadDataHashToParameters();
            Ubicacion ubicacion = new Ubicacion(context);

            if(ubicacion.DistanceMeters(
                    latitude, longitude,
                    Double.parseDouble(familyaux.Data.get("LATITUD")),
                    Double.parseDouble(familyaux.Data.get("LONGITUD")))
                    <30)
            {
                values.add(familyaux);
            }
            registros.moveToNext();
        }
        db.close();

        for (int j=0; j<values.size(); j++ ){
            for (int x=0; x<values.size(); x++) {
                if (Objects.equals(values.get(j).Data.get("LONGITUD"), values.get(x).Data.get("LONGITUD"))) {
                    values.remove(x);
                }
            }
        }

        return values;
    }

    public ArrayList<FamiliarUnityClass> MinnusMeters(Context context, double latitude, double longitude, int meters){
        ArrayList<FamiliarUnityClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            //familyaux.LoadDataHashToParameters();
            Ubicacion ubicacion = new Ubicacion(context);

            if(ubicacion.DistanceMeters(
                    latitude, longitude,
                    Double.parseDouble(familyaux.Data.get("LATITUD")),
                    Double.parseDouble(familyaux.Data.get("LONGITUD")))
                    <meters)
            {
                values.add(familyaux);
            }
            registros.moveToNext();
        }
        db.close();

        for (int j=0; j<values.size(); j++ ){
            for (int x=0; x<values.size(); x++) {
                if (Objects.equals(values.get(j).Data.get("LONGITUD"), values.get(x).Data.get("LONGITUD"))) {
                    values.remove(x);
                }
            }
        }

        return values;
    }

    public double Meters(Context context, double latitude, double longitude){
        ArrayList<FamiliarUnityClass> values = new ArrayList<>();
        double m = 0;
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                familyaux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            familyaux.LoadDataHashToParameters();
            Ubicacion ubicacion = new Ubicacion(context);
            m = ubicacion.DistanceMeters(
                    latitude, longitude,
                    Double.parseDouble(String.valueOf(familyaux.Latitud)),
                    Double.parseDouble(String.valueOf(familyaux.Longitud)));
            if(ubicacion.DistanceMeters(
                    latitude, longitude,
                    Double.parseDouble(String.valueOf(familyaux.Latitud)),
                    Double.parseDouble(String.valueOf(familyaux.Longitud)))
                    <30){
                values.add(familyaux);}
            registros.moveToNext();
        }
        db.close();

        return m;
    }

    public long insert_person(PersonClass person){
        CreateColumnsPerson(person);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        Object[] columnsNamePersonToInsert = person.Data.keySet().toArray();
        for (int i=0; i<columnsNamePersonToInsert.length; i++) {
            if (!columnsNamePersonToInsert[i].toString().contains("REDO")) {
                valores.put(columnsNamePersonToInsert[i].toString(),
                        person.Data.get(columnsNamePersonToInsert[i].toString()));
            }
        }
        long value = db.insert("PERSONS", null, valores);
        db.close();
        return value;
    }

    private void CreateColumnsPerson(PersonClass person){
        Object[] columnsNamePersonToInsert = person.Data.keySet().toArray();
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS";
        @SuppressLint("Recycle") Cursor registros = db.rawQuery(search, null);
        List<String> columnNamesPerson = Arrays.asList(registros.getColumnNames());
        db.close();

        for (Object o : columnsNamePersonToInsert) {
            if (!columnNamesPerson.contains(o.toString())){
                if (!o.toString().contains("REDO")) {
                    db = this.getWritableDatabase();
                    String addColumn = "ALTER TABLE PERSONS ADD COLUMN " + o.toString() + " TEXT";
                    db.execSQL(addColumn);
                    db.close();
                }
            }
        }
    }

    public ArrayList<PersonClass> SearchPersonsCoordinates(Context context, String latitude, String longitude){
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT DISTINCT * FROM PERSONS WHERE LATITUD='"+latitude+"' AND LONGITUD='"+longitude+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public ArrayList<String> SearchStuacionHabitacional(String latitude, String longitude, String Fecha){
        ArrayList<String> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT SITUACION_HABITACIONAL, CODIGO_COLOR FROM FAMILIES WHERE LATITUD='"+latitude+"' AND LONGITUD='"+longitude+"' AND FECHA='"+Fecha+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        values.add(registros.getString(0));
        values.add(registros.getString(1));
        db.close();

        return values;
    }

    public ArrayList<PersonClass> SearchPersonsCoordinatesFilatories(Context context, String latitude, String longitude){
        ArrayList<PersonClass> values = new ArrayList<>();

        String search = "SELECT DISTINCT LATITUD, LONGITUD, FECHA_NACIMIENTO, NOMBRE, APELLIDO, DNI, SEXO FROM PERSONS WHERE LATITUD='"+latitude+"' AND LONGITUD='"+longitude+"'";

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public void renameColumns(){
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS";
        @SuppressLint("Recycle") Cursor registros = db.rawQuery(search, null);
        List<String> columnNamesPerson = Arrays.asList(registros.getColumnNames());

        db = this.getWritableDatabase();
        if (columnNamesPerson.contains("DIAMESAÑO")) {
            String editNameColumn = "ALTER TABLE PERSONS RENAME COLUMN DIAMESAÑO TO FECHA_NACIMIENTO";
            db.execSQL(editNameColumn);
        }
        db.close();
    }

    public ArrayList<PersonClass> SearchAllPersons(Context context){
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                if (registros.getString(j) != null) {
                    if (registros.getString(j).length() != 0) {
                        aux.Data.put(registros.getColumnName(j),
                                registros.getString(j));
                    }
                }
            }
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public int CantFamilies(Context context){

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);
        int value = registros.getCount();
        db.close();

        return value;
    }

    public ArrayList<PersonClass> SearchAllPersonsWithDNI(Context context){
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE NOT(DNI = '')";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public ArrayList<PersonClass> SearchPersonsNameLastnameDNI(Context context, String name, String lastname, String dni){
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        //String search = "SELECT * FROM PERSONS WHERE NOMBRE LIKE '%"+name+"%' AND " +
        //        "APELLIDO LIKE '%"+lastname+"%' DNI LIKE '%"+dni+"%'";
        String search = "SELECT DISTINCT NOMBRE, APELLIDO, DNI, LATITUD, LONGITUD FROM PERSONS WHERE DNI LIKE '%"+dni+"%' AND NOMBRE LIKE '%"+name+"%' AND APELLIDO LIKE '%"+lastname+"%'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public ArrayList<LatLng> SearchAllCordinatesForDate(String date){
        ArrayList<LatLng> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT LATITUD, LONGITUD FROM FAMILIES WHERE FECHA LIKE '%"+date+"%'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            values.add(new LatLng(Double.parseDouble(registros.getString(0)), Double.parseDouble(registros.getString(1))));
            registros.moveToNext();
        }
        db.close();

        return values;
    }



    public String Date(){
        String values = "";

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT FECHA FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        values = registros.getString(0);
        db.close();

        return values;
    }

    public ArrayList<String> ColorCodeForDate(Context context, String cab_color, String date){
        ArrayList<String> values = new ArrayList<>();
        cab_color = cab_color.toUpperCase();
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT "+ cab_color +" FROM FAMILIES WHERE FECHA LIKE '%"+date+"%'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            values.add(registros.getString(0));
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public ArrayList<PersonClass> SearchPersonsCoordinatesAndDate(Context context, String latitude, String longitude, String date){
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE LATITUD='"+latitude+
                "' AND LONGITUD='"+longitude+"' AND FECHA='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            aux.LoadDataHashToParamaters();
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public JSONObject PiramidePoblacional () throws JSONException {

        JSONObject piramide = new JSONObject();

        JSONObject femeninas = new JSONObject();
        femeninas.put("-1", 0);
        femeninas.put("1", 0);
        femeninas.put("2-5", 0);
        femeninas.put("6-9", 0);
        femeninas.put("10-14", 0);
        femeninas.put("15-19", 0);
        femeninas.put("20-24", 0);
        femeninas.put("25-29", 0);
        femeninas.put("30-34", 0);
        femeninas.put("35-39", 0);
        femeninas.put("40-44", 0);
        femeninas.put("45-49", 0);
        femeninas.put("50-54", 0);
        femeninas.put("55-59", 0);
        femeninas.put("60-64", 0);
        femeninas.put("65-69", 0);
        femeninas.put("70 y +", 0);
        
        JSONObject masculinos = new JSONObject();
        masculinos.put("-1", 0);
        masculinos.put("1", 0);
        masculinos.put("2-5", 0);
        masculinos.put("6-9", 0);
        masculinos.put("10-14", 0);
        masculinos.put("15-19", 0);
        masculinos.put("20-24", 0);
        masculinos.put("25-29", 0);
        masculinos.put("30-34", 0);
        masculinos.put("35-39", 0);
        masculinos.put("40-44", 0);
        masculinos.put("45-49", 0);
        masculinos.put("50-54", 0);
        masculinos.put("55-59", 0);
        masculinos.put("60-64", 0);
        masculinos.put("65-69", 0);
        masculinos.put("70 y +", 0);

        SQLiteDatabase db = this.getReadableDatabase();
        //TODO: evitar contar para la piramide las personas que se han registrado mas de una vez
        String search = "SELECT FECHA_NACIMIENTO, SEXO FROM PERSONS";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();

        for (int i=0; i<registros.getCount(); i++) {
            SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");//yyyy-MM-dd'T'HH:mm:ss'Z'");
            try {
                Date date = format.parse(registros.getString(0));
                Date date_today = Calendar.getInstance().getTime();
                //Log.e("piramide", date.toString());
                //Log.e("dob", registros.getString(0));
                //Log.e("fecha actual", date_today.toString());
                int milisecondsByDay = 86400000;
                float dias = (float) ((date_today.getTime()-date.getTime()) / milisecondsByDay);
                float años = (float) (dias/365.0);
                //Log.e("dias calculados", Float.toString(dias));
                //Log.e("años calculados", Float.toString(años));

                if (registros.getString(1).equals("F")){
                    if (años<1){
                        femeninas.put("-1", (int)femeninas.get("-1")+1);
                    } else if (años<2 && años>=1) {
                        femeninas.put("1", (int)femeninas.get("1")+1);
                    } else if (años>=2 && años<=5) {
                        femeninas.put("2-5", (int)femeninas.get("2-5")+1);
                    } else if (años>5 && años<=9) {
                        femeninas.put("6-9", (int) femeninas.get("6-9")+1);
                    } else if (años>9 && años<=14) {
                        femeninas.put("10-14", (int) femeninas.get("10-14")+1);
                    } else if (años>14 && años<=19) {
                        femeninas.put("15-19", (int) femeninas.get("15-19")+1);
                    } else if (años>19 && años<=24) {
                        femeninas.put("20-24", (int) femeninas.get("20-24")+1);
                    } else if (años>24 && años<=29) {
                        femeninas.put("25-29", (int) femeninas.get("25-29")+1);
                    } else if (años>29 && años<=34) {
                        femeninas.put("30-34", (int) femeninas.get("30-34")+1);
                    } else if (años>34 && años<=39) {
                        femeninas.put("35-39", (int) femeninas.get("35-39")+1);
                    } else if (años>39 && años<=44) {
                        femeninas.put("40-44", (int) femeninas.get("40-44")+1);
                    } else if (años>44 && años<=49) {
                        femeninas.put("45-49", (int) femeninas.get("45-49")+1);
                    } else if (años>49 && años<=54) {
                        femeninas.put("50-54", (int) femeninas.get("50-54")+1);
                    } else if (años>54 && años<=59) {
                        femeninas.put("55-59", (int) femeninas.get("55-59")+1);
                    } else if (años>59 && años<=64) {
                        femeninas.put("60-64", (int) femeninas.get("60-64")+1);
                    } else if (años>64 && años<=69) {
                        femeninas.put("65-69", (int) femeninas.get("65-69")+1);
                    } else if (años>70) {
                        femeninas.put("70 y +", (int) femeninas.get("70 y +")+1);
                    }
                } else if (registros.getString(1).equals("M")) {
                    if (años<1){
                        masculinos.put("-1", (int)masculinos.get("-1")+1);
                    } else if (años<2 && años>=1) {
                        masculinos.put("1", (int)masculinos.get("1")+1);
                    } else if (años>=2 && años<=5) {
                        masculinos.put("2-5", (int)masculinos.get("2-5")+1);
                    } else if (años>5 && años<=9) {
                        masculinos.put("6-9", (int) masculinos.get("6-9")+1);
                    } else if (años>9 && años<=14) {
                        masculinos.put("10-14", (int) masculinos.get("10-14")+1);
                    } else if (años>14 && años<=19) {
                        masculinos.put("15-19", (int) masculinos.get("15-19")+1);
                    } else if (años>19 && años<=24) {
                        masculinos.put("20-24", (int) masculinos.get("20-24")+1);
                    } else if (años>24 && años<=29) {
                        masculinos.put("25-29", (int) masculinos.get("25-29")+1);
                    } else if (años>29 && años<=34) {
                        masculinos.put("30-34", (int) masculinos.get("30-34")+1);
                    } else if (años>34 && años<=39) {
                        masculinos.put("35-39", (int) masculinos.get("35-39")+1);
                    } else if (años>39 && años<=44) {
                        masculinos.put("40-44", (int) masculinos.get("40-44")+1);
                    } else if (años>44 && años<=49) {
                        masculinos.put("45-49", (int) masculinos.get("45-49")+1);
                    } else if (años>49 && años<=54) {
                        masculinos.put("50-54", (int) masculinos.get("50-54")+1);
                    } else if (años>54 && años<=59) {
                        masculinos.put("55-59", (int) masculinos.get("55-59")+1);
                    } else if (años>59 && años<=64) {
                        masculinos.put("60-64", (int) masculinos.get("60-64")+1);
                    } else if (años>64 && años<=69) {
                        masculinos.put("65-69", (int) masculinos.get("65-69")+1);
                    } else if (años>70) {
                        masculinos.put("70 y +", (int) masculinos.get("70 y +")+1);
                    }
                }
                registros.moveToNext();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        piramide.put("F", femeninas);
        piramide.put("M", masculinos);
        //Log.e("json piramide", piramide.toString());

        return piramide;
    }

    public ArrayList<PersonClass> SearchPersonsDate(Context context, String date){
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE FECHA='"+date+"'";// AND FECHA='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            PersonClass aux = new PersonClass(context);
            for (int j=0; j<registros.getColumnCount();j++){
                int cont = registros.getColumnName(j).split("_").length;


                if(cont==2){
                    if(registros.getColumnName(j).split("_")[1].equals("0")){
                        aux.Data.put(registros.getColumnName(j),
                                registros.getString(j));
                    }
                    else {
                        aux.Data.put(registros.getColumnName(j),
                                registros.getString(j));
                    }
                }
                else {
                    aux.Data.put(registros.getColumnName(j),
                            registros.getString(j));
                }
                }
            aux.LoadDataHashToParamaters();
            values.add(aux);
            registros.moveToNext();

            /*Object[] auxkey = aux.Data.keySet().toArray();
            for (Object c : auxkey){
                Log.e("data0", c.toString());
            }
        }
        db.close();

        return values;
    }*/

    /*public FamiliarUnityClass SearchFamilyCoordinateAndDate(Context context, String latitude, String longitude, String date){
        FamiliarUnityClass values = new FamiliarUnityClass(context);

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES WHERE LATITUD='"+latitude+"' " +
                "AND LONGITUD='"+longitude+"' AND FECHA='"+date+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int j=0; j<registros.getColumnCount();j++){
            values.Data.put(registros.getColumnName(j),
                    registros.getString(j));
        }
        values.LoadDataHashToParameters();
        db.close();

        return values;
    }

    public ArrayList<FamiliarUnityClass> SearchAllFamilies(Context context){
        ArrayList<FamiliarUnityClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i=0; i<registros.getCount(); i++){
            FamiliarUnityClass familyaux = new FamiliarUnityClass(context);
            for (int j=0; j<registros.getColumnCount();j++) {
                if (registros.getString(j) != null) {
                    if (registros.getString(j).length()!=0) {
                        familyaux.Data.put(registros.getColumnName(j),
                                registros.getString(j));
                    }
                }
            }
            familyaux.LoadDataHashToParameters();

            values.add(familyaux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public boolean ExistPerson(PersonClass person){
        boolean values = false;

        if (person.oldName.length()==0){person.oldName = person.Nombre;}
        if (person.oldSurname.length()==0){person.oldSurname = person.Apellido;}
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE LATITUD='"+person.Latitud+"' " +
                "AND LONGITUD='"+person.Longitud+"' AND FECHA='"+person.Fecha+"' " +
                "AND NOMBRE='"+person.oldName+"' AND APELLIDO='"+person.oldSurname+"'";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            values = true;
        }
        db.close();

        return values;
    }

    public boolean ExistPersonFiliatories(PersonClass person){
        boolean values = false;

        if (person.oldName.length()==0){person.oldName = person.Nombre;}
        if (person.oldSurname.length()==0){person.oldSurname = person.Apellido;}
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE LATITUD='"+person.Latitud+"' " +
                "AND LONGITUD='"+person.Longitud+"' AND FECHA='"+person.Fecha+"' " +
                "AND NOMBRE='"+person.oldName+"' AND APELLIDO='"+person.oldSurname+"'";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            values = true;
        }
        db.close();

        return values;
    }

    public boolean ExistRegisterPerson(String dni, String fecha, String latitud, String longitud){
        boolean values = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE LATITUD='"+latitud+"' " +
                "AND LONGITUD='"+longitud+"' AND FECHA='"+fecha+"' " +
                "AND DNI='"+dni+"'";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            values = true;
        }
        db.close();

        return values;
    }

    public boolean ExistFamily(FamiliarUnityClass family){
        boolean values = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES WHERE LATITUD='"+family.Latitud+"' " +
                "AND LONGITUD='"+family.Longitud+"' AND FECHA='"+family.Fecha+"' ";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            values = true;
        }
        db.close();

        return values;
    }

    public boolean ExistRegisterFamily(String longitud, String latitud, String fecha){
        boolean values = false;

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM FAMILIES WHERE LATITUD='"+latitud+"' " +
                "AND LONGITUD='"+longitud+"' AND FECHA='"+fecha+"' ";
        Cursor registros = db.rawQuery(search, null);

        if(registros.getCount()!=0){
            values = true;
        }
        db.close();

        return values;
    }

    public void UpdatePerson(PersonClass person){
        person.LoadData();
        CreateColumnsPerson(person);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        Object[] columnsNamePersonToInsert = person.Data.keySet().toArray();
        for (int i=0; i<columnsNamePersonToInsert.length; i++) {
            if (!columnsNamePersonToInsert[i].toString().contains("REDO")) {
                valores.put(columnsNamePersonToInsert[i].toString(),
                        person.Data.get(columnsNamePersonToInsert[i].toString()));
            }
        }

        String[] args = new String[]{person.Latitud, person.Longitud, person.oldName, person.oldSurname, person.Fecha};
        db.update("PERSONS", valores, "LATITUD=? AND LONGITUD=? AND NOMBRE=? AND APELLIDO=? AND FECHA=?", args);
        db.close();
    }

    public int UpdateFamily(FamiliarUnityClass family){
        //family.LoadData();
        CreateColumnsFamily(family);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        Object[] columnsNameFamiliaToInsert = family.Data.keySet().toArray();


        for (Object o : columnsNameFamiliaToInsert) {
            String key = o.toString().replace(" ","_");
            //Log.e(key + " update family data", (String) family.Data.get(key)+" values");
            if (!(!o.toString().contains("REDO") && o.toString().contains("RE"))) {
                valores.put(key, family.Data.get(key));
            }
        }

        String[] args = new String[]{family.Data.get("LATITUD"), family.Data.get("LONGITUD"), family.Data.get("FECHA")};

        int x = db.update("FAMILIES", valores, "LATITUD=? AND LONGITUD=? AND FECHA=?", args);

        db.close();
        return x;
    }

    public PersonClass SearchPersonsCoordinatesDateNameLastname(Context context, String latitude,
                                                                String longitude, String date,
                                                                String name, String lastname){

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT DISTINCT * FROM PERSONS WHERE LATITUD='"+latitude+
                "' AND LONGITUD='"+longitude+"' AND FECHA_NACIMIENTO='"+date+"' " +
                "AND NOMBRE='"+name+"' AND APELLIDO='"+lastname+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        PersonClass aux = new PersonClass(context);
        if(registros != null && registros.moveToFirst()){
            for (int j = 0; j < registros.getColumnCount(); j++) {
                    aux.Data.put(registros.getColumnName(j),
                            registros.getString(j));
                    //Log.e("busqueda" + registros.getColumnName(j),
                    //        registros.getString(j));
            }
        }
        aux.LoadDataHashToParamaters();

        db.close();

        return aux;
    }

    public PersonClass SearchPersonsCoordinatesDateregisterNameLastname(Context context, String latitude,
                                                                String longitude, String date,
                                                                String name, String lastname){

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT DISTINCT * FROM PERSONS WHERE LATITUD='"+latitude+
                "' AND LONGITUD='"+longitude+"' AND FECHA='"+date+"' " +
                "AND NOMBRE='"+name+"' AND APELLIDO='"+lastname+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        PersonClass aux = new PersonClass(context);
        if(registros != null && registros.moveToFirst()){
            for (int j = 0; j < registros.getColumnCount(); j++) {
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
                //Log.e("busqueda" + registros.getColumnName(j),
                //        registros.getString(j));
            }
        }
        aux.LoadDataHashToParamaters();

        db.close();

        return aux;
    }

    public ArrayList<String> Dates(){
        ArrayList<String> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT FECHA FROM FAMILIES";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for(int j=0; j<registros.getCount();j++){
            if(!values.contains(registros.getString(0).replace(".csv",""))){
                values.add(registros.getString(0).replace(".csv",""));
            }
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public ArrayList<String> DatesFamily(String latitud, String longitud){
        ArrayList<String> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT FECHA FROM FAMILIES WHERE LATITUD='"+latitud+"' AND LONGITUD='"+longitud+"'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for(int j=0; j<registros.getCount();j++){
            if(!values.contains(registros.getString(0).replace(".csv",""))){
                values.add(registros.getString(0).replace(".csv",""));
            }
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    public String[] cabeceras(){
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS";
        Cursor registros = db.rawQuery(search, null);
        String[] x = registros.getColumnNames();
        return x;
    }

//--------------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------------
    // BUSCO LAS PERSONAS QUE SE REALIZARON TEST DE HPV
    public ArrayList<PersonClass> SearchAllPersonsHPV(Context context) {
        ArrayList<PersonClass> values = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM PERSONS WHERE "+context.getString(R.string.realizo_test_hpv)+"='SI'";
        Cursor registros = db.rawQuery(search, null);

        registros.moveToFirst();
        for (int i = 0; i < registros.getCount(); i++) {
            PersonClass aux = new PersonClass(context);
            for (int j = 0; j < registros.getColumnCount(); j++) {
                aux.Data.put(registros.getColumnName(j),
                        registros.getString(j));
            }
            values.add(aux);
            registros.moveToNext();
        }
        db.close();

        return values;
    }

    // Cache memoria
    private void CreateColumnsCache(HashMap<String, String> caberas){
        Object[] columnsNameToInsert = caberas.keySet().toArray();

        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM CACHE";
        @SuppressLint("Recycle") Cursor registros = db.rawQuery(search, null);
        List<String> columnNames = Arrays.asList(registros.getColumnNames());
        db.close();

        for (Object o : columnsNameToInsert) {

            String toInsert = o.toString().toUpperCase().
                    replace(".", "").replace("/", "");
            if (!columnNames.contains(toInsert)) {
                db = this.getWritableDatabase();
                String addColumn = "ALTER TABLE CACHE ADD COLUMN " +
                         toInsert + " TEXT";
                db.execSQL(addColumn);
                db.close();
            }
            //db.close();
        }
    }

    public long insertDataCache(HashMap<String, String> data){
        CreateColumnsCache(data);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues valores = new ContentValues();
        Object[] columnsNameToInsert = data.keySet().toArray();
        for (int i=0; i<columnsNameToInsert.length; i++) {
            valores.put(columnsNameToInsert[i].toString().toUpperCase().
                            replace(".","").replace("?","").
                            replace("¿","").replace("/",""),
                    data.get(columnsNameToInsert[i].toString()));
        }
        long value = db.insert("CACHE", null, valores);
        db.close();
        return value;
    }

    public HashMap<String, String> SearchDataCache() {
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM CACHE";
        Cursor registros = db.rawQuery(search, null);

        HashMap<String, String> data = new HashMap<>();
        if (registros.getCount() != 0) {
            for(int i=0; i<registros.getCount(); i++) {
                registros.moveToPosition(i);
                for (int j = 0; j < registros.getColumnCount(); j++) {
                    data.put(registros.getColumnName(j),
                            registros.getString(j));
                }
            }
        }
        db.close();

        return data;
    }

    public HashMap<String, String> SearchDataCacheUD() {
        SQLiteDatabase db = this.getReadableDatabase();
        String search = "SELECT * FROM CACHEUD";
        Cursor registros = db.rawQuery(search, null);

        HashMap<String, String> data = new HashMap<>();
        if (registros.getCount() != 0) {
            for(int i=0; i<registros.getCount(); i++) {
                registros.moveToPosition(i);
                for (int j = 0; j < registros.getColumnCount(); j++) {
                    data.put(registros.getString(0),
                            registros.getString(1));
                }
            }
        }
        db.close();

        return data;
    }

    public void deleteCache(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM CACHE");
        //db.execSQL("DROP TABLE CACHE");
        //db.execSQL(CREATE_CACHE);
    }
}*/
