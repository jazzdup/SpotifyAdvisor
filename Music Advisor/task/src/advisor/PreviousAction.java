package advisor;

public class PreviousAction extends AbstractAction {
    public PreviousAction(ActionConfig actionConfig) {
        super(actionConfig);
    }

    @Override
    public Data execute(Data data) {
        int start = data.getStartIndex();
        int size = data.getItemsSize();
        if (start >  0) {
            data.setStartIndex(start - pageSize);
        }else{
            data.setError("No more pages");
        }
        return data;
    }

}
