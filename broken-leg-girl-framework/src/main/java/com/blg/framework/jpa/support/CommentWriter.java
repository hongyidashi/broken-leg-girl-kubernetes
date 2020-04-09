package com.blg.framework.jpa.support;

import com.blg.framework.Java;
import org.hibernate.Session;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.persister.entity.SingleTableEntityPersister;
import org.hibernate.persister.walking.spi.AttributeDefinition;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.HashMap;
import java.util.Map;

/**
 * 注释初始化器
 * @author lujijiang
 */
public class CommentWriter implements ApplicationListener<ContextRefreshedEvent> {

    private boolean done;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if(!done){
            done = true;
            // 后台线程处理，不影响启动时间
            new Thread(){
                public void run(){
                    EntityManagerFactory entityManagerFactory = event.getApplicationContext().getBean(EntityManagerFactory.class);
                    EntityManager entityManager = entityManagerFactory.createEntityManager();
                    EntityTransaction transaction = entityManager.getTransaction();
                    transaction.begin();
                    try{
                        MetamodelImplementor metamodel = (MetamodelImplementor)entityManagerFactory.getMetamodel();
                        Map<String, EntityPersister> persisterMap = metamodel.entityPersisters();
                        for(EntityPersister entityPersister : persisterMap.values()){
                            SingleTableEntityPersister persister = (SingleTableEntityPersister)entityPersister;
                            writeTableComment(persister,entityManager);
                            writeColumnsComment(persister,entityManager);
                        }
                        transaction.commit();
                    }catch (Throwable e){
                        transaction.rollback();
                        throw Java.unchecked(e);
                    }
                    finally {
                        entityManager.close();
                    }
                }
            }.start();
        }
    }

    private void writeColumnsComment(SingleTableEntityPersister persister, EntityManager entityManager) throws IntrospectionException {
        Class<?> mappedClass = persister.getMappedClass();
        PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(mappedClass).getPropertyDescriptors();
        Map<String,PropertyDescriptor> propertyDescriptorMap = new HashMap<>();
        for(PropertyDescriptor propertyDescriptor:propertyDescriptors){
            propertyDescriptorMap.put(propertyDescriptor.getName(),propertyDescriptor);
        }
        Iterable<AttributeDefinition> attributes = persister.getAttributes();
        for(AttributeDefinition attr : attributes){
            String propertyName = attr.getName(); //在entity中的属性名称
            PropertyDescriptor property = propertyDescriptorMap.get(propertyName);
            if(property==null){
                continue;
            }
            Comment comment = getComment(mappedClass, propertyName, property);
            if(comment==null){
                continue;
            }
            String[] columnNames = persister.getPropertyColumnNames(propertyName); //对应数据库表中的字段名
            String remark = comment.value();
            writeColumnComment(persister, entityManager, columnNames, remark);
        }
        {
            String propertyName = persister.getIdentifierPropertyName();
            PropertyDescriptor property = propertyDescriptorMap.get(propertyName);
            if(property==null){
                return;
            }
            Comment comment = getComment(mappedClass, propertyName, property);
            if(comment==null){
                return;
            }
            String[] columnNames = persister.getIdentifierColumnNames(); //对应数据库表中的字段名
            String remark = comment.value();
            writeColumnComment(persister, entityManager, columnNames, remark);
        }
    }

    private Comment getComment(Class<?> mappedClass, String propertyName, PropertyDescriptor property) {
        Comment comment = property.getReadMethod().getAnnotation(Comment.class);
        if(comment==null){
            Class cls = mappedClass;
            while(!cls.equals(Object.class)){
                try {
                    comment = cls.getDeclaredField(propertyName).getAnnotation(Comment.class);
                    break;
                } catch (NoSuchFieldException e) {
                    cls = cls.getSuperclass();
                    continue;
                }
            }
        }
        return comment;
    }

    private void writeColumnComment(SingleTableEntityPersister persister, EntityManager entityManager, String[] columnNames, String remark) {
        entityManager.unwrap(Session.class).doWork(connection->{
            try{
                for(String columnName:columnNames){
                    String schemeName = connection.getCatalog();
                    String tableName = persister.getTableName();
                    String sql = "SELECT CONCAT_WS(' ',COLUMNS.COLUMN_TYPE,IF(COLUMNS.IS_NULLABLE = 'NO','NOT NULL',''),COLUMNS.COLUMN_DEFAULT) FROM information_schema.COLUMNS "
                            + "where COLUMNS.TABLE_SCHEMA like ? " + "and COLUMNS.TABLE_NAME = ? "
                            + "and COLUMNS.COLUMN_NAME = ?";
                    Query query = entityManager.createNativeQuery(sql);
                    query.setParameter(1,schemeName);
                    query.setParameter(2,tableName);
                    query.setParameter(3,columnName);
                    String columnDefinition = (String) query.getSingleResult();
                    if(columnDefinition==null){
                        return;
                    }
                    sql = "ALTER TABLE "+schemeName+"."+tableName+" MODIFY COLUMN "+columnName+" "+columnDefinition+" COMMENT ?";
                    query = entityManager.createNativeQuery(sql);
                    query.setParameter(1,remark);
                    query.executeUpdate();
                }
            }catch (Throwable e){
                throw Java.unchecked(e);
            }
        });
    }

    private void writeTableComment(AbstractEntityPersister persister, EntityManager entityManager) {
        Class<?> targetClass = persister.getMappedClass();
        Comment comment = targetClass.getAnnotation(Comment.class);
        if(comment!=null){
            String tableName = persister.getTableName();
            String tableComment = comment.value();
            String sql = "ALTER TABLE "+tableName+" COMMENT ?";
            Query query = entityManager.createNativeQuery(sql);
            query.setParameter(1,tableComment);
            query.executeUpdate();
        }
    }
}
