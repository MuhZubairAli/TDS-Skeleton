package pk.gov.pbs.forms.maps;

import android.text.InputType;

import pk.gov.pbs.formbuilder.core.QuestionnaireMap;

public class PrimaryFormMap extends QuestionnaireMap {
    @Override
    protected void initQuestions(QuestionnaireBuilder builder) throws Exception {
        insertQuestion(builder.makeKBI("pcode", InputType.TYPE_CLASS_NUMBER));
        insertQuestion(builder.makeKBI("address", InputType.TYPE_CLASS_TEXT));
    }
}
