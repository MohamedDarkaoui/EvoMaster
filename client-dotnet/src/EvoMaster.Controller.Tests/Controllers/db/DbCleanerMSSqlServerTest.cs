using System.Collections.Generic;
using System.Data.Common;
using System.Data.SqlClient;
using System.Threading.Tasks;
using DotNet.Testcontainers.Containers.Builders;
using DotNet.Testcontainers.Containers.Configurations.Databases;
using DotNet.Testcontainers.Containers.Modules.Databases;
using EvoMaster.Controller.Api;
using EvoMaster.Controller.Controllers.db;
using Xunit;

namespace EvoMaster.Controller.Tests.Controllers.db {
    public class DbCleanerMsSqlServerTest : DbCleanTestBase, IAsyncLifetime {
        private static ITestcontainersBuilder<MsSqlTestcontainer> msSQLBuilder =
            new TestcontainersBuilder<MsSqlTestcontainer>()
                .WithDatabase(new MsSqlTestcontainerConfiguration("mcr.microsoft.com/mssql/server:2017-CU14-ubuntu") {
                    Password = "A_Str0ng_Required_Password"
                });

        private DbConnection _connection;
        private MsSqlTestcontainer _msSql;

        protected override DbConnection GetConnection() {
            return _connection;
        }

        protected override DatabaseType GetDbType() {
            return DatabaseType.MS_SQL_SERVER;
        }

        public async Task InitializeAsync() {
            _msSql = msSQLBuilder.Build();
            await _msSql.StartAsync();
            _connection = new SqlConnection(_msSql.ConnectionString);
            await _connection.OpenAsync();
        }

        public async Task DisposeAsync() {
            DbCleaner.ClearDatabase(_connection, null, GetDbType(), "Foo");

            await _connection.CloseAsync();
            await _msSql.StopAsync();
        }


        protected override void CleanDb(List<string> tablesToSkip) {
            DbCleaner.ClearDatabase(_connection, tablesToSkip, GetDbType(), "Foo");
        }
    }
}