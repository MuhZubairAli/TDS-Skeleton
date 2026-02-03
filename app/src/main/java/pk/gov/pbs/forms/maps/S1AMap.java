package pk.gov.pbs.forms.maps;

import android.text.InputType;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import pk.gov.pbs.formbuilder.core.ActivitySection;
import pk.gov.pbs.formbuilder.core.IQuestionnaireManager;
import pk.gov.pbs.formbuilder.core.QuestionnaireMap;
import pk.gov.pbs.formbuilder.inputs.singular.HouseholdMembersSpinnerInput;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.meta.ErrorStatementProvider;
import pk.gov.pbs.formbuilder.meta.QuestionStates;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.formbuilder.models.RosterSection;
import pk.gov.pbs.formbuilder.pojos.SpinnerItemMember;
import pk.gov.pbs.formbuilder.pojos.SpinnerItemRoster;
import pk.gov.pbs.formbuilder.utils.ValueStore;
import pk.gov.pbs.formbuilder.core.Question;
import pk.gov.pbs.formbuilder.inputs.abstracts.input.GroupInput;
import pk.gov.pbs.formbuilder.meta.ColumnCount;
import pk.gov.pbs.formbuilder.validator.BetweenValidator;
import pk.gov.pbs.formbuilder.validator.LimitLengthValidator;
import pk.gov.pbs.formbuilder.validator.RequiredValidator;
import pk.gov.pbs.forms.meta.ErrorStatements;
import pk.gov.pbs.forms.models.S1AModel;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.utils.DateTimeUtil;

public class S1AMap extends QuestionnaireMap {
    private final ActivitySection mContext;
    private final SpinnerItemMember.LabelMaker mLabelMaker;

    public S1AMap(ActivitySection context){
        mContext = context;
        mLabelMaker = context.getDefaultLabelMakerForItemSpinnerRoster();
    }

    @Override
    protected void initQuestions(QuestionnaireBuilder builder) throws Exception {
//        int enumGender = -1;
//        if (CustomApplication.getLoginPayload() != null)
//            enumGender = CustomApplication.getLoginPayload().gender;

        final ValueStore daysInMonth = new ValueStore(31);
        Question s4c1 = builder.makeKBI(
                "s1aq1"
                , InputType.TYPE_TEXT_FLAG_CAP_WORDS
                , new LimitLengthValidator(2, 32)
        ).setCritical();
        insertQuestion(s4c1);

        //-----------------------------------------
        Question s4c2 = builder.makeMultiColRBI(
                "s1aq2"
                , ColumnCount.DOUBLE
                , new RequiredValidator()
        ).setCritical();
        
        insertQuestion(s4c2).setConditions(new Question.Conditions() {
            @Override
            public String PreCondition(IQuestionnaireManager manager, Question self) {
                return null;
            }

            @Override
            public String PostCondition(IQuestionnaireManager manager, Question self) {
                int relation = (self.getAnswers() == null) ? 0 : self.getAnswers()[0][0].toInt();
                List<PrimaryModel> allMembers = manager.getViewModel().getSectionEntries();

                S1AModel hhh = null;
                S1AModel resumeModel = (S1AModel) manager.getViewModel().getResumeModel();

                for (PrimaryModel m : allMembers){
                    if (((RosterSection) m).getRelationCode() == 1) {
                        hhh = (S1AModel) m;
                        break;
                    }
                }

                boolean hhh_is_being_edited = resumeModel != null
                        && resumeModel.getRelationCode() != null
                        && resumeModel.getRelationCode() == 1;

                if (hhh_is_being_edited) {
                    if (relation != 1)
                        return ErrorStatements.hhh_relation_cannot_be_changed;
                }

                if(hhh != null && hhh.entryStatus == Constants.Status.ENTRY_COMPLETED) {
                    //======== Member has been added ============
                    if(relation == 1 && (!hhh_is_being_edited))
                        return ErrorStatements.head_already_added;

                    //s1aq6 = current marital status
                    if(hhh.getMaritalStatus() != 2 && relation == 2)
                        return ErrorStatements.head_not_married;

                    if(hhh.getMaritalStatus() == 1 && (relation == 3 || relation == 4 || relation == 8))
                        return ErrorStatements.cannot_have_children;

                    if (relation == 2) {
                        return ErrorStatementProvider.Make.newStatement("Head of the Household and spouse cannot be entered by same Enumerator");
                    }
                    if (relation == 2){
                        if(hhh.getGenderCode() == 2){
                            for (RosterSection hhm: manager.getViewModel().getHouseholdMembers()){
                                if(hhm.entryStatus == Constants.Status.ENTRY_COMPLETED && (resumeModel == null || resumeModel.aid.longValue() != hhm.aid.longValue())){
                                    if(hhm.getRelationCode() == 2){
                                        return ErrorStatements.female_cant_have_multiple_spouse;
                                    }
                                }
                            }
                        }
                    }
                }

                //Head could be in either male or female roster so can't enforce this check
//                if(manager.getViewModel().getLoginPayload().gender != 1 && (hhh == null || hhh.section_status == Constants.Status.SECTION_OPENED)){
//                    if (relation != 1)
//                        return ErrorStatements.first_member_must_be_hhh;
//                }

                return null;
            }
        });

        //-----------------------------------------
        Question q3 = builder.makeMultiColRBI(
                "s1aq3"
                , ColumnCount.DOUBLE
                , new RequiredValidator()
        ).setCritical();

        insertQuestion(q3).setConditions(new Question.Conditions() {
            @Override
            public String PreCondition(IQuestionnaireManager manager, Question self) {
                int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();

//                if (manager.getViewModel().getLoginPayload().gender == 1){
//                    self.loadAnswer(self.getIndex().concat("_1"), new ValueStore(1));
//                    self.setCritical();
//                    return null;
//                }

                if(relation == 2){
                    List<RosterSection> allMembers = manager.getViewModel().getHouseholdMembers();
                    if(allMembers.size() > 0) {
                        S1AModel hhh = null;

                        for (RosterSection m : allMembers){
                            if (m.getRelationCode() == 1) {
                                hhh = (S1AModel) m;
                                break;
                            }
                        }

                        if (hhh != null && hhh.getGenderCode() != null) {
                            if (hhh.getGenderCode() == 1) {
                                self.loadAnswer(self.getIndex().concat("_2"), new ValueStore(2));
                            } else {
                                self.loadAnswer(self.getIndex().concat("_1"), new ValueStore(1));
                            }
                        }
                    }
                }

                return null;
            }

            @Override
            public String PostCondition(IQuestionnaireManager manager, Question self) {
                int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();
                int gender = self.getAnswers()[0][0].toInt();

//                if (manager.getViewModel().getLoginPayload().gender == 1 && gender != 1)
//                    return ErrorStatements.getInstance().only_male_members;

                if(relation == 2){
                    List<RosterSection> allMembers = manager.getViewModel().getHouseholdMembers();
                    if(allMembers.size() > 0) {
                        S1AModel hhh = null;

                        for (RosterSection m : allMembers){
                            if (m.getRelationCode() == 1) {
                                hhh = (S1AModel) m;
                                break;
                            }
                        }

                        if (hhh != null && hhh.getGenderCode() != null) {
                            int hhhg = hhh.getGenderCode();

                            if ((hhhg == 1 && gender != 2) || (hhhg == 2 && gender != 1))
                                return ErrorStatements.getInstance().invalid_spouse_gender;
                        }
                    }
                }
                return null;
            }
        });


        //-----------------------------------------
        insertQuestion(builder.makeRBI(
                "s1aq4"
                , new RequiredValidator()
        ));


        //---------------------------------------
        Question q5 = builder.makeGI(
            "s1aq5",
            new GroupInput[]{
                    builder.prepareGI_KBI("s1aq51", InputType.TYPE_CLASS_NUMBER, new BetweenValidator(new ValueStore(0), new ValueStore(99))),
                    builder.prepareGI_KBI3x("s1aq52", InputType.TYPE_CLASS_NUMBER,
                            new BetweenValidator(new ValueStore(0), daysInMonth),
                            new BetweenValidator(new ValueStore(0), new ValueStore(12)),
                            new BetweenValidator(new ValueStore(DateTimeUtil.getCalendar().get(Calendar.YEAR)-99),
                                    new ValueStore(DateTimeUtil.getCalendar().get(Calendar.YEAR))
                            )
                    )
//                    builder.prepareGI_DI("s1aq52", new RequiredValidator())
            },
            (manager, askables) -> {
                int year = DateTimeUtil.getCalendar().get(Calendar.YEAR);
                int month = Math.min(DateTimeUtil.getCalendar().get(Calendar.MONTH) + 1, 12);
                int day = DateTimeUtil.getCalendar().get(Calendar.DAY_OF_MONTH);

                Integer ab1 = (askables[0].getAnswer(0) != null) ? askables[0].getAnswer(0).toInt() : null;
                ValueStore[] ab2 = askables[1].getAnswers();

                if (askables[0].hasFocus()){
                    int by = (ab1 != null) ? year - ab1 : 0;
                    if (ab2 != null && ab2[2] != null && ab2[2].toInt() == by)
                        return;

                    askables[1].setAnswers(ab2[0], ab2[1], new ValueStore(by));
                } else {
                    Integer by;
                    int age = -1;
                    if (ab2 == null || ab2[2] == null)
                        return;
                    by = ab2[2].tryCastToInt();
                    if (by == null)
                        return;

                    if (ab2[0] != null && ab2[1] != null){
                        if (((GroupInput) askables[1]).validateAnswer()) {
                            age = (int) DateTimeUtil.getDurationBetweenInYears(
                                    DateTimeUtil.getDateFrom(ab2[2].toInt(), ab2[1].toInt(), ab2[0].toInt()),
                                    DateTimeUtil.getDateFrom(year, month, day)
                            );
                        }
                    }

                    if (by < (DateTimeUtil.getCalendar().get(Calendar.YEAR)-99))
                        return;

                    if (age == -1)
                        age = year - by;

                    if (ab1 == null || age != ab1) {
                        askables[0].setAnswers(new ValueStore(age));
                    }
                }

                int birthYear = -1;
                if (ab2[2] != null)
                    birthYear =  ab2[2].toInt();

                int birthMonth = -1;
                if (ab2[1] != null)
                    birthMonth = ab2[1].toInt();

                if (birthMonth > 0 && birthYear > 0)
                    daysInMonth.setValue(DateTimeUtil.getDaysOfMonth(birthMonth, birthYear));
            }
        );

        insertQuestion(q5).setConditions(new Question.Conditions() {
            @Override
            public String PreCondition(IQuestionnaireManager manager, Question self) {
                return null;
            }

            @Override
            public String PostCondition(IQuestionnaireManager manager, Question self) {
                int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();
                int age = self.getAnswers()[0][0].toInt();
                ValueStore[] dob = self.getAnswers()[1];

                List<PrimaryModel> allMembers = manager.getViewModel().getSectionEntries();
                S1AModel hhh = null;
//
//                if (manager.getViewModel().getLoginPayload().gender == 1){
//                    if (age < 10)
//                        return ErrorStatements.male_enum_age_limit;
//                }
//
//                if (manager.getViewModel().getLoginPayload().gender == 2){
//                    if (q3.getAnswers()[0][0].toInt() == 1 && age > 9) {
//                        return ErrorStatements.female_enum_age_limit;
//                    }
//                }

                for (PrimaryModel m : allMembers){
                    if (((RosterSection) m).getRelationCode() == 1) {
                        hhh = (S1AModel) m;
                        break;
                    }
                }

                if(relation == 1 && age < pk.gov.pbs.tds.Constants.MIN_HHH_AGE){
                    return ErrorStatements.hhh_age_error;
                }

                if(relation == 2) {
                    if(age <= pk.gov.pbs.tds.Constants.MIN_MARRIAGEABLE_AGE)
                        return ErrorStatements.married_age_error;

                }

                if (hhh != null) {
                    if (relation == 3) {
                        if (!(hhh.getAge() > (age + pk.gov.pbs.tds.Constants.PARENT_CHILD_AGE_THRESHOLD)))
                            return ErrorStatements.parent_age_difference; // child must be [parent_age_threshold] years younger than parent
                    }

                    if (relation == 5 || relation == 10) {
                        if ((hhh.getAge() + pk.gov.pbs.tds.Constants.PARENT_CHILD_AGE_THRESHOLD) > age)
                            return ErrorStatements.parent_age_difference; // parent age must be greater
                    }

                    if (relation == 4) {
                        if (!(hhh.getAge() > (age + pk.gov.pbs.tds.Constants.PARENT_GRANDCHILD_AGE_THRESHOLD)))
                            return ErrorStatements.parent_grandchild_age_difference;
                    }

                    if (relation == 11) {
                        if ((hhh.getAge() + pk.gov.pbs.tds.Constants.PARENT_GRANDCHILD_AGE_THRESHOLD) > age)
                            return ErrorStatements.parent_grandchild_age_difference;
                    }
                }

                Date dobDate = DateTimeUtil.getDateFrom(dob[2].toInt(), dob[1].toInt(), dob[0].toInt());
                Date now = DateTimeUtil.getCurrentDateTime();

                if (dobDate != null && dobDate.after(now))
                    return ErrorStatements.date_is_in_future;

                return null;
            }
        });

        //-----------------------------------------

        insertQuestion(
                builder.makeRBI(
                        "s1aq6"
                        , new RequiredValidator()
                )
        ).setConditions(new Question.Conditions() {
            @Override
            public String PreCondition(IQuestionnaireManager manager, Question self) {
                int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();
                int age = manager.getAnswerFrom("s1aq5")[0][0].toInt();

                if(age <= pk.gov.pbs.tds.Constants.MIN_MARRIAGEABLE_AGE)
                    self.loadAnswer(self.getIndex()+"_1", new ValueStore(1)); // never married

                if(relation == 2)
                    self.loadAnswer(self.getIndex() + "_2", new ValueStore(2)); // married

                return null;
            }

            @Override
            public String PostCondition(IQuestionnaireManager manager, Question self) {
                int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();
                int maritalStatus = self.getAnswers()[0][0].toInt();
                int age = manager.getAnswerFrom("s1aq5")[0][0].toInt();

                if(relation == 2 && maritalStatus != 2)
                    return ErrorStatements.Make.integrityViolationBetween("s1aq2", "s1aq6");

                if(age < pk.gov.pbs.tds.Constants.MIN_MARRIAGEABLE_AGE && maritalStatus >= 2 && maritalStatus <= 5)
                    return ErrorStatements.getInstance().invalid_marital_status_for_underage;

                if(age <= pk.gov.pbs.tds.Constants.MIN_MARRIAGEABLE_AGE && maritalStatus != 1)
                    return ErrorStatements.getInstance().married_age_error;

                if((relation == 5 || relation == 11) && maritalStatus == 1)
                    return ErrorStatements.getInstance().parent_can_not_be_never_married;

                if ((relation == 2 || relation == 5 || relation == 10 || relation == 11)
                        && !(maritalStatus >= 2 && maritalStatus <= 5)
                )
                    return ErrorStatements.Make.integrityViolationBetween("s1aq2", "s1aq6");

                return null;
            }
        });

        //----------------------------------------

//        int idStart=0,idEnd=0;
//        if (enumGender == 1){
//            idStart = 1;
//            idEnd = 50;
//        } else if (enumGender == 2){
//            idStart = 51;
//            idEnd = 97;
//        }
//        insertQuestion(builder.makeGI("s1aq7", new GroupInput[]{
//                builder.prepareGI_BtnKBI("s1aq7", InputType.TYPE_CLASS_NUMBER, new OptionalValidator(new BetweenValidator(new ValueStore(idStart), new ValueStore(idEnd))),
//                        (btn, kbi, which) -> {
//                            kbi.setText("99");
//                        }
//                )
//        }));

//        if (enumGender > -1) {
//            List<SpinnerItemRoster> options = new ArrayList<>();
//            options.add(new SpinnerItemRoster("Household Members"));
//            for (RosterSection m : mContext.getViewModel().getHouseholdMembers()) {
//                if (m.status == 1 && m.entryStatus == Constants.Status.ENTRY_COMPLETED && (m.getMaritalStatus() > 1 && m.getMaritalStatus() < 6))
//                    options.add(new SpinnerItemRoster(m, mLabelMaker));
//            }
//
//            options.add(new SpinnerItemRoster(
//                    new RosterSection() {
//                        @Override
//                        public String getPrimaryIdentifier() {
//                            return "";
//                        }
//
//                        @Override
//                        public void setPrimaryIdentifier(String PI) {
//
//                        }
//
//                        @Override
//                        public Integer getSecondaryIdentifier() {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setSecondaryIdentifier(Integer SI) {
//
//                        }
//
//                        @Override
//                        public Integer getTertiaryIdentifier() {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setTertiaryIdentifier(Integer TI) {
//
//                        }
//
//                        @Override
//                        public String getName() {
//                            return "Member not Alive";
//                        }
//
//                        @Override
//                        public Integer getRelationCode() {
//                            return null;
//                        }
//
//                        @Override
//                        public Integer getGenderCode() {
//                            return null;
//                        }
//
//                        @Override
//                        public Integer getAge() {
//                            return null;
//                        }
//
//                        @Override
//                        public Integer getMaritalStatus() {
//                            return null;
//                        }
//                    }
//                    , mLabelMaker
//            ));
//            options.add(new SpinnerItemRoster(
//                    new RosterSection() {
//                        @Override
//                        public String getPrimaryIdentifier() {
//                            return "";
//                        }
//
//                        @Override
//                        public void setPrimaryIdentifier(String PI) {
//
//                        }
//
//                        @Override
//                        public Integer getSecondaryIdentifier() {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setSecondaryIdentifier(Integer SI) {
//
//                        }
//
//                        @Override
//                        public Integer getTertiaryIdentifier() {
//                            return 0;
//                        }
//
//                        @Override
//                        public void setTertiaryIdentifier(Integer TI) {
//
//                        }
//
//                        @Override
//                        public String getName() {
//                            return "Not in the Roster";
//                        }
//
//                        @Override
//                        public Integer getRelationCode() {
//                            return null;
//                        }
//
//                        @Override
//                        public Integer getGenderCode() {
//                            return null;
//                        }
//
//                        @Override
//                        public Integer getAge() {
//                            return null;
//                        }
//
//                        @Override
//                        public Integer getMaritalStatus() {
//                            return null;
//                        }
//                    },
//                    mLabelMaker
//            ));
//            if (enumGender == 1){ //male enumerator
//                insertQuestion(
//                        builder.makeHouseholdMembers_SI("s1aq8", options, new RequiredValidator())
//                ).setConditions(new Question.Conditions() {
//                    @Override
//                    public String PreCondition(IQuestionnaireManager manager, Question self) {
//                        int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();
//                        int age = manager.getAnswerFrom("s1aq5")[0][0].toInt();
//
//                        S1AModel hhh = null;
//                        S1AModel spouse = null;
//                        S1AModel parent = null;
//                        for (RosterSection m : manager.getViewModel().getHouseholdMembers()){
//                            if (m.getRelationCode() == 1) {
//                                hhh = (S1AModel) m;
//                            } else if (m.getRelationCode() == 2 && spouse == null){
//                                spouse = (S1AModel) m;
//                            } else if (m.getRelationCode() == 5 && parent == null){
//                                parent = (S1AModel) m;
//                            }
//                        }
//
//                        HouseholdMembersSpinnerInput input = (HouseholdMembersSpinnerInput) self.getAdapter().getAskables()[0];
//
//                        //removing younger ones
//                        List<SpinnerItemRoster> toBeRemoved = new ArrayList<>();
//                        for (SpinnerItemRoster item : input.getOptions()){
//                            if (item.getModel() != null){
//                                RosterSection m = item.getModel();
//                                if (m.getAge() != null && m.getAge() < age)
//                                    toBeRemoved.add(item);
//
//                                if (m.getRelationCode() != null) {
//                                    if ((relation == 3 || relation == 4) && (m.getRelationCode() == 11 || m.getRelationCode() == 12 || m.getRelationCode() == 13 || m.getRelationCode() == 6))
//                                        toBeRemoved.add(item);
//                                    if ((relation == 9 || relation == 6) && (m.getRelationCode() == 11 || m.getRelationCode() == 2 || m.getRelationCode() == 1 || m.getRelationCode() == 3 || m.getRelationCode() == 4 || m.getRelationCode() == 7 || m.getRelationCode() == 8))
//                                        toBeRemoved.add(item);
//                                }
//                            }
//                        }
//                        input.getOptions().removeAll(toBeRemoved);
//                        //--------------------------------------------------------------------------
//
//                        //hhh as parent
//                        if (relation == 3){
//                            if (hhh != null)
//                                input.setAnswer(new ValueStore(hhh.getTertiaryIdentifier()));
//                            else if (spouse != null)
//                                input.setAnswer(new ValueStore(spouse.getTertiaryIdentifier()));
//                        }
//
//                        //hhh siblings
//                        if (relation == 6 && hhh != null){
//                            if (hhh.s1aq8 != null) input.setAnswer(new ValueStore(hhh.s1aq8));
//                        }
//
//                        //hhh as child
//                        if (relation == 1 && parent != null){
//                            input.setAnswer(new ValueStore(parent.getTertiaryIdentifier()));
//                        }
//
//                        if (input.hasAnswer())
//                            self.setState(QuestionStates.ANSWERED);
//                        return null;
//                    }
//
//                    @Override
//                    public String PostCondition(IQuestionnaireManager manager, Question self) {
//                        return null;
//                    }
//                });
//                //----------------------------------------------------------------------------------
//            } else if (enumGender == 2){ //female enumerator
//                insertQuestion(
//                        builder.makeHouseholdMembers_SI("s1aq9", options, new RequiredValidator())
//                ).setConditions(new Question.Conditions() {
//                    @Override
//                    public String PreCondition(IQuestionnaireManager manager, Question self) {
//                        int relation = manager.getAnswerFrom("s1aq2")[0][0].toInt();
//                        int age = manager.getAnswerFrom("s1aq5")[0][0].toInt();
//
//                        S1AModel hhh = null;
//                        S1AModel spouse = null;
//                        S1AModel parent = null;
//                        for (RosterSection m : manager.getViewModel().getHouseholdMembers()){
//                            if (m.getRelationCode() == 1) {
//                                hhh = (S1AModel) m;
//                            } else if (m.getRelationCode() == 2 && spouse == null){
//                                spouse = (S1AModel) m;
//                            } else if (m.getRelationCode() == 5 && parent == null){
//                                parent = (S1AModel) m;
//                            }
//                        }
//
//                        HouseholdMembersSpinnerInput input = (HouseholdMembersSpinnerInput) self.getAdapter().getAskables()[0];
//
//                        //removing younger ones
//                        List<SpinnerItemRoster> toBeRemoved = new ArrayList<>();
//                        for (SpinnerItemRoster item : input.getOptions()){
//                            if (item.getModel() != null){
//                                RosterSection m = item.getModel();
//                                if (m.getAge() != null && m.getAge() < age)
//                                    toBeRemoved.add(item);
//                                if (m.getRelationCode() != null) {
//                                    if ((relation == 3 || relation == 4) && (m.getRelationCode() == 11 || m.getRelationCode() == 12 || m.getRelationCode() == 13 || m.getRelationCode() == 6))
//                                        toBeRemoved.add(item);
//                                    if ((relation == 9 || relation == 6) && (m.getRelationCode() == 11 || m.getRelationCode() == 2 || m.getRelationCode() == 1 || m.getRelationCode() == 3 || m.getRelationCode() == 4 || m.getRelationCode() == 7 || m.getRelationCode() == 8))
//                                        toBeRemoved.add(item);
//                                }
//                            }
//                        }
//                        input.getOptions().removeAll(toBeRemoved);
//                        //--------------------------------------------------------------------------
//
//                        //hhh or spouse as parent
//                        if (relation == 3){
//                            if (hhh != null)
//                                input.setAnswer(new ValueStore(hhh.getTertiaryIdentifier()));
//                            else if (spouse != null)
//                                input.setAnswer(new ValueStore(spouse.getTertiaryIdentifier()));
//                        }
//
//
//                        //hhh siblings
//                        if (relation == 6 && hhh != null){
//                            if (hhh.s1aq9 != null) input.setAnswer(new ValueStore(hhh.s1aq9));
//                        }
//
//                        //hhh as child
//                        if (relation == 1 && parent != null){
//                            input.setAnswer(new ValueStore(parent.getTertiaryIdentifier()));
//                        }
//
//                        if (input.hasAnswer())
//                            self.setState(QuestionStates.ANSWERED);
//                        return null;
//                    }
//
//                    @Override
//                    public String PostCondition(IQuestionnaireManager manager, Question self) {
//                        return null;
//                    }
//                });
//                //----------------------------------------------------------------------------------
//            }
//        }

        //-----------------------------------------
        insertQuestion(
                builder.makeMultiColRBI(
                        "s1aq10"
                        , ColumnCount.DOUBLE
                        , new RequiredValidator()
                )
        ).setConditions(new Question.Conditions() {
            @Override
            public String PreCondition(IQuestionnaireManager manager, Question self) {
                if(manager.getAnswerFrom("s1aq2")[0][0].toInt() == 1)
                    self.loadAnswer(self.getIndex()+"_1", new ValueStore(1));
                return null;
            }
            @Override
            public String PostCondition(IQuestionnaireManager manager, Question self) {
//                if (manager.getAnswerFrom("s1aq2")[0][0].toInt() == 1 && self.getAnswers()[0][0].toInt() != 1)
//                    return ErrorStatements.getInstance().hhh_must_be_member_of_hh;
                return null;
            }
        });
    }
}
