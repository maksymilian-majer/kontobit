using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using Kontobit.Web.Domain;
using MongoDB.Driver;
using MongoDB.Driver.Builders;

namespace Kontobit.Web.Infrastructure.Repositories
{
    public class MongoDbBitcoinAccountRepository
    {
        private readonly MongoCollection<BitcoinAccount> _accounts;

        public MongoDbBitcoinAccountRepository()
        {
            var connectionString = ConfigurationManager.AppSettings["MongoConnectionString"];
            var client = new MongoClient(connectionString);
            var server = client.GetServer();
            var database = server.GetDatabase("kontobit");
            _accounts = database.GetCollection<BitcoinAccount>("accounts");
        }

        public void Add(BitcoinAccount account)
        {
            _accounts.Insert(account);
        }

        public IEnumerable<BitcoinAccount> GetAll()
        {
            return _accounts.FindAll();
        }
    }
}