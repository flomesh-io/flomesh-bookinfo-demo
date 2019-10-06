package com.redhat.bookinfo.reviews;

import org.hibernate.dialect.Dialect;
import org.hibernate.type.AbstractSingleColumnStandardBasicType;
import org.hibernate.type.LiteralType;
import org.hibernate.type.PostgresUUIDType;
import org.hibernate.type.StringType;
import org.hibernate.type.descriptor.java.UUIDTypeDescriptor;
import org.hibernate.type.descriptor.sql.SqlTypeDescriptor;
import org.hibernate.type.descriptor.sql.VarcharTypeDescriptor;

import java.util.UUID;

/**
 * This UUID type descriptor adapts to either PostgreSQL or Varchar representations of the UUID depending on if the
 * PostgreSQL driver is loaded or not. If not, we can assume H2. This implementation would work best with Maven profiles.
 *
 * This is needed because PostgreSQL has native UUID type that should be used for production databases, but H2 is used
 * for testing and development, so a Varchar representation of the UUIDs is sufficient. By default, Hibernate will
 * assign UUIDs to be stored in binary form in H2. That makes it hard to work with and impedes the use of SQL scripts
 * that have UUID in string format.
 */
public class PostgresH2UUIDType extends AbstractSingleColumnStandardBasicType<UUID> implements LiteralType<UUID> {
    private static SqlTypeDescriptor SQL_DESCRIPTOR;

    static {
        try {
            Class.forName("org.postgresql.Driver");
            SQL_DESCRIPTOR = PostgresUUIDType.PostgresUUIDSqlTypeDescriptor.INSTANCE;
        }
        catch(ClassNotFoundException cnfe) {
            SQL_DESCRIPTOR = VarcharTypeDescriptor.INSTANCE;
        }
    }

    public PostgresH2UUIDType() {
        super( SQL_DESCRIPTOR , UUIDTypeDescriptor.INSTANCE );
    }

    @Override
    public String getName() {
        return "uuid-pgh2";
    }

    @Override
    public String objectToSQLString( UUID value, Dialect dialect ) throws Exception {
        return StringType.INSTANCE.objectToSQLString( value.toString(), dialect );
    }
}
