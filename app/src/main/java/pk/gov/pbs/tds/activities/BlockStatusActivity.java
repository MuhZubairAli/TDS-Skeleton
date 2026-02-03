package pk.gov.pbs.tds.activities;

import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.HashMap;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.FormRepository;

public class BlockStatusActivity extends ThemedCustomActivity {
    FormRepository repository;

    HashMap<String, Integer> bcFarmCount = new HashMap<>();
    HashMap<String, Integer> bcCompleteCount = new HashMap<>();
    HashMap<String, Integer> bcPendingCount = new HashMap<>();
    HashMap<String, Integer> bcRefusalCount = new HashMap<>();
    HashMap<String, Integer> bcNCCount = new HashMap<>();
    HashMap<String, Integer> bcStatus = new HashMap<>();
//    HashMap<String, Establishment> bcData = new HashMap<>();
    int completedBlockCount, syncedBlockCount, pendingBlockCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block_status);
        setActivityTitle("Poultry Farms | Block Status", "Detailed Information of all Assignments");
        repository = FormRepository.getInstance(getApplication());

        init();
        updateUI();
    }

    private void init(){
//        completedBlockCount = syncedBlockCount = pendingBlockCount = 0;
//        List<Establishment> result = repository.getAssignmentDao().getAllAssignments();
//
//        for (Establishment obj : result){
//            if(!bcData.containsKey(obj.getPCode().trim())){
//                bcData.put(obj.getPCode().trim(), obj);
//                int status = repository.getUtilsDao().determineBlockStatus(obj.getPCode());
//                if(status == Constants.Status.BLOCK_COMPLETED)
//                    completedBlockCount++;
//                else if (status == Constants.Status.BLOCK_SYNCED)
//                    syncedBlockCount++;
//                else if(status == Constants.Status.BLOCK_INCOMPLETE || status == Constants.Status.BLOCK_NOT_STARTED)
//                    pendingBlockCount++;
//                bcStatus.put(obj.getPCode(), status);
//                bcFarmCount.put(obj.getPCode().trim(), 1);
//            } else {
//                bcFarmCount.put(obj.getPCode().trim(), (bcFarmCount.get(obj.getPCode().trim()) + 1));
//            }
//
//            InformationModel info = repository.getDatabase().querySingle(
//                    InformationModel.class,
//                   "pcode=? AND srno=?",
//                    obj.getPCode(), String.valueOf(obj.id)
//            );
//
//            if(info == null || info.formStatus == null){
//                if(bcPendingCount.containsKey(obj.getPCode().trim())){
//                    bcPendingCount.put(
//                            obj.getPCode().trim()
//                            , (bcPendingCount.get(obj.getPCode().trim()) + 1));
//                }else {
//                    bcPendingCount.put(obj.getPCode().trim(), 1);
//                }
//            } else {
//                if(info.formStatus != null && info.formStatus == Constants.Status.FORM_COMPLETED){
//                    if(bcCompleteCount.containsKey(obj.getPCode().trim())){
//                        bcCompleteCount.put(
//                                obj.getPCode().trim()
//                                , (bcCompleteCount.get(obj.getPCode().trim()) + 1));
//                    }else {
//                        bcCompleteCount.put(obj.getPCode().trim(), 1);
//                    }
//                }
//
//                if(info.formStatus != null && info.formStatus == Constants.Status.FORM_REFUSED){
//                    if(bcRefusalCount.containsKey(obj.getPCode().trim())){
//                        bcRefusalCount.put(
//                                obj.getPCode().trim()
//                                , (bcRefusalCount.get(obj.getPCode().trim()) + 1));
//                    }else {
//                        bcRefusalCount.put(obj.getPCode().trim(), 1);
//                    }
//                }
//
//                if(info.formStatus != null && info.formStatus == Constants.Status.FORM_NON_CONTACTED){
//                    if(bcNCCount.containsKey(obj.getPCode().trim())){
//                        bcNCCount.put(
//                                obj.getPCode().trim()
//                                , (bcNCCount.get(obj.getPCode().trim()) + 1));
//                    }else {
//                        bcNCCount.put(obj.getPCode().trim(), 1);
//                    }
//                }
//
//            }
//        }

    }

    private void updateUI(){
//        ViewGroup stats = findViewById(R.id.container_big_numbers);
//
//        ((TextView) stats.findViewById(R.id.tv_completed_blocks)).setText(completedBlockCount + "");
//        ((TextView) stats.findViewById(R.id.tv_pending_blocks)).setText(pendingBlockCount + "");
//        ((TextView) stats.findViewById(R.id.tv_synced_blocks)).setText(syncedBlockCount + "");
//        ((TextView) stats.findViewById(R.id.tv_total_blocks)).setText(bcData.size() + "");
//
//        ViewGroup container = findViewById(R.id.container_block_items);
//        for (String bc : bcData.keySet()){
//            View blockDetails = getLayoutInflater().inflate(R.layout.item_expandable_block_status,container, false);
//            Establishment obj = bcData.get(bc);
//
//
////                    ((TextView) blockDetails.findViewById(R.id.tv_province)).setText(obj.getProvince());
////                    ((TextView) blockDetails.findViewById(R.id.tv_locality)).setText(Labels.region[obj.getUrban_Rural()]);
////                    ((TextView) blockDetails.findViewById(R.id.tv_district)).setText(obj.getDistrict());
////                    ((TextView) blockDetails.findViewById(R.id.tv_tehsile)).setText(obj.getTehsil());
//
//            ((TextView) blockDetails.findViewById(R.id.td_pcode)).setText(obj.getPCode());
//            ((TextView) blockDetails.findViewById(R.id.td_ebcode)).setText(obj.getEBCode());
//
//            int hhc = (bcFarmCount.get(obj.getPCode().trim()) == null) ? 0 : bcFarmCount.get(obj.getPCode().trim());
//            String hhCount = String.valueOf(hhc);
//            ((TextView) blockDetails.findViewById(R.id.td_total_hh)).setText(hhCount);
//
//            int pc = (bcPendingCount.get(obj.getPCode().trim()) == null) ? 0 : bcPendingCount.get(obj.getPCode().trim());
//            String pendingCount = String.valueOf(pc);
//            ((TextView) blockDetails.findViewById(R.id.td_pending_hh)).setText(pendingCount);
//
//            int cc = (bcCompleteCount.get(obj.getPCode().trim()) == null) ? 0 : bcCompleteCount.get(obj.getPCode().trim());
//            String completeCount = String.valueOf(cc);
//            ((TextView) blockDetails.findViewById(R.id.td_completed_household)).setText(completeCount);
//
//            int rc = (bcRefusalCount.get(obj.getPCode().trim()) == null) ? 0 : bcRefusalCount.get(obj.getPCode().trim());
//            String refusalCount = String.valueOf(rc);
//            ((TextView) blockDetails.findViewById(R.id.td_refusals_count)).setText(refusalCount);
//
//            int ncc = (bcNCCount.get(obj.getPCode().trim()) == null) ? 0 : bcNCCount.get(obj.getPCode().trim());
//            String ncCount = String.valueOf(ncc);
//            ((TextView) blockDetails.findViewById(R.id.td_non_contacts_count)).setText(ncCount);
//
//            ValueStore prcnt = new ValueStore((((float) cc / (float) hhc) * 100));
//            ((TextView) blockDetails.findViewById(R.id.td_coverage_percentage)).setText(prcnt + "%");
//
//            ((TextView) blockDetails.findViewById(R.id.td_address)).setText(obj.getAddress());
//
////                    ((TextView) blockDetails.findViewById(R.id.tv_assigned_date)).setText(obj.getAssignment_date());
////                    ((TextView) blockDetails.findViewById(R.id.tv_start_date)).setText(Utils.formatDate(obj.getAssigned_date()));
////                    ((TextView) blockDetails.findViewById(R.id.tv_end_date)).setText(Utils.formatDate(obj.getAssigned_date2()));
////                    ((TextView) blockDetails.findViewById(R.id.tv_block_status)).setText(Labels.block_status[bcStatus.get(bc)]);
//
//
//            //setting details from first assignment
//            ((TextView) blockDetails.findViewById(R.id.th_blockcode)).setText(obj.getEBCode());
//
//            String date = obj.DAssigned;
//            if (date != null) {
//                date = DateTimeUtil.formatDateTime(date, DateTimeUtil.sqlTimestampFormatWithoutT, DateTimeUtil.defaultDateOnlyFormat);
//                ((TextView) blockDetails.findViewById(R.id.td_assign_date)).setText(date);
//            }
//
//            date = obj.DBegin;
//            if (date != null) {
//                date = DateTimeUtil.formatDateTime(date, DateTimeUtil.sqlTimestampFormatWithoutT, DateTimeUtil.defaultDateOnlyFormat);
//                ((TextView) blockDetails.findViewById(R.id.td_start_date)).setText(date);
//                ((TextView) blockDetails.findViewById(R.id.th_deadline)).setText(date);
//            }
//
//            date = obj.DEnd;
//            if (date != null) {
//                date = DateTimeUtil.formatDateTime(date, DateTimeUtil.sqlTimestampFormatWithoutT, DateTimeUtil.defaultDateOnlyFormat);
//                ((TextView) blockDetails.findViewById(R.id.td_end_date)).setText(date);
//            }
//
//            int status = bcStatus.get(obj.getPCode());
//            ((TextView) blockDetails.findViewById(R.id.td_status)).setText(MetaManifest.block_status[status]);
//            ((TextView) blockDetails.findViewById(R.id.th_completed_hh)).setText(completeCount + "/" + hhCount);
//
//            container.addView(blockDetails);
//
//            if (CustomApplication.getLoginPayload().getSelectedBlock() != null
//                    && CustomApplication.getLoginPayload().getSelectedBlock().contains(obj.getEBCode())) {
//                StaticUtils.getHandler().post(()->{
//                    toggleDetails(blockDetails.findViewById(R.id.header));
//                });
//            }
//        }
    }

    public void toggleDetails(View view) {
        ViewGroup container = (ViewGroup) view.getParent();
        TransitionManager.beginDelayedTransition(container, new AutoTransition());

        ViewGroup viewGroup = container.findViewById(R.id.details);
        ImageView expand = container.findViewById(R.id.img_expand_icon);
        if (viewGroup.getVisibility() == View.VISIBLE) {
            viewGroup.setVisibility(View.GONE);
            expand.setImageResource(R.drawable.ic_arrow_circle_down);
        } else {
            viewGroup.setVisibility(View.VISIBLE);
            expand.setImageResource(R.drawable.ic_arrow_circle_up);
        }

    }
}