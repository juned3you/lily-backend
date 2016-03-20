package com.lily.config;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class RedshiftIdGenerator implements IdentifierGenerator {

	/**
	 * Get the Max id.
	 */
	@Override
	public Serializable generate(SessionImplementor session, Object domainObj)
			throws HibernateException {
		Connection connection = session.connection();
		try {

			PreparedStatement ps = connection
					.prepareStatement("SELECT MAX(id) as id from users");

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				Long id = rs.getLong("id");
				System.out.println("Next Max number-->" + id);
				if (id == null || id == 0)
					return new Long(1);

				return new Long(id + 1);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

}
