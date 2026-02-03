package pk.gov.pbs.forms.models;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import pk.gov.pbs.database.annotations.NotNull;
import pk.gov.pbs.database.annotations.Table;
import pk.gov.pbs.database.annotations.Unique;
import pk.gov.pbs.formbuilder.models.RosterSection;
import pk.gov.pbs.formbuilder.models.annotations.PrimaryIdentifier;
import pk.gov.pbs.formbuilder.models.annotations.SecondaryIdentifier;
import pk.gov.pbs.formbuilder.models.annotations.TertiaryIdentifier;
import pk.gov.pbs.tds.CustomApplication;

@Table(version = 2)
public class S1AModel extends RosterSection {
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

    @NotNull
    @Expose
    @Unique
    @SerializedName("S1AQ1")
    public String s1aq1;
    @NotNull
    @Expose
    @SerializedName("S1AQ2")
    public Integer s1aq2;
    @Nullable
    @Expose
    @SerializedName("S1AQ3")
    public Integer s1aq3;
    @Nullable
    @Expose
    @SerializedName("S1AQ4")
    public Integer s1aq4;
    @Nullable
    @Expose
    @SerializedName("S1AQ51")
    public Integer s1aq51;
    @Nullable
    @Expose
    @SerializedName("S1AQ52a")
    public Integer s1aq52a;
    @Nullable
    @Expose
    @SerializedName("S1AQ52b")
    public Integer s1aq52b;
    @Nullable
    @Expose
    @SerializedName("S1AQ52c")
    public Integer s1aq52c;
    @Nullable
    @Expose
    @SerializedName("S1AQ6")
    public Integer s1aq6;
    @Nullable
    @Expose
    @SerializedName("S1AQ7")
    public Integer s1aq7;
    @Nullable
    @Expose
    @SerializedName("S1AQ8")
    public Integer s1aq8;
    @Nullable
    @Expose
    @SerializedName("S1AQ9")
    public Integer s1aq9;
    @Nullable
    @Expose
    @SerializedName("S1AQ10")
    public Integer s1aq10;

    public S1AModel() {
        operatorId = CustomApplication.getUsername();
    }

    @Override
    public String getName() {
        return s1aq1;
    }

    @Override
    public Integer getRelationCode() {
        return s1aq2;
    }

    @Override
    public Integer getGenderCode() {
        return s1aq3;
    }

    @Override
    public Integer getAge() {
        return s1aq51;
    }

    @Override
    public Integer getMaritalStatus() {
        return s1aq6;
    }

    public boolean isEntryComplete(){
        return s1aq1 != null &&
                s1aq2 != null &&
                s1aq3 != null &&
                s1aq4 != null &&
                s1aq51 != null &&
                s1aq52a != null &&
                s1aq52b != null &&
                s1aq52c != null &&
                s1aq6 != null &&
                (s1aq7 != null || s1aq6 != 2) &&
                s1aq8 != null &&
                s1aq9 != null &&
                s1aq10 != null;
    }

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

}
