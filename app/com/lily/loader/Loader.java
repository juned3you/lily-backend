package com.lily.loader;

import com.lily.exception.LoaderException;

public interface Loader {

	public void load(Object obj) throws LoaderException;
}
