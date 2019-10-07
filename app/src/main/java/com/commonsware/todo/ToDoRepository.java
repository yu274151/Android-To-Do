package com.commonsware.todo;

import android.content.Context;
import java.util.ArrayList;
import java.util.List;


public class ToDoRepository {
    private static volatile ToDoRepository INSTANCE = null;
    private List<ToDoModel> items = new ArrayList<>();
    private final ToDoDatabase db;

    public synchronized static ToDoRepository get(Context ctx){
        if(INSTANCE==null){
            INSTANCE= new ToDoRepository(ctx.getApplicationContext());
        }
        return INSTANCE;
    }

    private ToDoRepository(Context ctx){
        db=ToDoDatabase.get(ctx);
    }

    //    ToDoRepository(){
//        items.add(ToDoModel.creator()
//                .description("Buy a copy of _Exploring Android_")
//                .notes("See https://wares.commonsware.com")
//                .isCompleted(true)
//                .build());
//        items.add(ToDoModel.creator()
//                .description("Complete all of the tutorials")
//                .build());
//        items.add(ToDoModel.creator()
//                .description("Write an app for somebody in my community")
//                .notes("Talk to some people at non-profit organizations to see what they need!")
//                .build());
//    }

    public List<ToDoModel> all(){

        List<ToDoEntity> entities = db.toDoStore().all();
        ArrayList<ToDoModel> result = new ArrayList<>(entities.size());
        for(ToDoEntity entity: entities){
            result.add(entity.toModel());
        }

        return result;
    }

    public void add(ToDoModel model){
        db.toDoStore().insert(ToDoEntity.fromModel(model));
    }

    public void replace(ToDoModel model){
        db.toDoStore().update(ToDoEntity.fromModel(model));
    }

    public void delete(ToDoModel model){
        db.toDoStore().delete(ToDoEntity.fromModel(model));
    }

//    public ToDoModel find(String id){
//        for(ToDoModel candidate: all()){
//            if(candidate.id().equals(id)){
//                return candidate;
//            }
//        }
//        return null;
//    }


}
