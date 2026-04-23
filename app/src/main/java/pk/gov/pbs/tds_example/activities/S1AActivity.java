package pk.gov.pbs.tds_example.activities;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ViewGroup;

import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import pk.gov.pbs.database.DatabaseUtils;
import pk.gov.pbs.database.IDatabaseOperation;
import pk.gov.pbs.database.ModelBasedDatabaseHelper;
import pk.gov.pbs.formbuilder.core.IErrorStatementProvider;
import pk.gov.pbs.formbuilder.core.IMetaManifest;
import pk.gov.pbs.formbuilder.pojos.SpinnerItemRoster;
import pk.gov.pbs.formbuilder.core.ViewModelSection;
import pk.gov.pbs.formbuilder.core.LabelProvider;
import pk.gov.pbs.formbuilder.core.QuestionnaireMap;
import pk.gov.pbs.formbuilder.core.ActivitySectionMember;
import pk.gov.pbs.formbuilder.core.QuestionActor;
import pk.gov.pbs.formbuilder.core.QuestionnaireManager;
import pk.gov.pbs.formbuilder.exceptions.InvalidQuestionStateException;
import pk.gov.pbs.formbuilder.inputs.singular.ButtonInput;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.RosterSection;
import pk.gov.pbs.tds_example.maps.S1AMap;
import pk.gov.pbs.tds_example.meta.ErrorStatements;
import pk.gov.pbs.tds_example.meta.MetaManifest;
import pk.gov.pbs.tds_example.models.S1AModel;
import pk.gov.pbs.tds_example.CustomApplication;
import pk.gov.pbs.tds.DefaultQuestionnaireManager;
import pk.gov.pbs.tds.DefaultViewModel;
import pk.gov.pbs.utils.ExceptionReporter;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.SystemUtils;
import pk.gov.pbs.utils.UXEvent;

public class S1AActivity extends ActivitySectionMember {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityTitle("Household Roster Section");

        for (RosterSection member : mViewModel.getHouseholdMembers()){
            if (member.status == 1)
                mViewModel.getHouseholdMembersFiltered().add(member);
        }

        if (mViewModel.getHouseholdMembers().isEmpty())
            startSectionImmediate();
    }

    @Override
    protected int getSectionNumberFromDataTabPosition(int position) {
        return position + 2;
    }

    @Override
    protected void onActionGoNext() {
        safeSaveOrUpdateModel(() -> {
            Intent intent = new Intent(S1AActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected void repeatSection() {
        super.repeatSection();
        resetSelectionSpinnerUsers();
    }

    @Override
    protected void setupSectionToolbox(ViewGroup containerTop) {
        ViewGroup toolbox = (ViewGroup) getLayoutInflater().inflate(pk.gov.pbs.formbuilder.R.layout.toolbox_form_roster_section_filter_spi_btn, containerTop);

        toolbox.findViewById(pk.gov.pbs.formbuilder.R.id.btn).setOnLongClickListener((view)->{
            if(mSpinnerMembers.getSelectedItemPosition() == 0)
                return false;

            RosterSection mhh = ((SpinnerItemRoster) mSpinnerMembers.getSelectedItem()).getModel();

            if(mhh != null) {
                if (mhh.getRelationCode() != null && mhh.getRelationCode() == 1 && getViewModel().getHouseholdMembersFiltered().size() > 1) {
                    mUXToolkit.alert("Action Denied!","Selected member '" + mhh.getName() + "' is head of the household, It could only be deleted if there are no other members in the household.");
                    return true;
                }

                mUXToolkit.confirm("Are you sure to delete '" + mhh.getName() + "' from member list",
                        new UXEvent.ConfirmDialogue() {
                            @Override
                            public void onCancel(DialogInterface dialog, int which) {
                            }

                            @Override
                            public void onOK(DialogInterface dialog, int which) {
                                mhh.status = 0;
                                getViewModel().getFormRepository().executeDatabaseOperation(new IDatabaseOperation<Boolean>() {
                                    @Override
                                    public Boolean execute(ModelBasedDatabaseHelper db) {
                                        SQLiteDatabase wdb = db.getWritableDatabase();
                                        try {
                                            List<String> args = new ArrayList<>();
                                            StringBuilder sb = new StringBuilder();

                                            sb.append("`pcode`=? AND `hhno`=?");
                                            args.add(mhh.getPrimaryIdentifier());
                                            args.add(String.valueOf(mhh.getSecondaryIdentifier()));

                                            sb.append(" AND `sno`=?");
                                            args.add(String.valueOf(mhh.getTertiaryIdentifier()));

                                            ContentValues cv = new ContentValues();
                                            cv.put("status", "0");

                                            for (Class<?> m : MetaManifest.getInstance().getModels()){
                                                if (SystemUtils.modelHasField(m, "sno")){
                                                    wdb.update(
                                                            m.getSimpleName(),
                                                            cv,
                                                            sb.toString(),
                                                            args.toArray(new String[0])
                                                    );
                                                }
                                            }
                                            return true;
                                        } catch (Exception e) {
                                            StaticUtils.getHandler().post(()->{
                                                mUXToolkit.alert("Can not delete Head of the Household twice");
                                            });
                                            ExceptionReporter.handle(e);
                                            return false;
                                        }
                                        finally {
                                            wdb.close();
                                        }
                                    }

                                    @Override
                                    public void postExecute(Boolean result) {
                                        if (result) {
                                            getViewModel().getHouseholdMembersFiltered().remove(mhh);
                                            mAdapterSpinnerMembers.remove((SpinnerItemRoster) mSpinnerMembers.getSelectedItem());
                                            mAdapterSpinnerMembers.notifyDataSetChanged();
                                            mViewModel.setCurrentMemberID(null);
                                            mViewModel.setResumeModel(null);
                                            mSpinnerMembers.setSelection(0);
                                            mUXToolkit.toast("Member deleted!");
                                            repeatSection();
                                        }
                                    }
                                });
                            }
                        });
            }
            return true;
        });

        mSpinnerMembers = toolbox.findViewById(pk.gov.pbs.formbuilder.R.id.spi);
        setupSpiMembers(mSpinnerMembers);
    }

    @Override
    protected QuestionnaireMap constructMap() {
        return new S1AMap(this);
    }

    @Override
    protected ViewModelSection constructViewModel() {
        return new ViewModelProvider(this).get(DefaultViewModel.class);
    }

    @Override
    protected LabelProvider constructLabelProvider() {
        return new LabelProvider(this, "1A", CustomApplication.getConfigurations().getLocale());
    }

    @Override
    protected void specifyLabelPlaceholders() {
        mQuestionnaireManager.addLabelPlaceholder("s1aq1", "name");
    }

    @Override
    protected QuestionnaireManager<S1AModel> constructQuestionnaireManager() {
        return new DefaultQuestionnaireManager<>(this, S1AModel.class);
    }

    @Override
    public boolean extractStoreSectionModel(int section_status) throws InvalidQuestionStateException {
        if (!mNavigationToolkit.verifyQuestionsStatuses())
            throw new InvalidQuestionStateException();

        //if only one question exist which in not locked (i,e has no answer)
        //then ignore save request as success and proceed normally
        int qCount = mNavigationToolkit.getQuestionsOnlyCount();
        if (qCount == 0  || (!mQuestionnaireManager.isSectionEnded() && qCount == 1))
            return true;

        S1AModel model = (S1AModel) mQuestionnaireManager.exportPrimaryModel();
        model.entryStatus = section_status;

        long insertID = mViewModel.insertSection(model);

        if (insertID != Constants.INVALID_NUMBER) {
            // because this method is not necessarily called when form is completed
            // so for incomplete query sno is stored in form context
            mViewModel.getFormContext().setTertiaryIdentifier(model.sno);
            if(mViewModel.persistFormContext() == Constants.INVALID_NUMBER)
                mUXToolkit.toast("Failed to persist form context!");
            mViewModel.setCurrentMemberID(null);
            mViewModel.getHouseholdMembersFiltered().add(model);
            refreshTopContainerSpinner();
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

        S1AModel model = (S1AModel) mQuestionnaireManager.updatePrimaryModel();
        model.entryStatus = section_status;

        if (mViewModel.getResumeModel().isSame(model)) {
            mViewModel.setResumeModel(null);
            return true;
        }

        Future<Integer> future = mViewModel.getFormRepository().update(model);
        Integer result = DatabaseUtils.getFutureValue(future);
        if (result > 0) {
            mViewModel.setResumeModel(null);
            for (int i=0; i<mViewModel.getSectionEntries().size(); i++){
                if (mViewModel.getSectionEntries().get(i).aid.longValue() == model.aid.longValue()){
                    mViewModel.getSectionEntries().remove(i);
                    mViewModel.getSectionEntries().add(i, model);
                    break;
                }
            }
            mViewModel.getFormContext().setTertiaryIdentifier(model.getTertiaryIdentifier());
            if(mViewModel.persistFormContext() == Constants.INVALID_NUMBER)
                mUXToolkit.toast("Failed to persist form context!");
            mViewModel.setCurrentMemberID(null);
            refreshTopContainerSpinner();
        }
        return result != Constants.INVALID_NUMBER;
    }

    @Override
    public QuestionActor getActionQuestion() {
        ButtonInput[] buttons = new ButtonInput[2];

        buttons[0] = new ButtonInput(
                Constants.Index.LABEL_BTN_REPEAT
                , (view) -> {
            safeSaveOrUpdateModel(this::repeatSection);
        }
        );

        buttons[1] = new ButtonInput(
                Constants.Index.LABEL_BTN_NEXT_SECTION, (view) -> {
            mUXToolkit.confirm(pk.gov.pbs.formbuilder.R.string.alert_goto_next_section_message,
                    new UXEvent.ConfirmDialogue() {
                        @Override
                        public void onCancel(DialogInterface dialog, int which) {
                        }

                        @Override
                        public void onOK(DialogInterface dialog, int which) {
                            safeSaveOrUpdateModel(S1AActivity.this::gotoNextSection);
                        }
                    }
            );
        }
        );

        return new QuestionActor(buttons);
    }

    @Override
    protected IErrorStatementProvider constructErrorStatementProvider() {
        return ErrorStatements.getInstance();
    }

    @Override
    protected IMetaManifest constructMetaManifest() {
        return MetaManifest.getInstance();
    }

    public String getApplicationVersion(){
        return CustomApplication.getApplicationVersion();
    }
}