package com.commonsware.todo;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.auto.value.AutoValue;
import com.google.auto.value.extension.memoized.Memoized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


@AutoValue
public abstract class ViewState {
    public abstract List<ToDoModel> items();
    public abstract boolean isLoaded();
    public abstract FilterMode filterMode();

    @Nullable public abstract ToDoModel current();

    static Builder builder(){
        return new AutoValue_ViewState.Builder().isLoaded(false)
                .filterMode(FilterMode.ALL);
    }

    @AutoValue.Builder
    abstract static class Builder{
        abstract Builder items(List<ToDoModel> items);
        abstract Builder current(ToDoModel current);
        abstract Builder isLoaded(boolean isLoaded);
        abstract ViewState build();
        abstract Builder filterMode(FilterMode filterMode);
    }

    static Builder empty(){
        return builder().items(Collections.unmodifiableList(new ArrayList<>()));
    }

    Builder toBuilder(){
        return builder().items(items()).current(current())
                .isLoaded(isLoaded())
                .filterMode(filterMode());
    }

    ViewState add(ToDoModel model){
        List<ToDoModel> models = new ArrayList<>();
        models.add(model);
        sort(models);
        return toBuilder().
                items(Collections.unmodifiableList(models)).
                current(model).build();
    }

    private void sort(List<ToDoModel> models) {
        Collections.sort(models, ToDoModel.SORT_BY_DEC);
    }

//    private ViewState foldResultIntoState(@NonNull ViewState state,
//                                          @NonNull Result result){
//        if(result instanceof Result.Added){
//            return state.add(((Result.Added)result).model());
//        } else{
//            throw new IllegalStateException("Unexpected result type: "+result.toString());
//        }
//    }

    ViewState modify(ToDoModel model){
        List<ToDoModel> models = new ArrayList<>(items());
        ToDoModel original = find(models, model.id());

        if(original != null){
            int index = models.indexOf(original);
            models.set(index, model);
        }
        sort(models);
        return toBuilder().items(Collections.unmodifiableList(models))
                .current(model).build();
    }

    private ToDoModel find(List<ToDoModel> models, String id) {
        int position=findPosition(models, id);

        return position>=0 ? models.get(position) : null;
    }

    private int findPosition(List<ToDoModel> models, String id) {
        for (int i=0;i<models.size();i++) {
            ToDoModel candidate=models.get(i);

            if (id.equals(candidate.id())) {
                return i;
            }
        }

        return -1;
    }

    ViewState delete(ToDoModel model) {
        List<ToDoModel> models=new ArrayList<>(items());
        ToDoModel original=find(models, model.id());

        if (original==null) {
            throw new IllegalArgumentException("Cannot find model to delete: "+model.toString());
        }
        else {
            models.remove(original);
        }

        sort(models);

        return toBuilder()
                .items(Collections.unmodifiableList(models))
                .current(null)
                .build();
    }

    ViewState show(ToDoModel current){
        return toBuilder().current(current()).build();
    }

    ViewState filter(FilterMode mode){
        return (toBuilder().filterMode(mode).build());
    }

    @Memoized
    public List<ToDoModel> filteredItems(){
        return (ToDoModel.filter(items(), filterMode()));
    }


}

