package pk.gov.pbs.forms;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;


import androidx.annotation.Nullable;

import java.util.List;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.forms.meta.MetaManifest;
import pk.gov.pbs.forms.models.PrimaryFormModel;
import pk.gov.pbs.forms.models.SecondaryFormModel;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.databinding.ActivityMainBinding;

public class MainActivity extends ThemedCustomActivity {
    ActivityMainBinding binding;
    FormRepository repository;
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
                List<PrimaryFormModel> pml = repository.getDatabase().query(PrimaryFormModel.class);
                if (pml != null && !pml.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (PrimaryFormModel pm : pml)
                        sb.append(pm.toString()).append("\n<br />\n<br />");
                    binding.text.setText(Html.fromHtml(sb.toString()));
                }

                mUXToolkit.toast(pml == null || pml.isEmpty() ? "0" : String.valueOf(pml.size()) + " Entries found in database");
                return true;
            }
        });
        binding.btn2.setOnClickListener((view -> {
            PrimaryFormModel pm = repository.getDatabase().querySingle(PrimaryFormModel.class, "aid=1");
            FormContext mFormContext = new FormContext(pm.pcode, 0,1);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);

            startActivity(intent);
        }));


        binding.btn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                List<SecondaryFormModel> pml = repository.getDatabase().query(SecondaryFormModel.class);
                if (pml != null && !pml.isEmpty()) {
                    StringBuilder sb = new StringBuilder();
                    for (SecondaryFormModel pm : pml)
                        sb.append(pm.toString()).append("\n<br />\n<br />");
                    binding.text.setText(Html.fromHtml(sb.toString()));
                }

                mUXToolkit.toast(pml == null || pml.isEmpty() ? "0" : String.valueOf(pml.size()) + " Entries found in database");
                return true;
            }
        });

        binding.btn3.setOnClickListener((view -> {
            SecondaryFormModel tm = repository.getDatabase().querySingle(SecondaryFormModel.class, "aid=1");

            FormContext mFormContext = new FormContext(tm.getPrimaryIdentifier(), tm.getSecondaryIdentifier(), 0,2);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);

            startActivity(intent);
        }));

        binding.btn5.setOnClickListener((v)-> {
            FormContext mFormContext = new FormContext("12345678",100,3);
            Intent intent = new Intent(this, MetaManifest.getInstance().getSection(mFormContext.getSection()));
            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, mFormContext);
            startActivity(intent);
        });
    }
}
