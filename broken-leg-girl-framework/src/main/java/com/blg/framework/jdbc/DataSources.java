package com.blg.framework.jdbc;

import com.blg.framework.Java;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.*;
import java.util.*;

/**
 * 数据源工具类
 * @author lujijiang
 */
public class DataSources {

    static Logger logger = LoggerFactory.getLogger(DataSources.class);
    /**
     * 读写分离数据源，自动根据编码过程中是否有更新来实时的切换数据源
     * @param master
     * @param slaves
     * @return
     */
    public static DataSource readWriteSeparate(final DataSource master,final DataSource... slaves){
        return (DataSource) Proxy.newProxyInstance(DataSources.class.getClassLoader(),
                new Class[]{DataSource.class},
                (proxy, method, args) -> {
                    if(Connection.class.equals(method.getReturnType())){
                        return Proxy.newProxyInstance(DataSources.class.getClassLoader(),
                                new Class[]{method.getReturnType()}, new InvocationHandler() {
                                    ThreadLocal<Connection> currentConnectionHolder;
                                    Connection masterConnection;
                                    Connection slaveConnection;
                                    boolean isAutoCommit = true;
                                    {
                                        // 初始化
                                        currentConnectionHolder = new ThreadLocal<Connection>();
                                        masterConnection = (Connection) method.invoke(master,args);
                                        if(slaves!=null && slaves.length>0){
                                            slaveConnection = tryGetSlaveConnection(method, args, slaves);
                                        }
                                        else {
                                            slaveConnection = masterConnection;
                                        }
                                        currentConnectionHolder.set(slaveConnection);
                                    }
                                    @Override
                                    public Object invoke(Object connectionProxy, Method connectionMethod, Object[] connectionArgs) throws Throwable {
                                        if("setAutoCommit".equals(connectionMethod.getName())){
                                            isAutoCommit = (Boolean)connectionArgs[0];
                                            return null;
                                        }
                                        else if("getAutoCommit".equals(connectionMethod.getName())){
                                            return isAutoCommit;
                                        }
                                        else if ("commit".equals(connectionMethod.getName())){
                                            Connection currentConnection = getCurrentConnection();
                                            if(!currentConnection.getAutoCommit()){
                                                currentConnection.commit();
                                            }
                                            currentConnectionHolder.set(slaveConnection);
                                            return null;
                                        }
                                        else if("rollback".equals(connectionMethod.getName())){
                                            Connection currentConnection = getCurrentConnection();
                                            if(!currentConnection.getAutoCommit()){
                                                currentConnection.rollback();
                                            }
                                            currentConnectionHolder.set(slaveConnection);
                                            return null;
                                        }
                                        else if("close".equals(connectionMethod.getName())){
                                            masterConnection.close();
                                            if(slaveConnection!=masterConnection){
                                                slaveConnection.close();
                                            }
                                            return null;
                                        }
                                        else {
                                            if(Statement.class.equals(connectionMethod.getReturnType())){
                                                return Proxy.newProxyInstance(DataSources.class.getClassLoader(), new Class[]{connectionMethod.getReturnType()}, new InvocationHandler() {
                                                    Map<Method,Object[]> preOps = new LinkedHashMap();
                                                    Statement statement;
                                                    @Override
                                                    public Object invoke(Object statementProxy, Method statementMethod, Object[] statementArgs) throws Throwable {
                                                        useMaster(currentConnectionHolder, masterConnection,statementArgs);
                                                        if(statementMethod.getReturnType().equals(Void.TYPE)){
                                                            preOps.put(statementMethod,statementArgs);
                                                            return null;
                                                        }
                                                        else {
                                                            if(statement==null){
                                                                statement = (Statement) connectionMethod.invoke(getCurrentConnection(),connectionArgs);
                                                                for (Iterator<Map.Entry<Method,Object[]>> it = preOps.entrySet().iterator(); it.hasNext();){
                                                                    Map.Entry<Method,Object[]> item = it.next();
                                                                    item.getKey().invoke(statement,item.getValue());
                                                                    it.remove();
                                                                }
                                                            }
                                                            return statementMethod.invoke(statement,statementArgs);
                                                        }
                                                    }
                                                });
                                            }
                                            else if(CallableStatement.class.equals(connectionMethod.getReturnType())){
                                                useMaster(currentConnectionHolder, masterConnection,connectionArgs);
                                                return connectionMethod.invoke(getCurrentConnection(),connectionArgs);
                                            }
                                            else if(PreparedStatement.class.equals(connectionMethod.getReturnType())){
                                                useMaster(currentConnectionHolder, masterConnection,connectionArgs);
                                                return connectionMethod.invoke(getCurrentConnection(),connectionArgs);
                                            }
                                            else {
                                                return connectionMethod.invoke(getCurrentConnection(),connectionArgs);
                                            }
                                        }
                                    }

                                    private Connection getCurrentConnection() throws SQLException {
                                        Connection connection = currentConnectionHolder.get();
                                        return connection;
                                    }

                                    private void useMaster(ThreadLocal<Connection> currentConnectionHolder, Connection masterConnection,Object[] args) throws SQLException {
                                        if(args!=null){
                                            for(Object arg:args){
                                                if(arg!=null && arg instanceof String){
                                                    String sql = ((String) arg).toLowerCase();
                                                    if(sql.contains("update ")
                                                            ||sql.contains("insert ")
                                                            ||sql.contains("delete ")
                                                            ||sql.contains("create ")
                                                            ||sql.contains("alter ")
                                                            ||sql.contains("drop ")
                                                            ||sql.contains("truncate ")
                                                    ){
                                                        if(currentConnectionHolder.get() != masterConnection){
                                                            currentConnectionHolder.set(masterConnection);
                                                            masterConnection.setAutoCommit(isAutoCommit);
                                                            logger.debug("The connection for transaction has been switched to the master begin with sql:{}",sql);
                                                        }
                                                        return;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                });
                    }
                    return method.invoke(master,args);
                });
    }

    private static Connection tryGetSlaveConnection(Method method, Object[] args, DataSource[] slaves) throws IllegalAccessException, InvocationTargetException {
        Connection slaveConnection = (Connection) method.invoke(slaves[new Random().nextInt(slaves.length)],args);
        return slaveConnection;
    }

    /**
     * 判断数据库类型
     * @param dataSource
     * @param type
     * @return
     */
    public static boolean is(DataSource dataSource, String type) {
        Objects.requireNonNull(type,"The type should not be null");
        try(Connection connection = dataSource.getConnection()){
            return connection.getMetaData().getURL().toLowerCase().contains(":"+type+":");
        }catch (Throwable e){
            throw Java.unchecked(e);
        }
    }
}