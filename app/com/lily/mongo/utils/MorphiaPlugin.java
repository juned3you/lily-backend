package com.lily.mongo.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.mongodb.morphia.AbstractEntityInterceptor;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;
import org.mongodb.morphia.ValidationExtension;
import org.mongodb.morphia.logging.MorphiaLoggerFactory;
import org.mongodb.morphia.logging.slf4j.SLF4JLoggerImplFactory;
import org.mongodb.morphia.mapping.Mapper;

import play.Application;
import play.Configuration;
import play.libs.Classpath;

import com.mongodb.DB;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import com.mongodb.gridfs.GridFS;

public class MorphiaPlugin {

	private final Application application;

	private boolean isEnabled;

	private static Morphia morphia = null;

	private static MongoClient mongo = null;
	private static Datastore ds = null;
	private static GridFS gridfs;

	public MorphiaPlugin(Application application) {
		this.application = application;
	}

	public void onStart() {
		enabled();

		if (!isEnabled) {
			return;
		}

		// Register SLF4JLogrImplFactory as Logger
		// @see http://nesbot.com/2011/11/28/play-2-morphia-logging-error
		MorphiaLoggerFactory.reset();

		MorphiaLoggerFactory.registerLogger(SLF4JLoggerImplFactory.class);

		try {
			Configuration morphiaConf = Configuration.root().getConfig(
					ConfigKey.PREFIX);
			if (morphiaConf == null) {
				throw Configuration.root().reportError(ConfigKey.PREFIX,
						"Missing Morphia configuration", null);
			}

			MorphiaLogger.debug(morphiaConf);

			String mongoURIstr = morphiaConf.getString(ConfigKey.DB_MONGOURI
					.getKey());
			String seeds = morphiaConf.getString(ConfigKey.DB_SEEDS.getKey());

			String dbName = null;
			String username = null;
			String password = null;

			if (StringUtils.isNotBlank(mongoURIstr)) {
				MongoClientURI mongoURI = new MongoClientURI(mongoURIstr);
				mongo = connect(mongoURI);
				dbName = mongoURI.getDatabase();
				username = mongoURI.getUsername();
				if (mongoURI.getPassword() != null) {
					password = new String(mongoURI.getPassword());
				}
			} else if (StringUtils.isNotBlank(seeds)) {
				mongo = connect(seeds);
			} else {
				mongo = connect(
						morphiaConf.getString(ConfigKey.DB_HOST.getKey()),
						morphiaConf.getString(ConfigKey.DB_PORT.getKey()));
			}

			if (StringUtils.isBlank(dbName)) {
				dbName = morphiaConf.getString(ConfigKey.DB_NAME.getKey());
				if (StringUtils.isBlank(dbName)) {
					throw morphiaConf.reportError(ConfigKey.DB_NAME.getKey(),
							"Missing Morphia configuration", null);
				}
			}

			morphia = new Morphia();
			// To prevent problem during hot-reload
			if (application.isDev()) {
				morphia.getMapper().getOptions()
						.setObjectFactory(new PlayCreator());
			}
			// Configure validator
			new ValidationExtension(morphia);

			// Check if credentials parameters are present
			if (StringUtils.isBlank(username)) {
				username = morphiaConf
						.getString(ConfigKey.DB_USERNAME.getKey());
			}
			if (StringUtils.isBlank(password)) {
				password = morphiaConf
						.getString(ConfigKey.DB_PASSWORD.getKey());
			}

			if (StringUtils.isNotBlank(username)
					^ StringUtils.isNotBlank(password)) {
				throw morphiaConf.reportError(ConfigKey.DB_NAME.getKey(),
						"Missing username or password", null);
			}

			// Create datastore
			if (StringUtils.isNotBlank(username)
					&& StringUtils.isNotBlank(password)) {
				// ds = morphia.createDatastore(mongo, dbName, username,
				// password.toCharArray());
				ds = morphia.createDatastore(mongo, dbName);
			} else {
				ds = morphia.createDatastore(mongo, dbName);
			}

			MorphiaLogger.debug("Datastore [%s] created", dbName);
			// Create GridFS
			String uploadCollection = morphiaConf
					.getString(ConfigKey.COLLECTION_UPLOADS.getKey());
			if (StringUtils.isBlank(dbName)) {
				uploadCollection = "uploads";
				MorphiaLogger
						.warn("Missing Morphia configuration key [%s]. Use default value instead [%s]",
								ConfigKey.COLLECTION_UPLOADS, "uploads");
			}
			gridfs = new GridFS(ds.getDB(), uploadCollection);
			MorphiaLogger.debug("GridFS created", "");
			MorphiaLogger.debug("Add Interceptor...", "");
			morphia.getMapper().addInterceptor(new AbstractEntityInterceptor() {

				@Override
				public void postLoad(final Object ent, final DBObject dbObj,
						final Mapper mapr) {
					if (ent instanceof Model) {
						com.lily.mongo.utils.Model m = (Model) ent;
						m._post_Load();
					}
				}
			});
			MorphiaLogger.debug("Classes mapping...", "");
			mapClasses(morphiaConf);
			MorphiaLogger.debug("End of initializing Morphia", "");
		} catch (MongoException e) {
			MorphiaLogger.error(e, "Problem connecting MongoDB");
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			MorphiaLogger.error(e, "Problem mapping class");
			throw new RuntimeException(e);
		}
	}

	public void onStop() {
		if (isEnabled) {
			MorphiaLoggerFactory.reset();
			morphia = null;
			ds = null;
			gridfs = null;
			mongo.close();
		}
	}

	public boolean enabled() {
		isEnabled = !"disabled".equals(application.configuration().getString(
				Constants.MORPHIA_PLUGIN_ENABLED));
		MorphiaLogger.warn(String.format("MorphiaPlugin is %s",
				isEnabled ? "enabled" : "disabled"));
		return isEnabled;
	}

	private void mapClasses(Configuration morphiaConf)
			throws ClassNotFoundException {
		// Register all models.Class
		String models = morphiaConf.getString(ConfigKey.MODELS.getKey());
		Set<String> classes = Classpath.getTypes(application, models);
		//Set<Class> classes = new HashSet<Class>();
		/*classes.addAll(Classpath.getTypesAnnotatedWith(application,
				models, Entity.class));
		classes.addAll(Classpath.getTypesAnnotatedWith(application, models,
				Embedded.class));*/
		for (String clazz : classes) {
			MorphiaLogger.debug("mapping class: %1$s", clazz);
			 morphia.map(Class.forName(clazz, true,
			 application.classloader()));
			//morphia.map(clazz);
		}
		// @see
		// http://code.google.com/p/morphia/wiki/Datastore#Ensure_Indexes_and_Caps
		ds.ensureCaps(); // creates capped collections from @Entity
		ds.ensureIndexes(); // creates indexes from @Index annotations in your
							// entities
	}

	private final static ConcurrentMap<String, Datastore> dataStores = new ConcurrentHashMap<String, Datastore>();

	public static Datastore ds(String dbName) {
		if (StringUtils.isBlank(dbName)) {
			return ds();
		}
		Datastore ds = dataStores.get(dbName);
		if (null == ds) {
			Datastore ds0 = morphia.createDatastore(mongo, dbName);
			ds = dataStores.putIfAbsent(dbName, ds0);
			if (null == ds) {
				ds = ds0;
			}
		}
		return ds;
	}

	public static Datastore ds() {
		return ds;
	}

	public static GridFS gridFs() {
		return gridfs;
	}

	public static DB db() {
		return ds().getDB();
	}

	private MongoClient connect(MongoClientURI mongoURI) {
		try {
			return new MongoClient(mongoURI);
		} catch (Exception e) {
			throw Configuration.root().reportError(
					ConfigKey.DB_MONGOURI.getKey(),
					"Cannot connect to mongodb: unknown host", e);
		}
	}

	private MongoClient connect(String seeds) {
		String[] sa = seeds.split("[;,\\s]+");
		List<ServerAddress> addrs = new ArrayList<ServerAddress>(sa.length);
		for (String s : sa) {
			String[] hp = s.split(":");
			if (0 == hp.length) {
				continue;
			}
			String host = hp[0];
			int port = 27017;
			if (hp.length > 1) {
				port = Integer.parseInt(hp[1]);
			}
			try {
				addrs.add(new ServerAddress(host, port));
			} catch (Exception e) {
				MorphiaLogger.error(e,
						"Error creating mongo connection to %s:%s", host, port);
			}
		}
		if (addrs.isEmpty()) {
			throw Configuration.root().reportError(ConfigKey.DB_SEEDS.getKey(),
					"Cannot connect to mongodb: no replica can be connected",
					null);
		}
		return new MongoClient(addrs);
	}

	private MongoClient connect(String host, String port) {
		String[] ha = host.split("[,\\s;]+");
		String[] pa = port.split("[,\\s;]+");
		int len = ha.length;
		if (len != pa.length) {
			throw Configuration.root().reportError(
					ConfigKey.DB_HOST.getKey() + "-"
							+ ConfigKey.DB_PORT.getKey(),
					"host and ports number does not match", null);
		}
		if (1 == len) {
			try {
				return new MongoClient(ha[0], Integer.parseInt(pa[0]));
			} catch (Exception e) {
				throw Configuration.root().reportError(
						ConfigKey.DB_HOST.getKey() + "-"
								+ ConfigKey.DB_PORT.getKey(),
						String.format("Cannot connect to mongodb at %s:%s",
								host, port), e);
			}
		}
		List<ServerAddress> addrs = new ArrayList<ServerAddress>(ha.length);
		for (int i = 0; i < len; ++i) {
			try {
				addrs.add(new ServerAddress(ha[i], Integer.parseInt(pa[i])));
			} catch (Exception e) {
				MorphiaLogger.error(e,
						"Error creating mongo connection to %s:%s", host, port);
			}
		}
		if (addrs.isEmpty()) {
			throw Configuration.root().reportError(
					ConfigKey.DB_HOST.getKey() + "-"
							+ ConfigKey.DB_PORT.getKey(),
					"Cannot connect to mongodb: no replica can be connected",
					null);
		}
		return new MongoClient(addrs);
	}

}
