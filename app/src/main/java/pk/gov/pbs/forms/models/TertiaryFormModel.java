package pk.gov.pbs.forms.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pk.gov.pbs.database.annotations.NotNull;
import pk.gov.pbs.database.annotations.Table;
import pk.gov.pbs.database.annotations.Unique;
import pk.gov.pbs.formbuilder.models.RosterSection;
import pk.gov.pbs.formbuilder.models.TertiaryModel;
import pk.gov.pbs.formbuilder.models.annotations.PrimaryIdentifier;
import pk.gov.pbs.formbuilder.models.annotations.SecondaryIdentifier;
import pk.gov.pbs.formbuilder.models.annotations.TertiaryIdentifier;

@Table(version = 2)
public class TertiaryFormModel extends RosterSection {
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
    @Expose
    @Unique
    @TertiaryIdentifier
    @SerializedName("SNo")
    public Integer sno;

    String name;
    Integer age;

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

    @Override
    public Integer getTertiaryIdentifier() {
        return sno;
    }

    @Override
    public void setTertiaryIdentifier(Integer TI) {
        sno = TI;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Integer getRelationCode() {
        return 0;
    }

    @Override
    public Integer getGenderCode() {
        return 0;
    }

    @Override
    public Integer getAge() {
        return age;
    }

    @Override
    public Integer getMaritalStatus() {
        return 0;
    }
}
