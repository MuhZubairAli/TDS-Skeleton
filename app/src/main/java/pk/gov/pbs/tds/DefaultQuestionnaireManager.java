package pk.gov.pbs.tds;

import pk.gov.pbs.formbuilder.core.ActivitySection;
import pk.gov.pbs.formbuilder.core.QuestionnaireManager;
import pk.gov.pbs.formbuilder.models.PrimaryModel;
import pk.gov.pbs.utils.ExceptionReporter;

public class DefaultQuestionnaireManager<T extends PrimaryModel> extends QuestionnaireManager<T> {
    Class<T> modelClass;

    public DefaultQuestionnaireManager(ActivitySection context, Class<T> modelClass) {
        super(context);
        this.modelClass = modelClass;
    }

    @Override
    public T exportPrimaryModel() {
        T model = null;
        try {
            model = modelClass.newInstance();
            model = super.fillModel(model);
            model.operatorId = CustomApplication.getLoginPayload().getUserName();
        } catch (Exception e) {
            ExceptionReporter.handle(e);
        }
        return model;
    }
}
