package pk.gov.pbs.forms;

import android.content.DialogInterface;

import androidx.lifecycle.ViewModelProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import pk.gov.pbs.formbuilder.core.ActivitySectionHousehold;
import pk.gov.pbs.formbuilder.core.IErrorStatementProvider;
import pk.gov.pbs.formbuilder.core.IMetaManifest;
import pk.gov.pbs.formbuilder.core.JsonBasedQuestionnaireMap;
import pk.gov.pbs.formbuilder.core.LabelProvider;
import pk.gov.pbs.formbuilder.core.QuestionnaireManager;
import pk.gov.pbs.formbuilder.core.QuestionnaireMap;
import pk.gov.pbs.formbuilder.core.ViewModelSection;
import pk.gov.pbs.formbuilder.exceptions.InvalidQuestionStateException;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.formbuilder.utils.ValueStore;
import pk.gov.pbs.forms.meta.ErrorStatements;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.forms.models.PrimaryFormModel;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.DefaultQuestionnaireManager;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.UXEvent;

public class JsonBasedForm2Activity extends ActivitySectionHousehold {

    protected QuestionnaireMap constructMap() {
        StringBuilder sb = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("sample_json_form.json"), StandardCharsets.UTF_8));

            String mLine;
            while ((mLine = reader.readLine()) != null) {
                sb.append(mLine);
            }
            return new JsonBasedQuestionnaireMap(this, sb.toString());
        } catch (IOException e) {
            ExceptionReporter.handle(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    ExceptionReporter.handle(e);
                }
            }
        }
        throw new RuntimeException("Unable to load form from json");
    }

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
        return new DefaultQuestionnaireManager<>(this, PrimaryFormModel.class);
    }

    @Override
    protected ViewModelSection constructViewModel() {
        return new ViewModelProvider(this).get(DefaultViewModel.class);
    }

    @Override
    protected LabelProvider constructLabelProvider() {
        return new LabelProvider() {
            @Override
            protected void en() {}
        };
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

        HashMap<String, ValueStore[]> responses = mQuestionnaireManager.exportAnswersAsMap();
        getUXToolkit().confirm(
                "Form Responses",
                StaticUtils.getSimpleGson(true,false).toJson(responses).replace("\n","<br />"),
                "Exit",
                "Go Back",
                new UXEvent.ConfirmDialogue() {
                    @Override
                    public void onCancel(DialogInterface dialog, int which) {
                    }

                    @Override
                    public void onOK(DialogInterface dialog, int which) {
                        JsonBasedForm2Activity.this.finish();
                    }
                }
        );
        return false;
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
