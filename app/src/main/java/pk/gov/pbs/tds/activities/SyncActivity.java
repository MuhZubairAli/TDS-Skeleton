package pk.gov.pbs.tds.activities;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.formbuilder.database.FormBuilderRepository;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.R;
import pk.gov.pbs.tds.database.DataProvider;
import pk.gov.pbs.tds.database.FormRepository;
import pk.gov.pbs.tds.models.online.ImportResponse;
import pk.gov.pbs.tds.models.online.SyncResponse;
import pk.gov.pbs.utils.StaticUtils;
import pk.gov.pbs.utils.UXEvent;
import pk.gov.pbs.utils.web.GsonWebRequest;

public class SyncActivity extends ThemedCustomActivity {

    RequestQueue webRequestQueue;
    FormBuilderRepository mFormBuilderRepo;
    FormRepository mFormRepo;
    Button btnImport, btnImportGeo, btnUpload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_sync);
        setActivityTitle("Poultry Farms | Data Import & Export", "Data Management");
        init();
    }

    private void init(){
        webRequestQueue = Volley.newRequestQueue(this);
        mFormBuilderRepo = FormBuilderRepository.getInstance(getApplication());
        mFormRepo = FormRepository.getInstance(getApplication());

        btnImport = findViewById(R.id.btn_import);
        btnImport.setOnClickListener(this::importAssignment);

        btnUpload = findViewById(R.id.btn_upload);
        btnUpload.setOnClickListener(this::upload);

        findViewById(R.id.btn_backup).setOnClickListener(view -> {
            startActivity(new Intent(SyncActivity.this, BackupActivity.class));
            finish();
        });

        populateImportedBlocksTable();
    }

    private void upload(View btn){
//        final List<Map<String,List<?>>> allData = DataProvider.getUnsyncedClosedForms(mFormRepo);
//        if (allData == null || allData.isEmpty()){
//            mUXToolkit.alert("No Data", "There is no form ready <small>(Refused, Non-Contacted or any other status)</small> in any block to upload.");
//            return;
//        }
//
//        mUXToolkit.confirm(
//                "Upload Data",
//                "Are you sure you want to upload all of the closed forms to server? it will include forms marked <u>Refused</u> and <u>Non-Contacted</u>. Once data is synced you won't be able to Update any form.",
//                new UXEvent.ConfirmDialogue() {
//                    @Override
//                    public void onCancel(DialogInterface dialog, int which) {
//                    }
//
//                    @Override
//                    public void onOK(DialogInterface dialog, int which) {
//                        mUXToolkit.showProgressDialogue("Uploading data...");
//                        GsonWebRequest<SyncResponse[]> geoRequest = new GsonWebRequest<>(
//                                Request.Method.POST
//                                , CustomApplication.getHostWebAPI() + "sync_entries_multi.php/"
//                                , StaticUtils.getSimpleGson().toJson(allData).getBytes(StandardCharsets.UTF_8)
//                                , SyncResponse[].class
//                                , response -> {
//                            if (response.length > 0) {
//                                List<String> ok = new ArrayList<>();
//                                List<String> failed = new ArrayList<>();
//
//                                for (SyncResponse sr : response) {
//                                    if (sr.status == 1){
//                                        for (String tbl : sr.ids.keySet()) {
//                                            int[][] ids = sr.ids.get(tbl);
//                                            if (ids != null) {
//                                                for (int[] id : ids) {
//                                                    ContentValues cv = new ContentValues();
//                                                    cv.put("sid", id[1]);
//                                                    mFormRepo.getDatabase().getWritableDatabase()
//                                                            .update(tbl, cv, "aid = ?", new String[]{String.valueOf(id[0])});
//                                                }
//                                            }
//                                        }
//                                        ok.add("SR-"+sr.message);
//                                    } else {
//                                        failed.add("<u><b>SR-"+sr.message+"</b></u> : "+sr.backtrace);
//                                    }
//                                }
//
//                                StaticUtils.getHandler().post(()->{
//                                    mUXToolkit.dismissProgressDialogue();
//                                    if (ok.size() == response.length){ //all ok
//                                        String msg = "Request is completed successfully.<br /><br />" +
//                                                "<b>Below Forms are synced</b><br />" +
//                                                String.join(", ", ok);
//                                        mUXToolkit.alert("Operation Successful", msg);
//                                    } else if (!ok.isEmpty() && !failed.isEmpty()){ //some ok
//                                        String msg = "Request is partially successful. <br /><br />" +
//                                                "<b>Below Forms are synced</b><br />" +
//                                                String.join(", ", ok) +
//                                                "<br /><br />" +
//                                                "<b>Below Forms failed to sync</b><br />" +
//                                                String.join("<br />", failed);
//                                        mUXToolkit.alert("Operation Partially Completed", msg);
//                                    } else if (ok.isEmpty() && failed.size() == response.length) { //some failed
//                                        mUXToolkit.alert(
//                                                "Operation Failed",
//                                                "Failed to sync any form. Reason(s) of failure specified below <br /></br />" +
//                                                        String.join("<br />", failed)
//                                        );
//                                    }
//                                });
//                            } else {
//                                StaticUtils.getHandler().post(()->{
//                                    mUXToolkit.dismissProgressDialogue();
//                                    mUXToolkit.alert("No Response", "No data returned from server, Kindly make sure if there is any candidate block in which any form is closed but not synced.");
//                                });
//                            }
//                        }
//                                , error -> {
//                            StaticUtils.getHandler().post(() -> {
//                                mUXToolkit.dismissProgressDialogue();
//                                if (error.getMessage() != null && error.getMessage().contains("timeout")) {
//                                    mUXToolkit.alert(
//                                            "Connection Timeout"
//                                            , "Please check your internet connection and try again."
//                                    );
//                                } else if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")) {
//                                    mUXToolkit.alert(
//                                            "No Internet Connection"
//                                            , "Please check your internet connection and try again."
//                                    );
//                                } else {
//                                    mUXToolkit.alert(
//                                            "Request Failed",
//                                            "err code: " + error.toString() + " | " + String.valueOf(error.getMessage())
//                                    );
//                                }
//                            });
//                        }
//                        );
//                        geoRequest.setSecurityToken(CustomApplication.getLoginPayload().token);
//                        webRequestQueue.add(geoRequest);
//                    }
//                }
//        );

    }

    private void importAssignment(View btn){
//        mUXToolkit.showProgressDialogue("Importing Blocks...");
//        //----------------------------------------------------------------------------
//        //                  ASSIGNMENT FETCH REQUEST
//        //----------------------------------------------------------------------------
//
//        // http://localhost/api_pf/import_assignments.php
//        String url = CustomApplication.makeWebApiUrl("import_assignments.php");
//        GsonWebRequest<ImportResponse> assignmentRequest = new GsonWebRequest<>(
//                Request.Method.GET
//                , url
//                , null
//                , ImportResponse.class
//                , response -> {
//                    final int[] receivedCount = {0};
//                    if (response != null && response.data != null){
//                        if (response.status == 1) {
//                            Establishment[] result = StaticUtils.getGson().fromJson(response.data, Establishment[].class);
//                            receivedCount[0] += result.length;
//                            if (result.length > 0) {
//                                List<Establishment> unique = new ArrayList<>();
//                                List<String> blockCodes = new ArrayList<>();
//
//                                for (Establishment pcd : result) {
//                                    if (!blockCodes.contains(pcd.getEBCode())) {
//                                        unique.add(pcd);
//                                        blockCodes.add(pcd.getEBCode());
//                                    }
//                                }
//
//                                mFormRepo.getDatabase().getWritableDatabase().delete(
//                                        Establishment.class.getSimpleName(),
//                                        "status=?",
//                                        new String[]{"1"}
//                                );
//
//                                for (Establishment estb : result)
//                                    mFormRepo.getDatabase().insert(estb);
//
//                                StaticUtils.getHandler().post(() -> {
//                                    mUXToolkit.dismissProgressDialogue();
//                                    mUXToolkit.alert("Operation Successful", String.valueOf(receivedCount[0]) + " assignments fetched from server");
//                                });
//                                showImportedBlocksInTable(unique);
//                            }
//
//                        } else {
//                            StaticUtils.getHandler().post(() -> {
//                                mUXToolkit.dismissProgressDialogue();
//                                mUXToolkit.alert("Failed to import assignments", "err: "+ String.valueOf(response.message) + " | " + String.valueOf(response.backtrace));
//                            });
//                        }
//
//                    } else {
//                        StaticUtils.getHandler().post(() -> {
//                            mUXToolkit.dismissProgressDialogue();
//                            mUXToolkit.alert("No New Assignment", "No assignment received from server, Kindly contact supervisor for assignments.");
//                        });
//                    }
//                }
//                , error -> {
//                    StaticUtils.getHandler().post(()->{
//                        mUXToolkit.dismissProgressDialogue();
//                        if (error.getMessage() != null && error.getMessage().contains("timeout")) {
//                            mUXToolkit.alert(
//                                    "Connection Timeout"
//                                    , "Please check your internet connection and try again."
//                            );
//                        }
//                        else if (error.getMessage() != null && error.getMessage().contains("Unable to resolve host")) {
//                            mUXToolkit.alert(
//                                    "No Internet Connection"
//                                    , "Please check your internet connection and try again."
//                            );
//                        } else {
//                            mUXToolkit.alert(
//                                    "Failed to import assignments",
//                                    "err code: " + (error.networkResponse != null ? error.networkResponse.statusCode : String.valueOf(error.networkResponse)) + " | " + String.valueOf(error.getMessage())
//                            );
//                        }
//                    });
//                }
//        );
//
//        assignmentRequest.setSecurityToken(CustomApplication.getLoginPayload().getToken());
//        webRequestQueue.add(assignmentRequest);
    }

    private void populateImportedBlocksTable(){
//        List<Establishment> blocks = mFormRepo.getDatabase().query(Establishment.class);
//
//        if (blocks.isEmpty())
//            return;
//
//        List<Establishment> unique = new ArrayList<>();
//        List<String> blockCodes = new ArrayList<>();
//
//        for (Establishment pcd : blocks) {
//            if (!blockCodes.contains(pcd.getEBCode())) {
//                unique.add(pcd);
//                blockCodes.add(pcd.getEBCode());
//            }
//        }
//
//        TableLayout container = findViewById(R.id.table_hh_data);
//        for (Establishment assignment : unique){
//            TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.item_row_imported_blocks, container, false);
//            ((TextView) tr.findViewById(R.id.td_bc)).setText(assignment.getEBCode());
//            ((TextView) tr.findViewById(R.id.td_assign_date)).setText(String.valueOf(assignment.DAssigned));
//            ((TextView) tr.findViewById(R.id.td_start_date)).setText(String.valueOf(assignment.DBegin));
//            ((TextView) tr.findViewById(R.id.td_end_date)).setText(String.valueOf(assignment.DEnd));
//            container.addView(tr);
//        }
    }
//
//    private void showImportedBlocksInTable(List<Establishment> newAssignments){
//        TableLayout container = findViewById(R.id.table_hh_data);
//        for (Establishment pcd : newAssignments){
//            TableRow tr = (TableRow) getLayoutInflater().inflate(R.layout.item_row_imported_blocks, container, false);
//            ((TextView) tr.findViewById(R.id.td_bc)).setText(pcd.getEBCode());
//            ((TextView) tr.findViewById(R.id.td_assign_date)).setText(String.valueOf(pcd.DAssigned));
//            ((TextView) tr.findViewById(R.id.td_start_date)).setText(String.valueOf(pcd.DBegin));
//            ((TextView) tr.findViewById(R.id.td_end_date)).setText(String.valueOf(pcd.DEnd));
//            container.addView(tr);
//        }
//    }
}