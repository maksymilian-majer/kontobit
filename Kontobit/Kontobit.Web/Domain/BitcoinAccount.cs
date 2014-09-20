using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using MongoDB.Bson;

namespace Kontobit.Web.Domain
{
    public class BitcoinAccount
    {
        public ObjectId Id { get; set; }
        public Guid UserId { get; set; }
        public string Name { get; set; }
        public string Address { get; set; }

        public BitcoinAccount(Guid userId, string name, string address)
        {
            UserId = userId;
            Name = name;
            Address = address;
        }
    }
}