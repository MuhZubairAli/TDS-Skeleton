package pk.gov.pbs.forms.maps;

import android.text.InputType;

import pk.gov.pbs.formbuilder.core.QuestionnaireMap;

public class SecondaryFormMap extends QuestionnaireMap {
    @Override
    protected void initQuestions(QuestionnaireBuilder builder) throws Exception {
        insertQuestion(builder.makeKBI("hhno", InputType.TYPE_CLASS_NUMBER));
        insertQuestion(builder.makeKBI("head", InputType.TYPE_CLASS_TEXT));
    }
}
