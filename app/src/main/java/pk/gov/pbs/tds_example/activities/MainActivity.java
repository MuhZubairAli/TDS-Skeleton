package pk.gov.pbs.tds_example.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;


import androidx.annotation.Nullable;

import java.util.List;
import java.util.UUID;

import pk.gov.pbs.database.ModelBasedDatabaseHelper;
import pk.gov.pbs.database.Updater;
import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.tds_example.meta.MetaManifest;
import pk.gov.pbs.tds_example.models.ExampleModel;
import pk.gov.pbs.tds_example.models.PrimaryFormModel;
import pk.gov.pbs.tds_example.models.SecondaryFormModel;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds_example.databinding.ActivityMainBinding;
import pk.gov.pbs.tds_example.models.TestModel;
import pk.gov.pbs.utils.StaticUtils;

public class MainActivity extends ThemedCustomActivity {
    ActivityMainBinding binding;
    FormRepository repository;
    int i = 0;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        super.setContentView(binding.getRoot());
        repository = FormRepository.getInstance(getApplication());

        binding.btn1.setOnClickListener((view -> {
            FormContext mFormContext = new FormContext("",0);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);
            startActivity(intent);
        }));

        binding.btn1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                List<PrimaryFormModel> pml = repository.getDatabase().queryRows(PrimaryFormModel.class);
                if (pml != null && !pml.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (PrimaryFormModel pm : pml)
                        sb.append(StaticUtils.getGson().toJson(pm)).append("\n<br />\n<br />");
                    binding.text.setText(Html.fromHtml(sb.toString()));
                }

                mUXToolkit.toast(pml == null || pml.isEmpty() ? "0" : String.valueOf(pml.size()) + " Entries found in database");
                return true;
            }
        });

        binding.btn2.setOnClickListener((view -> {
            PrimaryFormModel pm = repository.getDatabase().query(PrimaryFormModel.class, "aid=1");
            FormContext mFormContext = new FormContext(pm.pcode, 0,1);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);

            startActivity(intent);
        }));


        binding.btn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                List<SecondaryFormModel> pml = repository.getDatabase().queryRows(SecondaryFormModel.class);
                if (pml != null && !pml.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (SecondaryFormModel pm : pml)
                        sb.append(StaticUtils.getGson().toJson(pm)).append("\n<br />\n<br />");
                    binding.text.setText(Html.fromHtml(sb.toString()));
                }

                mUXToolkit.toast(pml == null || pml.isEmpty() ? "0" : String.valueOf(pml.size()) + " Entries found in database");
                return true;
            }
        });

        binding.btn3.setOnClickListener((view -> {
            //SecondaryFormModel tm = repository.getDatabase().query(SecondaryFormModel.class, "aid=2");

            FormContext mFormContext = new FormContext("12345678", 100,2);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);

            startActivity(intent);
        }));

        binding.btn4.setOnClickListener((view)->{

//            ExampleModel ex = new ExampleModel();
//            ex.setPrimaryIdentifier("Lahore");
//            ex.setSecondaryIdentifier(++i);
//            ex.setName(UUID.randomUUID().toString());
//            ex.setAge(i * 10 + i);
//            ex.setGender(i % 2 == 0 ? 1 : 2);
//            ex.setAddress("Address " + i);
//            ex.setUniversity("University " + i);
//            ex.setCgpa(i % 4.0f);
//            ex.setFee(100.0*i*i);
//            ex.entryStatus = 2;
//            long id = repository.getDatabase().insert(ex);
//            mUXToolkit.toast("Inserted " + id);
//
//
//
//            repository.getDatabase().update(ExampleModel.class, (setValue, whereClause, comparator) -> {
//                setValue.setName("Kitabi Chozy");
//                setValue.setAge(111);
//
//                whereClause.setPrimaryIdentifier("Chiniot");
//                whereClause.setCgpa(3.0F);
//            });
//
//
//            repository.getDatabase().update(ExampleModel.class, (setValue, comparator) -> {
//                setValue.setName("Nalaik Bhanda");
//                setValue.setAddress("Dozakh");
//                setValue.setGender(3);
//
//                return new Updater.Predicate("cgpa <= ? and area = ?", "2.0", "Chiniot");
//            });
//
//            repository.getDatabase().update(ExampleModel.class, (setValue, comparator) -> {
//                comparator.setUniversity("");
//                comparator.setFee(0D);
//
//                setValue.setUniversity(null);
//                setValue.setFee(null);
//                setValue.setName("Uneducated");
//
//                return new Updater.Predicate("cgpa <= ? and area = ?", "1", "Lahore");
//            });

            /// //////////////////////////////////////

            TestModel model = new TestModel();
            model.name = "Test one";
            model.age = 1111;

            try {
                repository.getDatabase().update(model);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
////
//            long id = repository.getDatabase().insert(model);
//
//            TestModel model1 = new TestModel();
//            model1.name = "Test1";
//            model1.age = 13;
//
//            long id1 = repository.getDatabase().insert(model1);



        });

        binding.btn5.setOnClickListener((v)-> {
            FormContext mFormContext = new FormContext("12345678",100,3);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);
            startActivity(intent);
        });

        binding.btn6.setOnClickListener((v)-> {
            FormContext mFormContext = new FormContext("0000000000", 100,5);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);
            startActivity(intent);
        });
    }
}
