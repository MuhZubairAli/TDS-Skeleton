package pk.gov.pbs.tds_example.models;

import pk.gov.pbs.database.annotations.Table;
import pk.gov.pbs.database.annotations.Unique;

@Table(version = 2)
public class TestModel {
    @Unique
    public String name;
    public Integer age;
}
