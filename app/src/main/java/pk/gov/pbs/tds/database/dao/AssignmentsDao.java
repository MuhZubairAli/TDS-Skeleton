package pk.gov.pbs.tds.database.dao;

import java.util.List;

import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.tds.database.FormRepository;

public class AssignmentsDao {
    FormRepository mRepository;
//    String table = Establishment.class.getSimpleName();

    public AssignmentsDao(FormRepository mRepository) {
        this.mRepository = mRepository;
    }
//
//    public Establishment getAssignment(FormContext fContext){
//        return mRepository.getDatabase().querySingleRawSql(
//                Establishment.class,
//                "SELECT * FROM " + table + " WHERE `PCode`=? AND `id`=?",
//                fContext.getPrimaryIdentifier(), String.valueOf(fContext.getSecondaryIdentifier()));
//    }
//
//    public Establishment getAssignment(String ebcode, Integer srNo){
//        return mRepository.getDatabase().querySingleRawSql(
//                Establishment.class,
//                "SELECT * FROM " + table + " WHERE `EBCode`=? AND `id`=?",
//                ebcode, String.valueOf(srNo));
//    }
//
//    public Establishment getAssignment(String ebcode){
//        return mRepository.getDatabase().querySingleRawSql(
//                Establishment.class,
//                "SELECT * FROM " + table + " WHERE `EBCode`=? LIMIT 1",
//                ebcode);
//    }
//
//    public Long getAssignmentsCount(){
//        return mRepository.getDatabase().getCount(Establishment.class, "`status`=?", "1");
//    }
//
//    public List<Establishment> getAllAssignments(){
//        return mRepository.getDatabase().query(Establishment.class);
//    }
//
//    public List<Establishment> getActiveAssignments(){
//        return mRepository.getDatabase().query(Establishment.class, "`status`=?","1");
//    }

}
