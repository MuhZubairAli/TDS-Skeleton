package pk.gov.pbs.tds_example.meta;

import pk.gov.pbs.formbuilder.core.IMetaManifest;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.tds.models.BlockAssignment;
import pk.gov.pbs.tds.models.BlockAssignmentStatus;
import pk.gov.pbs.tds.models.Household;
import pk.gov.pbs.tds.models.HouseholdAssignment;
import pk.gov.pbs.tds.models.InformationModel;
import pk.gov.pbs.tds.models.S0Model;
import pk.gov.pbs.tds.models.Structure;
import pk.gov.pbs.tds.models.pojo.BlockBoundary;
import pk.gov.pbs.tds.util.MetaManifestBase;
import pk.gov.pbs.tds_example.activities.JsonBasedForm2Activity;
import pk.gov.pbs.tds_example.activities.JsonBasedFormActivity;
import pk.gov.pbs.tds_example.activities.PrimaryFormActivity;
import pk.gov.pbs.tds_example.activities.S1AActivity;
import pk.gov.pbs.tds_example.models.PrimaryFormModel;
import pk.gov.pbs.tds_example.activities.SecondaryFormActivity;
import pk.gov.pbs.tds_example.models.S1AModel;
import pk.gov.pbs.tds_example.models.SecondaryFormModel;
import pk.gov.pbs.tds_example.activities.TertiaryFormActivity;
import pk.gov.pbs.tds_example.models.TertiaryFormModel;

/**
 Every section have an index and an identifier
 section index is the number used to get class of section or class of model
 identifier is symbol of section used in column names
 section titles could be obtained from activity class itself
 */
public class MetaManifest extends MetaManifestBase {
    private static MetaManifest INSTANCE;
    private static final Class<?>[] MODELS = new Class[] {
            PrimaryFormModel.class, SecondaryFormModel.class, TertiaryFormModel.class,
            S1AModel.class, PrimaryModel.class, PrimaryModel.class, S0Model.class,


            // TDS Models
            InformationModel.class, BlockBoundary.class, BlockAssignment.class, BlockAssignmentStatus.class,
            HouseholdAssignment.class,

            // listing models
            Structure.class, Household.class
    };

    private static final Class<?>[] SECTIONS = new Class[] {
            PrimaryFormActivity.class, SecondaryFormActivity.class, TertiaryFormActivity.class,
            S1AActivity.class, JsonBasedFormActivity.class, JsonBasedForm2Activity.class
    };

    private static final String[] IDENTIFIERS = new String[] {
            "Primary Form Example", "Secondary Form Example", "Tertiary Form Example",
            "Roster Section Example", "Example Json Form", "Example JSON form"
    };

    private MetaManifest(){
        super();
    }

    public static IMetaManifest getInstance(){
        if (INSTANCE == null)
            INSTANCE = new MetaManifest();
        return INSTANCE;
    }

    @Override
    public Class<?>[] getModels() {
        return MODELS;
    }

    @Override
    public String[] getSectionIdentifiers() {
        return IDENTIFIERS;
    }

    @Override
    public Class<?>[] getSections(){
        return SECTIONS;
    }

    @Override
    public Class<S1AModel> getRosterModel() {
        return S1AModel.class;
    }

    @Override
    public Class<?> getFormMenuActivity() {
        return null;
    }
}
