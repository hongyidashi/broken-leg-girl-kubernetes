package com.blg.framework.data.copier;

import java.lang.reflect.Method;

public interface FastCopierConverter {
	Object convert(Object source, Object target, Method sourceReader,
                   Method targetWriter, Object context);
}
