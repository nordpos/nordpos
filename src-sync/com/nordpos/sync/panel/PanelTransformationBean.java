package com.nordpos.sync.panel;

import com.openbravo.basic.BasicException;
import com.openbravo.data.loader.BaseSentence;
import com.openbravo.data.loader.Datas;
import com.openbravo.data.loader.QBFBuilder;
import com.openbravo.data.loader.SerializerReadBasic;
import com.openbravo.data.loader.StaticSentence;
import com.openbravo.data.user.EditorCreator;
import com.openbravo.pos.forms.AppLocal;
import com.openbravo.pos.forms.AppView;
import com.openbravo.pos.forms.BeanFactoryException;
import java.util.ArrayList;
import java.util.List;

public class PanelTransformationBean extends JPanelTransformation {

    private String title;
    private String transformation;

    private String resourcebundle = null;

    private String sentence;

    private final List<Datas> fielddatas = new ArrayList<>();
    private final List<String> fieldnames = new ArrayList<>();

    private final List<String> paramnames = new ArrayList<>();

//    private JParamsComposed qbffilter = new JParamsComposed();
    @Override
    public void init(AppView app) throws BeanFactoryException {

//        qbffilter.init(app);       
        super.init(app);
    }

    @Override
    public void activate() throws BasicException {

//        qbffilter.activate();
        super.activate();

//        if (qbffilter.isEmpty()) {
        setVisibleFilter(false);
        setVisibleButtonFilter(false);
////        }
    }

    @Override
    protected EditorCreator getEditorCreator() {

//        return qbffilter;
        return null;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTitleKey(String titlekey) {
        title = AppLocal.getIntString(titlekey);
    }

    public String getTitle() {
        return title;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    protected String getTransformation() {
        return transformation;
    }

    public void setResourceBundle(String resourcebundle) {
        this.resourcebundle = resourcebundle;
    }

    protected String getResourceBundle() {
        return resourcebundle == null
                ? transformation
                : resourcebundle;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    public void addField(String name, Datas data) {
        fieldnames.add(name);
        fielddatas.add(data);
    }

    public void addParameter(String name) {
        paramnames.add(name);
    }

    protected BaseSentence getSentence() {
        return new StaticSentence(m_App.getSession(), new QBFBuilder(sentence, paramnames.toArray(new String[paramnames.size()])) //            , qbffilter.getSerializerWrite()
                , null, new SerializerReadBasic(fielddatas.toArray(new Datas[fielddatas.size()])));
    }

//    protected ReportFields getReportFields() {
//        return new ReportFieldsArray(fieldnames.toArray(new String[fieldnames.size()]));
//    }       
//    
//    public void addQBFFilter(ReportEditorCreator qbff) {
//        qbffilter.addEditor(qbff);
//    }    
}
