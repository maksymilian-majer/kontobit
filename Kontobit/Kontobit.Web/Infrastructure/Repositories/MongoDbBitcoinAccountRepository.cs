using System.Collections.Generic;
using System.Configuration;
using Kontobit.Web.Domain;
using MongoDB.Bson;
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

        public void Remove(ObjectId id)
        {
            var query = Query<BitcoinAccount>.EQ(e => e.Id, id);
            _accounts.Remove(query);
        }

        public BitcoinAccount GetById(ObjectId id)
        {
            var query = Query<BitcoinAccount>.EQ(e => e.Id, id);
            return _accounts.FindOne(query);
        }

        public void Save(BitcoinAccount account)
        {
            _accounts.Save(account);
        }
    }
}