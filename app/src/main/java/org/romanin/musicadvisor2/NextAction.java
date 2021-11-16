package org.romanin.musicadvisor2;

public class NextAction extends AbstractAction {
    public NextAction(ActionConfig actionConfig) {
        super(actionConfig);
    }

    @Override
    public Data execute(Data data) {
        int start = data.getStartIndex();
        int size = data.getItemsSize();
        if (pageSize + start < size) {
            data.setStartIndex(start + pageSize);
        }else{
            data.setError("No more pages");
        }
        return data;
    }

}
