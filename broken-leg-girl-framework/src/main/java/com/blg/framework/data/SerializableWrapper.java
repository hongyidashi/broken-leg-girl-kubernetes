package com.blg.framework.data;

import java.io.*;

/**
 * 序列号包装器
 */
public class SerializableWrapper<T extends Serializable> {
    /**
     * 数据对象
     */
    private byte[] data;

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void write(T object){
        try(ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream()){
            try(ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)){
                objectOutputStream.writeObject(object);
            }
            data = byteArrayOutputStream.toByteArray();
        }catch (Throwable e){
            throw e instanceof RuntimeException?(RuntimeException)e:new RuntimeException(e);
        }
    }

    public T read(){
        try(ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data)){
            try(ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream)){
                return (T) objectInputStream.readObject();
            }
        }catch (Throwable e){
            throw e instanceof RuntimeException?(RuntimeException)e:new RuntimeException(e);
        }
    }
}
