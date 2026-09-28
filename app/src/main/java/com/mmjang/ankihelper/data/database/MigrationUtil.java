package com.mmjang.ankihelper.data.database;

import androidx.annotation.NonNull;

import com.mmjang.ankihelper.data.history.History;
import com.mmjang.ankihelper.data.history.HistoryPOJO;
import com.mmjang.ankihelper.data.plan.OutputPlan;
import com.mmjang.ankihelper.data.plan.OutputPlanPOJO;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MigrationUtil {
    public static boolean needMigration(){
        List<OutputPlan> oldPlan = DataSupport.findAll(OutputPlan.class);
        List<History> oldHistory = DataSupport.findAll(History.class);
        if(oldPlan.size() == 0 && oldHistory.size() == 0){
            return false;
        }else{
            return true;
        }
    }

    public static void migrate(){
        List<OutputPlan> oldPlan = DataSupport.findAll(OutputPlan.class);
        if(oldPlan.size() > 0) {
            List<OutputPlanPOJO> newPlan = new ArrayList<>();
            for (OutputPlan outputPlan : oldPlan) {
                OutputPlanPOJO np = new OutputPlanPOJO();
                np.setFieldsMap(outputPlan.getFieldsMap());
                np.setOutputModelId(outputPlan.getOutputModelId());
                np.setOutputDeckId(outputPlan.getOutputDeckId());
                np.setDictionaryKey(outputPlan.getDictionaryKey());
                np.setPlanName(outputPlan.getPlanName());
                newPlan.add(np);
            }
            ExternalDatabase.getInstance().refreshPlanWith(newPlan);
            DataSupport.deleteAll(OutputPlan.class);
        }
        List<History> oldHistory = DataSupport.findAll(History.class);
        List<HistoryPOJO> newHistory = new ArrayList<>();
        for(History history : oldHistory){
            HistoryPOJO hpojo = new HistoryPOJO();
            hpojo.setWord(history.getWord());
            hpojo.setType(history.getType());
            hpojo.setSentence(history.getSentence());
            hpojo.setNote(history.getNote());
            hpojo.setDictionary(history.getDictionary());
            hpojo.setDefinition(history.getDefinition());
            hpojo.setTimeStamp(history.getTimeStamp());
            newHistory.add(hpojo);
        }
        ExternalDatabase.getInstance().insertManyHistory(newHistory);
        DataSupport.deleteAll(History.class);
    }
}
