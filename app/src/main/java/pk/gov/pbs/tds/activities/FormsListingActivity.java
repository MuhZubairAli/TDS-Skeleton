package pk.gov.pbs.tds.activities;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.formbuilder.meta.Constants;
import pk.gov.pbs.formbuilder.models.FormContext;
import pk.gov.pbs.formbuilder.models.LoginPayload;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.services.SyncScheduler;
import pk.gov.pbs.tds.services.SyncService;
import pk.gov.pbs.tds.databinding.ActivityFormsListingBinding;
public class FormsListingActivity extends ThemedCustomActivity {
    private ActivityFormsListingBinding binding;
    FormRepository mFormRepository;
    FormBuilderRepository mFormBuilderRepo;

    BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(SyncScheduler.BROADCAST_ACTION_HOUSEHOLD_SYNCED)) {
                if (mFormRepository == null)
                    return;

                String hhId = intent.getStringExtra(SyncService.BROADCAST_EXTRA_HOUSEHOLD);
                setSyncStatus(hhId);
            }
        }
    };

    @Override
    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFormsListingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setActivityTitle("Poultry Farms | Start Survey", "Specify Block and Household");

        mFormBuilderRepo = FormBuilderRepository.getInstance(getApplication());
        mFormRepository = FormRepository.getInstance(getApplication());

        IntentFilter filter = new IntentFilter(SyncScheduler.BROADCAST_ACTION_HOUSEHOLD_SYNCED);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(syncReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else
            registerReceiver(syncReceiver, filter);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        init();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(syncReceiver);
    }

    private void init(){
        LoginPayload loginPayload = CustomApplication.getLoginPayload();

        binding.btnAddFarm.setOnClickListener(v -> {
            FormContext formContext = new FormContext(CustomApplication.getLoginPayload().selectedBlock, 100,0);
            Intent intent = new Intent(this, FormBeginnerActivity.class);

            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, formContext);
            startActivity(intent);
            finish();
        });

//        List<String> blockCodes = mFormRepository.getUtilsDao().getActiveBlockCodes();
//        ArrayList<String> bco = new ArrayList<>();
//        bco.add("Block Codes");
//        if (blockCodes != null)
//            bco.addAll(blockCodes);
//
//        ArrayAdapter<String> ad = new ArrayAdapter<>(this, R.layout.item_list_sp, bco);
//        ad.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
//        binding.spi.setAdapter(ad);
//        binding.spi.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if(position != 0) {
////                    binding.btnAddFarm.setVisibility(View.VISIBLE);
//                    String ebcode = parent.getSelectedItem().toString().substring(3);
//
//                    if(loginPayload.getSelectedBlock() == null || !ebcode.equalsIgnoreCase(loginPayload.getSelectedBlock())) {
//                        mUXToolkit.showProgressDialogue(R.string.pd_label_please_wait);
//                        loginPayload.setSelectedBlock(ebcode);
//                        mFormBuilderRepo.getLoginDao().setLoginPayload(loginPayload);
//                        CustomApplication.setLoginPayload(loginPayload);
//
//                    }
//
//                    mFormRepository.executeDatabaseOperation(new IDatabaseOperation<List<S1Model>>() {
//                        public List<S1Model> execute(ModelBasedDatabaseHelper db) {
//                            List<S1Model> result = new ArrayList<>();
//                            List<Establishment> models = mFormRepository.getDatabase().query(
//                                    Establishment.class,
//                                    "`EBCode` = ? AND `status` = ?",
//                                    ebcode,
//                                    String.valueOf(1)
//                            );
//
//                            for (Establishment estb : models){
//                                S1Model s1 = mFormRepository.getDatabase().querySingle(
//                                        S1Model.class,
//                                        "EBCode = ? AND srNo = ?",
//                                        estb.EBCode,
//                                        String.valueOf(estb.id)
//                                );
//                                if (s1 != null) {
//                                    if (estb.record_type != null)
//                                        s1.entryStatus = Integer.parseInt(estb.record_type);
//                                    else
//                                        s1.entryStatus = null;
//
//                                    result.add(s1);
//                                }
//                                else {
//                                    s1 = new S1Model();
//                                    s1.EBCode = estb.EBCode;
//                                    s1.pcode = estb.PCode;
//                                    s1.farmName = estb.name;
//                                    s1.ownerName = estb.manager;
//                                    s1.phone = estb.contactno;
//                                    s1.email = estb.email;
//                                    s1.website = estb.website;
//                                    s1.province = estb.province;
//                                    s1.district = estb.district;
//                                    s1.address = estb.address;
//                                    s1.srNo = estb.id;
//
//                                    if (estb.record_type != null)
//                                        s1.entryStatus = Integer.parseInt(estb.record_type);
//                                    else
//                                        s1.entryStatus = null;
//
//                                    result.add(s1);
//                                }
//                            }
//
//                            return result;
//                        }
//
//                        public void postExecute(List<S1Model> result) {
//                            displayBlocksInTable(result);
//                            mUXToolkit.dismissProgressDialogue();
//                        }
//                    });
//
//                } else {
////                    binding.btnAddFarm.setVisibility(View.GONE);
//                    LinearLayout itemsContainer = findViewById(pk.gov.pbs.tds.R.id.container_items);
//                    itemsContainer.removeAllViews();
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//            }
//        });
//
//        // Loading selected block in spinner
//        if(loginPayload.getSelectedBlock() != null){
//            int pos = -1;
//            for (int i = 0; i < bco.size(); i++){
//                if(bco.get(i).substring(3).equalsIgnoreCase(loginPayload.getSelectedBlock())){
//                    pos = i;
//                    break;
//                }
//            }
//            if(pos != -1)
//                binding.spi.setSelection(pos, true);
//        }

    }

    private void setSyncStatus(String entryId){
        mUXToolkit.toast("From FormListingActivity: Entry Synced : " + entryId);
    }
//
//    private void displayBlocksInTable(List<S1Model> dataSet){
//
//        String[] desigs = new String[] {
//                "",
//                "Owner",
//                "Responsible family member",
//                "Manager",
//                "Other Employee"
//        };
//
//        LinearLayout itemsContainer = findViewById(pk.gov.pbs.tds.R.id.container_items);
//        itemsContainer.removeAllViews();
//
//        for (S1Model obj : dataSet){
//            View tableRow = getLayoutInflater().inflate(pk.gov.pbs.tds.R.layout.item_farm_begin,itemsContainer, false);
//            itemsContainer.addView(tableRow);
//            tableRow.setTag(pk.gov.pbs.tds.R.id.hh_item_tag_id, obj.srNo + "@" + obj.pcode);
//            String header = String.format(Locale.getDefault(),"%03d: %s", obj.srNo, obj.farmName);
//            ((TextView) tableRow.findViewById(pk.gov.pbs.tds.R.id.th_farm_name)).setText(header);
//            ((TextView) tableRow.findViewById(pk.gov.pbs.tds.R.id.td_owner)).setText(String.valueOf(obj.ownerName));
//            String respondent = obj.respondentName;
//            if (obj.respondentDesignation != null)
//                respondent = String.format("%s (%s)",obj.respondentName, desigs[obj.respondentDesignation]);
//            ((TextView) tableRow.findViewById(pk.gov.pbs.tds.R.id.td_respondent)).setText(respondent);
//            ((TextView) tableRow.findViewById(pk.gov.pbs.tds.R.id.td_address)).setText(String.valueOf(obj.getAddress()));
//            ((TextView) tableRow.findViewById(pk.gov.pbs.tds.R.id.td_phone)).setText(String.valueOf(obj.phone));
//            ((TextView) tableRow.findViewById(pk.gov.pbs.tds.R.id.td_source)).setText(obj.entryStatus==null?"Unknown":obj.entryStatus==1?"Administrative Data":"Economic Census Data");
//
//            Button btnAction = tableRow.findViewById(pk.gov.pbs.tds.R.id.btn_begin);
//            Button btnVerify = tableRow.findViewById(pk.gov.pbs.tds.R.id.btn_verify);
//            ImageView imgStatus = tableRow.findViewById(pk.gov.pbs.tds.R.id.img_status);
//            TextView tvStatus = tableRow.findViewById(pk.gov.pbs.tds.R.id.th_status);
//
//            btnVerify.setEnabled(false);
//            FormStatus hhs = mFormRepository.getUtilsDao().getFormStatus(obj.pcode, obj.srNo);
//            if(hhs != null){
//                if (hhs.form_status != null && NullSafe.equals(hhs.final_entry_status, Constants.Status.ENTRY_COMPLETED)){
//                    if(hhs.sid != null && hhs.sid > 0){
//                        tvStatus.setText("Synced");
//                        imgStatus.setImageResource(R.drawable.ic_done_all);
//                        btnAction.setText(R.string.label_btn_details);
//                        btnAction.setEnabled(false);
//                        continue;
//                    }
//
//                    if(hhs.form_status != null && hhs.form_status > 0) {
//                        // for now set it to 2 (because of v2.0.0 this could be null)
//                        // in later versions it is not possible that this would be null (if form_status is set)
//                        if (hhs.final_entry_status == null)
//                            hhs.final_entry_status = 2;
//
//                        switch (hhs.form_status) {
//                            case Constants.Status.FORM_COMPLETED:
//                                tvStatus.setText(MetaManifest.form_status[hhs.final_entry_status + hhs.form_status]);
//                                imgStatus.setImageResource(R.drawable.ic_done);
//                                break;
//                            case Constants.Status.FORM_PARTIALLY_REFUSED:
//                                tvStatus.setText(MetaManifest.form_status[hhs.final_entry_status + hhs.form_status]);
//                                imgStatus.setImageResource(R.drawable.ic_incomplete);
//                                break;
//                            case Constants.Status.FORM_REFUSED:
//                            case Constants.Status.FORM_NON_CONTACTED:
//                            case Constants.Status.FORM_STATUS_OTHER:
//                                tvStatus.setText(MetaManifest.form_status[hhs.final_entry_status + hhs.form_status]);
//                                imgStatus.setImageResource(R.drawable.ic_close);
//                                break;
//                        }
//
//                        //Allow editing of refusal and non-contact
//                        btnAction.setText(R.string.label_btn_update);
//                        btnAction.setOnClickListener((View v) -> {
//                            Intent intent = new Intent(this, S1Activity.class);
//                            intent.putExtra(
//                                    Constants.Index.INTENT_EXTRA_FORM_CONTEXT
//                                    , new FormContext(
//                                            obj.pcode,
//                                            obj.srNo,
//                                            MetaManifest
//                                                    .getInstance()
//                                                    .getSectionNumberFromClass(S1Activity.class)
//                                    )
//                            );
//                            startActivity(intent);
//                        });
//
//                        if (hhs.form_status == 5) {
//                            btnAction.setText(R.string.label_btn_replaced);
//                            btnAction.setEnabled(false);
//                        }
//
//                        continue;
//                    }
//                } else {
//                    if (hhs.initial_entry_status != 0) {
//                        tvStatus.setText(MetaManifest.form_status[hhs.initial_entry_status]);
//                        imgStatus.setImageResource(R.drawable.ic_book);
//                        btnAction.setText(R.string.label_btn_resume);
//
//                        StaticUtils.getHandler().post(()->{
//                            toggleDetails(tableRow.findViewById(pk.gov.pbs.tds.R.id.header));
//                        });
//
//                        btnAction.setOnClickListener((View v) -> {
//                            FormContext fcs = mFormRepository
//                                    .getUtilsDao()
//                                    .getFCS(new FormContext(obj.pcode, obj.srNo));
//
//                            if (fcs == null) {
//                                fcs = new FormContext(obj.pcode, obj.srNo, 1);
//                            }
//
//                            if (fcs.getSection() == null)
//                                fcs.setSection(MetaManifest.getInstance().getSectionNumberFromClass(FormMenuActivity.class));
//
//                            Intent intent = new Intent(this, MetaManifest
//                                    .getInstance().getSection(fcs.getSection()));
//
//                            intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, fcs);
//                            startActivity(intent);
//                        });
//                        continue;
//                    }
//                }
//            }
//
//            //Form has not been opened because there is no household status
//            tvStatus.setText(MetaManifest.form_status[0]); //form not opened
//            imgStatus.setImageResource(R.drawable.ic_pending);
//
//            btnAction.setOnClickListener((View v)->{
//                Intent intent = new Intent(this, FormBeginnerActivity.class);
//                intent.putExtra(Constants.Index.INTENT_EXTRA_FORM_CONTEXT, new FormContext(obj.pcode, obj.srNo));
//                startActivity(intent);
//            });
//        }
//    }

//    public void toggleDetails(View view) {
//        ViewGroup container = (ViewGroup) view.getParent().getParent();
//
//        ViewGroup viewGroup = container.findViewById(pk.gov.pbs.tds.R.id.container_content);
//        ViewGroup btns = container.findViewById(pk.gov.pbs.tds.R.id.container_buttons);
//        if (viewGroup.getVisibility() == View.VISIBLE) {
//            viewGroup.setVisibility(View.GONE);
//            btns.setVisibility(View.GONE);
//        } else {
//            viewGroup.setVisibility(View.VISIBLE);
//            btns.setVisibility(View.VISIBLE);
//        }
//
//    }
}