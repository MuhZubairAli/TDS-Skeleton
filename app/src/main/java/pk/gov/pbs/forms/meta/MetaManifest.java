package pk.gov.pbs.forms.meta;

import pk.gov.pbs.formbuilder.core.IMetaManifest;
import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.forms.PrimaryFormActivity;
import pk.gov.pbs.forms.S1AActivity;
import pk.gov.pbs.forms.models.PrimaryFormModel;
import pk.gov.pbs.forms.SecondaryFormActivity;
import pk.gov.pbs.forms.models.S1AModel;
import pk.gov.pbs.forms.models.SecondaryFormModel;
import pk.gov.pbs.forms.TertiaryFormActivity;
import pk.gov.pbs.forms.models.TertiaryFormModel;
import pk.gov.pbs.tds.activities.FormsListingActivity;

/**
 Every section have an index and an identifier
 section index is the number used to get class of section or class of model
 identifier is symbol of section used in column names
 section titles could be obtained from activity class itself
 */
public class MetaManifest implements IMetaManifest {
    private static MetaManifest INSTANCE;
    private static final Class<?>[] MODELS = new Class[] {
           PrimaryFormModel.class, SecondaryFormModel.class, TertiaryFormModel.class, S1AModel.class
    };

    private static final Class<?>[] SECTIONS = new Class[] {
            PrimaryFormActivity.class, SecondaryFormActivity.class, TertiaryFormActivity.class, S1AActivity.class
    };

    private static final String[] IDENTIFIERS = new String[] {
            "Primary Form Example", "Secondary Form Example", "Tertiary Form Example", "Roster Section Example"
    };

    private MetaManifest(){
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
    public Class<?> getStarterActivity() {
        return FormsListingActivity.class;
    }

    @Override
    public Class<?> getHouseholdRosterSection() {
        return null;
    }

}
