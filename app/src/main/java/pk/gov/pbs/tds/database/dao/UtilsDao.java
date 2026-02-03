package pk.gov.pbs.tds.database.dao;

import java.util.List;

import pk.gov.pbs.database.DatabaseUtils;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.formbuilder.pojos.FormStatus;
import pk.gov.pbs.tds.database.FormRepository;

public class UtilsDao {
    FormRepository mRepository;

    public UtilsDao(FormRepository mRepository) {
        this.mRepository = mRepository;
    }

    //@Query("SELECT DISTINCT 'EB-'||EBCode FROM assignments")
//    public List<String> getBlockCodes() {
//        return DatabaseUtils.getFutureValue(mRepository.selectColMultiAs(
//                String.class,
//                "SELECT DISTINCT 'EB-'||EBCode FROM " + Establishment.class.getSimpleName()
//        ));
//    }
//
//    public List<String> getActiveBlockCodes() {
//        return DatabaseUtils.getFutureValue(mRepository.selectColMultiAs(
//                String.class,
//                "SELECT DISTINCT 'EB-'||EBCode FROM " + Establishment.class.getSimpleName() + " WHERE status=?",
//                String.valueOf(1)));
//    }
//
//    //@Query(`SELECT DISTINCT EBCode FROM assignments`)
//    public List<String> getSimpleBlockCodes(){
//        return DatabaseUtils.getFutureValue(mRepository.selectColMultiAs(
//                String.class,
//                "SELECT DISTINCT EBCode FROM "+Establishment.class.getSimpleName()
//        ));
//    }

//    public FormStatus getFormStatus(String PCode, Integer HHNo){
//        return mRepository.getDatabase().querySingleRawSql(
//                FormStatus.class,
//                "SELECT a.aid as `aid`, a.sid as `sid`, a.entryStatus as `initialEntryStatus`, b.entryStatus as `finalEntryStatus`, a.pcode as `pi`, a.srno as `si`, b.formStatus as `formStatus` FROM " + S1Model.class.getSimpleName() +
//                        " a LEFT JOIN "+ InformationModel.class.getSimpleName()+" b ON a.pcode=b.pcode AND a.srno=b.srno WHERE a.pcode=? AND a.srno=? AND a.status=?",
//                PCode, String.valueOf(HHNo), String.valueOf(1));
//    }

    //------------------- Copied from FormBuilder UitlsDao ---------------------
    //@Query("SELECT * FROM fcs WHERE mPCode=:pcode and mHHNo=:hhno")
    public FormContext getFCS(FormContext fContext){
        return mRepository.getDatabase().querySingleRawSql(
                FormContext.class, "SELECT * FROM "+FormContext.class.getSimpleName()+" WHERE `pi`=? and `si`=? ORDER BY `aid` DESC LIMIT 1",
                fContext.getPrimaryIdentifier(), String.valueOf(fContext.getSecondaryIdentifier())
        );
    }

    //@Query("SELECT * FROM fcs WHERE mPCode=:pcode and mHHNo=:hhno")
    public FormContext getFCS(String pi, int si){
        return mRepository.getDatabase().querySingleRawSql(
                FormContext.class, "SELECT * FROM "+FormContext.class.getSimpleName()+" WHERE `pi`=? and `si`=?",
                pi, String.valueOf(si)
        );
    }

    public Long setFormContext(FormContext fContext){
        return mRepository.getDatabase().replaceOrThrow(fContext);
    }

    //@Query("SELECT * FROM fcs")
    public List<FormContext> getAllFCs(){
        return mRepository.getDatabase().query(
                FormContext.class
        );
    }
//
//    public int determineBlockStatus(String pcode){
//        List<InformationModel> closedForms = mRepository
//                .getDatabase()
//                .query(
//                        InformationModel.class, "`pcode`=? AND `status`=?",
//                        pcode, String.valueOf(1)
//                );
//
//        long startedForms = mRepository.getDatabase().getCount(S1Model.class, "PCode=? AND status=?", pcode, "1");
//
//        long activeAssignmentCount = mRepository.getDatabase().getCount(Establishment.class, "PCode=? AND status=?", pcode, "1");
//        if(closedForms != null && !closedForms.isEmpty()) {
//            if (closedForms.size() == activeAssignmentCount) {
//                int syncedCount, unSyncCount, closedCount;
//                syncedCount = unSyncCount = closedCount = 0;
//                for (InformationModel s0 : closedForms){
//                    if(s0.sid != null && s0.sid > 0)
//                        syncedCount++;
//                    else
//                        unSyncCount++;
//
//                    if (s0.formStatus != null)
//                        closedCount++;
//                }
//
//                if(unSyncCount == 0)
//                    return Constants.Status.BLOCK_SYNCED;
//                else if(syncedCount > 0 && unSyncCount > 0)
//                    return Constants.Status.BLOCK_PARTIALLY_SYNCED;
//                else if(closedCount == activeAssignmentCount)
//                    return Constants.Status.BLOCK_COMPLETED;
//                else if(startedForms > 0 || closedCount > 0)
//                    return Constants.Status.BLOCK_INCOMPLETE;
//
//            } else
//                return Constants.Status.BLOCK_INCOMPLETE;
//        }
//
//        if (startedForms == 0)
//            return Constants.Status.BLOCK_NOT_STARTED;
//        else
//            return Constants.Status.BLOCK_INCOMPLETE;
//    }
}
