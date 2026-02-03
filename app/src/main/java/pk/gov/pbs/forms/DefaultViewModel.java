package pk.gov.pbs.forms;

import android.app.Application;

import androidx.annotation.NonNull;

import pk.gov.pbs.formbuilder.core.ViewModelSection;
import pk.gov.pbs.formbuilder.database.dao.HouseholdMemberDao;
import pk.gov.pbs.formbuilder.pojos.Assignment;
import pk.gov.pbs.forms.models.S1AModel;
import pk.gov.pbs.tds.database.FormRepository;

public class DefaultViewModel extends ViewModelSection {
    FormRepository mFormRepository;
    public DefaultViewModel(@NonNull Application application) {
        super(application);
        mFormRepository = FormRepository.getInstance(getApplication());
    }

    @Override
    public HouseholdMemberDao<?> getRosterDao() {
        return new HouseholdMemberDao<S1AModel>(mFormRepository, S1AModel.class);
    }

    @Override
    public Assignment getAssignment() {
//        if (getFormContext() != null){
//            return getFormRepository().getAssignmentDao().getAssignment(getFormContext());
//        }
        return null;
    }

    @Override
    public long persistFormContext() {
        return mFormRepository.getUtilsDao().setFormContext(mFormContext);
    }

    @Override
    public FormRepository getFormRepository(){
        return mFormRepository;
    }


}
