package pk.gov.pbs.tds_example.activities;

import android.content.DialogInterface;

import androidx.lifecycle.ViewModelProvider;

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
import pk.gov.pbs.formbuilder.pojos.JsonQuestion;
import pk.gov.pbs.formbuilder.utils.ValueStore;
import pk.gov.pbs.tds.DefaultViewModel;
import pk.gov.pbs.tds_example.meta.ErrorStatements;
import pk.gov.pbs.tds_example.meta.MetaManifest;
import pk.gov.pbs.tds_example.models.PrimaryFormModel;
import pk.gov.pbs.tds.CustomApplicationBase;
import pk.gov.pbs.tds.DefaultQuestionnaireManager;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.UXEvent;

public class JsonBasedFormActivity extends ActivitySectionHousehold {
    @Override
    protected QuestionnaireMap constructMap() {
        JsonQuestion[] questions = {
                new JsonQuestion(
                        new String[]{"name"},
                        "Enter your name",
                        "Max length 32 characters",
                        "KBI",
                        null,
                        new HashMap<>() {{
                            put("InputType", "TYPE_CLASS_TEXT");
                        }},
                        new HashMap<>() {{
                            put("Validator", "length:32");
                        }}
                ),

                new JsonQuestion(
                        new String[]{"gender"},
                        "Specify your gender",
                        "Select one option",
                        "RBI",
                        new String[]{
                                "Male",
                                "Female",
                                "Transgender",
                                "Other"
                        },
                        new HashMap<>() {{
                            put("ColumnCount", "DOUBLE");
                        }},
                        new HashMap<>() {{
                            put("Validator", "required");
                        }}
                ),

                new JsonQuestion(
                        new String[]{"class"},
                        "In which class do you read?",
                        "Select appropriate option from list",
                        "SPI",
                        new String[]{
                                "Nursery",
                                "Kindergarten",
                                "1st Class",
                                "2nd Class",
                                "3rd Class",
                                "4th Class",
                                "Primary",
                                "6th Class",
                                "7th Class",
                                "Middle",
                                "9th Class",
                                "Metric",
                        },
                        null,
                        new HashMap<>() {{
                            put("Validator", "greaterThan:3");
                        }}
                ),

                new JsonQuestion(
                        new String[]{"subject"},
                        "What are your favorite subject?",
                        "Multiple selection allowed",
                        "CBI",
                        new String[]{
                                "English", "Computer Science",
                                "Mathematics", "Physics",
                                "Chemistry", "Biology",
                                "History", "Geography",
                                "Islamiyat", "Social Studies"
                        },
                        new HashMap<>() {{
                            put("ColumnCount", "DOUBLE");
                        }},
                        new HashMap<>() {{
                            put("Validator", "required");
                        }}
                ),

                new JsonQuestion(
                        new String[]{"country"},
                        "Specify the country where you live",
                        "Either enter code or search by text",
                        "Annex",
                        null,
                        new HashMap<>(){{
                            put("DatumIdentifier", "{'key':'country'}");
                        }},
                        new HashMap<>(){{
                            put("Validator", "required");
                        }}
                ),

                new JsonQuestion(
                        new String[]{"hobbies"},
                        "Enter your hobbies",
                        "Type of select from suggestion",
                        "ACKBI",
                        null,
                        new HashMap<>(){{
                            put("DatumIdentifier", "{'section':'test', 'column':'hobbies'}");
                        }},
                        new HashMap<>(){{
                            put("Validator", "required");
                        }}
                ),

                new JsonQuestion(
                        new String[]{"dob"},
                        "Enter your date of birth",
                        "Choose date using input calendar",
                        "DI",
                        null,
                        null,
                        new HashMap<>(){{
                            put("Validator", "required");
                        }}
                ),


                new JsonQuestion(
                        new String[]{"gi"},
                        "Example of Grouped Input",
                        "Example of multiple inputs in a group",
                        "GI",
                        null,
                        null,
                        new HashMap<>(){{
                            put("Validator", "required");
                        }},

                        new JsonQuestion(
                                new String[]{"gi_kbi"},
                                "Example of keyboard input in a group question",
                                "Max 16 characters",
                                "KBI",
                                null,
                                new HashMap<>(){{
                                    put("InputType", "TYPE_CLASS_TEXT");
                                }},
                                new HashMap<>(){{
                                    put("Validator", "length:16");
                                }}
                        ),

                        new JsonQuestion(
                                new String[]{"gi_rbi"},
                                "Example of radio input in a group question",
                                "Choose on option",
                                "RBI",
                                new String[]{"Option A", "Option B", "Option C", "Option D"},
                                new HashMap<>(){{
                                    put("ColumnCount", "DOUBLE");
                                }},
                                new HashMap<>(){{
                                    put("Validator", "required");
                                }}
                        ),

                        new JsonQuestion(
                                new String[]{"gi_cbi"},
                                "Example of check input in a group question",
                                "Choose multiple option",
                                "CBI",
                                new String[]{
                                        "Option A", "Option B", "Option C", "Option D"
                                },
                                new HashMap<>(){{
                                    put("ColumnCount", "DOUBLE");
                                }},
                                new HashMap<>(){{
                                    put("Validator", "required");
                                }}
                        ),

                        new JsonQuestion(
                                new String[]{"gi_spi"},
                                "Example of Spinner input in a group question",
                                "Choose one option",
                                "SPI",
                                new String[]{
                                        "Option A", "Option B", "Option C", "Option D"
                                },
                                null,
                                new HashMap<>(){{
                                    put("Validator", "greaterThan:2");
                                }}
                        )

                )
        };

        String formJson = StaticUtils.getGson().toJson(questions);
        return new JsonBasedQuestionnaireMap(this, formJson);
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
                        JsonBasedFormActivity.this.finish();
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
        return CustomApplicationBase.getApplicationVersion();
    }
}
