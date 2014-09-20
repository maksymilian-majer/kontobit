using System;
using System.Collections.Generic;
using System.Configuration;
using System.Linq;
using System.Web;
using System.Web.Mvc;
using Kontobit.Web.Domain;
using Kontobit.Web.Infrastructure.Repositories;
using Microsoft.AspNet.Identity;
using MongoDB.Bson;
using MongoDB.Driver;

namespace Kontobit.Web.Controllers
{
    [Authorize]
    public class BitcoinAccountsController : KontobitControllerBase
    {
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult Add(BitcoinAccountModel model)
        {
            if (!ModelState.IsValid)
            {
                this.Response.StatusCode = 401;
                return this.Json(new {Message = "Validation error"});
            }
            var account = new BitcoinAccount(User.Identity.GetUserId(), model.Name, model.Address);

            var repository = new MongoDbBitcoinAccountRepository();
            repository.Add(account);

            return Json(new {Message = "OK", Data = new BitcoinAccountModel
            {
                Id = account.Id.ToString(),
                Address = account.Address,
                Name = account.Name
            } });
        }

        public ActionResult Update(BitcoinAccountModel model)
        {
            if (!ModelState.IsValid)
            {
                this.Response.StatusCode = 401;
                return this.Json(new { Message = "Validation error" });
            }

            var repository = new MongoDbBitcoinAccountRepository();

            ObjectId id;
            if (!ObjectId.TryParse(model.Id, out id))
            {
                throw new ArgumentException();
            }

            var account = repository.GetById(id);

            repository.Save(account);

            return Json(new
            {
                Message = "OK",
                Data = new BitcoinAccountModel
                {
                    Id = account.Id.ToString(),
                    Address = account.Address,
                    Name = account.Name
                }
            });
        }
        
        public ActionResult Delete(BitcoinAccountModel model)
        {
            if (!ModelState.IsValid)
            {
                this.Response.StatusCode = 401;
                return this.Json(new {Message = "Validation error"});
            }

            var account = new BitcoinAccount(User.Identity.GetUserId(), model.Name, model.Address);

            var repository = new MongoDbBitcoinAccountRepository();
            ObjectId id;
            if (!ObjectId.TryParse(model.Id, out id))
            {
                throw new ArgumentException();
            }
            
            repository.Remove(id);

            return Json(new {Message = "OK", Data = account });
        }

        public ActionResult List()
        {
            var repository = new MongoDbBitcoinAccountRepository();
            return Json(repository.GetAll().Select(x => new BitcoinAccountModel
            {
                Id = x.Id.ToString(),
                Name = x.Name,
                Address = x.Address
            }), JsonRequestBehavior.AllowGet);
        }
    }

    public class BitcoinAccountModel
    {
        public string Id { get; set; }
        public string Name { get; set; }
        public string Address { get; set; }
    }
}