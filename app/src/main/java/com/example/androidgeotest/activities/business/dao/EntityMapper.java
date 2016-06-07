package com.example.androidgeotest.activities.business.dao;

import android.content.Context;
import android.util.Log;

import com.example.androidgeotest.activities.business.model.Entity;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.table.DatabaseTableConfig;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;


public abstract class EntityMapper {

    private Context context;
    private Dao<Entity, Integer> viewDao;
    private Dao<Entity, Integer> tableDao;
    private Class<? extends Entity> viewEntityClass;
    private String tableName;
    private String viewName;


    public EntityMapper(Context context, Class<? extends Entity> entityClass) {
        this.setContext(context);
        this.setViewEntityClass(entityClass);

        initViewDao(context);
        initTableDao(context);
        tableName = DatabaseTableConfig.extractTableName(tableDao
                .getDataClass());
        viewName = DatabaseTableConfig.extractTableName(viewDao.getDataClass());
    }

    public abstract void initViewDao(Context context);

    public abstract void initTableDao(Context context);

    public void insert(final Entity entity, final String prefix)
            throws SQLException {
        Log.d("EntityMapper", "insert");
        // TODO GESTIRE L'ERRORE
        if (entity.isValid()) {
            TransactionManager.callInTransaction(
                    tableDao.getConnectionSource(), new Callable<Void>() {
                        public Void call() throws Exception {
                            tableDao.create(entity);

                            return null;
                        }
                    });
        }
    }
    public void insert(final Entity entity)
            throws SQLException {
        Log.d("EntityMapper", "insert");
        // TODO GESTIRE L'ERRORE
        if (entity.isValid()) {
            TransactionManager.callInTransaction(
                    tableDao.getConnectionSource(), new Callable<Void>() {
                        public Void call() throws Exception {
                            tableDao.create(entity);

                            return null;
                        }
                    });
        }
    }
    public void insert(final List<Entity> list, final String prefix)
            throws SQLException {
        try {
            tableDao.callBatchTasks(new Callable<Void>() {
                public Void call() throws SQLException {
                    for (Entity e : list) {
                        try {
                            tableDao.create(e);

                        } catch (SQLException e1) {
                            throw e1;
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void update(final Entity entity, final String prefix)
            throws SQLException {
        Log.d("EntityMapper", "update");
        // TODO SE VUOI GESTIRE L'ERRORE
        if (entity.isValid()) {
            TransactionManager.callInTransaction(
                    tableDao.getConnectionSource(), new Callable<Void>() {
                        public Void call() throws Exception {
                            tableDao.update(entity);

                            return null;
                        }
                    });
        }

    }

    public void update(final List<Entity> list, final String prefix) throws SQLException {
        try {
            tableDao.callBatchTasks(new Callable<Void>() {
                public Void call() throws SQLException {
                    for (Entity e : list) {
                        try {
                            tableDao.update(e);

                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public void delete(final Entity entity, final String prefix)
            throws SQLException {
        TransactionManager.callInTransaction(tableDao.getConnectionSource(),
                new Callable<Void>() {
                    public Void call() throws Exception {
                        try {
                            tableDao.delete(entity);

                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                });
    }

    public void delete(final List<Entity> list, final String prefix)
            throws SQLException {
        try {
            tableDao.callBatchTasks(new Callable<Void>() {
                public Void call() throws SQLException {
                    for (Entity e : list) {
                        try {
                            tableDao.delete(e);

                        } catch (SQLException e1) {
                            e1.printStackTrace();
                            Log.d(this.getClass().getSimpleName(),"ERRRRRR");
//                            System.out.println("ERRRRRRRRR");
                            throw e1;
                        }
                    }
                    return null;
                }
            });
        } catch (Exception e) {
            throw new SQLException(e.getMessage());
        }
    }

    public List<Entity> searchEquals(Entity entity) throws SQLException {
        return viewDao.queryForMatching(entity);
    }


    @SuppressWarnings("unchecked")
    public List<Entity> getAll() throws SQLException {
        return viewDao.queryForAll();
    }





    @SuppressWarnings("rawtypes")
    public Dao getViewDao() {
        return viewDao;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setWiewDao(Dao viewDao) {
        this.viewDao = viewDao;
    }

    @SuppressWarnings("rawtypes")
    public Dao getTableDao() {
        return tableDao;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public void setTableDao(Dao tableDao) {
        this.tableDao = tableDao;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public Class<? extends Entity> getViewEntityClass() {
        return viewEntityClass;
    }

    public void setViewEntityClass(Class<? extends Entity> viewEntityClass) {
        this.viewEntityClass = viewEntityClass;
    }


}
