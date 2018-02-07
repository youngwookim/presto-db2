/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.facebook.presto.plugin.db2;

import com.facebook.presto.plugin.jdbc.BaseJdbcClient;
import com.facebook.presto.plugin.jdbc.BaseJdbcConfig;
import com.facebook.presto.plugin.jdbc.DriverConnectionFactory;
import com.facebook.presto.plugin.jdbc.JdbcConnectorId;
import com.facebook.presto.plugin.jdbc.JdbcSplit;
import com.ibm.db2.jcc.DB2Driver;
import io.airlift.log.Logger;

import javax.inject.Inject;

import java.sql.Connection;
import java.sql.SQLException;

public class DB2Client
        extends BaseJdbcClient
{
    private static final Logger log = Logger.get(DB2Client.class);

    @Inject
    public DB2Client(JdbcConnectorId connectorId, BaseJdbcConfig config)
            throws SQLException
    {
        super(connectorId, config, "\"", new DriverConnectionFactory(new DB2Driver(), config));

        // http://stackoverflow.com/questions/16910791/getting-error-code-4220-with-null-sql-state
        System.setProperty("db2.jcc.charsetDecoderEncoder", "3");
    }

    @Override
    public Connection getConnection(JdbcSplit split)
            throws SQLException
    {
        Connection connection = connectionFactory.openConnection();
        try {
            connection.setReadOnly(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_READ_UNCOMMITTED);
        }
        catch (SQLException e) {
            connection.close();
            throw e;
        }
        return connection;
    }
}
