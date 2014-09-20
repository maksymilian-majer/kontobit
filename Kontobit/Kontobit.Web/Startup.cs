using Microsoft.Owin;
using Owin;

[assembly: OwinStartupAttribute(typeof(Kontobit.Web.Startup))]
namespace Kontobit.Web
{
    public partial class Startup
    {
        public void Configuration(IAppBuilder app)
        {
            ConfigureAuth(app);
        }
    }
}
