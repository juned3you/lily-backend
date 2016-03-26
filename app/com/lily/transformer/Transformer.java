package com.lily.transformer;

import com.lily.exception.TransformerException;

public interface Transformer {
	
	public Object transform(Object obj) throws TransformerException;

}
