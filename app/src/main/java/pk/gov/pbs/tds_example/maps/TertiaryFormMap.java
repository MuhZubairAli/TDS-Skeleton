package pk.gov.pbs.tds_example.maps;

import android.text.InputType;

import pk.gov.pbs.formbuilder.core.QuestionnaireMap;

public class TertiaryFormMap extends QuestionnaireMap {
    @Override
    protected void initQuestions(QuestionnaireBuilder builder) throws Exception {
        insertQuestion(builder.makeKBI("sno", InputType.TYPE_CLASS_NUMBER));
        insertQuestion(builder.makeKBI("name", InputType.TYPE_CLASS_TEXT));
        insertQuestion(builder.makeKBI("age", InputType.TYPE_CLASS_NUMBER));
    }
}
