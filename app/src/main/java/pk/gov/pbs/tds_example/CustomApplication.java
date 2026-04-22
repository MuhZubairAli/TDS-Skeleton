package pk.gov.pbs.tds_example;

import java.util.Calendar;

import pk.gov.pbs.formbuilder.core.IMetaManifest;
import pk.gov.pbs.tds.CustomApplicationBase;
import pk.gov.pbs.tds_example.meta.MetaManifest;
import pk.gov.pbs.utils.DateTimeUtil;

public class CustomApplication extends CustomApplicationBase {
    @Override
    public IMetaManifest _getMetaManifest() {
        return MetaManifest.getInstance();
    }

    protected String _getApplicationVersion(){
        return getInstance().getString(pk.gov.pbs.tds_example.R.string.app_version)
                .replace("(-)","("+ DateTimeUtil.getCalendar().get(Calendar.YEAR)+")");
    }

    protected int _getApplicationVersionCode(){
        return Integer.parseInt(
                getInstance().getString(R.string.app_version_code)
        );
    }
}
