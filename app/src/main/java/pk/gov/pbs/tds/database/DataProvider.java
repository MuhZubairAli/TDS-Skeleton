package pk.gov.pbs.tds.database;

import android.database.Cursor;

import com.google.gson.annotations.SerializedName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pk.gov.pbs.database.DatabaseUtils;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.Option;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.utils.ExceptionReporter;

public class DataProvider {
    public static final Class<?>[] sDataModels = new Class[]{

    };

    public static List<Option> getNewOptions(FormBuilderRepository fbr){
        List<Option> opts = fbr.getOptionsDao().getUnsyncedOptions();
        if (opts != null && !opts.isEmpty()) {
            return opts;
        }
        return null;
    }

    public static String getQueries(){
        StringBuilder sb = new StringBuilder();
        for (Class<?> model : sDataModels){
            sb.append("SELECT \n")
                    .append(String.join(", ", DataProvider.getCols(model)))
                    .append("\n FROM ")
                    .append(model.getSimpleName())
                    .append(" WHERE ID > 0").append(";\n\n\n");
        }
        return sb.toString();
    }

    public static String[] getCols (Class<?> m){
        Field[] fields = DatabaseUtils.getAllFields(m, true);
        String[] cols = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            SerializedName sn = fields[i].getAnnotation(SerializedName.class);
            String name = sn != null ? sn.value() : fields[i].getName();
            String n = name.contains("Q") ? "c" + name.substring(name.indexOf("Q")+1) : name;
            if (name.startsWith("__"))
                n = "__"+n;
            cols[i] = name + " as [" + n + "]\n";
        }
        return cols;
    }

//    public static List<Map<String,List<?>>> getUnsyncedClosedForms(FormRepository mFormRepo){
//        try (Cursor c = mFormRepo.getDatabase().getReadableDatabase()
//                .rawQuery("SELECT srNo||'@'||PCode FROM " + InformationModel.class.getSimpleName() + " WHERE `sid` is null AND `formStatus` is not null AND `entryStatus`=?", new String[]{String.valueOf(Constants.Status.ENTRY_COMPLETED)})) {
//            List<String> list = new ArrayList<>();
//            while (c.moveToNext())
//                list.add(c.getString(0));
//
//            if (!list.isEmpty() && CustomApplication.getLoginPayload() != null) {
//                List<Map<String, List<?>>> data = new ArrayList<>();
//                for (String hhId : list) {
//                    String[] hh = hhId.split("@");
//                    Integer hhNo = Integer.parseInt(hh[0]);
//                    String pcode = hh[1];
//                    data.add(getFarmData(mFormRepo, pcode, hhNo));
//                }
//                return data;
//            }
//
//        } catch (Exception e) {
//            ExceptionReporter.handle(e);
//        }
//        return null;
//    }
//
//    public static String[] getSyncableCompletedForms(FormRepository mFormRepo){
//        try (Cursor c = mFormRepo.getDatabase().getReadableDatabase()
//                .rawQuery("SELECT srNo||'@'||PCode FROM " + InformationModel.class.getSimpleName() + " WHERE sid is null AND formStatus=?", new String[]{"1"})) {
//            List<String> list = new ArrayList<>();
//            while (c.moveToNext())
//                list.add(c.getString(0));
//            return list.toArray(new String[0]);
//        }
//    }

    public static Map<String, List<?>> getFarmData(FormRepository repo, String pcode, Integer SNo) {
        Map<String, List<?>> data = new HashMap<>();
        for (Class<?> modelClass : sDataModels){
            data.put(modelClass.getSimpleName(), getDataFor(repo, modelClass, pcode, SNo));
        }
        return data;
    }

    private static List<?> getDataFor(FormRepository mFormRepo, Class<?> modelClass, String pcode, Integer SNo) {
        return mFormRepo.getDatabase().query(
                modelClass, "PCode = ? AND srNo = ?", pcode, String.valueOf(SNo)
        );
    }

}
