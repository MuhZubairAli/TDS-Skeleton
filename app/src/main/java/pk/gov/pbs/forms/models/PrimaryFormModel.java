package pk.gov.pbs.forms.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pk.gov.pbs.database.annotations.NotNull;
import pk.gov.pbs.database.annotations.Unique;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.formbuilder.models.annotations.PrimaryIdentifier;

public class PrimaryFormModel extends PrimaryModel {
    @NotNull
    @Expose
    @Unique
    @PrimaryIdentifier
    @SerializedName("PCode")
    public String pcode;

    public String address;

    @Override
    public String getPrimaryIdentifier() {
        return pcode;
    }

    @Override
    public void setPrimaryIdentifier(String PI) {
        pcode = PI;
    }
}
