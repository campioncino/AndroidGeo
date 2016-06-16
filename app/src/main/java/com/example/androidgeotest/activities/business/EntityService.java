package com.example.androidgeotest.activities.business;

import android.content.Context;
import android.util.Log;

import com.example.androidgeotest.activities.business.dao.EntityMapper;
import com.example.androidgeotest.activities.business.model.Entity;

import java.sql.SQLException;
import java.util.List;

public abstract class EntityService {

    private Context context;
    private EntityMapper mapper;
    private Class<? extends Entity> viewEntityClass;
    private Class<? extends Entity> tableEntityClass;

    protected abstract void initMapper();

    protected abstract void initViewEntityClass();

    protected abstract void initTableEntityClass();

    public EntityService(Context context) {
        this.setContext(context);
        initViewEntityClass();
        initTableEntityClass();
        initMapper();
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public EntityMapper getMapper() {
        return mapper;
    }

    public void setMapper(EntityMapper mapper) {
        this.mapper = mapper;
    }

    public Class<? extends Entity> getViewEntityClass() {
        return viewEntityClass;
    }

    public void setViewEntityClass(Class<? extends Entity> viewEntityClass) {
        this.viewEntityClass = viewEntityClass;
    }

    public Class<? extends Entity> getTableEntityClass() {
        return tableEntityClass;
    }

    public void setTableEntityClass(Class<? extends Entity> tableEntityClass) {
        this.tableEntityClass = tableEntityClass;
    }

    public List<Entity> getAll() throws CrudException {
        try {
            return getMapper().getAll();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public List<Entity> searchEquals(Entity entity) throws CrudException {
        try {
            return getMapper().searchEquals(entity);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    public void insert(Entity entity) throws CrudException {
        try {
           // beforeInsertValidation(entity);
            getMapper().insert(entity);
//        } catch (ValidationException e) {
//            e.printStackTrace();
//            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }
    public void insert(Entity entity, String prefix) throws CrudException {
        try {
            beforeInsertValidation(entity);
            getMapper().insert(entity, prefix);
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public void insert(List<Entity> list, String prefix) throws CrudException {
        try {
            for (Entity e : list) {
                beforeInsertValidation(e);
            }
            getMapper().insert(list, prefix);
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public void update(Entity entity, String prefix) throws CrudException {
        try {
            beforeUpdateValidation(entity);
            getMapper().update(entity, prefix);
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public void update(List<Entity> list, String prefix) throws CrudException {
        try {
            for (Entity e : list) {
                beforeUpdateValidation(e);
            }
            getMapper().update(list, prefix);
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public void delete(Entity entity, String prefix) throws CrudException {
        try {
            beforeDeleteValidation(entity);
            getMapper().delete(entity, prefix);
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public void delete(List<Entity> list, String prefix) throws CrudException {
        try {
            for (Entity e : list) {
                beforeDeleteValidation(e);
            }
            getMapper().delete(list, prefix);
        } catch (ValidationException e) {
            e.printStackTrace();
            throw new CrudException(e);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new CrudException(e);
        }
    }

    public String getContentItemType() {
        return getTableEntityClass().getName();
    }

    public void beforeInsertValidation(Entity entity)
            throws ValidationException, CrudException {
    }

    public void beforeUpdateValidation(Entity entity)
            throws ValidationException, CrudException {
    }

    public void beforeDeleteValidation(Entity entity)
            throws ValidationException, CrudException {
    }

}
