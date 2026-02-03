package pk.gov.pbs.forms.meta;

import pk.gov.pbs.formbuilder.meta.ErrorStatementProvider;
import pk.gov.pbs.tds.Constants;

public class ErrorStatements extends ErrorStatementProvider {
    private static ErrorStatements instance;
    public static String head_already_added;
    public static String head_not_married;
    public static String cannot_have_children;
    public static String female_cant_have_multiple_spouse;
    public static String hhh_relation_cannot_be_changed;
    public static String first_member_must_be_hhh;
    public static String invalid_spouse_gender;
    public static String hhh_age_error;
    public static String married_age_error;
    public static String parent_age_difference;
    public static String hhh_must_be_member_of_hh;
    public static String parent_grandchild_age_difference;
    public static String invalid_marital_status_for_underage;
    public static String parent_can_not_be_never_married;
    public static String only_male_members;
    public static String presence_can_not_be_changed;

    public static String specify_area_for_checked_items;
    public static String specify_unit_for_checked_items;
    public static String specify_number_of_items;
    public static String invalid_answer;
    public static String male_enum_age_limit;
    public static String female_enum_age_limit;
    public static String children_total_not_equal;
    public static String children_remaining_not_equal;
    public static String cant_unvisit_the_visited;
    public static String not_allowed_to_change;
    public static String amount_not_matching;
    public static String amount_not_provided;
    public static String income_not_matching;
    public static String income_not_provided;
    public static String must_be_immunized;
    public static String underage_cant_be_literate;
    public static String invalid_wrt_marital_status;
    public static String invalid_total_expenditure;;
    public static String date_is_before_birth;
    public static String date_is_in_future;

    private ErrorStatements(){
        super();
    }

    public static ErrorStatements getInstance(){
        if (instance == null)
            instance = new ErrorStatements();
        return instance;
    }

    @Override
    protected void initStatements() throws Exception {
        addErrorStatement(head_already_added, "Head of household already added");
        addErrorStatement(head_not_married,"Head of the household is not married, Can not add spouse");
        addErrorStatement(cannot_have_children,"Head of the household is never married, Can not have children or grand children");
        addErrorStatement(female_cant_have_multiple_spouse,"Being head of household a female cannot have more than one spouse");
        addErrorStatement(hhh_relation_cannot_be_changed,"Relation of head of household can not be changed");
        addErrorStatement(first_member_must_be_hhh,"The first member household being added must be Head of the household");
        addErrorStatement(invalid_spouse_gender,"Specified gender is not valid according to spouse's gender");
        addErrorStatement(hhh_age_error,"Age limit for Head of Household is "+ Constants.MIN_HHH_AGE +" years or over");
        addErrorStatement(married_age_error, "Age of Married household member must be greater than " + Constants.MIN_MARRIAGEABLE_AGE + " years");
        addErrorStatement(parent_age_difference,  "There must be the difference of at least "+Constants.PARENT_CHILD_AGE_THRESHOLD+" years between age of parent and child");
        addErrorStatement(parent_grandchild_age_difference,  "There must be the difference of at least "+Constants.PARENT_CHILD_AGE_THRESHOLD+" years between age of Grand Parent and Grand Child");
        addErrorStatement(hhh_must_be_member_of_hh,"Head of household must be member of household");
        addErrorStatement(invalid_marital_status_for_underage,"Current member's age is not valid for given marital status");
        addErrorStatement(parent_can_not_be_never_married,"According to {s1aq2} current member can not be Never Married");
        addErrorStatement(only_male_members, "Only add male members could be added to the roster");
        addErrorStatement(presence_can_not_be_changed, "Presence of the household member can not be different previous previous entry");
        addErrorStatement(cant_unvisit_the_visited, "Current member has visited the Healthcare Centre previously, So current selection is invalid for the member");

        addErrorStatement(specify_area_for_checked_items, "Specify area for all checked Land / Property of the household");
        addErrorStatement(specify_unit_for_checked_items, "Specify unit for all checked Land / Property");
        addErrorStatement(specify_number_of_items, "Specify number of items owned");
        addErrorStatement(invalid_answer, "Given answer is invalid");
        addErrorStatement(male_enum_age_limit, "Male Enumerator could only add household members who are male and older than 9 years");
        addErrorStatement(female_enum_age_limit, "Female Enumerator could only add household members who are either female or children less than 10 years old");
        addErrorStatement(children_total_not_equal, "Current selection is invalid considering specified total number of boys & girls (Q6) and specified children living with you or elsewhere (Q7)");
        addErrorStatement(children_remaining_not_equal, "Specified number of died children is invalid considering specified total number of boys & girls (Q6) and specified children living with you and elsewhere (Q7)");
        addErrorStatement(not_allowed_to_change,"This question do not allow change in existing answer");
        addErrorStatement(amount_not_matching,"Specified total amounts in <b>6A <em>Code-1000</em></b> or <b>6B <em>Code-2000</em></b> do not match in confirm inputs");
        addErrorStatement(amount_not_provided,"Total amount for <b>6A <em>Code-1000</em></b> or <b>6B <em>Code-2000</em></b> (Paid & Consumed), not provided. <em>Please enter <b>0</b> if amount is currently not available</em>");
        addErrorStatement(income_not_matching,"Specified amounts in <b>Total Income of Female Members</b> do not match in confirm inputs");
        addErrorStatement(income_not_provided,"Total income of female members not provided. <em>Please enter <b>0</b> if amount is currently not available</em>");
        addErrorStatement(must_be_immunized, "The child must be immunized at least once considering previous response from respondent (Q2)");
        addErrorStatement(underage_cant_be_literate, "Underage children can not read & write with understanding and solve basic math");
        addErrorStatement(invalid_wrt_marital_status, "Considering marital status of current member in the Roster, current selection is invalid, please update it's entry in Roster for correction.");
        addErrorStatement(invalid_total_expenditure, "Total expenditure must be greater than zero, Please report expenses in relevant inputs");
        addErrorStatement(date_is_before_birth, "Entered date is before date of birth, Please enter correct date");
        addErrorStatement(date_is_in_future, "Entered date is in future, Please enter correct date");


        addDatumDescriptor("s1aq2", "[Relationship with Head of Household] (1A-Q2)");
        addDatumDescriptor("s1aq3", "[Gender] (1A-Q3)");
        addDatumDescriptor("s1aq5", "[Age] (1A-Q4)");
        addDatumDescriptor("s1aq6", "[Marital Status] (1A-Q5)");
        addDatumDescriptor("s1aq10", "[Is Household Member] (1A-Q10)");
    }
}
