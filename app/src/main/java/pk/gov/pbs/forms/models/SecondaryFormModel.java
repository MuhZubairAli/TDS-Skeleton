package pk.gov.pbs.forms.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pk.gov.pbs.database.annotations.NotNull;
import pk.gov.pbs.database.annotations.Table;
import pk.gov.pbs.database.annotations.Unique;
import pk.gov.pbs.formbuilder.models.SecondaryModel;
import pk.gov.pbs.formbuilder.models.annotations.PrimaryIdentifier;
import pk.gov.pbs.formbuilder.models.annotations.SecondaryIdentifier;

@Table(version = 2)
public class SecondaryFormModel extends SecondaryModel {
    @NotNull
    @Expose
    @Unique
    @PrimaryIdentifier
    @SerializedName("PCode")
    public String pcode;

    @NotNull
    @Expose
    @Unique
    @SecondaryIdentifier
    @SerializedName("HHNo")
    public Integer hhno;

    @NotNull
    public String head;

    @Override
    public Integer getSecondaryIdentifier() {
        return hhno;
    }

    @Override
    public void setSecondaryIdentifier(Integer SI) {
        hhno=SI;
    }

    @Override
    public String getPrimaryIdentifier() {
        return pcode;
    }

    @Override
    public void setPrimaryIdentifier(String PI) {
        pcode = PI;
    }
}
