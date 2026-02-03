package pk.gov.pbs.tds.database;

import android.app.Application;

import java.util.List;
import java.util.concurrent.Future;

import pk.gov.pbs.database.ModelBasedRepository;
import pk.gov.pbs.formbuilder.database.FormDatabase;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.formbuilder.models.Table;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.database.dao.AssignmentsDao;
import pk.gov.pbs.tds.database.dao.UtilsDao;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.utils.SystemUtils;

public class FormRepository extends ModelBasedRepository {
    private static FormRepository sInstance;
    private static FormDatabase mFormDatabase;
    private final UtilsDao mUtilsDao;
    private final AssignmentsDao mAssignmentsDao;

    private FormRepository(Application context, LoginPayload payload){
        super(context);
        mFormDatabase = FormDatabase.getInstance(context.getApplicationContext(), payload, MetaManifest.getInstance());
        mUtilsDao = new UtilsDao(this);
        mAssignmentsDao = new AssignmentsDao(this);
    }

    public static FormRepository getInstance(Application context){
        if (sInstance == null)
            sInstance = new FormRepository(context, CustomApplication.getLoginPayload());

        if (!sInstance.getDatabase().getDatabaseName().equalsIgnoreCase(
                FormDatabase.deriveDatabaseName(CustomApplication.getLoginPayload(), MetaManifest.getInstance())
        )) {
            sInstance.getDatabase().close();
            sInstance = new FormRepository(context, CustomApplication.getLoginPayload());
        }

        return sInstance;
    }

    public FormDatabase getDatabase(){
        return mFormDatabase;
    }

    public UtilsDao getUtilsDao(){
        return mUtilsDao;
    }

    public AssignmentsDao getAssignmentDao(){
        return mAssignmentsDao;
    }

    @Override
    public Future<Long> replace(Object object) {
        if (object instanceof Table)
            ((Table) object).tsUpdated = SystemUtils.getUnixTs();
        return super.replace(object);
    }

    @Override
    public Future<Long> replaceOrThrow(Object object) {
        if (object instanceof Table)
            ((Table) object).tsUpdated = SystemUtils.getUnixTs();
        return super.replaceOrThrow(object);
    }

    @Override
    public Future<List<Long>> replace(Object[] object) {
        if (object[0] instanceof Table) {
            for (Object o : object) {
                ((Table) o).tsUpdated = SystemUtils.getUnixTs();
            }
        }
        return super.replace(object);
    }

    @Override
    public Future<List<Long>> replaceOrThrow(Object[] object) {
        if (object[0] instanceof Table) {
            for (Object o : object) {
                ((Table) o).tsUpdated = SystemUtils.getUnixTs();
            }
        }
        return super.replaceOrThrow(object);
    }
}
