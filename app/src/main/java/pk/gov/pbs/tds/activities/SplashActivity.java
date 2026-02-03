package pk.gov.pbs.tds.activities;

import android.content.Intent;
import android.os.Bundle;

import pk.gov.pbs.formbuilder.core.ThemedCustomActivity;
import pk.gov.pbs.tds.CustomApplication;
import pk.gov.pbs.tds.databinding.ActivitySplashBinding;
import pk.gov.pbs.utils.StaticUtils;

public class SplashActivity extends ThemedCustomActivity {
    ActivitySplashBinding binding;
    Runnable permissionsChecker = () -> {
        if (hasAllPermissions()) {
            gotoNextActivity();
        } else {
            schedulePermissionCheck();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        if (Constants.DEBUG_MODE) {
//            gotoNextActivity();
//            return;
//        }

        hideActionBar();
        hideSystemControls();

        binding= ActivitySplashBinding.inflate(this.getLayoutInflater());
        setContentView(binding.getRoot());

        binding.copyrightVersion.setText(
                CustomApplication.getApplicationVersion()
        );
        binding.txtVersion.setText(
                CustomApplication.getApplicationVersion()
        );
        schedulePermissionCheck();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();

        binding.imgLogo.animate().setStartDelay(500).setDuration(1000).alpha(1f).translationY(50f).start();
        binding.txtTitle.animate().setStartDelay(500).setDuration(1000).alpha(1f).translationY(-50f).start();
        binding.txtVersion.animate().setStartDelay(1000).setDuration(1000).alpha(1f).start();
        binding.footer.animate().setStartDelay(1000).setDuration(1000).alpha(1f).start();
        binding.imgMinistryLogo.animate().setStartDelay(2000).setDuration(2000).alpha(1f).start();
        binding.progressCircular.animate().setStartDelay(2000).setDuration(1000).alpha(1f).start();

    }

    private void schedulePermissionCheck(){
        StaticUtils.getHandler().postDelayed(permissionsChecker, 5000);
    }

    private void gotoNextActivity(){
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}