package com.lily.authorize.fitbit.loader;

import java.util.List;

import com.lily.exception.LoaderException;
import com.lily.loader.Loader;
import com.lily.mongo.models.Category;

/**
 * Load model into Mongodb.
 * 
 * @author Mohammad
 *
 */
public class CategoryLoader implements Loader {

	@SuppressWarnings("unchecked")
	@Override
	public void load(Object obj) throws LoaderException {
		List<Category> categoryList = (List<Category>) obj;
		try {
			saveCategories(categoryList);

		} catch (Throwable e) {
			throw new LoaderException(e);
		}
	}
	
	private void saveCategories(List<Category> categoryList){
		categoryList.forEach(cat -> {
			Category category = Category.find().filter("id", cat.id).get();

			if (category == null){
				if(cat.subCategories != null && cat.subCategories.size() > 0)
					Category.insertAll(cat.subCategories);
				
				cat.insert();
			}else {
				if(category.subCategories != null  && category.subCategories.size() > 0)
					saveCategories(cat.subCategories);
				
				category.activities = cat.activities;
				category.name = cat.name;
				category.update();
			}
		});
	}
}