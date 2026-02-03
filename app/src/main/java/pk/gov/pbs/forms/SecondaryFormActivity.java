package pk.gov.pbs.forms;

import androidx.lifecycle.ViewModelProvider;

import pk.gov.pbs.formbuilder.core.ActivitySectionHousehold;
import pk.gov.pbs.formbuilder.core.IErrorStatementProvider;
import pk.gov.pbs.formbuilder.core.IMetaManifest;
import pk.gov.pbs.formbuilder.core.LabelProvider;
import pk.gov.pbs.formbuilder.core.QuestionnaireManager;
import pk.gov.pbs.formbuilder.core.QuestionnaireMap;
import pk.gov.pbs.formbuilder.core.ViewModelSection;
import pk.gov.pbs.formbuilder.exceptions.InvalidQuestionStateException;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.forms.maps.SecondaryFormMap;
import pk.gov.pbs.forms.models.SecondaryFormModel;
import pk.gov.pbs.tds.DefaultQuestionnaireManager;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.forms.meta.ErrorStatements;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.utils.ExceptionReporter;

public class SecondaryFormActivity extends ActivitySectionHousehold {
    @Override
    protected IErrorStatementProvider constructErrorStatementProvider() {
        return ErrorStatements.getInstance();
    }

    @Override
    protected IMetaManifest constructMetaManifest() {
        return MetaManifest.getInstance();
    }

    @Override
    protected QuestionnaireManager<?> constructQuestionnaireManager() {
        return new DefaultQuestionnaireManager<>(this, SecondaryFormModel.class);
    }

    @Override
    protected QuestionnaireMap constructMap() {
        return new SecondaryFormMap();
    }

    @Override
    protected ViewModelSection constructViewModel() {
        return new ViewModelProvider(this).get(DefaultViewModel.class);
    }

    @Override
    protected LabelProvider constructLabelProvider() {
        return new LabelProvider(this, "sf", LabelProvider.USA_LOCALE);
    }

    @Override
    protected void specifyLabelPlaceholders() {
    }

    @Override
    public boolean extractStoreSectionModel(int sectionStatus) throws InvalidQuestionStateException {
        if (!mNavigationToolkit.verifyQuestionsStatuses())
            throw new InvalidQuestionStateException();

        //if only one question exist which in not locked (i,e has no answer)
        //then ignore save request as success and proceed normally
        int qCount = mNavigationToolkit.getQuestionsOnlyCount();
        if (qCount == 0 || (!mQuestionnaireManager.isSectionEnded() && qCount == 1))
            return true;

        PrimaryModel model = mQuestionnaireManager.exportPrimaryModel();
        model.entryStatus = sectionStatus;
        model.setupDataIntegrity();

        long insertID = mViewModel.insertSection(model);
        if (insertID != Constants.INVALID_NUMBER) {
            if (mViewModel.persistFormContext() == Constants.INVALID_NUMBER)
                mUXToolkit.toast("Failed to persist form context!");
        }
        return insertID != Constants.INVALID_NUMBER;
    }

    @Override
    public boolean updateStoreSectionModel(int section_status) throws InvalidQuestionStateException {
        if (!mNavigationToolkit.verifyQuestionsStatuses())
            throw new InvalidQuestionStateException();

        //if only one question exist which in not locked (i,e has no answer)
        //then ignore save request as success and proceed normally
        int qCount = mNavigationToolkit.getQuestionsOnlyCount();
        if (qCount == 0 || (!mQuestionnaireManager.isSectionEnded() && qCount == 1))
            return true;

        PrimaryModel model = mQuestionnaireManager.updatePrimaryModel();
        model.entryStatus = section_status;
        model.setupDataIntegrity();
        int result;
        if (!mViewModel.getResumeModel().isSame(model)) {
            try {
                result = mViewModel.updateSection(model);
            } catch (IllegalAccessException e) {
                ExceptionReporter.handle(e);
                return false;
            }

        } else
            result = 1;

        if (result > 0) {
            mViewModel.setResumeModel(null);
            if(mViewModel.persistFormContext() == Constants.INVALID_NUMBER)
                mUXToolkit.toast("Failed to persist form context!");
        }

        return result != Constants.INVALID_NUMBER;
    }
    @Override
    protected String getApplicationVersion() {
        return CustomApplication.getApplicationVersion();
    }
}
