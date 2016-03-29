package com.lily.mongo.utils;

import java.util.List;

import org.mongodb.morphia.mapping.Mapper;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.QueryImpl;

import static com.lily.mongo.utils.MorphiaPlugin.ds;

public class Model {

	public void _post_Load() {
	}

	/*
	 * Search Methods
	 */
	public static class Finder<I, T extends Model> extends QueryImpl<T> {

		private final Class<I> idType;
		private final Class<T> type;

		public Finder(Class<I> idType, Class<T> type) {
			super(type, MorphiaPlugin.ds().getCollection(type), MorphiaPlugin
					.ds());
			this.idType = idType;
			this.type = type;
		}

		public T byId(Object id) {
			return MorphiaPlugin.ds().createQuery(type).field(Mapper.ID_KEY)
					.equal(id).get();
		}

		public List<T> all() {
			return MorphiaPlugin.ds().find(type).asList();
		}
	}

	/*
	 * Life methods
	 */
	@SuppressWarnings("unchecked")
	public <T extends Model> T insert() {
		ds().save(this);
		return (T) this;
	}

	@SuppressWarnings("unchecked")
	public <T extends Model> T update() {
		ds().save(this);
		return (T) this;
	}

	public void delete() {
		ds().delete(this);
	}

	public static <T> void insertAll(List<T> list) {
		ds().save(list);
	}

	public static <T> void deleteAll(Query<T> query) {
		ds().delete(query);
	}

	public static <T> Query<T> q(Class<T> type) {
		return ds().createQuery(type);
	}
	
	@SuppressWarnings("rawtypes")
	public Query q() {
		return ds().createQuery(this.getClass());
	}

	@SuppressWarnings("unchecked")
	public <T extends Model> T refresh() {
		return (T) ds().get(this);
	}

}
